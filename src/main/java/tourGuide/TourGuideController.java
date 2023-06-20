package tourGuide;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import gpsUtil.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.model.UserPreferences;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) throws ExecutionException, InterruptedException {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
        User user = tourGuideService.getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    	return JsonStream.serialize(tourGuideService.getNearByAttractions(user, visitedLocation));
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	// TODO: Get a list of every user's most recent location as JSON
        Map<String, Location> allUsersLocation = tourGuideService.getAllCurrentLocations();
    	return JsonStream.serialize(allUsersLocation);
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }

    @PutMapping("/userPreferences")
    public UserPreferencesDTO updateUserPreferences(@RequestParam String userName, @RequestBody UserPreferencesDTO userPreferencesDTO) {
        return tourGuideService.updateUserPreferences(userName, userPreferencesDTO);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}