package com.orbitz.oltp.app.view.model;

public class AirProductSlice implements ProductItinerarySortable{
	protected String location;
	protected String locationTime;
	protected String locationName;
	protected String carrier;
	protected String flightInfo;
	protected String eventDate;
	protected Long eventEpoch;
	public Long getEventEpoch() {
		return eventEpoch;
	}
	public void setEventEpoch(Long eventEpoch) {
		this.eventEpoch = eventEpoch;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocationTime() {
		return locationTime;
	}
	public void setLocationTime(String locationTime) {
		this.locationTime = locationTime;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getFlightInfo() {
		return flightInfo;
	}
	public void setFlightInfo(String flightInfo) {
		this.flightInfo = flightInfo;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	public String getEventDate() {
		return eventDate;
	}
}
