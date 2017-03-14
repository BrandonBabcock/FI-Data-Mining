package util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by shawn on 3/11/2017.
 */
public class XmlToCsvConverterTest {

	private XmlToCsvConverter testConvert;
	private File xmlFile;

	@Before
	public void setup() {
		xmlFile = new File("Data/Bio.xml");
		testConvert = new XmlToCsvConverter(xmlFile);
	}

	@After
	public void teardown() {
		testConvert = null;
	}

	@Test
	public void convertToCsvTest() throws IOException, SAXException {
		File convertedFile = testConvert.convertToCsv();

		// check file name
		assertThat(convertedFile.getName(),
				equalTo(convertedFile.getName().substring(0, convertedFile.getName().indexOf(".")) + ".csv"));

		Scanner scan = new Scanner(convertedFile);

		String[] attributes = scan.nextLine().split(",");

		while (scan.hasNext()) {
			String[] line = scan.nextLine().split(",");
			assertThat(line.length, equalTo(attributes.length));
		}

		File xmlFile2 = new File("Data/TestXmlOne.xml");

		testConvert = new XmlToCsvConverter(xmlFile2);
		convertedFile = testConvert.convertToCsv();

		assertThat(convertedFile.getName(),
				equalTo(convertedFile.getName().substring(0, convertedFile.getName().indexOf(".")) + ".csv"));

		scan = new Scanner(convertedFile);

		attributes = scan.nextLine().split(",");

		assertTrue(attributes.length != 0);

		while (scan.hasNext()) {
			String[] line = scan.nextLine().split(",");
			assertThat(line.length, equalTo(attributes.length));
		}
	}

	// SAXException | IOException e

	@Test(expected = IllegalArgumentException.class)
	public void IOExceptionTest() {
		testConvert = new XmlToCsvConverter(new File("Data/fakeFile.xml"));
		testConvert.convertToCsv();
	}
}
