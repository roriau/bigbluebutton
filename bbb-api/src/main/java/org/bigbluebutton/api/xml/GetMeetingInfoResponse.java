package org.bigbluebutton.api.xml;

import java.util.Collection;
import java.util.Date;

import org.bigbluebutton.conference.Participant;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class GetMeetingInfoResponse extends StandardResponse {

	private String meetingID;
	private String attendeePW;
	private String moderatorPW;
	private boolean running;
	private boolean hasBeenForciblyEnded;
	private Date startTime;
	private Date endTime;
	private int participantCount;
	private int moderatorCount;
	private Attendee[] attendees;
	
	public GetMeetingInfoResponse(String returncode, String messageKey, String message, String meetingID, String attendeePW, String moderatorPW,
								  boolean running, boolean hasBeenForciblyEnded, Date startTime, Date endTime, int participantCount, int moderatorCount,
								  Collection<Participant> participants) {
		super(returncode, messageKey, message);
		
		attendees = new Attendee[participants.size()];
		int i = 0;
		for (Participant p : participants){
			attendees[i] = new Attendee(p.getExternUserID(), p.getName(), p.getRole());
			i++;
		}
		
		this.meetingID = meetingID;
		this.attendeePW = attendeePW;
		this.moderatorPW = moderatorPW;
		this.running = running;
		this.hasBeenForciblyEnded = hasBeenForciblyEnded;
		this.startTime = startTime;
		this.endTime = endTime;
		this.participantCount = participantCount;
		this.moderatorCount = moderatorCount;
	}

}
