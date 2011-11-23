package be.ugent.intec.gtfsfilter;

import java.io.Serializable;
import java.util.Collection;

import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsDao;

public abstract class GtfsDaoFilter implements GtfsDao {

	protected final GtfsDao input;

	protected GtfsDaoFilter(GtfsDao input) {
		this.input = input;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Collection<T> getAllEntitiesForType(Class<T> type) {
		
		if(type == Agency.class)
			return (Collection<T>) getAllAgencies();
		else if(type == ShapePoint.class)
			return (Collection<T>) getAllShapePoints();
		else if(type == Route.class)
			return (Collection<T>) getAllRoutes();
		else if(type == Stop.class)
			return (Collection<T>) getAllStops();
		else if(type == Trip.class)
			return (Collection<T>) getAllTrips();
		else if (type == StopTime.class)
			return (Collection<T>) getAllStopTimes();
		else if (type == ServiceCalendar.class)
			return (Collection<T>) getAllCalendars();
		else if (type == ServiceCalendarDate.class)
			return (Collection<T>) getAllCalendarDates();
		else if(type ==FareAttribute.class)
			return (Collection<T>) getAllFareAttributes();
		else if(type == FareRule.class)
			return (Collection<T>) getAllFareRules();
		else if(type == Frequency.class)
			return (Collection<T>) getAllFrequencies();
		else if (type == Pathway.class)
			return (Collection<T>) getAllPathways();
		else if (type == Transfer.class)
			return (Collection<T>) getAllTransfers();
		else
			throw new IllegalArgumentException("Unknown class");
	}

	@Override
	public <T> T getEntityForId(Class<T> type, Serializable id) {
		return input.getEntityForId(type, id);
	}

	@Override
	public Collection<Agency> getAllAgencies() {
		return input.getAllAgencies();
	}

	@Override
	public Agency getAgencyForId(String id) {
		return input.getAgencyForId(id);
	}

	@Override
	public Collection<ServiceCalendar> getAllCalendars() {
		return input.getAllCalendars();
	}

	@Override
	public ServiceCalendar getCalendarForId(int id) {
		return input.getCalendarForId(id);
	}

	@Override
	public Collection<ServiceCalendarDate> getAllCalendarDates() {
		return input.getAllCalendarDates();
	}

	@Override
	public ServiceCalendarDate getCalendarDateForId(int id) {
		return input.getCalendarDateForId(id);
	}

	@Override
	public Collection<FareAttribute> getAllFareAttributes() {
		return input.getAllFareAttributes();
	}

	@Override
	public FareAttribute getFareAttributeForId(AgencyAndId id) {
		return input.getFareAttributeForId(id);
	}

	@Override
	public Collection<FareRule> getAllFareRules() {
		return input.getAllFareRules();
	}

	@Override
	public FareRule getFareRuleForId(int id) {
		return input.getFareRuleForId(id);
	}

	@Override
	public Collection<Frequency> getAllFrequencies() {
		return input.getAllFrequencies();
	}

	@Override
	public Frequency getFrequencyForId(int id) {
		return input.getFrequencyForId(id);
	}

	@Override
	public Collection<Pathway> getAllPathways() {
		return input.getAllPathways();
	}

	@Override
	public Pathway getPathwayForId(AgencyAndId id) {
		return input.getPathwayForId(id);
	}

	@Override
	public Collection<Route> getAllRoutes() {
		return input.getAllRoutes();
	}

	@Override
	public Route getRouteForId(AgencyAndId id) {
		return input.getRouteForId(id);
	}

	@Override
	public Collection<ShapePoint> getAllShapePoints() {
		return input.getAllShapePoints();
	}

	@Override
	public ShapePoint getShapePointForId(int id) {
		return input.getShapePointForId(id);
	}

	@Override
	public Collection<Stop> getAllStops() {
		return input.getAllStops();
	}

	@Override
	public Stop getStopForId(AgencyAndId id) {
		return input.getStopForId(id);
	}

	@Override
	public Collection<StopTime> getAllStopTimes() {
		return input.getAllStopTimes();
	}

	@Override
	public StopTime getStopTimeForId(int id) {
		return input.getStopTimeForId(id);
	}

	@Override
	public Collection<Transfer> getAllTransfers() {
		return input.getAllTransfers();
	}

	@Override
	public Transfer getTransferForId(int id) {
		return input.getTransferForId(id);
	}

	@Override
	public Collection<Trip> getAllTrips() {
		return input.getAllTrips();
	}

	@Override
	public Trip getTripForId(AgencyAndId id) {
		return input.getTripForId(id);
	}

}
