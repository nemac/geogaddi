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

import com.amazonaws.auth.BasicAWSCredentials;

public class PropertiesManager {
    
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";

	private Properties properties = new Properties();
	private boolean override;
	private boolean useAll;
	private boolean fetcherEnabled;
	private boolean fetcherUncompress;
    private List<String> fetcherUrls;
    private String fetcherDumpDir;
    private boolean parcelerEnabled;
    private boolean parcelerUncompress;
    private boolean parcelerCleanSource;
    private boolean parcelerCleanDestination;
    private boolean parcelerExistingFromIntegrator;
    private List<String> parcelerSources;
    private String parcelerDestinatonDir;
    private String parcelerWhiteListSource;
    private int parcelerWhiteListIdx;
    private int parcelerFolderIdx;
    private int parcelerFileIdx;
    private int[] parcelerDataIdxArr;
    private boolean integratorEnabled;
    private boolean integratorCleanSource;
    private String integratorSourceDir;
    private BasicAWSCredentials integratorCredentials;
    private String integratorBucketName;

    private PropertiesManager() {
		
	}
    
    public static final PropertiesManager createFromPropertiesFile(String propertiesSource) throws IOException {
    	PropertiesManager p = new PropertiesManager();
    	Properties props = new Properties();
    	
        if (propertiesSource == null) {
            propertiesSource = DEFAULT_PROPERTIES_PATH;
        }
        
        FileReader reader = new FileReader(propertiesSource);
        props.load(reader);
        
        p.properties = props;
        
        // fetcher
        String fetchUrlPathProperty = props.getProperty("fetcher.source.url");
        p.fetcherUrls = Arrays.asList(fetchUrlPathProperty.replace(" ", "").split(","));
        p.fetcherDumpDir = props.getProperty("fetcher.dump.dir");
        
        // parceler
        String sourceCSVProperty = props.getProperty("parceler.source.csv");
		p.parcelerSources = Arrays.asList(sourceCSVProperty.replace(" ", "").split(","));
		
    	p.parcelerDestinatonDir = props.getProperty("parceler.output.dir");
    	p.parcelerWhiteListSource = props.getProperty("parceler.parcel.whitelist.source");
    	p.parcelerWhiteListIdx = new Integer(props.getProperty("parceler.parcel.whitelist.filter.index"));
    	p.parcelerFolderIdx = new Integer(props.getProperty("parceler.parce.folder.index"));
    	p.parcelerFileIdx = new Integer(props.getProperty("parceler.parce.file.index"));
    	String dataIdxProperty = props.getProperty("parceler.parce.data.index");
    	String[] dataIdxStrArr = dataIdxProperty.replace(" ", "").split(",");
    	int[] dataIdxArr = new int[dataIdxStrArr.length];
    	
    	for (int i = 0; i < dataIdxStrArr.length; i++) {
    		dataIdxArr[i] = Integer.parseInt(dataIdxStrArr[i]);
    	}
    	
    	p.parcelerDataIdxArr = dataIdxArr;
    	
    	// integrator
    	p.integratorSourceDir = props.getProperty("integrator.source.dir");
    	p.integratorCredentials = new BasicAWSCredentials(props.getProperty("integrator.s3.accesskeyid"), props.getProperty("integrator.s3.secretkey"));
    	p.integratorBucketName = props.getProperty("integrator.s3.bucket.name");
    	
    	return p;
    }
    
