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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math3.util.Pair;

import br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;
import br.com.tarlis.mov3lets.model.Subtrajectory;
import br.com.tarlis.mov3lets.utils.Mov3letsUtils;
import br.com.tarlis.mov3lets.utils.ProgressBar;

/**
 * The Class PrecomputeMoveletsDiscovery.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <MO> the generic type
 */
public class PrecomputeMoveletsDiscovery<MO> extends MemMoveletsDiscovery<MO> {
	
	/** The base. */
	private static double[][][][][] base = null;
	
	/**
	 * Instantiates a new precompute movelets discovery.
	 *
	 * @param trajsFromClass the trajs from class
	 * @param data the data
	 * @param train the train
	 * @param test the test
	 * @param qualityMeasure the quality measure
	 * @param descriptor the descriptor
	 */
	public PrecomputeMoveletsDiscovery(List<MAT<MO>> trajsFromClass, List<MAT<MO>> data, List<MAT<MO>> train, List<MAT<MO>> test, QualityMeasure qualityMeasure, 
			Descriptor descriptor) {
		super(trajsFromClass, data, train, test, qualityMeasure, descriptor);
	}
	

	/**
	 * Looks for candidates in the trajectory, then compares with every other trajectory.
	 *
	 * @return the list
	 */
	public List<Subtrajectory> discover() {
		
//		int n = this.data.size();
		int maxSize = getDescriptor().getParamAsInt("max_size");
		int minSize = getDescriptor().getParamAsInt("min_size");

		List<Subtrajectory> movelets = new ArrayList<Subtrajectory>();
		
		/** STEP 2.1: Starts at discovering movelets */
		for (MAT<MO> trajectory : trajsFromClass) {
			List<Subtrajectory> candidates = moveletsDiscovery(trajectory, this.train, minSize, maxSize);
			
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
		
		/** STEP 2.6, to write all outputs: */
		super.output("train", this.train, movelets, false);
		// Compute distances and best alignments for the test trajectories:
		/* If a test trajectory set was provided, it does the same.
		 * and return otherwise */
		if (!this.test.isEmpty())
			super.output("test", this.test, movelets, false);
				
		return movelets;
	}
	
	/**
	 * * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE BASE CASE COMPUTATION: * * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * * *.
	 *
	 * @param <MO> the generic type
	 * @param data the data
	 * @param N_THREADS the n threads
	 * @param descriptor the descriptor
	 */
	
	public  static <MO> void initBaseCases(List<MAT<MO>> data, int N_THREADS, Descriptor descriptor) {
		if (base == null) {
			if (N_THREADS > 1)
				multithreadComputeBaseDistances(data, N_THREADS, descriptor);
			else 
				computeBaseDistances(data, descriptor);
			System.gc();
		}
	}

	/**
	 * Compute base distances.
	 *
	 * @param <MO> the generic type
	 * @param trajectories the trajectories
	 * @param descriptor the descriptor
	 */
	public static <MO> void computeBaseDistances(List<MAT<MO>> trajectories, Descriptor descriptor){
//		int index = trajectories.indexOf(trajectory);
		int size = 1;

		base = new double[trajectories.size()][][][][];
		
		ProgressBar bar = new ProgressBar("[2.0] >> Computing Base Distances", 
				Mov3letsUtils.getInstance().totalPoints((List) trajectories));
		
		for (int i = 0; i < trajectories.size(); i++) {
			new PrecomputeBaseDistances<MO>(i, trajectories, 
					base, descriptor, bar).computeBaseDistances(i, trajectories);
		}
	}
	
	/**
	 * Multithread compute base distances.
	 *
	 * @param <MO> the generic type
	 * @param trajectories the trajectories
	 * @param N_THREADS the n threads
	 * @param descriptor the descriptor
	 */
	public static <MO> void multithreadComputeBaseDistances(List<MAT<MO>> trajectories, int N_THREADS, Descriptor descriptor) {
		ProgressBar bar = new ProgressBar("[2.0] >> Computing Base Distances", 
				Mov3letsUtils.getInstance().totalPoints((List) trajectories));

		base = new double[trajectories.size()][][][][];
		
		ExecutorService executor = (ExecutorService) 
				Executors.newFixedThreadPool(N_THREADS);
		List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
		
		for (int i = 0; i < trajectories.size(); i++) {
			Callable<Integer> task = new PrecomputeBaseDistances<MO>(i, trajectories, 
					base, descriptor, bar);
			futures.add(executor.submit(task));
		}
		
		for (Future<Integer> future : futures) {
			try {
				future.get();
				Executors.newCachedThreadPool();
				System.gc();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
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
	public List<Subtrajectory> moveletsDiscovery(MAT<MO> trajectory, List<MAT<MO>> trajectories, int minSize, int maxSize) {
		List<Subtrajectory> candidates = new ArrayList<Subtrajectory>();

		Random random = new Random(trajectory.getTid());
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
		
//		base = computeBaseDistances(trajectory, trajectories);
		
		int idxt = trajectories.indexOf(trajectory);
		
		if( minSize <= 1 ) {
			candidates.addAll(findCandidates(trajectory, trajectories, size, idxt));
			candidates.forEach(x -> assesQuality(x, random));
		}				
		
//		double[][][][] lastSize = clone4DArray(base);		

		total_size = total_size + candidates.size();
		
		// Tratar o resto dos tamanhos 
		for (size = 2; size <= maxSize; size++) {
	
			// Precompute de distance matrix
//			double[][][][] newSize = newSize(trajectory, trajectories, base, lastSize, size);

			// Create candidates and compute min distances		
			List<Subtrajectory> candidatesOfSize = findCandidates(trajectory, trajectories, size, idxt);
		
			total_size = total_size + candidatesOfSize.size();
			
			if (size >= minSize){
				
				//for (Subtrajectory candidate : candidatesOfSize) assesQuality(candidate);				
				candidatesOfSize.forEach(x -> assesQuality(x, random));
				
				candidates.addAll(candidatesOfSize);
			}
		
//			lastSize = newSize;
						
		} // for (int size = 2; size <= max; size++)	
	
//		base =  null;
//		lastSize = null;

		candidates = filterMovelets(candidates);
		
		progressBar.trace("Class: " + trajectory.getMovingObject() + ". Trajectory: " + trajectory.getTid() + ". Trajectory Size: " + trajectory.getPoints().size() + ". Number of Candidates: " + total_size + ". Total of Movelets: " + candidates.size() + ". Max Size: " + maxSize+ ". Used Features: " + this.maxNumberOfFeatures);

		return candidates;
	}
	
	/**
	 * Find candidates.
	 *
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param size the size
	 * @param idxt the idxt
	 * @return the list
	 */
	public List<Subtrajectory> findCandidates(MAT<MO> trajectory, List<MAT<MO>> trajectories, int size, int idxt) {
		
		// Trajectory P size => n
		int n = trajectory.getPoints().size();
		int[][] combinations = makeCombinations(exploreDimensions, numberOfFeatures, maxNumberOfFeatures);
		
		// List of Candidates to extract from P:
		List<Subtrajectory> candidates = new ArrayList<>();
		

		// From point 0 to (n - <candidate max. size>) 
		for (int start = 0; start <= (n - size); start++) {
//			Point p = trajectory.getPoints().get(start);
			
			// Extract possible candidates from P to max. candidate size:
			List<Subtrajectory> list = buildSubtrajectory(start, start + size - 1, trajectory, trajectories.size(), combinations);
									
			// For each trajectory in the database
			for (int i = 0; i < trajectories.size(); i++) {
				MAT<MO> T = trajectories.get(i);	
				
				int limit = T.getPoints().size() - size + 1;
				
				if (limit > 0)
					for (Subtrajectory subtrajectory : list) {	
						Pair<Subtrajectory, double[]> bestAlignment = bestAlignmentByPointFeatures(subtrajectory, T, idxt, i);
						
						double[] distances = bestAlignment.getSecond();
						for (int j = 0; j < subtrajectory.getPointFeatures().length; j++) {
							subtrajectory.getDistances()[j][i] = distances[j];							
						}
						
						subtrajectory.getBestAlignments().add(bestAlignment.getFirst());
					}
				
			} // for (int currentFeatures = 1; currentFeatures <= numberOfFeatures; currentFeatures++)
			
			candidates.addAll(list);

		} // for (int start = 0; start <= (n - size); start++)
		
		return candidates;
		
	}
	
	/**
	 * Best alignment by point features.
	 *
	 * @param s the s
	 * @param t the t
	 * @param idxTs the idx ts
	 * @param idxT the idx T
	 * @return the pair
	 */
	public Pair<Subtrajectory, double[]> bestAlignmentByPointFeatures(Subtrajectory s, MAT<MO> t, int idxTs, int idxT) {
		double[] maxValues = new double[numberOfFeatures];
		Arrays.fill(maxValues, MAX_VALUE);
				
		if (s.getSize() > t.getPoints().size())
			return new Pair<>(null, maxValues);

		List<Point> menor = s.getPoints();
		List<Point> maior = t.getPoints();
		
		int diffLength = maior.size() - menor.size();		
				
		int[] comb = s.getPointFeatures();
		double currentSum[] = new double[comb.length];
		double[] values = new double[numberOfFeatures];
		double[][] distancesForT = new double[comb.length][diffLength+1];
						
		double[] x = new double[comb.length];
		Arrays.fill(x, MAX_VALUE);
				
		for (int i = 0; i <= diffLength; i++) {

			Arrays.fill(currentSum, 0);
						
			for (int j = 0; j < menor.size(); j++) {

				// Here we get from mdist:
				values = getDistances(idxTs, j, idxT, (i + j));
//				values = mdist.getBaseDistances(menor.get(j), maior.get(i + j), comb);

				for (int k = 0; k < comb.length; k++) {					
					if (currentSum[k] != MAX_VALUE && values[k] != MAX_VALUE)
						currentSum[k] += values[comb[k]]; // * values[comb[k]];
					else {
						currentSum[k] = MAX_VALUE;
					}
				}
				
				
				if (firstVectorGreaterThanTheSecond(currentSum, x) ){
					for (int k = 0; k < comb.length; k++) {
						currentSum[k] = MAX_VALUE;
					}					
					break;					
				} 											
				
			}
			
			if (firstVectorGreaterThanTheSecond(x, currentSum) ){
				for (int k = 0; k < comb.length; k++) {
					x[k] = currentSum[k];					
				}				
			}
			
			for (int k = 0; k < comb.length; k++) {
				distancesForT[k][i] = currentSum[k];
			}
		}
		
		double[][] ranksForT = new double[distancesForT.length][];
		
		for (int k = 0; k < comb.length; k++) {
			ranksForT[k] = rankingAlgorithm.rank(distancesForT[k]);
		} // for (int k = 0; k < numberOfFeatures; k++)
		
		
		int bestPosition = bestAlignmentByRanking(ranksForT,comb);
		
		double[] bestAlignment = new double[comb.length];
		
		for (int j = 0; j < comb.length; j++) {
			
			double distance = distancesForT[j][bestPosition];
			
			bestAlignment[j] = 
					(distance != MAX_VALUE) ? Math.sqrt(distance / menor.size()) 
												   : MAX_VALUE;
			
		} // for (int j = 0; j < comb.length; j++)
		
		int start = bestPosition;
		int end = bestPosition + menor.size() - 1;
		
//		return bestAlignment;
		return new Pair<>(new Subtrajectory(start, end , t), bestAlignment);
	}
	
	/**
	 * Gets the distances.
	 *
	 * @param idxTs the idx ts
	 * @param pa the pa
	 * @param idxT the idx T
	 * @param pb the pb
	 * @return the distances
	 */
	public double[] getDistances(int idxTs, int pa, int idxT, int pb) {
		if (idxTs < idxT)
			return base[idxTs][pa][idxT - idxTs][pb];
		else
			return base[idxT][pa][idxTs - idxT][pb];
	}
	
	/**
	 * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE OUTPUT TRANSFORMATIONS:     * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * *.
	 *
	 * @param candidates the candidates
	 * @param trajectories the trajectories
	 * @param file the file
	 * @param startIdx the start idx
	 */
	
	public void transformTrajectoryOutput(List<Subtrajectory> candidates, List<MAT<MO>> trajectories, 
			String file, int startIdx) {
		
		for (Subtrajectory movelet : candidates) {
			// It initializes the set of distances of all movelets to null
			movelet.setDistances(null);
			// In this step the set of distances is filled by this method
			computeDistances(movelet, trajectories, startIdx); // computeDistances(movelet, trajectories);
		}
		
		/** STEP 3.0: Output Movelets */
		super.output(file, trajectories, candidates, true);
		
	}
	
	/**
	 * Compute distances.
	 *
	 * @param candidate the candidate
	 * @param trajectories the trajectories
	 * @param startIdx the start idx
	 */
	public void computeDistances(Subtrajectory candidate, List<MAT<MO>> trajectories, int startIdx) {
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
			
			distance = bestAlignmentByPointFeatures(candidate, trajectories.get(i), this.train.indexOf(candidate.getTrajectory()), i+startIdx);
			
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
