package org.bigbluebutton.web.services;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
import org.bigbluebutton.api.domain.Meeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeetingService implements IMeetingService {
	private static Logger log = LoggerFactory.getLogger(MeetingService.class);
	
	private final Map<String, Meeting> confsByMtgID;
	private int minutesElapsedBeforeMeetingExpiration = 60;
	
	public MeetingService() {
		confsByMtgID = new ConcurrentHashMap<String, Meeting>();

		// wait one minute to run, and run every five minutes:
		TimerTask task = new DynamicConferenceServiceCleanupTimerTask((IMeetingService) this);
		new Timer("api-cleanup", true).scheduleAtFixedRate(task, 60000, 300000);
		// PS - <rant> I hate Groovy - no inline (anonymous or inner) class support (until 1.7)?  Come on!  Closures aren't the be-all-end-all </rant>		
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

	@Override
	public Collection<Meeting> getAllMeetings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeMeeting(Meeting conf) {
		// TODO Auto-generated method stub

	}

	@Override
	public Meeting getMeetingByMeetingID(String meetingID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMeetingWithVoiceBridgeExist(String voiceBridge) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setMinutesElapsedBeforeMeetingExpiration(int minutes) {
		minutesElapsedBeforeMeetingExpiration = minutes;
	}

}
