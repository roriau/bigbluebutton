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
import javax.servlet.http.HttpSession;

import org.bigbluebutton.api.conference.DynamicConferenceService;
import org.bigbluebutton.api.xml.JoinResponse;
import org.bigbluebutton.api.xml.StandardResponse;
import org.bigbluebutton.api.xml.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class Enter
 */
public class Enter implements Filter {

	static final Logger log = LoggerFactory.getLogger(Join.class);
	static final DynamicConferenceService dynamicConferenceService = DynamicConferenceService.getInstance();
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
    /**
     * Default constructor. 
     */
    public Enter() {
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
		
		log.debug("Entered /enter");
		
		HttpSession session = httpReq.getSession();
		String fname = (String)session.getAttribute("fullname");
		String rl = (String)session.getAttribute("role");
		String cnf = (String)session.getAttribute("conference");
		String rm = (String)session.getAttribute("room");
		String vb = (String)session.getAttribute("voicebridge");
		String wbv = (String)session.getAttribute("record");
		String rec = (String)session.getAttribute("record");
		String md = (String)session.getAttribute("mode");
		String confName = (String)session.getAttribute("conferencename");
		String welcomeMsg = (String)session.getAttribute("welcome");
		String meetID = (String)session.getAttribute("meetingID");
		String externUID = (String)session.getAttribute("externUserID");
		
		log.debug("After reading from session cookie");
		
		if (rm == null) {
			log.info("Could not find conference");
			httpRes.addHeader("Cache-Control", "no-cache");
			httpRes.setContentType("text/xml");
			StandardResponse responseBody = new StandardResponse(StandardResponse.RESP_CODE_FAILED, "", "Could not find conference " + 
					httpReq.getParameter("conference"));
		} else{
			log.debug("Found conference");
			httpRes.addHeader("Cache-Control", "no-cache");
			JoinResponse joinResponseBody = new JoinResponse(StandardResponse.RESP_CODE_SUCCESS ,fname, confName, meetID, externUID, 
															 rl, cnf, rm, vb, wbv, md, rec, welcomeMsg);
			httpRes.getWriter().println((xmlConverter.xml().toXML(joinResponseBody)));
		}
		
		log.debug("Leaving /enter");

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
