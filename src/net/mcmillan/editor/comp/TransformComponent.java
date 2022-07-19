package net.mcmillan.editor.comp;

import net.mcmillan.ecs.ECSComponent;
import net.mcmillan.ecs.Entity;

public class TransformComponent extends ECSComponent {

	public double[] position = new double[3], size = new double[3], rotation = new double[3];
	
	public TransformComponent(Entity e) {
		super(e);
	}

}
