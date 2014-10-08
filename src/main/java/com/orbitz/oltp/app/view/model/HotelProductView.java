package com.orbitz.oltp.app.view.model;

import java.util.List;
import java.util.Map;

import com.orbitz.oltp.view.model.HotelCostSummary;

public class HotelProductView implements ProductItinerarySortable {

	protected String eventDate;
	protected String eventType;
	protected String address;
	protected String time;
	protected String hotelName;
	protected String hotelAddress;
	protected String additonalHotelInfo;
	protected Map<String, String> specialRequests;
	protected Map<String, String> hotelGuestInfo;
	protected Map<String, String> cancellationInfo;
	

    public Map<String, String> getCancellationInfo() {
        return cancellationInfo;
    }

    public void setCancellationInfo(Map<String, String> cancellationInfo) {
        this.cancellationInfo = cancellationInfo;
    }

    public Map<String, String> getHotelGuestInfo() {
        return hotelGuestInfo;
    }

    public void setHotelGuestInfo(Map<String, String> hotelGuestInfo) {
        this.hotelGuestInfo = hotelGuestInfo;
    }

    public Map<String, String> getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(Map<String, String> specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getAdditonalHotelInfo() {
        return additonalHotelInfo;
    }

    public void setAdditonalHotelInfo(String additonalHotelInfo) {
        this.additonalHotelInfo = additonalHotelInfo;
    }

    public Map<String, String> getHotelContacts() {
        return hotelContacts;
    }

    public void setHotelContacts(Map<String, String> hotelContacts) {
        this.hotelContacts = hotelContacts;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    protected String hotelAddress2;
	protected Map<String, String> hotelContacts;
	protected String roomDescription;
	public String getHotelAddress2() {
        return hotelAddress2;
    }

    public void setHotelAddress2(String hotelAddress2) {
        this.hotelAddress2 = hotelAddress2;
    }

    protected List<String> travelers;
	
	

	public List<String> getTravelers() {
		return travelers;
	}

	public void setTravelers(List<String> travelers) {
		this.travelers = travelers;
	}

	protected Long eventEpoch;
	
	public Long getEventEpoch() {
		return eventEpoch;
	}

	public void setEventEpoch(Long eventEpoch) {
		this.eventEpoch = eventEpoch;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getHotelAddress() {
		return hotelAddress;
	}

	public void setHotelAddress(String hotelAddress) {
		this.hotelAddress = hotelAddress;
	}

	public void setEventDate(String date) {
		this.eventDate = date;
	}

	public String getEventDate() {
		return eventDate;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	

}
