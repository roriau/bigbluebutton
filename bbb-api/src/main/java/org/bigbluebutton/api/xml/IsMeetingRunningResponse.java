package org.bigbluebutton.api.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class IsMeetingRunningResponse {
	
	private String returncode;
	private boolean isRunning;
	
	public IsMeetingRunningResponse(String returncode, boolean isRunning){
		this.isRunning = isRunning;
		this.returncode = returncode;
	}

}
