package org.bigbluebutton.api.conference;

import org.bigbluebutton.conference.Room;
import org.springframework.integration.annotation.Gateway;

public interface IApiConferenceEventListener {
	
	@Gateway(requestChannel="endMeetingRequest")
	void endMeetingRequest(Room room);
}
