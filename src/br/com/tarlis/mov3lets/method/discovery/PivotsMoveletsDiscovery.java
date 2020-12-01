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
package br.com.tarlis.mov3lets.method.discovery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Subtrajectory;
import br.com.tarlis.mov3lets.utils.Mov3letsUtils;

/**
 * The Class PivotsMoveletsDiscovery.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <MO> the generic type
 */
public class PivotsMoveletsDiscovery<MO> extends MemMoveletsDiscovery<MO> {

	/**
	 * Instantiates a new pivots movelets discovery.
	 *
	 * @param trajsFromClass the trajs from class
	 * @param data the data
	 * @param train the train
	 * @param test the test
	 * @param qualityMeasure the quality measure
	 * @param descriptor the descriptor
	 */
	public PivotsMoveletsDiscovery(List<MAT<MO>> trajsFromClass, List<MAT<MO>> data, List<MAT<MO>> train, List<MAT<MO>> test, QualityMeasure qualityMeasure, 
			Descriptor descriptor) {
		super(trajsFromClass, data, train, test, qualityMeasure, descriptor);
	}

	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.discovery.MemMoveletsDiscovery#moveletsDiscovery(br.com.tarlis.mov3lets.model.MAT, java.util.List, int, int, java.util.Random).
	 * 
	 * @param trajectory
	 * @param trajectories
	 * @param minSize
	 * @param maxSize
	 * @param random
	 * @return
	 */
	public List<Subtrajectory> moveletsDiscovery(MAT<MO> trajectory, List<MAT<MO>> trajectories, int minSize, int maxSize, Random random) {
		List<Subtrajectory> candidates = new ArrayList<Subtrajectory>();

		int n = trajectory.getPoints().size();
		
		// TO USE THE LOG, PUT "-Ms -3"
		switch (maxSize) {
			case -1: maxSize = n; break;
			case -2: maxSize = (int) Math.round( Math.log10(n) / Math.log10(2) ); break;	
			case -3: maxSize = (int) Math.ceil(Math.log(n))+1; break;	
			default: break;
		}

		// It starts with the base case	
		int size = 1;
		Integer total_size = 0;
		
		base = computeBaseDistances(trajectory, trajectories);
		
		List<Subtrajectory> candidatesOfSize = findCandidates(trajectory, trajectories, size, base);
		total_size = total_size + candidatesOfSize.size();
		
		candidatesOfSize = filterByQuality(candidatesOfSize, trajectories, random);

		if( minSize <= 1 ) {
			candidates.addAll(candidatesOfSize);
		}				
		
		double[][][][] lastSize = clone4DArray(base);		
		
		// Tratar o resto dos tamanhos 
		for (size = 2; size <= maxSize; size++) {
			
			// Precompute de distance matrix
   			double[][][][] newSize = newSize(trajectory, trajectories, base, lastSize, size);
			
			candidatesOfSize = growPivots(candidatesOfSize, trajectory, trajectories, base, newSize, size);
			total_size = total_size + candidatesOfSize.size();
			
			candidatesOfSize = filterByQuality(candidatesOfSize, trajectories, random);
	
			
			if (size >= minSize){
				candidates.addAll(candidatesOfSize);
			}
		
			lastSize = newSize;
						
		} // for (int size = 2; size <= max; size++)	
	
		progressBar.plus("Class: " + trajectory.getMovingObject() 
						+ ". Trajectory: " + trajectory.getTid() 
						+ ". Trajectory Size: " + trajectory.getPoints().size() 
						+ ". Number of Candidates: " + total_size 
						+ ". Total of Movelets: " + candidates.size() 
						+ ". Max Size: " + maxSize
						+ ". Used Features: " + this.maxNumberOfFeatures);
//						+ ". Memory Use: " + Mov3letsUtils.getUsedMemory());
	
		base =  null;
		lastSize = null;
	
		return candidates;
	}

