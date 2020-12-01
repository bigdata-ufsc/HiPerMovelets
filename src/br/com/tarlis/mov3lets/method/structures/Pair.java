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
package br.com.tarlis.mov3lets.method.structures;

/**
 * The Class Pair.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <M> the generic type
 * @param <N> the number type
 */
public class Pair<M, N> extends org.apache.commons.math3.util.Pair<M, N> {

	/**
	 * Instantiates a new pair.
	 *
	 * @param entry the entry
	 */
	public Pair(org.apache.commons.math3.util.Pair<? extends M, ? extends N> entry) {
		super(entry);
	}
	


    /**
     * Instantiates a new pair.
     *
     * @param x the x
     * @param y the y
     */
	public Pair(M x, N y) {
		super(x, y);
	}



	/**
     * Compare the specified object with this entry for equality.
     * Independent of the pair order.
     *
     * @param o Object.
     * @return {@code true} if the given object is also a map entry and
     * the two entries represent the same mapping.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair oP = (Pair) o;
            return (getFirst() == null ?
                    oP.getFirst() == null :
                    	getFirst().equals(oP.getFirst())) &&
	                (getSecond() == null ?
	                 oP.getSecond() == null :
	                	 getSecond().equals(oP.getSecond()))
	                ||
	                (getFirst() == null ?
                    oP.getSecond() == null :
                    	getFirst().equals(oP.getSecond())) &&
	                (getSecond() == null ?
	                 oP.getFirst() == null :
	                	 getSecond().equals(oP.getFirst()));
        }
    }

    /**
     * Compute a hash code.
     *
     * @return the hash code value.
     */
    @Override
    public int hashCode() {
        int result = getKey() == null ? 0 : getKey().hashCode();

        final int h = getKey() == null ? 0 : getKey().hashCode();
        
        if (result > h)
        	result = 37 * result + h ^ (h >>> 16);
        else
        	result = 37 * h + result ^ (result >>> 16);

        return result;
    }

}
