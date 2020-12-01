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

import java.util.ArrayList;
import java.util.HashMap;

import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;

/**
 * The Class Matrix2D.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class Matrix2D extends HashMap<Pair, ArrayList<Double>> {
	
	/** The default. */
	private double DEFAULT = Double.POSITIVE_INFINITY;
	
	/**
	 * Instantiates a new matrix 2 D.
	 */
	public Matrix2D() {}
	
	/**
	 * Instantiates a new matrix 2 D.
	 *
	 * @param defaultValue the default value
	 */
	public Matrix2D(double defaultValue) {
		this.DEFAULT = defaultValue;
	}

	/**
	 * Adds the.
	 *
	 * @param a the a
	 * @param b the b
	 * @param distance the distance
	 */
	public void add(Point a, Point b, double distance) {
		Pair pair = new Pair(a,b);
		ArrayList<Double> cube = null;
		
		if (containsKey(pair)) {
			cube = get(pair);
		} else {
			cube = new ArrayList<Double>();
			put(pair, cube);
		}
		
		cube.add(distance);
	}

	/**
	 * Contains.
	 *
	 * @param a the a
	 * @param b the b
	 * @return true, if successful
	 */
	public boolean contains(Point a, Point b) {
		return containsKey(new Pair(a, b));
	}

	/**
	 * Distances for aspect.
	 *
	 * @param index Aspect index.
	 * @param p the p
	 * @param T the t
	 * @return the double[]
	 */
	public double[] distancesForAspect(int index, Point p, MAT<?> T) {
		double[] distancesForAspect = new double[T.getPoints().size()];
		
		for (int j = 0; j < T.getPoints().size(); j++) {
			Point q = T.getPoints().get(j);
			distancesForAspect[j] = get(new Pair(p, q)).get(index);
		}
				
		return distancesForAspect;
	}

	/**
	 * Gets the.
	 *
	 * @param p the p
	 * @param q the q
	 * @return the array list
	 */
	public ArrayList<Double> get(Point p, Point q) {
		return get(new Pair(p, q));
	}
	
	/**
	 * Overridden method. 
	 * @see java.util.HashMap#clone().
	 * 
	 * @return
	 */
	@Override
	public Object clone() {
		Matrix2D clone = new Matrix2D();
		for(Entry<Pair, ArrayList<Double>> entry : this.entrySet()) {
	        //iterate through the graph
	        ArrayList<Double> sourceList = entry.getValue();
	        ArrayList<Double> clonedList = new ArrayList<Double>();
	        clonedList.addAll(sourceList);
	        //put value into new graph
	        clone.put(entry.getKey(), clonedList);
	    }
		return clone;
	}
	
//	public ArrayList<ArrayList<Double>> get(int i, int j) {
//		return super.get(i).get(j);
//	}
//	
//	public ArrayList<Double> get(int i, int j, int k) {
//		return super.get(i).get(j).get(k);
//	}
//	
//	public double get(int i, int j, int k, int l) {
//		try {
//			return super.get(i).get(j).get(k).get(l);
//		} catch (IndexOutOfBoundsException e) {
//			return DEFAULT;
//		}
//	}
//	
//	public double set(int i, int j, int k, int l, double value) {
//		if (super.get(i) == null)
//			super.set(i, new ArrayList<ArrayList<ArrayList<Double>>>());
//		if (super.get(i).get(j) == null)
//			super.get(i).set(j, new ArrayList<ArrayList<Double>>());
//		if (super.get(i).get(j).get(k) == null)
//			super.get(i).get(j).set(k, new ArrayList<Double>());
//		
//		return super.get(i).get(j).get(k).set(l, value);
//	}

}
