package org.kynuxcloud.AiChat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.kynuxcloud.AiChat.config.Config;
import org.kynuxcloud.AiChat.model.ChatMessage;
import org.kynuxcloud.AiChat.model.ChatRequest;
import org.kynuxcloud.AiChat.model.ChatResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AIService {
    private final JavaPlugin plugin;
    private final Config config;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Map<String, List<ChatMessage>> conversationHistory;
    
    public interface ResponseCallback {
        void onSuccess(String response);
    }
    
    public interface ErrorCallback {
        void onError(String errorMessage);
    }
    
    public interface ModelsCallback {
        void onSuccess(List<String> models);
    }
    
    public AIService(JavaPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        this.objectMapper = new ObjectMapper();
        this.conversationHistory = new HashMap<String, List<ChatMessage>>();
    }
    
    public void sendMessage(String playerName, String message, final ResponseCallback onResponse, final ErrorCallback onError) {
        if (!conversationHistory.containsKey(playerName)) {
            List<ChatMessage> messages = new ArrayList<ChatMessage>();
            messages.add(new ChatMessage("system", config.getSystemPrompt()));
            conversationHistory.put(playerName, messages);
        }
        
        List<ChatMessage> conversation = conversationHistory.get(playerName);
        
        conversation.add(new ChatMessage("user", message));
        
        List<ChatMessage> recentConversation = conversation;
        if (conversation.size() > 11) {
            recentConversation = conversation.subList(conversation.size() - 11, conversation.size());
        }
        
        final ChatRequest chatRequest = new ChatRequest(
                config.getModel(),
                new ArrayList<ChatMessage>(recentConversation),
                config.getTemperature(),
                config.getMaxTokens()
        );
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonBody = objectMapper.writeValueAsString(chatRequest);
                    
                    Request request = new Request.Builder()
                            .url(config.getApiUrl())
                            .post(RequestBody.create(MediaType.parse("application/json"), jsonBody))
                            .header("X-API-KEY", config.getApiKey())
                            .header("Content-Type", "application/json")
                            .build();
                    
                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            final String errorMsg = "AI API'sine bağlanırken hata oluştu: " + e.getMessage();
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    onError.onError(errorMsg);
                                }
                            });
                        }
                        
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ResponseBody responseBody = response.body();
                            try {
                                if (!response.isSuccessful()) {
                                    String errorBody = responseBody != null ? responseBody.string() : "Bilinmeyen hata";
                                    final String errorMsg = "AI API yanıt hatası: " + response.code() + " - " + errorBody;
                                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            onError.onError(errorMsg);
                                        }
                                    });
                                    return;
                                }
                                
                                if (responseBody == null) {
                                    final String errorMsg = "AI API'den boş yanıt geldi";
                                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            onError.onError(errorMsg);
                                        }
                                    });
                                    return;
                                }
                                
                                String responseString = responseBody.string();
                                ChatResponse chatResponse = objectMapper.readValue(responseString, ChatResponse.class);
                                
                                if (chatResponse.getChoices() == null || chatResponse.getChoices().isEmpty()) {
                                    final String errorMsg = "AI yanıtı geçersiz format içeriyor";
                                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            onError.onError(errorMsg);
                                        }
                                    });
                                    return;
                                }
                                
                                final String aiResponse = chatResponse.getChoices().get(0).getMessage().getContent();
                                
                                conversation.add(new ChatMessage("assistant", aiResponse));
                                
                                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        onResponse.onSuccess(aiResponse);
                                    }
                                });
                            } catch (Exception e) {
                                final String errorMsg = "AI yanıtı işlenirken hata oluştu: " + e.getMessage();
                                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        onError.onError(errorMsg);
                                    }
                                });
                            } finally {
                                if (responseBody != null) {
                                    responseBody.close();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    final String errorMsg = "AI isteği hazırlanırken hata oluştu: " + e.getMessage();
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            onError.onError(errorMsg);
                        }
                    });
                }
            }
        });
    }
    
    public void clearConversation(String playerName) {
        conversationHistory.remove(playerName);
    }
    
    public void clearAllConversations() {
        conversationHistory.clear();
    }
    
    public void fetchAvailableModels(final ModelsCallback onSuccess, final ErrorCallback onError) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("http://api.kynux.cloud/api/v1/models")
                            .get()
                            .header("Authorization", "Bearer " + config.getApiKey())
                            .build();
                    
                    Response response = null;
                    ResponseBody responseBody = null;
                    
                    try {
                        response = httpClient.newCall(request).execute();
                        responseBody = response.body();
                        
                        if (!response.isSuccessful() || responseBody == null) {
                            String errorBody = responseBody != null ? responseBody.string() : "Bilinmeyen hata";
                            final String errorMsg = "Model listesi alınamadı: " + response.code() + " - " + errorBody;
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    onError.onError(errorMsg);
                                }
                            });
                            return;
                        }
                        
                        String responseString = responseBody.string();
                        Map<String, Object> modelsResponse = objectMapper.readValue(responseString, Map.class);
                        
                        if (!modelsResponse.containsKey("data") || !(modelsResponse.get("data") instanceof List)) {
                            final String errorMsg = "Model listesi geçersiz format içeriyor";
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    onError.onError(errorMsg);
                                }
                            });
                            return;
                        }
                        
                        final List<String> modelIds = new ArrayList<String>();
                        List<?> modelList = (List<?>) modelsResponse.get("data");
                        
                        for (Object modelObj : modelList) {
                            if (modelObj instanceof Map) {
                                Map<?, ?> modelMap = (Map<?, ?>) modelObj;
                                if (modelMap.containsKey("id")) {
                                    modelIds.add(modelMap.get("id").toString());
                                }
                            }
                        }
                        
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                onSuccess.onSuccess(modelIds);
                            }
                        });
                    } finally {
                        if (responseBody != null) {
                            responseBody.close();
                        }
                        if (response != null) {
                            response.close();
                        }
                    }
                } catch (Exception e) {
                    final String errorMsg = "Model listesi alınırken hata oluştu: " + e.getMessage();
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            onError.onError(errorMsg);
                        }
                    });
                }
            }
        });
    }
}
