package org.nemac.geogaddi.write;

import org.apache.commons.io.FileUtils;
import org.nemac.geogaddi.GeogaddiOptionDriver;
import org.nemac.geogaddi.parcel.summary.Summarizer;
import org.nemac.geogaddi.parcel.summary.SummaryState;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nemac.geogaddi.parcel.summary.DataElement;

public class Writer extends GeogaddiOptionDriver {
    private static final String UNCOMPRESSED_OUTPUT_PATTERN = "%s/%s/%s.csv";
    private static final String COMPRESSED_OUTPUT_PATTERN = "%s/%s/%s.csv.gz";

    public static void write(Map<String, Map<String, Set<String>>> parcelMap, Summarizer summarizer, String destDirPath) throws IOException {
       boolean compressed = !geogaddiOptions.isUncompress();
        if (!geogaddiOptions.isQuiet()) System.out.println("Writing to " + destDirPath);

        for (Map.Entry<String, Map<String, Set<String>>> folderEntry : parcelMap.entrySet()) {
            /* folderEntry is now a single key/value pair from the parcelMap map.
             *     - the key [folderEntry.getKey()] is the station id
             *     - the value [folderEntry.getValue()] is another map, whose keys
             *       are variable ids, and whose values are the set of data for that variable */
            Map<String, Set<String>> fileHash = folderEntry.getValue();
            for (Map.Entry<String, Set<String>> fileEntry : fileHash.entrySet()) {
                /* fileEntry is now a single key/value pair from the folderEntry.getValue()
                 * value above.
                 *  - the key [fileEntry.getKey()] is the variable id
                 *  - the value [fileEntry.getValue()] is the set of data for that variable */
                try {
                    File destFile = new File(String.format(UNCOMPRESSED_OUTPUT_PATTERN, destDirPath, folderEntry.getKey(), fileEntry.getKey()));
                    destFile.getParentFile().mkdirs();
                    
                    File useFile = destFile;
                    if (compressed) { // need to uncompress to read
                        File compressedFile = new File(String.format(COMPRESSED_OUTPUT_PATTERN, destDirPath, folderEntry.getKey(), fileEntry.getKey()));
                        if (compressedFile.exists()) {
                            if (!geogaddiOptions.isQuiet()) System.out.println("  reading: " + compressedFile);
                            try {
                                useFile = new File(Utils.uncompress(compressedFile.getCanonicalPath(), true));
                            } catch (Exception e) {
                                System.out.println("Error reading file: " + compressedFile);
                                throw e;
                            }
                        }
                    } else {
                        if (!geogaddiOptions.isQuiet()) System.out.println("  reading: " + useFile);
                    }
                    
                    if (destFile.exists()) {
                        // scan and compare duplicate lines between the file and the hash
                        List<String> sourceList = FileUtils.readLines(useFile);
                        // remove duplicates from hash
                        List<String> hashedList = new ArrayList<String>(fileEntry.getValue());
                        hashedList.removeAll(sourceList);
                        
                        // check last element of source list to see if it should come before new item hash
                        if (!sourceList.isEmpty() && !hashedList.isEmpty() && sourceList.get(sourceList.size() - 1).compareTo(hashedList.get(0)) > 0) {
                            if (!geogaddiOptions.isQuiet()) System.out.println("... backlog data detected, rebuilding output");
                            Set<String> writeSet = new TreeSet<String>();
                            writeSet.addAll(sourceList);
                            writeSet.addAll(hashedList);
                            FileUtils.writeLines(useFile, writeSet);
                            summarizer.addElement(folderEntry.getKey(), fileEntry.getKey(), writeSet, SummaryState.BACKLOG);
                        } else {
                            if (hashedList.isEmpty()) {
                                // skip since most entries are duplicates
                                //if (!geogaddiOptions.isQuiet()) System.out.println("... skipping " + destFile + " - all entries are duplicates");
                                DataElement element = Summarizer.getDataRange(sourceList, SummaryState.UNCHANGED);
                                summarizer.addElement(folderEntry.getKey(), fileEntry.getKey(), element.getMin(), element.getMax(), SummaryState.UNCHANGED);
                            } else {
                                if (!geogaddiOptions.isQuiet()) System.out.println("... appending to " + useFile);
                                FileUtils.writeLines(useFile, hashedList, true);
                                summarizer.addElement(folderEntry.getKey(), fileEntry.getKey(), sourceList.get(0).split(",")[0], hashedList.get(hashedList.size() - 1).split(",")[0], SummaryState.APPEND);
                            }
                        }
                    } else {
                        if (!geogaddiOptions.isQuiet()) System.out.println("... writing out " + useFile);
                        FileUtils.writeLines(useFile, fileEntry.getValue());
                        
                        DataElement element = Summarizer.getDataRange(fileEntry.getValue(), SummaryState.NEW);
                        summarizer.addElement(folderEntry.getKey(), fileEntry.getKey(), element.getMin(), element.getMax(), SummaryState.NEW);
                    }
                    
                    // write out file as gz and delete temp
                    if (compressed) {
                        Utils.compress(useFile.getCanonicalPath());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (!geogaddiOptions.isQuiet()) System.out.println("... data written to the CSVs");
    }
}
