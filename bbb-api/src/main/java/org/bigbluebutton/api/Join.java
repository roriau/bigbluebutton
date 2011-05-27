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

import org.apache.commons.lang.RandomStringUtils;
import org.bigbluebutton.api.conference.DynamicConference;
import org.bigbluebutton.api.conference.DynamicConferenceService;
import org.bigbluebutton.api.util.Invalid;
import org.bigbluebutton.api.util.Security;
import org.bigbluebutton.api.xml.JoinResponse;
import org.bigbluebutton.api.xml.StandardResponse;
import org.bigbluebutton.api.xml.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class Join
 */
public class Join implements Filter {
	
	private static final String CALL_NAME = "join";
	
	private static final String ROLE_MODERATOR = "MODERATOR";
	private static final String ROLE_ATTENDEE = "VIEWER";
	
	private static final Integer SESSION_TIMEOUT = 10800;  // 3 hours
	
	static final Logger log = LoggerFactory.getLogger(Join.class);
	static final DynamicConferenceService dynamicConferenceService = DynamicConferenceService.getInstance();
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
    /**
     * Default constructor. 
     */
    public Join() {
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
		
		String fullName = httpReq.getParameter("fullName");
		if (fullName == null){
			Invalid.invalid("missingParamFullName", "You must specify a name for the attendee who will be joining the meeting.", httpRes);
		}
		
		String webVoice = httpReq.getParameter("webVoiceConf");
		String mtgID = httpReq.getParameter("meetingID");
		String attPW = httpReq.getParameter("password");
		boolean redirectImm = parseBoolean(httpReq.getParameter("redirectImmediately"));
		String externUserID = httpReq.getParameter("userID");
		if ((externUserID == null) || (externUserID == "")){
			externUserID = RandomStringUtils.randomAlphanumeric(12).toLowerCase();
		}
		
		// check for existing
		DynamicConference conf = dynamicConferenceService.getConferenceByMeetingID(mtgID);
		if (conf == null){
			Invalid.invalid("invalidMeetingIdentifier", "The meeting ID that you supplied did not match any existing meetings", httpRes);
		}
		
		if (conf.isForciblyEnded()){
			Invalid.invalid("meetingForciblyEnded", 
					"You can not re-join a meeting that has already been forcibly ended.  However, once the meeting is removed from memory (according to the timeout configured on this server, you will be able to once again create a meeting with the same meeting ID", 
					httpRes);
		}
		
		String role = null;
		if (conf.getModeratorPassword().equals(attPW)){
			role = ROLE_MODERATOR;
		} else if(conf.getModeratorPassword().equals(attPW)){
			role = ROLE_ATTENDEE;
		}
		
		if (role == null){
			Invalid.invalid("invalidPassword", 
					"You either did not supply a password or the password supplied is neither the attendee or moderator password for this conference.", 
					httpRes);
		}
		
		conf.setWebVoiceConf(webVoice == null || webVoice == "" ? conf.getVoiceBridge() : webVoice);
		
		//TODO success....
		log.debug("join successful - setting session parameters and redirecting to join");
		HttpSession session = httpReq.getSession();
		session.setAttribute("conferencename", conf.getMeetingID());
		session.setAttribute("meetingID", conf.getMeetingID());
		session.setAttribute("externUserID", externUserID);
		session.setAttribute("fullname", fullName);
		session.setAttribute("role", role);
		session.setAttribute("conference", conf.getMeetingToken());
		session.setAttribute("room", conf.getMeetingToken());
		session.setAttribute("voicebridge", conf.getVoiceBridge());
		session.setAttribute("webvoiceconf", conf.getWebVoiceConf());
		session.setAttribute("mode", "LIVE");
		session.setAttribute("record", false);
		session.setAttribute("welcome", conf.getWelcome());
		
		session.setMaxInactiveInterval(SESSION_TIMEOUT);
		
		String hostURL = APIServlet.SERVER_URL;
		String redirectUrl =  hostURL + "/client/BigBlueButton.html";
		log.debug("join done, redirecting to " + redirectUrl);
		httpRes.sendRedirect(redirectUrl);

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	private boolean parseBoolean(Object o){
		if (o instanceof Number){
			return ((Number) o).intValue() == 1;
		}
		return false;
	}

}
