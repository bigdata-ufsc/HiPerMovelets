package br.com.tarlis.mov3lets.utils;

//package com.github.mrebhan.crogamp.cli;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * The Class TableList.
 */
public class TableList {

//	private static final String[] BLINE = { "-", "\u2501" };
//	private static final String[] CROSSING = { "-+-", "\u2548" };
//	private static final String[] VERTICAL_TSEP = { "|", "\u2502" };
//	private static final String[] VERTICAL_BSEP = { "|", "\u2503" };
//	private static final String TLINE = "\u2500";
//	private static final String CORNER_TL = "\u250c";
//	private static final String CORNER_TR = "\u2510";
//	private static final String CORNER_BL = "\u2517";
//	private static final String CORNER_BR = "\u251b";
//	private static final String CROSSING_L = "\u2522";
//	private static final String CROSSING_R = "\u252a";
//	private static final String CROSSING_T = "\u252c";
//	private static final String CROSSING_B = "\u253b";

	/** The Constant BLINE. */
private static final String[] BLINE = { "-", "-" };
	
	/** The Constant CROSSING. */
	private static final String[] CROSSING = { "-+-", "+" };
	
	/** The Constant VERTICAL_TSEP. */
	private static final String[] VERTICAL_TSEP = { "|", "|" };
	
	/** The Constant VERTICAL_BSEP. */
	private static final String[] VERTICAL_BSEP = { "|", "|" };
	
	/** The Constant TLINE. */
	private static final String TLINE = "-";
	
	/** The Constant CORNER_TL. */
	private static final String CORNER_TL = "+";
	
	/** The Constant CORNER_TR. */
	private static final String CORNER_TR = "+";
	
	/** The Constant CORNER_BL. */
	private static final String CORNER_BL = "+";
	
	/** The Constant CORNER_BR. */
	private static final String CORNER_BR = "+";
	
	/** The Constant CROSSING_L. */
	private static final String CROSSING_L = "+";
	
	/** The Constant CROSSING_R. */
	private static final String CROSSING_R = "+";
	
	/** The Constant CROSSING_T. */
	private static final String CROSSING_T = "+";
	
	/** The Constant CROSSING_B. */
	private static final String CROSSING_B = "+";

	/** The descriptions. */
	private String[] descriptions;
	
	/** The table. */
	private ArrayList<String[]> table;
	
	/** The table sizes. */
	private int[] tableSizes;
	
	/** The rows. */
	private int rows;
	
	/** The findex. */
	private int findex;
	
	/** The filter. */
	private String filter;
	
	/** The ucode. */
	private boolean ucode;
	
	/** The comparator. */
	private Comparator<String[]> comparator;
	
	/** The spacing. */
	private int spacing;
	
	/** The aligns. */
	private EnumAlignment aligns[];

	/**
	 * Instantiates a new table list.
	 *
	 * @param descriptions the descriptions
	 */
	public TableList(String... descriptions) {
		this(descriptions.length, descriptions);
	}
	
	/**
	 * Instantiates a new table list.
	 *
	 * @param columns the columns
	 * @param descriptions the descriptions
	 */
	public TableList(int columns, String... descriptions) {
		if (descriptions.length != columns) {
			throw new IllegalArgumentException();
		}
		this.filter = null;
		this.rows = descriptions.length;
		this.descriptions = descriptions;
		this.table = new ArrayList<>();
		this.tableSizes = new int[columns];
		this.updateSizes(descriptions);
		this.ucode = true;
		this.spacing = 1;
		this.aligns = new EnumAlignment[columns];
		this.comparator = null;
		for (int i = 0; i < aligns.length; i++) {
			aligns[i] = EnumAlignment.LEFT;
		}
	}

	/**
	 * Update sizes.
	 *
	 * @param elements the elements
	 */
	private void updateSizes(String[] elements) {
		for (int i = 0; i < tableSizes.length; i++) {
			if (elements[i] != null) {
				int j = tableSizes[i];
				j = Math.max(j, elements[i].length());
				tableSizes[i] = j;
			}
		}
	}

	/**
	 * Compare with.
	 *
	 * @param c the c
	 * @return the table list
	 */
	public TableList compareWith(Comparator<String[]> c) {
		this.comparator = c;
		return this;
	}

	/**
	 * Sort by.
	 *
	 * @param column the column
	 * @return the table list
	 */
	public TableList sortBy(int column) {
		return this.compareWith((o1, o2) -> o1[column].compareTo(o2[column]));
	}

	/**
	 * Align.
	 *
	 * @param column the column
	 * @param align the align
	 * @return the table list
	 */
	public TableList align(int column, EnumAlignment align) {
		aligns[column] = align;
		return this;
	}

	/**
	 * With spacing.
	 *
	 * @param spacing the spacing
	 * @return the table list
	 */
	public TableList withSpacing(int spacing) {
		this.spacing = spacing;
		return this;
	}

	/**
	 * Adds a row to the table with the specified elements.
	 *
	 * @param elements the elements
	 * @return the table list
	 */

	public TableList addRow(Object... elements) {
		if (elements.length != rows) {
			throw new IllegalArgumentException();
		}
		String[] e = new String[elements.length];
		for (int i = 0; i < elements.length; i++)
			e[i] = String.valueOf(elements[i]);
		
		table.add(e);
		updateSizes(e);
		return this;
	}

