package org.kynuxcloud.chatService.config;

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
    
    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        
        // API Ayarları
        apiUrl = config.getString("api.url", "http://api.kynux.cloud/api/v1/chat/completions");
        apiKey = config.getString("api.key", "YOUR_API_KEY");
        model = config.getString("api.model", "gpt-4o");
        temperature = config.getDouble("api.temperature", 0.7);
        maxTokens = config.getInt("api.max_tokens", 500);
        
        // Chat Ayarları
        systemPrompt = config.getString("chat.system_prompt", "Sen yardımcı bir asistansın.");
        chatPrefix = config.getString("chat.prefix", "&b[AI] &r");
        
        // Default config ayarlarını kaydet
        if (!config.isSet("api.url")) {
            config.set("api.url", apiUrl);
            config.set("api.key", apiKey);
            config.set("api.model", model);
            config.set("api.temperature", temperature);
            config.set("api.max_tokens", maxTokens);
            config.set("chat.system_prompt", systemPrompt);
            config.set("chat.prefix", chatPrefix);
            plugin.saveConfig();
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
}
