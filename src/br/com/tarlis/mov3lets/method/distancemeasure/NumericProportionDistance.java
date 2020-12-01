/**
 * Wizard - Multiple Aspect Trajectory (MASTER) Classification. 
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

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.model.aspect.Aspect;

/**
 * The Class NumericProportionDistance.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class NumericProportionDistance extends NumericDifferenceDistance {
	
	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.distancemeasure.NumericDifferenceDistance#distance(br.com.tarlis.mov3lets.model.aspect.Aspect, br.com.tarlis.mov3lets.model.aspect.Aspect, br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor).
	 * 
	 * @param asp0
	 * @param asp1
	 * @param attr
	 * @return
	 */
	@Override
	public double distance(Aspect<Double> asp0, Aspect<Double> asp1, AttributeDescriptor attr) {
		if (asp0.getValue() == 0 && asp1.getValue() == 0)
			return 1;
		else 
			return Math.abs(asp0.getValue() - asp1.getValue()) / Math.abs(asp0.getValue() + asp1.getValue());
	}

}
