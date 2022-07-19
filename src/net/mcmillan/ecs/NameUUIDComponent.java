package net.mcmillan.ecs;

import java.util.Random;

public class NameUUIDComponent extends ECSComponent {
	
	private static Random rand = new Random();
	
	public String name;
	public final long uuid;
	public final String uuidString;
	
	public NameUUIDComponent(Entity e) {
		this(e, "Unnamed Entity", rand.nextLong());
	}
	
	public NameUUIDComponent(Entity e, String n, long uuid) {
		super(e);
		name = n;
		this.uuid = uuid;
		this.uuidString = Long.toHexString(uuid);
	}

}