	/**
	 * Filter by.
	 *
	 * @param par0 the par 0
	 * @param pattern the pattern
	 * @return the table list
	 */
	public TableList filterBy(int par0, String pattern) {
		this.findex = par0;
		this.filter = pattern;
		return this;
	}

	/**
	 * With unicode.
	 *
	 * @param ucodeEnabled the ucode enabled
	 * @return the table list
	 */
	public TableList withUnicode(boolean ucodeEnabled) {
		this.ucode = ucodeEnabled;
		return this;
	}

	/**
	 * Prints the.
	 *
	 * @return the string
	 */
	public String print() {
		String out = "";
		StringBuilder line = null;

		if (ucode) {
			for (int i = 0; i < rows; i++) {
				if (line != null) {
					line.append(CROSSING_T);
				} else {
					line = new StringBuilder();
					line.append(CORNER_TL);
				}
				for (int j = 0; j < tableSizes[i] + 2 * spacing; j++) {
					line.append(TLINE);
				}
			}
			line.append(CORNER_TR);
			out += line.toString() + "\n";
			line = null;
		}

		// print header
		for (int i = 0; i < rows; i++) {
			if (line != null) {
				line.append(gc(VERTICAL_TSEP));
			} else {
				line = new StringBuilder();
				if (ucode) {
					line.append(gc(VERTICAL_TSEP));
				}
			}
			String part = descriptions[i];
			while (part.length() < tableSizes[i] + spacing) {
				part += " ";
			}
			for (int j = 0; j < spacing; j++) {
				line.append(" ");
			}
			line.append(part);
		}
		if (ucode) {
			line.append(gc(VERTICAL_TSEP));
		}
		out += line.toString() + "\n";

		// print vertical seperator
		line = null;
		for (int i = 0; i < rows; i++) {
			if (line != null) {
				line.append(gc(CROSSING));
			} else {
				line = new StringBuilder();
				if (ucode) {
					line.append(CROSSING_L);
				}
			}
			for (int j = 0; j < tableSizes[i] + 2 * spacing; j++) {
				line.append(gc(BLINE));
			}
		}
		if (ucode) {
			line.append(CROSSING_R);
		}
		out += line.toString() + "\n";

		line = null;
		ArrayList<String[]> localTable = table;

		if (filter != null) {
			Pattern p = Pattern.compile(filter);
			localTable.removeIf(arr -> {
				String s = arr[findex];
				return !p.matcher(s).matches();
			});
		}

		if (localTable.isEmpty()) {
			String[] sa = new String[rows];
			localTable.add(sa);
		}

		localTable.forEach(arr -> {
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] == null) {
					arr[i] = "";
				}
			}
		});

		if (comparator != null) {
			localTable.sort(comparator);
		}

		for (String[] strings : localTable) {
			if (strings[0] == TLINE)
				out += printSeparator();
			else {
			for (int i = 0; i < rows; i++) {
				
				if (line != null) {
					line.append(gc(VERTICAL_BSEP));
				} else {
					line = new StringBuilder();
					if (ucode) {
						line.append(gc(VERTICAL_BSEP));
					}
				}
				String part = "";
				for (int j = 0; j < spacing; j++) {
					part += " ";
				}
				if (strings[i] != null) {
					switch (aligns[i]) {
					case LEFT:
						part += strings[i];
						break;
					case RIGHT:
						for (int j = 0; j < tableSizes[i] - strings[i].length(); j++) {
							part += " ";
						}
						part += strings[i];
						break;
					case CENTER:
						for (int j = 0; j < (tableSizes[i] - strings[i].length()) / 2; j++) {
							part += " ";
						}
						part += strings[i];
						break;
					}
				}
				while (part.length() < tableSizes[i] + spacing) {
					part += " ";
				}
				for (int j = 0; j < spacing; j++) {
					part += " ";
				}
				line.append(part);
			}
			if (ucode) {
				line.append(gc(VERTICAL_BSEP));
			}
			out += line.toString() + "\n";

			line = null;
			}
		}

		if (ucode) {
			for (int i = 0; i < rows; i++) {
				if (line != null) {
					line.append(CROSSING_B);
				} else {
					line = new StringBuilder();
					line.append(CORNER_BL);
				}
				for (int j = 0; j < tableSizes[i] + 2 * spacing; j++) {
					line.append(gc(BLINE));
				}
			}
			line.append(CORNER_BR);
			out += line.toString() + "\n";
		}

		return out;
		
	}

	/**
	 * Prints the separator.
	 *
	 * @return the string
	 */
	private String printSeparator() {
		StringBuilder line = null;
		if (ucode) {
			for (int i = 0; i < rows; i++) {
				if (line != null) {
					line.append(CROSSING_B);
				} else {
					line = new StringBuilder();
					line.append(CORNER_BL);
				}
				for (int j = 0; j < tableSizes[i] + 2 * spacing; j++) {
					line.append(gc(BLINE));
				}
			}
			line.append(CORNER_BR);
			return line.toString() + "\n";
		}
		return "";
	}

	/**
	 * Gc.
	 *
	 * @param src the src
	 * @return the string
	 */
	private String gc(String[] src) {
		return src[ucode ? 1 : 0];
	}

	/**
	 * The Enum EnumAlignment.
	 */
	public static enum EnumAlignment {
		
		/** The left. */
		LEFT, 
 /** The center. */
 CENTER, 
 /** The right. */
 RIGHT
	}

	/**
	 * Adds the rule.
	 */
	public void addRule() {
		table.add(new String[] {TLINE});
//		updateSizes(new String[] {});
	}

}