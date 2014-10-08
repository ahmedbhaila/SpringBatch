package com.orbitz.oltp.app.view.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.orbitz.oltp.view.model.ASTripEventView;
import com.orbitz.oltp.view.model.CarCostSummary;
import com.orbitz.oltp.view.model.HotelCostSummary;

public class ClassicTripSummaryView {
	protected Map<String, List<ProductItinerary<?>>> productMap;
	protected String tripTitle;
	protected String tripInfo;
	protected String tripMessage;
	
	protected String packageInfo;
	protected String orbitzPackageRecordLocator;
	protected String totalTripCost;
	
	protected BillingInfoView billingInfoView;
	protected CarCostSummary carCostSummary;
	protected HotelCostSummary hostCostSummary;
	
	
	public HotelCostSummary getHostCostSummary() {
        return hostCostSummary;
    }
    public void setHostCostSummary(HotelCostSummary hostCostSummary) {
        this.hostCostSummary = hostCostSummary;
    }
    public CarCostSummary getCarCostSummary() {
        return carCostSummary;
    }
    public void setCarCostSummary(CarCostSummary carCostSummary) {
        this.carCostSummary = carCostSummary;
    }
    // All non timeline view related products
	protected List<ASTripEventView> services;
	
	
    public List<ASTripEventView> getServices() {
        if(services == null){
            services = new ArrayList<ASTripEventView>();
        }
        return services;
    }
    public void setServices(List<ASTripEventView> services) {
        this.services = services;
    }
    protected boolean tripCancelled;
	protected String orbitzRecordLocator;
	
	protected List<AirFare> airFares;
	
	protected String totalAirFareCost;

    public String getTotalAirFareCost() {
        return totalAirFareCost;
    }
    public void setTotalAirFareCost(String totalAirFareCost) {
        this.totalAirFareCost = totalAirFareCost;
    }
    public List<AirFare> getAirFares() {
        if(airFares == null){
            airFares = new ArrayList<AirFare>();
        }
        return airFares;
    }
    public void setAirFares(List<AirFare> airFares) {
        this.airFares = airFares;
    }
	
    public String getOrbitzRecordLocator() {
        return orbitzRecordLocator;
    }
    public void setOrbitzRecordLocator(String orbitzRecordLocator) {
        this.orbitzRecordLocator = orbitzRecordLocator;
    }
    public boolean isTripCancelled() {
        return tripCancelled;
    }
    public void setTripCancelled(boolean tripCancelled) {
        this.tripCancelled = tripCancelled;
    }
    public String getTripTitle() {
		return tripTitle;
	}
	public void setTripTitle(String tripTitle) {
		this.tripTitle = tripTitle;
	}
	public String getTripInfo() {
		return tripInfo;
	}
	public void setTripInfo(String tripInfo) {
		this.tripInfo = tripInfo;
	}
	public String getTripMessage() {
		return tripMessage;
	}
	public void setTripMessage(String tripMessage) {
		this.tripMessage = tripMessage;
	}
	public String getPackageInfo() {
		return packageInfo;
	}
	public void setPackageInfo(String packageInfo) {
		this.packageInfo = packageInfo;
	}
	public String getOrbitzPackageRecordLocator() {
		return orbitzPackageRecordLocator;
	}
	public void setOrbitzPackageRecordLocator(String orbitzPackageRecordLocator) {
		this.orbitzPackageRecordLocator = orbitzPackageRecordLocator;
	}
	public String getTotalTripCost() {
		return totalTripCost;
	}
	public void setTotalTripCost(String totalTripCost) {
		this.totalTripCost = totalTripCost;
	}
	public BillingInfoView getBillingInfoView() {
		return billingInfoView;
	}
	public void setBillingInfoView(BillingInfoView billingInfoView) {
		this.billingInfoView = billingInfoView;
	}
	public Map<String, List<ProductItinerary<?>>> getProductMap() {
		return productMap;
	}
	public void setProductMap(Map<String, List<ProductItinerary<?>>> productMap) {
		this.productMap = productMap;
	}
}
