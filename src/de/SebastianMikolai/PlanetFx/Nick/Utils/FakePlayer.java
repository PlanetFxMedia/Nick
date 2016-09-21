package de.SebastianMikolai.PlanetFx.Nick.Utils;

import java.util.UUID;

public class FakePlayer {
	
	final UUID uuid;
	final String name;
	
	public FakePlayer(UUID _uuid, String _name) {
		uuid = _uuid;
		name = _name;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
}