package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class SpecialCharactersHandlerService {

	private File file;
	private String[] originalFileAttribute;
	private LinkedHashSet<Integer> hasPipes;
	private LinkedHashSet<Integer> hasDoubleQuotes;
	private LinkedHashSet<String> pipedAttributes;
	private LinkedHashSet<String> doubleQuotedAttributes;
	private ArrayList<String> newAttributes;
	private ArrayList<ArrayList<String>> data;

	public SpecialCharactersHandlerService(File file) {
		this.file = file;
		newAttributes = new ArrayList<>();
		pipedAttributes = new LinkedHashSet<>();
		doubleQuotedAttributes = new LinkedHashSet<>();
		hasDoubleQuotes = new LinkedHashSet<>();
		data = new ArrayList<>();
	}

	/**
	 * Used to find which attribute have special characters
	 * 
	 * @throws FileNotFoundException
	 */
	public void detectSpecialCharacters() {
		try {
			Scanner scan = new Scanner(file);

			originalFileAttribute = scan.nextLine().split(",");
			hasPipes = new LinkedHashSet<>();
			ArrayList<String> values;

			while (scan.hasNext()) {
				values = new ArrayList<String>();

				String line = scan.nextLine();
				line = line.replace(",,", ",null,");

				values.addAll(Arrays.asList(line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)));

				if (values.size() < originalFileAttribute.length) {
					values.add("null");
				}

				data.add(values);

				for (int i = 0; i < values.size(); i++) {
					if (values.get(i).contains("|")) {
						hasPipes.add(i);
					}

					if (values.get(i).contains("\"")) {
						hasDoubleQuotes.add(i);
					}
				}
			}

			scan.close();
			buildAttributes();
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	/**
	 * Creates the new attributes based on the attributes that were found to
	 * have special characters
	 */
	public void buildAttributes() {
		for (ArrayList<String> arrList : data) {
			for (Integer i : hasPipes) {
				String[] temp = arrList.get(i).split("\\|");

				for (String s : temp) {
					pipedAttributes.add(
							originalFileAttribute[i] + "_" + s.replace(", ", "_").replace("'", "").replace("\"", ""));
				}
			}

			for (Integer i : hasDoubleQuotes) {
				String temp = arrList.get(i);

				if (temp.contains("\"")) {
					temp = temp.substring(1, temp.length() - 1);
				}

				String[] temp2 = temp.split(",");

				for (String s : temp2) {
					doubleQuotedAttributes.add(
							originalFileAttribute[i] + "_" + s.replace(", ", "_").replace("'", "").replace("\"", ""));
				}
			}
		}

		updateData();
	}

	/**
	 * Creates the list of updated file attributes
	 */
	public void updateData() {
		newAttributes = new ArrayList<>();

		for (int i = 0; i < originalFileAttribute.length; i++) {
			if (!newAttributes.contains(originalFileAttribute[i])) {
				newAttributes.add(originalFileAttribute[i]);
			}
		}

		for (String s : pipedAttributes) {
			if (!newAttributes.contains(s)) {
				newAttributes.add(s);
			}
		}

		for (String s : doubleQuotedAttributes) {
			if (!newAttributes.contains(s)) {
				newAttributes.add(s);
			}
		}
	}

	/**
	 * Creates the final preprocessed file
	 */
	public File updateFile() {
		StringBuilder sb = new StringBuilder();

		for (String s : newAttributes) {
			sb.append(s.replace("-", "_"));
			sb.append(",");
		}

		sb = removeSpecialCharactersColumns(sb);

		sb.deleteCharAt(sb.length() - 1);
		sb.append("\n");

		try {
			File finalPreprocessedFile = File.createTempFile("finalPreprocessedFile", ".csv");
			file.deleteOnExit();

			// write the new file attributes
			PrintWriter writer = new PrintWriter(finalPreprocessedFile);
			writer.write(sb.toString());

			for (ArrayList<String> arrList : data) {
				sb = new StringBuilder();

				for (int i = 0; i < originalFileAttribute.length; i++) {
					sb.append(arrList.get(i));
					sb.append(",");
				}

				for (int i = originalFileAttribute.length; i < originalFileAttribute.length
						+ pipedAttributes.size(); i++) {
					String pipeAttribute = newAttributes.get(i);
					boolean contained = false;

					for (int j : hasPipes) {
						String[] values = arrList.get(j).split("\\|");

						for (String s : values) {
							s = s.replace(", ", "_").replace("'", "").replace("\"", "");

							if (pipeAttribute.contains(s)) {
								contained = true;
								break;
							}
						}
					}
					if (contained) {
						sb.append("true");
						sb.append(",");
					} else {
						sb.append("false");
						sb.append(",");
					}
				}

				for (int i = originalFileAttribute.length + pipedAttributes.size(); i < newAttributes.size(); i++) {
					String quotedAttribute = newAttributes.get(i);
					boolean contained = false;

					for (int j : hasDoubleQuotes) {
						String[] values = arrList.get(j).split(",");

						for (String s : values) {
							s = s.replace(", ", "_").replace("'", "").replace("\"", "");

							if (quotedAttribute.contains(s)) {
								contained = true;
								break;
							}
						}
					}
					if (contained) {
						sb.append("true");
						sb.append(",");
					} else {
						sb.append("false");
						sb.append(",");
					}
				}

				sb.deleteCharAt(sb.length() - 1);
				sb.append("\n");

				sb = removeSpecialCharactersColumns(sb);

				sb.deleteCharAt(sb.length() - 1);

				writer.write(sb.toString());
			}

			writer.close();
			return finalPreprocessedFile;
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	/**
	 * Removes the columns that were found to have special characters from a
	 * StringBuilder
	 * 
	 * @param sb
	 *            the StringBuilder to remove the columns from
	 * @return the new StringBuilder with the removed columns
	 */
	public StringBuilder removeSpecialCharactersColumns(StringBuilder sb) {
		StringBuilder newSb = new StringBuilder();
		String[] line = sb.toString().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

		for (int i = 0; i < line.length; i++) {
			if (!hasPipes.contains(i) && !hasDoubleQuotes.contains(i)) {
				newSb.append(line[i]);
				newSb.append(",");
			}
		}

		return newSb;
	}
}
