package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Transfer;

import com.google.common.base.Predicate;

public final class TransfersByStopsPredicate implements
		Predicate<Transfer> {

	private final Collection<Stop> stops;

	public TransfersByStopsPredicate(Collection<Stop> stops) {
		super();
		this.stops = stops;
	}

	@Override
	public boolean apply(Transfer input) {
		return stops.contains(input.getFromStop()) && stops.contains(input.getToStop());
	}
}