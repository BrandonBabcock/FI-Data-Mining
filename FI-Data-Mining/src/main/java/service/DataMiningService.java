package service;

import weka.associations.Apriori;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Service for data mining tasks
 */
public class DataMiningService {

	/**
	 * Finds the similarities within an ARFF file
	 * 
	 * @param filePath
	 *            the file path of the file to find the similarities within
	 * @throws Exception
	 */
	public void findSimilarities(String filePath) throws Exception {
		// Load data
		Instances data = DataSource.read(filePath);
		data.setClassIndex(data.numAttributes() - 1);

		// Build associator
		Apriori apriori = new Apriori();
		apriori.setClassIndex(data.classIndex());
		apriori.buildAssociations(data);

		// Output associator
		System.out.println(apriori);
	}

}
