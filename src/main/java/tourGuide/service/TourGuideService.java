package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.dto.UserPreferencesDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.*;
import tourGuide.tracker.Tracker;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {

	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final MapstructMapper mapper = Mappers.getMapper(MapstructMapper.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;

	final ExecutorService executor = Executors.newFixedThreadPool(200);

	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	/**
	 * Retrieves the list of rewards for a given user.
	 *
	 * @param user The user for whom to retrieve the rewards.
	 * @return The list of rewards for the user.
	 */
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	/**
	 * Retrieves the current location of a user.
	 *
	 * @param user The user for whom to retrieve the location.
	 * @return The visited location of the user.
	 */
	public VisitedLocation getUserLocation(User user) {
			VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
					user.getLastVisitedLocation() :
					trackUserLocation(user).join();
			return visitedLocation;
	}

	/**
	 * Retrieves a user based on the username.
	 *
	 * @param userName The username of the user to retrieve.
	 * @return The user object corresponding to the username.
	 */
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	/**
	 * Retrieves all the users.
	 *
	 * @return The list of all users.
	 */
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}

	/**
	 * Adds a new user.
	 *
	 * @param user The user to add.
	 */
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	/**
	 * Retrieves trip deals for a user.
	 *
	 * @param user The user for whom to retrieve trip deals.
	 * @return The list of trip deals for the user.
	 */
	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards()
				.stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(
				tripPricerApiKey,
				user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(),
				cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	/**
	 * Tracks the location of a user asynchronously.
	 *
	 * @param user The user for whom to track the location.
	 * @return A CompletableFuture that will complete with the visited location.
	 */
	public CompletableFuture<VisitedLocation> trackUserLocation(User user) {
		Locale.setDefault(Locale.US);

		return CompletableFuture
				.supplyAsync(() -> {
					VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
					user.addToVisitedLocations(visitedLocation);
					rewardsService.calculateRewards(user);
					return visitedLocation;
				}, executor);
	}

	/**
	 * Retrieves nearby attractions for a user's visited location.
	 *
	 * @param user             The user for whom to retrieve nearby attractions.
	 * @param visitedLocation  The visited location of the user.
	 * @return The list of nearby attractions.
	 */
	public List<NearbyAttraction> getNearByAttractions(User user, VisitedLocation visitedLocation) {
		List<NearbyAttraction> nearbyAttractionsList = new ArrayList<>();

		// Iterate over attractions and calculate distance and reward points
		gpsUtil.getAttractions().forEach(attraction -> {
			Location attractionLocation = new Location(attraction.latitude, attraction.longitude);
			double distance = rewardsService.getDistance(attractionLocation, visitedLocation.location);
			int rewardPoints = rewardsService.getRewardPoints(attraction, user);

			// Create a NearbyAttraction object and add it to the list
			NearbyAttraction nearbyAttraction = new NearbyAttraction(
					attraction.attractionName,
					attractionLocation,
					visitedLocation.location,
					distance,
					rewardPoints
			);

			nearbyAttractionsList.add(nearbyAttraction);
		});

		// Sort attractions by distance, limit to top 5, and return the list
		return nearbyAttractionsList.stream()
				.sorted(Comparator.comparing(NearbyAttraction::getDistance))
				.limit(5)
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves the current locations of all users.
	 *
	 * @return A map of user IDs to their current locations.
	 */
	public Map<String, Location> getAllCurrentLocations(){
		Map<String, Location> allUsersLocation = new HashMap<>();
		getAllUsers().forEach(user -> allUsersLocation.put(user.getUserId().toString(),user.getLastVisitedLocation().location));
		return allUsersLocation;
	}

	/**
	 * Updates the user preferences for a given user.
	 *
	 * @param userName          The username of the user.
	 * @param userPreferencesDTO The updated user preferences.
	 * @return The updated user preferences DTO.
	 */
	public UserPreferencesDTO updateUserPreferences(String userName, UserPreferencesDTO userPreferencesDTO) {
		User user = getUser(userName);

		UserPreferences userPreferences = mapper.userPreferencesDTOToUserPreferences(userPreferencesDTO);
		user.setUserPreferences(userPreferences);
		UserPreferencesDTO userPreferencesUpdated = mapper.userPreferencesToUserPreferencesDTO(user.getUserPreferences());

		return userPreferencesUpdated;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
}
