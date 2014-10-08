package com.orbitz.oltp.view.model;

public class ASTripEventView extends TripEventView {
    protected String attractionServiceName;
    protected String orbitzRecordLocator;
    protected String date;
    protected String quantity;
    protected String cost;
    public String getAttractionServiceName() {
        return attractionServiceName;
    }
    public void setAttractionServiceName(String attractionServiceName) {
        this.attractionServiceName = attractionServiceName;
    }
    public String getOrbitzRecordLocator() {
        return orbitzRecordLocator;
    }
    public void setOrbitzRecordLocator(String orbitzRecordLocator) {
        this.orbitzRecordLocator = orbitzRecordLocator;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getCost() {
        return cost;
    }
    public void setCost(String cost) {
        this.cost = cost;
    }
}
