package service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import weka.associations.Apriori;
import weka.associations.FilteredAssociator;

public class DataMiningServiceTest {

	DataMinerService service;

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
		Apriori apriori = (Apriori) service.findAssociationRules("Apriori", "Data/TestCsvOne.csv");

		boolean hasRules = apriori.getNumRules() > 0;

		assertThat(hasRules, equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_invalid_file_passed_with_apriori_algorithm() {
		service.findAssociationRules("Apriori", "InvalidFile.csv");
	}

	@Test
	public void should_return_association_rules_using_the_filtered_associator_algorithm_within_file() {
		FilteredAssociator filteredAssociator = (FilteredAssociator) service.findAssociationRules("Filtered Associator",
				"Data/TestCsvOne.csv");

		boolean hasRules = filteredAssociator.getAssociationRules().getNumRules() > 0;

		assertThat(hasRules, equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_invalid_file_passed_with_filtered_associator_algorithm() {
		service.findAssociationRules("Filtered Associator", "InvalidFile.csv");
	}
	
	@Test
	public void should_return_null_when_invalid_algorithm_is_passed() {
		assertThat(service.findAssociationRules("Invalid Algorithm", "Data/TestCsvOne.csv"), equalTo(null));
	}

}