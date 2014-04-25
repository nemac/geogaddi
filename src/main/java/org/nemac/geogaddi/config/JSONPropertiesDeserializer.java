package org.nemac.geogaddi.config;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.nemac.geogaddi.config.options.GeogaddiOptions;
import org.nemac.geogaddi.exception.PropertiesParseException;

import java.io.File;

public class JSONPropertiesDeserializer {
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.json";

    private final String propertiesSource;

    public JSONPropertiesDeserializer(String propertiesSource) {
        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
    }

    public GeogaddiOptions deserialize() throws PropertiesParseException {
        GeogaddiOptions geogaddiOptions = new GeogaddiOptions();

        try {
            String jsonPropsString = FileUtils.readFileToString(new File(propertiesSource));

            geogaddiOptions = new Gson().fromJson(jsonPropsString, GeogaddiOptions.class);
        } catch (Exception e) {
            throw new PropertiesParseException("Failed to parse properties file: " + propertiesSource, e);
        }

        return geogaddiOptions;
    }

}
