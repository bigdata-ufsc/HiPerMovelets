/**
 * Wizard - Multiple Aspect Trajectory (MASTER) Classification. 
 * Copyright (C) 2019  Tarlis Portela <tarlis@tarlis.com.br>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package br.com.tarlis.mov3lets.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

import br.com.tarlis.mov3lets.method.Mov3lets;
import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.utils.Mov3letsUtils;
import br.com.tarlis.mov3lets.utils.TableList;

/**
 * The Class Mov3letsRun.
 *
 * @author Tarlis Portela <tarlis@tarlis.com.br>
 */
public class Mov3letsRun {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// PARAMS:
		HashMap<String, Object> params = configure(args);

		// Config to - Show trace messages OR Ignore all
		if (params.containsKey("verbose") && (Boolean) params.get("verbose"))
			Mov3letsUtils.getInstance().configLogger();
		
		// Starting Date:
		Mov3letsUtils.trace(new Date().toString());
		
		if (!params.containsKey("descfile")) {
			showUsage(params, "-descfile\tDescription file must be set!");
			return;
		}
		String descFile = params.get("descfile").toString();
				
		// 2 - RUN
		Mov3lets<String> mov;
		try {
			mov = new Mov3lets<String>(descFile, params);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			showUsage(params, "-descfile\tDescription file not found: " + e.getMessage());
//			e.printStackTrace();
			return;
		}

//		mov.getDescriptor().setParams(params);
		mov.getDescriptor().setParam("result_dir_path", configRespath(descFile, mov.getDescriptor()));
		
		// STEP 1 - Input:
		Mov3letsUtils.getInstance().startTimer("[1] >> Load Input");
//				Mov3letsUtils.getInstance().printMemory();
		try {
			mov.loadTrain();
		} catch (IOException e) {
			showUsage(mov.getDescriptor().getParams(), "-curpath\tCould not load train dataset: " + e.getMessage());
//			e.printStackTrace();
			return;
		}
		
		try {
			// Try loading the test dataset
			mov.loadTest();
		} catch (IOException e) {
			// Empty if can't
			Mov3letsUtils.trace("Empty test dataset: "+ e.getMessage() +" [continue]");
			mov.setTest(new ArrayList<MAT<String>>());
		}
		
		if (mov.getTrain().isEmpty()) { 
			showUsage(mov.getDescriptor().getParams(), "-curpath\tEmpty training set!");
			return;
		}
				
		// Set Result Dir:
		mov.setResultDirPath(mov.getDescriptor().getParamAsText("result_dir_path"));
		Mov3letsUtils.trace(showConfiguration(mov.getDescriptor()));
		Mov3letsUtils.getInstance().stopTimer("[1] >> Load Input");
		Mov3letsUtils.printMemory();
		
		// STEP 2 - RUN:
		Mov3letsUtils.getInstance().startTimer("[3] >> Processing time");
		try {
			mov.mov3lets();
		} catch (Exception e) {
			// Empty if can't
			Mov3letsUtils.traceE("MASTERMovelets encoutered the following error.", e);
			Mov3letsUtils.getInstance().stopTimer("[3] >> Processing time");
//			e.printStackTrace();
			System.exit(1);
		}
		Mov3letsUtils.getInstance().stopTimer("[3] >> Processing time");
//		System.out.println(inputFile);
		
