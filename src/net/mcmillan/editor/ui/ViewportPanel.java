package net.mcmillan.editor.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class ViewportPanel extends Canvas {

	public ViewportPanel() {
		setMinimumSize(new Dimension(10,10));
		setPreferredSize(getMinimumSize());
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setIgnoreRepaint(true);
	}
	public void run() {
		threadIt();
	}
	
	private BufferStrategy findBufferStrategy() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			bs = getBufferStrategy();
		}
		return bs;
	}
	private Graphics getBufferedDrawGraphics() {
		BufferStrategy bs = findBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		return g;
	}
	private void flipBuffers() {
		BufferStrategy bs = findBufferStrategy();
		bs.show();
	}
	private long frameCount = 0;
	private void draw(long delta) {
		Graphics g = getBufferedDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.RED);
		g.drawRect(10, 10, 100, 100);
		g.setColor(Color.GREEN);
		g.drawString(Long.toString(frameCount), 10, 25);
		
		frameCount++;
	}
	private void threadIt() {
		new Thread(() -> {
			long last = System.currentTimeMillis(), now = last, d = 0;
			final long FRAMETIME = 16;
			draw(FRAMETIME);
			while (true) {
				now = System.currentTimeMillis();
				d = now - last;
				if (d >= FRAMETIME) {
					last = now;
					draw(d);
					flipBuffers();
//					System.out.println(d);
				}
			}
		}, "ViewportCanvasThread").start();
	}
	
}
