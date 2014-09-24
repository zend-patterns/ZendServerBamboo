package com.zend.zendserver.bamboo;

import java.awt.Event;

public class Tests {
	private EventListener listener;
	
	public void setListener(EventListener listener) {
        this.listener = listener;
    }
	
	public void collate() throws Exception {
		listener.fireEvent(new Event(listener, 0, listener));
	}
}
