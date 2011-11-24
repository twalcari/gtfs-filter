package be.ugent.intec.gtfsfilter.transformers;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Trip;

import com.google.common.base.Function;

public final class TripToShapeIdFunction implements
		Function<Trip, AgencyAndId> {
	@Override
	public AgencyAndId apply(Trip input) {
		return input.getShapeId();
	}
}