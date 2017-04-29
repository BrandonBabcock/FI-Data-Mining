package service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class DataMinerServiceIntegrationTest {

	private DataMinerService dataMiner;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		dataMiner = new DataMinerService();
	}

	@After
	public void tearDown() {
		dataMiner = null;
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_invalid_file_passed_with_apriori_algorithm() {
		String[] options = { "10", "0.9", "0.05", "1.0", "0.1" };

		dataMiner.findAssociationRules("Apriori", "InvalidFile.arff", options);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_invalid_file_passed_with_filtered_associator_algorithm() {

		String[] options = { "10", "0.9", "0.05", "1.0", "0.1" };

		dataMiner.findAssociationRules("Filtered Associator", "InvalidFile.arff", options);
	}

}
