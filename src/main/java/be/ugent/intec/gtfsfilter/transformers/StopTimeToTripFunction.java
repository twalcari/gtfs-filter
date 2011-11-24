package be.ugent.intec.gtfsfilter.transformers;

import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;

import com.google.common.base.Function;

public final class StopTimeToTripFunction implements
		Function<StopTime, Trip> {
	@Override
	public Trip apply(StopTime input) {
		return input.getTrip();
	}
}