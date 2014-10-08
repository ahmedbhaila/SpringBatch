package com.orbitz.oltp.app.view.model;

import java.util.ArrayList;
import java.util.List;

public class CarProductView implements ProductItinerarySortable{
	
	protected String time;
	protected String rentalCompany;
	protected String rentalLocation;
	protected List<String> drivers;
	protected String eventDate;
	protected String eventType;
	protected long eventEpoch;
	protected String confirmationNumber;
	protected String carClass;
	protected String carNote;
	protected String carDetails;
	protected String arrivalInformation;
	protected String shuttleInformation;
	protected List<String> contacts;
	
	
	
    public List<String> getContacts() {
        if(contacts == null){
            contacts = new ArrayList<String>();
        }
        return contacts;
    }
    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }
    public String getCarNote() {
        return carNote;
    }
    public void setCarNote(String carNote) {
        this.carNote = carNote;
    }
    public String getCarDetails() {
        return carDetails;
    }
    public void setCarDetails(String carDetails) {
        this.carDetails = carDetails;
    }
    public String getArrivalInformation() {
        return arrivalInformation;
    }
    public void setArrivalInformation(String arrivalInformation) {
        this.arrivalInformation = arrivalInformation;
    }
    public String getShuttleInformation() {
        return shuttleInformation;
    }
    public void setShuttleInformation(String shuttleInformation) {
        this.shuttleInformation = shuttleInformation;
    }
    public String getCarClass() {
        return carClass;
    }
    public void setCarClass(String carClass) {
        this.carClass = carClass;
    }
    public String getConfirmationNumber() {
        return confirmationNumber;
    }
    public void setConfirmationNumber(String confirmationNumber) {
        this.confirmationNumber = confirmationNumber;
    }
    public long getEventEpoch() {
		return eventEpoch;
	}
	public void setEventEpoch(long eventEpoch) {
		this.eventEpoch = eventEpoch;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRentalCompany() {
		return rentalCompany;
	}
	public void setRentalCompany(String rentalCompany) {
		this.rentalCompany = rentalCompany;
	}
	public String getRentalLocation() {
		return rentalLocation;
	}
	public void setRentalLocation(String rentalLocation) {
		this.rentalLocation = rentalLocation;
	}
	public List<String> getDrivers() {
	    if(drivers == null){
	        drivers = new ArrayList<String>();
	    }
		return drivers;
	}
	public void setDrivers(List<String> drivers) {
		this.drivers = drivers;
	}
	public void setEventDate(String date) {
		this.eventDate = date;
		
	}
	public String getEventDate() {
		return eventDate;
	}
   
	
}
