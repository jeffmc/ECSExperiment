package net.mcmillan.editor.ui;

import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.mcmillan.ecs.ECSComponent;
import net.mcmillan.ecs.Entity;
import net.mcmillan.ecs.NameUUIDComponent;
import net.mcmillan.editor.App;
import net.mcmillan.editor.comp.ComponentPanel;
import net.mcmillan.editor.comp.NameUUIDComponentPanel;

public class EntityEditorPanel extends JPanel {

//	static {
//		System.load("./tools/libjava-tree-sitter.so");
//	}
	
	private final App app;
	private Entity ent;
	
	public EntityEditorPanel(App a) {
		this(a,null);
	}
	
	public EntityEditorPanel(App a, Entity e) {
		app = a;
		setEntity(e);
	}
	
	public void setEntity(Entity e) {
		ent = e;
		populateEditorPanel();
	}
	
	private HashMap<Class<?>, ComponentPanel<?>> compPanels = new HashMap<>();
	private void populateEditorPanel() {
		this.removeAll();
		compPanels.clear();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		if (ent == null) {
			this.add(new JLabel("No entity selected!"));
			return;
		}
		// Header:
		NameUUIDComponent nuc = ent.getComponent(NameUUIDComponent.class);
		NameUUIDComponentPanel nucp = new NameUUIDComponentPanel(app, nuc);
		compPanels.put(NameUUIDComponent.class, nucp);
		this.add(nucp);
		this.add(new JSeparator());
		
		// All other comps
		for (ECSComponent comp : ent.getAllComponents()) {
			if (comp.getClass().equals(NameUUIDComponent.class)) continue;
			this.add(makePanel(comp));
			this.add(new JSeparator());
		}
		
		this.revalidate();
		this.repaint();
	}
	
	private JComponent makePanel(ECSComponent comp) {
		
		return new JLabel(comp.getClass().getSimpleName());
		
	}

	private void updateEditorPanelState() {
		for (ComponentPanel<?> compPanel : compPanels.values()) compPanel.updateProperties();
	}
	
	public void stateChanged(Entity e) {
		if (e != ent) return;
		populateEditorPanel();
		updateEditorPanelState();
	}
	
}
