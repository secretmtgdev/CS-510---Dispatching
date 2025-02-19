package edu.uw.advjava.wilsonma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.uw.pce.advjava.sighting.Season;
import edu.uw.pce.advjava.sighting.UfoShape;
import edu.uw.pce.advjava.sighting.UfoSighting;

public class SightingsUtilImplTests {
	private final int DEFAULT_DURATION = 10;
	private static AtomicInteger id = new AtomicInteger(0);
	private UfoSighting cylinder = createUfoByShape(UfoShape.CYLINDER);
	private UfoSighting circle = createUfoByShape(UfoShape.CIRCLE);
	private UfoSighting cone = createUfoByShape(UfoShape.CONE);
	private UfoSighting cross = createUfoByShape(UfoShape.CROSS);
	private UfoSighting diamond = createUfoByShape(UfoShape.DIAMOND);
	private UfoSighting disk = createUfoByShape(UfoShape.DISK);
	private LocalDate startDate = LocalDate.of(2025, 2, 5);
	private LocalDate endDate = LocalDate.of(2025, 2, 7);	
	
	private SightingsUtilImpl sightingsUtil;
	
	private UfoSighting createUfoByShape(UfoShape shape) {
		return new UfoSighting(
				id.getAndIncrement(),
				LocalDateTime.of(2025, 2, 6, 12, 53),
				LocalDate.of(2025, 2, 5),
				Season.WINTER,
				"US",
				"WA",
				"Seattle",
				47.6541,
				122.3080,
				shape,
				DEFAULT_DURATION,
				"UFO seen at the University of Washington, Seattle");
	}
	
	@BeforeEach
	public void init() {
		sightingsUtil = new SightingsUtilImpl();
		sightingsUtil.addSighting(circle);
		sightingsUtil.addSighting(cone);
		sightingsUtil.addSighting(cross);
		sightingsUtil.addSighting(cylinder);
		sightingsUtil.addSighting(diamond);
		sightingsUtil.addSighting(disk);
	}
	
	@AfterEach
	public void clean() {
		sightingsUtil = null;
	}
	
	@Disabled
	@Test
	public void verifyCountSightings() {
		long sightingsCount = sightingsUtil.countSigthings(startDate, endDate);
		assertEquals(6L, sightingsCount, "Expected sightings count to be 6");
	}

	@Disabled
	@Test
	public void verifySightingsByShape() {
		Set<UfoSighting> sightingsCount = sightingsUtil.sightingsByShape(startDate, endDate, UfoShape.CIRCLE);
		assertEquals(1, sightingsCount.size(), "Expected sightings count to be 1");
	}
	
	@Disabled
	@Test
	public void verifymaxSightingDuration() {
		double maxDuration = sightingsUtil.maxSightingDuration(startDate, endDate);
		assertEquals(DEFAULT_DURATION, maxDuration, "Expected max duration to be " + DEFAULT_DURATION);
	}
}
