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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import br.com.tarlis.mov3lets.method.qualitymeasure.ProportionQualityMeasure;
import br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;
import br.com.tarlis.mov3lets.model.Subtrajectory;
import br.com.tarlis.mov3lets.model.aspect.Aspect;

/**
 * The Class SuperMoveletsDiscovery.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 * @param <MO> the generic type
 */
public class SuperMoveletsDiscovery<MO> extends MemMoveletsDiscovery<MO> {
	
	/** The tau. */
	protected double TAU 		= 0.9;
//	protected double GAMMA 		= 1.0;

	protected double BU 		= 0.1;

	/** The proportion measure. */
ProportionQualityMeasure<MO> proportionMeasure;
	
	/**
	 * Instantiates a new super movelets discovery.
	 *
	 * @param trajsFromClass the trajs from class
	 * @param data the data
	 * @param train the train
	 * @param test the test
	 * @param qualityMeasure the quality measure
	 * @param descriptor the descriptor
	 */
	public SuperMoveletsDiscovery(List<MAT<MO>> trajsFromClass, List<MAT<MO>> data, List<MAT<MO>> train, List<MAT<MO>> test, QualityMeasure qualityMeasure, 
			Descriptor descriptor) {
		super(trajsFromClass, data, train, test, qualityMeasure, descriptor);
		
		TAU 	= getDescriptor().getParamAsDouble("tau");
//		GAMMA 	= getDescriptor().getParamAsDouble("gamma");
		BU 		= getDescriptor().getParamAsDouble("bucket_slice");
	}
	
	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.discovery.MemMoveletsDiscovery#discover().
	 * 
	 * @return
	 */
	public List<Subtrajectory> discover() {

		int maxSize = getDescriptor().getParamAsInt("max_size");
		int minSize = getDescriptor().getParamAsInt("min_size");

		List<Subtrajectory> movelets = new ArrayList<Subtrajectory>();

		progressBar.trace("Super Movelets Discovery for Class: " + trajsFromClass.get(0).getMovingObject());
		
		this.proportionMeasure = new ProportionQualityMeasure<MO>(this.trajsFromClass, TAU);
		
		for (MAT<MO> trajectory : trajsFromClass) {
			// This guarantees the reproducibility
			Random random = new Random(trajectory.getTid());
			/** STEP 2.1: Starts at discovering movelets */
			List<Subtrajectory> candidates = moveletsDiscovery(trajectory, this.trajsFromClass, minSize, maxSize, random);
			
//			progressBar.trace("Class: " + trajectory.getMovingObject() 
//					+ ". Trajectory: " + trajectory.getTid() 
//					+ ". Used GAMMA: " + GAMMA);
			
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
		
//		progressBar.trace("Class: " + trajsFromClass.get(0).getMovingObject() 
//				   + ". Total of Movelets: " + movelets.size());

		/** STEP 2.5, to write all outputs: */
		super.output("train", this.train, movelets, false);
		
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
		List<Subtrajectory> candidatesByProp = new ArrayList<Subtrajectory>();

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
			candidatesByProp.addAll(findCandidates(trajectory, trajectories, size, base));
//			candidates.forEach(x -> assesQuality(x, random));
		}				
		
		double[][][][] lastSize = clone4DArray(base);		

		total_size = total_size + candidatesByProp.size();
		
		// Tratar o resto dos tamanhos 
		for (size = 2; size <= maxSize; size++) {
	
			// Precompute de distance matrix
   			double[][][][] newSize = newSize(trajectory, trajectories, base, lastSize, size);

			// Create candidates and compute min distances		
			List<Subtrajectory> candidatesOfSize = findCandidates(trajectory, trajectories, size, newSize);
		
			total_size = total_size + candidatesOfSize.size();
			
			if (size >= minSize){
				
				//for (Subtrajectory candidate : candidatesOfSize) assesQuality(candidate);				
//				candidatesOfSize.forEach(x -> assesQuality(x, random));
				candidatesByProp.addAll(candidatesOfSize);
			}
		
			lastSize = newSize;
						
		} // for (int size = 2; size <= max; size++)
		
		List<Subtrajectory> bestCandidates = selectBestCandidates(trajectory, maxSize, random, candidatesByProp);	
	
		base =  null;
		lastSize = null;
		
		return bestCandidates;
	}

