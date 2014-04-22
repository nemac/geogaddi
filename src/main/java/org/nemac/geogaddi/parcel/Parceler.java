package org.nemac.geogaddi.parcel;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.nemac.geogaddi.model.ParcelerOptions;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public final class Parceler {
    static boolean isQuiet;
    static ParcelerOptions parcelerOptions;
    
    private Parceler() {
        
    }

//    public static Map<String, Map<String, Set<String>>> parcel(String csvSource, String destDir, String folderWhiteListSource, int folderWhiteListIdx, int folderIdx, String fileWhiteListSource, int fileWhiteListIdx, int fileIdx, int[] dataIdxArr, boolean isSourceUncompressed, boolean quiet) throws IOException {
    public static Map<String, Map<String, Set<String>>> parcel(String csvSource, ParcelerOptions parcelerOpts, boolean isUncompress, boolean geodaddiIsQuiet) throws IOException {
        isQuiet = geodaddiIsQuiet;
        parcelerOptions = parcelerOpts;

        Set<String> folderWhiteList = buildWhitelist(parcelerOpts.getFolderWhiteList());
        Set<String> fileWhiteList = buildWhitelist(parcelerOpts.getFileWhiteList());

        return hashSourceData(folderWhiteList, fileWhiteList, csvSource);
    }



    private static Set<String> buildWhitelist(String whiteListSource) throws IOException {
        if (!isQuiet) System.out.println("Building whitelist ");
        Set<String> whiteList = new HashSet<String>();

        if (whiteListSource != null && !whiteListSource.isEmpty()) {
            if (!isQuiet) System.out.print("from " + whiteListSource);
            CSVReader whiteListReader = new CSVReader(new FileReader(whiteListSource));
            String[] nextLine;
            while ((nextLine = whiteListReader.readNext()) != null) {
                whiteList.add(nextLine[0]);
            }

            whiteListReader.close();

            if (!isQuiet) System.out.println("... whitelist built with " + whiteList.size() + " items");
        } else {
            if (!isQuiet) System.out.println("... whitelist not found, no filtering will be perfomred");
        }

        return whiteList;
    }

    private static Map<String, Map<String, Set<String>>> hashSourceData(Set<String> folderWhiteList, Set<String> fileWhiteList, String csvSource) throws IOException {
    	// The data are hashed as follows
        // Map
        // 	String -> Folder
        //  Map
        //    String -> File
        //    Set   -> Row data

        if (!isQuiet) System.out.println("Hashing " + csvSource);
        Map<String, Map<String, Set<String>>> folderHash = new TreeMap<String, Map<String, Set<String>>>();

        CSVReader dataReader;
        if (parcelerOptions.isUncompress()) {
            dataReader = new CSVReader(new FileReader(csvSource));
        } else {
            GZIPInputStream in = new GZIPInputStream(new FileInputStream(csvSource));
            dataReader = new CSVReader(new InputStreamReader(in));
        }

        String[] nextLine;
        while ((nextLine = dataReader.readNext()) != null) {
            // compare to whitelist - check if whitelist isn't empty and doesn't contain the current key 
            if (!folderWhiteList.isEmpty() && !folderWhiteList.contains(nextLine[parcelerOptions.getFolderWhiteListIndex()])) {
                continue;
            }
            
            if (!fileWhiteList.isEmpty() && !fileWhiteList.contains(nextLine[parcelerOptions.getFileWhiteListIndex()])) {
                continue;
            }

            Integer folderIdx = parcelerOptions.getFolderIndex();
            Integer fileIdx = parcelerOptions.getFileIndex();
            List<Integer> dataIdxArr = parcelerOptions.getDataIndexes();

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

        if (!isQuiet) System.out.println("... CSV file hashed");
        return folderHash;
    }

    private static String getDataRow(String[] csvRow, List<Integer> dataIdxArr) {
        List<String> rowData = new ArrayList<String>();

        // loop over items in data index, add to row data
        for (int dataIdx : dataIdxArr) {
            rowData.add(csvRow[dataIdx]);
        }

        return StringUtils.join(rowData, ",");
    }
}
