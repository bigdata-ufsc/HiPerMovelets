/**
 * 
 */
package br.com.tarlis.mov3lets.method.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.tarlis.mov3lets.method.output.json.SubtrajectoryGSON;
import br.com.tarlis.mov3lets.method.output.json.TOGSON;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class JSONOutputter.
 *
 * @author tarlis
 * @param <MO> the generic type
 */
public class JSONOutputter<MO> extends OutputterAdapter<MO> {

	/**
	 * Instantiates a new JSON outputter.
	 *
	 * @param filePath the file path
	 * @param descriptor the descriptor
	 * @param subfolderClasses the subfolder classes
	 */
	public JSONOutputter(String filePath, Descriptor descriptor, boolean subfolderClasses) {
		super(filePath, descriptor, subfolderClasses);
	}
	
	/**
	 * Instantiates a new JSON outputter.
	 *
	 * @param descriptor the descriptor
	 */
	public JSONOutputter(Descriptor descriptor) {
		super(descriptor);
	}

	/**
	 * Instantiates a new JSON outputter.
	 *
	 * @param resultDirPath the result dir path
	 * @param descriptor the descriptor
	 */
	public JSONOutputter(String resultDirPath, Descriptor descriptor) {
		super(resultDirPath, descriptor, true);
	}

	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.output.OutputterAdapter#write(java.lang.String, java.util.List, java.util.List, boolean).
	 * 
	 * @param filename
	 * @param trajectories
	 * @param movelets
	 * @param delayOutput
	 */
	@Override
	public void write(String filename, List<MAT<MO>> trajectories, List<Subtrajectory> movelets, boolean delayOutput) {
		
		if (delayOutput) return; // Do nothing
		
		if (movelets.isEmpty()) {
//			Mov3letsUtils.traceW("Empty movelets set [NOT OUTPUTTED]");
			return;
		}
		
		List<Map<String,Object>> classOfTrajectories = new ArrayList<>();
		
		for (MAT<MO> t : trajectories) {			
			Map<String, Object> classOfT = new HashMap<>();
			classOfT.put("tid", t.getTid());
			classOfT.put("label", t.getMovingObject());			
			classOfTrajectories.add(classOfT);
		}
				
		List <SubtrajectoryGSON> subtrajectoryToGSONs = new ArrayList<>();
		
		for (Subtrajectory movelet : movelets) {
//			subtrajectoryToGSONs.add(fromSubtrajectory(movelet));
			subtrajectoryToGSONs.add(new SubtrajectoryGSON(movelet, getDescriptor()));
		}
		
		TOGSON toGSON = new TOGSON(classOfTrajectories, subtrajectoryToGSONs);
		
		try {
			File file = getFile(movelets.get(0).getTrajectory().getMovingObject().toString(), 
					"moveletsOn"+ StringUtils.capitalize(filename) + ".json");
//			String f = getFilePath() + movelets.get(0).getTrajectory().getMovingObject() 
//					+ System.getProperty("file.separator") + "moveletsOn"+ StringUtils.capitalize(filename) + ".json";
//			File file = new File(f); 
			file.getParentFile().mkdirs(); // TODO remove gambia ?
			
			FileWriter fileWriter = new FileWriter(file);
			Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();
			
			gson.toJson(toGSON, fileWriter);
			fileWriter.close();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.getLocalizedMessage();
			e.printStackTrace();
		}  
	}
	
//	public SubtrajectoryGSON fromSubtrajectory(Subtrajectory s) {
//		
//		int[] list_features = s.getPointFeatures();
//		
//		List<HashMap<String, Object>> features = new ArrayList<>();
//		List<HashMap<String, Object>> used_features = new ArrayList<>();
//		
//		HashMap<String, Double> maxValues = new HashMap<String, Double>();
//		for(int j=0; j < getDescriptor().getAttributes().size(); j++) {
//			if(ArrayUtils.contains(list_features, j))
//				maxValues.put(getDescriptor().getAttributes().get(j).getText(), getDescriptor().getAttributes().get(j).getComparator().getMaxValue());
//		}
//		
//		for(int i=0; i<s.getPoints().size(); i++) {
//			
//			Point point = s.getPoints().get(i);
//
//			HashMap<String, Object> features_in_point = new HashMap<>();
//			HashMap<String, Object> used_features_in_point = new HashMap<>();
//			
//			for(int j=0; j < getDescriptor().getAttributes().size(); j++) {
//				features_in_point.put(getDescriptor().getAttributes().get(j).getText(), point.getAspects().get(j).getValue());				
//				
//				if(ArrayUtils.contains(list_features, j))
//					used_features_in_point.put(getDescriptor().getAttributes().get(j).getText(), point.getAspects().get(j).getValue());
//			}
//
//			features.add(features_in_point);
//			used_features.add(used_features_in_point);
//		}
//
//		if (s.getQuality() != null)		
//			return new SubtrajectoryGSON(s.getStart(), s.getEnd(), s.getTrajectory().getTid(), 
//					s.getTrajectory().getMovingObject().toString(), features, s.getPointFeatures(), 
//					maxValues, s.getSplitpoints(), s.getDistances(), s.getBestAlignments(),
//					s.getQuality(), s.getPoints(), used_features);
//		else 
//			return new SubtrajectoryGSON(s.getStart(), s.getEnd(), s.getTrajectory().getTid(), 
//					s.getTrajectory().getMovingObject().toString(), features, s.getPointFeatures(), 
//					maxValues, s.getSplitpoints(), s.getDistances(), s.getBestAlignments(),
//					s.getProportionInClass(), s.getPoints(), used_features);
//	}

}
