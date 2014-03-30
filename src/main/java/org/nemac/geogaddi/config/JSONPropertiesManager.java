package org.nemac.geogaddi.config;

import com.amazonaws.auth.BasicAWSCredentials;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nemac.geogaddi.write.Utils;

public class JSONPropertiesManager extends AbstractPropertiesManager {
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.json";
    
    JSONPropertiesManager(final String propertiesSource) {
        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
    }

    public PropertiesManager build() throws FileNotFoundException, IOException {
        String input = FileUtils.readFileToString(new File(propertiesSource));
        JSONObject rootNode = new JSONObject(input);
        
        //override
        override = rootNode.getBoolean("override");
        useAll = rootNode.getBoolean("useAll");

        // fetcher
        JSONObject fetcherNode = rootNode.getJSONObject("fetcher");
        fetcherEnabled = fetcherNode.getBoolean("enabled");
        fetcherUncompress = fetcherNode.getBoolean("uncompress");
        JSONArray fetcherUrls = fetcherNode.getJSONArray("source");
        List<String> fetchUrls = new ArrayList<String>();
        for (int i = 0; i < fetcherUrls.length(); i++) {
            fetchUrls.add(fetcherUrls.getString(i));
        }
        this.fetcherUrls = fetchUrls;
        fetcherDumpDir = Utils.conformDirectoryString(fetcherNode.getString("dumpDir"));

        // parceler
        JSONObject parcelerNode = rootNode.getJSONObject("parceler");
        parcelerEnabled = parcelerNode.getBoolean("enabled");
        parcelerUncompress = parcelerNode.getBoolean("uncompress");
        parcelerCleanSource = parcelerNode.getBoolean("cleanSource");
        parcelerCleanDestination = parcelerNode.getBoolean("cleanDestination");
        JSONArray sourceCsvs = parcelerNode.getJSONArray("sourceCsv");
        List<String> parcelerSources = new ArrayList<String>();
        for (int i = 0; i < sourceCsvs.length(); i++) {
            parcelerSources.add(sourceCsvs.getString(i));
        }
        parcelerSources = parcelerSources;
        parcelerDestinatonDir = Utils.conformDirectoryString(parcelerNode.getString("outputDir"));
        parcelerFolderWhiteListSource = parcelerNode.getString("folderWhiteList");
        parcelerFolderWhiteListIdx = (int) parcelerNode.getLong("folderWhiteListIndex");
        parcelerFolderIdx = (int) parcelerNode.getLong("folderIndex");
        parcelerFileWhiteListSource = parcelerNode.getString("fileWhiteList");
        parcelerFileWhiteListIdx = (int) parcelerNode.getLong("fileWhiteListIndex");
        parcelerFileIdx = (int) parcelerNode.getLong("fileIndex");

        JSONArray dataIndexes = parcelerNode.getJSONArray("dataIndex");
        int[] dataIdxArr = new int[dataIndexes.length()];

        for (int i = 0; i < dataIndexes.length(); i++) {
            dataIdxArr[i] = (int) dataIndexes.getLong(i);
        }

        parcelerDataIdxArr = dataIdxArr;

        // integrator
        JSONObject integratorNode = rootNode.getJSONObject("integrator");
        integratorEnabled = integratorNode.getBoolean("enabled");
        integratorCleanSource = integratorNode.getBoolean("cleanSource");
        integratorSourceDir = Utils.conformDirectoryString(integratorNode.getString("sourceDir"));
        integratorCredentials = new BasicAWSCredentials(integratorNode.getString("awsAccessKeyId"), integratorNode.getString("awsSecretKey"));
        integratorBucketName = integratorNode.getString("bucketName");

        return this;
    }
}
