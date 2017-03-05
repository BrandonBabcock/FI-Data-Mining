package application.DataMining;

import application.GUI.PreProcessingScene;

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


    HashMap<String, ArrayList<String>> allFileAttributesMap = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> wantedFileAttributesMap = new HashMap<String, ArrayList<String>>();
	HashMap<String, AttributeLocation> attributeLocationMap = new HashMap<String, AttributeLocation>();
	HashMap<String, ArrayList<Attribute>> userAttributesMap = new HashMap<String, ArrayList<Attribute>>();

	String groupByAttribute;

	File dataFile;
	File[] dataFiles;

    public Preprocessor(){}

    // used for single file
    public Preprocessor(File file){
		this.dataFile = file;
	}

	// used for multiple files
    public Preprocessor(File[] file){
        this.dataFiles = file;
    }


    public void getFileAttributes(){
        Scanner fileReader;
        try {
            // Read first line in from file and store each element
            // separated by a comma in an array
            fileReader = new Scanner(dataFile);
            String[] firstLine = fileReader.nextLine().split(",");
            // Map file to its attribute titles
            allFileAttributesMap.put(dataFile.getAbsolutePath(), new ArrayList<String>(Arrays.asList(firstLine)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

	public void getUsefulAttributesFromUserFiles() {
		boolean done = false;
		Scanner userInput = new Scanner(System.in);
		String input = "";
		Scanner fileReader;

        try {
            // Read first line in from file and store each element
            // separated by a comma in an array
            fileReader = new Scanner(dataFile);
            String[] firstLine = fileReader.nextLine().split(",");

            // Map file to its attribute titles
            allFileAttributesMap.put(dataFile.getAbsolutePath(), new ArrayList<String>(Arrays.asList(firstLine)));

            // Show user the file's attributes
            System.out.print("The attributes for this file are: ");
            for (String attribute : firstLine) {
                System.out.print(attribute + ", ");
            }
            System.out.println();

            // Let user select important attributes and map them to the
            // file in wantedFileAttributesMap
            System.out
                    .print("Enter the important attributes for this file (seperated by comma and no space): ");
            wantedFileAttributesMap.put(input,
                    new ArrayList<String>(Arrays.asList(userInput.nextLine().split(","))));
        } catch (FileNotFoundException e) {
            System.out.println("File " + input + " not found.");
            e.printStackTrace();
        }
    }


	public void getGroupByAttribute() {
		Scanner userInput = new Scanner(System.in);
		System.out.print("Enter attribute suffix (what's after final period in attribute) to group files by: ");
		groupByAttribute = userInput.nextLine();
		userInput.close();
	}

	public void removeGroupByAttributeFromWantedMap() {
		for (Iterator<Map.Entry<String, ArrayList<String>>> iterator = wantedFileAttributesMap.entrySet()
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
		for (String filePath : wantedFileAttributesMap.keySet()) {
			// Read first line of file
			try {
				fileReader = new Scanner(new File(filePath));
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
				for (String wantedAttr : wantedFileAttributesMap.get(filePath)) {
					attributeLocation.addAttributeIndex(allFileAttributesMap.get(filePath).indexOf(wantedAttr));
				}

				attributeLocationMap.put(filePath, attributeLocation);
			} catch (FileNotFoundException e) {
				System.out.println("File " + filePath + " not found.");
				e.printStackTrace();
			}
		}
	}

	public void mapUserAttributes() {
		Scanner fileReader;
		for (String filePath : wantedFileAttributesMap.keySet()) {
			try {
				fileReader = new Scanner(new File(filePath));
				fileReader.nextLine(); // Skip first line

				// Loop through all lines of file
				while (fileReader.hasNextLine()) {
					String[] line = fileReader.nextLine().split(",");
					String key = line[attributeLocationMap.get(filePath).getGroupByIndex()];

					// Add new user key if map doesn't have it
					if (!userAttributesMap.containsKey(key)) {
						userAttributesMap.put(key, new ArrayList<Attribute>());
					}

					// Add all wanted attributes to the user key's ArrayList
					for (Integer num : attributeLocationMap.get(filePath).getAttributeIndexes()) {
						String attributeTitle = allFileAttributesMap.get(filePath).get(num);
						Attribute attribute = new Attribute(attributeTitle, line[num]);
						userAttributesMap.get(key).add(attribute);
					}
				}

				fileReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("File " + filePath + " not found.");
				e.printStackTrace();
			}
		}
	}

	public void createPreprocessedFile() {
		ArrayList<String> attributeTitles = new ArrayList<String>();
		attributeTitles.add(groupByAttribute);

		for (ArrayList<String> attributes : wantedFileAttributesMap.values()) {
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

    public boolean chechForPipes(){
	    return false;

    }
    
    public HashMap<String, ArrayList<String>> getAllFileAttributesMap() {
        return allFileAttributesMap;
    }

}
