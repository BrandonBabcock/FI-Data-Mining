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

	private File csvFile; // the passed csv file.'

	public CsvToArffConverter(File file) {
		this.csvFile = file;
	}

	public File convertFile() throws IOException{
			// load CSV
			CSVLoader loader = new CSVLoader();
			loader.setSource(csvFile);
			Instances data = loader.getDataSet();

			String filename = csvFile.getName().substring(0, csvFile.getName().indexOf("."));

			// save ARFF
			File arffFile = new File(filename + ".arff");
			ArffSaver saver = new ArffSaver();
			saver.setInstances(data);
			saver.setFile(arffFile);
			saver.writeBatch();
			return arffFile;
	}
}
