package org.bigbluebutton.api.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class StandardResponse {

	public static final String RESP_CODE_FAILED = "FAILED";
	public static final String RESP_CODE_SUCCESS = "SUCCESS";
	
	private String returncode; 
	private String messagekey;
	private String message;
	
	public StandardResponse(String returncode, String messageKey, String message){
		this.returncode = returncode;
		this.messagekey = messageKey;
		this.message = message;
	}
}

/*
<response>
<returncode>FAILED</returncode>
<messageKey>checksumError</messageKey>
<message>You did not pass the checksum security check</message>
</response>
*/