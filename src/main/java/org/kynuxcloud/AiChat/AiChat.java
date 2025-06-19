package org.kynuxcloud.AiChat;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
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
        
        // Modern bStats setup with custom charts
        setupMetrics();
        
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
        if (pluginConfig != null) {
            pluginConfig.loadConfig();
            // AIService'i yeni config ile yeniden oluştur
            aiService = new AIService(this, pluginConfig);
            getLogger().info("Yapılandırma yeniden yüklendi.");
            getLogger().info("Kullanılan Model: " + pluginConfig.getModel());
            getLogger().info("Sohbet Tetikleyicisi: " + (pluginConfig.isChatTriggerEnabled() ? "Aktif (" + pluginConfig.getChatTriggerKeyword() + ")" : "Devre Dışı"));
        }
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
                    String messageContent = response;
                    
                    if (!pluginConfig.isAllowColorCodes()) {
                        messageContent = messageContent.replaceAll("&[0-9a-fk-or]", "");
                    }
                    
                    String formattedResponse = ChatColor.translateAlternateColorCodes('&', 
                        pluginConfig.getChatPrefix() + messageContent);
                    
                    if (pluginConfig.isBroadcastResponses()) {
                        Bukkit.broadcastMessage(formattedResponse);
                    } else {
                        player.sendMessage(formattedResponse);
                    }
                },
                error -> {
                    player.sendMessage(ChatColor.RED + "AI Hatası: " + error);
                    getLogger().warning("AI API Hatası: " + error);
                }
            );
        }
    }
    
    /**
     * Modern bStats setup with custom charts
     */
    private void setupMetrics() {
        int pluginId = 26214; // Your plugin ID from bStats
        Metrics metrics = new Metrics(this, pluginId);
        
        // Custom chart for AI model usage
        metrics.addCustomChart(new SimplePie("used_ai_model", () -> {
            return pluginConfig.getModel();
        }));
        
        // Custom chart for chat trigger status
        metrics.addCustomChart(new SimplePie("chat_trigger_enabled", () -> {
            return pluginConfig.isChatTriggerEnabled() ? "Enabled" : "Disabled";
        }));
        
        // Custom chart for broadcast mode
        metrics.addCustomChart(new SimplePie("broadcast_mode", () -> {
            return pluginConfig.isBroadcastResponses() ? "Broadcast" : "Private";
        }));
        
        // Custom chart for color codes support
        metrics.addCustomChart(new SimplePie("color_codes_enabled", () -> {
            return pluginConfig.isAllowColorCodes() ? "Enabled" : "Disabled";
        }));
        
        // Custom chart for server player count ranges
        metrics.addCustomChart(new SimplePie("player_count", () -> {
            int playerCount = Bukkit.getOnlinePlayers().size();
            if (playerCount <= 10) {
                return "1-10";
            } else if (playerCount <= 25) {
                return "11-25";
            } else if (playerCount <= 50) {
                return "26-50";
            } else if (playerCount <= 100) {
                return "51-100";
            } else {
                return "100+";
            }
        }));
        
        // Custom chart for chat trigger keyword
        metrics.addCustomChart(new SimplePie("trigger_keyword", () -> {
            return pluginConfig.getChatTriggerKeyword().toLowerCase();
        }));
        
        getLogger().info("bStats metrics initialized successfully!");
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
                            "  url: 'https://ai.kynux.cloud/v1/chat/completions'\n" +
                            "  key: 'sk-c2dfa81d4b950eadba1ce55273e3d96830962fa1502cebdb987d04f4a8b9dd0b'\n" +
                            "  model: 'grok-3-mini'\n" +
                            "  temperature: 0.7\n" +
                            "  max_tokens: 500\n" +
                            "\n" +
                            "chat:\n" +
                            "  system_prompt: |\n" +
                            "    Sen yardımcı bir asistansın.\n" +
                            "    Minecraft oyuncusuna kısa ve öz yanıtlar ver.\n" +
                            "    Oyunculara yardımcı olmak için elinden geleni yap.\n" +
                            "  prefix: '&b[AI] &r'\n" +
                            "  broadcast_responses: true\n" +
                            "  allow_color_codes: true\n" +
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
