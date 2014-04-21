package org.nemac.geogaddi.config;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nemac.geogaddi.model.GeogaddiOptions;
import org.nemac.geogaddi.model.ParcelerOptions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PropertiesToObjectTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReadFile() throws URISyntaxException, IllegalAccessException, ConfigurationException, IOException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException {
        URL resource = PropertiesToObjectTest.class.getResource("/test.properties");
        String testProps = Paths.get(resource.toURI()).toString();

        PropertiesToObject pto = new PropertiesToObject(testProps);

        GeogaddiOptions options = pto.deserialize();

        ParcelerOptions parcelerOptions = options.getParcelerOptions();
        assertTrue(parcelerOptions.isEnabled());
        assertTrue(parcelerOptions.isUncompress());
        assertTrue(parcelerOptions.isCleanSource());
        assertTrue(parcelerOptions.isCleanDestination());
        assertTrue(parcelerOptions.isExistingFromIntegrator());
        assertTrue(parcelerOptions.getSourceCSVs().contains("data/dump/1900.csv.gz"));
        assertTrue(parcelerOptions.getSourceCSVs().contains("data/dump/1901.csv.gz"));
        assertEquals("stations.csv", parcelerOptions.getFolderWhiteList());
        assertEquals(0, parcelerOptions.getFolderWhiteListIndex().intValue());
        assertEquals(0, parcelerOptions.getFolderIndex().intValue());
        assertEquals("vars.csv", parcelerOptions.getFileWhiteList());
        assertEquals(2, parcelerOptions.getFileWhiteListIndex().intValue());
        assertEquals(2, parcelerOptions.getFileIndex().intValue());
        assertTrue(parcelerOptions.getDataIndexes().contains(1));
        assertTrue(parcelerOptions.getDataIndexes().contains(3));
        assertEquals("data/output", parcelerOptions.getOutputDir());
    }
}
