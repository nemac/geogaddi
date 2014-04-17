package org.nemac.geogaddi.config;

import org.junit.Test;
import org.nemac.geogaddi.config.element.DeriverOptions;
import org.nemac.geogaddi.config.element.FetcherOptions;
import org.nemac.geogaddi.config.element.GeogaddiOptions;
import org.nemac.geogaddi.config.element.TransformationProperty;

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

        DeriverOptions deriverOptions = options.getDeriverOptions();
        assertFalse(deriverOptions.isEnabled());
        assertEquals("test/data/output", deriverOptions.getSourceDir());
        assertEquals(2, deriverOptions.getTransformations().size());

        TransformationProperty transformationProperty = deriverOptions.getTransformations().get(0);
        assertEquals("First Transformation", transformationProperty.getName());
        assertEquals("testSourceLib", transformationProperty.getTransformationSourceLib());
        assertEquals("YTDCumulative", transformationProperty.getTransformation());
        assertEquals("testTransformationFolder", transformationProperty.getFolder());
        assertEquals("PRCP", transformationProperty.getFile());
        assertEquals("testNormalDir", transformationProperty.getNormalDir());
        assertEquals(0, transformationProperty.getDateIndex());
        assertEquals(1, transformationProperty.getDataIndex());
        assertEquals("PRCP_YTD", transformationProperty.getOutName());

        FetcherOptions fetcherOptions = options.getFetcherOptions();
        assertTrue(fetcherOptions.isEnabled());
        assertTrue(fetcherOptions.isUncompress());
        assertEquals("test/data/dump", fetcherOptions.getDumpDir());
        assertEquals(2, fetcherOptions.getSources().size());
        assertTrue(fetcherOptions.getSources().contains("ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily/by_year/1900.csv.gz"));
        assertTrue(fetcherOptions.getSources().contains("ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily/by_year/1901.csv.gz"));


    }

}
