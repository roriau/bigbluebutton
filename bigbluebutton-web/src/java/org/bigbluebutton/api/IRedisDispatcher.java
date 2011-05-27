package org.bigbluebutton.api;

import org.bigbluebutton.api.domain.Meeting;

public interface IRedisDispatcher {
	public void createConferenceRecord(Meeting conf, String redisHost, int redisPort);
}
