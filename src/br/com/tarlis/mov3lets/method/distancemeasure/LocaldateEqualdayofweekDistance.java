/**
 * 
 */
package br.com.tarlis.mov3lets.method.distancemeasure;

import java.time.LocalDate;

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.model.aspect.Aspect;

/**
 * The Class LocaldateEqualdayofweekDistance.
 *
 * @author tarlis
 */
public class LocaldateEqualdayofweekDistance extends DistanceMeasure<Aspect<LocalDate>> {

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
	public double distance(Aspect<LocalDate> asp0, Aspect<LocalDate> asp1, AttributeDescriptor attr) {
		return (asp0.getValue().getDayOfWeek() == asp1.getValue().getDayOfWeek())? 0 : 1;
	}

}
