package org.nemac.geogaddi.write;

import org.apache.commons.io.FileUtils;
import org.nemac.geogaddi.model.GeogaddiOptions;
import org.nemac.geogaddi.parcel.summary.Summarizer;
import org.nemac.geogaddi.parcel.summary.SummaryState;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Writer {
    private static final String UNCOMPRESSED_OUTPUT_PATTERN = "%s/%s/%s.csv";
    private static final String COMPRESSED_OUTPUT_PATTERN = "%s/%s/%s.csv.gz";
    private static final boolean isUncompress = GeogaddiOptions.isUncompress();
    private static final boolean isQuiet = GeogaddiOptions.isQuiet();

    // TODO: use uncompress flag to optionally work with gzipped files
    public static void write(Map<String, Map<String, Set<String>>> parcelMap, Summarizer summarizer, String destDirPath) throws IOException {
        boolean compressed = !isUncompress;
        if (!isQuiet) System.out.println("Writing to " + destDirPath);

        for (Map.Entry<String, Map<String, Set<String>>> folderEntry : parcelMap.entrySet()) {
            Map<String, Set<String>> fileHash = folderEntry.getValue();

            for (Map.Entry<String, Set<String>> fileEntry : fileHash.entrySet()) {
                File destFile = new File(String.format(UNCOMPRESSED_OUTPUT_PATTERN, destDirPath, folderEntry.getKey(), fileEntry.getKey()));
                destFile.getParentFile().mkdirs();
                
                File useFile = destFile;
                if (compressed) { // need to uncompress to read
                    File compressedFile = new File(String.format(COMPRESSED_OUTPUT_PATTERN, destDirPath, folderEntry.getKey(), fileEntry.getKey()));
                    if (compressedFile.exists()) {
                        useFile = new File(Utils.uncompress(compressedFile.getCanonicalPath(), true));
                    }
                }
                
                if (destFile.exists()) {
                    // scan and compare duplicate lines between the file and the hash
                    List<String> sourceList = FileUtils.readLines(useFile);
                    // remove duplicates from hash
                    List<String> hashedList = new ArrayList<String>(fileEntry.getValue());
                    hashedList.removeAll(sourceList);
                                        
                    // check last element of source list to see if it should come before new item hash
                    if (!sourceList.isEmpty() && !hashedList.isEmpty() && sourceList.get(sourceList.size() - 1).compareTo(hashedList.get(0)) > 0) {
                        if (!isQuiet) System.out.println("... backlog data detected, rebuilding output");
                        Set<String> writeSet = new TreeSet<String>();
                        writeSet.addAll(sourceList);
                        writeSet.addAll(hashedList);
                        FileUtils.writeLines(useFile, writeSet);
                        summarizer.addElement(folderEntry.getKey(), fileEntry.getKey(), writeSet, SummaryState.BACKLOG);
                    } else {
                        if (hashedList.isEmpty()) {
                            if (!isQuiet) System.out.println("... skipping " + destFile + " - all entries are duplicates");
                            summarizer.setElement(folderEntry.getKey(), fileEntry.getKey(), Summarizer.getDataRange(sourceList, SummaryState.UNCHANGED));
                        } else {
                            FileUtils.writeLines(useFile, hashedList, true);
                            summarizer.addElement(folderEntry.getKey(), fileEntry.getKey(), sourceList.get(0).split(",")[0], hashedList.get(hashedList.size() - 1).split(",")[0], SummaryState.APPEND);
                        }
                    }
                } else {
                    FileUtils.writeLines(useFile, fileEntry.getValue());
                }
                
                // write out file as gz and delete temp
                if (compressed) {
                    Utils.compress(useFile.getCanonicalPath());
                }
            }
        }

        if (!isQuiet) System.out.println("... data written to the CSVs");
    }
}
