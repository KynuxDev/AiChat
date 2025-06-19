package org.kynuxcloud.AiChat.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private final JavaPlugin plugin;
    private String apiUrl;
    private String apiKey;
    private String model;
    private double temperature;
    private int maxTokens;
    private String systemPrompt;
    private String chatPrefix;
    private boolean broadcastResponses;
    private boolean allowColorCodes;
    private boolean chatTriggerEnabled;
    private String chatTriggerKeyword;
    
    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        try {
            plugin.saveDefaultConfig();
            plugin.reloadConfig(); // Config dosyasını fresh olarak yükle
            FileConfiguration config = plugin.getConfig();
            
            // Config değerlerini doğrudan oku - default değerler config.yml'deki değerler olmalı
            apiUrl = config.getString("api.url", "https://ai.kynux.cloud/v1/chat/completions");
            apiKey = config.getString("api.key", "sk-c2dfa81d4b950eadba1ce55273e3d96830962fa1502cebdb987d04f4a8b9dd0b");
            model = config.getString("api.model", "grok-3-mini");
            temperature = config.getDouble("api.temperature", 0.7);
            maxTokens = config.getInt("api.max_tokens", 1000);
            
            systemPrompt = config.getString("chat.system_prompt", "Adın Kynuxai ve Sen yardımcı bir asistansın. Minecraft oyuncusuna kısa ve öz yanıtlar ver.");
            chatPrefix = config.getString("chat.prefix", "&b[KynuxAi] &r");
            broadcastResponses = config.getBoolean("chat.broadcast_responses", true);
            allowColorCodes = config.getBoolean("chat.allow_color_codes", true);
            chatTriggerEnabled = config.getBoolean("chat.chat_trigger.enabled", true);
            chatTriggerKeyword = config.getString("chat.chat_trigger.keyword", "kynuxai");
            
            plugin.getLogger().info("Config yüklendi:");
            plugin.getLogger().info("- Keyword: " + chatTriggerKeyword);
            plugin.getLogger().info("- Prefix: " + chatPrefix);
            plugin.getLogger().info("- API URL: " + apiUrl);
            plugin.getLogger().info("- Model: " + model);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Yapılandırma yüklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getModel() {
        return model;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public String getChatPrefix() {
        return chatPrefix;
    }
    
    public boolean isChatTriggerEnabled() {
        return chatTriggerEnabled;
    }
    
    public String getChatTriggerKeyword() {
        return chatTriggerKeyword;
    }
    
    public boolean isBroadcastResponses() {
        return broadcastResponses;
    }
    
    public boolean isAllowColorCodes() {
        return allowColorCodes;
    }
}
