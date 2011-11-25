package be.ugent.intec.gtfsfilter;

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
import org.onebusaway.gtfs.services.GtfsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.intec.gtfsfilter.predicates.FrequencyByTripsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ServiceCalendarByServiceIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ServiceCalendarDateByServiceIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ShapePointsByShapeIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.StopTimeByRoutesPredicate;
import be.ugent.intec.gtfsfilter.predicates.TripByRoutesPredicate;
import be.ugent.intec.gtfsfilter.transformers.StopTimeToStopFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToServiceIdFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToShapeIdFunction;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.primitives.Ints;

public class TransportTypeDaoFilter extends GtfsDaoFilter {
	private static final Logger LOG = LoggerFactory
			.getLogger(TransportTypeDaoFilter.class);
	public static final int TRAM_TYPE = 0;

	private final Set<Route> routes;
	private final Set<Stop> stops;
	private final Collection<Trip> trips;
	private final Collection<StopTime> stoptimes;

	private final Set<AgencyAndId> serviceIds;
	private final Set<AgencyAndId> shapeIds;

	public TransportTypeDaoFilter(GtfsDao dao, final int... transportTypes) {
		super(dao);

		Preconditions.checkArgument(transportTypes.length > 0);

		routes = new HashSet<>();
		routes.addAll(Collections2.filter(dao.getAllRoutes(),
				new Predicate<Route>() {
					List<Integer> list = Ints.asList(transportTypes);

					@Override
					public boolean apply(Route input) {
						return input.getType() == TRAM_TYPE;
					}
				}));

		LOG.info("Filtered down from {} to {} routes", input.getAllRoutes()
				.size(), routes.size());

		trips = Collections2.filter(super.getAllTrips(),
				new TripByRoutesPredicate(routes));

		LOG.info("Filtered down from {} to {} trips", super.getAllTrips()
				.size(), trips.size());

		stoptimes = Collections2.filter(super.getAllStopTimes(),
				new StopTimeByRoutesPredicate(routes));
		LOG.info("Filtered down from {} to {} stoptimes", input
				.getAllStopTimes().size(), stoptimes.size());

		stops = new HashSet<>();
		stops.addAll(Collections2.transform(stoptimes,
				new StopTimeToStopFunction()));
		LOG.info("Filtered down from {} to {} stops", input.getAllStops()
				.size(), stops.size());

		serviceIds = new HashSet<>();
		serviceIds.addAll(Collections2.transform(trips,
				new TripToServiceIdFunction()));

		LOG.info("Filtered down to {} serviceIds", serviceIds.size());

		shapeIds = new HashSet<>();
		shapeIds.addAll(Collections2.transform(trips,
				new TripToShapeIdFunction()));

		LOG.info("Filtered down to {} shapeIds", shapeIds.size());
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
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllTrips()
	 */
	@Override
	public synchronized Collection<Trip> getAllTrips() {
		return trips;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllStopTimes()
	 */
	@Override
	public synchronized Collection<StopTime> getAllStopTimes() {
		return stoptimes;

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
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllCalendars()
	 */
	@Override
	public Collection<ServiceCalendar> getAllCalendars() {
		return Collections2.filter(super.getAllCalendars(),
				new ServiceCalendarByServiceIdsPredicate(serviceIds));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllCalendarDates()
	 */
	@Override
	public Collection<ServiceCalendarDate> getAllCalendarDates() {
		return Collections2.filter(super.getAllCalendarDates(),
				new ServiceCalendarDateByServiceIdsPredicate(serviceIds));
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
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllShapePoints()
	 */
	@Override
	public Collection<ShapePoint> getAllShapePoints() {
		return Collections2.filter(super.getAllShapePoints(),
				new ShapePointsByShapeIdsPredicate(shapeIds));
	}

}
