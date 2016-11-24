package com.example.obd2.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Configuration {

	private static final int NOT_FOUND = -1;

	private final List<String> lines;
	private final File f;
	private final Map<String, String> cache;

	/**
	 * Initalize the Configuration class.
	 * <p>
	 * 
	 * @param f
	 *            Configuration file
	 * @throws IOException
	 */
	public Configuration(final File f) throws IOException {
		this.lines = new ArrayList<String>();
		this.f = f;
		this.cache = new HashMap<String, String>();
		readConfig();
	}

	/**
	 * Read the configuration file to cache.
	 * <p>
	 * 
	 * @throws IOException
	 */
	private void readConfig() throws IOException {
		if(!f.exists()){
			f.createNewFile();
		}
		BufferedReader br = new BufferedReader(new FileReader(f));
		while (br.ready()) {
			lines.add(br.readLine());
		}
		br.close();
	}

	/**
	 * Get the value for a given section and key.
	 * <p>
	 * 
	 * @param section
	 *            the section name
	 * @param key
	 *            a key in this section
	 * @return value for given key and section, or a <code>null</code> value if
	 *         section or key does not exist .
	 */
	public String getValue(final String section, final String key) {
		String result = cache.get(section + "." + key);
		if (result == null) { // if entry not in cache -> read from list
			int sectionPos = searchSectionStartPos(section);
			if (sectionPos == NOT_FOUND) {
				System.out.println("Section not found: " + section);
				return null;
			}
			int keyPos = searchKeyPos(key, sectionPos);
			if (keyPos == NOT_FOUND) {
				System.out.println("Key not found: " + key);
				return null;
			}
			result = readValue(keyPos);
			cache.put(section + "." + key, result);
		}
		return result;
	}

	public String getValue(final String section, final String key,
			final String valueDefault) {
		String val = getValue(section, key);
		if (val == null) {
			putValue(section, key, valueDefault);
			val = valueDefault;
		}
		//Log.v("", ">>>>>>>>>"+val+"<<<<<<<<<<<<");
		return val;
	}

	/**
	 * Add or modify a configuration entry.
	 * <p>
	 * 
	 * @param sectionName
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void putValue(final String sectionName, final String key,
			final String value) {

		cache.put(sectionName + "." + key, value);
		int sectStart = searchSectionStartPos(sectionName);
		if (sectStart == NOT_FOUND) { // add new section with k-v
			addSection(sectionName, key + "=" + value);
			sectStart = searchSectionStartPos(sectionName);
		}
		int entryPos = searchKeyPos(key, sectStart);
		if (entryPos == NOT_FOUND) { // add new key in existing section
			int insertPos = searchSectionEndPos(sectionName) + 1;
			lines.add(insertPos, key + "=" + value);
		} else { // modify existing k-v
			Entry e = parseLine(lines.get(entryPos));
			lines.set(entryPos, e.key + "=" + value
					+ (e.comment.length() > 0 ? "   #" + e.comment : ""));
		}
		writeConfig();
	}

	/**
	 * Add a section to the configuration.
	 * <p>
	 * 
	 * @param sectionName
	 * @param keyValuePairs
	 *            optional key-value pairs
	 * @throws IOException
	 */
	public void addSection(final String sectionName,
			final String... keyValuePairs) {
		if (searchSectionStartPos(sectionName) != NOT_FOUND) {
			throw new IllegalArgumentException("Section allready exists: "
					+ sectionName);
		}
		lines.add("\n[" + sectionName + "]");
		if (keyValuePairs != null) {
			for (String s : keyValuePairs) {
				lines.add(s);
				Entry e = parseLine(s);
				cache.put(sectionName + "." + e.key, e.value);
			}
		}
		writeConfig();
	}

	/**
	 * Remove a section and his entries from configuration.
	 * <p>
	 * 
	 * @param sectionName
	 * @throws IOException
	 */
	public void removeSection(final String sectionName) throws IOException {
		int sectStart = searchSectionStartPos(sectionName);
		int sectEnd = searchSectionEndPos(sectionName);
		if (sectStart == NOT_FOUND) {
			throw new IllegalArgumentException("Can not remove section: "
					+ sectionName);
		}
		for (int i = sectStart; i <= sectEnd; i++) {
			lines.remove(sectStart);
		}
		for (String key : cache.keySet()) {
			if (key.startsWith(sectionName)) {
				cache.remove(key);
			}
		}
		writeConfig();
	}

	/**
	 * Get a Collection of all section names.
	 * <p>
	 * 
	 * @return a Collection of section names
	 */
	public Collection<String> getSectionNames() {
		Collection<String> result = new LinkedList<String>();

		for (int i = 0; i < lines.size(); i++) {
			String s = lines.get(i).trim();
			if (s.startsWith("[") && s.contains("]")) {
				s = s.substring(1, s.length() - 1);
				result.add(s);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String s : lines) {
			sb.append(s);
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Write data to the configuration file.
	 * <p>
	 * If the configFile not allready exists, a new file will be created.
	 * 
	 * @throws IOException
	 *             if the file is not writeable.
	 */
	public void writeConfig() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (String s : lines) {
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	// -------------------------------------------------------------------------
	// private -----------------------------------------------------------------
	// -------------------------------------------------------------------------

	private int searchSectionStartPos(final String sectionName) {
		for (int i = 0; i < lines.size(); i++) {
			String s = lines.get(i).trim();
			if (s.startsWith("[" + sectionName + "]")) {
				return i;
			}
		}
		return -1; // not found
	}

	private int searchSectionEndPos(final String sectionName) {
		int start = searchSectionStartPos(sectionName);
		if (start == NOT_FOUND) {
			return NOT_FOUND;
		}
		for (int i = start + 1; i < lines.size(); i++) {
			String s = lines.get(i).trim();
			if (s.startsWith("[")) {
				return i - 1;
			}
		}
		return lines.size() - 1;
	}

	private int searchKeyPos(final String key, final int sectionPos) {
		for (int i = sectionPos + 1; i < lines.size(); i++) {
			String s = lines.get(i).trim();
			if (s.startsWith(key)) {
				return i;
			}
			if (s.startsWith("[")) {
				return -1;
			}
		}
		return NOT_FOUND;
	}

	private String readValue(final int pos) {
		if (pos == NOT_FOUND) {
			throw new IllegalArgumentException("negative position");
		}
		return parseLine(lines.get(pos)).value;
	}

	private Entry parseLine(final String line) {
		return new Entry(line);
	}

	/**
     * 
     */
	private static class Entry {

		final String key, value, comment;

		public Entry(final String line) {
			String[] sa1 = line.split("#", 2);
			this.comment = sa1.length == 2 ? sa1[1] : "";
			String[] sa2 = sa1[0].trim().split("=", 2);
			if (sa2.length < 2) {
				throw new IllegalArgumentException("can not parse line: "
						+ line);
			}
			this.key = sa2[0];
			this.value = sa2[1];
		}

		@Override
		public String toString() {
			return (key + "=" + value + "#" + comment);
		}
	}
}
