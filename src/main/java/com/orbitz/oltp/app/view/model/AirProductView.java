package com.orbitz.oltp.app.view.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AirProductView implements ProductItinerarySortable {
	protected String airLine;
	protected String flightNumber;
	protected String departureAirport;
	protected String arrivalAirport;
	protected List<String> travelers;
	protected String departureTime;
	protected String arrivalTime;
	protected String eventDate;
	protected Long eventEpoch;
	protected String cabinType;
	protected String distance;
	protected String duration;
	protected String aircraftType;
	protected String departureAirCode;
    protected String arrivalAirCode;
    protected String departTimeExtract;
    protected String arrivalTimeExtract;
    protected String mealPrefs;
    protected String eventType;
    protected String arrivalCity;
    protected String departureCity;
    protected String planeChangeMessage;
    protected List<String> tickets;
    protected boolean overnightFlight;
    
    public boolean isOvernightFlight() {
        return overnightFlight;
    }
    public void setOvernightFlight(boolean overnightFlight) {
        this.overnightFlight = overnightFlight;
    }
    public List<String> getTickets() {
        return tickets;
    }
    public void setTickets(List<String> tickets) {
        this.tickets = tickets;
    }
    public String getPlaneChangeMessage() {
        return planeChangeMessage;
    }
    public void setPlaneChangeMessage(String planeChangeMessage) {
        this.planeChangeMessage = planeChangeMessage;
    }
    public String getArrivalAirCode() {
        return arrivalAirCode;
    }
    public void setArrivalAirCode(String arrivalAirCode) {
        this.arrivalAirCode = arrivalAirCode;
    }
    public String getArrivalCity() {
        return arrivalCity;
    }
    public String getDepartureAirCode() {
        return departureAirCode;
    }
    public void setDepartureAirCode(String departureAirCode) {
        this.departureAirCode = departureAirCode;
    }
    public String getDepartureCity() {
        return departureCity;
    }
    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }
    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }
    public String getEventType() {
        return eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public String getMealPrefs() {
        return mealPrefs;
    }
    public void setMealPrefs(String mealPrefs) {
        this.mealPrefs = mealPrefs;
    }
    public String getCabinType() {
        return cabinType;
    }
    public void setCabinType(String cabinType) {
        this.cabinType = cabinType;
    }
    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getAircraftType() {
        return aircraftType;
    }
    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }
    public String getDepartureCityCode() {
        return departureAirCode;
    }
    public void setDepartureCityCode(String departureAirCode) {
        this.departureAirCode = departureAirCode;
    }
    public String getArrivalCityCode() {
        return arrivalAirCode;
    }
    public void setArrivalCityCode(String arrivalAirCode) {
        this.arrivalAirCode = arrivalAirCode;
    }
    public String getDepartTimeExtract() {
        return departTimeExtract;
    }
    public void setDepartTimeExtract(String departTimeExtract) {
        this.departTimeExtract = departTimeExtract;
    }
    public String getArrivalTimeExtract() {
        return arrivalTimeExtract;
    }
    public void setArrivalTimeExtract(String arrivalTimeExtract) {
        this.arrivalTimeExtract = arrivalTimeExtract;
    }
    public Long getEventEpoch() {
		return eventEpoch;
	}
	public void setEventEpoch(Long eventEpoch) {
		this.eventEpoch = eventEpoch;
	}
	protected Map<String, AirProductSlice> airSlice;
	
	
	public Map<String, AirProductSlice> getAirSlice() {
		return airSlice;
	}
	public void setAirSlice(Map<String, AirProductSlice> airSlice) {
		this.airSlice = airSlice;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getAirLine() {
		return airLine;
	}
	public void setAirLine(String airLine) {
		this.airLine = airLine;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public String getDepartureAirport() {
		return departureAirport;
	}
	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}
	public String getArrivalAirport() {
		return arrivalAirport;
	}
	public void setArrivalAirport(String arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}
	
	public List<String> getTravelers() {
	    if(travelers == null){
	        travelers = new ArrayList<String>();
	    }
		return travelers;
	}
	public void setTravelers(List<String> travelers) {
		this.travelers = travelers;
	}
	public void setEventDate(String date) {
		this.eventDate = date;
	}
	public String getEventDate() {
		return eventDate;
	}
	
}
