package org.bigbluebutton.api.xml;

import com.thoughtworks.xstream.XStream;

public final class XMLConverter {
	
	private static XMLConverter instance = null;
	
	private XStream xStream;
	
	private XMLConverter(){
		xStream = new XStream();
		//Process annotations for all the xml classes we need to convert to xml
		xStream.processAnnotations(StandardResponse.class);
	}
	
	public static XMLConverter getInstance(){
		if (instance == null){
			instance = new XMLConverter();
		}
		return instance;
	}
	
	public XStream xml(){
		return this.xStream;
	}
	
}
