package org.bigbluebutton.api.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class JoinResponse {
	
	private String returncode;
	private String fullname;
	private String confname;
	private String meetingID;
	private String externUserID;
	private String role;
	private String conference;
	private String room;
	private String voicebridge;
	private String webvoiceconf;
	private String mode;
	private String record;
	private String welcome;
	
	public JoinResponse(String returncode, String fullname, String confname, String meetingID, String externUserID, String role, String conference, 
						String room, String voicebridge, String webvoiceconf, String mode, String record, String welcome){
		
		this.returncode = returncode;
		this.fullname = fullname;
		this.confname = confname;
		this.meetingID = meetingID;
		this.externUserID = externUserID;
		this.role = role;
		this.conference = conference;
		this.room = room;
		this.voicebridge = voicebridge;
		this.webvoiceconf = webvoiceconf;
		this.mode = mode;
		this.record = record;
		this.welcome = welcome;
	}

}
