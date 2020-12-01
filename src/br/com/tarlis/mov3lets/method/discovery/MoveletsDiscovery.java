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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;
import org.apache.commons.math3.util.Combinations;
import org.apache.commons.math3.util.Pair;

import br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure;
import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class MoveletsDiscovery.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <MO> the generic type
 */
public class MoveletsDiscovery<MO> extends DiscoveryAdapter<MO> {

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
	 * Instantiates a new movelets discovery.
	 *
	 * @param trajsFromClass the trajs from class
	 * @param data the data
	 * @param train the train
	 * @param test the test
	 * @param qualityMeasure the quality measure
	 * @param descriptor the descriptor
	 */	
	public MoveletsDiscovery(List<MAT<MO>> trajsFromClass, List<MAT<MO>> data, List<MAT<MO>> train, List<MAT<MO>> test, QualityMeasure qualityMeasure, 
			Descriptor descriptor) {
		super(trajsFromClass, data, train, test, descriptor);
		init(qualityMeasure);
	}

	/**
	 * Inits the.
	 *
	 * @param qualityMeasure the quality measure
	 */
	private void init(QualityMeasure qualityMeasure) {
		this.qualityMeasure = qualityMeasure;
		
		this.numberOfFeatures = getDescriptor().numberOfFeatures();
		this.maxNumberOfFeatures = getDescriptor().getParamAsInt("max_number_of_features");
		this.exploreDimensions = getDescriptor().getFlag("explore_dimensions");
		
		switch (maxNumberOfFeatures) {
			case -1: this.maxNumberOfFeatures = numberOfFeatures; break;
			case -2: this.maxNumberOfFeatures = (int) Math.ceil(Math.log(numberOfFeatures))+1; break;
			default: break;
		}
	}
	
	/**
	 * * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE DISCOVERING PROCEDURES: * * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * * *.
	 *
	 * @return the list
	 */

