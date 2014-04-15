package org.nemac.geogaddi.derive;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.nemac.geogaddi.config.element.TransformationProperty;
import org.nemac.geogaddi.derive.transformation.AbstractTransformation;
import org.nemac.geogaddi.derive.transformation.TransformationFactory;
import org.nemac.geogaddi.derive.transformation.TransformationType;
import org.nemac.geogaddi.write.Utils;

public class Deriver {
    
    private static final String lineFormat = "%s,%s";
    
    public static Map<String, Map<String, Set<String>>> derive(String sourceDir, TransformationProperty transformationProperty, boolean uncompress) throws IOException, ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Map<String, Map<String, Set<String>>> derivedMap = new TreeMap<>();
        
        AbstractTransformation transformation;
        
        if (transformationProperty.getTransformationSourceLib().isEmpty()) {
            transformation = TransformationFactory.createTransformation(TransformationType.fromType(transformationProperty.getTransformation()));
        } else {
            // TODO: find the transformation with the reflection factory
            transformation = TransformationFactory.createTransformation(transformationProperty.getTransformation());
        }

        String[] extensions;
        
        if (uncompress) {
            String[] unExt = {"csv"};
            extensions = unExt;
        } else {
            String[] comExt = {"csv.gz"};
            extensions = comExt;
        }
        
        Iterator<File> files = FileUtils.iterateFiles(new File(sourceDir), extensions, true);
        
        while (files.hasNext()) {
            File file = files.next();
            if (file.getName().substring(0, file.getName().indexOf(".")).equals(transformationProperty.getFile())) {
                
                File useFile = file;
                if (!uncompress) {
                    useFile = new File(Utils.uncompress(file.getCanonicalPath(), false));
                }
                
                SortedMap<String, Float> dataPairs = linesToMap(FileUtils.readLines(useFile));
                SortedMap<String, Float> normalPairs = linesToMap(FileUtils.readLines(useFile)); // TODO
                
                System.out.println(FilenameUtils.getBaseName(useFile.getParent()));
                SortedMap<String, Float> processedPairs = transformation.process(dataPairs, normalPairs);

                Map<String, Set<String>> subMap = new TreeMap<>();
                subMap.put(transformationProperty.getOutName(),mapToLines(processedPairs));
                derivedMap.put(FilenameUtils.getBaseName(useFile.getParent()), subMap);
                
                if (!uncompress) {
                    FileUtils.deleteQuietly(useFile);
                }
            }
        }
        
        return derivedMap;
    }
    
    private static SortedMap<String, Float> linesToMap(List<String> lines) {
        SortedMap<String, Float> map = new TreeMap<>();
        
        for (String line : lines) {
            String[] pair = line.split(",");
            map.put(pair[0], Float.parseFloat(pair[1]));
        }
        
        return map;
    }
    
    private static Set<String> mapToLines(Map<String, Float> map) {
        Set<String> set = new TreeSet<>();
        
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            set.add(String.format(lineFormat, entry.getKey(), entry.getValue()));
        }
        
        return set;
    }
    
}
