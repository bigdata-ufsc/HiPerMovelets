package br.com.tarlis.mov3lets.method.qualitymeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;

import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class ProportionQualityMeasure.
 *
 * @param <MO> the generic type
 */
public class ProportionQualityMeasure<MO> extends QualityMeasure<MO> {

	/** The tau. */
	protected double TAU = 0.5;
	
	/**
	 * Instantiates a new proportion quality measure.
	 *
	 * @param trajectories the trajectories
	 * @param tau the tau
	 */
	public ProportionQualityMeasure(List<MAT<MO>> trajectories, double tau) {
		super(trajectories, 1, 1.0, "");
		this.TAU = tau;
	}

	/**
	 * Instantiates a new proportion quality measure.
	 *
	 * @param trajectories the trajectories
	 * @param samples the samples
	 * @param sampleSize the sample size
	 * @param medium the medium
	 */
	public ProportionQualityMeasure(List<MAT<MO>> trajectories, int samples, double sampleSize, String medium) {
		super(trajectories, samples, sampleSize, medium);
		this.trajectories = trajectories;
	}

	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure#assesQuality(br.com.tarlis.mov3lets.model.Subtrajectory, java.util.Random).
	 * 
	 * @param candidate
	 * @param random
	 */
	@Override
	public void assesQuality(Subtrajectory candidate, Random random) {

		int outTotal = (int) this.trajectories.stream().filter(e -> !candidate.getTrajectory().getMovingObject().equals(e.getMovingObject())).count();
		if (outTotal == 0) {
			assesClassQuality(candidate, getMaxDistances(candidate.getDistances()), random);
			return;
		}
		
		int inTotal  = (int) this.trajectories.stream().filter(e ->  candidate.getTrajectory().getMovingObject().equals(e.getMovingObject())).count();
		
		assesQuality(candidate, random, inTotal, outTotal);
		
	}
	
