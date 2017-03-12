package util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by shawn on 3/11/2017.
 */
public class XmlToCsvConverterTest {

    private XmlToCsvConverter testConvert;
    private String[] attributes; // xml file attributes are stored here
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private File xmlFile;

    @Before
    public void setup(){
        xmlFile = new File("Data/Bio.xml");

        init();

        testConvert = new XmlToCsvConverter(xmlFile);
    }

    /**
     * Used to obtain the xml file attributes
     */
    public void init(){
        factory = DocumentBuilderFactory.newInstance();

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            NodeList nList = document.getElementsByTagName("jdbc:record");
            Node node = nList.item(0);

            attributes = new String[node.getAttributes().getLength()];

            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                String str = node.getAttributes().item(i).getNodeName();
                attributes[i] =  str;
            }

        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException("Error: " + e.getMessage(), e);
        } catch (IOException e){

        } catch (SAXException e){

        }
    }

    @After
    public void teardown(){
        testConvert = null;
    }

    @Test
    public void convertToCsvTest(){
        File convertedFile = testConvert.convertToCsv();

        // test file extension is changed from xml to csv
        assertThat(convertedFile.getName(), equalTo("Bio.csv"));

        try {
            Scanner scan = new Scanner(convertedFile); // get csv attributes
            String[] att = scan.nextLine().split(",");
            assertThat(att, equalTo(attributes)); // test to see if the attributes from the xml match the attributes in the csv

            // check file content
            while(scan.hasNext()){
                String[] values = scan.nextLine().split(",");
                assertThat(attributes.length, equalTo(values.length)); // the number of values match the number of attributes
            }
        } catch (IOException e){
            fail("IO exception");
        }
    }
}
