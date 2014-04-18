package org.nemac.geogaddi.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PropertiesToObjectTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReadFile() throws URISyntaxException {
        URL resource = PropertiesToObjectTest.class.getResource("/test.properties");
        String testProps = Paths.get(resource.toURI()).toString();

        PropertiesToObject pto = new PropertiesToObject(testProps);

        try {
            pto.deserialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
