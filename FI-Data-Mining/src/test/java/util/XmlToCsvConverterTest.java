package util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Created by shawn on 3/11/2017.
 */
public class XmlToCsvConverterTest {

	private File xmlFile;

	@Before
	public void setup() {
		xmlFile = new File("Data/Bio.xml");
	}

	@After
	public void teardown() {
		xmlFile = null;
	}

	@Test
	public void convertToCsvTest() throws IOException, SAXException {
		File convertedFile = XmlToCsvConverter.convertToCsv(xmlFile);

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

		convertedFile = XmlToCsvConverter.convertToCsv(xmlFile2);

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

	@Test(expected = IllegalArgumentException.class)
	public void IOExceptionTest() {
		XmlToCsvConverter.convertToCsv(new File("Data/fakeFile.xml"));
	}
}
