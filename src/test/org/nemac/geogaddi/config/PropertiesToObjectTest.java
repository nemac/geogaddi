package org.nemac.geogaddi.config;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.nemac.geogaddi.exception.PropertiesParseException;
import org.nemac.geogaddi.options.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PropertiesToObjectTest {

    @Test
    public void testReadFile() throws URISyntaxException, IllegalAccessException, ConfigurationException, IOException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException, PropertiesParseException {
        URL resource = PropertiesToObjectTest.class.getResource("/test.properties");
        String testProps = Paths.get(resource.toURI()).toString();

        PropertiesToObject pto = new PropertiesToObject(testProps);

        GeogaddiOptions options = pto.deserialize();
        assertFalse(options.isQuiet());
        assertTrue(options.isUseAll());
        assertTrue(options.isUncompress());

        FetcherOptions fetcherOptions = options.getFetcherOptions();
        assertTrue(fetcherOptions.isEnabled());
        assertTrue(fetcherOptions.isUncompress());
        assertEquals("test/data/dump", fetcherOptions.getDumpDir());
        assertEquals(2, fetcherOptions.getSources().size());
        assertTrue(fetcherOptions.getSources().contains("ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily/by_year/1900.csv.gz"));
        assertTrue(fetcherOptions.getSources().contains("ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily/by_year/1901.csv.gz"));

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

        IntegratorOptions integratorOptions = options.getIntegratorOptions();
        assertTrue(integratorOptions.isEnabled());
        assertTrue(integratorOptions.isCleanSource());
        assertEquals("data/output", integratorOptions.getSourceDir());
        assertEquals("accessid", integratorOptions.getAwsAccessKeyId());
        assertEquals("secretkey", integratorOptions.getAwsSecretKey());
        assertEquals("testbucket", integratorOptions.getBucketName());

        DeriverOptions deriverOptions = options.getDeriverOptions();
        assertTrue(deriverOptions.isEnabled());
        assertEquals("test/data/output", deriverOptions.getSourceDir());
        assertEquals(1, deriverOptions.getTransformationOptions().size());

        TransformationOption transformationOption = deriverOptions.getTransformationOptions().get(0);
        assertEquals("First Transformation", transformationOption.getName());

        assertEquals("testSourceLib", transformationOption.getTransformationSourceLib());
        assertEquals("YTDCumulative", transformationOption.getTransformation());
        assertEquals("testTransformationFolder", transformationOption.getFolder());
        assertEquals("PRCP", transformationOption.getFile());
        assertEquals("testNormalDir", transformationOption.getNormalDir());
        assertTrue(0 == transformationOption.getDateIndex());
        assertTrue(1 == transformationOption.getDataIndex());
        assertEquals("PRCP_YTD", transformationOption.getOutName());
    }
}
