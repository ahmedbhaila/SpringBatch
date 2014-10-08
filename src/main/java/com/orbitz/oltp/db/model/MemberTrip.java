package com.orbitz.oltp.db.model;

public class MemberTrip {
	protected String email;
	protected String travelPlanId;
	protected String tripLocator;
	protected String jsonString;
	protected Integer posId;
	protected Integer memberId;
	public Integer getMemberId() {
        return memberId;
    }
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
    public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTravelPlanId() {
        return travelPlanId;
    }
    public void setTravelPlanId(String travelPlanId) {
        this.travelPlanId = travelPlanId;
    }
    public Integer getPosId() {
        return posId;
    }
    public void setPosId(Integer posId) {
        this.posId = posId;
    }
    public String getTripLocator() {
		return tripLocator;
	}
	public void setTripLocator(String tripLocator) {
		this.tripLocator = tripLocator;
	}
}
