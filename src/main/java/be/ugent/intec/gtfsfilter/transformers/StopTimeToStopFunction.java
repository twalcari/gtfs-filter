package be.ugent.intec.gtfsfilter.transformers;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;

import com.google.common.base.Function;

public final class StopTimeToStopFunction implements
		Function<StopTime, Stop> {
	@Override
	public Stop apply(StopTime input) {
		return input.getStop();
	}
}