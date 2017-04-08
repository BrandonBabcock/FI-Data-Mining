package service;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class PreprocessingServiceIntegrationTest {

	private PreprocessingService preprocessor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		preprocessor = new PreprocessingService();
	}

	@After
	public void tearDown() {
		preprocessor = null;
	}

	@Test
	public void should_convert_xml_file_to_csv_file() {
		ArrayList<Path> files = new ArrayList<Path>();
		files.add(Paths.get("Data/TestXmlOne.xml"));

		ArrayList<Path> convertedFiles = preprocessor.convertXmlToCsv(files);

		assertThat(convertedFiles.get(0).getFileName().toString(), endsWith(".csv"));
	}

}
