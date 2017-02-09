package application;

public class Main {

	public static void main(String[] args) {
		Preprocessor preprocessor = new Preprocessor();

		/*
		 * Call getUsefulAttributesFromUserFiles and getGroupByAttribute to
		 * receive user input for the files If you don't want to receive user
		 * input, comment those calls out and call useTestingFiles to use the
		 * Data/GroupsByUser and Data/AlumRoles files for testing with the
		 * groupByAttribute as BANNER_ID
		 */
		// preprocessor.getUsefulAttributesFromUserFiles();
		// preprocessor.getGroupByAttribute();
		preprocessor.useTestingFiles();

		preprocessor.removeGroupByAttributeFromWantedMap();
		preprocessor.mapAttributeLocations();
		preprocessor.mapUserAttributes();
		preprocessor.printUserAttributesMap();
	}

}