    public static final PropertiesManager createFromJSON(String jsonPath) throws IOException {
    	PropertiesManager p = new PropertiesManager();
    	
    	String input = FileUtils.readFileToString(new File(jsonPath));
    	JSONObject rootNode = new JSONObject(input);
    	
    	//override
    	p.override = rootNode.getBoolean("override");
    	p.useAll = rootNode.getBoolean("useAll");
    	
    	// fetcher
    	JSONObject fetcherNode = rootNode.getJSONObject("fetcher");
    	p.fetcherEnabled = fetcherNode.getBoolean("enabled");
    	p.fetcherUncompress = fetcherNode.getBoolean("uncompress");
    	JSONArray fetcherUrls = fetcherNode.getJSONArray("source");
    	List<String> fetchUrls = new ArrayList<String>();
    	for (int i = 0; i < fetcherUrls.length(); i++) {
    		fetchUrls.add(fetcherUrls.getString(i));
    	}
    	p.fetcherUrls = fetchUrls;
    	p.fetcherDumpDir = fetcherNode.getString("dumpDir");
    	
        // parceler
    	JSONObject parcelerNode = rootNode.getJSONObject("parceler");
    	p.parcelerEnabled = parcelerNode.getBoolean("enabled");
    	p.parcelerUncompress = parcelerNode.getBoolean("uncompress");
    	p.parcelerCleanSource = parcelerNode.getBoolean("cleanSource");
    	p.parcelerCleanDestination = parcelerNode.getBoolean("cleanDestination");
    	JSONArray sourceCsvs = parcelerNode.getJSONArray("sourceCsv");
    	List<String> parcelerSources = new ArrayList<String>();
    	for (int i = 0; i < sourceCsvs.length(); i++) {
    		parcelerSources.add(sourceCsvs.getString(i));
    	}
    	p.parcelerSources = parcelerSources;
    	p.parcelerDestinatonDir =  parcelerNode.getString("outputDir");
    	p.parcelerWhiteListSource = parcelerNode.getString("whiteList");
    	p.parcelerWhiteListIdx = (int) parcelerNode.getLong("whiteListIndex");
    	p.parcelerFolderIdx = (int) parcelerNode.getLong("folderIndex");
    	p.parcelerFileIdx = (int) parcelerNode.getLong("fileIndex");
    	
    	JSONArray dataIndexes = parcelerNode.getJSONArray("dataIndex");
    	int[] dataIdxArr = new int[dataIndexes.length()];
    	
    	for (int i = 0; i < dataIndexes.length(); i++) {
    		dataIdxArr[i] = (int) dataIndexes.getLong(i);
    	}
    	
    	p.parcelerDataIdxArr = dataIdxArr;
    	
    	// integrator
    	JSONObject integratorNode = rootNode.getJSONObject("integrator");
    	p.integratorEnabled = integratorNode.getBoolean("enabled");
    	p.integratorCleanSource = integratorNode.getBoolean("cleanSource");
    	p.integratorSourceDir = integratorNode.getString("sourceDir");
    	p.integratorCredentials = new BasicAWSCredentials(integratorNode.getString("awsAccessKeyId"), integratorNode.getString("awsSecretKey"));
    	p.integratorBucketName = integratorNode.getString("bucketName");
    	
    	return p;
    }
    
	public String getProperty(String key) {
		if (properties != null && !properties.isEmpty()) {
			return properties.getProperty(key);
		} else {
			return null;
		}
	}
    
    public boolean isOverride() {
    	return override;
    }
    
    public boolean isUseAll() {
    	return useAll;
    }
    
    public boolean isFetcherEnabled() {
    	return fetcherEnabled;
    }
    
    public boolean isFetcherUncompress() {
    	return fetcherUncompress;
    }

	public List<String> getFetchUrls() {
		return fetcherUrls;
	}

	public String getDumpDir() {
		return fetcherDumpDir;
	}
	
	public boolean isParcelerEnabled() {
		return parcelerEnabled;
	}
	
	public boolean isParcelerUncompress() {
		return parcelerUncompress;
	}
	
	public boolean isParcelerCleanSource() {
		return parcelerCleanSource;
	}
	
	public boolean isParcelerCleanDestination() {
		return parcelerCleanDestination;
	}
	
	public boolean isParcelerExistingFromIntegrator() {
		return parcelerExistingFromIntegrator;
	}

	public List<String> getParcelerSources() {
		return parcelerSources;
	}

	public String getDestinatonDir() {
		return parcelerDestinatonDir;
	}

	public String getWhiteListSource() {
		return parcelerWhiteListSource;
	}

	public int getWhiteListIdx() {
		return parcelerWhiteListIdx;
	}

	public int getFolderIdx() {
		return parcelerFolderIdx;
	}

	public int getFileIdx() {
		return parcelerFileIdx;
	}

	public int[] getDataIdxArr() {
		return parcelerDataIdxArr;
	}
	
	public boolean isIntegratorEnabled() {
		return integratorEnabled;
	}
	
	public boolean isIntegratorCleanSource() {
		return integratorCleanSource;
	}
	
	public String getIntegratorSourceDir() {
		return integratorSourceDir;
	}
	
	public BasicAWSCredentials getCredentials() {
		return integratorCredentials;
	}
    
	public String getBucketName() {
		return integratorBucketName;
	}
    
}
