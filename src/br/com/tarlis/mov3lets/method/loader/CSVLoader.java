package br.com.tarlis.mov3lets.method.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;

/**
 * The Class CSVLoader.
 *
 * @param <T> the generic type
 */
public class CSVLoader<T extends MAT<?>> implements LoaderAdapter<T> {

	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.loader.LoaderAdapter#loadTrajectories(java.lang.String, br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor).
	 * 
	 * @param file
	 * @param descriptor
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<T> loadTrajectories(String file, Descriptor descriptor) throws IOException {
		
		List<MAT<String>> trajectories = new ArrayList<MAT<String>>();
		// IF MO type is String:
//		MO mo = new MO();
		MAT<String> mat = new MAT<String>();

		file += ".csv";
		CSVParser csvParser = CSVFormat.DEFAULT.parse(new InputStreamReader((new FileInputStream(file))));
		csvParser.iterator().next();
		for (CSVRecord line : csvParser) {
			int tid = Integer.parseInt(line.get(descriptor.getIdFeature().getOrder()-1));
			
			if (mat.getTid() != tid) {
				mat = new MAT<String>();
				mat.setTid(tid);
				trajectories.add(mat);

				// Can use like this:
//				mo = (MO) new MovingObject<String>(label);
				// OR -- this for typing String:
				String label = line.get(descriptor.getLabelFeature().getOrder()-1);
				mat.setMovingObject(label);
			}
			
			// For each attribute of POI
			Point poi = new Point();	
			poi.setTrajectory(mat);
			for (AttributeDescriptor attr : descriptor.getAttributes()) {
//				poi.getAspects().put(attr.getText(), instantiateAspect(attr, line.get(attr.getOrder()-1)));
				if (attr.getType().startsWith("composite_")) {
					String value = line.get(attr.getOrder()-1) + " " + line.get(attr.getOrder());
					poi.getAspects().add(instantiateAspect(attr, value));
				} else 
					poi.getAspects().add(instantiateAspect(attr, line.get(attr.getOrder()-1)));
			}
			mat.getPoints().add(poi);
		}
		csvParser.close();

		return (List<T>) trajectories;
	}

}
