package com.zunisoft.utility;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class MessageListener implements EventListener {
	@Override
	public boolean handle(Event event) {
		if(event instanceof MessageEvent) {
			receivedMessage(((MessageEvent)event).getMessage() , event.getTarget());
		}
		return true;
	}
	protected void receivedMessage(int message, Actor actor) {
		
	}

}
