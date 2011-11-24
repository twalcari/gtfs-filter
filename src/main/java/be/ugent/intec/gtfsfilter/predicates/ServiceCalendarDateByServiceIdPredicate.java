package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendarDate;

import com.google.common.base.Predicate;

public final class ServiceCalendarDateByServiceIdPredicate implements
		Predicate<ServiceCalendarDate> {
	
	private final Collection<AgencyAndId> serviceIds;
	public ServiceCalendarDateByServiceIdPredicate(
			Collection<AgencyAndId> serviceIds) {
		this.serviceIds = serviceIds;
	}

	@Override
	public boolean apply(ServiceCalendarDate input) {
		return serviceIds.contains(input.getServiceId());
	}
}