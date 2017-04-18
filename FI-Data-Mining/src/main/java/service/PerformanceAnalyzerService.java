package service;

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
		return this.stopTime - this.startTime / 1000000000.0;
	}

}
