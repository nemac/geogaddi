package org.nemac.geogaddi.derive;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.nemac.geogaddi.GeogaddiOptionDriver;
import org.nemac.geogaddi.config.options.DeriverOptions;
import org.nemac.geogaddi.config.options.TransformationOption;
import org.nemac.geogaddi.derive.transformation.AbstractTransformation;
import org.nemac.geogaddi.derive.transformation.TransformationFactory;
import org.nemac.geogaddi.exception.TransformationNotFoundException;
import org.nemac.geogaddi.write.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nemac.geogaddi.parcel.summary.DataElement;
import org.nemac.geogaddi.parcel.summary.Summarizer;
import org.nemac.geogaddi.parcel.summary.SummaryState;
import org.nemac.geogaddi.write.Writer;

public class Deriver extends GeogaddiOptionDriver {
    private static final String LINE_FORMAT = "%s,%s";
    private static final String NORMAL_PATH_FORMAT = "%s/%s.%s";
    private static final String OUT_PATH_FORMAT = "%s/%s/%s.csv";
    private static final DeriverOptions DERIVER_OPTIONS = geogaddiOptions.getDeriverOptions();

    public static void derive(TransformationOption transformationOption, Summarizer summarizer, String destDir) throws TransformationNotFoundException, IOException, ParseException {
        if (!geogaddiOptions.isQuiet()) System.out.println("Derived product being generated using " + transformationOption.getName());
        
        AbstractTransformation transformation = TransformationFactory.createTransformation(transformationOption.getTransformationSourceLib(), transformationOption.getTransformation());

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
            try {
                File file = files.next();
                
                String station = Utils.removeExtension(file.getName());
                
                if (station.equals(transformationOption.getFile())) {
                    String folder = FilenameUtils.getBaseName(file.getParent());
                    String fileName = FilenameUtils.getBaseName(file.getName());

                    // check from summary if files need to be processed
                    DataElement element = summarizer.getElement(folder, fileName);
                    
                    // new or backlog - write as normal
                    File useFile = file;
                    if (!geogaddiOptions.isUncompress()) { // is working with compressed files, so uncompress temporarily
                        useFile = new File(Utils.uncompress(file.getCanonicalPath(), false));
                    }
                    
                    File normalFile = getNormalFile(transformationOption.getNormalDir(), folder, extensions[0]);

                    String writeFilename = String.format(OUT_PATH_FORMAT, DERIVER_OPTIONS.getSourceDir(), folder, transformationOption.getOutName(), "csv");
                    File writeFile = new File(writeFilename);
                    
                    boolean process = !writeFile.exists() || (element != null && (element.getState() == SummaryState.NEW.getValue() || element.getState() == SummaryState.BACKLOG.getValue() || element.getState() == SummaryState.APPEND.getValue()));
                    
                    if (process) {
                        if (normalFile.exists()) { // only use if there is a corresponding normal
                            SortedMap<String, Float> dataPairs = linesToMap(FileUtils.readLines(useFile));
                            
                            if (!geogaddiOptions.isUncompress()) {
                                normalFile = new File(Utils.uncompress(normalFile.getCanonicalPath(), false));
                            }
                            
                            SortedMap<String, Float> normalPairs = linesToMap(FileUtils.readLines(normalFile));
                            SortedMap<String, Float> processedPairs = transformation.process(dataPairs, normalPairs);
                            
                            Set<String> lines = mapToLines(processedPairs);
                            
                            // write out
                            FileUtils.writeLines(writeFile, lines);
                            summarizer.addElement(folder, transformationOption.getOutName(), lines, SummaryState.OTHER);
                            
                        } else {
                            if (!geogaddiOptions.isQuiet()) System.out.println("... normal file " + normalFile.getPath() + " was not found, derived product will not be generated");
                        }
                    } else {
                        if (writeFile.exists()) {
                            // add to summary in case the file already exists but wasn't created or appended to
                            summarizer.addElement(folder, transformationOption.getOutName(), new HashSet(FileUtils.readLines(writeFile)), SummaryState.OTHER);
                        }
                    }

                    if (!geogaddiOptions.isUncompress()) {
                        FileUtils.deleteQuietly(useFile);
                        FileUtils.deleteQuietly(normalFile);
                        Utils.compress(writeFile.getCanonicalPath());
                    }
                } 
            } catch (Exception ex) {
                System.out.println("ERROR -------------------");
                System.out.println(ex.getMessage());
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // The normals are organized basically as the inverse of the data
    // DATA: station/var.csv
    // NORMALS: NORMAL_VAR/station.csv
    private static File getNormalFile(String baseDir, String dataFolder, String extension) {
        String path = String.format(NORMAL_PATH_FORMAT, Utils.conformDirectoryString(baseDir), dataFolder, extension);
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
