package org.bigbluebutton.api.util;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.bigbluebutton.api.Create;
import org.bigbluebutton.api.conference.DynamicConferenceService;
import org.bigbluebutton.api.xml.StandardResponse;
import org.bigbluebutton.api.xml.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Security {
	
	static final Logger log = LoggerFactory.getLogger(Security.class);
	static final DynamicConferenceService dynamicConferenceService = DynamicConferenceService.getInstance();
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
	public static Boolean doChecksumSecurity(String callName, HttpServletRequest request, HttpServletResponse response){
		log.debug("Security: #doChecksumSecurity");
		log.debug("checksum: " + request.getParameter("checksum") + "; query string: " + request.getQueryString());
		
		if (StringUtils.isEmpty(callName)){
			throw new RuntimeException("Programming error - you must pass the call name to doChecksumSecurity so it can be used in the checksum");
		}
		if (StringUtils.isEmpty(request.getQueryString())){
			Invalid.invalid("noQueryString", "No query string was found in your request.", response);
			return false;
		}
		
		if (StringUtils.isEmpty(securitySalt()) == false){
			String qs = request.getQueryString();
			// handle either checksum as first or middle / end parameter
			// TODO: this is hackish - should be done better
			qs = qs.replace("&checksum=" + request.getParameter("checksum"), "");
			qs = qs.replace("checksum=" + request.getParameter("checksum") + "&", "");
			log.debug("query string after checksum removed: " + qs);
			String cs = DigestUtils.shaHex(callName + qs + securitySalt());
			log.debug("our checksum: " + cs);
			if (cs == null || cs.equals(request.getAttribute("checksum")) == false){
				log.info("checksumError: request did not pass the checksum security check");
				return false;
			}
			log.debug("checksum ok: request passed the checksum security check");
			return true;
		}
		
		log.warn("Security is disabled in this service. Make sure this is intentional.");
		return true;
	}
	
	private static String securitySalt(){
		return dynamicConferenceService.getSecuritySalt();
	}
	
}
