package org.nemac.geogaddi.write;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Writer {
	
	private static final String outputPattern = "%s/%s/%s.csv";
	
	public static void write(Map<String, Map<String, Set<String>>> parcelMap, String destDirPath) throws IOException {
		destDirPath = conformDirectoryString(destDirPath);
		System.out.println("Writing to " + destDirPath);
		
		for (Map.Entry<String, Map<String, Set<String>>> folderEntry : parcelMap.entrySet()) {
			Map <String, Set<String>> fileHash = folderEntry.getValue();
			
			for (Map.Entry<String, Set<String>> fileEntry : fileHash.entrySet()) {
				File destFile = new File(String.format(outputPattern, destDirPath, folderEntry.getKey(), fileEntry.getKey()));
				destFile.getParentFile().mkdirs();
				
				if (destFile.exists()) {
					// scan and compare duplicate lines between the file and the hash
					List<String> sourceList = FileUtils.readLines(destFile);
					List<String> hashedList = new ArrayList<String>(fileEntry.getValue());
					
					boolean sortRequired = false;
					
					// if no elements are common, can be appended cleanly
					if (!sourceList.isEmpty() && !Collections.disjoint(sourceList, hashedList)) {
						// compare order of source and hash to determine if full sort is required
						for (int i = 0; i < sourceList.size(); i++) {
							if (!sourceList.get(i).equals(hashedList.get(i))) {
								sortRequired = true;
								break;
							}
						}
					}

					if (sortRequired) {
						System.out.println("... backlog data detected, rebuilding output");
						FileUtils.writeLines(destFile, fileEntry.getValue());
					} else {
						// remove duplicates
						fileEntry.getValue().removeAll(sourceList);
						
						if (fileEntry.getValue().size() == 0) {
							System.out.println("... skipping " + destFile + " - all entries are duplicates");
						} else {
							//System.out.print("... appending new lines to the end");
							FileUtils.writeLines(destFile, fileEntry.getValue(), true);
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
