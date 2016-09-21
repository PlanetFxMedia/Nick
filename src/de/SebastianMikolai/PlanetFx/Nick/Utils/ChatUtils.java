package de.SebastianMikolai.PlanetFx.Nick.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.SebastianMikolai.PlanetFx.Nick.Nick;

public class ChatUtils {
	
	public static void sendMessageConfig(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', Nick.getInstance().getConfig().getString("Messages.Prefix")) + ChatColor.translateAlternateColorCodes('&', Nick.getInstance().getConfig().getString("Messages." + msg)));
	}
	
	public static void sendMessageConfig(CommandSender cs, String msg) {
		cs.sendMessage(ChatColor.translateAlternateColorCodes('&', Nick.getInstance().getConfig().getString("Messages.Prefix")) + ChatColor.translateAlternateColorCodes('&', Nick.getInstance().getConfig().getString("Messages." + msg)));
	}
	
	public static void sendMessageConfig(Player p, String msg, String replace, String replace_mitwas) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', Nick.getInstance().getConfig().getString("Messages.Prefix")) + ChatColor.translateAlternateColorCodes('&', Nick.getInstance().getConfig().getString("Messages." + msg).replace(replace, replace_mitwas)));
	}
	
	public static void sendMessageConfig(CommandSender cs, String msg, String replace, String replace_mitwas) {
		cs.sendMessage(ChatColor.translateAlternateColorCodes('&', Nick.getInstance().getConfig().getString("Messages.Prefix")) + ChatColor.translateAlternateColorCodes('&', Nick.getInstance().getConfig().getString("Messages." + msg).replace(replace, replace_mitwas)));
	}
}
