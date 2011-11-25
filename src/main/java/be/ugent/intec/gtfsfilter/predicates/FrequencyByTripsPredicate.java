package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Trip;

import com.google.common.base.Predicate;

public final class FrequencyByTripsPredicate implements
		Predicate<Frequency> {
	private final Collection<Trip> trips;
	public FrequencyByTripsPredicate(Collection<Trip> trips) {
		this.trips = trips;
	}

	@Override
	public boolean apply(Frequency input) {
		return trips.contains(input.getTrip());
	}
}