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

/**
 * Implementation of the SightingsUtil interface.
 * 
 * @author Michael Wilson
 */
public class SightingsUtilImpl implements SightingsUtil {
	private Set<UfoSighting> sightings;
	private Comparator<UfoSighting> comparator = (ufoA, ufoB) -> 
		ufoA.id() < ufoB.id() ? 1 : (ufoA.id() == ufoB.id() ? 0 : -1);
	
	/**
	 * Default constructor for the Sightings implementation.
	 */
	public SightingsUtilImpl() {
		sightings = new TreeSet<UfoSighting>(comparator);
	}
	
	/**
	 * Adds a sighting record to the sightings.
	 * 
	 * @param sighting The sighting to record.
	 */
	public void addSighting(UfoSighting sighting) {
		sightings.add(sighting);
	}
	
	/** 
	 * Count the number of sightings occurring between the specified dates. 
	 * 
	 * @param startDate The start range for the sightings.
	 * @param endDate The end range for the sightings.
	 * @return The total number of sightings in between the startDate and endDate.
	 */
	@Override
	public long countSigthings(LocalDate startDate, LocalDate endDate) {
	      return UfoSighting.streamOf().filter(s -> (!s.observationTime().toLocalDate().isBefore(startDate) && 
                (!s.observationTime().toLocalDate().isAfter(endDate))))
               .count();
   }

	/** 
	 * Obtain the longest duration of an observation occurring between the specified dates. 
	 * 
	 * @param startDate The start range for the sightings.
	 * @param endDate The end range for the sightings.
	 * @return The sighting with the maximum duration in between the startDate and endDate.
	 */
	@Override
	public double maxSightingDuration(LocalDate startDate, LocalDate endDate) {
		return UfoSighting.streamOf().filter(s -> (!s.observationTime().toLocalDate().isBefore(startDate) && 
                        (!s.observationTime().toLocalDate().isAfter(endDate))))
           .mapToDouble(UfoSighting::encounterDuration)
           .max().orElse(0);
   }

	/** 
	 * Obtains a subset of signtings occurring between the specified dates and having the specified shape.
	 * 
	 * @param startDate The start range for the sightings.
	 * @param endDate The end range for the sightings.
	 * @param shape The shape of Ufo objects that we expect to return.
	 * @return The set of Ufo objects that are in between the startDate and endDate and also of the expected shape.
	 */
	@Override
	public Set<UfoSighting> sightingsByShape(LocalDate startDate, LocalDate endDate, UfoShape shape) {
		return UfoSighting.streamOf().filter(s -> (!s.observationTime().toLocalDate().isBefore(startDate) && 
                        (!s.observationTime().toLocalDate().isAfter(endDate))))
				.filter(s -> s.ufoShape() == shape)
				.collect(Collectors.toSet());
   }

}
