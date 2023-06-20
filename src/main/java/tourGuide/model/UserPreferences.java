package tourGuide.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.Money;

public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	private CurrencyUnit currency = Monetary.getCurrency("USD");
	private Money lowerPricePoint = Money.of(0, currency);
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);
	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;
	
	public UserPreferences() {
	}

	public void setAttractionProximity(int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}

	public void setCurrency(String currency) {
		this.currency = Monetary.getCurrency(currency);
	}

	public void setLowerPricePoint(int lowerPricePoint) {
		this.lowerPricePoint = Money.of(lowerPricePoint, this.currency);
	}

	public void setHighPricePoint(int highPricePoint) {
		this.highPricePoint = Money.of(highPricePoint, this.currency);
	}

	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	public void setTicketQuantity(int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}

	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

	public int getAttractionProximity() {
		return attractionProximity;
	}

	public CurrencyUnit getCurrency() {
		return currency;
	}

	public Money getLowerPricePoint() {
		return lowerPricePoint;
	}

	public Money getHighPricePoint() {
		return highPricePoint;
	}

	public int getTripDuration() {
		return tripDuration;
	}

	public int getTicketQuantity() {
		return ticketQuantity;
	}

	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}
}
