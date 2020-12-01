package br.com.tarlis.mov3lets.method.loader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import br.com.tarlis.mov3lets.method.structures.descriptor.AttributeDescriptor;
import br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor;
import br.com.tarlis.mov3lets.model.MAT;
import br.com.tarlis.mov3lets.model.Point;

/**
 * The Class ZippedLoader.
 *
 * @param <T> the generic type
 */
public class ZippedLoader<T extends MAT<?>> extends CSVLoader<T> {

	/**
	 * Overridden method. 
	 * @see br.com.tarlis.mov3lets.method.loader.LoaderAdapter#load(java.lang.String, br.com.tarlis.mov3lets.method.structures.descriptor.Descriptor).
	 * 
	 * @param file
	 * @param descriptor
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<T> load(String file, Descriptor descriptor) throws IOException {

		List<T> list = new ArrayList<>();
		
		String curpath = descriptor.hasParam("curpath")? descriptor.getParamAsText("curpath") : "./";
		unzip(Paths.get(curpath, file+".zip").toString(), Paths.get(curpath, file).toString());
		
		File folder = new File(Paths.get(curpath, file).toString());
		if (folder.isDirectory()) {
			File[] listFiles = folder.listFiles();
			Arrays.sort(listFiles, new Comparator<File>() {
	            @Override
	            public int compare(File o1, File o2) {	                
	                return o1.getName().compareTo(o2.getName());
	            }
			});
						
			for (File subfile : listFiles) {

				String[] splittedName = subfile.getName().split("\\.");

				if (splittedName.length == 2 && splittedName[1].compareTo("r2") == 0) {

					list.add(loadTrajectory(subfile.getAbsolutePath(), descriptor));

				}
			}

			try {
				FileUtils.deleteDirectory(new File(Paths.get(curpath, file).toString()));			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else
			throw new IOException("Dirpath are not a directory.");
		
		return list;
	}
	
	/**
	 * Load trajectory.
	 *
	 * @param file the file
	 * @param descriptor the descriptor
	 * @return the t
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public T loadTrajectory(String file, Descriptor descriptor) throws IOException {
		
		List<MAT<String>> trajectories = new ArrayList<MAT<String>>();
		
		File subfile = new File(file);
		// Extracting information from file name
		String[] filenameSplitted = subfile.getName().split("[ |\\.]");
		int tid = Integer.valueOf( filenameSplitted[1].substring(1) );
		String label = filenameSplitted[2].substring(1);
		
		// IF MO type is String:
//		MO mo = new MO();
		MAT<String> mat = new MAT<String>();
		mat.setTid(tid);
		trajectories.add(mat);

		// Can use like this:
//		mo = (MO) new MovingObject<String>(label);
		// OR -- this for typing String:
		mat.setMovingObject(label);
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(subfile));
		String line;
		while ((line = bufferedReader.readLine()) != null) {

			String[] row = line.split(",");
			
			// For each attribute of POI
			Point poi = new Point();	
			poi.setTrajectory(mat);
			for (AttributeDescriptor attr : descriptor.getAttributes()) {
				
				if (attr.getType().startsWith("composite_")) {
					String value = row[attr.getOrder()-1] + " " + row[attr.getOrder()];
					poi.getAspects().add(instantiateAspect(attr, value));
				} else 
					poi.getAspects().add(instantiateAspect(attr, row[attr.getOrder()-1]));
				
			}
			mat.getPoints().add(poi);
		}

		bufferedReader.close();		

		return (T) mat;
	}
	
	/**
	 * **********************************************************************
	 * This utility extracts files and directories of a standard zip file to
	 * a destination directory.
	 *
	 * @author www.codejava.net
	 * ***********************************************************************
	 * @param zipFilePath the zip file path
	 * @param destDirectory the dest directory
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	
	/**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
	 * @throws IOException
     */
	protected void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
			
		ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        
    }

    /**
     * Extracts a zip entry (file entry).
     *
     * @param zipIn the zip in
     * @param filePath the file path
     * @throws IOException Signals that an I/O exception has occurred.
     */
	protected void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		int BUFFER_SIZE = 4096;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
