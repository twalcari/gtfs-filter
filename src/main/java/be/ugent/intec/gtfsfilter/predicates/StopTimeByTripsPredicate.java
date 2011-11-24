package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

import com.google.common.base.Predicate;

public class StopTimeByTripsPredicate implements Predicate<StopTime> {
	private final Collection<Trip> trips;

	public StopTimeByTripsPredicate(Collection<Trip> trips) {
		this.trips = trips;
	}

	@Override
	public boolean apply(StopTime input) {
		return trips.contains(input.getTrip());
	}

}
