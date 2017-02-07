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
		try {
			// Get all attribute titles into an array from first line of file
			Scanner fileReader = new Scanner(new File("Data/GroupsByUser.csv"));
			String[] firstLine = fileReader.nextLine().split(",");

			// Store array of all attribute titles as an ArrayList
			ArrayList<String> allFileAttributes = new ArrayList<String>(Arrays.asList(firstLine));

			// Get wanted attribute titles as a new ArrayList
			ArrayList<String> wantedAttributes = new ArrayList<String>();
			wantedAttributes.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.BANNER_ID");
			wantedAttributes.add("_CUSTOMER1_FISCHER_GROUPS_BY_USER.GROUP_EMAIL");

			// Select attribute title to group by and remove from the
			// wantedAttributes ArrayList
			String groupBy = "_CUSTOMER1_FISCHER_GROUPS_BY_USER.BANNER_ID";
			wantedAttributes.remove(groupBy);

			// Create new AttributeLocation object for file
			AttributeLocation attributeLocation = new AttributeLocation();

			// Get the index of the attribute title to group by and store in
			// AttributeLocation field
			for (int i = 0; i < firstLine.length; i++) {
				if (firstLine[i].equals(groupBy)) {
					attributeLocation.setGroupByIndex(i);
				}
			}

			// Get the indexes of the wanted attribute titles and store in
			// AttributeLocation field
			for (String str : wantedAttributes) {
				attributeLocation.addAttributeIndex(allFileAttributes.indexOf(str));
			}

			HashMap<String, ArrayList<String>> userAttributesMap = new HashMap<String, ArrayList<String>>();

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

			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
	}

}
