package net.mcmillan.ecs;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

public class Registry {

	public Random r = new Random();
	
	public ArrayList<Entity> entities = new ArrayList<>();
	public ArrayList<Class<? extends ECSComponent>> compTypes = new ArrayList<>();
//	public HashMap<Class<? extends Component>, ArrayList<? extends Component>> components = new HashMap<>();
	
	public Entity newEntity() {
		Entity e = new Entity(this);
		e.newComponent(NameUUIDComponent.class);
		entities.add(e);
		entityListModel.addedEntity(entities.size()-1);
		return e;
	}
	public Entity newEntity(String name) {
		Entity e = newEntity();
		NameUUIDComponent nuc = e.getComponent(NameUUIDComponent.class);
		nuc.name = name;
		entityStateChanged(e);
		return e;
	}
	
	protected void onComponentAttached(Entity e, ECSComponent c) {
	
	}
	
	public void entityStateChanged(Entity e) { entityListModel.entityStateChanged(e); }
	private EntityListModel entityListModel = new EntityListModel();
	public ListModel<Entity> getEntityListModel() { return entityListModel; }
	private class EntityListModel extends AbstractListModel<Entity> {
		@Override
		public int getSize() { return entities.size(); }
		@Override
		public Entity getElementAt(int idx) { return entities.get(idx); }
		
		public void addedEntity(int idx) {
			fireIntervalAdded(this, idx, idx);
		}
		public void entityStateChanged(Entity e) {
			int idx = entities.indexOf(e);
			fireContentsChanged(this, idx, idx);
		}
	}
	
	public void addComponentTypes(Class<? extends ECSComponent>[] newTypes) {
		int idx = compTypes.size();
		int added = 0;
		for (Class<? extends ECSComponent> ct : newTypes) {
			try {
				if (addComponentType(ct, false)) {
					added++;
				} else {
					System.err.println("[Registry] Couldn't add component type: " + ct.getSimpleName());
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Invalid Component Type: " + ct.getName());
				e.printStackTrace();
			}
		}
		if (added > 0) compTypeListModel.addedTypes(idx, added);
	}
	
	public boolean addComponentType(Class<? extends ECSComponent> type) { return addComponentType(type, true); }
	private boolean addComponentType(Class<? extends ECSComponent> type, boolean triggerListener) { // return true if added
		if (!verifyComponentType(type)) return false;
		compTypes.add(type);
		if (triggerListener) compTypeListModel.addedType(compTypes.size()-1);
		return true;
	}
	
	private boolean verifyComponentType(Class<? extends ECSComponent> newType) {
		try {
			newType.getConstructor(Entity.class);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			System.err.println("[Registry] Couldn't verify component type (" + newType.getName() +"): "
					+ "\n	" + e.getClass().getSimpleName() + ": "+ e.getMessage());
			return false;
		}
	}
	
	private CompTypeListModel compTypeListModel = new CompTypeListModel();
	public ListModel<Class<? extends ECSComponent>> getCompTypeListModel() {
		return compTypeListModel;
	}
	private class CompTypeListModel extends AbstractListModel<Class<? extends ECSComponent>> {
		@Override
		public int getSize() { return compTypes.size(); }
		@Override
		public Class<? extends ECSComponent> getElementAt(int idx) { return compTypes.get(idx); }
		
		public void addedType(int idx) {
			fireIntervalAdded(this, idx, idx);
		}
		public void addedTypes(int idx, int len) {
			if (len < 0) throw new IllegalArgumentException("Illegal length: " + len);
			if (len == 0) return;
			fireIntervalAdded(this, idx, idx+len-1);
		}
		public void removedType(int idx) {
			fireIntervalRemoved(this, idx, idx);
		}
	}
	
}
