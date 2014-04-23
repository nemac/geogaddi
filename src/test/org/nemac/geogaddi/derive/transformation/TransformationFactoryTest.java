package org.nemac.geogaddi.derive.transformation;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TransformationFactoryTest {
    @Test
    public void testCreateTransformationFromURLClass() throws Exception {
        String transformationClassName = "org.nemac.geogaddi.derive.transformation.TransformationStub";

        Path testPath = Paths.get(Thread.currentThread().getContextClassLoader().getResource("").toURI());
        File transformationSourceLib = CreateJarFile.buildJar(testPath.toFile());

        Transformation transformation = TransformationFactory.createTransformation(transformationSourceLib.toURI().toString(), transformationClassName);

        assertEquals(transformationClassName, transformation.getClass().getName());

        Files.deleteIfExists(Paths.get(transformationSourceLib.toURI()));
    }

    @Test
    public void testCreateTransformationFromClassPath() throws Exception {
        String transformationClassName = "org.nemac.geogaddi.derive.transformation.TransformationStub";

        Transformation transformation = TransformationFactory.createTransformation(transformationClassName);

        assertEquals(transformationClassName, transformation.getClass().getName());
    }

    @Test
    public void testCreateTransformationRegisteredTransformation() throws Exception {
        String transformationName = "YTDCumulative";

        Transformation transformation = TransformationFactory.createTransformation(transformationName);

        assertEquals(transformationName, transformation.getClass().getSimpleName());
    }
}