	/**
	 * Select best candidates.
	 *
	 * @param trajectory the trajectory
	 * @param maxSize the max size
	 * @param random the random
	 * @param candidatesByProp the candidates by prop
	 * @return the list
	 */
	public List<Subtrajectory> selectBestCandidates(MAT<MO> trajectory, int maxSize, Random random,
			List<Subtrajectory> candidatesByProp) {
		List<Subtrajectory> bestCandidates;

//		GAMMA = getDescriptor().getParamAsDouble("gamma");
		bestCandidates = filterByProportion(candidatesByProp, random);
		bestCandidates = filterByQuality(bestCandidates, random, trajectory);

		/* STEP 2.1.5: Recover Approach (IF Nothing found)
		 * * * * * * * * * * * * * * * * * * * * * * * * */
		if (bestCandidates.isEmpty()) { 
			bestCandidates = recoverCandidates(trajectory, random, candidatesByProp);
		}
		
		progressBar.plus("Class: " + trajectory.getMovingObject() 
						+ ". Trajectory: " + trajectory.getTid() 
						+ ". Trajectory Size: " + trajectory.getPoints().size() 
						+ ". Number of Candidates: " + candidatesByProp.size() 
						+ ". Total of Movelets: " + bestCandidates.size() 
						+ ". Max Size: " + maxSize
						+ ". Used Features: " + this.maxNumberOfFeatures);

		return bestCandidates;
	}

	/**
	 * Recover candidates.
	 *
	 * @param trajectory the trajectory
	 * @param random the random
	 * @param candidatesByProp the candidates by prop
	 * @return the list
	 */
	public List<Subtrajectory> recoverCandidates(MAT<MO> trajectory, Random random,
			List<Subtrajectory> candidatesByProp) {
		
		int n = bucketSize(candidatesByProp.size());;
		
		orderCandidates(bucket);
//		bucket = filterEqualCandidates(bucket);
		List<Subtrajectory> bestCandidates = new ArrayList<Subtrajectory>();
		
//		bestCandidates = filterByQuality(bestCandidates, random, trajectory);
		
		for (int i = n; i < bucket.size(); i += n) {
			bestCandidates = filterByQuality(bucket.subList(i-n, (i > bucket.size()? bucket.size() : i)), random, trajectory);
			
			if (i > bucket.size() || !bestCandidates.isEmpty()) break;
			else n *= 2; // expand the window size
		}
		return bestCandidates;
	}

	/** The bucket. */
	protected List<Subtrajectory> bucket = new ArrayList<Subtrajectory>();
	
	/**
	 * Mehod bucketSize. 
	 * 
	 */
	protected int bucketSize(int candidatesByProp) {
		if (BU < 0) return candidatesByProp;
		
		int n = (int) Math.ceil((double) (candidatesByProp+bucket.size()) * BU); // By 10%
		return (n > candidatesByProp)? candidatesByProp : n;
	}
	
