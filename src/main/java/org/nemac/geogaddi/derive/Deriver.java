package org.nemac.geogaddi.derive;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.nemac.geogaddi.derive.transformation.AbstractTransformation;
import org.nemac.geogaddi.derive.transformation.TransformationFactory;
import org.nemac.geogaddi.derive.transformation.TransformationType;
import org.nemac.geogaddi.model.DeriverOptions;
import org.nemac.geogaddi.model.GeogaddiOptions;
import org.nemac.geogaddi.model.TransformationOption;
import org.nemac.geogaddi.write.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Deriver {
    private static final String LINE_FORMAT = "%s,%s";
    private static final String PATH_FORMAT = "%s/%s.%s";
    private static final DeriverOptions DERIVER_OPTIONS = GeogaddiOptions.getDeriverOptions();
    private static final boolean isUncompress = GeogaddiOptions.isUncompress();
    
    public static Map<String, Map<String, Set<String>>> derive(TransformationOption transformationOption) throws IOException, ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Map<String, Map<String, Set<String>>> derivedMap = new TreeMap<>();
        
        AbstractTransformation transformation;
        
        if (transformationOption.getTransformationSourceLib().isEmpty()) {
            transformation = TransformationFactory.createTransformation(TransformationType.fromType(transformationOption.getTransformation()));
        } else {
            // TODO: find the transformation with the reflection factory
            transformation = TransformationFactory.createTransformation(transformationOption.getTransformation());
        }

        String[] extensions;
        
        if (isUncompress) {
            String[] unExt = {"csv"};
            extensions = unExt;
        } else {
            String[] comExt = {"csv.gz"};
            extensions = comExt;
        }
        
        Iterator<File> files = FileUtils.iterateFiles(new File(DERIVER_OPTIONS.getSourceDir()), extensions, true);
        
        while (files.hasNext()) {
            File file = files.next();
            if (file.getName().substring(0, file.getName().indexOf(".")).equals(transformationOption.getFile())) {
                
                File useFile = file;
                if (!isUncompress) {
                    useFile = new File(Utils.uncompress(file.getCanonicalPath(), false));
                }
                
                String folder = FilenameUtils.getBaseName(useFile.getParent());
                SortedMap<String, Float> dataPairs = linesToMap(FileUtils.readLines(useFile));
                
                File normalFile = getNormalFile(transformationOption.getNormalDir(), folder, extensions[0]);
                
                if (normalFile.exists()) { // only use if there is a corresponding normal
                    SortedMap<String, Float> normalPairs = linesToMap(FileUtils.readLines(normalFile));
                    SortedMap<String, Float> processedPairs = transformation.process(dataPairs, normalPairs);
                    
                    Map<String, Set<String>> subMap = new TreeMap<>();
                    subMap.put(transformationOption.getOutName(),mapToLines(processedPairs));
                    derivedMap.put(folder, subMap);
                }
                
                if (!isUncompress) {
                    FileUtils.deleteQuietly(useFile);
                }
            }
        }
        
        return derivedMap;
    }
    
    // The normals are organized basically as the inverse of the data
    // DATA: station/var.csv
    // NORMALS: NORMAL_VAR/station.csv
    private static File getNormalFile(String baseDir, String dataFolder, String extension) {
        String path = String.format(PATH_FORMAT, Utils.conformDirectoryString(baseDir), dataFolder, extension);
        return new File(path);
    }
    
    private static SortedMap<String, Float> linesToMap(List<String> lines) {
        SortedMap<String, Float> map = new TreeMap<>();
        
        for (String line : lines) {
            String[] pair = line.split(",");
            
            // round any non-int numeric input
            map.put(pair[0], Float.parseFloat(pair[1]));
        }
        
        return map;
    }
    
    private static Set<String> mapToLines(Map<String, Float> map) {
        Set<String> set = new TreeSet<>();
        
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            set.add(String.format(LINE_FORMAT, entry.getKey(), Math.round(entry.getValue())));
        }
        
        return set;
    }
    
}
