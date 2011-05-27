/* BigBlueButton - http://www.bigbluebutton.org
 * 
 * 
 * Copyright (c) 2008-2009 by respective authors (see below). All rights reserved.
 * 
 * BigBlueButton is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 3 of the License, or (at your option) any later 
 * version. 
 * 
 * BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with BigBlueButton; if not, If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Jeremy Thomerson <jthomerson@genericconf.com>
 * @version $Id: $
 */
package org.bigbluebutton.web.services

//import org.bigbluebutton.conference.Room
import java.util.concurrent.ConcurrentHashMap
import org.apache.commons.collections.bidimap.DualHashBidiMap
import java.util.*;
import java.util.concurrent.*;
import org.bigbluebutton.api.domain.Meeting;
import org.bigbluebutton.api.domain.Participant;
import org.bigbluebutton.api.IRedisDispatcher
import org.bigbluebutton.api.RedisDispatcherImp;
import org.bigbluebutton.web.services.MeetingServiceImp;

public class DynamicConferenceService {	
	static transactional = false
	def serviceEnabled = false
	
	def apiVersion;
	def securitySalt
	int minutesElapsedBeforeMeetingExpiration = 60
	def defaultWelcomeMessage
	def defaultDialAccessNumber
	def testVoiceBridge
	def testConferenceMock
	def recordingDir
	def recordingFile

	/** For record and playback **/
	def recordStatusDir
	def redisHost
	def redisPort
	
	IRedisDispatcher redisDispatcher
	MeetingService meetingService
	
	public DynamicConferenceService() {
		redisDispatcher = new RedisDispatcherImp();
		meetingService = new MeetingServiceImp();
	}
	
	void cleanupOldConferences() {
		println("Cleaning out old conferences");
		for (DynamicConference conf : confsByMtgID.values()) {
			boolean remove = false;
			if (conf.isRunning()) {
				println "Meeting [" + conf.getMeetingID() + "] is running - not cleaning it out"
				// won't remove one that's running
				continue;
			}
			
			long millisSinceStored = conf.getStoredTime() == null ? -1 : (System.currentTimeMillis() - conf.getStoredTime().getTime());
			long millisSinceEnd = conf.getEndTime() == null ? -1 : (System.currentTimeMillis() - conf.getEndTime().getTime());
			if (conf.getStartTime() != null && millisSinceEnd > (minutesElapsedBeforeMeetingExpiration * 60000)) {
				println("Removing meeting because it started, ended, and is past the max expiration");
				remove = true;
			} else if (conf.getEndTime() == null && millisSinceStored > (minutesElapsedBeforeMeetingExpiration * 60000)) {
				println("Removing meeting because it was stored, but never started [stored " + millisSinceStored + " millis ago]");
				remove = true;
			}
			
			if (remove) {
				println "Removing meeting [" + conf.getMeetingToken() + "]"
				confsByMtgID.remove(conf.getMeetingID());
				//roomsByToken.remove(conf.getMeetingToken());
				tokenMap.remove(conf.getMeetingToken());
			} else {
				println "Not removing meeting [" + conf.getMeetingID() + "]"
			}
		}
	}
	
	public Collection<Meeting> getAllMeetings() {
		return confsByMtgID.isEmpty() ? Collections.emptySet() : Collections.unmodifiableCollection(confsByMtgID.values());
	}
	
	public void storeMeeting(Meeting conf) {
		conf.setStoredTime(new Date());
		confsByMtgID.put(conf.getMeetingID(), conf);
		tokenMap.put(conf.getMeetingToken(), conf.getMeetingID());
		if (conf.isRecord()) {
			createMeetingRecord(conf);
		}
	}

	public void createMeetingRecord(Meeting conf) {
		String COLON = ":";
		println("Putting meeting info to redis for " + conf.getMeetingID() + " " + redisHost + ":" + redisPort);
		println("Conf name " + conf.getName() + " token " + conf.getMeetingToken());
		if (redisDispatcher == null) {
			println "Redis Dispatcher is NULL!!"
		} else {
			println "Redis Dispatcher is NOT NULL!!"
			if (redisHost == null) println "redisHost is null"
			else println redisHost
			if (redisPort == null) println "redisPort is null"
			else println redisPort
			redisDispatcher.createConferenceRecord(conf, redisHost, Integer.parseInt(redisPort))
		}
	}
	
	/*public Room getRoomByMeetingID(String meetingID) {
		if (meetingID == null) {
			return null;
		}
		String token = tokenMap.getKey(meetingID);
		if (token == null) {
			System.out.println("Cannot find token for meetingId " + meetingID)
			return null;
		}
		return roomsByToken.get(token);
	}*/
	
