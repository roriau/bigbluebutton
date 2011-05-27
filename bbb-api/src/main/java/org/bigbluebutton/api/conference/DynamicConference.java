package org.bigbluebutton.api.conference;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

public class DynamicConference {
	Date dateCreated;
	Date lastUpdated;
	String createdBy;
	String updatedBy;
	private String name;
	Integer conferenceNumber;
	
	Date storedTime;
	Date startTime;
	Date endTime;
	
	boolean forciblyEnded = false;
	
	String meetingID;
	String meetingToken;
	private String voiceBridge;
	private String webVoiceConf;
	private String moderatorPassword;
	private String attendeePassword;
	private String welcome;
	private String logoutUrl;
	int maxParticipants;
	
	public DynamicConference(){}
	
	public DynamicConference(String name, String meetingID, String attendeePW, String moderatorPW, Integer maxParticipants) {
		this.setName(name);
		this.meetingID = StringUtils.isEmpty(meetingID) ? "" : meetingID;
		this.setAttendeePassword(attendeePW == null ? createPassword() : attendeePW);
		this.setModeratorPassword(moderatorPW == null ? createPassword() : moderatorPW);
		this.maxParticipants = (maxParticipants == null || maxParticipants < 0) ? -1 : maxParticipants;
	}
	
	public static String createMeetingToken(){
		return UUID.randomUUID().toString();
	}
	
	private String createPassword(){
		return RandomStringUtils.randomAlphanumeric(8).toLowerCase();
	}
	
	public boolean isRunning(){
		boolean running = startTime != null && endTime == null;
		return running;
	}
	
	public String toString() {
		return "DynamicConference: " + this.meetingToken + "[" + this.meetingID + "|" + this.getVoiceBridge() + "]:" + this.getName();
	}

	public String getMeetingID() {
		// TODO Auto-generated method stub
		return this.getMeetingID();
	}
	
	public String getMeetingToken(){
		return this.meetingToken;
	}
	
	public void setMeetingToken(String meetingToken){
		this.meetingToken = meetingToken;
	}
	
	public Date getStoredTime(){
		return this.storedTime;
	}
	
	public void setStoredTime(Date storedTime){
		this.storedTime = storedTime;
	}
	
	public Date getEndTime(){
		return this.endTime;
	}
	
	public Date getStartTime(){
		return this.startTime;
	}

	public void setAttendeePassword(String attendeePassword) {
		this.attendeePassword = attendeePassword;
	}

	public String getAttendeePassword() {
		return attendeePassword;
	}

	public void setModeratorPassword(String moderatorPassword) {
		this.moderatorPassword = moderatorPassword;
	}

	public String getModeratorPassword() {
		return moderatorPassword;
	}

	public void setVoiceBridge(String voiceBridge) {
		this.voiceBridge = voiceBridge;
	}

	public String getVoiceBridge() {
		return voiceBridge;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}

	public String getWelcome() {
		return welcome;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isForciblyEnded() {
		return this.forciblyEnded;
	}
	
	public void setForciblyEnded(boolean isForciblyEnded) {
		this.forciblyEnded = isForciblyEnded;
	}

	public void setWebVoiceConf(String webVoiceConf) {
		this.webVoiceConf = webVoiceConf;
	}

	public String getWebVoiceConf() {
		return webVoiceConf;
	}

}
