package com.orbitz.oltp.app.view.model;

public class BillingInfoView {
	protected String subTotal;
	protected String cardHoldersName;
	protected String cardType;
	protected String cardNumber;
	public String getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public String getCardHoldersName() {
		return cardHoldersName;
	}
	public void setCardHoldersName(String cardHoldersName) {
		this.cardHoldersName = cardHoldersName;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
}
