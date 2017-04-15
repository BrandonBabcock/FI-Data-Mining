package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import data.Attribute;
import data.AttributeLocation;
import util.XmlToCsvConverter;

public class PreprocessingService {

	public ArrayList<Path> convertXmlToCsv(ArrayList<Path> files) {
		ArrayList<Path> convertedFiles = new ArrayList<Path>();

		for (Path filePath : files) {
			String fileName = filePath.getFileName().toString();

			if (fileName.toLowerCase().contains(".xml")) {
				XmlToCsvConverter converter = new XmlToCsvConverter();
				File csvFile = converter.convertToCsv(filePath.toFile());
				convertedFiles.add(Paths.get(csvFile.getAbsolutePath()));
			} else {
				convertedFiles.add(filePath);
			}
		}

		return convertedFiles;
	}

	public HashMap<Path, ArrayList<String>> mapAllAttributesToFiles(ArrayList<Path> files) {
		HashMap<Path, ArrayList<String>> allAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();

		for (Path filePath : files) {
			try {
				String[] firstLine = Files.lines(filePath).map(s -> s.split(",")).findFirst().get();
				allAttributesToFilesMap.put(filePath, new ArrayList<String>(Arrays.asList(firstLine)));
			} catch (IOException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}

		return allAttributesToFilesMap;
	}

	public ArrayList<String> findCommonAttributesInMap(HashMap<Path, ArrayList<String>> map) {
		HashMap<Path, ArrayList<String>> attributeSuffixesToFilesMap = createAttributeSuffixesToFilesMap(map);
		Map.Entry<Path, ArrayList<String>> entry = attributeSuffixesToFilesMap.entrySet().iterator().next();
		ArrayList<String> commonValues = new ArrayList<String>(entry.getValue());

		for (ArrayList<String> list : attributeSuffixesToFilesMap.values()) {
			commonValues.retainAll(list);
		}

		return commonValues;
	}

	private HashMap<Path, ArrayList<String>> createAttributeSuffixesToFilesMap(HashMap<Path, ArrayList<String>> map) {
		ArrayList<String> attributeSuffixes;
		HashMap<Path, ArrayList<String>> attributeSuffixesToFilesMap = new HashMap<Path, ArrayList<String>>();

		for (Map.Entry<Path, ArrayList<String>> entry : map.entrySet()) {
			attributeSuffixes = new ArrayList<String>();
			for (String attribute : entry.getValue()) {
				attributeSuffixes.add(attribute.substring(attribute.lastIndexOf(".") + 1));
			}
			attributeSuffixesToFilesMap.put(entry.getKey(), attributeSuffixes);
		}

		return attributeSuffixesToFilesMap;
	}

	public void createPreprocessedFile(HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap,
			HashMap<Path, ArrayList<String>> allAttributesToFilesMap, String groupByAttribute) {
		removeAttributeFromMap(wantedAttributesToFilesMap, groupByAttribute);

		HashMap<Path, AttributeLocation> attributeLocationsToFilesMap = mapAttributeLocationsToFiles(
				wantedAttributesToFilesMap, allAttributesToFilesMap, groupByAttribute);

		HashMap<String, ArrayList<Attribute>> userAttributesMap = mapGroupedByAttributes(wantedAttributesToFilesMap,
				allAttributesToFilesMap, attributeLocationsToFilesMap);

		buildFile(wantedAttributesToFilesMap, userAttributesMap, groupByAttribute);

		PipeHandlerService service = new PipeHandlerService(new File("Data/PreprocessedFile.csv"));
		try {
			service.detectPipe();
			service.updateFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void removeAttributeFromMap(HashMap<Path, ArrayList<String>> map, String attribute) {
		for (Iterator<Map.Entry<Path, ArrayList<String>>> mapIterator = map.entrySet().iterator(); mapIterator
				.hasNext();) {
			Map.Entry<Path, ArrayList<String>> entry = mapIterator.next();

			for (Iterator<String> entryIterator = entry.getValue().iterator(); entryIterator.hasNext();) {
				String attr = entryIterator.next();
				if (attr.contains(attribute)) {
					entryIterator.remove();
				}
			}
		}
	}

	private HashMap<Path, AttributeLocation> mapAttributeLocationsToFiles(
			HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap,
			HashMap<Path, ArrayList<String>> allAttributesToFilesMap, String groupByAttribute) {
		HashMap<Path, AttributeLocation> attributeLocationsToFilesMap = new HashMap<Path, AttributeLocation>();
		Scanner fileReader;
		for (Path filePath : wantedAttributesToFilesMap.keySet()) {
			// Read first line of file
			try {
				fileReader = new Scanner(filePath.toFile());
				String[] firstLine = fileReader.nextLine().split(",");

				// Create new AttributeLocation object for file
				AttributeLocation attributeLocation = new AttributeLocation();

				// Get the index of the attribute title to group by and store in
				// AttributeLocation field
				for (int i = 0; i < firstLine.length; i++) {
					if (firstLine[i].contains(groupByAttribute)) {
						attributeLocation.setGroupByIndex(i);
					}
				}

				// Get the indexes of the wanted attribute titles and store in
				// AttributeLocation field
				for (String wantedAttr : wantedAttributesToFilesMap.get(filePath)) {
					attributeLocation.addAttributeIndex(allAttributesToFilesMap.get(filePath).indexOf(wantedAttr));
				}

				attributeLocationsToFilesMap.put(filePath, attributeLocation);
			} catch (FileNotFoundException e) {
				System.out.println("File " + filePath + " not found.");
				e.printStackTrace();
			}
		}

		return attributeLocationsToFilesMap;
	}

	public HashMap<String, ArrayList<Attribute>> mapGroupedByAttributes(
			HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap,
			HashMap<Path, ArrayList<String>> allAttributesToFilesMap,
			HashMap<Path, AttributeLocation> attributeLocationsToFilesMap) {
		HashMap<String, ArrayList<Attribute>> userAttributesMap = new HashMap<String, ArrayList<Attribute>>();
		Scanner fileReader;

		for (Path filePath : wantedAttributesToFilesMap.keySet()) {
			try {
				fileReader = new Scanner(filePath.toFile());
				fileReader.nextLine(); // Skip first line

				// Loop through all lines of file
				while (fileReader.hasNextLine()) {

					// Remove ,'s between double quotes
					String readLine = fileReader.nextLine();
					String copy = new String();

					boolean inQuotes = false;

					for (int i = 0; i < readLine.length(); ++i) {
						if (readLine.charAt(i) == '"') {
							inQuotes = !inQuotes;
						}
//						if (readLine.charAt(i) == ',' && inQuotes) {
//							copy += '&';
//						} else {
//							copy += readLine.charAt(i);
//						}
						if (readLine.charAt(i) == ',' && inQuotes) {
//							copy += '&';
						} else {
							copy += readLine.charAt(i);
						}
					}

					String[] line = copy.split(",");
					String key = line[attributeLocationsToFilesMap.get(filePath).getGroupByIndex()];

					// Add new user key if map doesn't have it
					if (!userAttributesMap.containsKey(key)) {
						userAttributesMap.put(key, new ArrayList<Attribute>());
					}

					// Add all wanted attributes to the user key's ArrayList
					for (Integer num : attributeLocationsToFilesMap.get(filePath).getAttributeIndexes()) {
						String attributeTitle = allAttributesToFilesMap.get(filePath).get(num);
						Attribute attribute = new Attribute(attributeTitle, line[num]);

						boolean addAttribute = true;

						for (Attribute attr : userAttributesMap.get(key)) {
							if (attribute.getValue().equals(attr.getValue())) {
								addAttribute = false;
							}
						}

						if (addAttribute) {
							userAttributesMap.get(key).add(attribute);
						}

					}
				}

				fileReader.close();
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}

		return userAttributesMap;
	}

	private void buildFile(HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap,
			HashMap<String, ArrayList<Attribute>> userAttributesMap, String groupByAttribute) {
		ArrayList<String> attributeTitles = new ArrayList<String>();
		attributeTitles.add(groupByAttribute);

		for (ArrayList<String> attributes : wantedAttributesToFilesMap.values()) {
			for (String attribute : attributes) {
				attributeTitles.add(attribute);
			}
		}

		String fileHeader = String.join(",", attributeTitles) + "\n";

		try {
			FileWriter fileWriter = new FileWriter("Data/PreprocessedFile.csv");

			fileWriter.append(fileHeader);

			for (Map.Entry<String, ArrayList<Attribute>> entry : userAttributesMap.entrySet()) {
				String[] attributes = new String[attributeTitles.size()];
				attributes[0] = entry.getKey();

				for (Attribute attr : entry.getValue()) {
					if (attributes[attributeTitles.indexOf(attr.getTitle())] == null) {
						attributes[attributeTitles.indexOf(attr.getTitle())] = attr.getValue();
					} else {
						String combined = "";
						for (int i = 0; i < entry.getValue().size(); i++) {
							Attribute attr2 = entry.getValue().get(i);

							if (attr.getTitle().equals(attr2.getTitle())) {
								if (i != 0) {
									combined += "," + attr2.getValue();
								} else {
									combined += attr2.getValue();
								}
							}
						}

						if (combined.charAt(0) == ',') {
							combined = combined.replaceFirst(",", "");
						}

						attributes[attributeTitles.indexOf(attr.getTitle())] = "\"" + combined + "\"";
					}
				}

				String line = String.join(",", attributes) + "\n";

				fileWriter.append(line);
			}

			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("Unable to create file.");
			e.printStackTrace();
		}
	}

	public void handlePipes() {
		// Stores the column numbers the contain pipes
		ArrayList<Integer> columnsWithPipes = new ArrayList<Integer>();

		// Stores the attribute titles that contain pipes
		ArrayList<String> attributesWithPipes = new ArrayList<String>();

		try {
			Scanner fileReader = new Scanner(new File("Data/PreprocessedFile.csv"));

			// First line of the file as array and ArrayList
			String[] firstLine = fileReader.nextLine().split(",");
			ArrayList<String> firstLineList = new ArrayList<String>(Arrays.asList(firstLine));

			// Loop through all lines of file, populating the
			// columnsWithPipes and attributesWithPipes ArrayLists
			while (fileReader.hasNextLine()) {
				String[] line = fileReader.nextLine().split(",");

				for (int i = 0; i < line.length; i++) {
					if (line[i].contains("|")) {
						if (!columnsWithPipes.contains(i)) {
							columnsWithPipes.add(i);
							attributesWithPipes.add(firstLineList.get(i));
						}
					}
				}
			}

			fileReader = new Scanner(new File("Data/PreprocessedFile.csv"));

			fileReader.nextLine();

			// Map containing the column with a pipe as the key and the possible
			// values for that column (taking pipes into consideration) as an
			// ArrayList
			HashMap<Integer, ArrayList<String>> pipeColumnsToValuesMap = new HashMap<Integer, ArrayList<String>>();

			// Add an entry for each column and an empty ArrayList
			for (int i = 0; i < columnsWithPipes.size(); i++) {
				pipeColumnsToValuesMap.put(columnsWithPipes.get(i), new ArrayList<String>());
			}

			// Populate the empty ArrayList with the possible values of the
			// columns
			while (fileReader.hasNextLine()) {
				String[] line = fileReader.nextLine().split(",");

				for (int i = 0; i < columnsWithPipes.size(); i++) {
					String[] values = null;

					if (line[columnsWithPipes.get(i)].contains("|")) {
						values = line[columnsWithPipes.get(i)].split("\\|");
					}

					if (values != null) {
						for (int j = 0; j < values.length; j++) {
							if (!pipeColumnsToValuesMap.get(columnsWithPipes.get(i)).contains(values[j])) {
								pipeColumnsToValuesMap.get(columnsWithPipes.get(i)).add(values[j]);
							}
						}
					} else {
						if (!pipeColumnsToValuesMap.get(columnsWithPipes.get(i)).contains(line[columnsWithPipes.get(i)])) {
							pipeColumnsToValuesMap.get(columnsWithPipes.get(i)).add(line[columnsWithPipes.get(i)]);
						}
					}
				}
			}

			// Start making the new file
			try {
				FileWriter fileWriter = new FileWriter("Data/FinalPreprocessedFile.csv");
				fileReader = new Scanner(new File("Data/PreprocessedFile.csv"));

				for (int i = 0; i < columnsWithPipes.size(); i++) {
					for (String str : pipeColumnsToValuesMap.get(columnsWithPipes.get(i))) {
						System.out.println(str);
						str = str.replace(" ", "_");
						System.out.println(str);
						String newAttribute = attributesWithPipes.get(i) + "_" + str;
						firstLineList.add(newAttribute);
					}
				}

				String newFirstLine = String.join(",", firstLineList);
				System.out.println(newFirstLine);
				fileWriter.append(newFirstLine);
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PreprocessingService service = new PreprocessingService();

		service.handlePipes();
	}

}