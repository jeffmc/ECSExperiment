package net.mcmillan.editor.ui.movable;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class MovablePane {
	
	public JPanel getPane() { return pane; }
	private JPanel pane = new JPanel(new BorderLayout()); // will contain either MovableTabbedPane or JSplitPane
	
	// should be size=1 if single, 
	// if double, 0 is left/top, 1 is bot/right
	// otherwise tabs are in content (order not guaranteed)
	private ArrayList<MovableTab> tabs = null; // null if split, size=1 if single, size > 1 if tabbed 
	private MovablePane parent = null,
			mp0 = null, mp1 = null; // non-null only if split
	
	private JSplitPane splitPane = null; // both components should be MovablePane's pane
	private MovableTabbedPane movableTabbedPane = null; // both components should be Mov
	private int splitOrientation = -1;
	public static final int VERTICAL = JSplitPane.VERTICAL_SPLIT, 
			HORIZONTAL = JSplitPane.HORIZONTAL_SPLIT;
	
	public boolean isSplit() {
		return (mp0 != null && mp1 != null) && tabs == null;
	}
	public boolean isSingle() {
		return (mp0 == null && mp1 == null) && tabs != null && tabs.size() == 1;
	}
	public boolean isTabbed() {
		return (mp0 == null && mp1 == null) && tabs != null && tabs.size() > 1;
	}
	
	private MovablePaneState state = null;
	public MovablePaneState getPaneState() { return state; }
	public enum MovablePaneState { SINGLE, SPLIT, TABBED; }

	public int getSplitLocation() { return splitPane.getDividerLocation(); } 
	public void setSplitLocation(int locPx) { splitPane.setDividerLocation(locPx); } 
	public void setSplitLocation(float locFrac) { splitPane.setDividerLocation((int)(locFrac * (float)splitPane.getWidth())); } 
	
	public MovablePane(int orientation, MovableTab mt0, MovableTab mt1) {
		this(orientation, new MovablePane(mt0), new MovablePane(mt1));
	}
	public MovablePane(int orientation, MovablePane mp0, MovablePane mp1) {
		this.splitOrientation = orientation;
		this.mp0 = mp0;
		if (mp0.parent != null) mp0.parent.remove(mp0);
		mp0.parent = this;
		this.mp1 = mp1;
		if (mp1.parent != null) mp1.parent.remove(mp1);
		mp1.parent = this;
		update();
	}
	public MovablePane(MovableTab ...tabs) {
		this.tabs = new ArrayList<>();
		for (MovableTab tab : tabs) {
			if (tab.parent != null) tab.parent.remove(tab);
			tab.parent = this;
			this.tabs.add(tab);
		}
		update();
	}
	
	public boolean remove(MovableTab tab) {
		boolean removed = tabs.remove(tab);
		update();
		if (tabs.size() < 1) this.parent.remove(this);
		return removed;
	}
	public boolean remove(MovablePane pane) {
		boolean removed = false;
		MovablePane remainder = null;
		if (mp0 == pane) {
			removed = true;
			mp0 = null;
			remainder = mp1;
		}
		if (mp1 == pane) {
			if (removed) throw new IllegalStateException("Pane is both sides of split!");
			mp1 = null;
			remainder = mp0;
		}
		this.parent.resolve(this,remainder);
		return removed;
	}
	private void resolve(MovablePane crush, MovablePane remainder) {
		if (mp0 == mp1) throw new IllegalStateException("Splits are equal!");
		if (mp0 == crush) {
			mp0 = remainder;
		} else if (mp1 == crush) {
			mp1 = remainder;
		} else {
			throw new IllegalStateException("This pane didn't contain the crush");
		}
		if (mp0 == mp1) throw new IllegalStateException("Splits are equal!");
		updateSwing();
	}
	
	private void updateSwing() {
		pane.revalidate();
	}
	
	public void update() {
		pane.removeAll();
		splitPane = null;
		movableTabbedPane = null;
		if (isSplit()) {
			setupSplit();
		} else if (isSingle()) {
			setupTabbed();
		} else if (isTabbed()) {
			setupTabbed();
		} else {
			throw new IllegalStateException("Pane is in unknown state!");
		}
	}
	
	private void setupSplit() {
		if (splitOrientation != HORIZONTAL && splitOrientation != VERTICAL) 
			throw new IllegalStateException("Illegal SplitOrientation: " + splitOrientation);
		splitPane = new JSplitPane(splitOrientation, mp0.getPane(), mp1.getPane());
		pane.add(splitPane);
		updateSwing();
	}
	
	private void setupTabbed() {
		movableTabbedPane = new MovableTabbedPane();
		for (MovableTab tab : tabs) {
			movableTabbedPane.addMovableTab(tab);
		}
		pane.add(movableTabbedPane);
		updateSwing();
	}	
	
	private class MovableTabbedPane extends JTabbedPane {
		
		public MovableTabbedPane() {
			super();
		}
		
		public void addMovableTab(MovableTab tab) {
			this.addTab(null, tab.content);
			this.setTabComponentAt(this.indexOfComponent(tab.content), tab.titleComponent);
		}
		
	}
}