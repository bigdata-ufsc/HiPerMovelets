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

/**
 * Moving object label can be anything.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <L> the generic type
 */
public class MovingObject<L> {
	
	/** The label. */
	private L label;
	
	/**
	 * Instantiates a new moving object.
	 *
	 * @param label the label
	 */
	public MovingObject(L label) {
		this.label = label;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public L getLabel() {
		return label;
	}
	
	/**
	 * Sets the label.
	 *
	 * @param label the label to set
	 */
	public void setLabel(L label) {
		this.label = label;
	}
	
	/**
	 * Overridden method. 
	 * @see java.lang.Object#toString().
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return getLabel().toString();
	}
	
	/**
	 * Overridden method. 
	 * @see java.lang.Object#equals(java.lang.Object).
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || this.label.equals(obj);
	}

}
