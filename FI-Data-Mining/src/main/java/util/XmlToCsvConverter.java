package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Converter to convert an XML file to a CSV file
 */
public class XmlToCsvConverter {

	/**
	 * Private constructor to avoid making instances of this utility class
	 */
	private XmlToCsvConverter() {

	}

	/**
	 * Converts an XML file to a CSV file
	 */
	public static File convertToCsv(File xmlFile) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Create the CSV file
			File csvFile = File.createTempFile("xmlFile", ".csv");
			csvFile.deleteOnExit();

			// Configure PrintWriter to write to the CSV file
			PrintWriter writer = new PrintWriter(csvFile);

			// Values are appended to StringBuilder and written to the XML file
			StringBuilder strBuilder = new StringBuilder();

			// Parse through the XML file
			Document document = builder.parse(xmlFile);
			Element root = document.getDocumentElement();
			NodeList nodeList = root.getChildNodes();

			HashSet<String> attributes = new LinkedHashSet<String>();
			List<List<String>> values = new ArrayList<List<String>>();

			// Get all of the attributes and values from the file
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeType() != Node.TEXT_NODE) {
					NodeList childNodes = node.getChildNodes();
					List<String> nodeMapNodeValues = new ArrayList<String>();

					if (childNodes.getLength() == 0) {
						NamedNodeMap nodeMap = node.getAttributes();

						for (int j = 0; j < nodeMap.getLength(); j++) {
							attributes.add(nodeMap.item(j).getNodeName());
							String value = nodeMap.item(j).getNodeValue();

							if (value.equals("")) {
								value = "null";
							}

							value = value.replaceAll(",", "");
							value = value.trim().replaceAll(" +", "_");

							nodeMapNodeValues.add(value);
						}
					} else {
						for (int j = 0; j < childNodes.getLength(); j++) {
							if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE
									&& childNodes.item(j).getNodeType() != Node.TEXT_NODE) {
								attributes.add(childNodes.item(j).getNodeName());
								String value = childNodes.item(j).getFirstChild().getNodeValue();

								if (value.equals("")) {
									value = "null";
								}

								value = value.replaceAll(",", "");
								value = value.replaceAll("\n", "");
								value = value.trim().replaceAll(" +", "_");

								nodeMapNodeValues.add(value);
							}
						}
					}
					values.add(nodeMapNodeValues);
				}
			}

			// Append the attributes to the StringBuilder
			int count = 0;
			for (String attribute : attributes) {
				strBuilder.append(attribute);
				count++;

				if (count != attributes.size()) {
					strBuilder.append(",");
				}
			}

			// Make a new line
			strBuilder.append('\n');

			// Append the values to the StringBuilder
			for (List<String> list : values) {
				count = 0;
				for (String str : list) {
					strBuilder.append(str);
					count++;
					if (count != attributes.size()) {
						strBuilder.append(",");
					}
				}
				strBuilder.append('\n');
			}

			// Write the StringBuilder to a file
			writer.write(strBuilder.toString());
			writer.close();

			return csvFile;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}
}