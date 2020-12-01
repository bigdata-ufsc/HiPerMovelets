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

import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Subtrajectory;

/**
 * The Class SimpleRankOuputter.
 *
 * @author tarlis
 * @param <MO> the generic type
 */
public class SimpleRankOuputter<MO> extends OutputterAdapter<MO> {

	/**
	 * Instantiates a new simple rank ouputter.
	 *
	 * @param filePath the file path
	 * @param descriptor the descriptor
	 */
	public SimpleRankOuputter(String filePath, Descriptor descriptor) {
		super(filePath, descriptor, false);
		writeColumns();
	}

	/**
	 * Append CSV.
	 *
	 * @param resultDirPath the result dir path
	 * @param rank the rank
	 */
	private void appendCSV(String resultDirPath, List<HashMap<String, String>> rank) {
		File file= new File (resultDirPath);
		FileWriter writer;
		try {
			writer = new FileWriter(file, true); //True = Append to file, false = Overwrite
			// Write CSV
			for (int i = 0; i < rank.size(); i++) { 
				writer.write(rank.get(i).get("rank"));
				writer.write(",");
				writer.write(rank.get(i).get("tid").toString());
				writer.write(",");
				writer.write(rank.get(i).get("size").toString());
				writer.write(",");
				writer.write(rank.get(i).get("start").toString());
				writer.write(",");
				writer.write(rank.get(i).get("end").toString());
				writer.write(",");
				writer.write(rank.get(i).get("quality").toString());
				writer.write(",");
				writer.write(rank.get(i).get("proportion").toString());
				writer.write(",");
				writer.write(rank.get(i).get("class").toString());
				writer.write("\r\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write columns.
	 */
	public void writeColumns() {
		File file= new File (getFilePath() + "rank_t.csv");
		FileWriter writer;
		try {
			writer = new FileWriter(file, true); //True = Append to file, false = Overwrite
			// Write CSV
			writer.write("#,tid,size,start,end,quality,proportion,class\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
//		System.out.println("\t==> Output Rank of Movelets:");
		
		if (delayOutput) return; // Do nothing
		
		List<HashMap<String, String>> rank = new ArrayList<HashMap<String,String>>();
//		List<HashMap<String, String>> rankALL = new ArrayList<HashMap<String,String>>();
		
		List<Integer> trajs = new ArrayList<Integer>();
		for (int i = 0; i < movelets.size(); i++) {
			Subtrajectory m = movelets.get(i);
			
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("rank", "-");
			item.put("tid", 		m.getTrajectory().getTid() + "");
			item.put("class",		m.getTrajectory().getMovingObject().toString());
			item.put("size", 		m.getSize() + "");
			item.put("start", 		m.getStart()+"");
			item.put("end", 		m.getEnd()+"");
			item.put("quality", 	m.getQuality().toString());
//			item.put("proportion", 	m.getProportionInClass() + "");
//			System.out.print("\t\t["+(i+1)+"ª]: "
//					+ item.toString());
			if (!trajs.contains(m.getTrajectory().getTid())) {
				trajs.add(m.getTrajectory().getTid());
				item.put("rank", 	trajs.size() + "");
				rank.add(item);
//				System.out.print(" => RANK: " + trajs.size());
			}
//			System.out.println();
//			rankALL.add(item);
		}
		
		appendCSV(getFilePath() + "rank_t.csv", rank); 
//		appendCSV(MoveletsRunUnit_Supervised.RESULT_DIR + "rank_m.csv", rankALL); 
	}

}
