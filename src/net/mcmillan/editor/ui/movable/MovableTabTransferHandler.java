package net.mcmillan.editor.ui.movable;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class MovableTabTransferHandler extends TransferHandler {

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		MovableTab.Title t = (MovableTab.Title) c;
		System.out.println("[MovableTabTransferHandler.createTransferable] " + t.tab().name());
		return new MovableTabTransferable(t.tab());
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action == 0) return;
		if ((action & TransferHandler.MOVE) == 0x0) throw new IllegalArgumentException("Invalid action: " + action);
		MovableTabTransferable tx = (MovableTabTransferable) data;
		MovableTab.Title title = (MovableTab.Title) source;
		if (tx.tab.titleComponent != title) throw new IllegalStateException("Non-equal components!");
		System.out.println("[MovableTabTransferHandler.exportDone] " + title.tab().name());
	}
}
