package be.ugent.intec.gtfsfilter.predicates;

import java.util.Collection;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ShapePoint;

import com.google.common.base.Predicate;

public final class ShapePointsByShapeIdPredicate implements
		Predicate<ShapePoint> {

	private final Collection<AgencyAndId> shapeIds;

	public ShapePointsByShapeIdPredicate(Collection<AgencyAndId> shapeIds) {
		this.shapeIds = shapeIds;
	}

	@Override
	public boolean apply(ShapePoint input) {
		return shapeIds.contains(input.getShapeId());
	}
}