	/**
	 * Count covered.
	 *
	 * @param targetDistances the target distances
	 * @param limits the limits
	 * @return the int
	 */
	public int countCovered(double[][] targetDistances, double[] limits){
		
		int count = 0;
		
		for (int i = 0; i < targetDistances[0].length; i++) {
			
			for (int j = 0; j < limits.length; j++) {
				if (targetDistances[j][i] <= limits[j])
					count++;
			}
			
		}
		
		return count;
	}
	
	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure#isCovered(double[], double[]).
	 * 
	 * @param point
	 * @param limits
	 * @return
	 */
	public boolean isCovered(double[] point, double[] limits){
		
		for (int i = 0; i < limits.length; i++) {
			if (point[i] > (limits[i]))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the splitpoints.
	 *
	 * @param candidate the candidate
	 * @param distances the distances
	 * @param target the target
	 * @param random the random
	 * @return the splitpoints
	 */
	public Map<String,double[]> getSplitpoints(Subtrajectory candidate, double[][] distances, MO target, Random random) {
		
		List<Pair<double[],double[]>> results = new ArrayList<>();		
		Pair<double[][],List<MO>> chosePoints = null;
		
		for (int i = 0; i < this.samples; i++) {			
			
			//chosePoints = choosePoints(distances, labels, random);
			chosePoints = choosePointsStratified(distances, this.labels, target, random);
			
			int split = chosePoints.getSecond().lastIndexOf(target);
			RealMatrix rm = new Array2DRowRealMatrix(chosePoints.getFirst());
			
			/* Select only the distance of the target label
			 * */
			double[][] targetDistances = rm.getSubMatrix(0, distances.length-1, 0, split).getData(); //new double[distances.length][split+1];
			/* Select only the distance of the non-target label
			 * */
			double[][] nonTargetDistances = rm.getSubMatrix(0, distances.length-1, split+1, chosePoints.getSecond().size()-1).getData(); // new double[distances.length][chosePoints.getSecond().size()-(split+1)];
			
			double[] limits = new double[distances.length];
			for (int j = 0; j < distances.length; j++) {
				DescriptiveStatistics ds = new DescriptiveStatistics(targetDistances[j]);
				limits[j] = ds.getMean();
//				limits[j] = ds.apply(new Percentile(0.5));
			}

			double matchTarget 		= countCovered(targetDistances,    limits);
			double matchNonTarget 	= countCovered(nonTargetDistances, limits);
				
//			/* Step 3: Choose the best rectangle
//			 * */
			double proportionTarget 	= matchTarget 	 / ((double) (targetDistances[0].length * distances.length));
			double proportionNonTarget 	= matchNonTarget / ((double) (nonTargetDistances[0].length * distances.length));
//			double p = proportionTarget; 					   // D
//			double p = 1.0 - proportionNonTarget; 			   // I
			double p = proportionTarget / proportionNonTarget; // PT
//			double p = proportionNonTarget / proportionTarget; // PNT
			
			results.add(new Pair<double[],double[]>(new double[]{p, proportionTarget, proportionNonTarget}, limits));
		}	
		
		int split = chosePoints.getSecond().lastIndexOf(target);
		RealMatrix rm = new Array2DRowRealMatrix(chosePoints.getFirst());
		
		/* Select only the distance of the target label
		 * */
		double[][] targetDistances = rm.getSubMatrix(0, distances.length-1, 0, split).getData(); //new double[distances.length][split+1];
		/* Select only the distance of the non-target label
		 * */
//		double[][] nonTargetDistances = rm.getSubMatrix(0, distances.length-1, split+1, chosePoints.getSecond().size()-1).getData(); // new double[distances.length][chosePoints.getSecond().size()-(split+1)];
		
		double[] limits = new double[distances.length];
		for (int j = 0; j < distances.length; j++) {
			DescriptiveStatistics ds = new DescriptiveStatistics(targetDistances[j]);
//			limits[j] = ds.getMean();
			limits[j] = ds.getPercentile(25.0);
		}

//		double matchTarget 		= countCovered(targetDistances,    limits);
//		double matchNonTarget 	= countCovered(nonTargetDistances, limits);
			
//		/* Step 3: Choose the best rectangle
//		 * */
//		double proportionTarget 	= matchTarget 	 / ((double) (targetDistances[0].length * distances.length));
//		double proportionNonTarget 	= matchNonTarget / ((double) (nonTargetDistances[0].length * distances.length));
//		double p = proportionTarget / proportionNonTarget;
//		double p = proportionNonTarget / proportionTarget;
//		double p = 1.0 - proportionNonTarget;
		
		// Agora resultado vai ser usado para acumular os resultados parciais
		double[][] splitPoints = new double[distances.length][this.samples];
		
		double proportions[] = new double[] {0.0, 0.0, 0.0};
		
		for (int i = 0; i < this.samples; i++) {
			
			for (int j = 0; j < results.get(i).getSecond().length; j++) {
				splitPoints[j][i] += results.get(i).getSecond()[j];
			}			
			
			proportions[i] += results.get(i).getFirst()[i];
			
		}
		
		for (int i = 0; i < this.samples; i++) {
			proportions[i] = proportions[i] / (double) this.samples;
		}

		double[] splitPointsMean = new double[distances.length];		
		
		for (int i = 0; i < distances.length; i++) {
			DescriptiveStatistics ds = new DescriptiveStatistics(splitPoints[i]);
			splitPointsMean[i] = ds.getMean();
		}
		
		Map <String,double[]> splitpointsData = new HashMap<>();
		splitpointsData.put("mean", 		splitPointsMean);		
		splitpointsData.put("proportions", 	proportions);
		
		return splitpointsData;
	}
	
	/**
	 * Asses quality.
	 *
	 * @param candidate the candidate
	 * @param random the random
	 * @param inTotal the in total
	 * @param outTotal the out total
	 */
	protected void assesQuality(Subtrajectory candidate, Random random, int inTotal, int outTotal) {
		
		/*
		 * STEP 1: VERIFY WHICH ARE THE TRAJECTORIES THAT CONTAIN THAT CANDIDATE FOR EACH DIMENSION
		 */
		
		MO target = (MO) candidate.getTrajectory().getMovingObject();
		double[][] distances = candidate.getDistances();	
		
		Map<String,double[]> splitpointsData = getSplitpoints(candidate, distances, target, random);	
		double[] maxDistances = getMaxDistances(distances);
		
		List<MAT<MO>> coveredInClass = new ArrayList<MAT<MO>>();
//		List<MAT<MO>> coveredOutClass = new ArrayList<MAT<MO>>();
		
		/*
		 * STEP 2: Covered Trajectories
		 */
		RealMatrix rm = new Array2DRowRealMatrix(distances);
		double[] splitPoints = splitpointsData.get("mean");	
		for (int j = 0; j < distances[0].length; j++) {
			
			MAT<MO> T = this.trajectories.get(j);
			if (target.equals(T.getMovingObject()) && isCovered(rm.getColumn(j), splitPoints)) {
				coveredInClass.add(this.trajectories.get(j));
			} 
//			else {
//				coveredOutClass.add(this.trajectories.get(j));
//			}
			
		}
				
		Map<String, Double> data = new HashMap<>();

    	data.put("quality", 	splitpointsData.get("proportions")[0]);
    	data.put("p_target", 	splitpointsData.get("proportions")[1]);
    	data.put("p_nontarget", splitpointsData.get("proportions")[2]);
    	data.put("dimensions", 	1.0 * candidate.getPointFeatures().length );    	
    	data.put("size", 		1.0 * candidate.getSize() );
    	data.put("start", 		1.0 * candidate.getStart() );
    	data.put("tid", 		1.0 * candidate.getTrajectory().getTid() );
    	
    	ProportionQuality quality = new ProportionQuality();
    	quality.setData(data);	    
//    	quality.setCoveredInClass((List) coveredInClass);	
		candidate.setQuality(quality);
		candidate.setSplitpoints(splitpointsData.get("mean"));
		candidate.setSplitpointData(splitpointsData);
		candidate.setMaxDistances(maxDistances);
	}
	
	/**
	 * Asses class quality.
	 *
	 * @param candidate the candidate
	 * @param maxDistances the max distances
	 * @param random the random
	 */
	public void assesClassQuality(Subtrajectory candidate, double[] maxDistances, Random random) {
		/*
		 * STEP 1: VERIFY WHICH ARE THE TRAJECTORIES THAT CONTAIN THAT CANDIDATE FOR EACH DIMENSION
		 */
		List<MAT<MO>> coveredInClass = new ArrayList<MAT<MO>>();
		
		double[][] distances = candidate.getDistances();
//		double[] maxValues = getMaxDistances(distances);//new double[candidate.getDistances().length];	
		double[] splitPoints = new double[distances.length];
		
		double proportion  = 0.0;
		
		for (int i = 0; i < distances.length; i++) {
//			splitPoints[i] = maxDistances[candidate.getPointFeatures()[i]] * GAMMA;
			double sum = 0.0;
			
			for (int j = 0; j < distances[i].length; j++) {
//				if (distances[i][j] <= splitPoints[i])
				if (distances[i][j] != MAX_VALUE)
					sum += maxDistances[candidate.getPointFeatures()[i]] - distances[i][j];
//					sum += distances[i][j];
				else
					sum += maxDistances[candidate.getPointFeatures()[i]];
			}
			
			double total = (maxDistances[candidate.getPointFeatures()[i]] * (double) distances[i].length);
//			double p;
			if (total > 0.0)
				proportion += sum / total;
//			else
////				proportion += maxDistances[candidate.getPointFeatures()[i]];
//				proportion += 0.0;
			
//			proportion += 1.0 - p; // inverse for similarity
			
			splitPoints[i] = 0.0; //maxDistances[candidate.getPointFeatures()[i]] * TAU; //sum / total;
			
		}
		
		proportion 		= proportion / (double) distances.length;
		
		if (proportion >= TAU) {
			RealMatrix rm = new Array2DRowRealMatrix(distances);
			for (int j = 0; j < distances[0].length; j++) {
				
				if (isCovered(rm.getColumn(j), splitPoints)) {
					coveredInClass.add(this.trajectories.get(j));
				} 
	//			else {
	//				coveredOutClass.add(this.trajectories.get(j));
	//			}
				
			}
		}
		
		Map<String, Double> data = new HashMap<>();
		
    	data.put("quality", proportion);
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

}