/**
 * 
 */
package br.com.tarlis.mov3lets.method.distancemeasure;

import java.time.LocalDate;

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.model.aspect.Aspect;

/**
 * The Class LocaldateIsworkdayDistance.
 *
 * @author tarlis
 */
public class LocaldateIsworkdayDistance extends LocaldateIsworkdayorweekendDistance {
	
	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.distancemeasure.LocaldateIsworkdayorweekendDistance#distance(br.com.tarlis.mov3lets.model.aspect.Aspect, br.com.tarlis.mov3lets.model.aspect.Aspect, br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor).
	 * 
	 * @param asp0
	 * @param asp1
	 * @param attr
	 * @return
	 */
	@Override
	public double distance(Aspect<LocalDate> asp0, Aspect<LocalDate> asp1, AttributeDescriptor attr) {
		return (isWeekend(asp0.getValue()) && isWeekend(asp1.getValue()))? 1 : 0;
	}

}
