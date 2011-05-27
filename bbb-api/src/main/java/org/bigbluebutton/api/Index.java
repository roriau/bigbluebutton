package org.bigbluebutton.api;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bigbluebutton.api.conference.DynamicConferenceService;
import org.bigbluebutton.api.xml.StandardResponse;
import org.bigbluebutton.api.xml.VersionResponse;
import org.bigbluebutton.api.xml.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class Index
 */
public class Index implements Filter {
	
	static final Logger log = LoggerFactory.getLogger(Join.class);
	static final DynamicConferenceService dynamicConferenceService = DynamicConferenceService.getInstance();
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
    /**
     * Default constructor. 
     */
    public Index() {
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
		
		log.debug("Entered / (index)");
		httpRes.addHeader("Cache-Control", "no-cache");
		httpRes.setContentType("text/xml");
		VersionResponse responseBody = new VersionResponse(StandardResponse.RESP_CODE_SUCCESS, dynamicConferenceService.getApiVersion());
		httpRes.getWriter().println(xmlConverter.xml().toXML(responseBody));

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
