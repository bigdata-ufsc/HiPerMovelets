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

import org.apache.commons.math3.util.Pair;

import br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure;
import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class MemMoveletsDiscovery.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <MO> the generic type
 */
public class MemMoveletsDiscovery<MO> extends MoveletsDiscovery<MO> {
	
	/** The base. */
	protected double[][][][] base;
	
	/** The max distances. */
	protected double[] maxDistances; // Max distances by dimension
	
	/**
	 * Instantiates a new mem movelets discovery.
	 *
	 * @param trajsFromClass the trajs from class
	 * @param data the data
	 * @param train the train
	 * @param test the test
	 * @param qualityMeasure the quality measure
	 * @param descriptor the descriptor
	 */	
	public MemMoveletsDiscovery(List<MAT<MO>> trajsFromClass, List<MAT<MO>> data, List<MAT<MO>> train, List<MAT<MO>> test, QualityMeasure qualityMeasure, 
			Descriptor descriptor) {
		super(trajsFromClass, data, train, test, qualityMeasure, descriptor);
	}

	/**
	 * Looks for candidates in the trajectory, then compares with every other trajectory.
	 *
	 * @return the list
	 */
	public List<Subtrajectory> discover() {
		
		int maxSize = getDescriptor().getParamAsInt("max_size");
		int minSize = getDescriptor().getParamAsInt("min_size");

		/** STEP 2.1: Starts at discovering movelets */
//		progressBar.trace("Class: " + trajsFromClass.get(0).getMovingObject() + "."); // Might be saved in HD
//		Mov3letsUtils.getInstance().startTimer("\tClass >> " + trajectory.getClass());

		List<Subtrajectory> movelets = new ArrayList<Subtrajectory>();
		
		progressBar.trace("Movelets Discovery for Class: " + trajsFromClass.get(0).getMovingObject());
		
		for (MAT<MO> trajectory : trajsFromClass) {
			// This guarantees the reproducibility
			Random random = new Random(trajectory.getTid());

			/** STEP 2.1: --------------------------------- */
			List<Subtrajectory> candidates = moveletsDiscovery(trajectory, this.train, minSize, maxSize, random);
			
	//		Mov3letsUtils.getInstance().stopTimer("\tClass >> " + trajectory.getClass());
						
			/** Summary Candidates: */
	
			/** STEP 2.3, for this trajectory movelets: 
			 * It transforms the training and test sets of trajectories using the movelets */
//			for (Subtrajectory candidate : candidates) {
//				// It initializes the set of distances of all movelets to null
//				candidate.setDistances(null);
//				// In this step the set of distances is filled by this method
//				computeDistances(candidate, this.train); // computeDistances(movelet, trajectories);
//				
//				assesQuality(candidate, random);
//			}

			/** STEP 2.4: SELECTING BEST CANDIDATES */			
//			candidates = filterMovelets(candidates);		
			movelets.addAll(filterMovelets(candidates));
			
//			System.gc();
		}
		
		/** STEP 2.2: Runs the pruning process */
		if(getDescriptor().getFlag("last_prunning"))
			movelets = lastPrunningFilter(movelets);
		/** STEP 2.2: --------------------------------- */
		
		/** STEP 2.3.1: Output Movelets (partial) */
		super.output("train", this.train, movelets, true);
		base =  null;
		
		// Compute distances and best alignments for the test trajectories:
		/* If a test trajectory set was provided, it does the same.
		 * and return otherwise */
		/** STEP 2.3.2: Output Movelets (partial) */
		if (!this.test.isEmpty()) {
//			base = computeBaseDistances(trajectory, this.test);
			for (Subtrajectory candidate : movelets) {
				// It initializes the set of distances of all movelets to null
				candidate.setDistances(null);
				// In this step the set of distances is filled by this method
				computeDistances(candidate, this.test); //, computeBaseDistances(trajectory, this.test));
			}
			super.output("test", this.test, movelets, true);
		}
		/** --------------------------------- */

		/** STEP 2.3.3, to write all outputs: */
		super.output("train", this.train, movelets, false);
		
		if (!this.test.isEmpty())
			super.output("test", this.test, movelets, false);
		
		return movelets;
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

		// It starts with the base case	
		int size = 1;
		Integer total_size = 0;
		
		base = computeBaseDistances(trajectory, trajectories);
		
		if( minSize <= 1 ) {
			candidates.addAll(findCandidates(trajectory, trajectories, size, base));
			candidates.forEach(x -> assesQuality(x, random));
		}				
		
		double[][][][] lastSize = clone4DArray(base);		

		total_size = total_size + candidates.size();
		
		// Tratar o resto dos tamanhos 
		for (size = 2; size <= maxSize; size++) {
	
			// Precompute de distance matrix
   			double[][][][] newSize = newSize(trajectory, trajectories, base, lastSize, size);

			// Create candidates and compute min distances		
			List<Subtrajectory> candidatesOfSize = findCandidates(trajectory, trajectories, size, newSize);
		
			total_size = total_size + candidatesOfSize.size();
			
			if (size >= minSize){
				
				//for (Subtrajectory candidate : candidatesOfSize) assesQuality(candidate);				
				candidatesOfSize.forEach(x -> assesQuality(x, random));
				candidates.addAll(candidatesOfSize);
			}
		
			lastSize = newSize;
						
		} // for (int size = 2; size <= max; size++)	
		
		candidates = filterMovelets(candidates);
		
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
	 * Compute base distances.
	 *
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @return the double[][][][]
	 */
	public double[][][][] computeBaseDistances(MAT<?> trajectory, List<MAT<MO>> trajectories){
		int n = trajectory.getPoints().size();
		int size = 1;
		
		double[][][][] base = new double[(n - size)+1][][][];		
		
		for (int start = 0; start <= (n - size); start++) {
			
			base[start] = new double[trajectories.size()][][];				
			
			for (int i = 0; i < trajectories.size(); i++) {
				
				MAT<?> T = trajectories.get(i);
				Point a = trajectory.getPoints().get(start);
								
				base[start][i] = new double[getDescriptor().getAttributes().size()][(trajectories.get(i).getPoints().size()-size)+1];
						
				for (int j = 0; j <= (T.getPoints().size()-size); j++) {
					Point b = T.getPoints().get(j);
					

					for (int k = 0; k < getDescriptor().getAttributes().size(); k++) {
						AttributeDescriptor attr = getDescriptor().getAttributes().get(k);						
						base[start][i][k][j] = attr.getDistanceComparator().calculateDistance(
								a.getAspects().get(k), 
								b.getAspects().get(k), 
								attr);
					
//						base[start][i][k][j] = (distance != MAX_VALUE) ? (distance) : MAX_VALUE;	// No sense				
					
					} // for (int k = 0; k < distance.length; k++)
					
				} // for (int j = 0; j <= (train.size()-size); j++)
				
			} //for (int i = 0; i < train.size(); i++)
			
		} // for (int start = 0; start <= (n - size); start++)

		return base;
	}
	
	/**
	 * Clone 4 D array.
	 *
	 * @param source the source
	 * @return the double[][][][]
	 */
	public double[][][][] clone4DArray(double [][][][] source){
		double[][][][] dest = new double[source.length][][][];
		for (int i = 0; i < dest.length; i++) {
			dest[i] = new double[source[i].length][][];
			for (int j = 0; j < dest[i].length; j++) {
				dest[i][j] = new double[source[i][j].length][];
				for (int k = 0; k < dest[i][j].length; k++) {
					dest[i][j][k] = new double[source[i][j][k].length];
					for (int k2 = 0; k2 < source[i][j][k].length; k2++) {
						dest[i][j][k][k2] = source[i][j][k][k2];
					}
				}
			}
		}
		return dest;		
	}

	/**
	 * New size.
	 *
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param base the base
	 * @param lastSize the last size
	 * @param size the size
	 * @return the double[][][][]
	 */
	public double[][][][] newSize(MAT<?> trajectory, List<MAT<MO>> trajectories, double[][][][] base, double[][][][] lastSize, int size) {
		
		int n = trajectory.getPoints().size();	
		
		for (int start = 0; start <= (n - size); start++) {
						
			for (int i = 0; i < trajectories.size(); i++) {
				
				if (trajectories.get(i).getPoints().size() >= size) {						
							
					for (int j = 0; j <= (trajectories.get(i).getPoints().size()-size); j++) {
												
						for (int k = 0; k < lastSize[start][i].length; k++) {
							
							if (lastSize[start][i][k][j] != MAX_VALUE)
								
								lastSize[start][i][k][j] += base[start+size-1][i][k][j+size-1];
						
						} // for (int k = 0; k < distance.length; k++) {
											
					} // for (int j = 0; j <= (train.size()-size); j++)
					
				} // if (train.get(i).getData().size() >= size) 
				
			} // for (int i = 0; i < train.size(); i++)
			
		} // for (int start = 0; start <= (n - size); start++)
		
		return lastSize;
	}

	/**
	 * [THE GREAT GAP].
	 *
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param size the size
	 * @param mdist the mdist
	 * @return the list
	 */
	public List<Subtrajectory> findCandidates(MAT<MO> trajectory, List<MAT<MO>> trajectories, int size, double[][][][] mdist) {
		
		// Trajectory P size => n
		int n = trajectory.getPoints().size();
		int[][] combinations = makeCombinations(exploreDimensions, numberOfFeatures, maxNumberOfFeatures);
		
		maxDistances = new double[getDescriptor().getAttributes().size()];
		
		// List of Candidates to extract from P:
		List<Subtrajectory> candidates = new ArrayList<>();
		
		// From point 0 to (n - <candidate max. size>) 
		for (int start = 0; start <= (n - size); start++) {
//			Point p = trajectory.getPoints().get(start);
			
			// Extract possible candidates from P to max. candidate size:
			List<Subtrajectory> list = buildSubtrajectory(start, start + size - 1, trajectory, trajectories.size(), combinations);
							
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
				
				for (Subtrajectory subtrajectory : list) {		
					int bestPosition = (limit > 0) ? bestAlignmentByRanking(ranksForT, subtrajectory.getPointFeatures()) : -1;
					for (int j = 0; j < subtrajectory.getPointFeatures().length; j++) {	
						double distance = (bestPosition >= 0) ? 
								distancesForT[subtrajectory.getPointFeatures()[j]][bestPosition] : MAX_VALUE;
						subtrajectory.getDistances()[j][i] = (distance != MAX_VALUE) ? 
								Math.sqrt( distance / size ) : MAX_VALUE;	
								
						if (maxDistances[j] < subtrajectory.getDistances()[j][i] && subtrajectory.getDistances()[j][i] != MAX_VALUE)
							maxDistances[j] = subtrajectory.getDistances()[j][i];
					}
				}
				
			} // for (int currentFeatures = 1; currentFeatures <= numberOfFeatures; currentFeatures++)
			
			candidates.addAll(list);

		} // for (int start = 0; start <= (n - size); start++)
		
		return candidates;
		
	}
	
