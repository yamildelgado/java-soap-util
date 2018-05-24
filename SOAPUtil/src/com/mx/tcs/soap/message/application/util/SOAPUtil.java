/**
 * @author Yamil Omar Delgado Gonzalez
 * @version 1.0
 * {@link} yamil.delgado.gonzalez75@gmail.com
 */
package com.mx.tcs.soap.message.application.util;

import java.util.Objects;

import com.mx.tcs.soap.message.common.util.SOAPMessageAbstract;

public class SOAPUtil extends SOAPMessageAbstract {
	 public static final String SOAPAction="SOAPAction";
	
	private static SOAPUtil util;
	private SOAPUtil() {
		super();
	}

	
 public static SOAPUtil newInstance(){
	
	 if(Objects.isNull(util)){
		 util=new SOAPUtil();
	 }
	 return util; 
 }
	
	 

}
