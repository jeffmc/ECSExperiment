package net.mcmillan.editor.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JList;
import javax.swing.TransferHandler;

import net.mcmillan.ecs.ECSComponent;
import net.mcmillan.ecs.Entity;
import net.mcmillan.editor.App;

public class EntityTransferHandler extends TransferHandler {
	
	private static DataFlavor componentSet = CompTypeTransferHandler.componentSet;
	private static DataFlavor[] dataFlavors = new DataFlavor[] { componentSet };
	
	private App app;
	public EntityTransferHandler(App a) {
		app = a;
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		return findFlavor(support) != null;
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		DataFlavor df = findFlavor(support);
		if (!df.equals(componentSet)) return false;
		try {
			Object transferData = support.getTransferable().getTransferData(df);
			if (!(transferData instanceof List<?>)) throw new IllegalStateException("Illegal data!");
			List<?> transferList = (List<?>) transferData;
			List<Class<? extends ECSComponent>> comps = (List<Class<? extends ECSComponent>>) transferList;
			JList.DropLocation dropLoc = (JList.DropLocation) support.getDropLocation();
			Entity ent = app.registry.getEntityListModel().getElementAt(dropLoc.getIndex());
			System.out.println("Comps: " + comps.size());
			for (Class<? extends ECSComponent> comp : comps) {
				System.out.println("ent new comp: " + comp.getSimpleName());
				try {
					ent.newComponent(comp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			app.entityStateChanged(ent);
			return true;
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private DataFlavor findFlavor(TransferSupport support) {
		for (DataFlavor f : support.getDataFlavors()) {
			for (DataFlavor t : dataFlavors) {
				if (f.equals(t)) return f;
			}
		}
		return null;
	}
	
}
