package util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


/**
 * Created by shawn on 3/11/2017.
 */
public class CsvToArffConverterTest {

    private CsvToArffConverter testConverter;
    private File csv = new File("Data/csvToArffTestFile.csv");
    private int numAttributes;
    private int numRecords = 0;

    @Before
    public void setup(){
        init();
        testConverter = new CsvToArffConverter(csv);
    }

    @After
    public void teardown(){
        testConverter = null;
    }

    public void init(){
        try {
            Scanner scan = new Scanner(csv);
            String[] Attributes = scan.nextLine().split(",");
            numAttributes = Attributes.length;

            while(scan.hasNext()){
                numRecords++;
                scan.nextLine();
            }

            scan.close();
        } catch (IOException e){

        }
    }

    @Test
    public void convertFileTest() throws IOException {
        File arff = testConverter.convertToArff();
        String filename = arff.getName();
        assertTrue(filename, filename.contains(".arff"));

        Scanner scan = new Scanner(arff);
        String fileheader = scan.nextLine();
        String relationName = filename.substring(0, filename.indexOf("."));

        assertThat(fileheader, equalTo("@relation " +  relationName));

        int attributeCount = 0;
        while (scan.hasNext()){
            String line = scan.nextLine();
            if(line.contains("@attribute")){
                attributeCount++;
            }
            if(line.contains("@data")){
                break;
            }
        }

        assertThat(attributeCount, equalTo(numAttributes));

        int lineCount = 0;
        while (scan.hasNext()){
            lineCount++;
            scan.nextLine();
        }

        assertThat(lineCount, equalTo(numRecords));
    }
}
