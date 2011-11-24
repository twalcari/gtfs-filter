package be.ugent.intec.gtfsfilter.transformers;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;

import com.google.common.base.Function;

public final class ServiceCalendarToServiceIdFunction implements
		Function<ServiceCalendar, AgencyAndId> {
	@Override
	public AgencyAndId apply(ServiceCalendar input) {
		return input.getServiceId();
	}
}