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
package br.com.tarlis.mov3lets.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Moving object can be anything, including an instance of MovingObject.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <MO> the generic type
 */
public class MAT<MO> {

	/** The tid. */
	private int tid = -1;
	
	/** The moving object. */
	private MO movingObject;
	
	/** The aspects. */
	private List<?> aspects = null;
	
	/** The points. */
	private List<Point> points = new ArrayList<Point>();
	
	/** The attributes. */
	// Attributtes resulting of method discovery.
	private Map<String,Double> attributes = new ConcurrentHashMap<>();
	
	/**
	 * Gets the tid.
	 *
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}
	
	/**
	 * Sets the tid.
	 *
	 * @param tid the tid to set
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}
	
	/**
	 * Gets the moving object.
	 *
	 * @return the movingObject
	 */
	public MO getMovingObject() {
		return movingObject;
	}
	
	/**
	 * Sets the moving object.
	 *
	 * @param movingObject the movingObject to set
	 */
	public void setMovingObject(MO movingObject) {
		this.movingObject = movingObject;
	}
	
	/**
	 * Gets the aspects.
	 *
	 * @return the aspects
	 */
	public List<?> getAspects() {
		return aspects;
	}
	
	/**
	 * Sets the aspects.
	 *
	 * @param aspects the aspects to set
	 */
	public void setAspects(List<?> aspects) {
		this.aspects = aspects;
	}
	
	/**
	 * Gets the points.
	 *
	 * @return the points
	 */
	public List<Point> getPoints() {
		return points;
	}
	
	/**
	 * Sets the points.
	 *
	 * @param points the points to set
	 */
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	
	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public Map<String, Double> getAttributes() {
		return attributes;
	}
	
	/**
	 * Sets the attributes.
	 *
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, Double> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Overridden method. 
	 * @see java.lang.Object#toString().
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		String string = new String();

		string += "Label: " + getMovingObject().toString() + "\n";
		string += "Points: \n";

		for (int i = 0; i < getPoints().size(); i++)
			string += getPoints().get(i).toString() + "\n";

		return string;
	}

}
