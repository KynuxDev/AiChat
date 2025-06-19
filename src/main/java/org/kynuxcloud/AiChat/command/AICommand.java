package org.kynuxcloud.AiChat.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.kynuxcloud.AiChat.AiChat;
import org.kynuxcloud.AiChat.config.Config;
import org.kynuxcloud.AiChat.service.AIService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AICommand implements CommandExecutor, TabCompleter {
    private final AiChat plugin;
    private final Config config;
    private final AIService aiService;
    
    public AICommand(AiChat plugin, Config config, AIService aiService) {
        this.plugin = plugin;
        this.config = config;
        this.aiService = aiService;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /ai <mesaj> veya /ai clear, /ai reload, /ai models");
            return true;
        }
        
        if (args.length == 1) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("clear")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    aiService.clearConversation(player.getName());
                    sender.sendMessage(ChatColor.GREEN + "AI sohbet geçmişiniz temizlendi.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Bu komut sadece oyuncular tarafından kullanılabilir.");
                }
                return true;
            }
            
            if (subCommand.equals("clearall") && sender.hasPermission("aichat.admin")) {
                aiService.clearAllConversations();
                sender.sendMessage(ChatColor.GREEN + "Tüm AI sohbet geçmişleri temizlendi.");
                return true;
            }
            
            if (subCommand.equals("reload") && sender.hasPermission("aichat.admin")) {
                plugin.reloadConfig();
                config.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "AiChat yapılandırması yeniden yüklendi.");
                return true;
            }
            
            if (subCommand.equals("models") && sender.hasPermission("aichat.admin")) {
                sender.sendMessage(ChatColor.YELLOW + "Model listesi alınıyor...");
                
                aiService.fetchAvailableModels(
                    new AIService.ModelsCallback() {
                        @Override
                        public void onSuccess(List<String> models) {
                            if (models.isEmpty()) {
                                sender.sendMessage(ChatColor.RED + "Hiç model bulunamadı.");
                                return;
                            }
                            
                            sender.sendMessage(ChatColor.GREEN + "Kullanılabilir modeller:");
                            for (String model : models) {
                                sender.sendMessage(ChatColor.AQUA + "- " + model);
                            }
                        }
                    },
                    new AIService.ErrorCallback() {
                        @Override
                        public void onError(String error) {
                            sender.sendMessage(ChatColor.RED + "Model listesi alınamadı: " + error);
                        }
                    }
                );
                
                return true;
            }
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut sadece oyuncular tarafından kullanılabilir.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("aichat.ai")) {
            player.sendMessage(ChatColor.RED + "Bu komutu kullanma izniniz yok.");
            return true;
        }
        
        String message = String.join(" ", args);
        
        player.sendMessage(ChatColor.GRAY + "AI'ya mesajınız iletiliyor...");
        
        aiService.sendMessage(
            player.getName(),
            message,
            new AIService.ResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    String messageContent = response;
                    
                    if (!config.isAllowColorCodes()) {
                        messageContent = messageContent.replaceAll("&[0-9a-fk-or]", "");
                    }
                    
                    String formattedResponse = ChatColor.translateAlternateColorCodes('&',
                        config.getChatPrefix() + messageContent);
                    
                    if (config.isBroadcastResponses()) {
                        Bukkit.broadcastMessage(formattedResponse);
                    } else {
                        player.sendMessage(formattedResponse);
                    }
                }
            },
            new AIService.ErrorCallback() {
                @Override
                public void onError(String error) {
                    player.sendMessage(ChatColor.RED + "AI Hatası: " + error);
                    plugin.getLogger().warning("AI API Hatası: " + error);
                }
            }
        );
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<String>();
        
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<String>(Arrays.asList("clear"));
            
            if (sender.hasPermission("aichat.admin")) {
                subCommands.add("clearall");
                subCommands.add("reload");
                subCommands.add("models");
            }
            
            String input = args[0].toLowerCase();
            for (String cmd : subCommands) {
                if (cmd.startsWith(input)) {
                    completions.add(cmd);
                }
            }
        }
        
        return completions;
    }
}
