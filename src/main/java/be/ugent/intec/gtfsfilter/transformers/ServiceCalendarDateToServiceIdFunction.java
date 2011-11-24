package be.ugent.intec.gtfsfilter.transformers;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;

import com.google.common.base.Function;

public final class ServiceCalendarDateToServiceIdFunction implements
		Function<ServiceCalendarDate, AgencyAndId> {
	@Override
	public AgencyAndId apply(ServiceCalendarDate input) {
		return input.getServiceId();
	}
}