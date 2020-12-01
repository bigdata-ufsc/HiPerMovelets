package br.com.tarlis.mov3lets.method.structures.descriptor;

import java.util.List;

/**
 * The Class LoaderDescriptor.
 */
public class LoaderDescriptor {

	/** The train. */
	private List<String> train = null;
	
	/** The test. */
	private List<String> test = null;
	
	/** The format. */
	private String format = null; // Cached ZIP: CZIP (default) | Comma separated: CSV
	
	/** The loader. */
	private String loader = null; // default or null: interning | indexed
	
	/**
	 * Gets the train.
	 *
	 * @return the train
	 */
	public List<String> getTrain() {
		return train;
	}
	
	/**
	 * Sets the train.
	 *
	 * @param train the new train
	 */
	public void setTrain(List<String> train) {
		this.train = train;
	}
	
	/**
	 * Gets the test.
	 *
	 * @return the test
	 */
	public List<String> getTest() {
		return test;
	}
	
	/**
	 * Sets the test.
	 *
	 * @param test the new test
	 */
	public void setTest(List<String> test) {
		this.test = test;
	}
	
	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * Sets the format.
	 *
	 * @param format the new format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * Gets the loader.
	 *
	 * @return the loader
	 */
	public String getLoader() {
		return loader;
	}
	
	/**
	 * Sets the loader.
	 *
	 * @param loader the new loader
	 */
	public void setLoader(String loader) {
		this.loader = loader;
	}
	
}