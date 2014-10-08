package com.orbitz.oltp.app.view.model;

public class AirFare {
    protected String travelerName;
    protected String cost;
    protected String ticketNumber;
    protected String serviceFee;
    public String getServiceFee() {
        return serviceFee;
    }
    public void setServiceFee(String serviceFee) {
        this.serviceFee = serviceFee;
    }
    public String getTicketNumber() {
        return ticketNumber;
    }
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
    public String getTravelerName() {
        return travelerName;
    }
    public void setTravelerName(String travelerName) {
        this.travelerName = travelerName;
    }
    public String getCost() {
        return cost;
    }
    public void setCost(String cost) {
        this.cost = cost;
    }
}
