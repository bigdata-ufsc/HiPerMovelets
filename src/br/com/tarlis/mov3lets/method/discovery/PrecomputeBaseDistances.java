package br.com.tarlis.mov3lets.method.discovery;

import java.util.List;
import java.util.concurrent.Callable;

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;
import br.com.tarlis.mov3lets.utils.ProgressBar;

/**
 * The Class PrecomputeBaseDistances.
 *
 * @param <MO> the generic type
 */
public class PrecomputeBaseDistances<MO> implements Callable<Integer> {
	
	/** The from index. */
	private int fromIndex;
	
	/** The trajectories. */
	private List<MAT<MO>> trajectories;
	
	/** The base. */
	private double[][][][][] base;
	
	/** The descriptor. */
	private Descriptor descriptor;
	
	/** The bar. */
	private ProgressBar bar;
	
	/**
	 * Instantiates a new precompute base distances.
	 *
	 * @param fromIndex the from index
	 * @param trajectories the trajectories
	 * @param base the base
	 * @param descriptor the descriptor
	 * @param bar the bar
	 */
	public PrecomputeBaseDistances(int fromIndex, List<MAT<MO>> trajectories, double[][][][][] base, Descriptor descriptor, ProgressBar bar) {
		this.fromIndex = fromIndex;
		this.trajectories = trajectories;
//		this.base2 = base;
		this.descriptor = descriptor;
		this.bar = bar;
		this.base = base;
	}

