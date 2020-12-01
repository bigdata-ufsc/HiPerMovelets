/**
 * Mov3lets - Multiple Aspect Trajectory (MASTER) Classification Version 3. 
 * Copyright (C) 2019  Tarlis Portela <tarlis@tarlis.com.br>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package br.com.tarlis.mov3lets.method.distancemeasure;

import java.util.Date;

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.model.aspect.Aspect;

/**
 * The Class DatetimeDifferenceDistance.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class DatetimeDifferenceDistance extends DistanceMeasure<Aspect<Date>> {

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
	public double distance(Aspect<Date> asp0, Aspect<Date> asp1, AttributeDescriptor attr) {
		return normalizeDistance(
				  Math.abs( (double)(asp1.getValue().getTime() - asp1.getValue().getTime()))/1000, 
					attr.getComparator().getMaxValue()
			   );
	}

}
