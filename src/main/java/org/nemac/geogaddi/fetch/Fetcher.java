package org.nemac.geogaddi.fetch;

import org.nemac.geogaddi.model.FetcherOptions;
import org.nemac.geogaddi.model.GeogaddiOptions;
import org.nemac.geogaddi.write.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Fetcher {
    private static final FetcherOptions fetcherOptions = GeogaddiOptions.getFetcherOptions();
    private static final boolean isUncompress = GeogaddiOptions.isUncompress();
    private static final boolean isQuiet = GeogaddiOptions.isQuiet();

    public static List<String> multiFetch() throws IOException {
        List<String> outputFiles = new ArrayList<String>();
        for (String sourceUrlPath : fetcherOptions.getSources()) {
            outputFiles.add(fetch(sourceUrlPath));
        }

        return outputFiles;
    }

    private static String fetch(String sourceUrlPath) throws IOException {
        String destinationDirectory = fetcherOptions.getDumpDir();
        String downloadedFilePath = download(sourceUrlPath, destinationDirectory);

        if (isUncompress) {
            return destinationDirectory + Utils.uncompress(downloadedFilePath, destinationDirectory);
        } else {
            return destinationDirectory + downloadedFilePath;
        }
    }

    private static String download(String sourceUrlPath, String destinationDirectory) throws IOException {
        if (!isQuiet) System.out.println(sourceUrlPath + " is downloading to " + destinationDirectory);
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

        if (!isQuiet) System.out.println("... download complete");
        return outputFilePath;
    }
}
