package converter;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 * Converter to convert a CSV file to an ARFF file
 */
public class CsvToArffConverter {

	/**
	 * Converts a CSV file to an ARFF file
	 * 
	 * @param csvFile
	 *            the CSV file to convert
	 * @return the converted file
	 */
	public File convertToArff(File csvFile) {
		try {
			// Load CSV file
			CSVLoader loader = new CSVLoader();
			loader.setSource(csvFile);
			Instances data = loader.getDataSet();

			// Create ARFF file
			File arffFile = File.createTempFile("arffFile", ".arff");
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