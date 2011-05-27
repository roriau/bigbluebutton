package org.bigbluebutton.api.conference;

import java.util.TimerTask;

public class DynamicConferenceServiceCleanupTimerTask extends TimerTask {

	private final DynamicConferenceService service;
	
	public DynamicConferenceServiceCleanupTimerTask(DynamicConferenceService svc) {
		this.service = svc;
	}
	
	@Override
	public void run() {
		service.cleanupOldConferences();
	}

}
