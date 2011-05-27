package org.bigbluebutton.api.conference;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.bigbluebutton.api.Create;
import org.bigbluebutton.api.xml.XMLConverter;
import org.bigbluebutton.conference.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicConferenceService {
	Logger log = LoggerFactory.getLogger(Create.class);
	
	private static DynamicConferenceService instance = null;
	
	static boolean transactional = false;
	boolean serviceEnabled = false;
	private String apiVersion;
	String securitySalt;
	int minutesElapsedBeforeMeetingExpiration = 60;
	private String defaultWelcomeMessage;
	private String defaultDialAccessNumber;
	private String testVoiceBridge;
	private String testConferenceMock;
	
	// TODO: need to remove use of DynamicConference and make it use "Room.groovy" instead
	//				so that both apps and web are using common domain objects and we don't map between them
	private final Map<String, Room> roomsByToken;
	private final Map<String, DynamicConference> confsByMtgID;
	private final Map<String, String> tokenMap;
	
	private DynamicConferenceService(){
		confsByMtgID = new ConcurrentHashMap<String, DynamicConference>();
		tokenMap = new DualHashBidiMap();
		roomsByToken = new ConcurrentHashMap<String, Room>();
		
		//wait one minute to run, and run every five minutes
		TimerTask task = new DynamicConferenceServiceCleanupTimerTask(this);
		new Timer("api-cleanup", true).scheduleAtFixedRate(task, 60000, 30000);
	}
	
	public static DynamicConferenceService getInstance(){
		if (instance == null){
			instance = new DynamicConferenceService();
		}
		return instance;
	}
	
	public void cleanupOldConferences(){
		log.info("Cleaning out old conferences");
		for (DynamicConference conf :  confsByMtgID.values()){
			boolean remove = false;
			if (conf.isRunning()){
				log.info("Meeting [" + conf.getMeetingID() + "] is running - not cleaning it out");
				//don't remove if running
				continue;
			}
			
			long millisSinceStored = conf.getStoredTime() == null ? -1 : (System.currentTimeMillis() - conf.getStoredTime().getTime());
			long millisSinceEnd = conf.getEndTime() == null ? -1 : (System.currentTimeMillis() - conf.getEndTime().getTime());
			if (conf.getStartTime() != null && millisSinceEnd > (minutesElapsedBeforeMeetingExpiration * 60000)){
				log.info("Removing meeting because it started, ended, and is past the max expiration");
				remove = true;
			} else if (conf.getEndTime() == null && millisSinceStored > (minutesElapsedBeforeMeetingExpiration * 60000)){
				log.info("Removing meeting because it was stored, but never started [stored " + millisSinceStored + " millis ago]");
				remove = true;
			}
			
			if (remove){
				confsByMtgID.remove(conf.getMeetingID());
				roomsByToken.remove(conf.getMeetingToken());
				tokenMap.remove(conf.getMeetingToken());
			} else{
				log.info("Not removing meeting [" + conf.getMeetingID() + "]");
			}
		}
	}
	
	public Collection<DynamicConference> getAllConferences(){
		return Collections.unmodifiableCollection(confsByMtgID.values());
	}
	
	public void storeConference(DynamicConference conf) {
		conf.setStoredTime(new Date());
		confsByMtgID.put(conf.getMeetingID(), conf);
		tokenMap.put(conf.getMeetingToken(), conf.getMeetingID());
	}
	
	public Room getRoomByMeetingId(String meetingID){
		if (meetingID == null){
			return null;
		}
		String token = tokenMap.get(meetingID);
		if (token == null){
			return null;
		}
		return roomsByToken.get(token);
	}
	
	public DynamicConference getConferenceByMeetingID(String meetingID){
		if (meetingID == null){
			return null;
		}
		return confsByMtgID.get(meetingID);
	}
	
	public DynamicConference getConferenceByToken(String token){
		if (token == null){
			return null;
		}
		String mtgID = tokenMap.get(token);
		if (mtgID == null){
			return null;
		}
		return confsByMtgID.get(mtgID);
	}
	
	public boolean isMeetingWithVoiceBridgeExist(String voiceBridge){
		Collection<DynamicConference> confs = confsByMtgID.values();
		for (DynamicConference c : confs){
			if (voiceBridge == c.getVoiceBridge()){
				log.debug("Found voice bridge " + voiceBridge);
				return true;
			}
		}
		log.debug("could not find voice bridge " + voiceBridge);
		return false;
	}
	
	//these methods called by spring integration
	public void conferenceStarted(Room room){
		log.debug("conference started: " + room.getName());
		participantsUpdated(room);
	}
	
	public void conferenceEnded(Room room){
		log.debug("conference ended: " + room.getName());
	}
	
	public void participantsUpdated(Room room){
		log.debug("participants updated: " + room.getName());
		roomsByToken.put(room.getName(), room);
	}
	//end spring integration methods

	public void setTestVoiceBridge(String testVoiceBridge) {
		this.testVoiceBridge = testVoiceBridge;
	}

	public String getTestVoiceBridge() {
		return testVoiceBridge;
	}

	public void setTestConferenceMock(String testConferenceMock) {
		this.testConferenceMock = testConferenceMock;
	}

	public String getTestConferenceMock() {
		return testConferenceMock;
	}

	public void setDefaultWelcomeMessage(String defaultWelcomeMessage) {
		this.defaultWelcomeMessage = defaultWelcomeMessage;
	}

	public String getDefaultWelcomeMessage() {
		return defaultWelcomeMessage;
	}

	public void setDefaultDialAccessNumber(String defaultDialAccessNumber) {
		this.defaultDialAccessNumber = defaultDialAccessNumber;
	}

	public String getDefaultDialAccessNumber() {
		return defaultDialAccessNumber;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getApiVersion() {
		return apiVersion;
	}
	
	public String getSecuritySalt() {
		return this.securitySalt;
	}
}