	public Meeting getMeetingByMeetingID(String meetingID) {
		if (meetingID == null) {
			return null;
		}
		return (Meeting) confsByMtgID.get(meetingID);
	}
	
	private Meeting getConferenceByToken(String token) {
		if (token == null) {
			return null;
		}
		String mtgID = tokenMap.get(token);
		if (mtgID == null) {
			return null;
		}
		return confsByMtgID.get(mtgID);
	}
	
	public boolean isMeetingWithVoiceBridgeExist(String voiceBridge) {
		Collection<Meeting> confs = confsByMtgID.values()
		for (Meeting c : confs) {
	        if (voiceBridge == c.voiceBridge) {
	        	log.debug "Found voice bridge $voiceBridge"
	        	return true
	        }
		}
		log.debug "could not find voice bridge $voiceBridge"
		return false
	}
		
	// these methods called by spring integration:
	/*public void conferenceStarted(Room room) {
		log.debug "conference started: " + room.getName();
		participantsUpdated(room);
		Meeting conf = getConferenceByToken(room.getName());
		if (conf != null) {
			conf.setStartTime(new Date());
			conf.setEndTime(null);
			log.debug "found conference and set start date"
		}
	}
	
	public void conferenceEnded(Room room) {
		log.debug "conference ended: " + room.getName();
		Meeting conf = getConferenceByToken(room.getName());
		if (conf != null) {
			conf.setEndTime(new Date());
			log.debug "found conference and set end date"
		}
	}
	
	public void participantsUpdated(Room room) {
		log.debug "participants updated: " + room.getName();
		System.out.println("participants updated: " + room.getName())
		//roomsByToken.put(room.getName(), room);
	}*/
	// end of spring integration-called methods
	
	//these methods are without using bbb-commons
	public void conferenceStarted2(String roomname){
		Meeting conf = getConferenceByToken(roomname);
		if (conf != null) {
			conf.setStartTime(new Date());
			conf.setEndTime(null);
			log.debug "redis: found conference and set start date"
		}
	}
	public void conferenceEnded2(String roomname) {
		log.debug "redis: conference ended: " + roomname;
		Meeting conf = getConferenceByToken(roomname);
		if (conf != null) {
			conf.setEndTime(new Date());
			log.debug "redis: found conference and set end date"
		}
	}
	
	public void participantsUpdatedJoin(String roomname, String userid, String fullname, String role) {
		log.debug "redis: participants updated join: " + roomname;
		Participant dcp = Participant(userid, fullname, role);
		Meeting conf = getConferenceByToken(roomname);
		if(conf != null){
			conf.addParticipant(dcp);
			log.debug "redis: added participant"
		}
	}
	
	public void participantsUpdatedStatus(String roomname, String userid, String status, String value) {
		log.debug "redis: participants updated join: " + roomname;
		DynamicConference conf = getConferenceByToken(roomname);
		if(conf!=null){
			for(DynamicConferenceParticipant dcp: conf.getParticipants()){
				if(dcp.getUserid().equalsIgnoreCase(userid))
					dcp.setStatus(status,value);
			}
			log.debug "redis: update status"
		}
	}
	
	public void participantsUpdatedLeft(String roomname, String userid) {
		log.debug "redis: participants updated remove: " + roomname;
		System.out.println("participants updated: " + roomname);
		Meeting conf = getConferenceByToken(roomname);
		if(conf!=null){
			conf.removeParticipant(userid);
			log.debug "redis: removed participant"
		}
	}
	
	//TODO: update accordinly
	public void processRecording(String meetingId) {
		System.out.println("enter processRecording " + meetingId);
		DynamicConference conf = getConferenceByToken(roomname);
		if (conf != null) {
			System.out.println("Number of participants in room " +conf.getNumberOfParticipants())
			if (conf.getNumberOfParticipants() == 0) {
				System.out.println("starting processRecording " + meetingId)
				// Run conversion on another thread.
				new Timer().runAfter(1000) {
					startIngestAndProcessing(meetingId)
				}		
			} else {
				System.out.println("Someone still in the room...not processRecording " + meetingId)
			}
		} else {
			System.out.println("Could not find room " + meetingId + " ... Not processing recording")
		}
	}
	
	private void startIngestAndProcessing(meetingId) {	
		String done = recordStatusDir + "/" + meetingId + ".done"
		log.debug( "Writing done file " + done)
		File doneFile = new File(done)
		if (!doneFile.exists()) {
			doneFile.createNewFile()
			if (!doneFile.exists())
				log.error("Failed to create " + done + " file.")
		} else {
			log.error(done + " file already exists.")
		}
	}	
}
