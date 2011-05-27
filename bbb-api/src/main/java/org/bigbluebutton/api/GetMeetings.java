package org.bigbluebutton.api;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bigbluebutton.api.conference.DynamicConference;
import org.bigbluebutton.api.conference.DynamicConferenceService;
import org.bigbluebutton.api.util.Security;
import org.bigbluebutton.api.xml.GetMeetingsResponse;
import org.bigbluebutton.api.xml.StandardResponse;
import org.bigbluebutton.api.xml.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class GetMeetings
 */
public class GetMeetings implements Filter {
	
	private static final String CALL_NAME = "getMeetings";

	static final Logger log = LoggerFactory.getLogger(Join.class);
	static final DynamicConferenceService dynamicConferenceService = DynamicConferenceService.getInstance();
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
    /**
     * Default constructor. 
     */
    public GetMeetings() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)){
			throw new RuntimeException("Only HTTP is supported by the BigBlueButton API");
		}
		
		HttpServletRequest httpReq = (HttpServletRequest)request;
		HttpServletResponse httpRes = (HttpServletResponse)response;
		
		if (!Security.doChecksumSecurity(CALL_NAME, httpReq, httpRes)) return;
		
		log.debug("entered /getMeetings");
		
		Collection<DynamicConference> confs = dynamicConferenceService.getAllConferences();
		
		if (confs == null || confs.size() == 0){
			httpRes.addHeader("Cache-Control", "no-cache");
			httpRes.setContentType("text/xml");
			GetMeetingsResponse responseBody = new GetMeetingsResponse(StandardResponse.RESP_CODE_SUCCESS, "noMeetings", "no meetings were found on this server", null);
			httpRes.getWriter().println(xmlConverter.xml().toXML(responseBody));
		} else{
			httpRes.addHeader("Cache-Control", "no-cache");
			httpRes.setContentType("text/xml");
			GetMeetingsResponse responseBody = new GetMeetingsResponse(StandardResponse.RESP_CODE_SUCCESS, "", "", confs);
			httpRes.getWriter().println(xmlConverter.xml().toXML(responseBody));
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
