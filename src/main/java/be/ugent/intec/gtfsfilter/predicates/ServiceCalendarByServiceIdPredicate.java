package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;

import com.google.common.base.Predicate;

public final class ServiceCalendarByServiceIdPredicate implements
		Predicate<ServiceCalendar> {
	private final Collection<AgencyAndId> serviceIds;
	public ServiceCalendarByServiceIdPredicate(
			Collection<AgencyAndId> serviceIds) {
		this.serviceIds = serviceIds;
	}

	@Override
	public boolean apply(ServiceCalendar input) {
		return serviceIds.contains(input.getServiceId());
	}
}