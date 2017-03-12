package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Converter to convert an XML file to a CSV file
 */
public class XmlToCsvConverter {

	private File xmlFile;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	// private Document document;

	public XmlToCsvConverter(File file) {
		this.xmlFile = file;
		factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	/**
	 * Converts an XML file to a CSV file
	 */
	public File convertToCsv() {

		// Create the CSV file
		File csvFile = new File("Data/" + xmlFile.getName().replaceAll(".xml", ".csv"));

		// Remove commas from the XML file's values
		File xmlFile = removeCommasFromXML();

		try {
			// Configure PrintWriter to write to the CSV file
			PrintWriter writer = new PrintWriter(csvFile);

			// we append the values to string builder then write it to a csv
			// file
			StringBuilder strBuilder = new StringBuilder();

			// Parse through the XML file
			Document document = builder.parse(xmlFile);

			// Hardcoded need to find way to make general
			NodeList nList = document.getElementsByTagName("jdbc:record");
			Node node = nList.item(0);

			for (int i = 0; i < node.getAttributes().getLength(); i++) {
				String str = node.getAttributes().item(i).getNodeName();
				strBuilder.append(str);
				strBuilder.append(',');
			}

			// Make a new line
			strBuilder.append('\n');

			// add the attribute values to the string builder
			for (int i = 0; i < nList.getLength(); i++) {
				node = nList.item(i);
				for (int j = 0; j < node.getAttributes().getLength(); j++) {
					String str = node.getAttributes().item(j).getNodeValue();
					strBuilder.append(str);
					strBuilder.append(',');
				}
				strBuilder.append('\n');
			}

			// write the string builder to a file
			writer.write(strBuilder.toString());
			writer.close();

			return csvFile;
		} catch (SAXException | IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}

	/**
	 * The purpose of this method is to remove commas if any from the XML
	 * document
	 * 
	 * @throws TransformerException
	 */
	private File removeCommasFromXML() {
		try {
			Document document = builder.parse(xmlFile);
			// Hardcoded need to find way to make general
			NodeList nList = document.getElementsByTagName("jdbc:record");

			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				for (int j = 0; j < node.getAttributes().getLength(); j++) {
					String nodeValue = node.getAttributes().item(j).getNodeValue();
					if (nodeValue.contains(",")) {
						String newValue = nodeValue.replaceAll(",", "");
						node.getAttributes().item(j).setNodeValue(newValue);
					}
				}
			}

			File removedCommas = new File("removedCommas.xml");
			TransformerFactory transformFact = TransformerFactory.newInstance();

			Transformer transformer = transformFact.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(removedCommas);
			transformer.transform(source, result);
			return removedCommas;
		} catch (SAXException | IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

}
