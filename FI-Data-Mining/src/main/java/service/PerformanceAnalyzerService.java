package service;

import java.io.File;

/**
 * The PerformanceAnalyzerService can be used to generate performance metrics
 * for data mining tasks
 */
public class PerformanceAnalyzerService {

	/* The start time */
	private double startTime;

	/* The ending time */
	private double stopTime;

	/**
	 * Records the starting time
	 */
	public void start() {
		startTime = System.nanoTime();
	}

	/**
	 * Records the ending time
	 */
	public void stop() {
		stopTime = System.nanoTime();
	}

	/**
	 * Gets the run time
	 * 
	 * @return the run time
	 */
	public double getRunTime() {
		return this.stopTime - this.startTime;
	}

	/**
	 * Gets the ratio between the data set size and run time
	 * 
	 * @param filePath
	 *            the path to the data set to find the data set size to run time
	 *            ratio of
	 * @return the ratio between the data set size and run time
	 */
	public double getDataSetSizeToRunTimeRatio(String filePath) {
		File file = new File(filePath);
		double bytes = file.length();

		return bytes / getRunTime();
	}

}
