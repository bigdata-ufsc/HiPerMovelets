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

import br.com.tarlis.mov3lets.model.aspect.Aspect;

/**
 * The Class Point.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class Point {
	
	/** The id. */
	private int id;
	
	/** The trajectory. */
	private MAT<?> trajectory;
	
	/** The aspects. */
	private List<Aspect<?>> aspects = new ArrayList<Aspect<?>>();
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the trajectory.
	 *
	 * @return the trajectory
	 */
	public MAT<?> getTrajectory() {
		return trajectory;
	}
	
	/**
	 * Sets the trajectory.
	 *
	 * @param trajectory the trajectory to set
	 */
	public void setTrajectory(MAT<?> trajectory) {
		this.trajectory = trajectory;
	}
	
	/**
	 * Gets the aspects.
	 *
	 * @return the aspects
	 */
	public List<Aspect<?>> getAspects() {
		return aspects;
	}
	
	/**
	 * Sets the aspects.
	 *
	 * @param aspects the aspects to set
	 */
	public void setAspects(List<Aspect<?>> aspects) {
		this.aspects = aspects;
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
		string += "{";
		int i = 0;
		for (; i < aspects.size()-1; i++) {
			string += i + ": " + aspects.get(i).toString() + ",";
		}
		string += i + ": " + aspects.get(i).toString() + "}";
		
		return string;
	}

}
