package org.bigbluebutton.web.services;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
import org.bigbluebutton.api.domain.Meeting;
import org.bigbluebutton.api.domain.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeetingServiceImp implements MeetingService {
	private static Logger log = LoggerFactory.getLogger(MeetingServiceImp.class);
	
	private final Map<String, Meeting> confsByMtgID;
	private int minutesElapsedBeforeMeetingExpiration = 60;
	
	public MeetingServiceImp() {
		confsByMtgID = new ConcurrentHashMap<String, Meeting>();
	}
	
	@Override
	public void cleanupOldMeetings() {
		log.debug("Cleaning out old conferences");
		for (Meeting conf : confsByMtgID.values()) {
			boolean remove = false;
			if (conf.isRunning()) {
				log.debug( "Meeting [" + conf.getMeetingID() + "] is running - not cleaning it out");
				// won't remove one that's running
				continue;
			}
			
			long now = System.currentTimeMillis();
			long millisSinceStored = conf.getStoredTime() == null ? -1 : (now - conf.getStoredTime().getTime());
			long millisSinceEnd = conf.getEndTime() == null ? -1 : (now - conf.getEndTime().getTime());
			if (conf.getStartTime() != null && millisSinceEnd > (minutesElapsedBeforeMeetingExpiration * 60000)) {
				log.debug("Removing meeting because it started, ended, and is past the max expiration");
				remove = true;
			} else if (conf.getEndTime() == null && millisSinceStored > (minutesElapsedBeforeMeetingExpiration * 60000)) {
				log.debug("Removing meeting because it was stored, but never started [stored " + millisSinceStored + " millis ago]");
				remove = true;
			}
			
			if (remove) {
				log.debug("Removing meeting [" + conf.getMeetingToken() + "]");
				confsByMtgID.remove(conf.getMeetingID());
			} else {
				log.debug("Not removing meeting [" + conf.getMeetingID() + "]");
			}
		}
	}

	public Collection getAllMeetings() {
		return confsByMtgID.isEmpty() ? Collections.emptySet() : Collections.unmodifiableCollection(confsByMtgID.values());
	}
	
	public void storeMeeting(Meeting conf) {
		conf.setStoredTime(new Date());
		confsByMtgID.put(conf.getMeetingID(), conf);
		if (conf.isRecord()) {
//			createMeetingRecord(conf);
		}
	}


	public Meeting getMeeting(String meetingID) {
		if (meetingID == null) {
			return null;
		}
		return (Meeting) confsByMtgID.get(meetingID);
	}
	
	
	public boolean isMeetingWithVoiceBridgeExist(String voiceBridge) {
		Collection<Meeting> confs = confsByMtgID.values();
		for (Meeting c : confs) {
	        if (voiceBridge == c.getVoiceBridge()) {
	        	return true;
	        }
		}
		return false;
	}
			
	public void conferenceStarted(String meetingID){
		Meeting conf = getMeeting(meetingID);
		if (conf != null) {
			conf.setStartTime(new Date());
			conf.setEndTime(null);
		}
	}
	public void conferenceEnded(String meetingID) {
		Meeting conf = getMeeting(meetingID);
		if (conf != null) {
			conf.setEndTime(new Date());
		}
	}
	
	public void participantsUpdatedJoin(String meetingID, String userid, String fullname, String role) {
		Participant dcp = new Participant(userid, fullname, role);
		Meeting conf = getMeeting(meetingID);
		if(conf != null){
			conf.addParticipant(dcp);
		}
	}
	
	public void participantsUpdatedRemove(String meetingID, String userid) {
		Meeting conf = getMeeting(meetingID);
		if(conf!=null){
			conf.removeParticipant(userid);
		}
	}
		
	public void setMinutesElapsedBeforeMeetingExpiration(int minutes) {
		minutesElapsedBeforeMeetingExpiration = minutes;
	}

}
