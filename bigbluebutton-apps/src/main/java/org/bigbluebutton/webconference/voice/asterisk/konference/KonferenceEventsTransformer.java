/*
 * BigBlueButton - http://www.bigbluebutton.org
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
 * Author: Richard Alam <ritzalam@gmail.com>
 * 
 * $Id: $
 */
package org.bigbluebutton.webconference.voice.asterisk.konference;

import org.bigbluebutton.webconference.voice.asterisk.konference.events.KonferenceEvent;
import org.bigbluebutton.webconference.voice.asterisk.konference.events.ConferenceJoinEvent;
import org.bigbluebutton.webconference.voice.asterisk.konference.events.ConferenceLeaveEvent;
import org.bigbluebutton.webconference.voice.asterisk.konference.events.ConferenceMemberMuteEvent;
import org.bigbluebutton.webconference.voice.asterisk.konference.events.ConferenceMemberUnmuteEvent;
import org.bigbluebutton.webconference.voice.asterisk.konference.events.ConferenceStateEvent;
import org.bigbluebutton.webconference.voice.events.ConferenceEvent;
import org.bigbluebutton.webconference.voice.events.ConferenceEventListener;
import org.bigbluebutton.webconference.voice.events.ConferenceEventSender;
import org.bigbluebutton.webconference.voice.events.ParticipantJoinedEvent;
import org.bigbluebutton.webconference.voice.events.ParticipantLeftEvent;
import org.bigbluebutton.webconference.voice.events.ParticipantMutedEvent;
import org.bigbluebutton.webconference.voice.events.ParticipantTalkingEvent;

/**
 * This class transforms AppKonference events into BigBlueButton Voice
 * Conference Events.
 * 
 * @author Richard Alam
 *
 */
public class KonferenceEventsTransformer {
	
	//private ConferenceEventListener conferenceEventListener;
	private ConferenceEventSender conferenceEventSender;
	/*
	 * Transforms AppKonferenceEvents into BBB Voice Conference Events.
	 * Return UnknownConferenceEvent if unable to transform the event.
	 */
	public void transform(KonferenceEvent event) {	
		ConferenceEvent confevt=null;
		if (event instanceof ConferenceJoinEvent) {
			ConferenceJoinEvent cj = (ConferenceJoinEvent) event;
			confevt = new ParticipantJoinedEvent(cj.getMember(), cj.getConferenceName(),
					cj.getCallerID(), cj.getCallerIDName(), cj.getMuted(), cj.getSpeaking());
			//conferenceEventListener.handleConferenceEvent(pj);
			
		} else if (event instanceof ConferenceLeaveEvent) {
			ConferenceLeaveEvent cl = (ConferenceLeaveEvent) event;
			confevt = new ParticipantLeftEvent(cl.getMember(), cl.getConferenceName());
			//conferenceEventListener.handleConferenceEvent(pl);
		} else if (event instanceof ConferenceMemberMuteEvent) {
			ConferenceMemberMuteEvent cmm = (ConferenceMemberMuteEvent) event;
			confevt = new ParticipantMutedEvent(cmm.getMemberId(), cmm.getConferenceName(), true);
			//conferenceEventListener.handleConferenceEvent(pm);
		} else if (event instanceof ConferenceMemberUnmuteEvent) {
			ConferenceMemberUnmuteEvent cmu = (ConferenceMemberUnmuteEvent) event;
			confevt = new ParticipantMutedEvent(cmu.getMemberId(), cmu.getConferenceName(), false);
			//conferenceEventListener.handleConferenceEvent(pm);
		} else if (event instanceof ConferenceStateEvent) {
			ConferenceStateEvent cse = (ConferenceStateEvent) event;
			boolean talking = "speaking".equals(cse.getState())? true : false;
			confevt = new ParticipantTalkingEvent(cse.getMemberId(), cse.getConferenceName(), talking);
			//conferenceEventListener.handleConferenceEvent(pt);
		}
		this.conferenceEventSender.produce(confevt);
	}

	//public void setConferenceEventListener(ConferenceEventListener listener) {
		//this.conferenceEventListener = listener;
	//}
	public void setConferenceEventSender(ConferenceEventSender sender) {
		this.conferenceEventSender = sender;
	}
}
