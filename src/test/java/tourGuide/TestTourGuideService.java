package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.NearbyAttraction;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tripPricer.Provider;

public class TestTourGuideService {

	private TourGuideService tourGuideService;
	private GpsUtil gpsUtil;
	private RewardsService rewardsService;

	@Before
	public void setUp() throws Exception {
		Locale.setDefault(Locale.US);
		gpsUtil = new GpsUtil();
		rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		tourGuideService = new TourGuideService(gpsUtil, rewardsService);
	}

	@Test
	public void getUserLocation() throws Exception {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		//when
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).get();

		//then
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void addUser() {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		//when
		User retrievedUser = tourGuideService.getUser(user.getUserName());
		User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(user, retrievedUser);
		assertEquals(user2, retrievedUser2);
	}

	@Test
	public void getAllUsers() {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		//when
		List<User> allUsers = tourGuideService.getAllUsers();

		//then
		tourGuideService.tracker.stopTracking();
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() throws Exception {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		//when
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).get();

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() throws Exception {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).get();

		//when
		List<NearbyAttraction> attractions = tourGuideService.getNearByAttractions(user, visitedLocation);

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(5, attractions.size());
	}

	@Test
	public void getNearbyAttractionsWithNoRegisteredLocation() throws Exception {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation emptyVisitedLocation = new VisitedLocation(user.getUserId(), null, null);

		//when
		List<NearbyAttraction> attractions = tourGuideService.getNearByAttractions(user, emptyVisitedLocation);

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(0, attractions.size());
	}

	@Test
	public void getTripDeals() {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		//when
		List<Provider> providers = tourGuideService.getTripDeals(user);

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(5, providers.size());
	}

	@Test
	public void getAllCurrentLocations() {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		//when
		Map<String, Location> allUsersLocation = tourGuideService.getAllCurrentLocations();

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(2, tourGuideService.getAllUsers().size());
		assertEquals(2, allUsersLocation.size());
	}

	@Test
	public void getAllCurrentLocationsWithNoRegisteredLocation() {
		//given
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.addUser(user);

		//when
		Map<String, Location> allUsersLocation = tourGuideService.getAllCurrentLocations();

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(1, allUsersLocation.size());
	}

	@Test
	public void getAllCurrentLocationsWithNoRegisteredUser() {
		//when
		Map<String, Location> allUsersLocation = tourGuideService.getAllCurrentLocations();

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(0, tourGuideService.getAllUsers().size());
		assertEquals(0, allUsersLocation.size());
	}
}