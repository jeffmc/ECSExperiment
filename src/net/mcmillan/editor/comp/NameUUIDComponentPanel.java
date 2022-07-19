package net.mcmillan.editor.comp;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.mcmillan.ecs.NameUUIDComponent;
import net.mcmillan.editor.App;

public class NameUUIDComponentPanel extends ComponentPanel<NameUUIDComponent> {

	public NameUUIDComponentPanel(App app, NameUUIDComponent c) {
		super(app, c);
	}
	
	private static final Color invalidRed = new Color(255, 170, 170);
	
	private JTextField nameField;
	private JLabel uuidLabel;
	
	@Override
	protected void makeUI() {
		this.removeAll();
		this.setLayout(new FlowLayout(FlowLayout.LEFT, App.PDG2, App.PDG2));
		
		nameField = new JTextField(20);
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) { update(e); }
			@Override
			public void insertUpdate(DocumentEvent e) { update(e); }
			@Override
			public void changedUpdate(DocumentEvent e) { update(e); }
			private void update(DocumentEvent e) {
				if (nameField.getText().equals(comp.name)) {
					nameField.setBackground(Color.WHITE);
				} else {
					nameField.setBackground(invalidRed);
				}
			}
		});
		nameField.addActionListener((e) -> {
			String newName = nameField.getText().trim();
			if (newName.length() > 0) {
				comp.name = newName;
				app.entityStateChanged(comp.ent());
			} else {
				JOptionPane.showMessageDialog(nameField, "Cannot set entity name to whitespace",
						"Invalid input", JOptionPane.ERROR_MESSAGE, null);
			}
		});
		
		uuidLabel = new JLabel("uuidLabel");
		
		this.add(nameField);
		this.add(uuidLabel);
	}
	
	@Override
	public void updateProperties() {
		nameField.setText(comp.name);
		nameField.setBackground(Color.WHITE);
		
		uuidLabel.setText(comp.uuidString);
		uuidLabel.setToolTipText(Long.toString(comp.uuid));
	}
	
}
