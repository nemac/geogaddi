package org.nemac.geogaddi.fetch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;

public class Fetcher {

    private static final String COMPRESSION_EXTENSION = ".gz";

    public static String fetch(String sourceUrlPath, String destinationDirectory) throws IOException {
        String downloadedFilePath = download(sourceUrlPath, destinationDirectory);
        return unzip(downloadedFilePath, destinationDirectory);
    }

    public static List<String> multiFetch(List<String> sourceUrlPaths, String destinationDirectory) throws IOException {
        List<String> outputFiles = new ArrayList<String>();

        for (String sourceUrlPath : sourceUrlPaths) {
            outputFiles.add(fetch(sourceUrlPath, destinationDirectory));
        }

        return outputFiles;
    }

    private static String download(String sourceUrlPath, String destinationDirectory) throws IOException {
        System.out.println(sourceUrlPath + " is downloading to " + destinationDirectory);
        URL url = new URL(sourceUrlPath);
        URLConnection downloader = url.openConnection(); // new FtpURLConnection(url);
        InputStream inputStream = downloader.getInputStream();

        String outputFilePath = sourceUrlPath.substring(sourceUrlPath.lastIndexOf("/"));
        // build folder path, if doesn't exist
        File destCSV = new File(destinationDirectory + outputFilePath);
        destCSV.getParentFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream(destCSV);

        byte[] buffer = new byte[4096];
        int len;

        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }

        inputStream.close();
        outputStream.close();
        
        System.out.println("... download complete");
        return outputFilePath;
    }

    private static String unzip(String source, String destinationDirectory) throws IOException {
        return unzip(source, destinationDirectory, COMPRESSION_EXTENSION);
    }

    private static String unzip(String downloadedSourceZip, String destinationDirectory, String compressionExtension) throws IOException {
        int suffixDelimiterPosition = downloadedSourceZip.lastIndexOf(compressionExtension);
        String outputFileName = downloadedSourceZip.substring(0, suffixDelimiterPosition);
        System.out.println("Unzipping " + downloadedSourceZip + " to " + outputFileName);
        
        File downloadedSourceZipFile = new File(destinationDirectory + downloadedSourceZip);

        GZIPInputStream inputZip = new GZIPInputStream(new FileInputStream(downloadedSourceZipFile));

        byte[] buffer = new byte[4096];
        FileOutputStream outputFile = new FileOutputStream(destinationDirectory + outputFileName);

        int len;
        while ((len = inputZip.read(buffer)) > 0) {
            outputFile.write(buffer, 0, len);
        }

        inputZip.close();
        outputFile.close();
        
        // cleanup source zip
        FileUtils.forceDelete(downloadedSourceZipFile);

        System.out.println("... unzip complete");
        return destinationDirectory + outputFileName;
    }
}