	/**
	 * Looks for candidates in the trajectory, then compares with every other trajectory
	 */
	public List<Subtrajectory> discover() {
		
//		int n = this.data.size();
		int maxSize = getDescriptor().getParamAsInt("max_size");
		int minSize = getDescriptor().getParamAsInt("min_size");

		/** STEP 2.1: Starts at discovering movelets */
//		progressBar.trace("Class: " + trajsFromClass.get(0).getMovingObject() + "."); // Might be saved in HD
//		Mov3letsUtils.getInstance().startTimer("\tClass >> " + trajectory.getClass());
		
		List<Subtrajectory> movelets = new ArrayList<Subtrajectory>();

		progressBar.trace("Movelets Discovery for Class [No MEM]: " + trajsFromClass.get(0).getMovingObject());
		
		for (MAT<MO> trajectory : trajsFromClass) {
			// This guarantees the reproducibility
			Random random = new Random(trajectory.getTid());

			/** STEP 2.1: --------------------------------- */
			List<Subtrajectory> candidates = moveletsDiscovery(trajectory, this.train, minSize, maxSize, random);
		
	//		Mov3letsUtils.getInstance().stopTimer("\tClass >> " + trajectory.getClass());
			
			/** Summary Candidates: */
	//		Map<String,Integer> map = new HashMap<String,Integer>();
	//		for (Subtrajectory m : candidates) {
	//			String str =  Arrays.toString( m.getPointFeatures() );
	//			if (map.containsKey(str))
	//				map.put(str, (map.get(str) + 1) );
	//			else 
	//				map.put(str, 1);
	//		}
	//		Mov3letsUtils.trace(map.toString());
	//		MyCounter.data.put("MoveletsDiscoveryTime", estimatedTime);
	//		MyCounter.data.put("MoveletsFound", (long) movelets.size());
	//		MyCounter.numberOfShapelets = movelets.size();
			
	//		MyCounter.data.put("MoveletsAfterPruning", (long) movelets.size());
			
	//		int numberOfCandidates = (maxSize * (maxSize-1) / 2);

			/** STEP 2.3, for this trajectory movelets: 
			 * It transforms the training and test sets of trajectories using the movelets */
//			for (Subtrajectory candidate : candidates) {
//				// It initializes the set of distances of all movelets to null
//				candidate.setDistances(null);
//				// In this step the set of distances is filled by this method
//				computeDistances(candidate, this.train);
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
		// Compute distances and best alignments for the test trajectories:
		/* If a test trajectory set was provided, it does the same.
		 * and return otherwise */
		if (!this.test.isEmpty())
			super.output("test", this.test, movelets, false);
		
		return movelets;
				
	}

	/**
	 * Movelets discovery.
	 *
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param minSize the min size
	 * @param maxSize the max size
	 * @param random the random
	 * @return the list
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
		
		if( minSize <= 1 ) {
			candidates.addAll(findCandidates(trajectory, trajectories, size));
			candidates.forEach(x -> assesQuality(x, random));
		}				
		
		total_size = total_size + candidates.size();
		
		// Tratar o resto dos tamanhos 
		for (size = 2; size <= maxSize; size++) {
			
			if (size >= minSize){

				// Create candidates and compute min distances		
				List<Subtrajectory> candidatesOfSize = findCandidates(trajectory, trajectories, size);
				
				total_size = total_size + candidatesOfSize.size();
				
				//for (Subtrajectory candidate : candidatesOfSize) assesQuality(candidate);				
				candidatesOfSize.forEach(x -> assesQuality(x, random));
				candidates.addAll(candidatesOfSize);
			}
						
		} // for (int size = 2; size <= max; size++)	
		
		candidates = filterMovelets(candidates);
		
		progressBar.plus("Class: " + trajectory.getMovingObject() 
						+ ". Trajectory: " + trajectory.getTid() 
						+ ". Trajectory Size: " + trajectory.getPoints().size() 
						+ ". Number of Candidates: " + total_size 
						+ ". Total of Movelets: " + candidates.size() 
						+ ". Max Size: " + maxSize
						+ ". Used Features: " + this.maxNumberOfFeatures);
				
		return candidates;
	}

	/**
	 * [THE GREAT GAP].
	 *
	 * @param trajectory the trajectory
	 * @param trajectories the trajectories
	 * @param size the size
	 * @return the list
	 */
	public List<Subtrajectory> findCandidates(MAT<MO> trajectory, List<MAT<MO>> trajectories, int size) {
		
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
				
//				int limit = T.getPoints().size() - size + 1;
				
//				if (limit > 0)
					for (Subtrajectory subtrajectory : list) {						
						double[] distances = bestAlignmentByPointFeatures(subtrajectory, T).getSecond();
						for (int j = 0; j < subtrajectory.getPointFeatures().length; j++) {
							subtrajectory.getDistances()[j][i] = distances[j]; //Math.sqrt(distances[j] / size);							
						}
					}
				
			} // for (int currentFeatures = 1; currentFeatures <= numberOfFeatures; currentFeatures++)
			
			candidates.addAll(list);

		} // for (int start = 0; start <= (n - size); start++)
		
