package org.nemac.geogaddi.write;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.FileUtils;

public class Utils {
    private static final String COMPRESSION_EXTENSION = ".gz";
    private static final String filePattern = "%s/%s";
    
    // TODO expect full path like compress method
    public static String uncompress(String sourceFileName, String destDir) throws IOException {
        int suffixDelimiterPosition = sourceFileName.lastIndexOf(COMPRESSION_EXTENSION);
        String outputFileName = sourceFileName.substring(0, suffixDelimiterPosition);
        System.out.println("Unzipping " + sourceFileName + " to " + outputFileName);

        File sourceFile = new File(String.format(filePattern, destDir, sourceFileName));
        GZIPInputStream inputZip = new GZIPInputStream(new FileInputStream(sourceFile));
        FileOutputStream outputFile = new FileOutputStream(String.format(filePattern, destDir, outputFileName));
        
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputZip.read(buffer)) > 0) {
            outputFile.write(buffer, 0, len);
        }

        inputZip.close();
        outputFile.close();

        // cleanup source zip
        FileUtils.forceDelete(sourceFile);

        System.out.println("... unzip complete");
        return outputFileName;
    }
    
    public static String compress(String sourceFileName) throws IOException {
        String outputFileName = sourceFileName + COMPRESSION_EXTENSION;
        System.out.println("Zipping" + sourceFileName + " to " + outputFileName);
        
        File sourceFile = new File(sourceFileName);
        
        FileInputStream in = new FileInputStream(sourceFile);
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outputFileName));        
        
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        
        in.close();
        out.close();
        
        // cleanup source file
        FileUtils.forceDelete(sourceFile);
        
        System.out.println("... zip complete");
        return outputFileName;
    }
    
    public static String conformDirectoryString(String directoryString) {
        String lastChar = directoryString.substring(directoryString.length() - 1);
        if (lastChar.equals("/")) {
            return directoryString.substring(0, directoryString.length() - 1);
        } else {
            return directoryString;
        }
    }
}
