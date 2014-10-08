package com.orbitz.oltp.app.view.model;




public class ProductItinerary<T> {
	protected Long startDate;
	protected T product;
	protected boolean isCar;
	protected boolean isAir;
	protected boolean isHotel;
	protected String eventStartDate;
	
	

	public String getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(String eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	public T getProduct() {
		return sortableProduct;
	}
	
	public boolean isCar() {
		return isCar;
	}

	public void setCar(boolean isCar) {
		this.isCar = isCar;
	}

	public boolean isAir() {
		return isAir;
	}

	public void setAir(boolean isAir) {
		this.isAir = isAir;
	}

	public boolean isHotel() {
		return isHotel;
	}

	public void setHotel(boolean isHotel) {
		this.isHotel = isHotel;
	}

	public Long getStartDate() {
		return startDate;
	}
	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}
	protected T sortableProduct;
	public ProductItinerary(Long startDate, String eventDateString, T sortableProduct){
		this.startDate = startDate;
		this.sortableProduct = sortableProduct;
		this.eventStartDate = eventDateString;
		isCar = false;
		isAir = false;
		isHotel = false;
	}
	
	
}
