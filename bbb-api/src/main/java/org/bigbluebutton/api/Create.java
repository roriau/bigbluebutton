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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.bigbluebutton.api.conference.DynamicConference;
import org.bigbluebutton.api.conference.DynamicConferenceService;
import org.bigbluebutton.api.util.Invalid;
import org.bigbluebutton.api.util.Security;
import org.bigbluebutton.api.xml.CreateResponse;
import org.bigbluebutton.api.xml.StandardResponse;
import org.bigbluebutton.api.xml.XMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class Create
 */
public class Create implements Filter {

	private static final String CALL_NAME = "create";
	
	public static final String DIAL_NUM = "/%%DIALNUM%%/";
	public static final String CONF_NUM = "/%%CONFNUM%%/";
	public static final String CONF_NAME = "/%%CONFNAME%%/";
	
	static final Logger log = LoggerFactory.getLogger(Create.class);
	static final DynamicConferenceService dynamicConferenceService = DynamicConferenceService.getInstance();
	static final XMLConverter xmlConverter = XMLConverter.getInstance();
	
    /**
     * Default constructor. 
     */
    public Create() {
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
		
		String name = httpReq.getParameter("name");
		if (name == null){
			Invalid.invalid("missingParamName", "You must specify a name for the meeting.", httpRes);
		}
		
		String mtgID = httpReq.getParameter("meetingID");
		if (StringUtils.isEmpty(mtgID)){
			Invalid.invalid("missingParamMeetingID", "You must specify a meeting ID for the meeting.", httpRes);
		}
		log.debug("passed parameter validation - creating conference");
		String attPW = httpReq.getParameter(httpReq.getParameter("attendeePW"));
		String modPW = httpReq.getParameter("moderatorPW");
		String voiceBr = httpReq.getParameter("voiceBridge");
		String welcomeMessage = httpReq.getParameter("welcome");
		String dialNumber = httpReq.getParameter("dialNumber");
		String logoutUrl = httpReq.getParameter("logoutURL");
		
		Integer maxParts = -1;
		try{
			maxParts = Integer.parseInt(httpReq.getParameter("maxParticipants"));
		} catch (Exception e){/* num participants not specified, do nothing */}
		String mmRoom = httpReq.getParameter("meetmeRoom");
		String mmServer = httpReq.getParameter("meetmeServer");
		
		//check for existing
		DynamicConference existing = dynamicConferenceService.getConferenceByMeetingID(mtgID);
		if (existing != null){
			log.debug("Existing conference found");
			if (existing.getAttendeePassword().equals(attPW) && existing.getModeratorPassword().equals(modPW)){
				//trying to create a conference a second time
				//return success, but give extra info
				uploadDocuments(existing, httpReq, httpRes);
				respondWithConference(existing, httpRes, "duplicateWarning", "This conference was already in existence and may currently be in progress.");
			} else{
				//enfore meetingID unique-ness
				Invalid.invalid("idNotUnique", "A meeting already exists with that meeting ID.  Please use a different meeting ID.", httpRes);
			}
			return;
		}
		if (StringUtils.isEmpty(attPW)){
			attPW = RandomStringUtils.randomAlphabetic(8);
		}
		if (StringUtils.isEmpty(modPW)){
			modPW = RandomStringUtils.randomAlphabetic(8);
		}
		DynamicConference conf = new DynamicConference(name, mtgID, attPW, modPW, maxParts);
		conf.setVoiceBridge(voiceBr == null || voiceBr == "" ? mtgID : voiceBr);
		
		if ((dynamicConferenceService.getTestVoiceBridge() != null) && (conf.getVoiceBridge() == dynamicConferenceService.getTestVoiceBridge())){
			if (dynamicConferenceService.getTestConferenceMock() != null){
				conf.setMeetingToken(dynamicConferenceService.getTestConferenceMock());
			} else{
				log.warn("Cannot set test conference because it is not set in bigbluebutton.properties");
			}
		}
		
		if ((logoutUrl != null) || (logoutUrl != "")){
			conf.setLogoutUrl(logoutUrl);
		}
		
		if (welcomeMessage == null || welcomeMessage == ""){
			welcomeMessage = dynamicConferenceService.getDefaultWelcomeMessage();
		}
		
		if ((dialNumber == null) || (dialNumber == "")){
			welcomeMessage = dynamicConferenceService.getDefaultWelcomeMessage();
		}
		
		if ((dialNumber == null) || (dialNumber == "")){
			dialNumber = dynamicConferenceService.getDefaultDialAccessNumber();
		}
		
		if (welcomeMessage != null || welcomeMessage != ""){
			log.debug("Substituting keywords");
			
			if ((dialNumber != null) || (dialNumber != "")){
				welcomeMessage = welcomeMessage.replaceAll(DIAL_NUM, dialNumber);
			}
			welcomeMessage = welcomeMessage.replaceAll(CONF_NUM, conf.getVoiceBridge());
			welcomeMessage = welcomeMessage.replaceAll(CONF_NAME, conf.getName());
		}
		
		conf.setWelcome(welcomeMessage);
		
		log.debug("Conference created: " + conf);
		//TODO support voiceBridge and voiceServer
		
		//success!
		uploadDocuments(conf, httpReq, httpRes);
		dynamicConferenceService.storeConference(conf);
		respondWithConference(conf, httpRes, null, null);
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		log.debug("/create filter started : " + fConfig.toString());
	}
	
	private void uploadDocuments(DynamicConference conf, HttpServletRequest request, HttpServletResponse response){
		log.debug("ApiController#uploadDocuments: " + conf.getMeetingID());
		
		//TODO - Plain Java chunk streaming
		//String requestBody = requestBody.geti
	}
	
	private void respondWithConference(DynamicConference conf, HttpServletResponse response, String msgKey, String msg){
		try{
			response.addHeader("Cache-Control", "no-cache");
			response.setContentType("text/xml");
			String messageKey = msgKey == null ? "" :msgKey;
			String message = msg == null ? "" : msg;
			CreateResponse responseBody = new CreateResponse(StandardResponse.RESP_CODE_SUCCESS, messageKey, message, 
													conf.getMeetingID(), conf.getAttendeePassword(), conf.getModeratorPassword(), conf.isForciblyEnded());
			response.getWriter().println(xmlConverter.xml().toXML(responseBody));
		} catch (IOException e){
			throw new RuntimeException("Could not respond with conference: " + conf.toString());
		}
		
	}

}
