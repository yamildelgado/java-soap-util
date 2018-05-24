package com.mx.tcs.soap.message.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Yamil Omar Delgado Gonzalez
 * @version 1.0 {@link} yamil.delgado.gonzalez75@gmail.com
 * 
 */
public abstract class SOAPMessageAbstract implements SOAPMessageService {

	/** Only one reference **/
	private SOAPMessage message;

	public SOAPMessageAbstract() {
		synchronized (SOAPMessageAbstract.class) {
			if (Objects.isNull(message)) {
				try {

					MessageFactory messageFactory = createMessageFactory();
					this.message = messageFactory.createMessage();
				} catch (SOAPException e) {
					throw new WebServiceException(e);
				}
			}
		}
	}

	private SOAPHeader ToSOAPHeader(SOAPMessage sMessage) throws SOAPException {
		return ToSOAPEnvelope(sMessage).getHeader();
	}

	/**
	 * @param sMessage
	 * @return
	 * @throws SOAPException
	 */
	private SOAPBody ToSOAPBody(SOAPMessage sMessage) throws SOAPException {
		return ToSOAPEnvelope(sMessage).getBody();
	}

	/**
	 * @param sMessage
	 * @return
	 * @throws SOAPException
	 */
	private SOAPPart ToSOAPPart(SOAPMessage sMessage) throws SOAPException {
		return sMessage.getSOAPPart();
	}

	/**
	 * @param sMessage
	 * @return
	 * @throws SOAPException
	 */
	private SOAPEnvelope ToSOAPEnvelope(SOAPMessage sMessage) throws SOAPException {
		return ToSOAPPart(sMessage).getEnvelope();
	}

	/**
	 * Permite extraer el contenido del SOAP Body y mostrarlo como XML
	 * 
	 * @param body
	 * @return
	 * @throws SOAPException
	 * @throws TransformerException
	 */
	public String getSOAPBodyAsString(SOAPBody body) throws TransformerException {
		String xmlMessage = null;

		Document document = null;
		try {
			document = body.extractContentAsDocument();
		} catch (SOAPException e) {
			return e.getMessage();
		}
		document.normalizeDocument();
		Transformer transformer = createTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		DOMSource xmlSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult outputTarget = new StreamResult(writer);

		transformer.transform(xmlSource, outputTarget);
		xmlMessage = writer.toString();

		return xmlMessage;
	}
	
	public Document getSOAPBodyAsDocument(SOAPBody body) {
		String xmlMessage = null;

		Document document = null;
			try {
				document = body.extractContentAsDocument();
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		document.normalizeDocument();

		return document;
	}

	/**
	 * @param body
	 * @return
	 * @throws TransformerException
	 */
	public String getSOAPBodyAsString(Source body) throws TransformerException {
		String xmlMessage = null;
		Transformer transformer = createTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		StringWriter writer = new StringWriter();
		StreamResult outputTarget = new StreamResult(writer);

		transformer.transform(body, outputTarget);
		xmlMessage = writer.toString();

		return xmlMessage;
	}

	/**
	 * @param handler
	 * @return
	 * @throws SOAPException
	 * @throws FileNotFoundException
	 */
	public void addAttachments(File... files) throws SOAPException, FileNotFoundException {

		AttachmentPart attachmentPart = null;
		/** Envolvemos el archivo en un FileDataSource **/
		
		 Objects.requireNonNull(files, "Files can't be null");
		 
		
		for (File file : files) {
			attachmentPart = this.message.createAttachmentPart();
			attachmentPart.setBase64Content(new FileInputStream(file), null);

			/** Guardamos los attachment en el SOAP Message Part **/
			this.message.addAttachmentPart(attachmentPart);
		}
	}

	/**
	 * @param content
	 * @param contentType
	 * @return
	 * @throws SOAPException
	 * @throws FileNotFoundException
	 */
	public void addAttachment(final File content, final String contentType)
			throws SOAPException, FileNotFoundException {

		FileInputStream input = new FileInputStream(content);

		AttachmentPart attachmentPart = this.message.createAttachmentPart();
		/** The message will be encode on Base 64  lo hara 33 porciento mas grande **/
		attachmentPart.setBase64Content(input, contentType);
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.message.addAttachmentPart(attachmentPart);
	}

	/**
	 * @param bodyXML
	 * @return 
	 * @throws SOAPException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public SOAPMessage addSOAPBodyMessage(String bodyXML) throws SOAPException, IOException, ParserConfigurationException {

		Objects.requireNonNull(bodyXML, "Body Xml field can't be null,please check it.");

		/** Creamos en memoria un string en representacion del objeto DOM **/
		byte[] bytes = bodyXML.getBytes("UTF-8");
		InputStream inputStream = new ByteArrayInputStream(bytes);
		Document document = null;
		try {
			document = getDocumentBuilder().parse(inputStream);
		} catch (SAXException e) {
			throw new WebServiceException(e);
		}

		/** Recuperamos el Envelope del mensaje **/
		SOAPBody soapBody = ToSOAPBody(message);
		soapBody.addDocument(document);
		return message;

	}

