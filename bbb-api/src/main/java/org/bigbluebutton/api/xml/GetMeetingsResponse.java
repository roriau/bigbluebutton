package org.bigbluebutton.api.xml;

import java.util.Collection;

import org.bigbluebutton.api.conference.DynamicConference;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class GetMeetingsResponse extends StandardResponse {

	private Meeting[] meetings;
	public GetMeetingsResponse(String returncode, String messageKey, String message, Collection<DynamicConference> confs) {
		super(returncode, messageKey, message);
		
		int i=0;
		for (DynamicConference c : confs){
			meetings[i] = new Meeting(c.getMeetingID(), c.getAttendeePassword(), c.getModeratorPassword(), c.isForciblyEnded(), c.isRunning());
			i++;
		}
	}

}
