package org.nemac.geogaddi.config;

import com.amazonaws.auth.BasicAWSCredentials;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class SimplePropertiesManager extends AbstractPropertiesManager {
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";

    SimplePropertiesManager(String propertiesSource) {
        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
    }

    @Override
    public PropertiesManager build() throws FileNotFoundException, IOException {
        FileReader reader = new FileReader(propertiesSource);
        Properties props = new Properties();
        props.load(reader);

        //override
        override = Boolean.parseBoolean(props.getProperty("override"));
        useAll = Boolean.parseBoolean(props.getProperty("useall"));
        
        // fetcher
        fetcherEnabled = Boolean.parseBoolean(props.getProperty("fetcher.enabled"));
        fetcherUncompress = Boolean.parseBoolean(props.getProperty("fetcher.uncompress"));
        String fetchUrlPathProperty = props.getProperty("fetcher.source.url");
        fetcherUrls = Arrays.asList(fetchUrlPathProperty.replace(" ", "").split(","));
        fetcherDumpDir = props.getProperty("fetcher.dump.dir");

        // parceler
        parcelerEnabled = Boolean.parseBoolean(props.getProperty("parceler.enabled"));
        parcelerUncompress = Boolean.parseBoolean(props.getProperty("parceler.uncompress"));
        parcelerCleanSource = Boolean.parseBoolean(props.getProperty("parceler.clean.source"));
        parcelerCleanDestination = Boolean.parseBoolean(props.getProperty("parceler.clean.destination"));
        String sourceCSVProperty = props.getProperty("parceler.source.csv");
        parcelerSources = Arrays.asList(sourceCSVProperty.replace(" ", "").split(","));
        parcelerDestinatonDir = props.getProperty("parceler.output.dir");
        parcelerFolderWhiteListSource = props.getProperty("parceler.parcel.whitelist.source");
        parcelerFolderWhiteListIdx = new Integer(props.getProperty("parceler.parcel.whitelist.filter.index"));
        parcelerFolderIdx = new Integer(props.getProperty("parceler.parce.folder.index"));
        parcelerFileWhiteListSource = props.getProperty("parceler.parcel.whitelist.file.source");
        parcelerFileWhiteListIdx = Integer.parseInt(props.getProperty("parceler.parcel.whitelist.file.index"));
        parcelerFileIdx = new Integer(props.getProperty("parceler.parce.file.index"));
        String dataIdxProperty = props.getProperty("parceler.parce.data.index");
        String[] dataIdxStrArr = dataIdxProperty.replace(" ", "").split(",");
        int[] dataIdxArr = new int[dataIdxStrArr.length];

        for (int i = 0; i < dataIdxStrArr.length; i++) {
            dataIdxArr[i] = Integer.parseInt(dataIdxStrArr[i]);
        }

        parcelerDataIdxArr = dataIdxArr;

        // integrator
        integratorEnabled = Boolean.parseBoolean(props.getProperty("integrator.enabled"));
        integratorCleanSource = Boolean.parseBoolean(props.getProperty("integrator.clean.source"));
        integratorSourceDir = props.getProperty("integrator.source.dir");
        integratorCredentials = new BasicAWSCredentials(props.getProperty("integrator.s3.accesskeyid"), props.getProperty("integrator.s3.secretkey"));
        integratorBucketName = props.getProperty("integrator.s3.bucket.name");

        return this;
    }

}
