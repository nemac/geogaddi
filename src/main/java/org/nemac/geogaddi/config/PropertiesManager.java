package org.nemac.geogaddi.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
    
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";
    private static final Properties properties = new Properties();

    // TODO: factory?
    public PropertiesManager(String propertiesSource) throws IOException {
        if (propertiesSource == null) {
            propertiesSource = DEFAULT_PROPERTIES_PATH;
        }
        
        FileReader reader = new FileReader(propertiesSource);
        properties.load(reader);
    }
        
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
