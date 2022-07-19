package net.mcmillan.ecs;

public abstract class ECSComponent {
	
	private Entity ent; 
	public Entity ent() { return ent; }
	
	public <T extends ECSComponent> T getComponent(Class<T> clazz) { 
		return ent.getComponent(clazz);
	}
	
	public ECSComponent(Entity e) {
		ent = e;
	}
	
}