		// End Date:
		Mov3letsUtils.trace(new Date().toString());
	}
	
	/**
	 * Show usage.
	 *
	 * @param params the params
	 * @param errorMessage the error message
	 */
	public static void showUsage(HashMap<String, Object> params, String errorMessage) {
		Mov3letsUtils.trace(errorMessage + " [ERROR]");
		Mov3letsUtils.trace("Usage: java -jar MASTERMov3lets.jar [args...]");
		Mov3letsUtils.trace(printParams(params, null));
	}

	/**
	 * Prints the params.
	 *
	 * @param params the params
	 * @param descriptor the descriptor
	 * @return the string
	 */
	public static String printParams(HashMap<String, Object> params, Descriptor descriptor) {
		String str = "Configurations:" + System.getProperty("line.separator");
		
//		String[] columns = {"Option", "Description", "Value", "Help"};
		Object[][] data = {
//				{"-curpath", 			"Datasets directory", 		params.get("curpath"), 						""},
//				{"-respath", 			"Results directory", 		(params.containsKey("result_dir_path")? params.get("result_dir_path") : params.get("respath")), 				""},
//				{"-descfile", 			"Description file", 		params.get("descfile"), 					""},
				{"-nt", 				"Allowed Threads", 			params.get("nthreads"), 					""},
				{"-ms", 				"Min size", 				params.get("min_size"), 					""},
				{"-Ms", 				"Max size", 				params.get("max_size"), 					"Any positive, All sizes: -1, Log: -3"},
//				{"", 					"", 						"",						 					"All sizes: -1,"},
//				{"", 					"", 						"", 										"Log: -3"},
				{"-mnf", 				"Max Number of Dimensions",	params.get("max_number_of_features"), 		"Any positive, Explore dim.: -1, Log: -3"},
//				{"", 					"", 						"",						 					"Explore dim.: -1,"},
//				{"", 					"", 						"", 										"Log: -3"},
//				{"-ed", 				"Explore dimensions", 		params.get("explore_dimensions"), 			"Same as -mnf -1"},
				{"-samples", 			"Samples", 					params.get("samples"), 						""},
				{"-sampleSize", 		"Sample Size", 				params.get("sample_size"), 					""},
				{"-q", 					"Quality Measure", 			params.get("str_quality_measure"), 			""},
				{"-medium", 			"Medium", 					params.get("medium"), 						""},
				{"-mpt", 				"Movelets Per Traj.", 		params.get("movelets_per_trajectory"), 		"Any positive, Auto: -1"},
				{"-output", 			"Output", 					params.get("output"), 						""},
				{"", 					"", 						"", 										""},
				{"-version", 			"Version Impl.", 			params.get("version"), 						"1.0, 2.0, super, hiper, hiper-pvt"},
				{"", 					"-- Last Prunning",			params.get("last_prunning"), 				""},
			};
		
		str += "   -curpath		Datasets directory:	" + params.get("curpath") + System.getProperty("line.separator");
		str += "   -respath		Results directory: 	" + (params.containsKey("result_dir_path")? params.get("result_dir_path") : params.get("respath")) + System.getProperty("line.separator");
		str += "   -descfile 		Description file : 	" + params.get("descfile") + System.getProperty("line.separator");
//		str += "   -nt 						Allowed Threads: 				" + params.get("nthreads") + System.getProperty("line.separator");
//		str += "   -ms						Min size: 						" + params.get("min_size") + System.getProperty("line.separator");
//		str += "   -Ms						Max size 						" + params.get("max_size")  + System.getProperty("line.separator")
//		    	+ "							[(Any positive) OR (all sizes, -1) OR (log, -3)]" + System.getProperty("line.separator");
//		str += "   -mnf 					Max # of Features: 				" + params.get("max_number_of_features")  + System.getProperty("line.separator")
//				+ "							[(Any positive) OR (explore dimensions, -1) OR (log, -2)]" + System.getProperty("line.separator");
//		str += "   -ed 					Explore dimensions: 			" + params.get("explore_dimensions") + System.getProperty("line.separator")
//				+ " 							[Same as -mnf -1]" + System.getProperty("line.separator");
//		str += "   -samples 				Samples: 					" + params.get("samples") + System.getProperty("line.separator");
//		str += "   -sampleSize  			Sample Size: 						" + params.get("sample_size") + System.getProperty("line.separator");
//		str += "   -q						Quality Measure: 				" + params.get("str_quality_measure") + System.getProperty("line.separator");
//		str += "   -medium 				Medium:						" + params.get("medium") + System.getProperty("line.separator");
//		str += "   -mpt 					Movelets Per Traj.: 				" + params.get("movelets_per_trajectory") + System.getProperty("line.separator");
//		str += "   -output 				Output:						" + params.get("output") + System.getProperty("line.separator");
//		str += "   -version 				Mov. Discovery Impl.:			" + params.get("version") + System.getProperty("line.separator");
		
		TableList at = new TableList("Option", "Description", "Value", "Help");
//		at.addRule();
//		at.addRow("Option", "Description", "Value", "Help");
//		at.addRule();
		for (Object[] row : data) {
			at.addRow(row);
		}
//		at.addRule();

//		at.addRow("Optimizations:", "", "", "");
//		at.addRow("", "Interning", 	(params.containsKey("interning")? (boolean)params.get("interning") : false), 	"");
//		at.addRow("", "Index", 		(params.containsKey("index")? 	  (boolean)params.get("index") 	   : false), 	"");
		at.addRule();
		
		if (descriptor != null) {
			at.addRow("Attributes:", "", "Type:", "Comparrator:");
			int i = 1;
			for (AttributeDescriptor attr : descriptor.getAttributes())
				at.addRow(i++, attr.getOrder() + " - " + attr.getText(), attr.getType(), attr.getComparator().toString());
		}
		
		str += at.print();
		
//		str += System.getProperty("line.separator") + "    Optimizations: ";
//		str += System.getProperty("line.separator")
//				+ "\t[Index: " + (params.containsKey("index")? (boolean)params.get("index") : false) + "], "
//				+ "[Interning: " + (params.containsKey("interning")? (boolean)params.get("interning") : false) + "]"
//				+ System.getProperty("line.separator");
		
		return str;
	}

	/**
	 * Default params.
	 *
	 * @return the hash map
	 */
	public static HashMap<String, Object> defaultParams() {
		
		HashMap<String, Object> params = new HashMap<String, Object>();

//		params.put("curpath", 					 null);
//		params.put("respath",					 null);
//		params.put("descfile",					 null);
//		params.put("outside_pivots",			 null);
		params.put("nthreads",					 1);
		params.put("min_size",					 1);
		params.put("max_size",					 -1); // unlimited maxSize
		params.put("str_quality_measure",		 "LSP"); // LSP | PROP
//		params.put("cache",						 true); // Deprecated: Cache Always.	
		params.put("explore_dimensions",		 false);
		params.put("max_number_of_features",	 -1);
		params.put("samples",					 1);			
		params.put("sample_size",				 0.5);			
		params.put("medium",					 "none"); // Other values minmax, sd, interquartil
		params.put("output",					 "numeric"); // Other values numeric discretized				
		params.put("pivots",					 false);						
		params.put("supervised",				 false);
		params.put("movelets_per_trajectory",	 -1);	// Filtering		
		params.put("lowm_memory",				 false);		
		params.put("last_prunning",				 false);		
		params.put("pivot_porcentage",			 10);
		params.put("only_pivots",				 false);
		params.put("interning",  				 true);
		params.put("verbose",				 	 true);
		params.put("version",				 	 "2.0");
		params.put("tau",					 	 0.9);
		params.put("bucket_slice",				 0.1);
		params.put("filter_strategy",		 	 "none"); // Use Buckets
		params.put("LDM",					 	 false);
		
		return params;
	}
	
	/**
	 * Configure.
	 *
	 * @param args the args
	 * @return the hash map
	 */
	public static HashMap<String, Object> configure(String[] args) {
		
		HashMap<String, Object> params = defaultParams();

		for (int i = 0; i < args.length; i = i + 2) {
			String key = args[i];
			String value = args[i + 1];
			switch (key) {
			case "-curpath":
				params.put("curpath", value);
				break;
			case "-respath":
				params.put("respath", value);
				break;
			case "-descfile":
				params.put("descfile", value);
				break;
			case "-outside_pivots":
				params.put("outside_pivots", value);
				break;
			case "-nt":
			case "-nthreads":
				int N_THREADS = Integer.valueOf(value);
				N_THREADS = N_THREADS == 0? 1 : N_THREADS;
				params.put("nthreads", N_THREADS);
				break;
			case "-ms":
			case "-minSize":
			case "-min_size":
				params.put("min_size", Integer.valueOf(value));
				break;
			case "-Ms":
			case "-maxSize":
			case "-max_size":
				params.put("max_size", Integer.valueOf(value));
				break;
			case "-q":
			case "-strQualityMeasure":
				params.put("str_quality_measure", value);
				break;
//			case "-cache": // => Deprecated
//				params.put("cache", Boolean.valueOf(value));		
//				break;
			case "-ed":
			case "-exploreDimensions":
			case "-explore_dimensions":
				params.put("explore_dimensions", Boolean.valueOf(value));
				break;					
			case "-mnf":			
			case "-maxNumberOfFeatures":			
			case "-max_number_of_features":
				params.put("max_number_of_features", Integer.valueOf(value));
				break;
			case "-samples":
				params.put("samples", Integer.valueOf(value));			
				break;					
			case "-sampleSize":	
			case "-sample_size":
				params.put("sample_size", Double.valueOf(value));			
				break;					
			case "-medium":
				params.put("medium", value);		
				break;			
			case "-output":
				params.put("output", value);			
				break;		
			case "-pvt":
			case "-pivots":
				if (Boolean.valueOf(value)) {
					params.put("pivots",  true);
					params.put("version", "pivots");
				} else
					params.put("pivots", false);				
				break;		
			case "-sup":
			case "-supervised":
				params.put("supervised", Boolean.valueOf(value));				
				break;	
			case "-mpt":
			case "-movelets_per_trajectory":
			case "-moveletsPerTrajectory":
			case "-movelets_per_traj":
				params.put("movelets_per_trajectory", Integer.valueOf(value));			
				break;
			case "-lowm":
			case "-lowm_memory":
				params.put("lowm_memory", Boolean.valueOf(value));		
				break;			
			case "-last_prunning":		
			case "-lp":
				params.put("last_prunning", Boolean.valueOf(value));		
				break;	
			case "-pp":
			case "-pivot_porcentage":
				params.put("pivot_porcentage", Integer.valueOf(value));
				break;
			case "-only_pivots":
			case "-op":
				params.put("only_pivots", Boolean.valueOf(value));
			case "-index":
				params.put("index", Boolean.valueOf(value));
				break;
			case "-interning":
				params.put("interning", Boolean.valueOf(value));
				break;
			case "-v":
			case "-verbose":
				params.put("verbose", Boolean.valueOf(value));
				break;
			case "-d":
			case "-version":
				params.put("version", value.toLowerCase());
				if ("pivots".equalsIgnoreCase(value)) params.put("pivots",  true);
				break;		
			case "-tau":	
			case "-T":
				params.put("tau", Double.valueOf(value));			
				break;	
			case "-bucket_slice":	
			case "-BU":
				params.put("bucket_slice", Double.valueOf(value));			
				break;	
			case "-filter":
			case "-filter_strategy":
				params.put("filter_strategy", value);
				break;
			case "-LDM":	
			case "-ldm":
				params.put("LDM", Boolean.valueOf(value));			
				break;				
			default:
				System.err.println("Parâmetro " + key + " inválido.");
				System.exit(1);
			}
		}
		
		if (params.containsKey("max_number_of_features") && (int) params.get("max_number_of_features") == -1)
			params.put("explore_dimensions", true);

		return params;
	}
	
	
	/**
	 * Show configuration.
	 *
	 * @param descriptor the descriptor
	 * @return the string
	 */
	public static String showConfiguration(Descriptor descriptor) {

		String str = new String();
		
		if(descriptor.getFlag("pivots"))
			str += "Starting MASTERMovelets +Pivots extractor (10%) " + System.getProperty("line.separator");
		else if(descriptor.getParamAsInt("max_size")==-3)
			str += "Starting MASTERMovelets +Log extractor " + System.getProperty("line.separator");
		else
			str += "Starting MASTERMovelets extractor " + System.getProperty("line.separator");
		
		if (descriptor.hasParam("outside_pivots"))
			str += "Getting pivots from outside file:" + descriptor.getParam("outside_pivots") + System.getProperty("line.separator");
		
		str += printParams(descriptor.getParams(), descriptor);
		
//		if(descriptor.getFlag("last_prunning"))
//			str += "\tWITH Last Prunning" + System.getProperty("line.separator");
//		else
//			str += "\tWITHOUT Last Prunning" + System.getProperty("line.separator");
//		
//		str += "\tAttributes:"+ System.getProperty("line.separator") 
//			+ descriptor.toString() + System.getProperty("line.separator");
		
		return str;

	}
	
	/**
	 * Config respath.
	 *
	 * @param descFile the desc file
	 * @param descriptor the descriptor
	 * @return the string
	 */
	public static String configRespath(String descFile, Descriptor descriptor) {
		
		String resultDirPath = "MASTERMovelets" + System.getProperty("file.separator");
		
		if (descriptor.getFlag("supervised") || descriptor.getParamAsText("version").equals("super")) {
			resultDirPath += "Super_";
		} else if (descriptor.getParamAsText("version").equals("hiper") || descriptor.getParamAsText("version").equals("hpivots")) {
			resultDirPath += "Hiper_";
		} else if (descriptor.getFlag("pivots") || descriptor.getParamAsText("version").equals("pivots")) {			
			resultDirPath += "Pivots_";		
		} else if (descriptor.getParamAsText("version").equals("1.0")) {
			resultDirPath += "V1_";
		} else if (descriptor.getParamAsText("version").equals("3.0")) {
			resultDirPath += "V3_";
		} else if (descriptor.getParamAsText("version").equals("4.0")) {
			resultDirPath += "V4_";
		}
		
//		if (PIVOTS_FILE != null) {
//			resultDirPath += "_PivotsBOW";
//			outside_pivots = true;
//		}
//		else {
			if(descriptor.getFlag("pivots"))
//				resultDirPath += "Pivots_"
				resultDirPath +=  "Porcentage_" + descriptor.getParamAsText("pivot_porcentage")
							  + "_";
			else {
				if(descriptor.getParamAsInt("max_size") == -3) {
					resultDirPath += "Log_";
				} // else resultDirPath = + "_MM"; // No need
			}
//		}

		String DESCRIPTION_FILE_NAME = FilenameUtils.removeExtension(
				new File(descFile).getName());
		
		DESCRIPTION_FILE_NAME += "_" + descriptor.getParamAsText("str_quality_measure"); 
				
		if (descriptor.getFlag("explore_dimensions"))
			DESCRIPTION_FILE_NAME += "_ED"; 
		
		if (descriptor.getParamAsInt("max_number_of_features") > 0)
			DESCRIPTION_FILE_NAME += "_MNF-" + descriptor.getParamAsInt("max_number_of_features"); 
		
		if(descriptor.getFlag("last_prunning"))
			DESCRIPTION_FILE_NAME += "_WithLastPrunning";
//		else
//			DESCRIPTION_FILE_NAME += "_Witout-Last-Prunning";

		return Paths.get(descriptor.getParamAsText("respath"), resultDirPath + DESCRIPTION_FILE_NAME).toString();
	}

}
