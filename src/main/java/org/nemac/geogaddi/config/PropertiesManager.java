package org.nemac.geogaddi.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class PropertiesManager {
    
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";

	private static Properties properties = new Properties();
    private List<String> fetchUrls;
    private String dumpDir;
    private List<String> parcelerSources;
    private String destinatonDir;
    private String whiteListSource;
    private int whiteListIdx;
    private int folderIdx;
    private int fileIdx;
    private int[] dataIdxArr;

    private PropertiesManager() {
		
	}
    
    public static final PropertiesManager createFromPropertiesFile(String propertiesSource) throws IOException {
    	PropertiesManager p = new PropertiesManager();
    	
        if (propertiesSource == null) {
            propertiesSource = DEFAULT_PROPERTIES_PATH;
        }
        
        FileReader reader = new FileReader(propertiesSource);
        properties.load(reader);
        
        // fetcher
        String fetchUrlPathProperty = properties.getProperty("fetcher.source.url");
        p.fetchUrls = Arrays.asList(fetchUrlPathProperty.replace(" ", "").split(","));
        p.dumpDir = properties.getProperty("fetcher.dump.dir");
        
        // parceler
        String sourceCSVProperty = properties.getProperty("parceler.source.csv");
		p.parcelerSources = Arrays.asList(sourceCSVProperty.replace(" ", "").split(","));
		
    	p.destinatonDir = properties.getProperty("parceler.output.dir");
    	p.whiteListSource = properties.getProperty("parceler.parcel.whitelist.source");
    	p.whiteListIdx = new Integer(properties.getProperty("parceler.parcel.whitelist.filter.index"));
    	p.folderIdx = new Integer(properties.getProperty("parceler.parce.folder.index"));
    	p.fileIdx = new Integer(properties.getProperty("parceler.parce.file.index"));
    	String dataIdxProperty = properties.getProperty("parceler.parce.data.index");
    	String[] dataIdxStrArr = dataIdxProperty.replace(" ", "").split(",");
    	int[] dataIdxArr = new int[dataIdxStrArr.length];
    	
    	for (int i = 0; i < dataIdxStrArr.length; i++) {
    		dataIdxArr[i] = Integer.parseInt(dataIdxStrArr[i]);
    	}
    	
    	p.dataIdxArr = dataIdxArr;
    	
    	return p;
    }
    
    public static final PropertiesManager createFromJSON(String jsonPath) throws IOException {
    	PropertiesManager p = new PropertiesManager();
    	
    	String input = FileUtils.readFileToString(new File(jsonPath));
    	JSONObject rootNode = new JSONObject(input);
    	
    	// fetcher
    	JSONObject fetcherNode = rootNode.getJSONObject("fetcher");
    	JSONArray fetcherUrls = fetcherNode.getJSONArray("source");
    	List<String> fetchUrls = new ArrayList<String>();
    	for (int i = 0; i < fetcherUrls.length(); i++) {
    		fetchUrls.add(fetcherUrls.getString(i));
    	}
    	p.fetchUrls = fetchUrls;
    	p.dumpDir = fetcherNode.getString("dumpDir");
    	
        // parceler
    	JSONObject parcelerNode = rootNode.getJSONObject("parceler");
    	JSONArray sourceCsvs = parcelerNode.getJSONArray("sourceCsv");
    	List<String> parcelerSources = new ArrayList<String>();
    	for (int i = 0; i < sourceCsvs.length(); i++) {
    		parcelerSources.add(sourceCsvs.getString(i));
    	}
    	p.parcelerSources = parcelerSources;
    	p.destinatonDir =  parcelerNode.getString("outputDir");
    	p.whiteListSource = parcelerNode.getString("whiteList");
    	p.whiteListIdx = (int) parcelerNode.getLong("whiteListIndex");
    	p.folderIdx = (int) parcelerNode.getLong("folderIndex");
    	p.fileIdx = (int) parcelerNode.getLong("fileIndex");
    	
    	JSONArray dataIndexes = parcelerNode.getJSONArray("dataIndex");
    	int[] dataIdxArr = new int[dataIndexes.length()];
    	
    	for (int i = 0; i < dataIndexes.length(); i++) {
    		dataIdxArr[i] = (int) dataIndexes.getLong(i);
    	}
    	
    	p.dataIdxArr = dataIdxArr;
    	
    	return p;
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
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
    
    
}
