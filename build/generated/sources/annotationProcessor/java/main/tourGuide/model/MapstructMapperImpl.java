package tourGuide.model;

import javax.annotation.Generated;
import javax.money.CurrencyUnit;
import org.springframework.stereotype.Component;
import tourGuide.dto.UserPreferencesDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-02T19:33:59+0200",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.0.2.jar, environment: Java 20.0.1 (Oracle Corporation)"
)
@Component
public class MapstructMapperImpl implements MapstructMapper {

    @Override
    public UserPreferences userPreferencesDTOToUserPreferences(UserPreferencesDTO userPreferencesDTO) {
        if ( userPreferencesDTO == null ) {
            return null;
        }

        UserPreferences userPreferences = new UserPreferences();

        userPreferences.setCurrency( userPreferencesDTO.getCurrency() );
        userPreferences.setLowerPricePoint( userPreferencesDTO.getLowerPricePoint() );
        userPreferences.setHighPricePoint( userPreferencesDTO.getHighPricePoint() );
        userPreferences.setAttractionProximity( userPreferencesDTO.getAttractionProximity() );
        userPreferences.setTripDuration( userPreferencesDTO.getTripDuration() );
        userPreferences.setTicketQuantity( userPreferencesDTO.getTicketQuantity() );
        userPreferences.setNumberOfAdults( userPreferencesDTO.getNumberOfAdults() );
        userPreferences.setNumberOfChildren( userPreferencesDTO.getNumberOfChildren() );

        return userPreferences;
    }

    @Override
    public UserPreferencesDTO userPreferencesToUserPreferencesDTO(UserPreferences userPreferences) {
        if ( userPreferences == null ) {
            return null;
        }

        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();

        userPreferencesDTO.setCurrency( userPreferencesCurrencyCurrencyCode( userPreferences ) );
        userPreferencesDTO.setAttractionProximity( userPreferences.getAttractionProximity() );
        userPreferencesDTO.setTripDuration( userPreferences.getTripDuration() );
        userPreferencesDTO.setTicketQuantity( userPreferences.getTicketQuantity() );
        userPreferencesDTO.setNumberOfAdults( userPreferences.getNumberOfAdults() );
        userPreferencesDTO.setNumberOfChildren( userPreferences.getNumberOfChildren() );

        userPreferencesDTO.setLowerPricePoint( mapMonetaryAmountToInt(userPreferences.getLowerPricePoint()) );
        userPreferencesDTO.setHighPricePoint( mapMonetaryAmountToInt(userPreferences.getHighPricePoint()) );

        return userPreferencesDTO;
    }

    private String userPreferencesCurrencyCurrencyCode(UserPreferences userPreferences) {
        if ( userPreferences == null ) {
            return null;
        }
        CurrencyUnit currency = userPreferences.getCurrency();
        if ( currency == null ) {
            return null;
        }
        String currencyCode = currency.getCurrencyCode();
        if ( currencyCode == null ) {
            return null;
        }
        return currencyCode;
    }
}
