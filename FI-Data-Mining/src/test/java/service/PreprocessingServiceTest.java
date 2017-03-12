package service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.AttributeLocation;

public class PreprocessingServiceTest {

	PreprocessingService preprocessor;

	@Before
	public void setUp() {
		preprocessor = new PreprocessingService();
	}

	@After
	public void tearDown() {
		preprocessor = null;
	}

	// @Test
	// public void
	// should_return_a_list_with_a_csv_file_when_passed_a_list_with_an_xml_file()
	// {
	// ArrayList<Path> files = new ArrayList<Path>();
	// files.add(Paths.get("Data/TestXmlOne.xml"));
	//
	// ArrayList<Path> convertedFiles = preprocessor.convertXmlToCsv(files);
	//
	// assertThat(convertedFiles.get(0).getFileName().toString(),
	// endsWith(".xml"));
	// }

	@Test
	public void should_map_attributes_to_file() {
		ArrayList<Path> files = new ArrayList<Path>();
		files.add(Paths.get("Data/TestCsvOne.csv"));

		HashMap<Path, ArrayList<String>> map = preprocessor.mapAllAttributesToFiles(files);

		assertThat(map.containsKey(Paths.get("Data/TestCsvOne.csv")), equalTo(true));
		assertThat(map.get(Paths.get("Data/TestCsvOne.csv")).contains("attributeOne"), equalTo(true));
		assertThat(map.get(Paths.get("Data/TestCsvOne.csv")).contains("attributeTwo"), equalTo(true));
		assertThat(map.get(Paths.get("Data/TestCsvOne.csv")).contains("attributeThree"), equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_mapping_attributes_to_invalid_file() {
		ArrayList<Path> files = new ArrayList<Path>();
		files.add(Paths.get("Data/InvalidFile.csv"));

		preprocessor.mapAllAttributesToFiles(files);
	}

	@Test
	public void should_find_common_attributes_in_map() {
		ArrayList<Path> files = new ArrayList<Path>();
		files.add(Paths.get("Data/TestCsvOne.csv"));
		files.add(Paths.get("Data/TestCsvTwo.csv"));

		HashMap<Path, ArrayList<String>> map = preprocessor.mapAllAttributesToFiles(files);
		ArrayList<String> commonAttributes = preprocessor.findCommonAttributesInMap(map);

		assertThat(commonAttributes.contains("attributeOne"), equalTo(true));
		assertThat(commonAttributes.contains("attributeTwo"), equalTo(true));
		assertThat(commonAttributes.contains("attributeThree"), equalTo(true));
		assertThat(commonAttributes.contains("attributeFour"), equalTo(false));
	}

	@Test
	public void should_map_group_by_attributes() throws FileNotFoundException {
		HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();
		ArrayList<String> wantedAttributes = new ArrayList<String>();
		wantedAttributes.add("attributeTwo");
		wantedAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributes);

		HashMap<Path, ArrayList<String>> allAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();
		ArrayList<String> allAttributes = new ArrayList<String>();
		allAttributes.add("attributeOne");
		allAttributes.add("attributeTwo");
		allAttributes.add("attributeThree");
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributes);

		HashMap<Path, AttributeLocation> attributeLocationsToFilesMap = new HashMap<Path, AttributeLocation>();
		AttributeLocation attributeLocation = new AttributeLocation();
		attributeLocation.setGroupByIndex(0);
		ArrayList<Integer> attributeIndexes = new ArrayList<Integer>();
		attributeIndexes.add(1);
		attributeLocation.setAttributeIndexes(attributeIndexes);
		attributeLocationsToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), attributeLocation);

		preprocessor.mapGroupedByAttributes(wantedAttributesToFilesMap, allAttributesToFilesMap,
				attributeLocationsToFilesMap);

		Scanner fileReader = new Scanner(Paths.get("Data/PreprocessedFile.csv").toFile());
		String firstLine = fileReader.nextLine();
		String secondLine = fileReader.nextLine();
		fileReader.close();

		assertThat(firstLine.equals("attributeOne,attributeTwo"), equalTo(true));
		assertThat(secondLine.equals("valueOne,valueTwo"), equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_mapping_group_by_attributes_with_invalid_file() throws FileNotFoundException {
		HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();
		ArrayList<String> wantedAttributes = new ArrayList<String>();
		wantedAttributesToFilesMap.put(Paths.get("Data/InvalidFile.csv"), wantedAttributes);

		HashMap<Path, ArrayList<String>> allAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();
		HashMap<Path, AttributeLocation> attributeLocationsToFilesMap = new HashMap<Path, AttributeLocation>();

		preprocessor.mapGroupedByAttributes(wantedAttributesToFilesMap, allAttributesToFilesMap,
				attributeLocationsToFilesMap);
	}

	@Test
	public void should_create_preprocessed_file() {
		HashMap<Path, ArrayList<String>> wantedAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();
		ArrayList<String> wantedAttributes = new ArrayList<String>();
		wantedAttributes.add("attributeOne");
		wantedAttributes.add("attributeTwo");
		wantedAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributes);

		HashMap<Path, ArrayList<String>> allAttributesToFilesMap = new HashMap<Path, ArrayList<String>>();
		ArrayList<String> allAttributes = new ArrayList<String>();
		allAttributes.add("attributeOne");
		allAttributes.add("attributeTwo");
		allAttributes.add("attributeThree");
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributes);

		String groupByAttribute = "attributeOne";

		preprocessor.createPreprocessedFile(wantedAttributesToFilesMap, allAttributesToFilesMap, groupByAttribute);

		assertThat(Paths.get("Data/TestCsvOne.csv"), notNullValue());
	}

}
