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
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;

import br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class ProgressiveMoveletsDiscovery.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <MO> the generic type
 */
public class ProgressiveMoveletsDiscovery<MO> extends MoveletsDiscovery<MO> {

	/** The number of features. */
	protected int numberOfFeatures = 1;
	
	/** The max number of features. */
	protected int maxNumberOfFeatures = 2;
	
	/** The explore dimensions. */
	protected boolean exploreDimensions;
	
	/** The quality measure. */
	protected QualityMeasure qualityMeasure = null;
	
	/** The ranking algorithm. */
	protected RankingAlgorithm rankingAlgorithm = new NaturalRanking();
	
	/**
	 * Instantiates a new progressive movelets discovery.
	 *
	 * @param trajsFromClass the trajs from class
	 * @param data the data
	 * @param train the train
	 * @param test the test
	 * @param qualityMeasure the quality measure
	 * @param descriptor the descriptor
	 */	
	public ProgressiveMoveletsDiscovery(List<MAT<MO>> trajsFromClass, List<MAT<MO>> data, List<MAT<MO>> train, List<MAT<MO>> test, QualityMeasure qualityMeasure, 
			Descriptor descriptor) {
		super(trajsFromClass, data, train, test, qualityMeasure, descriptor);
	}
	
	/**
	 * * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE DISCOVERING PROCEDURES: * * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * * *.
	 *
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param minSize the min size
	 * @param maxSize the max size
	 * @param random the random
	 * @return the list
	 */

	/**
	 * @param trajectory2
	 * @param data2
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

		// It starts with the base case: size = 1
		Integer total_size = 0;
		maxSizeUsed = 0;
		
		// Size 1:
		List<Subtrajectory> candidatesOfSize = findCandidates(trajectory, trajectories, 1); // super.findCandidates
		candidatesOfSize.forEach(x -> assesQuality(x, random));			
		
		if ( minSize < maxSize ){

			/** This is the difference: find candidates until the quality increases, qualify and returns 
			 * - As we filter candidates from the overlapping points, this returns only one starting from each 
			 *   point of the trajectory (with the best quality) */	
			for (Subtrajectory candidate : candidatesOfSize) {
				candidates.add(findCandidate(candidate, trajectory, trajectories, minSize+1, minSize, maxSize, random)); // size = 2
			}

			total_size = total_size + candidates.size();
			
		} else {
			
			total_size = total_size + candidatesOfSize.size();
			candidates = candidatesOfSize;
			
		}
		
		candidates = filterMovelets(candidates);

		System.out.println("\nMOVELETS:");
		for (int i = 0; i < candidates.size(); i++) {
			System.out.println(i +" => "+ candidates.get(i));
		}
		
		progressBar.plus("Class: " + trajectory.getMovingObject() 
						+ ". Trajectory: " + trajectory.getTid() 
						+ ". Trajectory Size: " + trajectory.getPoints().size() 
						+ ". Number of Candidates: " + total_size 
						+ ". Total of Movelets: " + candidates.size() 
						+ ". Max Size: " + maxSize
						+ ". Max Size Used: " + maxSizeUsed
						+ ". Used Features: " + this.maxNumberOfFeatures);
				
		return candidates;
	}

	/** The max size used. */
	private int maxSizeUsed = 0;
	
	/**
	 * [THE GREAT GAP].
	 *
	 * @param previousCandidate the previous candidate
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param size the size
	 * @param minSize the min size
	 * @param maxSize the max size
	 * @param random the random
	 * @return the subtrajectory
	 */
	public Subtrajectory findCandidate(Subtrajectory previousCandidate, MAT<MO> trajectory, List<MAT<MO>> trajectories, int size, int minSize, int maxSize, Random random) {
				
		maxSizeUsed = maxSizeUsed > size? maxSizeUsed : size;
		
		// Both conditions are search end points:
		if (size > maxSize) {
			
			return previousCandidate;
			
		} else 
		if (previousCandidate.getEnd()+2 >= trajectory.getPoints().size()) {
			
			return previousCandidate;
			
		} else {
			
			// Calc. distances and asses quality:
			Subtrajectory candidate = buildCandidate(previousCandidate, trajectory, trajectories, random);
			Subtrajectory nextCandidate = buildCandidate(candidate, trajectory, trajectories, random);
			
			Subtrajectory best = candidate;
			
			if (previousCandidate.getQuality().compareTo(best.getQuality()) <= 0)
				best = previousCandidate;
			
			if (nextCandidate.getQuality().compareTo(best.getQuality()) <= 0)
				best = nextCandidate;
			
			// if quality is decreasing
			if (previousCandidate.getQuality().compareTo(candidate.getQuality()) <= 0 && candidate.getQuality().compareTo(nextCandidate.getQuality()) <= 0)
				return best;
			
			nextCandidate = findCandidate(nextCandidate, trajectory, trajectories, size+2, minSize, maxSize, random);
			
			if (nextCandidate.getQuality().compareTo(best.getQuality()) <= 0)
				best = nextCandidate;
				
			return best;
		}
		
	}

	/**
	 * Builds the candidate.
	 *
	 * @param previousCandidate the previous candidate
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param random the random
	 * @return the subtrajectory
	 */
	private Subtrajectory buildCandidate(Subtrajectory previousCandidate, MAT<MO> trajectory, List<MAT<MO>> trajectories,
			Random random) {
		Subtrajectory candidate = nextSize(previousCandidate, trajectory, trajectories.size());
		
		// For each trajectory in the database
		for (int i = 0; i < trajectories.size(); i++) {
			MAT<MO> T = trajectories.get(i);
		
			double[] distances = bestAlignmentByPointFeatures(candidate, T).getSecond();
			for (int k = 0; k < candidate.getPointFeatures().length; k++) {
				candidate.getDistances()[k][i] = distances[k];
			}
		}
		
		assesQuality(candidate, random);
		return candidate;
	}

	/**
	 * Next size.
	 *
	 * @param s the s
	 * @param T the t
	 * @param numberOfTrajectories the number of trajectories
	 * @return the subtrajectory
	 */
	private Subtrajectory nextSize(Subtrajectory s, MAT<MO> T, int numberOfTrajectories) {
		return new Subtrajectory(s.getStart(), s.getEnd()+1, T, s.getPointFeatures(), numberOfTrajectories);
	}
	
}
