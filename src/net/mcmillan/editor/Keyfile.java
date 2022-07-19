package net.mcmillan.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Keyfile {

	private Map<String, String> map;
	public String get(String k) { return map.get(k); }
	public void put(String k, String v) { map.put(k, v); }
	public Keyfile() {
		map = new HashMap<String, String>();
	}
	public Keyfile(String f) {
		this(new File(f));
	}
	public Keyfile(File f) {
		this();
		loadFrom(f);
	}
	public Keyfile(Map<String, String> map) {
		this.map = map;
	}
	private void loadFrom(File f) {
		if (!f.exists()) throw new IllegalArgumentException("File doesn't exist: " + f.toString());
		if (!f.canRead()) throw new IllegalArgumentException("Can't read: " + f.getAbsolutePath());
		int lineCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(f));) {
			while (true) {
				final String line = br.readLine();
				if (line == null) break;
				lineCount++;
				int sep = line.indexOf('=');
				if (sep < 0) throw new IllegalArgumentException("Line " + lineCount + " doesn't contain =");
				String k = line.substring(0,sep), v = line.substring(sep+1);
				map.put(k, v);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public void saveTo(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				throw new IllegalArgumentException(f.toString(), e);
			}
		}
		if (!f.canWrite()) throw new IllegalArgumentException("Can't write to " + f.getAbsolutePath());
		try (FileWriter fw = new FileWriter(f);) {
			for (String k : map.keySet()) {
				String v = map.get(k);
				if (k.contains("=")) throw new IllegalArgumentException("Illegal key: " + k);
				if (k.contains("\n")) throw new IllegalArgumentException("Illegal key: " + k);
				if (v.contains("\n")) throw new IllegalArgumentException("Illegal value: " + k);
				fw.write(k);
				fw.write("=");
				fw.write(v);
				fw.write("\n");
			}
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void print() {
		for (String k : map.keySet()) {
			String v = map.get(k);
			System.out.println(k + "=" + v);
		}
	}
}