	/**
	 * Filter by proportion.
	 *
	 * @param candidatesByProp the candidates by prop
	 * @param random the random
	 * @return the list
	 */
	public List<Subtrajectory> filterByProportion(List<Subtrajectory> candidatesByProp, Random random) {
//		calculateProportion(candidatesByProp, random);
//		candidatesByProp = filterEqualCandidates(candidatesByProp);
		
		// Relative TAU based on the higher proportion:
		double rel_tau = (candidatesByProp.size() > 0? candidatesByProp.get(0).getQuality().getData().get("quality") : 0.0) * TAU;
		
		int n = bucketSize(candidatesByProp.size());

		/* STEP 2.1.2: SELECT ONLY CANDIDATES WITH PROPORTION > 50%
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
		List<Subtrajectory> orderedCandidates = new ArrayList<>();
		for(Subtrajectory candidate : candidatesByProp)
			if(orderedCandidates.size() <= n &&
			   candidateQuality(candidate) >= rel_tau) //TAU)
				orderedCandidates.add(candidate);
			else 
				bucket.add(candidate);
		
		return orderedCandidates;
	}

	/**
	 * Mehod candidateQuality. 
	 * 
	 * @param candidate
	 * @return
	 */
	protected double candidateQuality(Subtrajectory candidate) {
		return candidate.getQuality().getData().get("quality");
	}

	/**
	 * Filter equal candidates.
	 *
	 * @param orderedCandidates the ordered candidates
	 * @return the list
	 */
	public List<Subtrajectory> filterEqualCandidates(List<Subtrajectory> orderedCandidates) {
		/* STEP 2.1.4: IDENTIFY EQUAL CANDIDATES
		 * * * * * * * * * * * * * * * * * * * * * * * * */
		List<Subtrajectory> bestCandidates = new ArrayList<>();
//		int[] attribute_usage = new int [numberOfFeatures]; // array of 5 ints
		
		for(Subtrajectory candidate : orderedCandidates) {
			
			if(bestCandidates.isEmpty())
				bestCandidates.add(candidate);
			else {
				boolean equal = false;
				for(Subtrajectory best_candidate : bestCandidates) {
					
					List<HashMap<Integer, Aspect<?>>> used_features_c1 = getDimensions(candidate);
					List<HashMap<Integer, Aspect<?>>> used_features_c2 = getDimensions(best_candidate);
					
					if(used_features_c1.size()==used_features_c2.size())
						if(areEqual(used_features_c1, used_features_c2)) {
							equal = true;
							break;
						}
					
				}
				if(!equal) {
					bestCandidates.add(candidate);
//					attribute_usage[candidate.getPointFeatures().length-1]++;
				}
			}
		}
		return bestCandidates;
	}

	/**
	 * Calculate proportion.
	 *
	 * @param candidatesByProp the candidates by prop
	 * @param random the random
	 */
	public void calculateProportion(List<Subtrajectory> candidatesByProp, Random random) {
		candidatesByProp.forEach(x -> proportionMeasure.assesClassQuality(x, maxDistances, random));
		
		orderCandidates(candidatesByProp);
	}

	/**
	 * Order candidates.
	 *
	 * @param candidatesByProp the candidates by prop
	 */
	public void orderCandidates(List<Subtrajectory> candidatesByProp) {
		/* STEP 2.1.3: SORT THE CANDIDATES BY PROPORTION VALUE
		 * * * * * * * * * * * * * * * * * * * * * * * * * */
		candidatesByProp.sort(new Comparator<Subtrajectory>() {
			@Override
			public int compare(Subtrajectory o1, Subtrajectory o2) {
				
//				return (-1) * o1.getQuality().compareTo(o2.getQuality());
				return o1.getQuality().compareTo(o2.getQuality());				
				
			}
		});
	}

	/**
	 * Filter by quality.
	 *
	 * @param bestCandidates the best candidates
	 * @param random the random
	 * @param trajectory the trajectory
	 * @return the list
	 */
	public List<Subtrajectory> filterByQuality(List<Subtrajectory> bestCandidates, Random random, MAT<MO> trajectory) {
		/** STEP 2.3, for this trajectory movelets: 
		 * It transforms the training and test sets of trajectories using the movelets */
		for (Subtrajectory candidate : bestCandidates) {
			// It initializes the set of distances of all movelets to null
			candidate.setDistances(null);
			candidate.setQuality(null);
			// In this step the set of distances is filled by this method
			computeDistances(candidate, this.train); // computeDistances(movelet, trajectories);

			/* STEP 2.1.6: QUALIFY BEST HALF CANDIDATES 
			 * * * * * * * * * * * * * * * * * * * * * * * * */
//			assesQuality(candidate);
			assesQuality(candidate, random); //TODO change?
		}

		/** STEP 2.2: SELECTING BEST CANDIDATES */	
		return filterMovelets(bestCandidates);
	}
	
	/**
	 * Gets the dimensions.
	 *
	 * @param candidate the candidate
	 * @return the dimensions
	 */
	public List<HashMap<Integer, Aspect<?>>> getDimensions(Subtrajectory candidate) {
		
		List<Integer> features_in_movelet = new ArrayList<>();
		
		int[] list_features = candidate.getPointFeatures();
		
		for(int i=0; i <= getDescriptor().getAttributes().size(); i++) {
			
			if(ArrayUtils.contains(list_features, i))				
				features_in_movelet.add(i);
			
		}
		
		List<HashMap<Integer, Aspect<?>>> used_features = new ArrayList<>();
		
		for(int i=0; i < candidate.getPoints().size(); i++) {
			
			Point point = candidate.getPoints().get(i);
			
			HashMap<Integer, Aspect<?>> features_in_point = new HashMap<>();
			
			for(Integer feature : features_in_movelet) {
				features_in_point.put(feature, point.getAspects().get(feature));
			}
			
			used_features.add(features_in_point);
		}
		
		return used_features;
	}
	
	/**
	 * Are equal.
	 *
	 * @param first the first
	 * @param second the second
	 * @return true, if successful
	 */
	public boolean areEqual(List<HashMap<Integer, Aspect<?>>> first, List<HashMap<Integer, Aspect<?>>> second) {
		
		if (first.size() != second.size())
	        return false;
		
		if (first.get(0).size() != second.get(0).size())
	        return false;
	 
		for ( Integer key : first.get(0).keySet() ) {
			if(!second.get(0).containsKey(key)) {
		        return false;
		    }
		}
		
		boolean all_match = true;
		
		for(int i=0; i<first.size();i++) {
			
			HashMap<Integer, Aspect<?>> f = first.get(i);
			HashMap<Integer, Aspect<?>> s = second.get(i);
						
			if(!f.entrySet().stream()
				      .allMatch(e -> e.getValue().equals(s.get(e.getKey()))))
				return false;
			
		}
	    return all_match;
	}
	
}
