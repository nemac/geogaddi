package org.nemac.geogaddi.parcel;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

public class Parceler {

    public static Map<String, Map<String, Set<String>>> parcel(String csvSource, String destDir, String whiteListSource, int whiteListIdx, int folderIdx, int fileIdx, int[] dataIdxArr, boolean isSourceUncompressed) throws IOException {
        Set<String> whiteList = buildWhitelist(whiteListSource);
        return hashSourceData(csvSource, whiteList, whiteListIdx, folderIdx, fileIdx, dataIdxArr, isSourceUncompressed);
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

    private static Map<String, Map<String, Set<String>>> hashSourceData(String sourceCSV, Set<String> whiteList, int whiteListIdx, int folderIdx, int fileIdx, int[] dataIdxArr, boolean isSourceUncompressed) throws IOException {
    	// The data are hashed as follows
        // Map
        // 	String -> Folder
        //  Map
        //    String -> File
        //    Set   -> Row data

        System.out.println("Hashing " + sourceCSV);
        Map<String, Map<String, Set<String>>> folderHash = new TreeMap<String, Map<String, Set<String>>>();

        CSVReader dataReader;
        if (isSourceUncompressed) {
            dataReader = new CSVReader(new FileReader(sourceCSV));
        } else {
            GZIPInputStream in = new GZIPInputStream(new FileInputStream(sourceCSV));
            dataReader = new CSVReader(new InputStreamReader(in));
        }

        String[] nextLine;
        while ((nextLine = dataReader.readNext()) != null) {
            // compare to whitelist - check if whitelist isn't empty and doesn't contain the current key 
            if (!whiteList.isEmpty() && !whiteList.contains(nextLine[whiteListIdx])) {
                continue;
            }

            if (folderHash.containsKey(nextLine[folderIdx])) { // add to existing folder hash item
                Map<String, Set<String>> fileHash = folderHash.get(nextLine[folderIdx]);

                if (fileHash.containsKey(nextLine[fileIdx])) { // add to existing file hash item
                    Set<String> dataRow = fileHash.get(nextLine[fileIdx]);
                    dataRow.add(getDataRow(nextLine, dataIdxArr));
                } else { // create new file hash item
                    Set<String> dataRow = new TreeSet<String>();
                    dataRow.add(getDataRow(nextLine, dataIdxArr));
                    fileHash.put(nextLine[fileIdx], dataRow);
                }

            } else { // create new folder hash item
                Map<String, Set<String>> fileHash = new TreeMap<String, Set<String>>();
                Set<String> dataRow = new TreeSet<String>();
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

        return StringUtils.join(rowData, ",");
    }
}
