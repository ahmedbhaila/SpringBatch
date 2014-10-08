package com.orbitz.oltp.view.model;

public abstract class TripEventView {
	protected String tripType;
	protected String tripTitle;
	protected String tripStartTime;
	protected String tripEndTime;
	protected String tripImage;
	public String getTripType() {
		return tripType;
	}
	public void setTripType(String tripType) {
		this.tripType = tripType;
	}
	public String getTripTitle() {
		return tripTitle;
	}
	public void setTripTitle(String tripTitle) {
		this.tripTitle = tripTitle;
	}
	public String getTripStartTime() {
		return tripStartTime;
	}
	public void setTripStartTime(String tripStartTime) {
		this.tripStartTime = tripStartTime;
	}
	public String getTripEndTime() {
		return tripEndTime;
	}
	public void setTripEndTime(String tripEndTime) {
		this.tripEndTime = tripEndTime;
	}
	public String getTripImage() {
		return tripImage;
	}
	public void setTripImage(String tripImage) {
		this.tripImage = tripImage;
	}
}
