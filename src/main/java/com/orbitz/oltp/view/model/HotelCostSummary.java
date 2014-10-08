package com.orbitz.oltp.view.model;

public class HotelCostSummary {
    protected String cost;
    protected String taxes;
    protected String total;
    protected String hotelCostSummary;
    

    public String getHotelCostSummary() {
        return hotelCostSummary;
    }

    public void setHotelCostSummary(String hotelCostSummary) {
        this.hotelCostSummary = hotelCostSummary;
    }
    public String getCost() {
        return cost;
    }
    public void setCost(String cost) {
        this.cost = cost;
    }
    public String getTaxes() {
        return taxes;
    }
    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }
    public String getTotal() {
        return total;
    }
    public void setTotal(String total) {
        this.total = total;
    }
}
