package service;

import java.util.concurrent.TimeUnit;

/**
 * The RuntimeRecorderService can be used to record the runtime of a method
 */
public class RuntimeRecorderService {

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
		return (TimeUnit.SECONDS.convert((long) (stopTime - startTime), TimeUnit.NANOSECONDS));
	}

}
