package be.ugent.intec.gtfsfilter.transformers;

import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;

import com.google.common.base.Function;

public final class TripToRouteFunction implements
		Function<Trip, Route> {
	@Override
	public Route apply(Trip input) {
		return input.getRoute();
	}
}