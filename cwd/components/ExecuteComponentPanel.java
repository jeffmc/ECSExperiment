import java.awt.FlowLayout;

import javax.swing.JButton;

import net.mcmillan.ecs.NameUUIDComponent;
import net.mcmillan.ecstest.App;
import net.mcmillan.ecstest.comp.ComponentPanel;

public class ExecuteComponentPanel extends ComponentPanel<ExecuteComponent> {

	private NameUUIDComponent nuc;
	
	public ExecuteComponentPanel(App app, ExecuteComponent c) {
		super(app, c);
		nuc = comp.getComponent(NameUUIDComponent.class);
	}

	private JButton executeBtn;
	@Override
	protected void makeUI() {

		this.removeAll();
		this.setLayout(new FlowLayout(FlowLayout.LEADING, App.PDG2, App.PDG2));
		
		executeBtn = new JButton("Execute");
		executeBtn.addActionListener((e) -> {
			System.out.println("Execute: " + nuc.name + ", " + nuc.uuidString);
		});
	}

	@Override
	public void updateProperties() {
		
	}

}
