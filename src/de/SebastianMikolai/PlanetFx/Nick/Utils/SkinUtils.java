package de.SebastianMikolai.PlanetFx.Nick.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.SebastianMikolai.PlanetFx.Nick.Nick;
import de.SebastianMikolai.PlanetFx.Nick.NickManager;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;

public class SkinUtils {
	
	private static Field modifiers = getField(Field.class, "modifiers");
	private static Field uuidField = getField(GameProfile.class, "id");

	public static String[] getSkinData(UUID id) {
		String[] array = new String[3];
		if (NickManager.getInstance().PlayerDatas.containsKey(id)) {
			array = (String[]) NickManager.getInstance().PlayerDatas.get(id);
			return array;
		}
		File file = new File("plugins/PlanetFxNick/" + id.toString() + ".yml");
		if (!file.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/"
						+ id.toString().replace("-", "") + "?unsigned=false");
				URLConnection uc = url.openConnection();
				uc.setUseCaches(false);
				uc.setDefaultUseCaches(false);
				uc.setRequestProperty("User-Agent", "curl/7.26.0");
				uc.setRequestProperty("Host", "sessionserver.mojang.com");
				uc.setRequestProperty("Accept", "*/*");

				Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8");
				String json = scanner.useDelimiter("\\A").next();
				JSONArray properties = (JSONArray) ((JSONObject) new JSONParser().parse(json)).get("properties");
				for (int i = 0; i < properties.size(); i++) {
					JSONObject property = (JSONObject) properties.get(i);
					String name = (String) property.get("name");
					String value = (String) property.get("value");
					String signature = property.containsKey("signature") ? (String) property.get("signature") : null;
					array[0] = name;
					array[1] = value;
					array[2] = signature;
					NickManager.getInstance().PlayerDatas.put(id, array);
				}
				scanner.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			cfg.set("name", array[0]);
			cfg.set("value", array[1]);
			cfg.set("signature", array[2]);
			try {
				cfg.save(file);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			array[0] = cfg.getString("name");
			array[1] = cfg.getString("value");
			array[2] = cfg.getString("signature");
		}
		return array;
	}

	public static void updateGameProfile(GameProfile gp, UUID id) {
		String[] array = getSkinData(id);
		String name = array[0];
		String value = array[1];
		String signature = array[2];
		gp.getProperties().clear();
		gp.getProperties().put(name, new Property(name, value, signature));
	}

	public static void updateSkin(Player p, UUID id) {
		EntityPlayer ep = ((CraftPlayer) p).getHandle();
		UUID uuid = UUIDFetcher.getUUID(p.getName());
		try {
			uuidField.set(ep.getProfile(), uuid);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String[] data = getSkinData(id);
		ep.getProfile().getProperties().clear();
		ep.getProfile().getProperties().put(data[0], new Property(data[0], data[1], data[2]));
	}

	public static void updateSkinFrom(final Player p, UUID id, final boolean noSelfAutoUpdate) {
		String[] array = getSkinData(id);
		String name = array[0];
		String value = array[1];
		String signature = array[2];
		EntityPlayer ep = ((CraftPlayer) p).getHandle();
		try {
			uuidField.set(ep.getProfile(), UUIDFetcher.getUUID(p.getName()));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(
				PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] { ep });
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (!noSelfAutoUpdate) {
				((CraftPlayer) players).getHandle().playerConnection.sendPacket(remove);
			} else if (players != p) {
				((CraftPlayer) players).getHandle().playerConnection.sendPacket(remove);
			}
		}
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[] { p.getEntityId() });
		final Location oldloc = p.getLocation();
		final boolean isOp = p.isOp();
		if (!noSelfAutoUpdate) {
			NickManager.getInstance().NoDeath.add(p);
		}
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (players != p) {
				((CraftPlayer) players).getHandle().playerConnection.sendPacket(destroy);
			} else if (!noSelfAutoUpdate) {
				((CraftPlayer) players).getHandle().playerConnection.sendPacket(destroy);
			}
		}
		GameProfile gp = ep.getProfile();
		gp.getProperties().clear();
		gp.getProperties().put(name, new Property(name, value, signature));
		new BukkitRunnable() {
			public void run() {
				PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(ep);
				PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(p.getEntityId(), EnumDifficulty.EASY,
						ep.world.G(), ep.playerInteractManager.getGameMode());
				for (Player players : Bukkit.getOnlinePlayers()) {
					if (players != p) {
						((CraftPlayer) players).getHandle().playerConnection.sendPacket(spawn);
					} else {
						((CraftPlayer) players).getHandle().playerConnection.sendPacket(respawn);
					}
				}
				PacketPlayOutPlayerInfo add = new PacketPlayOutPlayerInfo(
						PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { ep });
				for (Player players : Bukkit.getOnlinePlayers()) {
					if (!noSelfAutoUpdate) {
						((CraftPlayer) players).getHandle().playerConnection.sendPacket(add);
					} else if (players != p) {
						((CraftPlayer) players).getHandle().playerConnection.sendPacket(add);
					}
				}
				if (!noSelfAutoUpdate) {
					p.teleport(oldloc);
					p.setOp(isOp);
					NickManager.getInstance().NoDeath.remove(p);
				}
			}
		}.runTaskLater(Nick.getInstance(), 2L);
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			if (Modifier.isFinal(f.getModifiers())) {
				modifiers.set(f, Integer.valueOf(f.getModifiers() & 0xFFFFFFEF));
			}
			return f;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
