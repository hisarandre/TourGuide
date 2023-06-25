package tourGuide.dto;

import lombok.Data;

@Data
public class UserPreferencesDTO {
    private int attractionProximity;
    private String currency;
    private int lowerPricePoint;
    private int highPricePoint;
    private int tripDuration;
    private int ticketQuantity;
    private int numberOfAdults;
    private int numberOfChildren;

    public UserPreferencesDTO() {

    }
}
