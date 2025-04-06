package org.kynuxcloud.chatService;

import org.bukkit.plugin.java.JavaPlugin;
import org.kynuxcloud.chatService.command.AICommand;
import org.kynuxcloud.chatService.config.Config;
import org.kynuxcloud.chatService.service.AIService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class ChatService extends JavaPlugin {
    private Config pluginConfig;
    private AIService aiService;

    @Override
    public void onEnable() {
        // Config dosyasını oluştur
        saveDefaultConfig();
        
        // Config yöneticisini oluştur
        pluginConfig = new Config(this);

        // AI servisini oluştur
        aiService = new AIService(this, pluginConfig);
        
        // Komut işleyicisini kaydet
        getCommand("ai").setExecutor(new AICommand(this, pluginConfig, aiService));
        
        getLogger().info("ChatService Aktif! API: " + pluginConfig.getApiUrl());
        getLogger().info("Kullanılan Model: " + pluginConfig.getModel());
    }

    @Override
    public void onDisable() {
        getLogger().info("ChatService Devre Dışı!");
    }
    
    @Override
    public void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        File configFile = new File(getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            try (InputStream in = getResource("config.yml")) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                    getLogger().info("Varsayılan yapılandırma dosyası oluşturuldu.");
                } else {
                    // Varsayılan yapılandırma dosyasını manuel olarak oluştur
                    String defaultConfig = "api:\n" +
                            "  url: 'http://api.kynux.cloud/api/v1/chat/completions'\n" +
                            "  key: 'YOUR_API_KEY'\n" +
                            "  model: 'gpt-4o'\n" +
                            "  temperature: 0.7\n" +
                            "  max_tokens: 500\n" +
                            "\n" +
                            "chat:\n" +
                            "  system_prompt: 'Sen yardımcı bir asistansın.'\n" +
                            "  prefix: '&b[AI] &r'";
                    
                    Files.write(configFile.toPath(), defaultConfig.getBytes());
                    getLogger().info("Varsayılan yapılandırma dosyası manuel olarak oluşturuldu.");
                }
            } catch (IOException e) {
                getLogger().severe("Yapılandırma dosyası oluşturulamadı: " + e.getMessage());
            }
        }
    }
}
