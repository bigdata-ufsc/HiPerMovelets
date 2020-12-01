package br.com.tarlis.mov3lets.method.qualitymeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.Pair;

import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class EntropyQualityMeasure.
 *
 * @param <MO> the generic type
 */
public class EntropyQualityMeasure<MO> extends ProportionQualityMeasure<MO> {
	
	/**
	 * Instantiates a new entropy quality measure.
	 *
	 * @param trajectories the trajectories
	 * @param tau the tau
	 */
	public EntropyQualityMeasure(List<MAT<MO>> trajectories, double tau) {
		super(trajectories, tau);
	}

	/**
	 * Instantiates a new entropy quality measure.
	 *
	 * @param trajectories the trajectories
	 * @param samples the samples
	 * @param sampleSize the sample size
	 * @param medium the medium
	 */
	public EntropyQualityMeasure(List<MAT<MO>> trajectories, int samples, double sampleSize, String medium) {
		super(trajectories, samples, sampleSize, medium);
	}
	
	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.qualitymeasure.ProportionQualityMeasure#assesQuality(br.com.tarlis.mov3lets.model.Subtrajectory, java.util.Random).
	 * 
	 * @param candidate
	 * @param random
	 */
	@Override
	public void assesQuality(Subtrajectory candidate, Random random) {
		assesClassQuality(candidate, getMaxDistances(candidate.getDistances()), random);
	}

	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.qualitymeasure.ProportionQualityMeasure#assesClassQuality(br.com.tarlis.mov3lets.model.Subtrajectory, double[], java.util.Random).
	 * 
	 * @param candidate
	 * @param maxDistances
	 * @param random
	 */
	@Override
	public void assesClassQuality(Subtrajectory candidate, double[] maxDistances, Random random) {
		
		int split  = (int) this.trajectories.stream().filter(e -> 
			candidate.getTrajectory().getMovingObject().equals(e.getMovingObject()))
				  .count();
		
		/*
		 * STEP 1: VERIFY WHICH ARE THE TRAJECTORIES THAT CONTAIN THAT CANDIDATE FOR EACH DIMENSION
		 */
//		MO target = (MO) candidate.getTrajectory().getMovingObject();
		double[][] distances = candidate.getDistances();

//		double[] maxValues = getMaxDistances(distances);
		
		RealMatrix rm = new Array2DRowRealMatrix(distances);
		
		/* Select only the distance of the target label
		 * */
		double[][] targetDistances = rm.getSubMatrix(0, distances.length-1, 0, split-1).getData(); //new double[distances.length][split+1];
		/* Select only the distance of the non-target label
		 * */
		double[][] nonTargetDistances = rm.getSubMatrix(0, distances.length-1, split, distances[0].length-1).getData(); // new double[distances.length][chosePoints.getSecond().size()-(split+1)];
	
		Pair<Double, List<MAT<MO>>> prop = proportionClass(targetDistances, candidate, new ArrayList<MAT<MO>>(), maxDistances, random);
		
		double pt = prop.getFirst();
		List<MAT<MO>> coveredInClass = prop.getSecond();
		
		prop = proportionClass(nonTargetDistances, candidate, null, maxDistances, random);
		double pnt = prop.getFirst();
		
//		double info = -Math.log(pt/pnt); // Information, based on proportion/probability - entrophy:
		double info = (pt-pnt); // Just proportion/probability
		
		Map<String, Double> data = new HashMap<>();
		
    	data.put("quality", info);
    	data.put("p_target", pt);
    	data.put("p_nontarget", pnt);
    	data.put("dimensions", 1.0 * candidate.getPointFeatures().length );    	
    	data.put("size", 1.0 * candidate.getSize() );
    	data.put("start", 1.0 * candidate.getStart() );
    	data.put("tid", 1.0 * candidate.getTrajectory().getTid() );
    	
    	ProportionQuality quality = new ProportionQuality();
    	quality.setData(data);	    
//    	quality.setCoveredInClass((List) coveredInClass);
    	candidate.setCovered((List) coveredInClass);
		candidate.setQuality(quality);
	}
	
	/**
	 * Proportion class.
	 *
	 * @param distances the distances
	 * @param candidate the candidate
	 * @param covered the covered
	 * @param maxDistances the max distances
	 * @param random the random
	 * @return the pair
	 */
	public Pair<Double, List<MAT<MO>>> proportionClass(double[][] distances, Subtrajectory candidate, List<MAT<MO>> covered, double[] maxDistances, Random random) {
		/*
		 * STEP 1: VERIFY WHICH ARE THE TRAJECTORIES THAT CONTAIN THAT CANDIDATE FOR EACH DIMENSION
		 */
		double[] splitPoints = new double[distances.length];
		
		double proportion  = 0.0;
		
		for (int i = 0; i < distances.length; i++) {
//			splitPoints[i] = maxDistances[candidate.getPointFeatures()[i]] * gamma;
			double sum = 0.0;
			
			for (int j = 0; j < distances[i].length; j++) {
//				if (distances[i][j] <= splitPoints[i])
				if (distances[i][j] != MAX_VALUE)
//					sum += maxDistances[candidate.getPointFeatures()[i]] - distances[i][j];
					sum += distances[i][j];
				else
					sum += maxDistances[candidate.getPointFeatures()[i]];
			}
			
			double total = (maxDistances[candidate.getPointFeatures()[i]] * (double) distances[i].length);
//			double p;
			if (total > 0.0)
				proportion += sum / total;
			else
//				proportion += maxDistances[candidate.getPointFeatures()[i]];
				proportion += 1.0;
			
//			proportion += 1.0 - p; // inverse for similarity
			
//			splitPoints[i] = sum / total;
			splitPoints[i] = 0.0; //maxDistances[candidate.getPointFeatures()[i]] * TAU; //sum / total;
			
		}
		
		proportion = proportion / (double) distances.length;
		
		if (covered != null && proportion >= TAU) {
			RealMatrix rm = new Array2DRowRealMatrix(distances);
			for (int j = 0; j < distances[0].length; j++) {
				
				if (isCovered(rm.getColumn(j), splitPoints)) {
					covered.add(this.trajectories.get(j));
				} 
	//			else {
	//				coveredOutClass.add(this.trajectories.get(j));
	//			}
				
			}
		}
			
		return new Pair<Double, List<MAT<MO>>>(proportion, covered);
	}

}