package de.SebastianMikolai.PlanetFx.Nick;

import org.bukkit.plugin.java.JavaPlugin;

import de.SebastianMikolai.PlanetFx.Nick.Datenbank.MySQL;

public class Nick extends JavaPlugin {
	
	private static Nick instance;
	
	public static Nick getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		MySQL.LadeTabellen();
		MySQL.getMinecraftServers();
		getCommand("nick").setExecutor(new CommandListener());
		new NickManager();
	}
		
	@Override
	public void onDisable() {
		
	}	
}