import net.mcmillan.ecs.ECSComponent;
import net.mcmillan.ecs.Entity;

public class ColorComponent extends ECSComponent {

	public int[] color;
	
	public ColorComponent(Entity e) {
		super(e);
	}

}
