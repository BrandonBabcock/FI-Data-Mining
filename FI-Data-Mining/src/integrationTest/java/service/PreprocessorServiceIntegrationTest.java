package service;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import converter.XmlToCsvConverter;

public class PreprocessorServiceIntegrationTest {

	private PreprocessorService preprocessor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		preprocessor = new PreprocessorService();
	}

	@After
	public void tearDown() {
		preprocessor = null;
	}

	@Test
	public void should_convert_xml_file_to_csv_file() {
		List<Path> files = new ArrayList<Path>();
		files.add(Paths.get("Data/TestXmlOne.xml"));

		List<Path> convertedFiles = preprocessor.convertXmlToCsv(files, new XmlToCsvConverter());

		assertThat(convertedFiles.get(0).getFileName().toString(), endsWith(".csv"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_not_map_attributes_with_invalid_file() {
		List<Path> files = new ArrayList<Path>();
		files.add(Paths.get("InvalidFile.csv"));
		preprocessor.mapAllAttributesToFiles(files);
	}

}