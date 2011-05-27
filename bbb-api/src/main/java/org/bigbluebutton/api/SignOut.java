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

import org.bigbluebutton.api.conference.DynamicConference;
import org.bigbluebutton.api.conference.DynamicConferenceService;
import org.bigbluebutton.api.xml.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class SignOut
 */
public class SignOut implements Filter {

	static final Logger log = LoggerFactory.getLogger(Join.class);
	static final DynamicConferenceService dynamicConferenceService = DynamicConferenceService.getInstance();
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
    /**
     * Default constructor. 
     */
    public SignOut() {
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
		
		String hostURL = APIServlet.LOGOUT_URL;
		log.debug("LogoutURL=" + hostURL);
		
		HttpSession session = httpReq.getSession();
		String meetingToken = (String)session.getAttribute("conference");
		DynamicConference conf = dynamicConferenceService.getConferenceByToken(meetingToken);
		if (conf != null){
			if ((conf.getLogoutUrl() != null) && (conf.getLogoutUrl() != "")){
				hostURL = conf.getLogoutUrl();
				log.debug("logoutURL has been set from API. Redirecting to server url " + hostURL);
			}
		}
		
		if (hostURL.isEmpty()){
			hostURL = APIServlet.SERVER_URL;
			log.debug("No logout url. Redirecting to server url " + hostURL);
		}
		
		//Log the user out of the application
		session.invalidate();
		log.debug("serverURL " + hostURL);
		httpRes.sendRedirect(hostURL);

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
