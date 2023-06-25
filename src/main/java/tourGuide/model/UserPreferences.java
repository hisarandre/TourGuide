package tourGuide.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.javamoney.moneta.Money;

@Data
public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	@Setter(AccessLevel.NONE)
	private CurrencyUnit currency = Monetary.getCurrency("USD");
	@Setter(AccessLevel.NONE)
	private Money lowerPricePoint = Money.of(0, currency);
	@Setter(AccessLevel.NONE)
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);
	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;
	
	public UserPreferences() {}


	public void setCurrency(String currency) {
		this.currency = Monetary.getCurrency(currency);
	}

	public void setLowerPricePoint(int lowerPricePoint) {
		this.lowerPricePoint = Money.of(lowerPricePoint, this.currency);
	}

	public void setHighPricePoint(int highPricePoint) {
		this.highPricePoint = Money.of(highPricePoint, this.currency);
	}

}
