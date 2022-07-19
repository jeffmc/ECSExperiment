package net.mcmillan.editor;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.lwjgl.nuklear.Nuklear;

import net.mcmillan.ecs.ECSComponent;
import net.mcmillan.ecs.Entity;
import net.mcmillan.ecs.NameUUIDComponent;
import net.mcmillan.ecs.Registry;
import net.mcmillan.editor.comp.TransformComponent;
import net.mcmillan.editor.dnd.CompTypeTransferHandler;
import net.mcmillan.editor.dnd.EntityTransferHandler;
import net.mcmillan.editor.ui.EntityEditorPanel;
import net.mcmillan.editor.ui.FileEditorPanel;
import net.mcmillan.editor.ui.ViewportPanel;
import net.mcmillan.editor.ui.movable.MovablePane;
import net.mcmillan.editor.ui.movable.MovableTab;
import net.mcmillan.editor.util.TeeOutputStream;

public class App {
	public static final int PDG0 = 12, PDG1 = 8, PDG2 = 4, PDG3 = 2;
	
	private JFrame frame = new JFrame("ESTTest");
	public final Registry registry = new Registry();
	
	private JList<Entity> entityList;
	private JList<Class<? extends ECSComponent>> compTypeList; // new String[] { "Rigidbody", "Mesh", "HoverableProperties", "Explosive", "ElectricityProducer" }
	
	private FileEditorPanel fileEditorPanel = new FileEditorPanel();
	private EntityEditorPanel entityEditorPanel;
	private ViewportPanel viewportPanel = new ViewportPanel();
	private JTextArea textLog = new JTextArea();
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenuItem[] itemsRequireSelection;
	
	private JFileChooser fileChooser;


//	private static void experiment() {
//		JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
//		File f = new File("components/ExecuteComponent.java");
//		File o = new File("tmp/bin");
//		if (!o.exists()) if (!o.mkdirs()) throw new IllegalStateException("Can't make temp directory!");
//		if (!f.exists()) throw new IllegalStateException("File doesn't exist: " + f.getAbsolutePath());
//		System.out.println(jc.name());
//		final String[] args = new String[] {
//				 "-verbose",
//				 "-cp ./",
//				 f.getPath(),
//		};
//		int code = jc.run(null, null, null, args);
//		System.out.println("Compiler returned: " + code);
//	}
	public static void main(String args[]) {
//		new Thread(() -> experiment()).start();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new App();
	}
	
