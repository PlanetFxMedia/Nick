package de.SebastianMikolai.PlanetFx.Nick.Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;

import de.SebastianMikolai.PlanetFx.Nick.Nick;
import de.SebastianMikolai.PlanetFx.Nick.NickManager;
import de.SebastianMikolai.PlanetFx.Nick.Utils.FakePlayer;

public class MySQL {
	
	public static Connection con;
	
	public static Connection Connect() {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Nick.getInstance().getConfig().getString("database.host") + ":" + 
					Nick.getInstance().getConfig().getInt("database.port") + "/" + Nick.getInstance().getConfig().getString("database.db") + "?autoReconnect=true", Nick.getInstance().getConfig().getString("database.user"), Nick.getInstance().getConfig().getString("database.password"));
			return con;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Connection getConnection() {
		try {
			if (con == null) {
				con = Connect();
			} else if (con.isClosed()) {
				con = Connect();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static void LadeTabellen() {
		try {
			Connection c = getConnection();
			Statement stmt = c.createStatement();
			ResultSet rss = stmt.executeQuery("SHOW TABLES LIKE 'PlanetFxBanManagerUUID'");
			if (rss.next()) {
				Nick.getInstance().getLogger().info("Die Tabelle PlanetFxBanManagerUUID wurde geladen!");
			} else {
				System.exit(0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void getMinecraftServers() {
		try {
			Connection c = getConnection();
			Statement stmt = c.createStatement();
			ResultSet rss = stmt.executeQuery("SELECT * FROM PlanetFxBanManagerUUID");
			while (rss.next()) {
				Bukkit.getLogger().info(rss.getString("PlayerUUID"));
				Bukkit.getLogger().info(rss.getString("PlayerName"));
				UUID.fromString(rss.getString("PlayerUUID"));
				FakePlayer fp = new FakePlayer(UUID.fromString(rss.getString("PlayerUUID")), rss.getString("PlayerName"));
				NickManager.getInstance().FakePlayers.add(fp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}