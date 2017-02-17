package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * The purpose of this class is to take an .XML file and convert it into a .CSV
 * file
 * 
 * @author Shawn Reece 2/14/2017
 */
public class XmlToCsvConverter {
	private File csvFile;
	private File xmlFile;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document document;

	public XmlToCsvConverter(File file) throws ParserConfigurationException, SAXException, IOException {
		this.xmlFile = file;
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		document = builder.parse(xmlFile);
	}

	public static void main(String args[]) {
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
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public void convertToCsv() throws SAXException, IOException, ParserConfigurationException {

		try {
			File xmlFile = removeCommasFromXML();

			// do conversion here

		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // this xml will have no commas
	}

	/**
	 * The purpose of this method is to remove commas if any from the XML
	 * document
	 * 
	 * @throws TransformerException
	 */
	private File removeCommasFromXML() throws TransformerException {
		Element root = document.getDocumentElement();

		// Hardcoded need to find way to make general
		NodeList nList = document.getElementsByTagName("jdbc:record");

		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			for (int j = 0; j < node.getAttributes().getLength(); j++) {
				String nodeValue = node.getAttributes().item(j).getNodeValue();
				if (nodeValue.contains(",")) {
					String newValue = nodeValue.replaceAll(",", " ");
					node.getAttributes().item(j).setNodeValue(newValue);
				}
			}
		}

		File newBio = new File("Data/newBio.xml");
		TransformerFactory transformFact = TransformerFactory.newInstance();
		Transformer transformer = transformFact.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(newBio);
		transformer.transform(source, result);

		return newBio;
	}

	/**
	 * Returns the created .csv File
	 * 
	 * @return csvFile which is the converted .XML File
	 */
	public File getCSVFile() {
		return this.csvFile;
	}

}
