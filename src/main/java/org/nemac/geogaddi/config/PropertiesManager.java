package org.nemac.geogaddi.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesManager {
    
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.properties";
    private static final Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger(PropertiesManager.class.getName());

    // TODO: singletonize? factory?
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
