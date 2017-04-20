package service;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import data.Attribute;
import data.AttributeLocation;
import util.XmlToCsvConverter;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PreprocessorService.class, XmlToCsvConverter.class })
public class PreprocessingServiceTest {

	private PreprocessorService preprocessor;

	@Before
	public void setUp() {
		preprocessor = new PreprocessorService();
	}

	@After
	public void tearDown() {
		preprocessor = null;
	}

	@Test
	public void should_return_a_list_with_a_csv_file_when_passed_a_list_with_an_xml_file() throws Exception {
		mockStatic(XmlToCsvConverter.class);
		expect(XmlToCsvConverter.convertToCsv(Paths.get("Data/TestXmlOne.xml").toFile()))
				.andReturn(Paths.get("Data/TestCsvOne.csv").toFile());
		replay(XmlToCsvConverter.class);

		ArrayList<Path> files = new ArrayList<Path>();
		files.add(Paths.get("Data/TestXmlOne.xml"));

		List<Path> convertedFiles = preprocessor.convertXmlToCsv(files);

		verify(XmlToCsvConverter.class);
		assertThat(convertedFiles.get(0).getFileName().toString(), endsWith(".csv"));
	}

	@Test
	public void should_map_attributes_to_file() {
		ArrayList<Path> files = new ArrayList<Path>();
		files.add(Paths.get("Data/TestCsvOne.csv"));

		Map<Path, List<String>> map = preprocessor.mapAllAttributesToFiles(files);

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

		Map<Path, List<String>> map = preprocessor.mapAllAttributesToFiles(files);
		List<String> commonAttributes = preprocessor.findCommonAttributesInMap(map);

		assertThat(commonAttributes.contains("attributeOne"), equalTo(true));
		assertThat(commonAttributes.contains("attributeTwo"), equalTo(true));
		assertThat(commonAttributes.contains("attributeThree"), equalTo(true));
		assertThat(commonAttributes.contains("attributeFour"), equalTo(false));
	}

	@Test
	public void should_map_group_by_attributes() throws FileNotFoundException {
		Map<Path, List<String>> wantedAttributesToFilesMap = new HashMap<Path, List<String>>();
		List<String> wantedAttributes = new ArrayList<String>();
		wantedAttributes.add("attributeTwo");
		wantedAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributes);

		Map<Path, List<String>> allAttributesToFilesMap = new HashMap<Path, List<String>>();
		ArrayList<String> allAttributes = new ArrayList<String>();
		allAttributes.add("attributeOne");
		allAttributes.add("attributeTwo");
		allAttributes.add("attributeThree");
		allAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), allAttributes);

		HashMap<Path, AttributeLocation> attributeLocationsToFilesMap = new HashMap<Path, AttributeLocation>();
		AttributeLocation attributeLocation = new AttributeLocation();
		attributeLocation.setGroupByIndex(0);

		attributeLocation.addAttributeIndex(1);
		attributeLocationsToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), attributeLocation);

		Map<String, List<Attribute>> map = preprocessor.mapGroupedByAttributes(wantedAttributesToFilesMap,
				allAttributesToFilesMap, attributeLocationsToFilesMap);

		List<Attribute> values = map.get("valueOne");
		Attribute firstAttr = values.get(0);
		Attribute secondAttr = values.get(1);

		assertThat(map.containsKey("valueOne"), equalTo(true));
		assertThat(firstAttr.getValue(), equalTo("valueTwo"));
		assertThat(secondAttr.getValue(), equalTo("valueTwoDifferent"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_mapping_group_by_attributes_with_invalid_file() throws FileNotFoundException {
		Map<Path, List<String>> wantedAttributesToFilesMap = new HashMap<Path, List<String>>();
		List<String> wantedAttributes = new ArrayList<String>();
		wantedAttributesToFilesMap.put(Paths.get("Data/InvalidFile.csv"), wantedAttributes);

		Map<Path, List<String>> allAttributesToFilesMap = new HashMap<Path, List<String>>();
		Map<Path, AttributeLocation> attributeLocationsToFilesMap = new HashMap<Path, AttributeLocation>();

		preprocessor.mapGroupedByAttributes(wantedAttributesToFilesMap, allAttributesToFilesMap,
				attributeLocationsToFilesMap);
	}

	@Test
	public void should_create_preprocessed_file() {
		Map<Path, List<String>> wantedAttributesToFilesMap = new HashMap<Path, List<String>>();
		List<String> wantedAttributes = new ArrayList<String>();
		wantedAttributes.add("attributeOne");
		wantedAttributes.add("attributeTwo");
		wantedAttributesToFilesMap.put(Paths.get("Data/TestCsvOne.csv"), wantedAttributes);

		Map<Path, List<String>> allAttributesToFilesMap = new HashMap<Path, List<String>>();
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
