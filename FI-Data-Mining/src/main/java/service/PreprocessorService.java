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
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import data.Attribute;
import data.AttributeLocation;
import util.XmlToCsvConverter;

/**
 * Service for preprocessing tasks
 */
public class PreprocessorService {

	/**
	 * Converts any XML files in a list of file paths to CSV files, and keeps
	 * the other files the same
	 * 
	 * @param files
	 *            the list of files to convert
	 * @return the converted files
	 */
	public List<Path> convertXmlToCsv(List<Path> files) {
		List<Path> convertedFiles = new ArrayList<Path>();

		for (Path filePath : files) {
			String fileName = filePath.getFileName().toString();

			if (fileName.toLowerCase().substring(fileName.length() - 4).equals(".xml")) {
				File csvFile = XmlToCsvConverter.convertToCsv(filePath.toFile());
				convertedFiles.add(Paths.get(csvFile.getAbsolutePath()));
			} else {
				convertedFiles.add(filePath);
			}
		}

		return convertedFiles;
	}

	/**
	 * Creates a map containing the file paths as keys and lists of the
	 * attributes within the files as values
	 * 
	 * @param files
	 *            the files to map the attributes to
	 * @return a map of the file paths mapped to their attributes
	 */
	public Map<Path, List<String>> mapAllAttributesToFiles(List<Path> files) {
		Map<Path, List<String>> allAttributesToFilesMap = new HashMap<Path, List<String>>();

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

	/**
	 * Finds all of the common attributes contained inside the lists of a map
	 * with the file paths mapped to their attributes
	 * 
	 * @param map
	 *            the map of the file paths mapped to their attributes
	 * @return the common attributes
	 */
	public List<String> findCommonAttributesInMap(Map<Path, List<String>> map) {
		Map<Path, List<String>> attributeSuffixesToFilesMap = createAttributeSuffixesToFilesMap(map);
		Map.Entry<Path, List<String>> entry = attributeSuffixesToFilesMap.entrySet().iterator().next();
		ArrayList<String> commonValues = new ArrayList<String>(entry.getValue());

		for (List<String> list : attributeSuffixesToFilesMap.values()) {
			commonValues.retainAll(list);
		}

		return commonValues;
	}

	/**
	 * Maps the file paths to the attribute suffixes. An attribute suffix is the
	 * part of an attribute before the last period or the whole attribute if
	 * there is no period.
	 * 
	 * @param map
	 *            the map of the file paths mapped to their attributes
	 * @return a map of the file paths mapped to the attribute suffixes
	 */
	private Map<Path, List<String>> createAttributeSuffixesToFilesMap(Map<Path, List<String>> map) {
		List<String> attributeSuffixes;
		Map<Path, List<String>> attributeSuffixesToFilesMap = new HashMap<Path, List<String>>();

		for (Map.Entry<Path, List<String>> entry : map.entrySet()) {
			attributeSuffixes = new ArrayList<String>();

			for (String attribute : entry.getValue()) {
				attributeSuffixes.add(attribute.substring(attribute.lastIndexOf(".") + 1));
			}
			attributeSuffixesToFilesMap.put(entry.getKey(), attributeSuffixes);
		}

		return attributeSuffixesToFilesMap;
	}

	/**
	 * Creates the fully preprocessed file
	 * 
	 * @param wantedAttributesToFilesMap
	 *            the map of file paths to their wanted attributes
	 * @param allAttributesToFilesMap
	 *            the map of file paths to all their attributes
	 * @param groupByAttribute
	 *            the group by attribute
	 * @return the preprocessed file
	 */
	public File createPreprocessedFile(Map<Path, List<String>> wantedAttributesToFilesMap,
			Map<Path, List<String>> allAttributesToFilesMap, String groupByAttribute) {
		removeAttributeFromMap(wantedAttributesToFilesMap, groupByAttribute);

		Map<Path, AttributeLocation> attributeLocationsToFilesMap = mapAttributeLocationsToFiles(
				wantedAttributesToFilesMap, allAttributesToFilesMap, groupByAttribute);

		Map<String, List<Attribute>> userAttributesMap = mapGroupedByAttributes(wantedAttributesToFilesMap,
				allAttributesToFilesMap, attributeLocationsToFilesMap);

		File preprocessedFile = buildFile(wantedAttributesToFilesMap, userAttributesMap, groupByAttribute);

		return preprocessedFile;
	}

	/**
	 * Removes a given attribute from a map containing file paths mapped to its
	 * list of attributes
	 * 
	 * @param map
	 *            the map to remove the attribute from
	 * @param attribute
	 *            the attribute to remove from the map
	 */
	private void removeAttributeFromMap(Map<Path, List<String>> map, String attribute) {
		for (Iterator<Map.Entry<Path, List<String>>> mapIterator = map.entrySet().iterator(); mapIterator.hasNext();) {
			Map.Entry<Path, List<String>> entry = mapIterator.next();

			for (Iterator<String> entryIterator = entry.getValue().iterator(); entryIterator.hasNext();) {
				String attr = entryIterator.next();
				if (attr.contains(attribute)) {
					entryIterator.remove();
				}
			}
		}
	}

	/**
	 * Maps the file paths to the location of the wanted attributes with the
	 * file
	 * 
	 * @param wantedAttributesToFilesMap
	 *            the files mapped to the wanted attributes
	 * @param allAttributesToFilesMap
	 *            the files mapped to all of the attributes
	 * @param groupByAttribute
	 *            the group by attribute
	 * @return the map of the file paths to the location of the wanted
	 *         attributes
	 */
	private Map<Path, AttributeLocation> mapAttributeLocationsToFiles(
			Map<Path, List<String>> wantedAttributesToFilesMap, Map<Path, List<String>> allAttributesToFilesMap,
			String groupByAttribute) {
		Map<Path, AttributeLocation> attributeLocationsToFilesMap = new HashMap<Path, AttributeLocation>();
		Scanner fileReader;

		for (Path filePath : wantedAttributesToFilesMap.keySet()) {
			try {
				// Read first line of file
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
				throw new IllegalArgumentException("Error: " + e.getMessage(), e);
			}
		}

		return attributeLocationsToFilesMap;
	}

	public Map<String, List<Attribute>> mapGroupedByAttributes(Map<Path, List<String>> wantedAttributesToFilesMap,
			Map<Path, List<String>> allAttributesToFilesMap,
			Map<Path, AttributeLocation> attributeLocationsToFilesMap) {
		Map<String, List<Attribute>> userAttributesMap = new HashMap<String, List<Attribute>>();
		Scanner fileReader;

		for (Path filePath : wantedAttributesToFilesMap.keySet()) {
			try {
				fileReader = new Scanner(filePath.toFile());
				fileReader.nextLine(); // Skip first line

				// Loop through all lines of file
				while (fileReader.hasNextLine()) {

					// Remove ,'s between double quotes
					String readLine = fileReader.nextLine();
					// readLine = readLine.replace(",,", ",null,");

					if (readLine.substring(readLine.length() - 1).equals(",")) {
						readLine += "null";
					}

					String[] line = readLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

					String key = line[attributeLocationsToFilesMap.get(filePath).getGroupByIndex()];

					// Add new group by key if map doesn't have it
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

	private File buildFile(Map<Path, List<String>> wantedAttributesToFilesMap,
			Map<String, List<Attribute>> userAttributesMap, String groupByAttribute) {
		List<String> attributeTitles = new ArrayList<String>();
		attributeTitles.add(groupByAttribute);

		for (List<String> attributes : wantedAttributesToFilesMap.values()) {
			for (String attribute : attributes) {
				attributeTitles.add(attribute);
			}
		}

		String fileHeader = String.join(",", attributeTitles) + "\n";

		try {
			File preprocessedFile = File.createTempFile("preprocessedFile", ".csv");
			preprocessedFile.deleteOnExit();

			FileWriter fileWriter = new FileWriter(preprocessedFile);

			fileWriter.append(fileHeader);

			for (Map.Entry<String, List<Attribute>> entry : userAttributesMap.entrySet()) {
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
				line = line.replace("'", "").replace(" ", "_").replace(",_", "_");

				fileWriter.append(line);
			}

			fileWriter.flush();
			fileWriter.close();

			return preprocessedFile;
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

}