		return candidates;
		
	}
	
	/** The combinations. */
	protected int[][] combinations = null;
	
	/**
	 * Make combinations.
	 *
	 * @param exploreDimensions the explore dimensions
	 * @param numberOfFeatures the number of features
	 * @param maxNumberOfFeatures the max number of features
	 * @return the int[][]
	 */
	public int[][] makeCombinations(boolean exploreDimensions, int numberOfFeatures, int maxNumberOfFeatures) {
		
		if (combinations != null)
			return combinations;
		
		int currentFeatures;
		if (exploreDimensions){
			currentFeatures = 1;
		} else {
			currentFeatures = numberOfFeatures;
		}
		
		combinations = new int[(int) (Math.pow(2, maxNumberOfFeatures) - 1)][];
		int k = 0;
		// For each possible NumberOfFeatures and each combination of those: 
		for (;currentFeatures <= maxNumberOfFeatures; currentFeatures++) {
			for (int[] comb : new Combinations(numberOfFeatures,currentFeatures)) {					
				
				combinations[k++] = comb;
				
			} // for (int[] comb : new Combinations(numberOfFeatures,currentFeatures)) 					
		} // for (int i = 0; i < train.size(); i++

		return combinations;
	}
	
	/**
	 * Best alignment by point features.
	 *
	 * @param s the s
	 * @param t the t
	 * @return the pair
	 */
	public Pair<Subtrajectory, double[]> bestAlignmentByPointFeatures(Subtrajectory s, MAT<MO> t) {
		double[] maxValues = new double[numberOfFeatures];
		Arrays.fill(maxValues, MAX_VALUE);
				
		if (s.getSize() > t.getPoints().size())
			return new Pair<>(null, maxValues);

		List<Point> menor = s.getPoints();
		List<Point> maior = t.getPoints();
		
		int size =  s.getSize();
		int diffLength = maior.size() - size;	
		int limit = maior.size() - size + 1;		
				
		int[] comb = s.getPointFeatures();
		double[] currentSum; // = new double[comb.length];
		double[] values = new double[numberOfFeatures];
		double[][] distancesForT = new double[comb.length][diffLength+1];
						
		double[] x = new double[comb.length];
		Arrays.fill(x, MAX_VALUE);
				
		for (int i = 0; i <= diffLength; i++) {

//			Arrays.fill(currentSum, 0);
			currentSum = new double[comb.length];
						
			for (int j = 0; j < size; j++) {

				// Here we get from mdist:
				values = getDistances(menor.get(j), maior.get(i + j), s.getPointFeatures());
//				values = mdist.getBaseDistances(menor.get(j), maior.get(i + j), comb);

				for (int k = 0; k < comb.length; k++) {					
					if (currentSum[k] != MAX_VALUE && values[k] != MAX_VALUE)
//					if (currentSum[k] < MAX_VALUE)
						currentSum[k] += values[k]; // * values[comb[k]];
//						distancesForT[k][i] = values[k];
					else {
						currentSum[k] = MAX_VALUE;
//						distancesForT[k][i] = MAX_VALUE;
					}
				}
								
//				if (firstVectorGreaterThanTheSecond(currentSum, x) ){
////					for (int k = 0; k < comb.length; k++) {
////						currentSum[k] = MAX_VALUE;
////					}
//					Arrays.fill(currentSum, MAX_VALUE);
//					break;					
//				} 											
				
			}
			
//			if (firstVectorGreaterThanTheSecond(x, currentSum) ){
////				for (int k = 0; k < comb.length; k++) {
////					x[k] = currentSum[k];					
////				}	
//				x = currentSum;
//			}
			
			for (int k = 0; k < comb.length; k++) {
				distancesForT[k][i] = currentSum[k];
			}
		}
		
		double[][] ranksForT = new double[distancesForT.length][];
		
		if (limit > 0)
			for (int k = 0; k < comb.length; k++) {
//				ranksForT[k] = rankingAlgorithm.rank(distancesForT[k]);
				ranksForT[k] = rankingAlgorithm.rank(Arrays.stream(distancesForT[k],0,limit).toArray());
			} // for (int k = 0; k < numberOfFeatures; k++)
		
		
//		int bestPosition = bestAlignmentByRanking(ranksForT,comb);
		int bestPosition = (limit > 0) ? bestAlignmentByRanking(ranksForT,comb) : -1;
		
		double[] bestAlignment = new double[comb.length];
		
		for (int j = 0; j < comb.length; j++) {
			
//			double distance = distancesForT[j][bestPosition];
			double distance = (bestPosition >= 0) ? distancesForT[j][bestPosition] : MAX_VALUE;
			
			bestAlignment[j] = (distance != MAX_VALUE) ? 
					Math.sqrt( distance / size ) : MAX_VALUE;
			
		} // for (int j = 0; j < comb.length; j++)
		
		int start = bestPosition;
		int end = bestPosition + size - 1;
		
//		return bestAlignment;
		return new Pair<>(new Subtrajectory(start, end , t), bestAlignment);
	}
	
	/**
	 * Best alignment by ranking.
	 *
	 * @param ranksForT the ranks for T
	 * @param comb the comb
	 * @return the int
	 */
	public int bestAlignmentByRanking(double[][] ranksForT, int[] comb) {
		
		double[] rankMerged = new double[ranksForT[0].length];
		
		if (ranksForT.length > 1)
			// In case it's a combination of more than one dimension
			for (int i = 0; i < comb.length; i++) {
				for (int j = 0; j < ranksForT[0].length; j++) {
	//				rankMerged[j] += ranksForT[comb[i]][j];
					rankMerged[j] += ranksForT[i][j]; // It's indexed differently now
				}
			}
		else
			// W/ one dimention, no need to merge (use directly)
			rankMerged = ranksForT[0];

		int minRankIndex = 0;
		for (int j = 1; j < rankMerged.length; j++) {
			if (rankMerged[j] < rankMerged[minRankIndex])
				minRankIndex = j;
		}
		
		return minRankIndex;
	}

	/** The k. */
	protected int K = 0;
	
	/**
	 * Builds the subtrajectory.
	 *
	 * @param start the start
	 * @param end the end
	 * @param t the t
	 * @param numberOfTrajectories the number of trajectories
	 * @param combinations the combinations
	 * @return the list
	 */
	public List<Subtrajectory> buildSubtrajectory(
			int start, int end, MAT<MO> t, int numberOfTrajectories, int[][] combinations){
		
		List<Subtrajectory> list = new ArrayList<>();
		
		for (int k = 0; k < combinations.length; k++) {
			list.add(new Subtrajectory(start, end, t, numberOfTrajectories, combinations[k], K++));
		}
				
		return list;
	}
	

	
	/**
	 * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE QUALITY ASSESMENT:    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * *.
	 *
	 * @param candidate the candidate
	 * @param random the random
	 */
	
	/**
	 * @param x
	 * @param random
	 * @return
	 */
	public void assesQuality(Subtrajectory candidate, Random random) {
		qualityMeasure.assesQuality(candidate, random);
//		assesQuality(candidate);
	}
	
	/**
	 * Asses quality.
	 *
	 * @param candidate the candidate
	 */
	public void assesQuality(Subtrajectory candidate) {
		qualityMeasure.assesQuality(candidate, new Random());
	}
	
	/**
	 * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE OUTPUT TRANSFORMATIONS:     * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * .
	 *
	 * @param candidates the candidates
	 * @param trajectories the trajectories
	 * @param file the file
	 * @return *
	 */

