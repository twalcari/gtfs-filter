package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.StopTime;

import com.google.common.base.Predicate;

public final class StopTimeByRoutesPredicate implements
		Predicate<StopTime> {
	private final Collection<Route> routes;

	public StopTimeByRoutesPredicate(Collection<Route> routes) {
		super();
		this.routes = routes;
	}

	@Override
	public boolean apply(StopTime input) {
		return routes.contains(input.getTrip().getRoute());
	}
}