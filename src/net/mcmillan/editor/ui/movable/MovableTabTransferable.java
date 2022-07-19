package net.mcmillan.editor.ui.movable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class MovableTabTransferable implements Transferable {

	public final MovableTab tab;
	
	public MovableTabTransferable(MovableTab tab) {
		this.tab = tab;
	}
	 
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return MovableTabDataFlavor.INSTANCE_ARRAY;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor instanceof MovableTabDataFlavor;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!(flavor instanceof MovableTabDataFlavor)) throw new UnsupportedFlavorException(flavor);
		return tab;
	}

}
