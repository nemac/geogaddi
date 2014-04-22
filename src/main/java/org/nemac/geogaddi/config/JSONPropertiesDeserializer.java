package org.nemac.geogaddi.config;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.nemac.geogaddi.exception.PropertiesParseException;
import org.nemac.geogaddi.model.GeogaddiOptions;
import org.nemac.geogaddi.model.IntegratorOptions;

import java.io.File;
import java.lang.reflect.Type;

public class JSONPropertiesDeserializer {
    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.json";

    final String propertiesSource;

    public JSONPropertiesDeserializer(String propertiesSource) {
        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
    }

    public GeogaddiOptions deserialize() throws PropertiesParseException {
        GeogaddiOptions geogaddiOptions = GeogaddiOptions.getInstance();

        try {
            // TODO: broken because of the static GeogaddiOptions class
            String jsonPropsString = FileUtils.readFileToString(new File(propertiesSource));

            IntegratorOptions integratorOptions = new Gson().fromJson(jsonPropsString, IntegratorOptions.class);

            geogaddiOptions.setIntegratorOptions(integratorOptions);
        } catch (Exception e) {
            throw new PropertiesParseException("Failed to parse properties file: " + propertiesSource, e);
        }

        return geogaddiOptions;
    }

}