	private static FileFilter jarFilter = new FileFilter() {
		@Override public String getDescription() { return "JAR Files"; }
		@Override public boolean accept(File f) { return f.isDirectory() || f.getName().endsWith(".jar"); }
	};
	private void chooseExternalJar() {
		fileChooser.setFileFilter(jarFilter);
		switch (fileChooser.showOpenDialog(frame)) {
		case JFileChooser.APPROVE_OPTION:
			break;
		case JFileChooser.CANCEL_OPTION:
			return;
		default:
			throw new IllegalStateException("Unsupported response from JFileChooser!");
		}
		File jarFile = fileChooser.getSelectedFile();
		try {
			loadJarFile(jarFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean loadJarFile(File jarfile) throws IOException, ClassNotFoundException {
		System.out.println("Loading: " + jarfile.getAbsolutePath());
		ArrayList<String> classNames = new ArrayList<String>();
		{
			// https://stackoverflow.com/questions/15720822/how-to-get-names-of-classes-inside-a-jar-file
			ZipInputStream zip = new ZipInputStream(new FileInputStream(jarfile));
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
			    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
			        // This ZipEntry represents a class. Now, what class does it represent?
			        String className = entry.getName().replace('/', '.'); // including ".class"
			        classNames.add(className.substring(0, className.length() - ".class".length()));
			    }
			}
			zip.close();
		}
		{
			URL jarURL = jarfile.toURI().toURL();
			new URLClassLoader(
				new URL[] { jarURL },
				ClassLoader.getSystemClassLoader()
			).close();
			Class<?>[] classes = new Class<?>[classNames.size()];
			for (int i=0;i<classes.length;i++) {
				Class<?> cl = Class.forName(classNames.get(i));
				classes[i] = cl;
			}
			Class<? extends ECSComponent>[] compTypes;
			compTypes = (Class<? extends ECSComponent>[]) new Class<?>[classes.length];
			int j=0;
			for (Class<?> c : classes) {
				try {
					Class<? extends ECSComponent> compType = c.asSubclass(ECSComponent.class);
					compTypes[j++] = compType;
					System.out.println("Found: " + compType.getName());
				} catch (ClassCastException e) { }
			}
			registry.addComponentTypes(Arrays.copyOf(compTypes, j));
			return true;
		}
	}
	
	public App() {
		entityEditorPanel = new EntityEditorPanel(this);
		new Thread(() -> { // TODO: Make this safe, but it also ruins startup times
			fileChooser = new JFileChooser(new File(".")); 
		}).start();
		registry.addComponentType(TransformComponent.class);
		setupFakeScene();
		makeLists();

		setupMenuBar();
		frame.setJMenuBar(menuBar);
		frame.add(makeUIStructure());

		setupListBehaviour();
		
		setupLogTunnel();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.pack();
		
		ws = new WindowState();
		windowStateSetup();
		
		frame.setVisible(true);
		viewportPanel.run();

//		try {
//			loadJarFile(new File("./External.jar")); // TODO: Load individual class files when they're compiled at runtime
//		} catch (ClassNotFoundException | IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private void setupFakeScene() {
		registry.newEntity("Example entity");
		registry.newEntity("Human");
		registry.newEntity("Dog");
		registry.newEntity("Tree");
		registry.newEntity("Building");
		registry.newEntity("Among us");
	}
	
	private void setupLogTunnel() {
		textLog.setEditable(false);
		TeeOutputStream tos = new TeeOutputStream(System.out, new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				textLog.append(Character.toString((char) b));
			}
		}, true));
		System.setOut(new PrintStream(tos));
	}
	
	private final WindowState ws;
	private void windowStateSetup() {
		if (ws.valid()) {
			if (ws.maximized()) {
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			} else {
				frame.setLocation(ws.windowX(), ws.windowY());
				frame.setSize(ws.windowWidth(), ws.windowHeight());
			}
		}
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				fileEditorPanel.shutdown();
				ws.windowX(frame.getX());
				ws.windowY(frame.getY());
				ws.windowWidth(frame.getWidth());
				ws.windowHeight(frame.getHeight());
				ws.maximized(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH);
				ws.save();
			}
		});
	}
	
	private void makeLists() {
		entityList = new JList<>(registry.getEntityListModel());
		entityList.setCellRenderer(new DefaultListCellRenderer() {
		    public Component getListCellRendererComponent(
		        JList<?> list,
		        Object value,
		        int index,
		        boolean isSelected,
		        boolean cellHasFocus)
		    {
		    	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		    	Entity ent = (Entity) value;
		    	NameUUIDComponent nuc = ent.getComponent(NameUUIDComponent.class);
				this.setText(nuc.name);
				this.setToolTipText(nuc.uuidString);
				return this;
			}
		});
		compTypeList = new JList<Class<? extends ECSComponent>>(registry.getCompTypeListModel());
		compTypeList.setCellRenderer(new DefaultListCellRenderer() {
		    public Component getListCellRendererComponent(
		        JList<?> list,
		        Object value,
		        int index,
		        boolean isSelected,
		        boolean cellHasFocus)
		    {
		    	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		    	Class<? extends ECSComponent> compType = (Class<? extends ECSComponent>) value;
				this.setText(compType.getSimpleName());
				this.setToolTipText(compType.getName());
				return this;
			}
		});
	}
	
	private void setupListBehaviour() {
		entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		compTypeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		entityList.addListSelectionListener((e) -> {
			Entity ent = entityList.getSelectedValue();
			
			boolean nonNull = ent != null;
			for (JMenuItem mi : itemsRequireSelection) mi.setEnabled(nonNull);
			
			entityEditorPanel.setEntity(ent);
			
		});
		entityList.setDropMode(DropMode.ON);
		entityList.setTransferHandler(new EntityTransferHandler(this));
		compTypeList.setDragEnabled(true);
		compTypeList.setTransferHandler(new CompTypeTransferHandler());
	}

	public void entityStateChanged(Entity ent) {
		registry.entityStateChanged(ent);
		entityEditorPanel.stateChanged(ent);
	}
	
	
	private void setupMenuBar() {
		JMenu file = new JMenu("File"), entity = new JMenu("Entity");
		menuBar.add(file);
		menuBar.add(entity);
		JMenuItem loadExternalPackage = new JMenuItem("Load external package");
		file.add(new JMenuItem("Open scene"));
		file.add(new JMenuItem("Save scene"));
		file.add(loadExternalPackage);
		loadExternalPackage.addActionListener((e)->chooseExternalJar());
		loadExternalPackage.setMnemonic(KeyEvent.VK_O);
		JMenuItem newEntity = new JMenuItem("New entity"), 
				removeEntity = new JMenuItem("Remove entity"), 
				copyEntity = new JMenuItem("Copy entity"), 
				cutEntity = new JMenuItem("Cut entity"),
				pasteEntity = new JMenuItem("Paste entity");
		entity.add(newEntity);
		entity.add(removeEntity);
		entity.add(copyEntity);
		entity.add(cutEntity);
		entity.add(pasteEntity);
		itemsRequireSelection = new JMenuItem[] { removeEntity, copyEntity, cutEntity };
		for (JMenuItem mi : itemsRequireSelection) mi.setEnabled(false);
		
		newEntity.addActionListener((e) -> {
			registry.newEntity();
		});
	}
	
	private JComponent makeUIStructure() {
		MovablePane compEntDiv = new MovablePane( 
				new MovableTab("Entities", new JScrollPane(entityList)),
				new MovableTab("Components", new JScrollPane(compTypeList)));

		MovablePane yDiv = new MovablePane(MovablePane.HORIZONTAL, 
				new MovablePane(MovablePane.VERTICAL,
					new MovableTab("Scene Viewport", viewportPanel),
					new MovableTab("Log", new JScrollPane(textLog))),
				new MovablePane(new MovableTab("File Editor", fileEditorPanel),
					new MovableTab("Entity Editor", new JScrollPane(entityEditorPanel))));
		
		MovablePane xDiv = new MovablePane(MovablePane.HORIZONTAL, compEntDiv, yDiv);
		
		return xDiv.getPane();
	}

	private JTabbedPane giveLabel(JComponent c, String label, int pdg, boolean noRight) {
		JTabbedPane p = new JTabbedPane();
		p.insertTab(label, null, c, null, 0);
		p.setBorder(BorderFactory.createEmptyBorder(pdg,pdg,pdg,noRight?0:pdg));
		return p;
	}
	private JComponent giveLabel(JComponent c, String label, int pdg) {
		return giveLabel(c, label, pdg, false);
	}
	private JComponent giveLabel(JComponent c, String label) {
		return giveLabel(c, label, 0);
	}
	
}