//	public void transformOutput(List<Subtrajectory> candidates) {
//		for (Subtrajectory movelet : candidates) {
//			// It initializes the set of distances of all movelets to null
//			movelet.setDistances(null);
//			// In this step the set of distances is filled by this method
//			computeDistances(movelet, this.data); // computeDistances(movelet, trajectories);
//		}
//		
//		/** STEP 3.0: Output Movelets */
//		super.output("train", this.data, candidates);
//		super.output("test", this.data, candidates);
//	}

	public List<Subtrajectory> transformTrajectoryOutput(List<Subtrajectory> candidates, List<MAT<MO>> trajectories, String file) {
		for (Subtrajectory movelet : candidates) {
			// It initializes the set of distances of all movelets to null
			movelet.setDistances(null);
			// In this step the set of distances is filled by this method
			computeDistances(movelet, trajectories); // computeDistances(movelet, trajectories);
		}

		/** STEP 2.5: SELECTING BEST CANDIDATES */			
		candidates = filterMovelets(candidates); //TODO is necessary?
		
		/** STEP 3.0: Output Movelets (partial) */
		super.output(file, trajectories, candidates, true);
		
		return candidates;
	}

//	public void transformOutput(List<Subtrajectory> candidates, List<MAT<MO>> trajectories, String file) {
////		for (Subtrajectory movelet : candidates) {
////			// It initializes the set of distances of all movelets to null
////			movelet.setDistances(null);
////			// In this step the set of distances is filled by this method
////			computeDistances(movelet, trajectories); // computeDistances(movelet, trajectories);
////		}
//		
//		/** STEP 3.0: Output Movelets */
//		super.output(file, trajectories, candidates, false);
//	}
	
	/**
 * Compute distances.
 *
 * @param candidate the candidate
 * @param trajectories the trajectories
 */
