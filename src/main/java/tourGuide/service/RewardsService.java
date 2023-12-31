package tourGuide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.model.User;
import tourGuide.model.UserReward;

/**
 * Service class that handles the calculation of rewards for users based on their visited locations.
 */
@Service
public class RewardsService implements IRewardsService{
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	// proximity in miles
    private int defaultProximityBuffer = 10;
	public int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	final ExecutorService executor = Executors.newFixedThreadPool(400);

	/**
	 * Constructs a RewardsService with the given dependencies.
	 *
	 * @param gpsUtil        The GpsUtil dependency.
	 * @param rewardCentral  The RewardCentral dependency.
	 */
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}

	@Override
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	@Override
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	@Override
	public void calculateRewards(User user) {
		CompletableFuture.runAsync(() -> {
			List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
			List<Attraction> attractions = gpsUtil.getAttractions();

			// Iterate over each visited location
			userLocations.parallelStream().forEach(visitedLocation -> {
				// Filter attractions to find the ones near the visited location
				attractions.parallelStream().filter(attraction -> nearAttraction(visitedLocation, attraction))
						.forEach(attraction -> {
							// Add a new UserReward to the user for the visited location and attraction
							// using the getRewardPoints() method to calculate the reward points
							user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
						});
			});

		}, executor);
	}

	@Override
	public void awaitCalculateRewardsEnding() {
		try {
			executor.shutdown();
			executor.awaitTermination(15, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	@Override
	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	@Override
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	@Override
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
