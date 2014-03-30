/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
        
        // fetcher
        String fetchUrlPathProperty = props.getProperty("fetcher.source.url");
        fetcherUrls = Arrays.asList(fetchUrlPathProperty.replace(" ", "").split(","));
        fetcherDumpDir = props.getProperty("fetcher.dump.dir");

        // parceler
        String sourceCSVProperty = props.getProperty("parceler.source.csv");
        parcelerSources = Arrays.asList(sourceCSVProperty.replace(" ", "").split(","));

        parcelerDestinatonDir = props.getProperty("parceler.output.dir");
        parcelerFolderWhiteListSource = props.getProperty("parceler.parcel.whitelist.source");
        parcelerFolderWhiteListIdx = new Integer(props.getProperty("parceler.parcel.whitelist.filter.index"));
        parcelerFolderIdx = new Integer(props.getProperty("parceler.parce.folder.index"));
        parcelerFileIdx = new Integer(props.getProperty("parceler.parce.file.index"));
        String dataIdxProperty = props.getProperty("parceler.parce.data.index");
        String[] dataIdxStrArr = dataIdxProperty.replace(" ", "").split(",");
        int[] dataIdxArr = new int[dataIdxStrArr.length];

        for (int i = 0; i < dataIdxStrArr.length; i++) {
            dataIdxArr[i] = Integer.parseInt(dataIdxStrArr[i]);
        }

        parcelerDataIdxArr = dataIdxArr;

        // integrator
        integratorSourceDir = props.getProperty("integrator.source.dir");
        integratorCredentials = new BasicAWSCredentials(props.getProperty("integrator.s3.accesskeyid"), props.getProperty("integrator.s3.secretkey"));
        integratorBucketName = props.getProperty("integrator.s3.bucket.name");

        return this;
    }

}