	/**
	 * @param namespace
	 * @param prefix
	 * @throws SOAPException
	 */
	public void addTagNamespaces(String namespace, String prefix) throws SOAPException {
		/** Obtenemos el objeto SOAP Message **/
		SOAPEnvelope envelope = ToSOAPEnvelope(message);
		envelope.addNamespaceDeclaration(namespace, prefix);
	}

	/**
	 * @param key
	 * @param value
	 * @throws SOAPException
	 */
	public void addMimeHeader(String key, String value) throws SOAPException {
		MimeHeaders headers = message.getMimeHeaders();
		headers.addHeader(key, value);
	}

	/**
	 * @param mapTagNameSapces
	 * @throws SOAPException
	 */
	public void addTagNamespaces(Map<String, String> mapTagNameSapces) throws SOAPException {

		SOAPEnvelope envelope = ToSOAPEnvelope(message);
		/** Agregamos Prefix al namespaces */
		if (mapTagNameSapces != null) {
			Set<Entry<String, String>> entrySet = mapTagNameSapces.entrySet();
			Iterator<Entry<String, String>> iterable = entrySet.iterator();
			while (iterable.hasNext()) {
				Entry<String, String> element = iterable.next();
				envelope.addNamespaceDeclaration(element.getKey(), element.getValue());
			}
		}
	}

	/**
	 * @param mimeHeader
	 * @param file
	 * @return
	 * @throws SOAPException
	 * @throws IOException
	 */
	public SOAPMessage createSOAPMessage(MimeHeaders mimeHeader, File file) throws SOAPException, IOException {

		FileInputStream inputStream = new FileInputStream(file);
		MessageFactory messageFactory = createMessageFactory();
		this.message = messageFactory.createMessage(mimeHeader, inputStream);

		return message;
	}

	/**
	 * @param file
	 * @throws SOAPException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public SOAPMessage createSOAPPMessage(File file) throws SOAPException, IOException, ParserConfigurationException {

		byte[] bytes = FileUtils.readFileToByteArray(file);
		String xmlFile = new String(bytes);
		return addSOAPMessage(xmlFile);

	}

	/**
	 * @param arg
	 * @param loader
	 * @return
	 * @throws SOAPException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public SOAPMessage createSOAPMessage(Object arg, Class... loader)
			throws SOAPException, IOException, ParserConfigurationException {

		JAXBContext context = null;
		try {
			context = JAXBContext.newInstance(loader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Marshaller marshaller = null;
		try {
			marshaller = context.createMarshaller();
		} catch (JAXBException e) {
			throw new WebServiceException(e);
		}
		StringWriter writeToXML = new StringWriter();
		try {
			marshaller.marshal(arg, writeToXML);
		} catch (JAXBException e) {
			throw new WebServiceException(e);
		}

		this.message = createSOAPMessage(writeToXML.toString());
		return message;
	}

	/**
	 * @param bodyXML
	 * @throws SOAPException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public void addSOAPBodyMessage(SOAPElement element) throws SOAPException {

		if (element == null) {
			throw new WebServiceException("Body Xml field can't be null,please check it.");
		}

		/** Recuperamos el Envelope del mensaje **/
		SOAPEnvelope soapEnvelope = ToSOAPEnvelope(message);

