package com.mx.tcs.soap.message.application.util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtil {
	
	public static String prettyPrint(final Document document) throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	 
	    StringWriter stringWriter = new StringWriter();
	    StreamResult streamResult = new StreamResult(stringWriter);
	 
	    transformer.transform(new DOMSource(document), streamResult);
	 
	   return stringWriter.toString();

	}

	public static Document toXmlDocument(final String xml) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

		Document document = DocumentBuilderFactory.newInstance()
	            .newDocumentBuilder()
	            .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
	 
	    XPath xPath = XPathFactory.newInstance().newXPath();
	    NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
	                                                  document,
	                                                  XPathConstants.NODESET);
	 
	    for (int i = 0; i < nodeList.getLength(); ++i) {
	        Node node = nodeList.item(i);
	        node.getParentNode().removeChild(node);
	    }
	 
		return document;
	}


}
