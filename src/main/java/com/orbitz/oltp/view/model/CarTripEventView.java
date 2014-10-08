package com.orbitz.oltp.view.model;

public class CarTripEventView extends TripEventView {
	protected String eventName;
	protected String eventLocation;
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventLocation() {
		return eventLocation;
	}
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}
}
