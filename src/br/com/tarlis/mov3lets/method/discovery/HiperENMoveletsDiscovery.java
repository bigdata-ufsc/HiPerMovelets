/**
 * 
 */
package br.com.tarlis.mov3lets.method.discovery;

import java.util.Comparator;
import java.util.List;

import br.com.tarlis.mov3lets.method.qualitymeasure.QualityMeasure;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class HiperENMoveletsDiscovery.
 *
 * @author tarlis
 * @param <MO> the generic type
 */
public class HiperENMoveletsDiscovery<MO> extends HiperCEMoveletsDiscovery<MO> {

	/** The sample trajectories. */
	protected List<MAT<MO>> sampleTrajectories;

	/**
	 * Instantiates a new hiper EN movelets discovery.
	 *
	 * @param trajsFromClass the trajs from class
	 * @param data the data
	 * @param train the train
	 * @param test the test
	 * @param qualityMeasure the quality measure
	 * @param descriptor the descriptor
	 */
	public HiperENMoveletsDiscovery(List<MAT<MO>> trajsFromClass, List<MAT<MO>> data, List<MAT<MO>> train, List<MAT<MO>> test,
			QualityMeasure qualityMeasure, Descriptor descriptor) {
		super(trajsFromClass, data, train, test, qualityMeasure, descriptor);
	}

	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.discovery.SuperMoveletsDiscovery#orderCandidates(java.util.List).
	 * 
	 * @param candidatesByProp
	 */
	public void orderCandidates(List<Subtrajectory> candidatesByProp) {
		/* STEP 2.1.3: SORT THE CANDIDATES BY PROPORTION VALUE
		 * * * * * * * * * * * * * * * * * * * * * * * * * */
		candidatesByProp.sort(new Comparator<Subtrajectory>() {
			@Override
			public int compare(Subtrajectory o1, Subtrajectory o2) {
				
//				return (-1) * o1.getQuality().compareTo(o2.getQuality());
				return (-1) * Double.compare(candidateQuality(o1), candidateQuality(o2));				
				
			}
		});
	}
	
//	/**
//	 * Overridden method. 
//	 * @see br.com.tarlis.mov3lets.method.discovery.SuperMoveletsDiscovery#filterByProportion(java.util.List, java.util.Random).
//	 * 
//	 * @param candidatesByProp
//	 * @param random
//	 * @return
//	 */
//	public List<Subtrajectory> filterByProportion(List<Subtrajectory> candidatesByProp, Random random) {
//		calculateProportion(candidatesByProp, random);
////		candidatesByProp = filterEqualCandidates(candidatesByProp);
//
//		/* STEP 2.1.2: SELECT ONLY CANDIDATES WITH PROPORTION > 50%
//		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
//		List<Subtrajectory> orderedCandidates = new ArrayList<>();
//		for(Subtrajectory candidate : candidatesByProp)
//			if(entrophy(candidate) >= TAU)
//				orderedCandidates.add(candidate);
//			else 
//				bucket.add(candidate);
//
//		return orderedCandidates;
//	}

	/**
	 * Entrophy.
	 *
	 * @param candidate the candidate
	 * @return the quality
	 */
	protected double candidateQuality(Subtrajectory candidate) {
		double p = candidate.getQuality().getData().get("p_target") / candidate.getQuality().getData().get("p_nontarget");
		return -p * (Math.log(p));
//		return -Math.log(candidate.getQuality().getData().get("p_target") / candidate.getQuality().getData().get("p_nontarget"));
	}

}
