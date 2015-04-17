package com.zunisoft.utility;

import com.badlogic.gdx.scenes.scene2d.Event;

public class MessageEvent extends Event {
	private int message;
	public MessageEvent(int message) {
		this.message = message;
	}
	public int getMessage() {
		return message;
	}
}
