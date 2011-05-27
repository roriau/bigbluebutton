package org.bigbluebutton.web.services;

import java.util.Collection;
import org.bigbluebutton.api.domain.Meeting;

public interface IMeetingService {
	public void cleanupOldMeetings();
	public Collection<Meeting> getAllMeetings();	
	public void storeMeeting(Meeting conf);	
	public Meeting getMeetingByMeetingID(String meetingID);	
	public boolean isMeetingWithVoiceBridgeExist(String voiceBridge);
}
