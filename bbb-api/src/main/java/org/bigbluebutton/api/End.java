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

import org.bigbluebutton.api.conference.DynamicConference;
import org.bigbluebutton.api.conference.DynamicConferenceService;
import org.bigbluebutton.api.conference.IApiConferenceEventListener;
import org.bigbluebutton.api.util.Invalid;
import org.bigbluebutton.api.xml.StandardResponse;
import org.bigbluebutton.api.xml.XMLConverter;
import org.bigbluebutton.conference.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class End
 */
public class End implements Filter {

	static final Logger log = LoggerFactory.getLogger(Join.class);
	static final DynamicConferenceService dynamicConferenceService = DynamicConferenceService.getInstance();
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
	IApiConferenceEventListener conferenceEventListener;
	
    /**
     * Default constructor. 
     */
    public End() {
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
		
		log.debug("entered /end");
		String mtgID = httpReq.getParameter("meetingID");
		String callPW = httpReq.getParameter("password");
		
		//check for existing
		DynamicConference conf = dynamicConferenceService.getConferenceByMeetingID(mtgID);
		Room room = dynamicConferenceService.getRoomByMeetingId(mtgID);
		
		if (conf == null || room == null){
			Invalid.invalid("notFound", "We could not find a meeting with that meeting ID - perhaps the meeting is not yet running?", httpRes);
			return;
		}
		
		if (conf.getModeratorPassword().equals(callPW) == false){
			Invalid.invalid("invalidPassword", "You must supply the moderator password for this call.", httpRes);
			return;
		}
		
		conf.setForciblyEnded(true);
		
		conferenceEventListener.endMeetingRequest(room);
		
		httpRes.addHeader("Cache-Control", "no-cache");
		httpRes.setContentType("text/xml");
		StandardResponse responseBody = new StandardResponse(StandardResponse.RESP_CODE_SUCCESS, "sentEndMeetingRequest", 
				"A request to end the meeting was sent.  Please wait a few seconds, and then use the getMeetingInfo or isMeetingRunning API calls to verify that it was ended.");
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