	/**
	 * Grow pivots.
	 *
	 * @param candidatesOfSize the candidates of size
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param base the base
	 * @param newSize the new size
	 * @param size the size
	 * @return the list
	 */
	private List<Subtrajectory> growPivots(List<Subtrajectory> candidatesOfSize, MAT<MO> trajectory,
			List<MAT<MO>> trajectories, double[][][][] base, double[][][][] newSize, int size) {
		List<Subtrajectory> newCandidates = new ArrayList<Subtrajectory>();
		
		for(Subtrajectory candidate : candidatesOfSize) {
			Subtrajectory subtrajectory = buildNewSize(candidate, trajectory, trajectories, newSize, size, true);
			if (subtrajectory != null)
				newCandidates.add(subtrajectory);
			
			subtrajectory = buildNewSize(candidate, trajectory, trajectories, newSize, size, false);	
			if (subtrajectory != null)
				newCandidates.add(subtrajectory);		
		}
		
		return newCandidates;
	}

	/**
	 * Builds the new size.
	 *
	 * @param candidate the candidate
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param mdist the mdist
	 * @param size the size
	 * @param left the left
	 * @return the subtrajectory
	 */
	private Subtrajectory buildNewSize(Subtrajectory candidate, MAT<MO> trajectory, List<MAT<MO>> trajectories,
			double[][][][] mdist, int size, boolean left) {
		
		int start = candidate.getStart() - (left? 1 : 0);
		int end   = candidate.getEnd()   + (left? 0 : 1);
		
		if (start < 0 || end > trajectory.getPoints().size()-1)
			return null;
		
		Subtrajectory subtrajectory = new Subtrajectory(start, end, trajectory, trajectories.size(),
				candidate.getPointFeatures(), candidate.getK());
		
		computeCandidateDistances(subtrajectory, trajectories, mdist, size, start);
		
		return subtrajectory;
	}

	/**
	 * Compute candidate distances.
	 *
	 * @param subtrajectory the subtrajectory
	 * @param trajectories the trajectories
	 * @param mdist the mdist
	 * @param size the size
	 * @param start the start
	 */
	private void computeCandidateDistances(Subtrajectory subtrajectory, List<MAT<MO>> trajectories, double[][][][] mdist, int size, int start) {
		double[][][] distancesForAllT = mdist[start];
		
		// For each trajectory in the database
		for (int i = 0; i < trajectories.size(); i++) {
			MAT<MO> T = trajectories.get(i);	
			
			double[][] distancesForT = distancesForAllT[i];
			double[][] ranksForT = new double[distancesForT.length][];
			
			int limit = T.getPoints().size() - size + 1;
			
			if (limit > 0)
				for (int k = 0; k < numberOfFeatures; k++) {				
					ranksForT[k] = rankingAlgorithm.rank(Arrays.stream(distancesForT[k],0,limit).toArray());
				} // for (int k = 0; k < numberOfFeatures; k++)
				
			int bestPosition = (limit > 0) ? bestAlignmentByRanking(ranksForT, subtrajectory.getPointFeatures()) : -1;
			for (int j = 0; j < subtrajectory.getPointFeatures().length; j++) {	
				double distance = (bestPosition >= 0) ? 
						distancesForT[subtrajectory.getPointFeatures()[j]][bestPosition] : MAX_VALUE;
				subtrajectory.getDistances()[j][i] = (distance != MAX_VALUE) ? 
						Math.sqrt( distance / size ) : MAX_VALUE;					
			}
			
		}
	}

	/**
	 * Filter by quality.
	 *
	 * @param bestCandidates the best candidates
	 * @param trajectories the trajectories
	 * @param random the random
	 * @return the list
	 */
	public List<Subtrajectory> filterByQuality(List<Subtrajectory> bestCandidates, List<MAT<MO>> trajectories, Random random) {
		/** STEP 2.3, for this trajectory movelets: 
		 * It transforms the training and test sets of trajectories using the movelets */
		for (Subtrajectory candidate : bestCandidates) {
			// It initializes the set of distances of all movelets to null
			computeCandidateDistances(candidate, trajectories, base, candidate.getSize(), candidate.getStart());
			// In this step the set of distances is filled by this method
			computeDistances(candidate, trajectories); // computeDistances(movelet, trajectories);

			/* STEP 2.1.6: QUALIFY BEST HALF CANDIDATES 
			 * * * * * * * * * * * * * * * * * * * * * * * * */
//			assesQuality(candidate);
			assesQuality(candidate, random); //TODO change?
		}

		/** STEP 2.2: SELECTING BEST CANDIDATES */	
		return filterMovelets(bestCandidates);
	}
	
}
