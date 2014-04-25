package org.nemac.geogaddi.derive.transformation;

import org.nemac.geogaddi.exception.TransformationNotFoundException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class TransformationFactory {

    private TransformationFactory() {
        //
    }

    public static AbstractTransformation createTransformation(final String transformationLibDir, final String transformationName) throws TransformationNotFoundException {
        return findTransformationFrom(transformationLibDir, transformationName);
    }

    public static AbstractTransformation createTransformation(final String transformationName) throws TransformationNotFoundException {
        return findTransformationFrom("", transformationName);
    }

    // factory for ad-hoc transformation classes - not recommended
    private static AbstractTransformation findTransformationFrom(final String transformationLibDir, final String transformationName) throws TransformationNotFoundException {
        AbstractTransformation transformation = null;

        // moving down from the most specific transformation definition
        // try url loader first
        if (transformationLibDir != null && !transformationLibDir.isEmpty()) {
            try {
                URL url = new URL(transformationLibDir + "/");
                URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, TransformationFactory.class.getClassLoader());
                Class<?> clazz = urlClassLoader.loadClass(transformationName);
                transformation = (AbstractTransformation) clazz.newInstance();
            } catch (ClassNotFoundException | MalformedURLException | InstantiationException | IllegalAccessException e) {
                throw new TransformationNotFoundException("Could not find specified Transformation type with given transformation source library.");
            }
        } else {
            // now try the Geogaddi registered Transformations
            TransformationType transformationType = TransformationType.fromType(transformationName);
            transformation = transformationFromType(transformationType);
            
            // now check the class path
            if (transformation == null) {
                try {
                    Class<?> clazz = Class.forName(transformationName);
                    transformation = (AbstractTransformation) clazz.newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new TransformationNotFoundException("Could not find specified Transformation: " + transformationName + " on classpath.");
                }
            }
        }

        return transformation;
    }

    // factory for registered transformations - it is recommended that we keep up with this
    private static AbstractTransformation transformationFromType(final TransformationType type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case YTDCumulative:
                return new YTDCumulative();
            default:
                return null;
        }
    }

}