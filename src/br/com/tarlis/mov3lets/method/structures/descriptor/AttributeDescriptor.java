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

import br.com.tarlis.mov3lets.method.distancemeasure.DistanceMeasure;

/**
 * The Class AttributeDescriptor.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class AttributeDescriptor {
	
	/** The order. */
	private Integer order;
    
    /** The type. */
    private String type;
    
    /** The text. */
    private String text;
    
    /** The comparator. */
    private Comparator comparator;
    
    /** The distance comparator. */
    private DistanceMeasure<?> distanceComparator = null;
    
    /**
     * Instantiates a new attribute descriptor.
     */
    public AttributeDescriptor() {
    	
	}
    
	/**
	 * Instantiates a new attribute descriptor.
	 *
	 * @param order the order
	 * @param type the type
	 * @param text the text
	 * @param distance the distance
	 * @param maxValue the max value
	 */
	public AttributeDescriptor(Integer order, String type, String text, String distance, Double maxValue) {
		this.order = order;
		this.type = type;
		this.text = text;
		this.comparator = new Comparator(distance, maxValue);
	}



	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}
	
	/**
	 * Sets the order.
	 *
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type != null? type.trim().toLowerCase() : type;
	}
	
	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets the text.
	 *
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Gets the comparator.
	 *
	 * @return the comparator
	 */
	public Comparator getComparator() {
		return comparator;
	}
	
	/**
	 * Sets the comparator.
	 *
	 * @param comparator the comparator to set
	 */
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
	
	/**
	 * Gets the distance comparator.
	 *
	 * @return the distanceComparator
	 */
	public DistanceMeasure<?> getDistanceComparator() {
		return distanceComparator;
	}
	
	/**
	 * Sets the distance comparator.
	 *
	 * @param distanceComparator the distanceComparator to set
	 */
	public void setDistanceComparator(DistanceMeasure<?> distanceComparator) {
		this.distanceComparator = distanceComparator;
	}
	
	/**
	 * Overridden method. 
	 * @see java.lang.Object#toString().
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return "Attr: " + getOrder() +" - "+ getText() +"/"+ getType() +" ("+getComparator()+")";
	}

}
