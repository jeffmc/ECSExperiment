package net.mcmillan.editor.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import net.mcmillan.editor.App;
import net.mcmillan.editor.Keyfile;

public class FileEditorPanel extends JPanel {

	private final File file;
	
	public FileEditorPanel() {
		this(new File("./components/ColorComponent.java"));
	}
	private FileEditorPanel(File f) {
		file = f;
		makeUI();
	}
	
	private JEditorPane editorPane;
	private JPanel debugBar = new JPanel() { // 100% parent width panel
		@Override
		public Dimension getMinimumSize() {
			return super.getMinimumSize();
		}
		@Override
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d.width = getParent().getWidth();
			return d;
		}
		@Override
		public Dimension getMaximumSize() {
			Dimension d = super.getPreferredSize();
			d.width = Integer.MAX_VALUE;
			return d;
		}
	};
	private PlainDocument doc;
	
	private void makeUI() {
		this.setLayout(new BorderLayout());
		
		setupPane();
		setupDebug();
		loadInKeyfile();
		
		loadFileIntoDoc();
		editorPane.setDocument(doc);;
		this.add(debugBar, BorderLayout.NORTH);
		JScrollPane sp = new JScrollPane(editorPane);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(sp, BorderLayout.CENTER);
	}
	private void setupPane() {
		editorPane = new JEditorPane();
		editorPane.setBackground(Color.DARK_GRAY);
		editorPane.setForeground(Color.WHITE);
		Font f = new Font(Font.MONOSPACED, Font.BOLD, 16);
		editorPane.setFont(f);
	}
	private JComboBox<Font> fontChooser;
	private JComboBox<Integer> sizeChooser;
	private void setupDebug() {
		debugBar.setLayout(new FlowLayout(FlowLayout.LEADING, App.PDG2, App.PDG2));
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		fontChooser = new JComboBox<>(ge.getAllFonts());
		fontChooser.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, ((Font)value).getName(), index, isSelected, cellHasFocus);
			}
		});
		final Integer[] ints = new Integer[128];
		for (int i=0;i<ints.length;i++) {
			ints[i] = (i+1)*2;
		}
		sizeChooser = new JComboBox<>(ints);
		ActionListener updateFont = (e) -> {
			Font f = (Font) fontChooser.getSelectedItem();
			Font ff = f.deriveFont(((Integer)sizeChooser.getSelectedItem()).floatValue());
			editorPane.setFont(ff);
		};
		sizeChooser.addActionListener(updateFont);
		fontChooser.addActionListener(updateFont);
		MouseWheelListener cbmwl = new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				JComboBox<?> cb = (JComboBox<?>) e.getSource();
				int d = Math.min(Math.max(-1,e.getWheelRotation()),1);
				int i = d + cb.getSelectedIndex();
				int ii = Math.min(Math.max(0,i),cb.getModel().getSize()-1);
				cb.setSelectedIndex(ii);
			}
		};
		sizeChooser.addMouseWheelListener(cbmwl);
//		fontChooser.addMouseWheelListener(cbmwl); // TOO LAGGY
		debugBar.add(fontChooser);
		debugBar.add(sizeChooser);
		updateFont.actionPerformed(null);
		
	}
	
	private static final String keyfilePath = "./fileEditorFont.txt", fontIdx = "fontIdx", fontSz = "fontSzIdx";
	private Keyfile kf;
	private void loadInKeyfile() {
		try {
			kf = new Keyfile(keyfilePath);
			int i = Integer.parseInt(kf.get(fontIdx));
			int sz = Integer.parseInt(kf.get(fontSz));
			fontChooser.setSelectedIndex(i);
			sizeChooser.setSelectedIndex(sz);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void shutdown() {
		saveToKeyfile();
	}
	private void saveToKeyfile() {
		try {
			kf = new Keyfile();
			int i = fontChooser.getSelectedIndex();
			int sz = sizeChooser.getSelectedIndex();
			kf.put(fontIdx, Integer.toString(i));
			kf.put(fontSz, Integer.toString(sz));
			kf.saveTo(keyfilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void loadFileIntoDoc() {
		doc = new PlainDocument();
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			if (line == null) return;
			sb.append(line);
			br.readLine();
			while (line != null) {
				sb.append("\n" + line);
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			doc.insertString(0, sb.toString(), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
