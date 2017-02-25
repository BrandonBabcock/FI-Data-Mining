package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Purpose: This class is used to contruct the a .arff file. This file type used
 * by weka. Especially for using the apriori association algorithm.
 * 
 * @author Shawn Reece 2/25/2017
 *
 */
public class CsvToArff {

	private File csvFile; // the passed csv file.
	private File arffFile; // the csc file converted into the arff format.
	private String[] attributes;

	// Used for development
	public static void main(String[] args) {
		CsvToArff converter = new CsvToArff(new File("Data/newBio.csv"));
		converter.convertFile();
	}

	public CsvToArff(File file) {
		this.csvFile = file;
	}

	public void convertFile() {
		createAttributeSet();
	}

	/**
	 * This method will create the set of attributes found at the tope of the
	 * CSV file
	 */
	private void createAttributeSet() {
		try {
			Scanner scan = new Scanner(csvFile);

			/*
			 * obtaines the attributes found at the top of the file and store
			 * them into a String array
			 */
			attributes = scan.nextLine().split(",");
			
			for(String s : attributes)
				System.out.println(s);

			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
