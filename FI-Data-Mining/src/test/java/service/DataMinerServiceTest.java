package service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import weka.associations.AbstractAssociator;
import weka.associations.Apriori;
import weka.associations.FilteredAssociator;

public class DataMinerServiceTest {

	private DataMinerService service;

	@Before
	public void setUp() {
		service = new DataMinerService();
	}

	@After
	public void tearDown() {
		service = null;
	}

	@Test
	public void should_return_association_rules_using_the_apriori_algorithm_within_file() {
		String[] options = { "10", "0.9", "0.05", "1.0", "0.1" };

		Apriori apriori = (Apriori) service.findAssociationRules("Apriori", "Data/TestArffOne.arff", options);

		boolean hasRules = apriori.getAssociationRules().getNumRules() > 0;

		assertThat(hasRules, equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_invalid_file_passed_with_apriori_algorithm() {
		String[] options = { "10", "0.9", "0.05", "1.0", "0.1" };

		service.findAssociationRules("Apriori", "InvalidFile.arff", options);
	}

	@Test
	public void should_return_association_rules_using_the_filtered_associator_algorithm_within_file() {
		String[] options = { "10", "0.9", "0.05", "1.0", "0.1" };

		FilteredAssociator filteredAssociator = (FilteredAssociator) service.findAssociationRules("Filtered Associator",
				"Data/TestArffOne.arff", options);

		boolean hasRules = filteredAssociator.getAssociationRules().getNumRules() > 0;

		assertThat(hasRules, equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_invalid_file_passed_with_filtered_associator_algorithm() {

		String[] options = { "10", "0.9", "0.05", "1.0", "0.1" };

		service.findAssociationRules("Filtered Associator", "InvalidFile.arff", options);

	}

	@Test
	public void should_return_null_when_invalid_algorithm_is_passed() {
		String[] options = { "10", "0.9", "0.05", "1.0", "0.1" };

		AbstractAssociator associator = service.findAssociationRules("Invalid", "Data/TestArffOne.arff", options);

		assertThat(associator, equalTo(null));
	}

	@Test
	public void should_return_null_when_invalid_file_type_is_passed() {
		String[] options = { "10", "0.9", "0.05", "1.0", "0.1" };

		AbstractAssociator associator = service.findAssociationRules("Apriori", "Data/TestCsvOne.csv", options);

		assertThat(associator, equalTo(null));
	}

}