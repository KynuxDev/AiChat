package org.kynuxcloud.AiChat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.kynuxcloud.AiChat.command.AICommand;
import org.kynuxcloud.AiChat.config.Config;
import org.kynuxcloud.AiChat.service.AIService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class AiChat extends JavaPlugin implements Listener {
    private Config pluginConfig;
    private AIService aiService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        pluginConfig = new Config(this);

        aiService = new AIService(this, pluginConfig);
        
        getCommand("ai").setExecutor(new AICommand(this, pluginConfig, aiService));
        
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("AiChat Aktif! API: " + pluginConfig.getApiUrl());
        getLogger().info("Kullanılan Model: " + pluginConfig.getModel());
        getLogger().info("Sohbet Tetikleyicisi: " + (pluginConfig.isChatTriggerEnabled() ? "Aktif (" + pluginConfig.getChatTriggerKeyword() + ")" : "Devre Dışı"));
    }

    @Override
    public void onDisable() {
        getLogger().info("AiChat Devre Dışı!");
    }
    
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        pluginConfig.loadConfig();
        getLogger().info("Yapılandırma yeniden yüklendi.");
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!pluginConfig.isChatTriggerEnabled()) {
            return;
        }
        
        String message = event.getMessage();
        Player player = event.getPlayer();
        String keyword = pluginConfig.getChatTriggerKeyword().toLowerCase();
        
        if (message.toLowerCase().startsWith(keyword + " ")) {
            String aiMessage = message.substring(keyword.length() + 1);
            
            event.setCancelled(true);
            
            String playerName = event.getPlayer().getDisplayName();
            String originalFormat = String.format(event.getFormat(), playerName, message);
            for (Player recipient : event.getRecipients()) {
                recipient.sendMessage(originalFormat);
            }
            
            player.sendMessage(ChatColor.GRAY + "AI'ya mesajınız iletiliyor...");
            
            aiService.sendMessage(
                player.getName(),
                aiMessage,
                response -> {
                    String formattedResponse = ChatColor.translateAlternateColorCodes('&', 
                        pluginConfig.getChatPrefix() + response);
                    player.sendMessage(formattedResponse);
                },
                error -> {
                    player.sendMessage(ChatColor.RED + "AI Hatası: " + error);
                    getLogger().warning("AI API Hatası: " + error);
                }
            );
        }
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
                    String defaultConfig = "api:\n" +
                            "  url: 'http://api.kynux.cloud/api/v1/chat/completions'\n" +
                            "  key: 'YOUR_API_KEY'\n" +
                            "  model: 'gpt-4o'\n" +
                            "  temperature: 0.7\n" +
                            "  max_tokens: 500\n" +
                            "\n" +
                            "chat:\n" +
                            "  system_prompt: 'Sen yardımcı bir asistansın.'\n" +
                            "  prefix: '&b[AI] &r'\n" +
                            "  chat_trigger:\n" +
                            "    enabled: true\n" +
                            "    keyword: 'ai'";
                    
                    Files.write(configFile.toPath(), defaultConfig.getBytes());
                    getLogger().info("Varsayılan yapılandırma dosyası manuel olarak oluşturuldu.");
                }
            } catch (IOException e) {
                getLogger().severe("Yapılandırma dosyası oluşturulamadı: " + e.getMessage());
            }
        }
    }
}
