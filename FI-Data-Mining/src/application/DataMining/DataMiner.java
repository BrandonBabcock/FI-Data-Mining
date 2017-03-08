package application.DataMining;

import weka.associations.Apriori;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class DataMiner {

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
