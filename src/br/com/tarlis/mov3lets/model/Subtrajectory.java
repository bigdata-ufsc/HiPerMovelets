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
package br.com.tarlis.mov3lets.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import br.com.tarlis.mov3lets.method.qualitymeasure.Quality;

/**
 * The Class Subtrajectory.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class Subtrajectory {

	/** The start. */
	private int start;
	
	/** The end. */
	private int end;
 
	/** The trajectory. */
	private MAT<?> trajectory;
	
	/** The points. */
	private List<Point> points;
		
/** The distances. */
//	private List<List<Double>> distances;
	private double[][] distances;
	
	/** The best alignments. */
	private List<Subtrajectory> bestAlignments;
	
	/** The quality. */
	private Quality quality;
//	private double proportionInClass;

//	private HashMap<String, Aspect<?>> features;
	
	/** The splitpoint data. */
//TODO necessary?????????????????????????????:
	public Map<String, double[]> splitpointData; 
	
	/** The point features. */
	private int[] pointFeatures;
	
	/** The k. */
	private int k; // Combination index
	
//	private Map<Integer, double[]> mdist; 
	
	/** The splitpoints. */
private double[] splitpoints;
	
	/** The max distances. */
	private double[] maxDistances;

	/** The good trajectories. */
	private boolean[] goodTrajectories;
	
	/** The covered. */
	private List<MAT<?>> covered;

	/**
	 * Instantiates a new subtrajectory.
	 *
	 * @param start the start
	 * @param end the end
	 * @param t the t
	 */
	public Subtrajectory(int start, int end, MAT<?> t) {
		super();
		this.start = start;
		this.end = end;
		this.trajectory = t;
		this.points = t.getPoints().subList(start, end+1);
//		this.features = new HashMap<>();
	}
	
	/**
	 * Instantiates a new subtrajectory.
	 *
	 * @param start the start
	 * @param end the end
	 * @param t the t
	 * @param pointFeatures the point features
	 * @param numberOfTrajectories the number of trajectories
	 */
	public Subtrajectory(int start, int end, MAT<?> t, int[] pointFeatures, int numberOfTrajectories) {
		super();
		this.start = start;
		this.end = end;
		this.trajectory = t;
		this.points = t.getPoints().subList(start, end+1);
		this.pointFeatures = pointFeatures;
//		this.distances = new HashMap<Aspect<?>, Double>();
		this.distances = new double[pointFeatures.length][numberOfTrajectories];
//		this.features = new HashMap<>();
	}
	
	/**
	 * Instantiates a new subtrajectory.
	 *
	 * @param start the start
	 * @param end the end
	 * @param t the t
	 * @param numberOfTrajectories the number of trajectories
	 * @param pointFeatures the point features
	 * @param k the k
	 */
	public Subtrajectory(int start, int end, MAT<?> t, int numberOfTrajectories, int[] pointFeatures, int k) {
		super();
		this.start = start;
		this.end = end;
		this.trajectory = t;
		this.points = t.getPoints().subList(start, end+1);
		this.k = k;
		this.pointFeatures = pointFeatures;
//		this.distances = new HashMap<Aspect<?>, Double>();
		this.distances = new double[pointFeatures.length][numberOfTrajectories];
//		this.features = new HashMap<>();
		this.bestAlignments = new ArrayList<Subtrajectory>();
	}

	/**
	 * Gets the good trajectories.
	 *
	 * @return the good trajectories
	 */
	public boolean[] getGoodTrajectories() {
		return goodTrajectories;
	}
	
//	public HashMap<String, IFeature> getFeatures() {
//		return features;
//	}
//	
//	@Override
//	public IFeature getFeature(String featureName){
//		return 	features.get(featureName);
//	}

	/**
 * Overridden method. 
 * @see java.lang.Object#toString().
 * 
 * @return
 */
