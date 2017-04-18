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

	private CsvToArffConverter() {

	}

	/**
	 * Converts a CSV file to an ARFF file
	 * 
	 * @return the ARFF file
	 */
	public static File convertToArff(File csvFile) {
		try {
			// Load CSV file
			CSVLoader loader = new CSVLoader();
			loader.setSource(csvFile);
			Instances data = loader.getDataSet();

			String filename = csvFile.getName().substring(0, csvFile.getName().lastIndexOf("."));

			// Create ARFF file
			File arffFile = File.createTempFile(filename, ".arff");
			arffFile.deleteOnExit();

			// Write to ARFF file
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