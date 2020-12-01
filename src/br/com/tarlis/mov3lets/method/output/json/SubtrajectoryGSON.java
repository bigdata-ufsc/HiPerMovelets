package br.com.tarlis.mov3lets.method.output.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import br.com.tarlis.mov3lets.method.qualitymeasure.LeftSidePureQuality;
import br.com.tarlis.mov3lets.method.qualitymeasure.Quality;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;
import br.com.tarlis.mov3lets.model.Subtrajectory;


/**
 * The Class SubtrajectoryGSON.
 */
public class SubtrajectoryGSON {
	
	/** The start. */
	private int start;

	/** The end. */
	private int end;
 
	/** The trajectory. */
	private int trajectory;
	
	/** The label. */
	private String label;

//	private List<HashMap<String, Object>> features;
	
	/** The points with only the used features. */
private List<HashMap<String, Object>> points_with_only_the_used_features;
		
	/** The max values. */
	private HashMap<String, Double> maxValues;
	
	/** The point features. */
	private int[] pointFeatures;
	
	/** The splitpoints. */
	private double[] splitpoints;
	
	/** The distances. */
	private double[][] distances;
	
	/** The positions. */
	private int[] positions;
	
/** The data. */
//	private List<Point> data;
	private List<HashMap<String, Object>> data;
	
	/** The quality. */
	private Map<String,Double> quality;
	
	/**
	 * Instantiates a new subtrajectory GSON.
	 *
	 * @param start the start
	 * @param end the end
	 * @param trajectory the trajectory
	 * @param label the label
	 * @param features the features
	 * @param pointFeatures the point features
	 * @param maxValues the max values
	 * @param splitpoints the splitpoints
	 * @param distances the distances
	 * @param bestAlignments the best alignments
	 * @param quality the quality
	 * @param only_used_features the only used features
	 */
	public SubtrajectoryGSON(int start, int end, int trajectory, String label,
			List<HashMap<String, Object>> features, int[] pointFeatures, 
			HashMap<String, Double> maxValues, double[] splitpoints, double[][] distances, 
			List<Subtrajectory> bestAlignments, Quality quality, 
			List<HashMap<String, Object>> only_used_features) {
		super();
		init(start, end, trajectory, label, features, pointFeatures, maxValues, 
				splitpoints, distances, bestAlignments, quality, only_used_features);
	}

	/**
	 * Instantiates a new subtrajectory GSON.
	 *
	 * @param start the start
	 * @param end the end
	 * @param trajectory the trajectory
	 * @param label the label
	 * @param features the features
	 * @param pointFeatures the point features
	 * @param maxValues the max values
	 * @param splitpoints the splitpoints
	 * @param distances the distances
	 * @param bestAlignments the best alignments
	 * @param proportion the proportion
	 * @param data the data
	 * @param only_used_features the only used features
	 */
	public SubtrajectoryGSON(int start, int end, int trajectory, String label,
			List<HashMap<String, Object>> features, int[] pointFeatures, 
			HashMap<String, Double> maxValues, double[] splitpoints, double[][] distances, 
			List<Subtrajectory> bestAlignments, double proportion, 
			List<Point> data, List<HashMap<String, Object>> only_used_features) {
		super();
		init(start, end, trajectory, label, features, pointFeatures, maxValues, 
				splitpoints, distances, bestAlignments, null, only_used_features);
		this.quality = new HashMap<>();
		this.quality.put("proportion", proportion);
	
	}
	
	/**
	 * Instantiates a new subtrajectory GSON.
	 *
	 * @param start the start
	 * @param trajectory the trajectory
	 * @param label the label
	 * @param pointFeatures the point features
	 * @param splitpoints the splitpoints
	 * @param distances the distances
	 * @param bestAlignments the best alignments
	 * @param quality the quality
	 */
	public SubtrajectoryGSON(int start, int trajectory, String label, int[] pointFeatures, double [] splitpoints, 
			double[][] distances, List<Subtrajectory> bestAlignments, Quality quality) {
		super();
		this.start = start;
		this.trajectory = trajectory;
		this.label = label;	
		this.pointFeatures = pointFeatures;
		this.splitpoints = splitpoints;
		this.distances = distances;
		this.positions = bestAlignments.stream().mapToInt(e -> (e!=null) ? e.getStart() : -1).toArray();
		this.quality = quality.getData();
//		this.points_with_only_the_used_features = only_used_features;
	}
	