	/**
	 * Overridden method. 
	 * @see java.util.concurrent.Callable#call().
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public Integer call() throws Exception {
		computeBaseDistances(fromIndex, this.trajectories);
		return 0;
	}
	
	/**
	 * Compute base distances.
	 *
	 * @param idxFrom the idx from
	 * @param trajectories the trajectories
	 */
	public void computeBaseDistances(int idxFrom, List<MAT<MO>> trajectories){
		MAT<MO> trajectory = trajectories.get(idxFrom);
		int n = trajectory.getPoints().size();
		int size = 1;
		
		base[idxFrom] = new double[(n - size)+1][][][];	
		
		for (int start = 0; start <= (n - size); start++) {
			
			base[idxFrom][start] = new double[trajectories.size() - idxFrom][][];				
			
			for (int i = idxFrom; i < trajectories.size(); i++) {
				
				MAT<?> T = trajectories.get(i);
				Point a = trajectory.getPoints().get(start);
								
				base[idxFrom][start][i - idxFrom] = new double[(T.getPoints().size()-size)+1][this.descriptor.getAttributes().size()];
						
				for (int j = 0; j <= (T.getPoints().size()-size); j++) {
					Point b = T.getPoints().get(j);
										
					for (int k = 0; k < this.descriptor.getAttributes().size(); k++) {
						AttributeDescriptor attr = this.descriptor.getAttributes().get(k);
						// For each possible *Number Of Features* and each combination of those:
						base[idxFrom][start][i - idxFrom][j][k] = this.descriptor.getAttributes().get(k)
								.getDistanceComparator().calculateDistance(
								a.getAspects().get(k), 
								b.getAspects().get(k), 
								attr); // This also enhance distances
					}
					
				} // for (int j = 0; j <= (train.size()-size); j++)
				
			} //for (int i = 0; i < train.size(); i++)
			
			bar.plus();
			
		} // for (int start = 0; start <= (n - size); start++)

//		return base;
	}
	
//	private double[][][][] computeBaseDistances1(MAT<?> trajectory, List<MAT<MO>> trajectories){
//		int n = trajectory.getPoints().size();
//		int size = 1;
//		
//		double[][][][] base = new double[(n - size)+1][][][];		
//		
//		for (int start = 0; start <= (n - size); start++) {
//			
//			base[start] = new double[trajectories.size()][][];				
//			
//			for (int i = 0; i < trajectories.size(); i++) {
//				
//				MAT<?> T = trajectories.get(i);
//				Point a = trajectory.getPoints().get(start);
//								
//				base[start][i] = new double[this.descriptor.getAttributes().size()][(trajectories.get(i).getPoints().size()-size)+1];
//						
//				for (int j = 0; j <= (T.getPoints().size()-size); j++) {
//					Point b = T.getPoints().get(j);
//					
//
//					for (int k = 0; k < this.descriptor.getAttributes().size(); k++) {
//						AttributeDescriptor attr = this.descriptor.getAttributes().get(k);						
//						double distance = attr.getDistanceComparator().calculateDistance(
//								a.getAspects().get(k), 
//								b.getAspects().get(k), 
//								attr);
//					
//						base[start][i][k][j] = (distance != MAX_VALUE) ? (distance) : MAX_VALUE;					
//					
//					} // for (int k = 0; k < distance.length; k++)
//					
//				} // for (int j = 0; j <= (train.size()-size); j++)
//				
//			} //for (int i = 0; i < train.size(); i++)
//			
//		} // for (int start = 0; start <= (n - size); start++)
//
//		return base;
//	}
//	
//	private void computeBaseDistances2(MAT<?> trajectory, List<MAT<MO>> trajectories){
//		int n = trajectory.getPoints().size();
//		int size = 1;
//		
////		double[][][][] base = new double[(n - size)+1][][][];	
//		base2 = new Matrix3D(descriptor.getFlag("explore_dimensions"),
//				descriptor.numberOfFeatures(), 
//				descriptor.getParamAsInt("max_number_of_features"));
//		
//		for (int start = 0; start <= (n - size); start++) {
//			
////			base[start] = new double[trajectories.size()][][];				
//			
//			for (int i = fromIndex; i < trajectories.size(); i++) {
//				
//				MAT<?> T = trajectories.get(i);
//				Point a = trajectory.getPoints().get(start);
//								
////				base[start][i] = new double[this.descriptor.getAttributes().size()][(trajectories.get(i).getPoints().size()-size)+1];
//						
//				for (int j = 0; j <= (T.getPoints().size()-size); j++) {
//					Point b = T.getPoints().get(j);
//					
//					double[] distances = new double[this.descriptor.getAttributes().size()];
//					
//					for (int k = 0; k < this.descriptor.getAttributes().size(); k++) {
//						AttributeDescriptor attr = this.descriptor.getAttributes().get(k);
//						distances[k] = this.descriptor.getAttributes().get(k)
//								.getDistanceComparator().calculateDistance(
//								a.getAspects().get(k), 
//								b.getAspects().get(k), 
//								attr); // This also enhance distances
//					}
//
//					// For each possible *Number Of Features* and each combination of those:
//					base2.addDistances(a, b, distances);
//					
//				} // for (int j = 0; j <= (train.size()-size); j++)
//				
//			} //for (int i = 0; i < train.size(); i++)
//			
//			bar.plus();
//			
//		} // for (int start = 0; start <= (n - size); start++)
//
////		return base;
//	}
	
//	public Matrix3D computeBaseDistances(int fromIndex, List<MAT<MO>> trajectories){
//		MAT<?> trajectory = trajectories.get(fromIndex);
////		int n = trajectory.getPoints().size();
////		int size = 1;
//		
//		for (Point a : trajectory.getPoints()) {
////		for (int start = 0; start <= (n - size); start++) {		
////			Point a = trajectory.getPoints().get(start);
//			
//			for (int k = fromIndex; k < trajectories.size(); k++) {
//				MAT<?> T = trajectories.get(k);
//				
//				for (Point b : T.getPoints()) {
////				for (int j = 0; j <= (T.getPoints().size()-size); j++) {
////					Point b = T.getPoints().get(j);
//					
//					double[] distances = new double[this.descriptor.getAttributes().size()];
//					
//					for (int i = 0; i < this.descriptor.getAttributes().size(); i++) {
//						AttributeDescriptor attr = this.descriptor.getAttributes().get(i);
//						distances[i] = this.descriptor.getAttributes().get(i)
//								.getDistanceComparator().calculateDistance(
//								a.getAspects().get(i), 
//								b.getAspects().get(i), 
//								attr); // This also enhance distances
//					}
//
//					// For each possible *Number Of Features* and each combination of those:
//					base.addDistances(a, b, distances);
//					
//				} // for (int j = 0; j <= (train.size()-size); j++)
//				
//			} //for (MAT<?> T : trajectories) { --//-- for (int i = 0; i < train.size(); i++)
//			
//			bar.plus();
//			
//		} // for (int start = 0; start <= (n - size); start++)
//
//		return base;
//	}
	
}
