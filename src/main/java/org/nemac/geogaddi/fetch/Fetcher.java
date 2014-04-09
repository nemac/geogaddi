package org.nemac.geogaddi.fetch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.nemac.geogaddi.write.Utils;

public class Fetcher {

    public static List<String> multiFetch(List<String> sourceUrlPaths, String destinationDirectory, boolean unzip, boolean quiet) throws IOException {
        List<String> outputFiles = new ArrayList<String>();
        for (String sourceUrlPath : sourceUrlPaths) {
            outputFiles.add(fetch(sourceUrlPath, destinationDirectory, unzip, quiet));
        }

        return outputFiles;
    }

    public static String fetch(String sourceUrlPath, String destinationDirectory, boolean unzip, boolean quiet) throws IOException {
        String downloadedFilePath = download(sourceUrlPath, destinationDirectory, quiet);
        if (unzip) {
            return destinationDirectory + Utils.uncompress(downloadedFilePath, destinationDirectory);
        } else {
            return destinationDirectory + downloadedFilePath;
        }
    }

    private static String download(String sourceUrlPath, String destinationDirectory, boolean quiet) throws IOException {
        if (!quiet) System.out.println(sourceUrlPath + " is downloading to " + destinationDirectory);
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

        if (!quiet) System.out.println("... download complete");
        return outputFilePath;
    }
}