// TODO: esse método é um problema, tem que ver como fazer isso e para que serve.
	public void computeDistances(Subtrajectory candidate, List<MAT<MO>> trajectories) {
		/* This pairs will store the subtrajectory of the best alignment 
		 * of the candidate into each trajectory and the distance 
		 * */
		Pair<Subtrajectory, double[]> distance;
		
		double[][] trajectoryDistancesToCandidate = new double[candidate.getPointFeatures().length]
															  [trajectories.size()];
		
		List<Subtrajectory> bestAlignments = new ArrayList<Subtrajectory>();
				
		/* It calculates the distance of trajectories to the candidate
		 */
		for (int i = 0; i < trajectories.size(); i++) {
			
			distance = bestAlignmentByPointFeatures(candidate, trajectories.get(i));
			
			for (int j = 0; j < candidate.getPointFeatures().length; j++) {
				trajectoryDistancesToCandidate[j][i] = distance.getSecond()[j];							
			}
						
			bestAlignments.add(distance.getFirst());
//			trajectoryDistancesToCandidate[i] = distance.getSecond();			
		}
		
		candidate.setDistances(trajectoryDistancesToCandidate);
		candidate.setBestAlignments(bestAlignments);
	}
	
	/**
	 * Gets the distances.
	 *
	 * @param a the a
	 * @param b the b
	 * @param comb the comb
	 * @return the distances
	 */
	public double[] getDistances(Point a, Point b, int[] comb) {

//		double[] distances = new double[this.descriptor.getAttributes().size()];
		double[] distances = new double[comb.length];
		
		int i = 0;
		for (int k : comb) {
			AttributeDescriptor attr = this.descriptor.getAttributes().get(k);
			
			distances[i++] = attr.getDistanceComparator().calculateDistance(
					a.getAspects().get(k), 
					b.getAspects().get(k), 
					attr); // This also enhance distances
		}
		
		return distances;
		
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
	 * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE FILTERING PROCEDURES: * * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * *.
	 *
	 * @param candidates the candidates
	 * @return the list
	 */
	
	/**
	 * 
	 * @param candidates
	 * @return
	 */
	public List<Subtrajectory> filterMovelets(List<Subtrajectory> candidates) {

		List<Subtrajectory> orderedCandidates = rankCandidates(candidates);

		return bestShapelets(orderedCandidates, 0);
	}
	
	/**
	 * Rank candidates.
	 *
	 * @param candidates the candidates
	 * @return the list
	 */
	public List<Subtrajectory> rankCandidates(List<Subtrajectory> candidates) {

		List<Subtrajectory> orderedCandidates = new ArrayList<>(candidates);
		
		orderedCandidates.removeIf(e -> e == null);
		
		orderedCandidates.sort(new Comparator<Subtrajectory>() {
			@Override
			public int compare(Subtrajectory o1, Subtrajectory o2) {
				
				return o1.getQuality().compareTo(o2.getQuality());				
				
			}
		});

		return orderedCandidates;
	}

	/**
	 * Best shapelets.
	 *
	 * @param rankedCandidates the ranked candidates
	 * @param selfSimilarityProp the self similarity prop
	 * @return the list
	 */
	public List<Subtrajectory> bestShapelets(List<Subtrajectory> rankedCandidates, double selfSimilarityProp) {

		// Realiza o loop até que acabem os atributos ou até que atinga o número
		// máximo de nBestShapelets
		// Isso é importante porque vários candidatos bem rankeados podem ser
		// selfsimilares com outros que tiveram melhor score;
		for (int i = 0; (i < rankedCandidates.size()); i++) {

			// Se a shapelet candidata tem score 0 então já termina o processo
			// de busca
			if (rankedCandidates.get(i).getQuality().hasZeroQuality())
				return rankedCandidates.subList(0, i);

			Subtrajectory candidate = rankedCandidates.get(i);

			// Removing self similar
			if (searchIfSelfSimilarity(candidate, rankedCandidates.subList(0, i), selfSimilarityProp)) {
				rankedCandidates.remove(i);
				i--;
			}

		}

		return rankedCandidates;
	}

	/**
	 * Search if self similarity.
	 *
	 * @param candidate the candidate
	 * @param list the list
	 * @param selfSimilarityProp the self similarity prop
	 * @return true, if successful
	 */
	public boolean searchIfSelfSimilarity(Subtrajectory candidate, List<Subtrajectory> list,
			double selfSimilarityProp) {

		for (Subtrajectory s : list) {
			if (areSelfSimilar(candidate, s, selfSimilarityProp))
				return true;
		}

		return false;
	}
	
	/**
	 * Are self similar.
	 *
	 * @param candidate the candidate
	 * @param subtrajectory the subtrajectory
	 * @param selfSimilarityProp the self similarity prop
	 * @return true, if successful
	 */
	public boolean areSelfSimilar(Subtrajectory candidate, Subtrajectory subtrajectory,
			double selfSimilarityProp) {
		
		//return false;
		
		// If their tids are different return false
		
		if (candidate.getTrajectory().getTid() != subtrajectory.getTrajectory().getTid())
			return false;

		else if (candidate.getStart() < subtrajectory.getStart()) {

			if (candidate.getEnd() < subtrajectory.getStart())
				return false;

			if (selfSimilarityProp == 0)
				return true;

			double intersection = (candidate.getEnd() - subtrajectory.getStart())
					/ (double) Math.min(candidate.getSize(), subtrajectory.getSize());

			return intersection >= selfSimilarityProp;

		} else {

			if (subtrajectory.getEnd() < candidate.getStart())
				return false;

			if (selfSimilarityProp == 0)
				return true;

			double intersection = (subtrajectory.getEnd() - candidate.getStart())
					/ (double) Math.min(candidate.getSize(), subtrajectory.getSize());

			return intersection >= selfSimilarityProp;

		}

	}
	

	/**
	 * * * * * * * * * * * * * * * * * * * * ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * HERE FOLLOWS THE PRUNNING PROCEDURES: ** * * * * * * * * * * * * * * * * * * * * * * * * * * * * >>
	 * ** * * * * * * * * * * * * * * * * * * *.
	 *
	 * @param movelets the movelets
	 * @return the list
	 */
	
	/**
	 * Last Prunning Method
	 * 
	 * @param candidates
	 * @return
	 */
	public List<Subtrajectory> lastPrunningFilter(List<Subtrajectory> movelets) {
		List<Subtrajectory> noveltyShapelets = new ArrayList<>();
		Set<Integer> allCovered = new HashSet<Integer>();
		
		for (int i = 0; i < movelets.size(); i++) {
			double[][] distances = movelets.get(i).getDistances();
			double[] splitpoint = movelets.get(i).getSplitpoints();
			Set<Integer> currentCovered = findIndexesLowerSplitPoint(distances, splitpoint);
			
			if ( ! SetUtils.difference(currentCovered, allCovered).isEmpty()){
				noveltyShapelets.add(movelets.get(i));
				allCovered.addAll(currentCovered);
			}
		}
		
		return noveltyShapelets;
	}
	
	/**
	 * Find indexes lower split point.
	 *
	 * @param distances the distances
	 * @param splitpoints the splitpoints
	 * @return the sets the
	 */
	public Set<Integer> findIndexesLowerSplitPoint(double[][] distances, double[] splitpoints ){
		Set<Integer> indexes = new HashSet<>();
		
		RealMatrix rm = new Array2DRowRealMatrix(distances);
		
		for (int i = 0; i < distances[0].length; i++) {
			if (isCovered(rm.getColumn(i), splitpoints) )			
				indexes.add(i);
			}
		
		return indexes;		
	}

	/**
	 * Checks if is covered.
	 *
	 * @param point the point
	 * @param limits the limits
	 * @return true, if is covered
	 */
	/* Para o caso de empate por conta de movelets discretas
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