		/** Agregamos el Documento al body **/
		soapEnvelope.getBody().addChildElement(element);

	}

	/**
	 * @param header
	 * @param body
	 * @return
	 * @throws SOAPException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public SOAPMessage addSOAPMessage(String soapXML) throws SOAPException, IOException, ParserConfigurationException {

		if (soapXML == null) {
			throw new WebServiceException("Body Xml field cant be null,please check it.");
		}

		ByteArrayInputStream inputStream = new ByteArrayInputStream(soapXML.getBytes(Charset.forName("UTF-8")));
		MessageFactory messageFactory = createMessageFactory();
		this.message = messageFactory.createMessage(new MimeHeaders(), inputStream);

		return message;
	}

	/**
	 * @param bodyXML
	 * @return
	 * @throws SOAPException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public SOAPMessage createSOAPPMessage(Source bodyXML)
			throws SOAPException, IOException, ParserConfigurationException, TransformerException {

		if (bodyXML == null) {
			throw new WebServiceException("Error en el cuerpo del mensaje no puede ser null");
		}

		/** Creamos en memoria un string en representacion del objeto DOM **/
		String xmlData = getSOAPBodyAsString(bodyXML);
		return addSOAPMessage(xmlData);

	}

	/**
	 * Metodo que permite agregar sobre el objeto SOAP MESSAGE elementos en el
	 * HEADER
	 * 
	 * @param element
	 * @throws SOAPException
	 */
	public void addSOAPHeader(SOAPElement element) throws SOAPException {
		SOAPHeader header = ToSOAPHeader(message);
		header.addChildElement(element);
	}

	/**
	 * @param element
	 * @throws SOAPException
	 * @throws ParserConfigurationException
	 */
	public void addSOAPHeader(String headerXML) throws SOAPException, ParserConfigurationException {

		DocumentBuilder documentBuilder = getDocumentBuilder();
		InputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(headerXML.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			throw new WebServiceException(e1);
		}
		Document document = null;
		try {
			document = documentBuilder.parse(inputStream);
		} catch (SAXException e) {

			throw new WebServiceException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Document puede ser representado como Element el cual necesitamos
		 * convertirlo en SOAPElement
		 **/
		Element domElement = document.getDocumentElement();
		SOAPElement soapElement = createSOAPElement(domElement);
		addSOAPHeader(soapElement);
	}

	/**
	 * @param localname
	 * @return
	 * @throws SOAPException
	 */
	public SOAPElement createSOAPElement(String localname) throws SOAPException {
		SOAPFactory soapFactory = createSOAPFactory();
		SOAPElement element = soapFactory.createElement(localname);
		return element;
	}

	/**
	 * @param localname
	 * @return
	 * @throws SOAPException
	 */
	public SOAPElement createSOAPElement(String localname, String prefix, String uri) throws SOAPException {

		SOAPFactory soapFactory = createSOAPFactory();
		SOAPElement element = soapFactory.createElement(localname, prefix, uri);
		return element;
	}

	/**
	 * @param localname
	 * @return
	 * @throws SOAPException
	 */
	public SOAPElement createSOAPElement(Element domElement) throws SOAPException {

		SOAPElement element = createSOAPFactory().createElement(domElement);
		return element;
	}

	/**
	 * @param url
	 * @return
	 * @throws SOAPException
	 */
	public void addAttachment(final URL url) throws SOAPException {
		DataHandler dh = new DataHandler(url);
		AttachmentPart attachmentPart = this.message.createAttachmentPart(dh);
		this.message.addAttachmentPart(attachmentPart);
	}

	/**
	 * @return
	 * @throws SOAPException
	 * @throws TransformerException
	 */
	public String getBodyAsString() throws SOAPException, TransformerException {
		SOAPBody body = ToSOAPBody(message);
		return getSOAPBodyAsString(body);

	}

	
	@Override
	public SOAPMessage sendSOAPMessage(String endPoint) throws SOAPException {

		this.message.saveChanges();
		SOAPConnection connection = createSOAPConnection();
		this.message=connection.call(message, endPoint);
		return message;

	}



	
}
