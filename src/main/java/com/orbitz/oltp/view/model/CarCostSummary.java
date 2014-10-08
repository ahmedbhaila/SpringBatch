package com.orbitz.oltp.view.model;

import java.util.ArrayList;
import java.util.List;

public class CarCostSummary {
    protected String baseRate;
    protected String taxesAndFees;
    protected String totalCarRentalEstimate;
    protected String amountPaidAtReservation;
    protected String amountDueAtRental;
    protected List<String> carTaxRates;
    
    
    public List<String> getCarTaxRates() {
        if(carTaxRates == null){
            carTaxRates = new ArrayList<String>();
        }
        return carTaxRates;
    }
    public void setCarTaxRates(List<String> carTaxRates) {
        this.carTaxRates = carTaxRates;
    }
    public String getBaseRate() {
        return baseRate;
    }
    public void setBaseRate(String baseRate) {
        this.baseRate = baseRate;
    }
    public String getTaxesAndFees() {
        return taxesAndFees;
    }
    public void setTaxesAndFees(String taxesAndFees) {
        this.taxesAndFees = taxesAndFees;
    }
    public String getTotalCarRentalEstimate() {
        return totalCarRentalEstimate;
    }
    public void setTotalCarRentalEstimate(String totalCarRentalEstimate) {
        this.totalCarRentalEstimate = totalCarRentalEstimate;
    }
    public String getAmountPaidAtReservation() {
        return amountPaidAtReservation;
    }
    public void setAmountPaidAtReservation(String amountPaidAtReservation) {
        this.amountPaidAtReservation = amountPaidAtReservation;
    }
    public String getAmountDueAtRental() {
        return amountDueAtRental;
    }
    public void setAmountDueAtRental(String amountDueAtRental) {
        this.amountDueAtRental = amountDueAtRental;
    }
}
