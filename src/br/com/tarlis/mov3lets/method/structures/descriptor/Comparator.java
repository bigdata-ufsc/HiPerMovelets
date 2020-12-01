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
package br.com.tarlis.mov3lets.method.structures.descriptor;

/**
 * The Class Comparator.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class Comparator {

	/** The distance. */
	private String distance;
    
    /** The max value. */
    private Double maxValue = -1.0;
    
    /**
     * Instantiates a new comparator.
     */
    public Comparator() {

	}
    
	/**
	 * Instantiates a new comparator.
	 *
	 * @param distance the distance
	 * @param maxValue the max value
	 */
	public Comparator(String distance, Double maxValue) {
		this.distance = distance;
		this.maxValue = maxValue;
	}



	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}
	
	/**
	 * Sets the distance.
	 *
	 * @param distance the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance != null? distance.trim().toLowerCase() : distance;
	}
	
	/**
	 * Gets the max value.
	 *
	 * @return the maxValue
	 */
	public Double getMaxValue() {
		return maxValue;
	}
	
	/**
	 * Sets the max value.
	 *
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	
	/**
	 * Overridden method. 
	 * @see java.lang.Object#toString().
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return getDistance() +"/"+ getMaxValue();
	}

}
