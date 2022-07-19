package net.mcmillan.ecs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Entity {
	
	private Registry reg;
	public Registry reg() { return reg; }
	private ArrayList<ECSComponent> comps = new ArrayList<ECSComponent>();
	
	protected Entity(Registry r) {
		reg = r;
	}
	
	public <T extends ECSComponent> T newComponent(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getConstructor(Entity.class);
			T c = constructor.newInstance(this);
			comps.add(c);
			reg.onComponentAttached(this, c);
			return c;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Invalid component class", e);
		}
	}
	
	public ECSComponent[] getAllComponents() {
		return comps.toArray(new ECSComponent[comps.size()]);
	}
	
	public boolean hasComponent(Class<? extends ECSComponent> clazz) {
		for (ECSComponent c : comps) 
			if (clazz.isInstance(c)) return true;
		return false;
	}
	
	public <T extends ECSComponent> T getComponent(Class<T> clazz) {
		for (ECSComponent c : comps) 
			if (clazz.isInstance(c)) return clazz.cast(c);
		throw new IllegalStateException("Entity doesn't contain component: " + clazz.getName());
	}
	public boolean removeComponent(ECSComponent c) {
		if (comps.remove(c)) {
			return true;
		} else {
			throw new IllegalStateException("Removed a component the entity didn't contain!");
		}
	}
}
