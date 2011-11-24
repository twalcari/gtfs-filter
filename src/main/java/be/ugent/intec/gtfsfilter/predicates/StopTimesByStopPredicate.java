package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;

import com.google.common.base.Predicate;

public final class StopTimesByStopPredicate implements
		Predicate<StopTime> {

	private final Collection<Stop> stops;

	public StopTimesByStopPredicate(Collection<Stop> stops) {
		super();
		this.stops = stops;
	}

	@Override
	public boolean apply(StopTime input) {
		return stops.contains(input.getStop());
	}
}