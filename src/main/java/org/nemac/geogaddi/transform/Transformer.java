package org.nemac.geogaddi.transform;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

public class Transformer {
    
    public static boolean transform(String sourceCSV, String destDir, String whiteListSource, int whiteListIdx, int folderIdx, int fileIdx, int[] dataIdxArr) throws IOException {
    	Set<String> whiteList = buildWhitelist(whiteListSource);
    	Map<String, Map<String, List<String>>> sourceDataHash = hashSourceData(sourceCSV, whiteList, whiteListIdx, folderIdx, fileIdx, dataIdxArr);
    	
    	// TODO write
        return true;
    }
    
    private static Set<String> buildWhitelist(String whiteListSource) throws IOException {
    	System.out.println("Building whitelist from " + whiteListSource);
    	Set<String> whiteList = new HashSet<String>();
    	
    	CSVReader whiteListReader = new CSVReader(new FileReader(whiteListSource));
    	
    	String[] nextLine;
    	while ((nextLine = whiteListReader.readNext()) != null) {
    		whiteList.add(nextLine[0]);
    	}
    	
    	whiteListReader.close();
    	
    	System.out.println("... whitelist built with " + whiteList.size() + " items");
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
