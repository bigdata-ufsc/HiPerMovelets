package br.com.tarlis.mov3lets.method.output.json;

import java.util.List;
import java.util.Map;

/**
 * The Class TOGSON.
 */
public class TOGSON {
	
	/** The classes. */
	private List<Map<String,Object>> classes;
	
	/** The movelets. */
	private List <SubtrajectoryGSON> movelets;
	
	/**
	 * Instantiates a new togson.
	 */
	public TOGSON() {
	}

	/**
	 * Instantiates a new togson.
	 *
	 * @param classes the classes
	 * @param movelets the movelets
	 */
	public TOGSON(List<Map<String, Object>> classes, List<SubtrajectoryGSON> movelets) {
		super();
		this.classes = classes;
		this.movelets = movelets;
	}

	/**
	 * Sets the classes.
	 *
	 * @param classes the classes
	 */
	public void setClasses(List<Map<String, Object>> classes) {
		this.classes = classes;
	}
	
	/**
	 * Sets the shapelets.
	 *
	 * @param movelets the new shapelets
	 */
	public void setShapelets(List<SubtrajectoryGSON> movelets) {
		this.movelets = movelets;
	}
	
	/**
	 * Gets the shapelets.
	 *
	 * @return the shapelets
	 */
	public List<SubtrajectoryGSON> getShapelets() {
		return movelets;
	}
	
	
	/**
	 * Gets the classes.
	 *
	 * @return the classes
	 */
	public List<Map<String, Object>> getClasses() {
		return classes;
	}
}
