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
import br.com.tarlis.mov3lets.model.aspect.Space2DAspect;

/**
 * The Class Space2dEuclideanDistance.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class Space2dEuclideanDistance extends DistanceMeasure<Space2DAspect> {

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
	public double distance(Space2DAspect asp0, Space2DAspect asp1, AttributeDescriptor attr) {
		return normalizeDistance(euclideanDistance(asp0, asp1), attr.getComparator().getMaxValue());
	}

	/**
	 * Euclidean distance.
	 *
	 * @param asp0 the asp 0
	 * @param asp1 the asp 1
	 * @return the double
	 */
	public double euclideanDistance(Space2DAspect asp0, Space2DAspect asp1){
		
		double diffLat = Math.abs(asp0.getX() - asp1.getX());
		double diffLon = Math.abs(asp0.getY() - asp1.getY());
		
		return Math.sqrt( diffLat * diffLat + diffLon * diffLon );

	}
	
}
