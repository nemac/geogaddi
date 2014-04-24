package org.nemac.geogaddi.derive;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.nemac.geogaddi.GeogaddiOptionDriver;
import org.nemac.geogaddi.derive.transformation.Transformation;
import org.nemac.geogaddi.derive.transformation.TransformationFactory;
import org.nemac.geogaddi.exception.TransformationNotFoundException;
import org.nemac.geogaddi.config.options.DeriverOptions;
import org.nemac.geogaddi.config.options.TransformationOption;
import org.nemac.geogaddi.write.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class Deriver extends GeogaddiOptionDriver {
    private static final String LINE_FORMAT = "%s,%s";
    private static final String PATH_FORMAT = "%s/%s.%s";
    private static final DeriverOptions DERIVER_OPTIONS = geogaddiOptions.getDeriverOptions();

    public static Map<String, Map<String, Set<String>>> derive(TransformationOption transformationOption) throws TransformationNotFoundException, IOException, ParseException {
        if (!geogaddiOptions.isQuiet()) System.out.println("Derived product being generated using " + transformationOption.getName());
        Map<String, Map<String, Set<String>>> derivedMap = new TreeMap<>();
        
        Transformation transformation = TransformationFactory.createTransformation(transformationOption.getTransformationSourceLib(), transformationOption.getTransformation());

        String[] extensions;
        
        if (geogaddiOptions.isUncompress()) {
            String[] unExt = {"csv"};
            extensions = unExt;
        } else {
            String[] comExt = {"csv.gz"};
            extensions = comExt;
        }
        
        Iterator<File> files = FileUtils.iterateFiles(new File(DERIVER_OPTIONS.getSourceDir()), extensions, true);
        
        while (files.hasNext()) {
            File file = files.next();
            
            if (Utils.removeExtension(file.getName()).equals(transformationOption.getFile())) {
                                
                File useFile = file;
                if (!geogaddiOptions.isUncompress()) { // is working with compressed files, so uncompress temporarily
                    useFile = new File(Utils.uncompress(file.getCanonicalPath(), false));
                }
                                
                String folder = FilenameUtils.getBaseName(useFile.getParent());
                SortedMap<String, Float> dataPairs = linesToMap(FileUtils.readLines(useFile));
                
                File normalFile = getNormalFile(transformationOption.getNormalDir(), folder, extensions[0]);

                if (normalFile.exists()) { // only use if there is a corresponding normal                    
                    if (!geogaddiOptions.isUncompress()) {
                        normalFile = new File(Utils.uncompress(normalFile.getCanonicalPath(), false));
                    }
                    
                    SortedMap<String, Float> normalPairs = linesToMap(FileUtils.readLines(normalFile));
                    SortedMap<String, Float> processedPairs = transformation.process(dataPairs, normalPairs);
                    
                    Map<String, Set<String>> subMap = new TreeMap<>();
                    subMap.put(transformationOption.getOutName(),mapToLines(processedPairs));
                    derivedMap.put(folder, subMap);
                    
                } else {
                    if (!geogaddiOptions.isQuiet()) System.out.println("... normal file " + normalFile.getPath() + " was not found, derived product will not be generated");
                }
                
                if (!geogaddiOptions.isUncompress()) {
                    FileUtils.deleteQuietly(useFile);
                    FileUtils.deleteQuietly(normalFile);
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
        System.out.println(path);
        return new File(path);
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
        
        // round non-integers before writing
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            set.add(String.format(LINE_FORMAT, entry.getKey(), Math.round(entry.getValue())));
        }
        
        return set;
    }
    
}
