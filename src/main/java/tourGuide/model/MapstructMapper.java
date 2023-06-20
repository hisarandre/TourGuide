package tourGuide.model;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tourGuide.dto.UserPreferencesDTO;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface MapstructMapper {

    UserPreferences userPreferencesDTOToUserPreferences(UserPreferencesDTO userPreferencesDTO);

    @Mapping(source = "currency.currencyCode", target = "currency")
    @Mapping(expression = "java(mapMonetaryAmountToInt(userPreferences.getLowerPricePoint()))", target = "lowerPricePoint")
    @Mapping(expression = "java(mapMonetaryAmountToInt(userPreferences.getHighPricePoint()))", target = "highPricePoint")
    UserPreferencesDTO userPreferencesToUserPreferencesDTO(UserPreferences userPreferences);

    default int mapMonetaryAmountToInt(MonetaryAmount monetaryAmount) {
        return monetaryAmount.getNumber().numberValue(BigDecimal.class).intValue();
    }
}