package com.mx.tcs.soap.message.application.util;

import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;

import com.mx.tcs.soap.message.common.util.SOAPMessageAbstract;

public class SOAPFaulUtil extends SOAPMessageAbstract{
	
	private SOAPFault fault ;
	
	
	public SOAPFaulUtil() {
		 try {
			 
			 SOAPFactory soapFactory = createSOAPFactory();
			 this.fault = soapFactory.createFault();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * @param faultReason
	 * @param code
	 * @return
	 * @throws SOAPException
	 */
	public SOAPFault createSOAPFault(Exception e,String faultReason,String code) throws SOAPException{
		this.fault.setFaultString(faultReason);
		this.fault.setFaultCode(code);
		
		Detail detail = fault.addDetail();
		detail.addTextNode(e.toString());
		return fault;
	}
	
	/**
	 * @param detalle
	 * @param faultReason
	 * @param code
	 * @return
	 * @throws SOAPException
	 */
	public SOAPFault createSOAPFault(String detalle,String faultReason,String code) throws SOAPException{
		this.fault.setFaultString(faultReason);
		this.fault.setFaultCode(code);
		Detail detail = fault.addDetail();
		detail.addTextNode(detalle);
		return fault;
	}
	
	

	
	
	
	
	
	
}
