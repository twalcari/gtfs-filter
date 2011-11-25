package be.ugent.intec.gtfsfilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.intec.gtfsfilter.predicates.FrequencyByTripsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ServiceCalendarByServiceIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ServiceCalendarDateByServiceIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ShapePointsByShapeIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.StopTimeByRoutesPredicate;
import be.ugent.intec.gtfsfilter.predicates.StopTimeByTripsPredicate;
import be.ugent.intec.gtfsfilter.predicates.TripByServiceIdsPredicate;
import be.ugent.intec.gtfsfilter.transformers.ServiceCalendarDateToServiceIdFunction;
import be.ugent.intec.gtfsfilter.transformers.ServiceCalendarToServiceIdFunction;
import be.ugent.intec.gtfsfilter.transformers.StopTimeToStopFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToRouteFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToShapeIdFunction;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class TimespanDaoFilter extends GtfsDaoFilter {
	private final Logger LOG = LoggerFactory.getLogger(TimespanDaoFilter.class);
	private final ServiceDate start, end;

	private List<ServiceCalendar> calendars;
	private Collection<ServiceCalendarDate> calendarDates;
	private Set<AgencyAndId> serviceIds;
	private Set<AgencyAndId> shapeIds;
	private Set<Trip> trips; //use set instead of collection to optimize stoptimes-filtering
	private Set<Route> routes;
	private Collection<StopTime> stoptimes;
	private Set<Stop> stops;

	public TimespanDaoFilter(GtfsDao input, ServiceDate oneDay) {
		this(input, oneDay, oneDay);
	}

	public TimespanDaoFilter(GtfsDao input, final ServiceDate start,
			final ServiceDate end) {
		super(input);
		this.start = start;
		this.end = end;

		// filter calendars and calendardates
		calendars = new ArrayList<>();
		calendars.addAll(Collections2.filter(input.getAllCalendars(),
				new Predicate<ServiceCalendar>() {
					@Override
					public boolean apply(ServiceCalendar input) {
						return start.compareTo(input.getEndDate()) <= 0
								&& end.compareTo(input.getStartDate()) >= 0;
					}
				}));
		
		//change the calendar begin- and enddates for consistency
		for(ServiceCalendar sc : calendars){
			if(start.compareTo(sc.getStartDate()) > 1){
				sc.setStartDate(start);
			}
			if(end.compareTo(sc.getEndDate()) < 1){
				sc.setEndDate(end);
			}
		}

		LOG.info("Filtered down to {} calendars", calendars.size());

		calendarDates = Collections2.filter(input.getAllCalendarDates(),
				new Predicate<ServiceCalendarDate>() {
					@Override
					public boolean apply(ServiceCalendarDate input) {
						return start.compareTo(input.getDate()) <= 0
								&& end.compareTo(input.getDate()) >= 0;
					}
				});

		LOG.info("Filtered down to {} calendardates", calendarDates.size());

		serviceIds = new HashSet<>();

		serviceIds.addAll(Collections2.transform(calendars,
				new ServiceCalendarToServiceIdFunction()));
		serviceIds.addAll(Collections2.transform(calendarDates,
				new ServiceCalendarDateToServiceIdFunction()));

		LOG.info("Filtered down to {} serviceIds", serviceIds.size());

		trips = new HashSet<>();
		trips.addAll(Collections2.filter(input.getAllTrips(),
				new TripByServiceIdsPredicate(serviceIds)));

		LOG.info("Filtered down from {} to {} trips", super.getAllTrips()
				.size(), trips.size());

		routes = new HashSet<>();
		routes.addAll(Collections2.transform(trips, new TripToRouteFunction()));

		LOG.info("Filtered down from {} to {} routes", input.getAllRoutes()
				.size(), routes.size());

		stoptimes = Collections2.filter(input.getAllStopTimes(),
				new StopTimeByRoutesPredicate(routes));
		LOG.info("Filtered down from {} to {} stoptimes after 1st pass", input
				.getAllStopTimes().size(), stoptimes.size());
		stoptimes = Collections2.filter(stoptimes,
				new StopTimeByTripsPredicate(trips));
		LOG.info("Filtered down to {} stoptimes after 2nd pass", stoptimes.size());

		stops = new HashSet<>();
		stops.addAll(Collections2.transform(stoptimes,
				new StopTimeToStopFunction()));

		LOG.info("Filtered down from {} to {} stops", input.getAllStops()
				.size(), stops.size());

		shapeIds = new HashSet<>();
		shapeIds.addAll(Collections2.transform(trips,
				new TripToShapeIdFunction()));

		LOG.info("Filtered down to {} shapeIds", shapeIds.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllCalendars()
	 */
	@Override
	public Collection<ServiceCalendar> getAllCalendars() {
		return calendars;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllCalendarDates()
	 */
	@Override
	public Collection<ServiceCalendarDate> getAllCalendarDates() {
		return calendarDates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllFrequencies()
	 */
	@Override
	public Collection<Frequency> getAllFrequencies() {
		return Collections2.filter(super.getAllFrequencies(),
				new FrequencyByTripsPredicate(trips));
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
				new ShapePointsByShapeIdsPredicate(shapeIds));
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
