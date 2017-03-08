
package application.DataMining;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Preprocessor {

	private HashMap<String, ArrayList<String>> allFileAttributesMap = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> wantedFileAttributesMap = new HashMap<String, ArrayList<String>>();
	private HashMap<String, AttributeLocation> attributeLocationMap = new HashMap<String, AttributeLocation>();
	private HashMap<String, ArrayList<Attribute>> userAttributesMap = new HashMap<String, ArrayList<Attribute>>();
	private File dataFile;
	private String groupByAttribute;

	public Preprocessor(File file){
		this.setDataFile(file);
	}

	public void processFile(){

	}

	public void removeGroupByAttributeFromWantedMap() {
		for (Iterator<Map.Entry<String, ArrayList<String>>> iterator = getWantedFileAttributesMap().entrySet()
				.iterator(); iterator.hasNext();) {
			Map.Entry<String, ArrayList<String>> entry = iterator.next();

			for (Iterator<String> iterator2 = entry.getValue().iterator(); iterator2.hasNext();) {
				String attr = iterator2.next();
				if (attr.contains(groupByAttribute)) {
					iterator2.remove();
				}
			}
		}
	}

	public void mapAttributeLocations() {
		Scanner fileReader;
		for (String filePath : getWantedFileAttributesMap().keySet()) {
			// Read first line of file
			try {
				fileReader = new Scanner(dataFile);
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
				for (String wantedAttr : getWantedFileAttributesMap().get(filePath)) {
					attributeLocation.addAttributeIndex(getAllFileAttributesMap().get(filePath).indexOf(wantedAttr));
				}

				getAttributeLocationMap().put(filePath, attributeLocation);
			} catch (FileNotFoundException e) {
				System.out.println("File " + filePath + " not found.");
				e.printStackTrace();
			}
		}
	}

	public void mapUserAttributes() {
		Scanner fileReader;
		for (String filePath : getWantedFileAttributesMap().keySet()) {
			try {
				fileReader = new Scanner(dataFile);
				fileReader.nextLine(); // Skip first line

				// Loop through all lines of file
				while (fileReader.hasNextLine()) {
					String[] line = fileReader.nextLine().split(",");
					String key = line[getAttributeLocationMap().get(filePath).getGroupByIndex()];

					// Add new user key if map doesn't have it
					if (!getUserAttributesMap().containsKey(key)) {
						getUserAttributesMap().put(key, new ArrayList<Attribute>());
					}

					// Add all wanted attributes to the user key's ArrayList
					for (Integer num : getAttributeLocationMap().get(filePath).getAttributeIndexes()) {
						String attributeTitle = getAllFileAttributesMap().get(filePath).get(num);
						Attribute attribute = new Attribute(attributeTitle, line[num]);
						getUserAttributesMap().get(key).add(attribute);
					}
				}

				fileReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("File " + filePath + " not found.");
				e.printStackTrace();
			}
		}
	}

	// Method used for testing
	public void printUserAttributesMap() {
		// Print out the keys and values of the userAttributesMap
		for (Map.Entry<String, ArrayList<Attribute>> entry : getUserAttributesMap().entrySet()) {
			System.out.println("Key is: " + entry.getKey());
			System.out.print("Values are: ");
			for (Attribute attr : entry.getValue()) {
				System.out.print(attr.getValue());
			}
			System.out.println();
		}
	}

	public void createPreprocessedFile() {
		ArrayList<String> attributeTitles = new ArrayList<String>();
		attributeTitles.add(groupByAttribute);

		for (ArrayList<String> attributes : getWantedFileAttributesMap().values()) {
			for (String attribute : attributes) {
				attributeTitles.add(attribute);
			}
		}

		String fileHeader = String.join(",", attributeTitles) + "\n";

		try {
			FileWriter fileWriter = new FileWriter("Data/PreprocessedFile.csv");

			fileWriter.append(fileHeader);

			for (Map.Entry<String, ArrayList<Attribute>> entry : getUserAttributesMap().entrySet()) {
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

	public HashMap<String, ArrayList<String>> getAllFileAttributesMap() {
		return allFileAttributesMap;
	}

	public HashMap<String, ArrayList<String>> getWantedFileAttributesMap() {
		return wantedFileAttributesMap;
	}

	public void setWantedFileAttributesMap(HashMap<String, ArrayList<String>> wantedFileAttributesMap) {
		this.wantedFileAttributesMap = wantedFileAttributesMap;
	}

	public HashMap<String, AttributeLocation> getAttributeLocationMap() {
		return attributeLocationMap;
	}

	public HashMap<String, ArrayList<Attribute>> getUserAttributesMap() {
		return userAttributesMap;
	}

	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}

	public void setGroupByAttribute(String groupByAttribute) {
		this.groupByAttribute = groupByAttribute;
	}
}