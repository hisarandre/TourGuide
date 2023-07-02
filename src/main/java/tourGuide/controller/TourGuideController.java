package tourGuide.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tripPricer.Provider;

/**
 * The controller class that handles requests related to the tour guide functionality.
 */
@RestController
public class TourGuideController {

    private final Logger logger = LoggerFactory.getLogger(TourGuideController.class);

	@Autowired
	TourGuideService tourGuideService;

    /**
     * Handles the request for the homepage.
     *
     * @return A greeting message from TourGuide.
     */
    @RequestMapping("/")
    public String index() {
        logger.info("Homepage requested");
        return "Greetings from TourGuide!";
    }

    /**
     * Handles the request to get the visited location for a user.
     *
     * @param userName The username of the user.
     * @return The JSON of the visited location.
     * @throws ExecutionException   If an execution exception occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) throws ExecutionException, InterruptedException {
        logger.info("request visited location from " + userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }

    /**
     * Handles the request to get nearby attractions for a user.
     *
     * @param userName The username of the user.
     * @return The JSON of the nearby attractions.
     * @throws ExecutionException   If an execution exception occurs.
     * @throws InterruptedException If the operation is interrupted.
     */
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
        logger.info("request nearby attraction from " + userName);
        User user = tourGuideService.getUser(userName);

        //check is the user exist
        if (user == null) return JsonStream.serialize("no user found");

        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(tourGuideService.getNearByAttractions(user, visitedLocation));
    }

    /**
     * Handles the request to get user rewards.
     *
     * @param userName The username of the user.
     * @return The JSON of the user rewards.
     */
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
        logger.info("request rewards from " + userName);
        User user = tourGuideService.getUser(userName);

        //check is the user exist
        if (user == null) return JsonStream.serialize("no user found");

    	return JsonStream.serialize(tourGuideService.getUserRewards(user));
    }

    /**
     * Handles the request to get all current locations of users.
     *
     * @return The JSON of all current user locations.
     */
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        logger.info("request all current locations");
        Map<String, Location> allUsersLocation = tourGuideService.getAllCurrentLocations();
        return JsonStream.serialize(allUsersLocation);
    }

    /**
     * Handles the request to get trip deals for a user.
     *
     * @param userName The username of the user.
     * @return The JSON of the trip deals.
     */
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        logger.info("request trip deals from " + userName);
        User user = tourGuideService.getUser(userName);

        //check is the user exist
        if (user == null) return JsonStream.serialize("no user found");

    	List<Provider> providers = tourGuideService.getTripDeals(user);
    	return JsonStream.serialize(providers);
    }

    /**
     * Handles the request to update user preferences.
     *
     * @param userName          The username of the user.
     * @param userPreferencesDTO The updated user preferences.
     * @return The updated user preferences.
     */
    @PutMapping("/userPreferences")
    public String updateUserPreferences(@RequestParam String userName, @RequestBody UserPreferencesDTO userPreferencesDTO) {
        logger.info("update user preferences from " + userName);
        User user = tourGuideService.getUser(userName);

        //check is the user exist
        if (user == null) return JsonStream.serialize("no user found");

        return JsonStream.serialize(tourGuideService.updateUserPreferences(user, userPreferencesDTO));
    }

    /**
     * Retrieves the user object based on the username.
     *
     * @param userName The username of the user.
     * @return The User object associated with the given username.
     */
    private User getUser(String userName) {
        logger.info("request user by username : " + userName);
    	return tourGuideService.getUser(userName);
    }

}