package edu.uw.advjava.wilsonma;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.uw.pce.advjava.services.SightingsUtil;
import edu.uw.pce.advjava.sighting.UfoShape;
import edu.uw.pce.advjava.sighting.UfoSighting;

public class SightingsUtilImpl implements SightingsUtil {
	private Set<UfoSighting> sightings;
	private Comparator<UfoSighting> comparator = (ufoA, ufoB) -> 
		ufoA.id() < ufoB.id() ? 1 : (ufoA.id() == ufoB.id() ? 0 : -1);
	
	public SightingsUtilImpl() {
		sightings = new TreeSet<UfoSighting>(comparator);
	}
	
	public void addSighting(UfoSighting sighting) {
		sightings.add(sighting);
	}
	
	private Stream<UfoSighting> getSightingsByDateRange(LocalDate startDate, LocalDate endDate) {
		LocalDate adjustedStart = startDate.minusDays(1);
		LocalDate adjustedEnd = endDate.plusDays(1);
		return sightings.stream()
				.filter(ufo -> 
					ufo.dateDocumented().isAfter(adjustedStart) && 
					ufo.dateDocumented().isBefore(adjustedEnd));
	}
	
	/** 
	 * Count the number of sightings occurring between the specified dates. 
	 * 
	 * @param startDate The start range for the sightings.
	 * @param endDate The end range for the sightings.
	 * @returns The total number of sightings in [startDate, endDate].
	 */
	@Override
	public long countSigthings(LocalDate startDate, LocalDate endDate) {
		return getSightingsByDateRange(startDate, endDate).count();
	}

	/** 
	 * Obtain the longest duration of an observation occurring between the specified dates. 
	 * 
	 * @param startDate The start range for the sightings.
	 * @param endDate The end range for the sightings.
	 * @returns The sighting with the maximum duration in [startDate, endDate].
	 */
	@Override
	public double maxSightingDuration(LocalDate startDate, LocalDate endDate) {
		return getSightingsByDateRange(startDate, endDate)
				.mapToDouble(UfoSighting::encounterDuration)
				.max()
				.orElse(0);
	}

	/** 
	 * Obtains a subset of signtings occurring between the specified dates and having the specified shape.
	 * 
	 * @param startDate The start range for the sightings.
	 * @param endDate The end range for the sightings.
	 * @param shape The shape of Ufo objects that we expect to return.
	 * @return The set of Ufo objects that are in [startDate, endDate] and also of the expected shape.
	 */
	@Override
	public Set<UfoSighting> sightingsByShape(LocalDate startDate, LocalDate endDate, UfoShape shape) {
		 Stream<UfoSighting> filteredSightings = getSightingsByDateRange(startDate, endDate)
				.filter(ufo -> ufo.ufoShape()
							.equals(shape));
		 return filteredSightings.collect(Collectors.toCollection(() -> new TreeSet<UfoSighting>(comparator)));
	}

}
