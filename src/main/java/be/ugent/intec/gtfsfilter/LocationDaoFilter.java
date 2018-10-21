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
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.intec.gtfsfilter.predicates.FrequencyByTripsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ServiceCalendarByServiceIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ServiceCalendarDateByServiceIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.ShapePointsByShapeIdsPredicate;
import be.ugent.intec.gtfsfilter.predicates.StopTimesByStopsPredicate;
import be.ugent.intec.gtfsfilter.predicates.TransfersByStopsPredicate;
import be.ugent.intec.gtfsfilter.transformers.StopTimeToTripFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToRouteFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToServiceIdFunction;
import be.ugent.intec.gtfsfilter.transformers.TripToShapeIdFunction;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class LocationDaoFilter extends GtfsDaoFilter {
	private static final Logger LOG = LoggerFactory
			.getLogger(LocationDaoFilter.class);

	private final double minlat, minlon, maxlat, maxlon;

	private final Set<Stop> stops;
	private final Collection<StopTime> stoptimes;
	private final Set<Trip> trips;
	private final Set<Route> routes;
	private final Collection<Transfer> transfers;

	private final Set<AgencyAndId> serviceIds;
	private final Set<AgencyAndId> shapeIds;

	public LocationDaoFilter(GtfsDao input, final double minlat,
			final double minlon, final double maxlat, final double maxlon) {
		super(input);
		this.minlat = minlat;
		this.minlon = minlon;
		this.maxlat = maxlat;
		this.maxlon = maxlon;

		this.stops = new HashSet<>();
		stops.addAll(Collections2.filter(input.getAllStops(),
				new Predicate<Stop>() {
					@Override
					public boolean apply(Stop input) {
						return input.getLat() > minlat
								&& input.getLon() > minlon
								&& input.getLat() < maxlat
								&& input.getLon() < maxlon;
					}
				}));

		LOG.info("Filtered down from {} to {} stops", input.getAllStops()
				.size(), stops.size());

		this.stoptimes = (Collections2.filter(input.getAllStopTimes(),
				new StopTimesByStopsPredicate(stops)));

		LOG.info("Filtered down from {} to {} stoptimes", input
				.getAllStopTimes().size(), stoptimes.size());

		this.trips = new HashSet<>();
		trips.addAll(Collections2.transform(stoptimes,
				new StopTimeToTripFunction()));

		LOG.info("Filtered down from {} to {} trips", input.getAllTrips()
				.size(), trips.size());

		this.routes = new HashSet<>();
		routes.addAll(Collections2.transform(trips, new TripToRouteFunction()));
		LOG.info("Filtered down from {} to {} routes", input.getAllRoutes()
				.size(), routes.size());

		serviceIds = new HashSet<>();
		serviceIds.addAll(Collections2.transform(trips,
				new TripToServiceIdFunction()));

		LOG.info("Filtered down to {} serviceIds", serviceIds.size());

		shapeIds = new HashSet<>();
		shapeIds.addAll(Collections2.transform(trips,
				new TripToShapeIdFunction()));

		LOG.info("Filtered down to {} shapeIds", shapeIds.size());

		this.transfers = (Collections2.filter(input.getAllTransfers(),
				new TransfersByStopsPredicate(stops)));

		LOG.info("Filtered down from {} to {} transfers", input
				.getAllTransfers().size(), transfers.size());
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
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllTrips()
	 */
	@Override
	public Collection<Trip> getAllTrips() {
		return trips;
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
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllStopTimes()
	 */
	@Override
	public Collection<StopTime> getAllStopTimes() {
		return stoptimes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllTransfers()
	 */
	@Override
	public Collection<Transfer> getAllTransfers() {
		return transfers;
	}
}
