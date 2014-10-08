package com.orbitz.oltp.view.model;

import java.util.Map;

public class TripDetails {
	protected String tripTitle;
	protected String orbitzRecordLocator;
	protected Map<String, String> recordLocators;
	protected Map<String, Map<String, TripEventView>> tripCards;
	protected String tripMessage;
	public String getTripMessage() {
		return tripMessage;
	}
	public void setTripMessage(String tripMessage) {
		this.tripMessage = tripMessage;
	}
	public String getTripTitle() {
		return tripTitle;
	}
	public void setTripTitle(String tripTitle) {
		this.tripTitle = tripTitle;
	}
	public String getOrbitzRecordLocator() {
		return orbitzRecordLocator;
	}
	public void setOrbitzRecordLocator(String orbitzRecordLocator) {
		this.orbitzRecordLocator = orbitzRecordLocator;
	}
	public Map<String, String> getRecordLocators() {
		return recordLocators;
	}
	public void setRecordLocators(Map<String, String> recordLocators) {
		this.recordLocators = recordLocators;
	}
	public Map<String, Map<String, TripEventView>> getTripCards() {
		return tripCards;
	}
	public void setTripCards(Map<String, Map<String, TripEventView>> tripCards) {
		this.tripCards = tripCards;
	}
}
