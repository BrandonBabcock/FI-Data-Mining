package application;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * The purpose of this class is to take an .XML file and convert it into a .CSV file
 * @author Shawn Reece 2/14/2017
 */
public class XmlToCsvConverter {
	private File csvFile;
	private File xmlFile;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document document;
	
	public XmlToCsvConverter(File file) throws ParserConfigurationException, SAXException, IOException{
		this.xmlFile = file;
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		document = builder.parse(xmlFile);
	}
	
	
	public static void main(String args [] ){
		File testFile = new File("Data/Bio.xml");
		try {
			XmlToCsvConverter test = new XmlToCsvConverter(testFile);
			test.convertToCsv();		
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * The purpose of this method is convert the .XML file into a .CSV file
	 */
	public void convertToCsv(){
		File xmlFile = removeCommasFromXML(); // this xml will have no commas in the data
	}
	
	/**
	 * The purpose of this method is to remove commas if any from the XML document
	 */
	private File removeCommasFromXML(){
		Element root = document.getDocumentElement();

		NodeList nList = root.getChildNodes();
		for(int i = 0; i < nList.getLength(); i++){
			System.out.println(nList.item(i).getNodeName());
		}
		
		return null;
	}
	
	/**
	 * Returns the created .csv File
	 * @return csvFile which is the converted .XML File
	 */
	public File getCSVFile(){
		return this.csvFile;
	}

}
