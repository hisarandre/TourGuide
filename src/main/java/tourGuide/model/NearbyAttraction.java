package tourGuide.model;

import gpsUtil.location.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class NearbyAttraction {
    private String attractionName;
    private Location attractionLocation;
    private Location userLocation;
    private double distance;
    private int rewardPoints;

}
