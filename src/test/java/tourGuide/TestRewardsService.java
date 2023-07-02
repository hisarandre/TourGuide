package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tourGuide.model.UserReward;

public class TestRewardsService {

	private RewardsService rewardsService;
	private TourGuideService tourGuideService;
	private GpsUtil gpsUtil;

	@Before
	public void init() {
		Locale.setDefault(Locale.US);
		gpsUtil = new GpsUtil();
		rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		tourGuideService = new TourGuideService(gpsUtil, rewardsService);
	}

	@Test
	public void userGetRewards() {
		//given
		InternalTestHelper.setInternalUserNumber(0);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

		//when
		tourGuideService.trackUserLocation(user).join();
		rewardsService.awaitCalculateRewardsEnding();
		List<UserReward> userRewards = user.getUserRewards();

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(1, userRewards.size());
	}

	@Test
	public void isWithinAttractionProximity() {
		//when
		Attraction attraction = gpsUtil.getAttractions().get(0);
		//then
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	public void nearAllAttractions() throws ExecutionException, InterruptedException {
		//given
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);
		InternalTestHelper.setInternalUserNumber(1);
		User user = tourGuideService.getAllUsers().get(0);

		//when
		rewardsService.calculateRewards(user);
		rewardsService.awaitCalculateRewardsEnding();
		List<UserReward> userRewards = tourGuideService.getUserRewards(user);

		//then
		tourGuideService.tracker.stopTracking();
		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}
}