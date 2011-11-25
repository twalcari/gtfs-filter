package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Trip;

import com.google.common.base.Predicate;

public class TripByServiceIdsPredicate implements Predicate<Trip> {

	private final Collection<AgencyAndId> serviceIds;

	public TripByServiceIdsPredicate(Collection<AgencyAndId> serviceIds) {
		this.serviceIds = serviceIds;
	}

	@Override
	public boolean apply(Trip input) {
		return serviceIds.contains(input.getServiceId());
	}

}
