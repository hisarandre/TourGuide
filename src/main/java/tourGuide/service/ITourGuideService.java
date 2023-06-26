package tourGuide.service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.model.NearbyAttraction;
import tripPricer.Provider;
import tourGuide.model.User;
import tourGuide.model.UserReward;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ITourGuideService {

    /**
     * Retrieves the list of rewards for a given user.
     *
     * @param user The user for whom to retrieve the rewards.
     * @return The list of rewards for the user.
     */
    List<UserReward> getUserRewards(User user);

    /**
     * Retrieves the current location of a user.
     *
     * @param user The user for whom to retrieve the location.
     * @return The visited location of the user.
     */
    VisitedLocation getUserLocation(User user);

    /**
     * Retrieves a user based on the username.
     *
     * @param userName The username of the user to retrieve.
     * @return The user object corresponding to the username.
     */
    User getUser(String userName);

    /**
     * Retrieves all the users.
     *
     * @return The list of all users.
     */
    List<User> getAllUsers();

    /**
     * Adds a new user.
     *
     * @param user The user to add.
     */
    void addUser(User user);

    /**
     * Retrieves trip deals for a user.
     *
     * @param user The user for whom to retrieve trip deals.
     * @return The list of trip deals for the user.
     */
    List<Provider> getTripDeals(User user);

    /**
     * Tracks the location of a user asynchronously.
     *
     * @param user The user for whom to track the location.
     * @return A CompletableFuture that will complete with the visited location.
     */
    CompletableFuture<VisitedLocation> trackUserLocation(User user);

    /**
     * Retrieves nearby attractions for a user's visited location.
     *
     * @param user             The user for whom to retrieve nearby attractions.
     * @param visitedLocation  The visited location of the user.
     * @return The list of nearby attractions.
     */
    List<NearbyAttraction> getNearByAttractions(User user, VisitedLocation visitedLocation);

    /**
     * Retrieves the current locations of all users.
     *
     * @return A map of user IDs to their current locations.
     */
    Map<String, Location> getAllCurrentLocations();

    /**
     * Updates the user preferences for a given user.
     *
     * @param userName          The username of the user.
     * @param userPreferencesDTO The updated user preferences.
     * @return The updated user preferences DTO.
     */
    UserPreferencesDTO updateUserPreferences(String userName, UserPreferencesDTO userPreferencesDTO);


}