@Override
	public String toString() {

		String string = new String();

		string += "Origin: t" + getTrajectory().getTid() + " from " + start + " to " + end + ", ";
		string += "Size: " + getSize() + ", Features: " + Arrays.toString(getPointFeatures()) + "\n";
		string += "Label: " + getTrajectory().getMovingObject().toString() + "\n";
		string += "Quality: " + getQuality() + "\n";
//		string += "Distances: \n";
//		string += "Data: \n";

		return string;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Sets the start.
	 *
	 * @param start the new start
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Sets the end.
	 *
	 * @param end the new end
	 */
	public void setEnd(int end) {
		this.end = end;
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
	 * @param trajectory the new trajectory
	 */
	public void setTrajectory(MAT<?> trajectory) {
		this.trajectory = trajectory;
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

//	public void setFeatures(HashMap<String, Aspect<?>> features) {
//		this.features = features;
//	}

	/**
 * Gets the size.
 *
 * @return the size
 */
public int getSize() {
		return end - start + 1;
	}
	
	/**
	 * Gets the best alignments.
	 *
	 * @return the bestAlignments
	 */
	public List<Subtrajectory> getBestAlignments() {
		return bestAlignments;
	}
	
	/**
	 * Sets the best alignments.
	 *
	 * @param bestAlignments the bestAlignments to set
	 */
	public void setBestAlignments(List<Subtrajectory> bestAlignments) {
		this.bestAlignments = bestAlignments;
	}
	
//	/**
//	 * @return the distances
//	 */
//	public List<List<Double>> getDistances() {
//		return distances;
//	}
//	
//	/**
//	 * @param distances the distances to set
//	 */
//	public void setDistances(List<List<Double>> distances) {
//		this.distances = distances;
//	}

//	/**
//	 * @return
//	 */
//	public double[][] getDistancesToArray() {
//		double[][] distances = new double[getDistances().size()][];
//		for (int i = 0; i < getDistances().size(); i++) {
//			distances[i] = getDistances().get(i).stream().mapToDouble(d -> d).toArray();
//			
//		}
//		return distances;
//	}
	
	/**
 * Gets the distances.
 *
 * @return the distances
 */
public double[][] getDistances() {
		return distances;
	}
	
	/**
	 * Sets the distances.
	 *
	 * @param distances the new distances
	 */
	public void setDistances(double[][] distances) {
		this.distances = distances;
	}
	
//	 public Subtrajectory[] getBestAlignments() {
//		return bestAlignments;
//	}
//	 
//	public Map<Integer, double[]> getMdist() {
//		return mdist;
//	}
//	
//	public void setBestAlignments(Subtrajectory[] bestAlignments) {
//		this.bestAlignments = bestAlignments;
//	}	
	
	/**
 * Sets the quality.
 *
 * @param quality the new quality
 */
public void setQuality(Quality quality) {
		this.quality = quality;
	}
	
	/**
	 * Gets the quality.
	 *
	 * @return the quality
	 */
	public Quality getQuality() {
		// TODO Auto-generated method stub
		return this.quality;
	}

	/**
	 * Gets the point.
	 *
	 * @return the point
	 */
	public double[] getPoint() {
		// TODO Auto-generated method stub
		return null;
	}

//	public void createMDist() {
//		// TODO Auto-generated method stub
//		mdist = new HashMap<>();
//	}
	
	/**
 * Gets the point features.
 *
 * @return the point features
 */
public int[] getPointFeatures() {
		return pointFeatures;
	}
	
	/**
	 * Sets the point features.
	 *
	 * @param pointFeatures the new point features
	 */
	public void setPointFeatures(int[] pointFeatures) {
		this.pointFeatures = pointFeatures;
	}
	
	/**
	 * Gets the k.
	 *
	 * @return the k
	 */
	public int getK() {
		return k;
	}
	
	/**
	 * Sets the k.
	 *
	 * @param k the k to set
	 */
	public void setK(int k) {
		this.k = k;
	}
	
	/**
	 * Gets the max distances.
	 *
	 * @return the max distances
	 */
	public double[] getMaxDistances() {
		return maxDistances;
	}

	/**
	 * Sets the max distances.
	 *
	 * @param maxDistances the new max distances
	 */
	public void setMaxDistances(double[] maxDistances) {
		this.maxDistances = maxDistances;
	}

	/**
	 * Gets the splitpoints.
	 *
	 * @return the splitpoints
	 */
	public double[] getSplitpoints() {
		return splitpoints;
	}

	/**
	 * Sets the splitpoints.
	 *
	 * @param splitpoints the new splitpoints
	 */
	public void setSplitpoints(double[] splitpoints) {
		this.splitpoints = splitpoints;
	}
	
	/**
	 * Gets the splitpoint data.
	 *
	 * @return the splitpoint data
	 */
	public Map<String, double[]> getSplitpointData() {
		return splitpointData;
	}

	/**
	 * Sets the splitpoint data.
	 *
	 * @param splitpointData the splitpoint data
	 */
	public void setSplitpointData(Map<String, double[]> splitpointData) {
		this.splitpointData = splitpointData;
	}
	
//	/**
//	 * @return the proportionInClass
//	 */
//	public double getProportionInClass() {
//		return proportionInClass;
//	}
//	
//	/**
//	 * @param proportionInClass the proportionInClass to set
//	 */
//	public void setProportionInClass(double proportionInClass) {
//		this.proportionInClass = proportionInClass;
//	}
	
	/**
 * Gets the covered.
 *
 * @return the covered
 */
public List<MAT<?>> getCovered() {
		return covered;
	}
	
	/**
	 * Sets the covered.
	 *
	 * @param covered the new covered
	 */
	public void setCovered(List<MAT<?>> covered) {
		this.covered = covered;
	}
}
