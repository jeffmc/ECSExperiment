package net.mcmillan.editor.ui.movable;

import java.awt.datatransfer.DataFlavor;

public class MovableTabDataFlavor extends DataFlavor {

	public static final MovableTabDataFlavor INSTANCE = new MovableTabDataFlavor();
	public static final MovableTabDataFlavor[] INSTANCE_ARRAY = new MovableTabDataFlavor[] { INSTANCE };
	
	private MovableTabDataFlavor() {
		super(MovableTab.class, null);
	}
	
}
