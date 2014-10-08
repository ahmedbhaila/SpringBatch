package com.orbitz.oltp.view.model;

import java.util.List;

public class HotelTripEventView extends TripEventView {
	protected String hotelAddress;
	protected String hotelConfirmationNumber;
	protected String phoneNumber;
	protected String checkinDate;
	protected String checkoutDate;
	protected String rooms;
	protected String guests;
	protected String nights;
	protected List<String> guestNames;
	protected String roomDescription;
	protected String specialRequests;
	public String getHotelAddress() {
		return hotelAddress;
	}
	public void setHotelAddress(String hotelAddress) {
		this.hotelAddress = hotelAddress;
	}
	public String getHotelConfirmationNumber() {
		return hotelConfirmationNumber;
	}
	public void setHotelConfirmationNumber(String hotelConfirmationNumber) {
		this.hotelConfirmationNumber = hotelConfirmationNumber;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getCheckinDate() {
		return checkinDate;
	}
	public void setCheckinDate(String checkinDate) {
		this.checkinDate = checkinDate;
	}
	public String getCheckoutDate() {
		return checkoutDate;
	}
	public void setCheckoutDate(String checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	public String getRooms() {
		return rooms;
	}
	public void setRooms(String rooms) {
		this.rooms = rooms;
	}
	public String getGuests() {
		return guests;
	}
	public void setGuests(String guests) {
		this.guests = guests;
	}
	public String getNights() {
		return nights;
	}
	public void setNights(String nights) {
		this.nights = nights;
	}
	public List<String> getGuestNames() {
		return guestNames;
	}
	public void setGuestNames(List<String> guestNames) {
		this.guestNames = guestNames;
	}
	public String getRoomDescription() {
		return roomDescription;
	}
	public void setRoomDescription(String roomDescription) {
		this.roomDescription = roomDescription;
	}
	public String getSpecialRequests() {
		return specialRequests;
	}
	public void setSpecialRequests(String specialRequests) {
		this.specialRequests = specialRequests;
	}
	
}
