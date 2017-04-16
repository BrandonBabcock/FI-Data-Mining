package util;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 * Converter to convert a CSV file to an ARFF file
 */
public class CsvToArffConverter {

	/* The CSV file to convert */
	private File csvFile;

	/**
	 * Creates a new instance of the CsvToArffConverter
	 * 
	 * @param file
	 *            the file to convert
	 */
	public CsvToArffConverter(File file) {
		this.csvFile = file;
	}

	/**
	 * Converts a CSV file to an ARFF file
	 * 
	 * @return the ARFF file
	 */
	public File convertToArff() {
		// Load CSV
		CSVLoader loader = new CSVLoader();
		try {
			loader.setSource(csvFile);
			Instances data = loader.getDataSet();

			String filename = csvFile.getName().substring(0, csvFile.getName().indexOf("."));

			// Save ARFF
			File arffFile = File.createTempFile(filename, ".arff");
			arffFile.deleteOnExit();
			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);
			saver.setFile(arffFile);
			saver.writeBatch();
			return arffFile;
		} catch (IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

}
