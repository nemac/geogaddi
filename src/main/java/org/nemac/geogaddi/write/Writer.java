package org.nemac.geogaddi.write;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

public class Writer {

    private static final String outputPattern = "%s/%s/%s.csv";

    // TODO: use uncompress flag to optionally work with gzipped files
    public static void write(Map<String, Map<String, Set<String>>> parcelMap, String destDirPath, boolean uncompress) throws IOException {
        destDirPath = conformDirectoryString(destDirPath);
        System.out.println("Writing to " + destDirPath);

        for (Map.Entry<String, Map<String, Set<String>>> folderEntry : parcelMap.entrySet()) {
            Map<String, Set<String>> fileHash = folderEntry.getValue();

            for (Map.Entry<String, Set<String>> fileEntry : fileHash.entrySet()) {
                File destFile = new File(String.format(outputPattern, destDirPath, folderEntry.getKey(), fileEntry.getKey()));
                destFile.getParentFile().mkdirs();

                if (destFile.exists()) {
                    // scan and compare duplicate lines between the file and the hash
                    List<String> sourceList = FileUtils.readLines(destFile);
                    // remove duplicates from hash
                    List<String> hashedList = new ArrayList<String>(fileEntry.getValue());
                    hashedList.removeAll(sourceList);

                    // check last element of source list to see if it should come before new item hash
                    if (!sourceList.isEmpty() && !hashedList.isEmpty() && sourceList.get(sourceList.size() - 1).compareTo(hashedList.get(0)) > 0) {
                        System.out.println("... backlog data detected, rebuilding output");
                        Set<String> writeSet = new TreeSet<String>();
                        writeSet.addAll(sourceList);
                        writeSet.addAll(hashedList);
                        FileUtils.writeLines(destFile, writeSet);
                    } else {
                        if (hashedList.size() == 0) {
                            System.out.println("... skipping " + destFile + " - all entries are duplicates");
                        } else {
                            //System.out.print("... appending new lines to the end");
                            FileUtils.writeLines(destFile, hashedList, true);
                        }
                    }
                } else {
                    FileUtils.writeLines(destFile, fileEntry.getValue());
                }
            }
        }

        System.out.println("... data written to the CSVs");
    }

    private static final String conformDirectoryString(String directoryString) {
        String lastChar = directoryString.substring(directoryString.length() - 1);
        if (lastChar == "/") {
            return directoryString.substring(0, directoryString.length() - 1);
        } else {
            return directoryString;
        }
    }
}
