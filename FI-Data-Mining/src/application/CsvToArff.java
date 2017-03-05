package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
	private String[] attributes; // array that holds all the csv attributes
	private ArrayList<String[]> data; // structure thats holds all the csv data
    private ArrayList<LinkedHashSet> sets;

	// Used for development
	public static void main(String[] args) {
		CsvToArff converter = new CsvToArff(new File("Data/GroupsByUser.csv"));
		converter.convertFile();
	}

	public CsvToArff(File file) {
		this.csvFile = file;
	}

	public void convertFile() {
		try {

			fileAttributes(); // get the file attributes
			fileData(); // get the files data
            valueSet(); // get the set of values
			makeFile(); // create the arff file

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will create the set of attributes found at the tope of the
	 * CSV file
	 *
	 * @throws FileNotFoundException
	 */
	private void fileAttributes() throws FileNotFoundException {

		Scanner scan = new Scanner(csvFile);

		// obtaines the attributes found at the top of the file
		attributes = scan.nextLine().split(",");

		scan.close();

	}

	/**
	 * Purpose: This method is used to obtain all the values of the attributes
	 * and store them in an arrayList and fills blank values with "?"
	 *
	 * @throws FileNotFoundException
	 */
	private void fileData() throws FileNotFoundException {
		Scanner scan = new Scanner(csvFile);

		scan.nextLine();

		this.data = new ArrayList<>();

		while (scan.hasNext()) {
			String[] row = scan.nextLine().split(",");

			// Fill empty values with a questio mark
			for (int i = 0; i < row.length; i++) {

				if (row[i].equals("")) {
					row[i] = "?";
				}
			}

			data.add(row);
		}

		scan.close();
	}

	/**
	 * Purpose: To build the arff file
	 *
	 * @throws FileNotFoundException
	 */
	private void makeFile() throws FileNotFoundException {

		// trim off the file extension
		String fileName = csvFile.getName();
		String name = fileName.substring(0, fileName.indexOf('.'));

		File file = new File("Data/" + name + ".arff"); //create a new file with same name but with arff extension

		// write to the new file
		PrintWriter writer = new PrintWriter(file);

		writer.write("@relation " + name);

		writer.flush();

		writer.write('\n');

		// writes attributes to top of the file
		for (String s : attributes) {
			writer.write(createArffAttribute(s) + '\n');
			writer.flush();
		}

		// start the data section
		writer.write("@data" + '\n');

		writer.flush();

		// go to each array and write the value to the file
		for (String[] s : data) {
			for (int i = 0; i < s.length; i++) {
				writer.write(s[i]);
				writer.flush();
				if (i != s.length - 1) { // used to put a comma after each value
											// except for the last value
					writer.write(",");
					writer.flush();
				}
			}
			writer.write('\n');
			writer.flush();
		}

		writer.close();
	}

    /**
     * Purpose: Build the sets containing all the values in the file. Stores the sets in an array list
     */
	private void valueSet(){

        sets = new ArrayList(); // initialize the array list

        // build sets for each attribute
        for(int i = 0; i < attributes.length; i++){
            LinkedHashSet<String> value = new LinkedHashSet<String>();
            sets.add(value);
        }

        // go through the data and an add the value to the correct set
        for(String[] arr : data){
            for(int i = 0; i < arr.length; i++){
                sets.get(i).add(arr[i]);
            }
        }
    }

	/**
	 * Purpose: Create the attributes and the values contained in the curly
	 * braces
	 *
	 * @param s
	 *            the attribute
	 * @return the contructed attribute value
	 */
	private String createArffAttribute(String s) {

        int index = 0;

        // find the index value for that attribute
        for(int i = 0; i < attributes.length; i++){
            if(s.equals(attributes[i])) {
                index = i;
                break;
            }
        }

		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("@attribute " + s + "{");

		int count = 0;
		int length = sets.get(index).size();

        // go to the set and get the values in it
        for(String str : (LinkedHashSet<String>)sets.get(index)){
            strBuilder.append(str);

            count++;
            if(count != length){
				strBuilder.append(",");
			}
        }

		strBuilder.append("}");

		return strBuilder.toString();
	}

	/**
	 * Returns the generated arff File
	 *
	 * @return File the converted csv into arff format
	 */
	public File getArffFile() {
		return this.arffFile;
	}

    /**
     * Return the sets of values for each attribute
     * @return ArrayList containing LinkedHashSets with all the unqiue values in the file
     */
    public ArrayList<LinkedHashSet> getSets(){
        return this.sets;
    }
}
