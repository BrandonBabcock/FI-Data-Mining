package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Converter to convert an XML file to a CSV file
 */
public class XmlToCsvConverter {
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;

	public XmlToCsvConverter() {
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
	public File convertToCsv(File xmlFile) {
		// Create the CSV file
		File csvFile = new File("Data/" + xmlFile.getName().replaceAll(".xml", ".csv"));
		try {
			// Configure PrintWriter to write to the CSV file
			PrintWriter writer = new PrintWriter(csvFile);
			// we append the values to string builder then write it to a csv
			// file
			StringBuilder strBuilder = new StringBuilder();
			// Parse through the XML file
			Document document = builder.parse(xmlFile);
			Element root = document.getDocumentElement();
			NodeList nList = root.getChildNodes();
			LinkedHashSet<String> attributes = new LinkedHashSet<>();
			ArrayList<ArrayList<String>> values = new ArrayList<>();
			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeType() != Node.TEXT_NODE) {
					NodeList nl = node.getChildNodes();
					ArrayList<String> temp = new ArrayList<String>();
					if (nl.getLength() == 0) {
						NamedNodeMap nm = node.getAttributes();
						for (int j = 0; j < nm.getLength(); j++) {
							attributes.add(nm.item(j).getNodeName());
							String value = nm.item(j).getNodeValue();

							if(value.equals("")){
							    value = "null";
                            }

							value = value.replaceAll(",", "");
							value = value.trim().replaceAll(" +", " ");
							temp.add(value);
						}
					} else {
						for (int j = 0; j < nl.getLength(); j++) {
							if (nl.item(j).getNodeType() == Node.ELEMENT_NODE
									&& nl.item(j).getNodeType() != Node.TEXT_NODE) {
								attributes.add(nl.item(j).getNodeName());
								String value = nl.item(j).getFirstChild().getNodeValue();

                                if(value.equals("")){
                                    value = "null";
                                }

								value = value.replaceAll(",", "");
								value = value.replaceAll("\n", "");
								value = value.trim().replaceAll(" +", " ");
								temp.add(value);
							}
						}
					}
					values.add(temp);
				}
			}
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
			for (ArrayList<String> list : values) {
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
			// write the string builder to a file
			writer.write(strBuilder.toString());
			writer.close();
			return csvFile;
		} catch (SAXException | IOException e) {
			throw new IllegalArgumentException("Error: " + e.getMessage(), e);
		}
	}
}