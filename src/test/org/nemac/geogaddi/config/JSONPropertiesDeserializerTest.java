package org.nemac.geogaddi.config;

import org.junit.Test;
import org.nemac.geogaddi.model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class JSONPropertiesDeserializerTest {

    @Test
    public void testDeserialize() throws IOException, URISyntaxException {
        URL resource = PropertiesToObjectTest.class.getResource("/test.json");
        String testProps = Paths.get(resource.toURI()).toString();

        GeogaddiOptions options = new JSONPropertiesDeserializer(testProps).deserialize();
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
        assertTrue(parcelerOptions.isCleanSource());
        assertTrue(parcelerOptions.isCleanDestination());
        assertTrue(parcelerOptions.isExistingFromIntegrator());
        assertTrue(parcelerOptions.getSourceCSVs().contains("data/dump/1900.csv.gz"));
        assertTrue(parcelerOptions.getSourceCSVs().contains("data/dump/1901.csv.gz"));
        assertEquals("stations.csv", parcelerOptions.getFolderWhiteList());
        assertEquals(0, parcelerOptions.getFolderWhiteListIndex());
        assertEquals(0, parcelerOptions.getFolderIndex());
        assertEquals("vars.csv", parcelerOptions.getFileWhiteList());
        assertEquals(2, parcelerOptions.getFileWhiteListIndex());
        assertEquals(2, parcelerOptions.getFileIndex());
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
        assertFalse(deriverOptions.isEnabled());
        assertEquals("test/data/output", deriverOptions.getSourceDir());
        assertEquals(2, deriverOptions.getTransformations().size());

        TransformationOptions transformationOptions = deriverOptions.getTransformations().get(0);
        assertEquals("First Transformation", transformationOptions.getName());
        assertEquals("testSourceLib", transformationOptions.getTransformationSourceLib());
        assertEquals("YTDCumulative", transformationOptions.getTransformation());
        assertEquals("testTransformationFolder", transformationOptions.getFolder());
        assertEquals("PRCP", transformationOptions.getFile());
        assertEquals("testNormalDir", transformationOptions.getNormalDir());
        assertEquals(0, transformationOptions.getDateIndex());
        assertEquals(1, transformationOptions.getDataIndex());
        assertEquals("PRCP_YTD", transformationOptions.getOutName());

    }

}
