package com.mx.main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import com.mx.tcs.soap.message.application.util.SOAPUtil;

public class Main {


	public static void main(String[] args) throws SOAPException, IOException, ParserConfigurationException {

		
		String to="http://64.178.214.155:8081/IdaHoldsPlaceRegular";
		 Path path = Paths.get("src/msg/CISACTAI_RQ.xml");
		 SOAPUtil instance = SOAPUtil.newInstance();
		 instance.addMimeHeader(SOAPUtil.SOAPAction,"CISACTAI");
		
		 SOAPMessage payloadRQ = instance.createSOAPPMessage(path.toFile());
		
		 System.out.println("----------SOAP Message Request-----------");
		 payloadRQ.writeTo(System.out);
		 System.out.println("----------SOAP Message Request-----------");
		
		
		 SOAPMessage payloadRs =instance.sendSOAPMessage(to);
		 System.out.println("----------SOAP Message Response-----------");
		 payloadRs.writeTo(System.out);
		
		 System.out.println("----------SOAP Message Response-----------");

	}
}