	/**
	 * Best alignment by ranking.
	 *
	 * @param ranksForT the ranks for T
	 * @param comb the comb
	 * @param reindex the reindex
	 * @return the int
	 */
	public int bestAlignmentByRanking(double[][] ranksForT, int[] comb, boolean reindex) {
		
		if (reindex)
			return super.bestAlignmentByRanking(ranksForT, comb);
		
		double[] rankMerged = new double[ranksForT[0].length];
		
		// In case it's a combination of more than one dimension
		for (int i = 0; i < comb.length; i++) {
			for (int j = 0; j < ranksForT[0].length; j++) {
				rankMerged[j] += ranksForT[comb[i]][j];
			}
		}

		int minRankIndex = 0;
		for (int j = 1; j < rankMerged.length; j++) {
			if (rankMerged[j] < rankMerged[minRankIndex])
				minRankIndex = j;
		}
		
		return minRankIndex;
	}
	
	/**
	 * Best alignment by point features.
	 *
	 * @param s the s
	 * @param t the t
	 * @param mdist the mdist
	 * @param idxt the idxt
	 * @return the pair
	 */
	public Pair<Subtrajectory, double[]> bestAlignmentByPointFeatures(Subtrajectory s, MAT<MO> t, double[][][][] mdist, int idxt) {
		double[] maxValues = new double[numberOfFeatures];
		Arrays.fill(maxValues, MAX_VALUE);
				
		if (s.getSize() > t.getPoints().size())
			return new Pair<>(null, maxValues);

		List<Point> menor = s.getPoints();
		List<Point> maior = t.getPoints();
		
//		int idxs = this.train.indexOf(s.getTrajectory()); 
//		int idxt = this.train.indexOf(t); // mdist[idxs][idx];

		int size =  s.getSize();
		int diffLength = maior.size() - size;	
		int limit = maior.size() - size + 1;
				
		int[] comb = s.getPointFeatures();
		double[] currentSum = new double[comb.length];
		double[] values = new double[numberOfFeatures];
		double[][] distancesForT = new double[comb.length][diffLength+1];
						
		double[] x = new double[comb.length];
		Arrays.fill(x, MAX_VALUE);
				
		for (int i = 0; i <= diffLength; i++) {

			Arrays.fill(currentSum, 0);
						
			for (int j = 0; j < menor.size(); j++) {

				if (mdist == null) {
					values = getDistances(menor.get(j), maior.get(i + j), comb);
					
					for (int k = 0; k < comb.length; k++) {					
						if (currentSum[k] != MAX_VALUE && values[k] != MAX_VALUE)
							currentSum[k] += values[k];
						else 
							currentSum[k] = MAX_VALUE;
						
					}
				} else {
					for (int k = 0; k < comb.length; k++) {
						if (currentSum[k] != MAX_VALUE && mdist[s.getStart()+j][idxt][comb[k]][i+j] != MAX_VALUE)
							currentSum[k] += mdist[s.getStart()+j][idxt][comb[k]][i+j]; //values[comb[k]] * values[comb[k]];
						else
							currentSum[k] = MAX_VALUE;
			
					}
				}	
			}
			
			for (int k = 0; k < comb.length; k++) {
				distancesForT[k][i] = currentSum[k];
			}
		}
		
		double[][] ranksForT = new double[comb.length][];
		
		for (int k = 0; k < comb.length; k++) {
			ranksForT[k] = rankingAlgorithm.rank(Arrays.stream(distancesForT[k],0,limit).toArray());
		} // for (int k = 0; k < numberOfFeatures; k++)
		
		
		int bestPosition = bestAlignmentByRanking(ranksForT, comb, (mdist == null? true : false));
		
		double[] bestAlignment = new double[comb.length];
		
		for (int j = 0; j < comb.length; j++) {
			
			double distance = (bestPosition >= 0) ? distancesForT[j][bestPosition] : MAX_VALUE;
			
			bestAlignment[j] = (distance != MAX_VALUE) ? 
					Math.sqrt( distance / size ) : MAX_VALUE;
			
		} // for (int j = 0; j < comb.length; j++)
		
		int start = bestPosition;
		int end = bestPosition + menor.size() - 1;
		
//		return bestAlignment;
		return new Pair<>(new Subtrajectory(start, end , t), bestAlignment);
	}
	
