package org.kynuxcloud.chatService.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.kynuxcloud.chatService.ChatService;
import org.kynuxcloud.chatService.config.Config;
import org.kynuxcloud.chatService.service.AIService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AICommand implements CommandExecutor, TabCompleter {
    private final ChatService plugin;
    private final Config config;
    private final AIService aiService;
    
    public AICommand(ChatService plugin, Config config, AIService aiService) {
        this.plugin = plugin;
        this.config = config;
        this.aiService = aiService;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Komut kontrolü
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Kullanım: /ai <mesaj> veya /ai clear, /ai reload, /ai models");
            return true;
        }
        
        // Alt komut kontrolü
        if (args.length == 1) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("clear")) {
                // Konuşma geçmişini temizle
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    aiService.clearConversation(player.getName());
                    sender.sendMessage(ChatColor.GREEN + "AI sohbet geçmişiniz temizlendi.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Bu komut sadece oyuncular tarafından kullanılabilir.");
                }
                return true;
            }
            
            if (subCommand.equals("clearall") && sender.hasPermission("chatservice.admin")) {
                // Tüm konuşma geçmişlerini temizle
                aiService.clearAllConversations();
                sender.sendMessage(ChatColor.GREEN + "Tüm AI sohbet geçmişleri temizlendi.");
                return true;
            }
            
            if (subCommand.equals("reload") && sender.hasPermission("chatservice.admin")) {
                // Config'i yeniden yükle
                plugin.reloadConfig();
                config.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "ChatService yapılandırması yeniden yüklendi.");
                return true;
            }
            
            if (subCommand.equals("models") && sender.hasPermission("chatservice.admin")) {
                // Mevcut modelleri göster
                sender.sendMessage(ChatColor.YELLOW + "Model listesi alınıyor...");
                
                aiService.fetchAvailableModels(
                    models -> {
                        // Başarılı olduğunda
                        if (models.isEmpty()) {
                            sender.sendMessage(ChatColor.RED + "Hiç model bulunamadı.");
                            return;
                        }
                        
                        sender.sendMessage(ChatColor.GREEN + "Kullanılabilir modeller:");
                        for (String model : models) {
                            sender.sendMessage(ChatColor.AQUA + "- " + model);
                        }
                    },
                    error -> {
                        // Hata durumunda
                        sender.sendMessage(ChatColor.RED + "Model listesi alınamadı: " + error);
                    }
                );
                
                return true;
            }
        }
        
        // Oyuncu kontrolü
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut sadece oyuncular tarafından kullanılabilir.");
            return true;
        }
        
        Player player = (Player) sender;
        
        // İzin kontrolü
        if (!player.hasPermission("chatservice.ai")) {
            player.sendMessage(ChatColor.RED + "Bu komutu kullanma izniniz yok.");
            return true;
        }
        
        // Mesajı birleştir
        String message = String.join(" ", args);
        
        // Kullanıcıyı bilgilendir
        player.sendMessage(ChatColor.GRAY + "AI'ya mesajınız iletiliyor...");
        
        // AI'ya isteği gönder
        aiService.sendMessage(
            player.getName(),
            message,
            response -> {
                // Başarılı yanıt
                String formattedResponse = ChatColor.translateAlternateColorCodes('&', 
                    config.getChatPrefix() + response);
                player.sendMessage(formattedResponse);
            },
            error -> {
                // Hata yanıtı
                player.sendMessage(ChatColor.RED + "AI Hatası: " + error);
                plugin.getLogger().warning("AI API Hatası: " + error);
            }
        );
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("clear"));
            
            // Admin komutları
            if (sender.hasPermission("chatservice.admin")) {
                subCommands.add("clearall");
                subCommands.add("reload");
                subCommands.add("models");
            }
            
            // Başlangıç filtreleme
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                    .filter(s -> s.startsWith(input))
                    .collect(Collectors.toList());
        }
        
        return completions;
    }
}
