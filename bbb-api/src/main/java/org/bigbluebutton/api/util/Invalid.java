package org.bigbluebutton.api.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.bigbluebutton.api.xml.StandardResponse;
import org.bigbluebutton.api.xml.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Invalid {
	
	static final Logger log = LoggerFactory.getLogger(Security.class);
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
	public static void invalid(String key, String message, HttpServletResponse response){
		log.debug("Security : #invalid");
		try{
			response.addHeader("Cache-Control", "no-cache");
			response.setContentType("text/xml");
			StandardResponse responseBody = new StandardResponse(StandardResponse.RESP_CODE_FAILED, key, message);
			response.getWriter().println(xmlConverter.xml().toXML(responseBody));
		} catch (IOException e){
			
		}
		
	}
}
