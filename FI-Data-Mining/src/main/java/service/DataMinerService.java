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
	public AbstractAssociator findAssociationRules(String algorithm, String filePath, String[] dataMiningOptions) {
		if (filePath.toLowerCase().substring(filePath.length() - 5).equals(".arff")) {
			if (algorithm.equals("Apriori")) {
				return findAprioriRules(filePath, dataMiningOptions);
			} else if (algorithm.equals("Filtered Associator")) {
				return findFilteredAssociatorRules(filePath, dataMiningOptions);
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
	private Apriori findAprioriRules(String filePath, String[] dataMiningOptions) {
		try {
			// Load data
			Instances data = DataSource.read(filePath);
			data.setClassIndex(data.numAttributes() - 1);

			// -N = required number of rules
			// -C = minimum confidence
			// -D = delta to reduce minimum support by
			// -U = upper bound for minimum support
			// -M = lower bound for minimum support
			String[] options = { "-N", dataMiningOptions[0], "-C", dataMiningOptions[1], "-D", dataMiningOptions[2],
					"-U", dataMiningOptions[3], "-M", dataMiningOptions[4] };

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
	private FilteredAssociator findFilteredAssociatorRules(String filePath, String[] dataMiningOptions) {
		try {
			// Load data
			Instances data = DataSource.read(filePath);
			data.setClassIndex(data.numAttributes() - 1);

			// -N = required number of rules
			// -C = minimum confidence
			// -D = delta to reduce minimum support by
			// -U = upper bound for minimum support
			// -M = lower bound for minimum support
			String[] options = { "-N", dataMiningOptions[0], "-C", dataMiningOptions[1], "-D", dataMiningOptions[2],
					"-U", dataMiningOptions[3], "-M", dataMiningOptions[4] };

			// Build associator
			FilteredAssociator filteredAssociator = new FilteredAssociator();
			filteredAssociator.setOptions(options);
			filteredAssociator.setClassIndex(data.classIndex());
			filteredAssociator.buildAssociations(data);

			return filteredAssociator;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

}
