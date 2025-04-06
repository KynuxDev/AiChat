package org.kynuxcloud.chatService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.kynuxcloud.chatService.config.Config;
import org.kynuxcloud.chatService.model.ChatMessage;
import org.kynuxcloud.chatService.model.ChatRequest;
import org.kynuxcloud.chatService.model.ChatResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AIService {
    private final JavaPlugin plugin;
    private final Config config;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Map<String, List<ChatMessage>> conversationHistory;
    
    public AIService(JavaPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        
        // HTTP Client kurulumu
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        this.objectMapper = new ObjectMapper();
        this.conversationHistory = new HashMap<>();
    }
    
    /**
     * Kullanıcının mesajını AI'ya gönderir ve cevabı asenkron olarak alır
     * 
     * @param playerName Oyuncu adı
     * @param message Oyuncunun mesajı
     * @param onResponse Cevap geldiğinde çağrılacak callback
     * @param onError Hata durumunda çağrılacak callback
     */
    public void sendMessage(String playerName, String message, Consumer<String> onResponse, Consumer<String> onError) {
        // Eğer oyuncu için konuşma geçmişi yoksa oluştur
        if (!conversationHistory.containsKey(playerName)) {
            List<ChatMessage> messages = new ArrayList<>();
            // Sistem mesajını ekle
            messages.add(new ChatMessage("system", config.getSystemPrompt()));
            conversationHistory.put(playerName, messages);
        }
        
        // Konuşma geçmişi
        List<ChatMessage> conversation = conversationHistory.get(playerName);
        
        // Kullanıcı mesajını ekle
        conversation.add(new ChatMessage("user", message));
        
        // Konuşma geçmişinden maksimum son 10 mesajı al (API token limitlerini aşmamak için)
        List<ChatMessage> recentConversation = conversation;
        if (conversation.size() > 11) { // system message + max 10 mesaj
            recentConversation = conversation.subList(conversation.size() - 11, conversation.size());
        }
        
        // API isteğini hazırla
        ChatRequest chatRequest = new ChatRequest(
                config.getModel(),
                new ArrayList<>(recentConversation), // ArrayList'e çevirerek shallow copy oluştur
                config.getTemperature(),
                config.getMaxTokens()
        );
        
        // Bu işlemi asenkron olarak gerçekleştir
        CompletableFuture.runAsync(() -> {
            try {
                // JSON'a dönüştür
                String jsonBody = objectMapper.writeValueAsString(chatRequest);
                
                // HTTP isteğini hazırla
                Request request = new Request.Builder()
                        .url(config.getApiUrl())
                        .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                        .header("Authorization", "Bearer " + config.getApiKey())
                        .header("Content-Type", "application/json")
                        .build();
                
                // İsteği asenkron olarak gönder
                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Ana thread'e dön ve hata bilgisini ilet
                        Bukkit.getScheduler().runTask(plugin, () -> 
                            onError.accept("AI API'sine bağlanırken hata oluştu: " + e.getMessage())
                        );
                    }
                    
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()) {
                                // Hata durumunda
                                String errorBody = responseBody != null ? responseBody.string() : "Bilinmeyen hata";
                                Bukkit.getScheduler().runTask(plugin, () -> 
                                    onError.accept("AI API yanıt hatası: " + response.code() + " - " + errorBody)
                                );
                                return;
                            }
                            
                            if (responseBody == null) {
                                Bukkit.getScheduler().runTask(plugin, () -> 
                                    onError.accept("AI API'den boş yanıt geldi")
                                );
                                return;
                            }
                            
                            // Yanıtı parse et
                            String responseString = responseBody.string();
                            ChatResponse chatResponse = objectMapper.readValue(responseString, ChatResponse.class);
                            
                            if (chatResponse.getChoices() == null || chatResponse.getChoices().isEmpty()) {
                                Bukkit.getScheduler().runTask(plugin, () -> 
                                    onError.accept("AI yanıtı geçersiz format içeriyor")
                                );
                                return;
                            }
                            
                            // AI yanıtını al
                            String aiResponse = chatResponse.getChoices().get(0).getMessage().getContent();
                            
                            // Yanıtı konuşma geçmişine ekle
                            conversation.add(new ChatMessage("assistant", aiResponse));
                            
                            // Ana thread'e dön ve yanıtı ilet
                            Bukkit.getScheduler().runTask(plugin, () -> onResponse.accept(aiResponse));
                        } catch (Exception e) {
                            // JSON parse hatası
                            Bukkit.getScheduler().runTask(plugin, () -> 
                                onError.accept("AI yanıtı işlenirken hata oluştu: " + e.getMessage())
                            );
                        }
                    }
                });
            } catch (Exception e) {
                // İstek oluşturma hatası
                Bukkit.getScheduler().runTask(plugin, () -> 
                    onError.accept("AI isteği hazırlanırken hata oluştu: " + e.getMessage())
                );
            }
        });
    }
    
    /**
     * Belli bir oyuncunun konuşma geçmişini temizler
     * 
     * @param playerName Oyuncu adı
     */
    public void clearConversation(String playerName) {
        conversationHistory.remove(playerName);
    }
    
    /**
     * Tüm konuşma geçmişlerini temizler
     */
    public void clearAllConversations() {
        conversationHistory.clear();
    }
    
    /**
     * Mevcut model listesini API'den çeker
     * 
     * @param onSuccess Başarılı olduğunda çağrılacak callback
     * @param onError Hata durumunda çağrılacak callback
     */
    public void fetchAvailableModels(Consumer<List<String>> onSuccess, Consumer<String> onError) {
        CompletableFuture.runAsync(() -> {
            try {
                // HTTP isteğini hazırla
                Request request = new Request.Builder()
                        .url("http://api.kynux.cloud/api/v1/models")
                        .get()
                        .header("Authorization", "Bearer " + config.getApiKey())
                        .build();
                
                // İsteği gönder
                try (Response response = httpClient.newCall(request).execute()) {
                    ResponseBody responseBody = response.body();
                    
                    if (!response.isSuccessful() || responseBody == null) {
                        String errorBody = responseBody != null ? responseBody.string() : "Bilinmeyen hata";
                        Bukkit.getScheduler().runTask(plugin, () -> 
                            onError.accept("Model listesi alınamadı: " + response.code() + " - " + errorBody)
                        );
                        return;
                    }
                    
                    // Yanıtı parse et
                    String responseString = responseBody.string();
                    Map<String, Object> modelsResponse = objectMapper.readValue(responseString, Map.class);
                    
                    if (!modelsResponse.containsKey("data") || !(modelsResponse.get("data") instanceof List)) {
                        Bukkit.getScheduler().runTask(plugin, () -> 
                            onError.accept("Model listesi geçersiz format içeriyor")
                        );
                        return;
                    }
                    
                    // Model ID'lerini çıkart
                    List<String> modelIds = new ArrayList<>();
                    for (Object modelObj : (List<?>) modelsResponse.get("data")) {
                        if (modelObj instanceof Map) {
                            Map<?, ?> modelMap = (Map<?, ?>) modelObj;
                            if (modelMap.containsKey("id")) {
                                modelIds.add(modelMap.get("id").toString());
                            }
                        }
                    }
                    
                    // Ana thread'e dön ve model listesini ilet
                    final List<String> finalModelIds = modelIds;
                    Bukkit.getScheduler().runTask(plugin, () -> onSuccess.accept(finalModelIds));
                }
            } catch (Exception e) {
                // İstek hatası
                Bukkit.getScheduler().runTask(plugin, () -> 
                    onError.accept("Model listesi alınırken hata oluştu: " + e.getMessage())
                );
            }
        });
    }
}
