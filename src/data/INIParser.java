package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import core.Logger;

public class INIParser {
	
	private static final char SEPARATOR = '=';
	
	private File file;

	public INIParser(String path) {
		try {
			file = new File(path);
			if(!file.exists()) {
				FileWriter fw = new FileWriter(file);
				fw.flush();
				fw.close();
			}
		} catch(Exception e) {
			System.err.println("Error opening file " + path);
			e.printStackTrace();
		}
	}

	public boolean put(String key, String value) {
		try {
			// Read existing contents
			HashMap<String, String> map = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String next = br.readLine();
			while(next != null) {
				String currentKey = next.substring(0, next.indexOf(SEPARATOR));
				String currentValue = next.substring(next.indexOf(SEPARATOR) + 1);
				map.put(currentKey, currentValue);

				next = br.readLine();
			}

			// Add new contents and write to disk
			map.put(key, value);
			FileWriter fw = new FileWriter(file);
			for(String k : map.keySet()) {
				fw.write(k + SEPARATOR + map.get(k));
				fw.write("\r\n");
			}

			fw.flush();
			fw.close();
			br.close();
			return true;
		} catch(Exception e) {
			System.err.println("Error putting " + file.getAbsolutePath());
			e.printStackTrace();
			return false;
		}
	}
	
	public String get(String key, boolean required) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String next = br.readLine();
			while(next != null) {
				if(next.contains(key)) {
					br.close();
					return next.substring(next.indexOf(key + SEPARATOR) + (key.length() + 1));
				}
				next = br.readLine();
			}
			if(required) {
				Logger.log(INIParser.class, "Could not find " + key + " in file " + file.getAbsolutePath(), Logger.ERROR, true);
			}
			br.close();
			return null;
		} catch(Exception e) {
			System.err.println("Error getting " + key + " from " + file.getAbsolutePath());
			e.printStackTrace();
			return null;
		}
	}
	
}