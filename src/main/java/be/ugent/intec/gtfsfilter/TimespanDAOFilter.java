package be.ugent.intec.gtfsfilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsDao;

import be.ugent.intec.gtfsfilter.predicates.FrequencyByTripPredicate;
import be.ugent.intec.gtfsfilter.predicates.ServiceCalendarByServiceIdPredicate;
import be.ugent.intec.gtfsfilter.predicates.ServiceCalendarDateByServiceIdPredicate;
import be.ugent.intec.gtfsfilter.predicates.ShapePointsByShapeIdPredicate;
import be.ugent.intec.gtfsfilter.predicates.StopTimeByTripsPredicate;
import be.ugent.intec.gtfsfilter.predicates.TripByServiceIdPredicate;
import be.ugent.intec.gtfsfilter.transformers.ServiceCalendarDateToServiceIdFunction;
import be.ugent.intec.gtfsfilter.transformers.ServiceCalendarToServiceIdFunction;
import be.ugent.intec.gtfsfilter.transformers.StopTimeToStopFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToRouteFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToShapeIdFunction;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class TimespanDAOFilter extends GtfsDaoFilter {

	private final ServiceDate start, end;

	private Collection<ServiceCalendar> calendars;
	private Collection<ServiceCalendarDate> calendarDates;
	private Set<AgencyAndId> serviceIds;
	private Set<AgencyAndId> shapeIds;
	private Collection<Trip> trips;
	private Set<Route> routes;
	private Collection<StopTime> stoptimes;
	private Set<Stop> stops;

	public TimespanDAOFilter(GtfsDao input, ServiceDate oneDay) {
		this(input, oneDay, oneDay);
	}

	public TimespanDAOFilter(GtfsDao input, final ServiceDate start,
			final ServiceDate end) {
		super(input);
		this.start = start;
		this.end = end;

		// filter calendars and calendardates
		calendars = Collections2.filter(input.getAllCalendars(),
				new Predicate<ServiceCalendar>() {
					@Override
					public boolean apply(ServiceCalendar input) {
						return start.compareTo(input.getEndDate()) <= 0
								&& end.compareTo(input.getStartDate()) >= 0;
					}
				});

		calendarDates = Collections2.filter(input.getAllCalendarDates(),
				new Predicate<ServiceCalendarDate>() {
					@Override
					public boolean apply(ServiceCalendarDate input) {
						return start.compareTo(input.getDate()) <= 0
								&& end.compareTo(input.getDate()) >= 0;
					}
				});

		serviceIds = new HashSet<>();

		serviceIds.addAll(Collections2.transform(calendars,
				new ServiceCalendarToServiceIdFunction()));
		serviceIds.addAll(Collections2.transform(calendarDates,
				new ServiceCalendarDateToServiceIdFunction()));

		trips = Collections2.filter(input.getAllTrips(),
				new TripByServiceIdPredicate(serviceIds));

		routes = new HashSet<>();
		routes.addAll(Collections2.transform(trips, new TripToRouteFunction()));

		stoptimes = Collections2.filter(input.getAllStopTimes(),
				new StopTimeByTripsPredicate(trips));

		stops = new HashSet<>();
		stops.addAll(Collections2.transform(stoptimes,
				new StopTimeToStopFunction()));
		
		shapeIds = new HashSet<>();
		shapeIds.addAll(Collections2.transform(trips, new TripToShapeIdFunction()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllCalendars()
	 */
	@Override
	public Collection<ServiceCalendar> getAllCalendars() {
		return Collections2.filter(super.getAllCalendars(),
				new ServiceCalendarByServiceIdPredicate(serviceIds));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllCalendarDates()
	 */
	@Override
	public Collection<ServiceCalendarDate> getAllCalendarDates() {
		return Collections2.filter(super.getAllCalendarDates(),
				new ServiceCalendarDateByServiceIdPredicate(serviceIds));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllFrequencies()
	 */
	@Override
	public Collection<Frequency> getAllFrequencies() {
		return Collections2.filter(super.getAllFrequencies(),
				new FrequencyByTripPredicate(trips));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllRoutes()
	 */
	@Override
	public Collection<Route> getAllRoutes() {
		return routes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllShapePoints()
	 */
	@Override
	public Collection<ShapePoint> getAllShapePoints() {
		return Collections2.filter(super.getAllShapePoints(),
				new ShapePointsByShapeIdPredicate(shapeIds));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllStops()
	 */
	@Override
	public Collection<Stop> getAllStops() {
		return stops;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllStopTimes()
	 */
	@Override
	public Collection<StopTime> getAllStopTimes() {
		return stoptimes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllTrips()
	 */
	@Override
	public Collection<Trip> getAllTrips() {
		return trips;
	}

}
