package de.SebastianMikolai.PlanetFx.Nick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.SebastianMikolai.PlanetFx.Nick.Utils.ChatUtils;
import de.SebastianMikolai.PlanetFx.Nick.Utils.FakePlayer;
import de.SebastianMikolai.PlanetFx.Nick.Utils.NameUtils;
import de.SebastianMikolai.PlanetFx.Nick.Utils.SkinUtils;
import de.SebastianMikolai.PlanetFx.Nick.Utils.UUIDFetcher;

public class NickManager {

	private static NickManager instance;
	public List<FakePlayer> FakePlayers = new ArrayList<FakePlayer>();
	public Map<Player, String> RealPlayers = new HashMap<Player, String>();
	private Map<UUID, UUID> NickedPlayers = new HashMap<UUID, UUID>();
	public HashMap<UUID, String[]> PlayerDatas = new HashMap<UUID, String[]>();
	public List<Player> NoDeath = new ArrayList<Player>();
	
	public NickManager() {
		instance = this;
	}
	
	public static NickManager getInstance() {
		return instance;
	}
	
	public boolean isPlayerNicked(UUID uuid) {
		if (NickedPlayers.containsKey(uuid)) {
			return true;
		} else {
			return false;
		}
	}
	
	public FakePlayer getRandomFakePlayer() {
		Random rnd = new Random();
		int id = rnd.nextInt(FakePlayers.size());
		return FakePlayers.get(id);
	}
	
	public void nickPlayer(Player p) {
		if (!isPlayerNicked(p.getUniqueId())) {
			FakePlayer fp = getRandomFakePlayer();
			if (fp != null) {
				NickedPlayers.put(p.getUniqueId(), fp.getUUID());
				SkinUtils.updateSkinFrom(p, fp.getUUID(), false);
				NameUtils.setName(p, fp.getName());
				p.setDisplayName(fp.getName());
				ChatUtils.sendMessageConfig(p, "Nicked");
			} else {
				ChatUtils.sendMessageConfig(p, "NotFound");
			}
		} else {
			ChatUtils.sendMessageConfig(p, "IsNicked");
		}
	}
	
	public void unnickPlayer(Player p) {
		if (isPlayerNicked(p.getUniqueId())) {
			String realName = NameUtils.getRealName(p);
			if (realName != null) {
				NickedPlayers.remove(p.getUniqueId());
				NameUtils.resetName(p);
				SkinUtils.updateSkinFrom(p, UUIDFetcher.getUUID(realName), false);
				p.setDisplayName(realName);
				ChatUtils.sendMessageConfig(p, "UnNicked");
			}
		} else {
			ChatUtils.sendMessageConfig(p, "IsNotNicked");
		}
	}
}