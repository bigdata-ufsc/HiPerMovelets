package br.com.tarlis.mov3lets.method.loader;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.aspect.Aspect;
import br.com.tarlis.mov3lets.model.aspect.Space2DAspect;
import br.com.tarlis.mov3lets.utils.Mov3letsUtils;

/**
 * Class LoaderAdapter.
 *
 * @author Tarlis Portela <tarlis [at] tarlis.com.br>
 * @param <T> the generic type
 * @created 2020-07-01
 */
public interface LoaderAdapter<T extends MAT<?>> {
	
	/**
	 * Mehod loadTrajectories. 
	 * LoaderAdapter: List<T>.
	 *
	 * @param file the file
	 * @param descriptor the descriptor
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract List<T> loadTrajectories(String file, Descriptor descriptor) throws IOException;
	
	/**
	 * Load.
	 *
	 * @param file the file
	 * @param descriptor the descriptor
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public default List<T> load(String file, Descriptor descriptor) throws IOException {
		String curpath = descriptor.hasParam("curpath")? descriptor.getParamAsText("curpath") : "./";
		
		List<T> data = loadTrajectories(Paths.get(curpath, file).toString(), descriptor);
		return data;
	}
	
	/** The date formatter. */
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Instantiate aspect.
	 *
	 * @param attr the attr
	 * @param value the value
	 * @return the aspect
	 */
	public default Aspect<?> instantiateAspect(AttributeDescriptor attr, String value) {

		switch (attr.getType()) {
			case "numeric":
				return new Aspect<Double>(Double.parseDouble(value));
			case "space2d":
			case "composite_space2d":
				return new Space2DAspect(value);
			case "time":
				return new Aspect<Integer>(Integer.parseInt(value));
			case "datetime":
				try {
//					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return new Aspect<Date>(dateFormatter.parse(value));
				} catch (ParseException e) {
					Mov3letsUtils.trace("\tAtribute datetime '"+value+"' in wrong format, must be yyyy-MM-dd HH:mm:ss");
					return new Aspect<Date>(new Date());
				}
			case "localdate":
				return new Aspect<LocalDate>(LocalDate.parse(value));
			case "localtime":
				return new Aspect<LocalTime>(LocalTime.parse(value));
			case "foursquarevenue":
			case "gowallacheckin":
			case "nominal":
			default:
				return new Aspect<String>(value);
		}
	}

}
