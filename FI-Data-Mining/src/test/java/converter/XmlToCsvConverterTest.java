package converter;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

public class XmlToCsvConverterTest {

	@Test
	public void should_convert_xml_file_from_relational_database_to_csv_file() throws FileNotFoundException {
		File convertedFile = new XmlToCsvConverter().convertToCsv(new File("Data/bio.xml"));

		assertThat(convertedFile.getName().substring(convertedFile.getName().length() - 4), equalTo(".csv"));

		Scanner scan = new Scanner(convertedFile);
		String[] attributes = scan.nextLine().split(",");

		while (scan.hasNext()) {
			String[] line = scan.nextLine().split(",");
			assertThat(line.length, equalTo(attributes.length));
		}

		scan.close();
	}

	@Test
	public void should_convert_standard_xml_file_to_csv_file() throws FileNotFoundException {
		File convertedFile = new XmlToCsvConverter().convertToCsv(new File("Data/TestXmlOne.xml"));

		assertThat(convertedFile.getName().substring(convertedFile.getName().length() - 4), equalTo(".csv"));

		Scanner scan = new Scanner(convertedFile);
		String[] attributes = scan.nextLine().split(",");
		assertTrue(attributes.length != 0);

		while (scan.hasNext()) {
			String[] line = scan.nextLine().split(",");
			assertThat(line.length, equalTo(attributes.length));
		}

		scan.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_for_invalid_file() {
		new XmlToCsvConverter().convertToCsv(new File("Data/TestCsvOne.csv"));
	}
}
