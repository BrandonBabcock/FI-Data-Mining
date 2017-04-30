package converter;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CsvToArffConverterTest {

	private File csvFile;
	private int numAttributes;
	private int numRecords;
	private CsvToArffConverter csvToArffConverter;

	@Before
	public void setUp() throws FileNotFoundException {
		csvToArffConverter = new CsvToArffConverter();
		csvFile = new File("Data/csvToArffTestFile.csv");
		numRecords = 0;

		Scanner scan = new Scanner(csvFile);
		String[] attributes = scan.nextLine().split(",");
		numAttributes = attributes.length;

		while (scan.hasNext()) {
			numRecords++;
			scan.nextLine();
		}

		scan.close();
	}

	@After
	public void tearDown() {
		csvFile = null;
		numAttributes = 0;
		numRecords = 0;
		csvToArffConverter = null;
	}

	@Test
	public void should_convert_csv_file_to_arff_file() throws IOException {
		File arffFile = csvToArffConverter.convertToArff(csvFile);
		String fileName = arffFile.getName();
		assertThat(fileName.substring(fileName.length() - 5), equalTo(".arff"));

		Scanner scan = new Scanner(arffFile);
		String fileheader = scan.nextLine();
		String relationName = csvFile.getName().substring(0, csvFile.getName().indexOf("."));
		assertThat(fileheader, equalTo("@relation " + relationName));

		int attributeCount = 0;
		while (scan.hasNext()) {
			String line = scan.nextLine();
			if (line.contains("@attribute")) {
				attributeCount++;
			}
			if (line.contains("@data")) {
				break;
			}
		}
		assertThat(attributeCount, equalTo(numAttributes));

		int lineCount = 0;
		while (scan.hasNext()) {
			lineCount++;
			scan.nextLine();
		}
		assertThat(lineCount, equalTo(numRecords));
		scan.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_illegal_argument_exception_for_invalid_file() {
		csvToArffConverter.convertToArff(new File("Data/TestXmlOne.xml"));
	}
}
