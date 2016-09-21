package de.SebastianMikolai.PlanetFx.Nick;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.SebastianMikolai.PlanetFx.Nick.Utils.ChatUtils;

public class CommandListener implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (cs instanceof Player) {
			Player p = (Player)cs;
			if (p.hasPermission("pfx.nick.use")) {
				if (NickManager.getInstance().isPlayerNicked(p.getUniqueId())) {
					NickManager.getInstance().unnickPlayer(p);
				} else {
					NickManager.getInstance().nickPlayer(p);
				}
			} else {
				ChatUtils.sendMessageConfig(p, "NoPermission");
			}
		} else {
			ChatUtils.sendMessageConfig(cs, "OnlyPlayer");
		}
		return true;
	}
}