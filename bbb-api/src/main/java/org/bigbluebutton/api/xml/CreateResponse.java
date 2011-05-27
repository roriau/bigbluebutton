package org.bigbluebutton.api.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class CreateResponse extends StandardResponse {
	
	private String meetingID;
	private String attendeePW;
	private String moderatorPW;
	private boolean hasBeenForciblyEnded;
	
	public CreateResponse(String returncode, String messageKey, String message, String meetingID, 
							  String attendeePW, String moderatorPW, boolean hasBeenForciblyEnded) {
		super(returncode, messageKey, message);
		this.meetingID = meetingID;
		this.attendeePW = attendeePW;
		this.moderatorPW = moderatorPW;
		this.hasBeenForciblyEnded = hasBeenForciblyEnded;
	}

}
