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
import org.onebusaway.gtfs.services.GtfsDao;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class TramDaoFilter extends GtfsDaoFilter {
	private static final Logger LOG = LoggerFactory
			.getLogger(TramDaoFilter.class);
	protected static final int TRAM_TYPE = 0;

	private Set<Route> tramRoutes;
	private Set<Stop> tramStops;
	private Collection<Trip> filteredTrips = null;
	private Collection<StopTime> filteredStopTimes = null;

	private Set<AgencyAndId> serviceIds;
	private Set<AgencyAndId> shapeIds;

	public TramDaoFilter(GtfsDao dao) {
		super(dao);

		tramRoutes = new HashSet<>();
		tramRoutes.addAll(Collections2.filter(dao.getAllRoutes(),
				new Predicate<Route>() {
					@Override
					public boolean apply(Route input) {
						return input.getType() == TRAM_TYPE;
					}
				}));

		LOG.info("Filtered out {} tram routes", tramRoutes.size());

		tramStops = new HashSet<>();
		tramStops.addAll(Collections2.transform(getAllStopTimes(),
				new Function<StopTime, Stop>() {
					@Override
					public Stop apply(StopTime input) {
						return input.getStop();
					}
				}));
		LOG.info("Filtered out {} tram stops", tramStops.size());

		serviceIds = new HashSet<>();
		serviceIds.addAll(Collections2.transform(getAllTrips(),
				new Function<Trip, AgencyAndId>() {
					@Override
					public AgencyAndId apply(Trip input) {
						return input.getServiceId();
					}
				}));

		shapeIds = new HashSet<>();
		shapeIds.addAll(Collections2.transform(getAllTrips(),
				new Function<Trip, AgencyAndId>() {
					@Override
					public AgencyAndId apply(Trip input) {
						return input.getShapeId();
					}
				}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllRoutes()
	 */
	@Override
	public Collection<Route> getAllRoutes() {
		return tramRoutes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllTrips()
	 */
	@Override
	public synchronized Collection<Trip> getAllTrips() {
		if (filteredTrips == null) {
			filteredTrips = Collections2.filter(super.getAllTrips(),
					new Predicate<Trip>() {

						@Override
						public boolean apply(Trip input) {
							return tramRoutes.contains(input.getRoute());
						}
					});
		}
		return filteredTrips;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllStopTimes()
	 */
	@Override
	public synchronized Collection<StopTime> getAllStopTimes() {
		if (filteredStopTimes == null) {
			filteredStopTimes = Collections2.filter(super.getAllStopTimes(),
					new Predicate<StopTime>() {

						@Override
						public boolean apply(StopTime input) {
							return tramRoutes.contains(input.getTrip()
									.getRoute());
						}
					});
		}
		return filteredStopTimes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllStops()
	 */
	@Override
	public Collection<Stop> getAllStops() {
		return tramStops;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllCalendars()
	 */
	@Override
	public Collection<ServiceCalendar> getAllCalendars() {
		return Collections2.filter(super.getAllCalendars(),
				new Predicate<ServiceCalendar>() {
					@Override
					public boolean apply(ServiceCalendar input) {
						return serviceIds.contains(input.getServiceId());
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllCalendarDates()
	 */
	@Override
	public Collection<ServiceCalendarDate> getAllCalendarDates() {
		return Collections2.filter(super.getAllCalendarDates(),
				new Predicate<ServiceCalendarDate>() {
					@Override
					public boolean apply(ServiceCalendarDate input) {
						return serviceIds.contains(input.getServiceId());
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllFrequencies()
	 */
	@Override
	public Collection<Frequency> getAllFrequencies() {
		return Collections2.filter(super.getAllFrequencies(),
				new Predicate<Frequency>() {
					@Override
					public boolean apply(Frequency input) {
						return getAllTrips().contains(input.getTrip());
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ugent.intec.gtfsfilter.GtfsDaoFilter#getAllShapePoints()
	 */
	@Override
	public Collection<ShapePoint> getAllShapePoints() {
		return Collections2.filter(super.getAllShapePoints(),
				new Predicate<ShapePoint>() {
					@Override
					public boolean apply(ShapePoint input) {
						return shapeIds.contains(input.getShapeId());
					}
				});
	}

}
