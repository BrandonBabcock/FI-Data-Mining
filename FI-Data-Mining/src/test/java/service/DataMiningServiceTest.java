package service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import weka.associations.Apriori;

public class DataMiningServiceTest {

	@Test
	public void should_return_rules_within_file() {
		Apriori apriori = DataMiningService.findSimilarities("Data/TestCsvOne.csv");

		boolean hasRules = apriori.getNumRules() > 0;

		assertThat(hasRules, equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_error_when_invalid_file_passed() {
		DataMiningService.findSimilarities("InvalidFile.csv");
	}

}
