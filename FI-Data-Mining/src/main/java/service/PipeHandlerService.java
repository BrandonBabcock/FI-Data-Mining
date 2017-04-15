package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class PipeHandlerService {

	private File file;
	private String[] originalFileAttribute;
	private LinkedHashSet<Integer> hasPipes;
	private HashSet<String> pipedAttributes;
	private ArrayList<String> newAttributes;
	private ArrayList<ArrayList<String>> data;

	public PipeHandlerService(File file) {
		this.file = file;
		newAttributes = new ArrayList<>();
		pipedAttributes = new HashSet<>();
		data = new ArrayList<>();
	}

	public static void main(String[] args) {
		try {
			File testFile = new File("Data/PreprocessedFile.csv");
			PipeHandlerService service = new PipeHandlerService(testFile);
			service.detectPipe();
			service.updateFile();
		} catch (FileNotFoundException e) {

		}
	}

	/**
	 * Used to find which attribute has been piped
	 * 
	 * @throws FileNotFoundException
	 */
	public void detectPipe() throws FileNotFoundException {
		Scanner scan = new Scanner(file);

		originalFileAttribute = scan.nextLine().split(",");
		hasPipes = new LinkedHashSet<>();
		ArrayList<String> values;

		while (scan.hasNext()) {
			values = new ArrayList<String>();

			String line = scan.nextLine();
			line = line.replace(",,", ",null,");

			values.addAll(Arrays.asList(line.split(",")));

			if (values.size() < originalFileAttribute.length) {
				values.add("null");
			}

			data.add(values);

			for (int i = 0; i < values.size(); i++) {
				if (values.get(i).contains("|")) {
					hasPipes.add(i);
				}
			}
		}

		scan.close();

		buildAttributes();
	}

	public void buildAttributes() throws FileNotFoundException {
		for (ArrayList<String> arrList : data) {
			for (Integer i : hasPipes) {
				String[] temp = arrList.get(i).split("\\|");

				for (String s : temp) {
					pipedAttributes.add(originalFileAttribute[i] + "_" + s.replace(" ", "_").replace("'", "").replace("\"", ""));
				}
			}
		}

		updateData();
	}

	public void updateData() {
		newAttributes = new ArrayList<>();

		for (int i = 0; i < originalFileAttribute.length; i++) {
			if (!hasPipes.contains(i)) {
				newAttributes.add(originalFileAttribute[i]);
			}
		}

		for (String s : pipedAttributes) {
			newAttributes.add(s);
		}

		System.out.println("New attributes size: " + newAttributes.size());
	}

	public void updateFile() throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();

		for (String s : newAttributes) {
			sb.append(s.replace("-", "_"));
			sb.append(",");
		}

		sb.deleteCharAt(sb.length() - 1);
		sb.append("\n");

		// write the new file attributes
		PrintWriter writer = new PrintWriter(new File("Data/updatedPreprocessedFile.csv"));

		writer.write(sb.toString());

		for (ArrayList<String> arrList : data) {
			sb = new StringBuilder();

			for (int i = 0; i < newAttributes.size(); i++) {
				boolean contained = false;
				String att = newAttributes.get(i);

				if (pipedAttributes.contains(att)) {
					for (int j : hasPipes) {
						String[] values = arrList.get(j).split("\\|");

						for (String s : values) {
							s = s.replace(" ", "_").replace("'", "").replace("\"", "");
							
//							pipedAttributes.add(originalFileAttribute[i] + "_" + s.replace(" ", "_").replace("'", "").replace("\"", ""));
//							if (!s.equals("null")) {
//								System.out.println("att: " + att);
//								System.out.println("s: " + s);
//							}
							if (att.contains(s)) {
								contained = true;
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
				} else {
					sb.append(arrList.get(i));
					sb.append(",");
				}

			}

			System.out.println(sb.toString().split(",").length);

			sb.deleteCharAt(sb.length() - 1);
			sb.append("\n");
			writer.write(sb.toString());
		}

		sb.delete(sb.length() - 1, sb.length());
		writer.close();
	}
}
