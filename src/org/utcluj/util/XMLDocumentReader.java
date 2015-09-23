package org.utcluj.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 *	Load an XML document based on a file path.
 * 
 * @author Florin Pop
 *
 */
public class XMLDocumentReader {

	public static Document read(String path) throws SAXException, IOException,
			ParserConfigurationException {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		// Parse the XML file and build the Document object in RAM
		Document doc = docBuilder.parse(new File(path));

		// Normalize text representation.
	      // Collapses adjacent text nodes into one node.
	      doc.getDocumentElement().normalize();
		
		return doc;
	}
	
	public static void main(String [] args) {
		
		try {
			
			Document d = XMLDocumentReader.read("plan.xml");
			XPath x = XPathFactory.newInstance().newXPath();
			
			XPathExpression e = x.compile("/*/plan/step");
			NodeList result = (NodeList) e.evaluate(d, XPathConstants.NODESET);
			
			int len = result.getLength();
			
			System.out.println("Plan has " + result.getLength() + " steps");
			
			for (int i = 1; i <= len; i ++) {
				
				XPathExpression expr = x.compile("/*/plan/step[" + i + "]/@name");
				System.out.println(expr.evaluate(d));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
