package de.SebastianMikolai.PlanetFx.Nick;

import org.bukkit.plugin.java.JavaPlugin;

public class Nick extends JavaPlugin {
	
	private static Nick instance;
	
	public static Nick getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		getCommand("nick").setExecutor(new CommandListener());
		new NickManager();
	}
	
	@Override
	public void onDisable() {
		
	}
}