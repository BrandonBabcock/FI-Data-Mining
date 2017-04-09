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

	private File csvFile; // The passed CSV file

	public CsvToArffConverter(File file) {
		this.csvFile = file;
	}

	public File convertFile() throws IOException {
		// Load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(csvFile);
		Instances data = loader.getDataSet();

		String filename = csvFile.getName().substring(0, csvFile.getName().indexOf("."));

		// Save ARFF
		File arffFile = new File("Data/" + filename + ".arff");
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(arffFile);
		saver.writeBatch();
		return arffFile;
	}

}
