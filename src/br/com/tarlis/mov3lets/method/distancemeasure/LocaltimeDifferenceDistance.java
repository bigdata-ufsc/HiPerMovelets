/**
 * 
 */
package br.com.tarlis.mov3lets.method.distancemeasure;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.model.aspect.Aspect;

/**
 * The Class LocaltimeDifferenceDistance.
 *
 * @author tarlis
 */
public class LocaltimeDifferenceDistance extends DistanceMeasure<Aspect<LocalTime>> {

	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.distancemeasure.DistanceMeasure#distance(br.com.tarlis.mov3lets.model.aspect.Aspect, br.com.tarlis.mov3lets.model.aspect.Aspect, br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor).
	 * 
	 * @param asp0
	 * @param asp1
	 * @param attr
	 * @return
	 */
	@Override
	public double distance(Aspect<LocalTime> asp0, Aspect<LocalTime> asp1, AttributeDescriptor attr) {
		return ChronoUnit.MILLIS.between(asp0.getValue(), asp1.getValue()) / 1000;
	}

}
