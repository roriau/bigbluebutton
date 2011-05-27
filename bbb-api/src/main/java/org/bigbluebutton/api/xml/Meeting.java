package org.bigbluebutton.api.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("meeting")
public class Meeting {
	
	private String meetingID;
	private String attendeePW;
	private String moderatorPW;
	private boolean hasBeenForciblyEnded;
	private boolean isRunning;
	
	public Meeting(String meetingID, String attendeePW, String mdoeratorPW, boolean hasBeenForciblyEnded, boolean isRunning){
		this.meetingID = meetingID;
		this.moderatorPW = moderatorPW;
		this.hasBeenForciblyEnded = hasBeenForciblyEnded;
		this.isRunning = isRunning;
	}
	
}
