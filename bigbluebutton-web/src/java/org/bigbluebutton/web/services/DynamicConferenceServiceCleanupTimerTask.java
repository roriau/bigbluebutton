package org.bigbluebutton.web.services;

import java.util.Timer;
import java.util.TimerTask;

public class DynamicConferenceServiceCleanupTimerTask {

	private final MeetingServiceImp service;
	private final Timer cleanupTimer;
	
	public DynamicConferenceServiceCleanupTimerTask(MeetingServiceImp svc) {
		this.service = svc;
		
		cleanupTimer = new Timer("bbb-api-cleanup", true);
		cleanupTimer.scheduleAtFixedRate(new CleanupTask(), 60000, 300000);		
	}
	
	class CleanupTask extends TimerTask {
        public void run() {
        	service.cleanupOldMeetings();
        }
    }
}
