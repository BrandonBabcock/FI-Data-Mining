package service;

import weka.associations.AbstractAssociator;
import weka.associations.Apriori;
import weka.associations.FilteredAssociator;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Service for data mining tasks
 */
public class DataMinerService {

	/**
	 * Finds the association rules within an ARFF file using the given algorithm
	 * 
	 * @param algorithm
	 *            the algorithm to use
	 * @param filePath
	 *            the file path of the file to find the association rules in
	 * @return the association rules
	 */
	public AbstractAssociator findAssociationRules(String algorithm, String filePath) {
		if (filePath.toLowerCase().substring(filePath.length() - 5).equals(".arff")) {
			if (algorithm.equals("Apriori")) {
				return findAprioriRules(filePath);
			} else if (algorithm.equals("Filtered Associator")) {
				return findFilteredAssociatorRules(filePath);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Finds the association rules within an ARFF file using the Apriori
	 * algorithm
	 * 
	 * @param filePath
	 *            the file path of the file to find the association rules in
	 * @return the Apriori object containing the association rules
	 */
	private Apriori findAprioriRules(String filePath) {
		try {
			// Load data
			Instances data = DataSource.read(filePath);
			data.setClassIndex(data.numAttributes() - 1);

			String[] options = { "-N", "10", "-T", "0", "-C", "0.01", "-D", "0.01", "-U", "1.0", "-M", "0.01" };

			// Build associator
			Apriori apriori = new Apriori();
			apriori.setOptions(options);
			apriori.setClassIndex(data.classIndex());
			apriori.buildAssociations(data);

			return apriori;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	/**
	 * Finds the association rules within an ARFF file using the
	 * FilteredAssociator algorithm
	 * 
	 * @param filePath
	 *            the file path of the file to find the association rules in
	 * @return the FilteredAssociator object containing the association rules
	 */
	private FilteredAssociator findFilteredAssociatorRules(String filePath) {
		try {
			// Load data
			Instances data = DataSource.read(filePath);
			data.setClassIndex(data.numAttributes() - 1);

			// Build associator
			FilteredAssociator filteredAssociator = new FilteredAssociator();
			filteredAssociator.setClassIndex(data.classIndex());
			filteredAssociator.buildAssociations(data);

			return filteredAssociator;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

}
