package service;

import weka.associations.Apriori;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Service for data mining tasks
 */
public class DataMiningService {

	private DataMiningService() {

	}

	/**
	 * Finds the similarities within an ARFF file
	 * 
	 * @param filePath
	 *            the file path of the file to find the similarities within
	 * @throws Exception
	 */
	public static Apriori findSimilarities(String filePath) {
		try {
			// Load data
			Instances data = DataSource.read(filePath);
			data.setClassIndex(data.numAttributes() - 1);

			// Build associator
			Apriori apriori = new Apriori();
			apriori.setClassIndex(data.classIndex());
			apriori.buildAssociations(data);

			// Output associator
			System.out.println(apriori);
			return apriori;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

}
