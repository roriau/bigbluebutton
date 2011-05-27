package org.bigbluebutton.api.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class VersionResponse {
	public String version;
	public String returncode;
	
	public VersionResponse(String returncode, String version){
		this.version = version;
		this.returncode = returncode;
	}
}
