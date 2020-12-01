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
package br.com.tarlis.mov3lets.method.qualitymeasure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.util.Pair;

import br.com.tarlis.mov3lets.method.distancemeasure.DistanceMeasure;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class QualityMeasure.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <MO> the generic type
 */
public abstract class QualityMeasure<MO> {
	
	/** The max value. */
	public double MAX_VALUE = DistanceMeasure.DEFAULT_MAX_VALUE;
	
	/** The labels. */
	protected List<MO> labels;
	
	/** The trajectories. */
	protected List<MAT<MO>> trajectories;
	
	/** The samples. */
	protected int samples = 1;
	
	/** The sample size. */
	protected double sampleSize = 1;
	
	/** The medium. */
	protected String medium = "none";	
	
	/**
	 * Instantiates a new quality measure.
	 *
	 * @param trajectories the trajectories
	 * @param samples the samples
	 * @param sampleSize the sample size
	 * @param medium the medium
	 */
	public QualityMeasure(List<MAT<MO>> trajectories, int samples, double sampleSize, String medium) {
		this.labels = new ArrayList<>();
	
		for (int j = 0; j < trajectories.size(); j++) {
			labels.add(trajectories.get(j).getMovingObject());
		}	
		this.trajectories = trajectories;	
		this.samples = samples;
		this.sampleSize = sampleSize;
		this.medium = medium;
	}
	
	/**
	 * Asses quality.
	 *
	 * @param candidate the candidate
	 * @param random the random
	 */
	public abstract void assesQuality(Subtrajectory candidate, Random random);

	/**
	 * Gets the max distances.
	 *
	 * @param distances the distances
	 * @return the max distances
	 */
	public double[] getMaxDistances(double[][] distances) {
		
		double[] maxDistances = new double[distances.length];
		for (int i = 0; i < distances.length; i++) {
			maxDistances[i] =
					Arrays.stream(distances[i]).filter(e -> e != MAX_VALUE).max().getAsDouble();
		}
				
		return maxDistances;
	}
	
	/**
	 * Choose points stratified.
	 *
	 * @param distances the distances
	 * @param labels the labels
	 * @param target the target
	 * @param random the random
	 * @return the pair
	 */
	public Pair<double[][],List<MO>> choosePointsStratified(double[][] distances, List<MO> labels, MO target, Random random){
		
//		if (this.sampleSize == 1){
//			return new Pair<>(distances,labels);
//		}
		
		List<Integer> positive = new ArrayList<>();
		List<Integer> negative = new ArrayList<>();
		for (int i = 0; i < labels.size(); i++) {
			if (labels.get(i).equals(target))
				positive.add(i);
			else
				negative.add(i);
		}
		
		List<Integer> choosed = new ArrayList<>();
		Collections.shuffle(positive,random);
		choosed.addAll(positive.subList(0, (int) (positive.size() * this.sampleSize) ) );		
		Collections.shuffle(negative,random);
		choosed.addAll(negative.subList(0, (int) (negative.size() * this.sampleSize) ) );
		
		// Selecionar os dados
		double[][] newDistances = new double[distances.length][choosed.size()];
		List<MO> newLabels = new ArrayList<>();
		
		for (int i = 0; i < choosed.size(); i++) {
			for (int j = 0; j < newDistances.length; j++) {
				newDistances[j][i] = distances[j][choosed.get(i)];
			}
			newLabels.add(labels.get(choosed.get(i)));			
		}
		
		
		return new Pair<>(newDistances,newLabels);
	}
	
	/**
	 * First vector greater than the second.
	 *
	 * @param first the first
	 * @param second the second
	 * @return true, if successful
	 */
	public boolean firstVectorGreaterThanTheSecond(double [] first, double [] second){
		
		for (int i = 0; i < first.length; i++) {
			if (first[i] <= second[i])
				return false;
		}
		
		return true;
	}
	
	/**
	 * Fill split points limits.
	 *
	 * @param splitpointsData the splitpoints data
	 * @param medium the medium
	 * @return the pair
	 */
	public Pair<double[],double[]> fillSplitPointsLimits(Map<String, double[]> splitpointsData, String medium){
		int n = splitpointsData.get("mean").length;
		double[] splitpointsLI = new double[n];
		double[] splitpointsLS = new double[n];
		
		switch (medium){
		
			case "interquartil" :
				splitpointsLI = splitpointsData.get("p25");
				splitpointsLS = splitpointsData.get("p75");				
				break;
			case "sd" :
				for (int i = 0; i < n; i++) {
					splitpointsLI[i] = splitpointsData.get("mean")[i] - splitpointsData.get("sd")[i];
					splitpointsLS[i] = splitpointsData.get("mean")[i] + splitpointsData.get("sd")[i];
				}
				break;
			case "minmax" :
				splitpointsLI = splitpointsData.get("min");
				splitpointsLS = splitpointsData.get("max");				
				break;
			case "mean" :
				splitpointsLI = splitpointsData.get("mean");
				splitpointsLS = splitpointsData.get("mean");	
				break;	
				
			default :
				splitpointsLI = splitpointsData.get("mean");
				splitpointsLS = splitpointsData.get("mean");					
		
		}		
		
		return new Pair<double[],double[]>(splitpointsLI,splitpointsLS);
	}
	
	/**
	 * Count covered.
	 *
	 * @param targetDistances the target distances
	 * @param candidate the candidate
	 * @return the int
	 */
	public int countCovered(List<double[]> targetDistances, double[] candidate){
		
		int count = 0;
		
		for (int i = 0; i < targetDistances.size(); i++) {
			
			if (isCovered(targetDistances.get(i), candidate))
				count++;
			
		}
		
		return count;
	}
	
	/**
	 * Checks if is covered.
	 *
	 * @param point the point
	 * @param limits the limits
	 * @return true, if is covered
	 */
	public boolean isCovered(double[] point, double[] limits){
		
		int dimensions = limits.length;
		
		for (int i = 0; i < dimensions; i++) {
			if (limits[i] > 0){
				if (point[i] >= limits[i])
					return false;
			} else
				if (point[i] > limits[i])
					return false;
		}
		
		return true;
	}

}
