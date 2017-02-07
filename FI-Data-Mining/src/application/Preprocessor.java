package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class Preprocessor {

	public static void main(String[] args) {
		// Declare and Initialize variables
		HashMap<String, ArrayList<String>> wantedFileAttributesMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> allFileAttributesMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> userAttributesMap = new HashMap<String, ArrayList<String>>();

		Scanner userInput = new Scanner(System.in);
		String input = "";
		boolean done = false;
		Scanner fileReader;

		try {
			// Create attribute maps from user input
			while (!done) {
				// Let user select a file until they have no more to select
				System.out.print("Enter file path (leave blank is no more files): ");
				input = userInput.nextLine();

				if (input.isEmpty()) {
					done = true;
				} else {
					fileReader = new Scanner(new File(input));
					String[] firstLine = fileReader.nextLine().split(",");

					// Mapped file to its attribute titles
					allFileAttributesMap.put(input, new ArrayList<String>(Arrays.asList(firstLine)));

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
				}
			}

			// Get the groupBy attribute
			System.out.print("Enter attribute suffix (what's after final period in attribute) to group files by: ");
			String groupBy = userInput.nextLine();

			for (String filePath : wantedFileAttributesMap.keySet()) {
				// Remove groupBy attribute from wantedAttributes map
				for (String attr : wantedFileAttributesMap.get(filePath)) {
					if (attr.contains(groupBy)) {
						wantedFileAttributesMap.get(filePath).remove(attr);
					}
				}

				// Read first line of file
				fileReader = new Scanner(new File(filePath));
				String[] firstLine = fileReader.nextLine().split(",");

				// Create new AttributeLocation object for file
				AttributeLocation attributeLocation = new AttributeLocation();

				// Get the index of the attribute title to group by and store in
				// AttributeLocation field
				for (int i = 0; i < firstLine.length; i++) {
					if (firstLine[i].contains(groupBy)) {
						attributeLocation.setGroupByIndex(i);
					}
				}

				// Get the indexes of the wanted attribute titles and store in
				// AttributeLocation field
				for (String wantedAttr : wantedFileAttributesMap.get(filePath)) {
					attributeLocation.addAttributeIndex(allFileAttributesMap.get(filePath).indexOf(wantedAttr));
				}

				// Loop through all lines of file
				while (fileReader.hasNextLine()) {
					String[] line = fileReader.nextLine().split(",");
					String key = line[attributeLocation.getGroupByIndex()];

					// Add new user key if map doesn't have it
					if (!userAttributesMap.containsKey(key)) {
						userAttributesMap.put(key, new ArrayList<String>());
					}

					// Add all wanted attributes to the user key's ArrayList
					for (Integer num : attributeLocation.getAttributeIndexes()) {
						userAttributesMap.get(key).add(line[num]);
					}
				}

				fileReader.close();
			}

			// Print out the keys and values of the userAttributesMap
			Iterator<Entry<String, ArrayList<String>>> iterator = userAttributesMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry pair = (Map.Entry) iterator.next();
				System.out.println("Key is: " + pair.getKey());
				System.out.print("Values are: ");
				for (String str : (ArrayList<String>) pair.getValue()) {
					System.out.print(str + ", ");
				}
				System.out.println();
				iterator.remove();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
	}

}
