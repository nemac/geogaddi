package org.nemac.geogaddi.config;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.nemac.geogaddi.config.element.GeogaddiOptions;

import java.io.File;
import java.io.IOException;

public class JSONPropertiesDeserializer {
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.json";

    final String propertiesSource;

    public JSONPropertiesDeserializer(String propertiesSource) {
        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
    }

    public GeogaddiOptions deserialize() throws IOException {
        String jsonPropsString = FileUtils.readFileToString(new File(propertiesSource));

        GeogaddiOptions geogaddiOptions = new Gson().fromJson(jsonPropsString, GeogaddiOptions.class);

        return geogaddiOptions;
    }

}
