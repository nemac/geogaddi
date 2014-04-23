package org.nemac.geogaddi.derive.transformation;

import java.text.ParseException;
import java.util.SortedMap;

public interface Transformation {
    // all public facing methods and methods not implemented in any abstract classes

    SortedMap<String, Float> process(final SortedMap<String, Float> values, final SortedMap<String, Float> defaults) throws ParseException;

    SortedMap<String, Float> transform(final SortedMap<String, Float> values) throws ParseException;

    SortedMap<String, Float> transformNormals(final SortedMap<String, Float> values);
}
