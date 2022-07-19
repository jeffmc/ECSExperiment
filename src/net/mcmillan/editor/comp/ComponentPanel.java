package net.mcmillan.editor.comp;

import java.awt.Dimension;

import javax.swing.JPanel;

import net.mcmillan.ecs.ECSComponent;
import net.mcmillan.editor.App;

public abstract class ComponentPanel<T extends ECSComponent> extends JPanel {

	public final App app;
	public final T comp;
	
	public ComponentPanel(App app, T c) {
		super();
		this.app = app;
		comp = c;
		makeUI();
		updateProperties();
	}
	
	protected abstract void makeUI(); // Construct swing strucutre
	public abstract void updateProperties(); // Fill structure with data from component
	
	@Override
	public Dimension getMaximumSize() { // Fill widht, min height
		return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
	}
	
}
