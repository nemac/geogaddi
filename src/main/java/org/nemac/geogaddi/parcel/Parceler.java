package org.nemac.geogaddi.parcel;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nemac.geogaddi.Geogaddi;

import au.com.bytecode.opencsv.CSVReader;

public class Parceler {
    
    public static boolean parcel(List<String> csvSources, String destDir, String whiteListSource, int whiteListIdx, int folderIdx, int fileIdx, int[] dataIdxArr) throws IOException {
    	Set<String> whiteList = buildWhitelist(whiteListSource);
    	
    	for (String sourceCSV : csvSources) {
    		Map<String, Map<String, List<String>>> sourceDataHash = hashSourceData(sourceCSV, whiteList, whiteListIdx, folderIdx, fileIdx, dataIdxArr);
    		// TODO write
    	}
    	
        return true;
    }
    
    private static Set<String> buildWhitelist(String whiteListSource) throws IOException {
    	System.out.println("Building whitelist ");
    	Set<String> whiteList = new HashSet<String>();
    	
    	if (whiteListSource != null && !whiteListSource.isEmpty()) {
    		System.out.print("from " + whiteListSource);
    		CSVReader whiteListReader = new CSVReader(new FileReader(whiteListSource));
    		String[] nextLine;
        	while ((nextLine = whiteListReader.readNext()) != null) {
        		whiteList.add(nextLine[0]);
        	}
        	
        	whiteListReader.close();
        	
        	System.out.println("... whitelist built with " + whiteList.size() + " items");
    	} else {
    		System.out.println("... whitelist not found, no filtering will be perfomred");
    	}
    	
    	return whiteList;
    }
    
    private static Map<String, Map<String, List<String>>> hashSourceData(String sourceCSV, Set<String> whiteList, int whiteListIdx, int folderIdx, int fileIdx, int[] dataIdxArr) throws IOException {
    	// The data are hashed as follows
    	// Map
    	// 	String -> Folder
    	//  Map
    	//    String -> File
    	//    List   -> Row data
    	
    	System.out.println("Hashing " + sourceCSV);
    	Map<String, Map<String, List<String>>> folderHash = new HashMap<String, Map<String, List<String>>>();
    	CSVReader dataReader = new CSVReader(new FileReader(sourceCSV));
    	
    	String [] nextLine;
    	while ((nextLine = dataReader.readNext()) != null) {
    		// compare to whitelist - check if whitelist isn't empty and doesn't contain the current key 
    		if (!whiteList.isEmpty() && !whiteList.contains(nextLine[whiteListIdx])) {
    			continue;
    		}
    		
			if (folderHash.containsKey(nextLine[folderIdx])) { // add to existing folder hash item
				Map<String, List<String>> fileHash = folderHash.get(nextLine[folderIdx]);
				
				if (fileHash.containsKey(nextLine[fileIdx])) { // add to existing file hash item
					List<String> dataRow = fileHash.get(nextLine[fileIdx]);
					dataRow.add(getDataRow(nextLine, dataIdxArr));
				} else { // create new file hash item
					List<String> dataRow = new ArrayList<String>();
					dataRow.add(getDataRow(nextLine, dataIdxArr));
					fileHash.put(nextLine[fileIdx], dataRow);
				}
			
			} else { // create new folder hash item
				Map<String, List<String>> fileHash = new HashMap<String, List<String>>();
				List<String> dataRow = new ArrayList<String>();
				dataRow.add(getDataRow(nextLine, dataIdxArr));
				fileHash.put(nextLine[fileIdx], dataRow);
				folderHash.put(nextLine[folderIdx], fileHash);
			}
    	}
    	
    	dataReader.close();
    	
    	System.out.println("... CSV file hashed");
    	return folderHash;
    }
    
    private static String getDataRow(String[] csvRow, int[] dataIdxArr) {
		List<String> rowData = new ArrayList<String>();
		
		// loop over items in data index, add to row data
		for (int dataIdx : dataIdxArr) {
			rowData.add(csvRow[dataIdx]);
		}
		
		String[] rowArray = rowData.toArray(new String[rowData.size()]);
		return Arrays.toString(rowArray);
    }
    
}
