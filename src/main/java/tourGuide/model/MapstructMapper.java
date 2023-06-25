package tourGuide.model;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tourGuide.dto.UserPreferencesDTO;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface MapstructMapper {

    /**
     * Converts a UserPreferencesDTO object to a UserPreferences object.
     *
     * @param userPreferencesDTO The UserPreferencesDTO to be converted.
     * @return The converted UserPreferences object.
     */
    UserPreferences userPreferencesDTOToUserPreferences(UserPreferencesDTO userPreferencesDTO);

    /**
     * Converts a UserPreferences object to a UserPreferencesDTO object.
     *
     * @param userPreferences The UserPreferences to be converted.
     * @return The converted UserPreferencesDTO object.
     */
    @Mapping(source = "currency.currencyCode", target = "currency")
    @Mapping(expression = "java(mapMonetaryAmountToInt(userPreferences.getLowerPricePoint()))", target = "lowerPricePoint")
    @Mapping(expression = "java(mapMonetaryAmountToInt(userPreferences.getHighPricePoint()))", target = "highPricePoint")
    UserPreferencesDTO userPreferencesToUserPreferencesDTO(UserPreferences userPreferences);

    /**
     * Maps a MonetaryAmount to an integer value.
     *
     * @param monetaryAmount The MonetaryAmount to be mapped.
     * @return The mapped integer value.
     */
    default int mapMonetaryAmountToInt(MonetaryAmount monetaryAmount) {
        return monetaryAmount.getNumber().numberValue(BigDecimal.class).intValue();
    }
}