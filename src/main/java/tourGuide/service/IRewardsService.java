package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.model.User;

/**
 * Interface for a RewardsService that handles the calculation of rewards for users based on their visited locations.
 */
public interface IRewardsService {

    /**
     * Sets the proximity buffer to the specified value.
     *
     * @param proximityBuffer The proximity buffer value to set.
     */
    void setProximityBuffer(int proximityBuffer);

    /**
     * Sets the proximity buffer to the default value.
     */
    void setDefaultProximityBuffer();

    /**
     * Calculates the rewards for a user based on their visited locations.
     *
     * @param user The User for which to calculate the rewards.
     */
    void calculateRewards(User user);

    /**
     * Waits for the completion of the calculateRewards task.
     * If the task does not complete within the given time,
     * it shuts down the executor and interrupts the current thread.
     */
    void awaitCalculateRewardsEnding();

    /**
     * Checks if the given location is within the attraction proximity range.
     *
     * @param attraction The Attraction to check.
     * @param location   The Location to compare with.
     * @return True if the location is within the attraction proximity range, false otherwise.
     */
    boolean isWithinAttractionProximity(Attraction attraction, Location location);

    /**
     * Checks if the given visited location is near the given attraction.
     *
     * @param visitedLocation The VisitedLocation to check.
     * @param attraction      The Attraction to compare with.
     * @return True if the visited location is near the attraction, false otherwise.
     */
    boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction);

    /**
     * Calculates the reward points for the given attraction and user.
     *
     * @param attraction The attraction for which to calculate the reward points.
     * @param user The user for whom the reward points are calculated.
     * @return The reward points for the attraction and user.
     */
    int getRewardPoints(Attraction attraction, User user);

    /**
     * Calculates the distance between two locations using the haversine formula.
     *
     * @param loc1 The first location.
     * @param loc2 The second location.
     * @return The distance between the two locations in statute miles.
     */
    double getDistance(Location loc1, Location loc2);
}