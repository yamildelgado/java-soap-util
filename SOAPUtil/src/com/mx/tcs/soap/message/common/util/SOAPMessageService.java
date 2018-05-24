/**
 * @author Yamil Omar Delgado Gonzalez
 * @version 1.0
 * {@link} yamil.delgado.gonzalez75@gmail.com
 * 
 */
package com.mx.tcs.soap.message.common.util;

import java.util.Objects;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.ws.WebServiceException;


/**
 * @author Yamil Omar Delgado Gonzalez
 *
 */
@FunctionalInterface
public interface SOAPMessageService {
	
	/**
	 * @param endPoint
	 * @return
	 * @throws SOAPException
	 */
	public SOAPMessage sendSOAPMessage(String endPoint) throws SOAPException;	
	

	/**
	 * @return
	 * @throws TransformerConfigurationException
	 */
	public default Transformer createTransformer() throws TransformerConfigurationException {
		TransformerFactory transferFactory = TransformerFactory.newInstance();
		Transformer transformer = transferFactory.newTransformer();
		return transformer;
	}

	/**
	 * @return MessageFactory
	 */
	public default MessageFactory createMessageFactory() {
		MessageFactory messageFactory = null;
		try {
			messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
		} catch (SOAPException e) {

			throw new WebServiceException(e);
		}
		return messageFactory;
	}

	/**
	 * @return SOAPFactory
	 */
	public default SOAPFactory createSOAPFactory() {
		SOAPFactory soapFactory = null;

		try {
			soapFactory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

		} catch (SOAPException e) {
			throw new WebServiceException(e);
		}
		return soapFactory;
	}
	
	/**
	 * @return
	 * @throws ParserConfigurationException
	 */
	public default DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		return documentBuilder;

	}
	
	
	/**
	 * @return
	 * @throws SOAPException
	 */
	public default SOAPConnection createSOAPConnection() throws SOAPException {
		SOAPConnectionFactory instance = null;
		SOAPConnection soapConnection =null;
		try {
			instance = SOAPConnectionFactory.newInstance();
			if( Objects.nonNull(instance)){
				soapConnection = instance.createConnection();
			}
		} catch (UnsupportedOperationException e) {

			throw new WebServiceException(e);
		} 
		
		return soapConnection;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getRandomUUID() {
		String uniqueID = new String();
		uniqueID = UUID.randomUUID().toString();
		return uniqueID;
	}
	
}
