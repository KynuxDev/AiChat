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
            FileConfiguration config = plugin.getConfig();
            
            apiUrl = config.getString("api.url", "http://api.kynux.cloud/api/v1/chat/completions");
            apiKey = config.getString("api.key", "YOUR_API_KEY");
            model = config.getString("api.model", "gpt-4o");
            temperature = config.getDouble("api.temperature", 0.7);
            maxTokens = config.getInt("api.max_tokens", 500);
            
            systemPrompt = config.getString("chat.system_prompt", "Sen yardımcı bir asistansın.");
            chatPrefix = config.getString("chat.prefix", "&b[AI] &r");
            broadcastResponses = config.getBoolean("chat.broadcast_responses", true);
            allowColorCodes = config.getBoolean("chat.allow_color_codes", true);
            chatTriggerEnabled = config.getBoolean("chat.chat_trigger.enabled", true);
            chatTriggerKeyword = config.getString("chat.chat_trigger.keyword", "ai");
            
            if (!config.isSet("api.url")) {
                config.set("api.url", apiUrl);
                config.set("api.key", apiKey);
                config.set("api.model", model);
                config.set("api.temperature", temperature);
                config.set("api.max_tokens", maxTokens);
                config.set("chat.system_prompt", systemPrompt);
                config.set("chat.prefix", chatPrefix);
                config.set("chat.broadcast_responses", broadcastResponses);
                config.set("chat.allow_color_codes", allowColorCodes);
                config.set("chat.chat_trigger.enabled", chatTriggerEnabled);
                config.set("chat.chat_trigger.keyword", chatTriggerKeyword);
                plugin.saveConfig();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Yapılandırma yüklenirken hata oluştu: " + e.getMessage());
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
