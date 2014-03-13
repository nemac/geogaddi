package org.nemac.geogaddi.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertiesManager {
    
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";
    private static final Properties properties = new Properties();
    private final List<String> fetchUrls;
    private final String dumpDir;
    private final List<String> parcelerSources;
    private final String destinatonDir;
    private final String whiteListSource;
    private final int whiteListIdx;
    private final int folderIdx;
    private final int fileIdx;
    private final int[] dataIdxArr;

    // TODO: factory?
    public PropertiesManager(String propertiesSource) throws IOException {
        if (propertiesSource == null) {
            propertiesSource = DEFAULT_PROPERTIES_PATH;
        }
        
        FileReader reader = new FileReader(propertiesSource);
        properties.load(reader);
        
        // fetcher
        String fetchUrlPathProperty = properties.getProperty("fetcher.source.url");
        this.fetchUrls = Arrays.asList(fetchUrlPathProperty.replace(" ", "").split(","));
        this.dumpDir = properties.getProperty("fetcher.dump.dir");
        
        // parceler
        String sourceCSVProperty = properties.getProperty("parceler.source.csv");
		this.parcelerSources = Arrays.asList(sourceCSVProperty.replace(" ", "").split(","));
		
    	this.destinatonDir = properties.getProperty("parceler.output.dir");
    	this.whiteListSource = properties.getProperty("parceler.parcel.whitelist.source");
    	this.whiteListIdx = new Integer(properties.getProperty("parceler.parcel.whitelist.filter.index"));
    	this.folderIdx = new Integer(properties.getProperty("parceler.parce.folder.index"));
    	this.fileIdx = new Integer(properties.getProperty("parceler.parce.file.index"));
    	String dataIdxProperty = properties.getProperty("parceler.parce.data.index");
    	String[] dataIdxStrArr = dataIdxProperty.replace(" ", "").split(",");
    	int[] dataIdxArr = new int[dataIdxStrArr.length];
    	
    	for (int i = 0; i < dataIdxStrArr.length; i++) {
    		dataIdxArr[i] = Integer.parseInt(dataIdxStrArr[i]);
    	}
    	
    	this.dataIdxArr = dataIdxArr;
        
    }
        
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public List<String> getFetchUrls() {
    	return fetchUrls;
    }

	public String getDumpDir() {
		return dumpDir;
	}

	public List<String> getParcelerSources() {
		return parcelerSources;
	}

	public String getDestinatonDir() {
		return destinatonDir;
	}

	public String getWhiteListSource() {
		return whiteListSource;
	}

	public int getWhiteListIdx() {
		return whiteListIdx;
	}
	
	public int getFolderIdx() {
		return folderIdx;
	}

	public int getFileIdx() {
		return fileIdx;
	}

	public int[] getDataIdxArr() {
		return dataIdxArr;
	}
	
	
}