	/**
	 * Instantiates a new subtrajectory GSON.
	 *
	 * @param subtrajectory the subtrajectory
	 * @param descriptor the descriptor
	 */
	public SubtrajectoryGSON(Subtrajectory subtrajectory, Descriptor descriptor) {
		super();
		int[] list_features = subtrajectory.getPointFeatures();
		
		List<HashMap<String, Object>> features = new ArrayList<>();
		List<HashMap<String, Object>> used_features = new ArrayList<>();
		
		HashMap<String, Double> maxValues = new HashMap<String, Double>();
		for(int j=0; j < descriptor.getAttributes().size(); j++) {
			if(ArrayUtils.contains(list_features, j))
				maxValues.put(descriptor.getAttributes().get(j).getText(), descriptor.getAttributes().get(j).getComparator().getMaxValue());
		}
		
		for(int i=0; i < subtrajectory.getPoints().size(); i++) {
			
			Point point = subtrajectory.getPoints().get(i);

			HashMap<String, Object> features_in_point = new HashMap<>();
			HashMap<String, Object> used_features_in_point = new HashMap<>();
			
			for(int j=0; j < descriptor.getAttributes().size(); j++) {
				features_in_point.put(descriptor.getAttributes().get(j).getText(), point.getAspects().get(j).getValue());				
				
				if(ArrayUtils.contains(list_features, j))
					used_features_in_point.put(descriptor.getAttributes().get(j).getText(), point.getAspects().get(j).getValue());
			}

			features.add(features_in_point);
			used_features.add(used_features_in_point);
		}
		
		init(subtrajectory.getStart(), subtrajectory.getEnd(), subtrajectory.getTrajectory().getTid(), 
				subtrajectory.getTrajectory().getMovingObject().toString(), features, subtrajectory.getPointFeatures(), 
				maxValues, subtrajectory.getSplitpoints(), subtrajectory.getDistances(), subtrajectory.getBestAlignments(),
				subtrajectory.getQuality(), used_features);
	}
	
	/**
	 * Inits the.
	 *
	 * @param start the start
	 * @param end the end
	 * @param trajectory the trajectory
	 * @param label the label
	 * @param features the features
	 * @param pointFeatures the point features
	 * @param maxValues the max values
	 * @param splitpoints the splitpoints
	 * @param distances the distances
	 * @param bestAlignments the best alignments
	 * @param quality the quality
	 * @param only_used_features the only used features
	 */
	private void init(int start, int end, int trajectory, String label,
			List<HashMap<String, Object>> features, int[] pointFeatures, 
			HashMap<String, Double> maxValues, double[] splitpoints, double[][] distances, 
			List<Subtrajectory> bestAlignments, Quality quality, 
			List<HashMap<String, Object>> only_used_features) {
		this.start = start;
		this.end = end;
		this.trajectory = trajectory;
		this.label = label;
//		this.features = features;		
		this.distances = distances;
		this.positions = bestAlignments.stream().mapToInt(e -> (e!=null) ? e.getStart() : -1).toArray();
		this.pointFeatures = pointFeatures;
		this.splitpoints = splitpoints;
		this.quality = quality.getData();
		this.maxValues = maxValues;
		this.points_with_only_the_used_features = only_used_features;
		this.data = features;
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
	public int getTrajectory() {
		return trajectory;
	}

	/**
	 * Sets the trajectory.
	 *
	 * @param trajectory the new trajectory
	 */
	public void setTrajectory(int trajectory) {
		this.trajectory = trajectory;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public List<HashMap<String, Object>> getData() {
		return data;
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the data
	 */
	public void setData(List<HashMap<String, Object>> data) {
		this.data = data;
	}
	
	/**
	 * Sets the used features.
	 *
	 * @param data the data
	 */
	public void setUsedFeatures(List<HashMap<String, Object>> data) {
		this.points_with_only_the_used_features = data;
	}
	
	/**
	 * Gets the only used features.
	 *
	 * @return the only used features
	 */
	public List<HashMap<String, Object>> getOnlyUsedFeatures() {
		return points_with_only_the_used_features;
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

//	public List<HashMap<String, Object>> getFeatures() {
//		return features;
//	}
//
//	public void setFeatures(List<HashMap<String, Object>> features) {
//		this.features = features;
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

	/**
	 * Gets the quality.
	 *
	 * @return the quality
	 */
	public Map<String, Double> getQuality() {
		return quality;
	}
	
	/**
	 * Sets the quality.
	 *
	 * @param quality the quality
	 */
	public void setQuality(Map<String, Double> quality) {
		this.quality = quality;
	}
	
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
	 * To subtrajectory.
	 *
	 * @param trajectories the trajectories
	 * @return the subtrajectory
	 */
	public Subtrajectory toSubtrajectory(List<MAT<?>> trajectories){
		MAT<?> t = trajectories.stream().filter(e -> e.getTid() == this.trajectory).collect(Collectors.toList()).get(0);
		
		Subtrajectory s = new Subtrajectory(start, end, t, pointFeatures, distances[0].length);
		
		LeftSidePureQuality lspq = new LeftSidePureQuality();
		lspq.setData(quality);		
		s.setDistances(distances);		
		s.setQuality(lspq);
		s.setSplitpoints(splitpoints);		
		
		return s;
	}

}