	/**
	 * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE OUTPUT TRANSFORMATIONS:     * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * .
	 *
	 * @param candidates the candidates
	 * @param trajectories the trajectories
	 * @param file the file
	 * @param mdist *
	 */
	
	public void transformTrajectoryOutput(List<Subtrajectory> candidates, List<MAT<MO>> trajectories, 
			String file, double[][][][] mdist) {
		
		for (Subtrajectory movelet : candidates) {
			// It initializes the set of distances of all movelets to null
			movelet.setDistances(null);
			
			// In this step the set of distances is filled by this method
			computeDistances(movelet, trajectories, mdist); // computeDistances(movelet, trajectories);
		}
		
		/** STEP 3.0: Output Movelets (partial) */
		super.output(file, trajectories, candidates, true);
		
	}
	
	/**
	 * Compute distances.
	 *
	 * @param candidate the candidate
	 * @param trajectories the trajectories
	 * @param mdist the mdist
	 */
	public void computeDistances(Subtrajectory candidate, List<MAT<MO>> trajectories, double[][][][] mdist) {
		/* This pairs will store the subtrajectory of the best alignment 
		 * of the candidate into each trajectory and the distance 
		 * */
		Pair<Subtrajectory, double[]> distance;
		
		double[][] trajectoryDistancesToCandidate = new double[candidate.getSplitpoints().length]
															  [trajectories.size()];
		
		List<Subtrajectory> bestAlignments = new ArrayList<Subtrajectory>();
				
		/* It calculates the distance of trajectories to the candidate
		 */
		for (int i = 0; i < trajectories.size(); i++) {
			
			distance = bestAlignmentByPointFeatures(candidate, trajectories.get(i), mdist, i);
			
			for (int j = 0; j < candidate.getSplitpoints().length; j++) {
				trajectoryDistancesToCandidate[j][i] = distance.getSecond()[j];							
			}
						
			bestAlignments.add(distance.getFirst());
//			trajectoryDistancesToCandidate[i] = distance.getSecond();			
		}
		
		candidate.setDistances(trajectoryDistancesToCandidate);
		candidate.setBestAlignments(bestAlignments);
	}

}
