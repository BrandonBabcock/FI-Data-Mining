package service;

import org.junit.After;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

public class DataMinerServicePerformanceTest {

	private DataMinerService dataMiner;
	private String[] dataMiningOptions = { "10", "0.9", "0.05", "1.0", "0.1" };

	/**
	 * JUnit Rule to keep track of a test's execution time
	 */
	@Rule
	public final Stopwatch stopwatch = new Stopwatch() {
		/**
		 * Invoked when a test passes
		 */
		protected void succeeded(long nanos, Description description) {
			System.out.println(description.getMethodName() + " succeeded, time taken " + nanos);
		}

		/**
		 * Invoked when a test fails
		 */
		protected void failed(long nanos, Throwable e, Description description) {
			System.out.println(description.getMethodName() + " failed, time taken " + nanos);
		}

		/**
		 * Invoked when a test is skipped due to a failed assumption.
		 */
		protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
			System.out.println(description.getMethodName() + " skipped, time taken " + nanos);
		}

		/**
		 * Invoked when a test method finishes (whether passing or failing)
		 */
		protected void finished(long nanos, Description description) {
			System.out.println(description.getMethodName() + " finished, time taken " + nanos);
		}

	};

	@Before
	public void setUp() {
		dataMiner = new DataMinerService();
	}

	@After
	public void tearDown() {
		dataMiner = null;
	}

	@Test
	public void should_run_apriori_data_mining_method() {
		System.out.println("Start Apriori Performance Test");
		dataMiner.findAssociationRules("Apriori", "Data/TestArffOne.arff", dataMiningOptions);
		System.out.println("Finish Apriori Performance Test");
	}

	@Test
	public void should_run_filtered_associator_data_mining_method() {
		System.out.println("Start Filtered Associator Performance Test");
		dataMiner.findAssociationRules("Filtered Associator", "Data/TestArffOne.arff", dataMiningOptions);
		System.out.println("Finish Filtered Associator Test");
	}

}
