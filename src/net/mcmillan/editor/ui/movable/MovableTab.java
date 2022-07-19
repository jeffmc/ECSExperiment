package net.mcmillan.editor.ui.movable;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

public class MovableTab {

	public final Component content;
	public final MovableTab.Title titleComponent;
	private String name;
	public String name() { return name; }
	public void name(String str) {
		name = str;
		titleComponent.update();
	}
	protected MovablePane parent;
	
	public MovableTab(String name, Component content) {
		this.name = name;
		this.content = content;
		titleComponent = new Title();
	}
	
	public class Title extends JLabel {
		public Title() {
			super(MovableTab.this.name);
			this.setOpaque(false);
			this.setFocusable(false);
			this.setTransferHandler(new MovableTabTransferHandler());
			DragMouseListener dml = new DragMouseListener() {
				private boolean started = false;
				@Override
				public void mouseMoved(MouseEvent e) {
					started = false;
					super.mouseMoved(e);
				}
				@Override
				public void mouseDragged(MouseEvent e) {
					if (started) return;
					started = true;
					JComponent c = (JComponent) e.getSource();
					TransferHandler handler = c.getTransferHandler();
					handler.exportAsDrag(c, e, TransferHandler.MOVE);
				}
			};
			this.addMouseListener(dml);
			this.addMouseMotionListener(dml);
		}
		public void update() {
			setText(MovableTab.this.name);
		}
		public MovableTab tab() {
			return MovableTab.this;
		}
		private class DragMouseListener extends MouseAdapter { // https://stackoverflow.com/a/53034794
	        @Override
	        public void mouseDragged(MouseEvent e) {
	        	redispatch(e);
	        }
	        @Override
        	public void mouseMoved(MouseEvent e) {
        		redispatch(e);
        	}
			@Override
	        public void mouseClicked(MouseEvent e)
	        {
	            redispatch(e);
	        }

	        @Override
	        public void mousePressed(MouseEvent e)
	        {
	            redispatch(e);
	        }

	        @Override
	        public void mouseReleased(MouseEvent e)
	        {
	            redispatch(e);
	        }

	        @Override
	        public void mouseEntered(MouseEvent e)
	        {
	            redispatch(e);
	        }

	        @Override
	        public void mouseExited(MouseEvent e)
	        {
	            redispatch(e);
	        }

	        private void redispatch(MouseEvent e)
	        {
	            Component source = e.getComponent();
	            Component target = source.getParent();
	            while (true)
	            {
	                if (target == null)
	                {
	                    break;
	                }
	                if (target instanceof JTabbedPane)
	                {
	                    break;
	                }
	                target = target.getParent();
	            }
	            if (target != null)
	            {
	                MouseEvent targetEvent =
	                    SwingUtilities.convertMouseEvent(source, e, target);
	                target.dispatchEvent(targetEvent);
	            }
	        }
		}
	}
	
}
