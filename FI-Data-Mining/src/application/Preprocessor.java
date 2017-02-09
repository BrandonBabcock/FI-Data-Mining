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

	HashMap<String, ArrayList<String>> allFileAttributesMap = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> wantedFileAttributesMap = new HashMap<String, ArrayList<String>>();
	HashMap<String, AttributeLocation> attributeLocationMap = new HashMap<String, AttributeLocation>();
	HashMap<String, ArrayList<Attribute>> userAttributesMap = new HashMap<String, ArrayList<Attribute>>();

	String groupByAttribute;

	public void getUsefulAttributesFromUserFiles() {
		boolean done = false;
		Scanner userInput = new Scanner(System.in);
		String input = "";
		Scanner fileReader;

		// Let the user select a file until they have no more to select
		while (!done) {
			// Get file from user
			System.out.print("Enter file path (leave blank if no more files): ");
			input = userInput.nextLine();

			if (input.isEmpty()) {
				// No more files
				done = true;
			} else {
				try {
					// Read first line in from file and store each element
					// separated by a comma in an array
					fileReader = new Scanner(new File(input));
					String[] firstLine = fileReader.nextLine().split(",");

					// Map file to its attribute titles
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
				} catch (FileNotFoundException e) {
					System.out.println("File " + input + " not found.");
					e.printStackTrace();
				}
			}
		}
		userInput.close();
	}

	public void getGroupByAttribute() {
		Scanner userInput = new Scanner(System.in);
		System.out.print("Enter attribute suffix (what's after final period in attribute) to group files by: ");
		groupByAttribute = userInput.nextLine();
		userInput.close();
	}

	public void removeGroupByAttributeFromWantedMap() {
		for (String filePath : wantedFileAttributesMap.keySet()) {
			// Remove groupBy attribute from wantedAttributes map
			for (String attr : wantedFileAttributesMap.get(filePath)) {
				if (attr.contains(groupByAttribute)) {
					wantedFileAttributesMap.get(filePath).remove(attr);
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

	// Method used for testing
	public void printUserAttributesMap() {
		// Print out the keys and values of the userAttributesMap
		Iterator<Entry<String, ArrayList<Attribute>>> iterator = userAttributesMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry pair = (Map.Entry) iterator.next();
			System.out.println("Key is: " + pair.getKey());
			System.out.print("Values are: ");
			for (Attribute attr : (ArrayList<Attribute>) pair.getValue()) {
				System.out.print(attr.getValue() + ", ");
			}
			System.out.println();
			iterator.remove();
		}
	}

	public void useTestingFiles() {
		ArrayList<String> allAttributesInGroupsByUser = new ArrayList<String>();
		allAttributesInGroupsByUser.add("_changetype");
		allAttributesInGroupsByUser.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.LAST_NAME_NON_CUSTOMER1");
		allAttributesInGroupsByUser.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.USER_EMAIL");
		allAttributesInGroupsByUser.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.FIRST_NAME_NON_CUSTOMER1");
		allAttributesInGroupsByUser.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.BANNER_ID");
		allAttributesInGroupsByUser.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.GROUP_EMAIL");
		allAttributesInGroupsByUser.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.GROUP_ID");
		allFileAttributesMap.put("Data/GroupsByUser.csv", allAttributesInGroupsByUser);

		ArrayList<String> allAttributesInAlumRoles = new ArrayList<String>();
		allAttributesInAlumRoles.add("_changetype");
		allAttributesInAlumRoles.add("_CUSTOMER1_FISCHER_ROLES_ALUM_VIEW.BANNER_ID");
		allAttributesInAlumRoles.add("_CUSTOMER1_FISCHER_ROLES_ALUM_VIEW.ROLE");
		allFileAttributesMap.put("Data/AlumRoles.csv", allAttributesInAlumRoles);

		ArrayList<String> wantedAttributesInGroupsByUser = new ArrayList<String>();
		wantedAttributesInGroupsByUser.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.BANNER_ID");
		wantedAttributesInGroupsByUser.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.GROUP_EMAIL");
		wantedFileAttributesMap.put("Data/GroupsByUser.csv", wantedAttributesInGroupsByUser);

		ArrayList<String> wantedAttributesInAlumRoles = new ArrayList<String>();
		wantedAttributesInAlumRoles.add("_CUSTOMER1_FISCHER_ROLES_ALUM_VIEW.BANNER_ID");
		wantedAttributesInAlumRoles.add("_CUSTOMER1_FISCHER_ROLES_ALUM_VIEW.ROLE");
		wantedFileAttributesMap.put("Data/AlumRoles.csv", wantedAttributesInAlumRoles);

		groupByAttribute = "BANNER_ID";
	}

}
