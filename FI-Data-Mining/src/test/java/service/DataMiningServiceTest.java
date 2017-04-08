package service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import weka.associations.Apriori;

public class DataMiningServiceTest {

	DataMiningService service;

	@Before
	public void setUp() {
		service = new DataMiningService();
	}

	@After
	public void tearDown() {
		service = null;
	}

	@Test
	public void should_return_rules_within_file() {
		Apriori apriori = service.findSimilarities("Data/TestCsvOne.csv");

		boolean hasRules = apriori.getNumRules() > 0;

		assertThat(hasRules, equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_invalid_file_passed() {
		service.findSimilarities("InvalidFile.csv");
	}

}