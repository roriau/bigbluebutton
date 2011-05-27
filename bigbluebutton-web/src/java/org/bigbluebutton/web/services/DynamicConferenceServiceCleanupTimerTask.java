package org.bigbluebutton.web.services;

import java.util.TimerTask;

public class DynamicConferenceServiceCleanupTimerTask extends TimerTask {

	private final IMeetingService service;
	
	public DynamicConferenceServiceCleanupTimerTask(IMeetingService svc) {
		this.service = svc;
	}
	
	@Override
	public void run() {
		service.cleanupOldMeetings();
	}

}
