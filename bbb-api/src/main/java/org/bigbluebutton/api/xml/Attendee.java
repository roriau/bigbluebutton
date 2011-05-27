package org.bigbluebutton.api.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("attendee")
public class Attendee {
	private String userID;
	private String fullName;
	private String role;
	
	public Attendee(String userID, String fullName, String role){
		this.userID = userID;
		this.fullName = fullName;
		this.role = role;
	}
}
