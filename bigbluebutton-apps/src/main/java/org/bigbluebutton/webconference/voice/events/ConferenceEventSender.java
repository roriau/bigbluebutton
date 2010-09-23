package org.bigbluebutton.webconference.voice.events;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.bigbluebutton.conference.service.recorder.RecorderEventDispatcher;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ConferenceEventSender {
	private static Logger log = Red5LoggerFactory.getLogger( ConferenceEventSender.class, "bigbluebutton" );
	
	private JmsTemplate jmsTemplate;
	
	public ConferenceEventSender() {}

    public void setJmsTemplate(JmsTemplate jmsTemplate) 
    {
    	log.debug("setting jmstemplate");
        this.jmsTemplate = jmsTemplate;
    }

    public void produce(final ConferenceEvent object) {
        this.jmsTemplate.send(new MessageCreator() {
        	public Message createMessage(Session session) throws JMSException {
        		ObjectMessage om = session.createObjectMessage();
        		om.setObject(object);
        		return om;
        	}
        });
    }
}
