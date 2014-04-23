package org.nemac.geogaddi.derive.transformation;

import java.text.ParseException;
import java.util.SortedMap;

public class TransformationStub extends AbstractTransformation {

    @Override
    public SortedMap<String, Float> transform(SortedMap<String, Float> values) throws ParseException {
        return null;
    }

    @Override
    public SortedMap<String, Float> transformNormals(SortedMap<String, Float> values) {
        return null;
    }
}
