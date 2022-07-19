package net.mcmillan.editor.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import net.mcmillan.ecs.ECSComponent;

public class CompTypeTransferHandler extends TransferHandler {
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}
	public static final DataFlavor componentSet = new DataFlavor(Class[].class, "componentSet");
	public static final DataFlavor[] dataFlavors = new DataFlavor[] { componentSet };
	@Override
	protected Transferable createTransferable(JComponent c) {
		JList<Class<? extends ECSComponent>> list = (JList<Class<? extends ECSComponent>>) c;
		List<Class<? extends ECSComponent>> comps = (List<Class<? extends ECSComponent>>) list.getSelectedValuesList();
		return new Transferable() {
			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.equals(dataFlavors[0]);
			}
			
			@Override public DataFlavor[] getTransferDataFlavors() { return dataFlavors; }
			
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (!flavor.equals(dataFlavors[0])) throw new UnsupportedFlavorException(flavor);
				return comps;
			}
		};
	}
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) { } // Dont handle moves
}
