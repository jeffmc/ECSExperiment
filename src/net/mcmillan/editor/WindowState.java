package net.mcmillan.editor;

public class WindowState {
	private static final String filename = "windowState.txt",
			windowX = "windowX", windowY = "windowY", 
			windowWidth ="windowWidth", windowHeight = "windowHeight", maximized = "maximized";
	
	public int windowX() { return i(windowX); }
	public void windowX(int v) { i(windowX, v); }
	public int windowY() { return i(windowY); }
	public void windowY(int v) { i(windowY, v); }
	public int windowWidth() { return i(windowWidth); }
	public void windowWidth(int v) { i(windowWidth, v); }
	public int windowHeight() { return i(windowHeight); }
	public void windowHeight(int v) { i(windowHeight, v); }
	public boolean maximized() { return b(maximized); }
	public void maximized(boolean m) { b(maximized, m); }
	
	private Keyfile kf;
	public WindowState() {
		try {
			kf = new Keyfile(filename);
			if (!testKeyfile()) throw new IllegalStateException("Test failed! " + findInvalidKey());
		} catch (Exception e) {
			System.err.println("Error reading window state: " + e.getMessage());
			valid = false;
			kf = new Keyfile();
		}
	}
	private int i(String k) throws NumberFormatException { return Integer.parseInt(kf.get(k)); }
	private void i(String k, int v) { kf.put(k,Integer.toString(v)); }
	
	private static final String TRUESTR = "true", FALSESTR = "false";
	private boolean b(String k) { 
		String v = kf.get(k);
		if (v.equals(TRUESTR)) {
			return true;
		} else if (v.equals(FALSESTR)) {
			return false;
		} else {
			throw new NumberFormatException("Boolean: " + v);
		}
	}
	private void b(String k, boolean v) { kf.put(k,v?TRUESTR:FALSESTR); }
	
	private static final String[] keys = new String[] {
			windowX, windowY, windowWidth, windowHeight, maximized
	};
	private static final Class<?>[] types = new Class[] {
			int.class, int.class, int.class, int.class, boolean.class
	};
	
	private Object v(String k) {
		for (int i=0;i<keys.length;i++) {
			if (!k.equals(keys[i])) continue;
			Class<?> c = types[i];
			if (c.equals(int.class)) {
				return i(k);
			} else if (c.equals(boolean.class)) {
				return b(k);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + c.getName());
			}
		}
		throw new NumberFormatException("Couldn't find key: " + k);
	}
	
	private boolean valid = false;
	public boolean valid() { return valid; }
	private boolean testKeyfile() {
		try {
			for (String k : keys) v(k);
		} catch (NumberFormatException e) {
			valid = false;
			return false;
		}
		valid = true;
		return true;
	}
	private String findInvalidKey() {
		for (String k : keys) {
			try {
				v(k);
			} catch (NumberFormatException e) {
				return k;
			}
		}
		return null;
	}
	public void save() {
		testKeyfile();
		if (valid) {
			kf.saveTo(filename);
		} else {
			System.err.println("Tried to save invalid window state: " + findInvalidKey());
		}
	}
}
