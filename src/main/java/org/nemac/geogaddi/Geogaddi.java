package org.nemac.geogaddi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.nemac.geogaddi.config.PropertiesManager;
import org.nemac.geogaddi.fetch.Fetcher;
import org.nemac.geogaddi.integrate.Integrator;
import org.nemac.geogaddi.parcel.Parceler;
import org.nemac.geogaddi.write.Writer;

public class Geogaddi {
    
    private static PropertiesManager propertiesManager;
    
    // command-line util
    public static void main(String args[]) {
    	long start = System.currentTimeMillis();
    	
    	// command-line args
        Options options = new Options();
        options.addOption("a", false, "Runs the fetch, transform, and integrator operations");
        options.addOption("f", false, "Runs only the fetch operation");
        options.addOption("t", false, "Runs only the transform operation");
        options.addOption("c", false, "Does an clean-write, default");
        options.addOption("u", false, "Work with unzipped files");
        options.addOption("i", false, "Runs only the integrator operation");
        Option propertyArg = new Option("p", true, "Defines the override properties for Geogaddi operations in Java Properties");
        propertyArg.setArgs(1);
        options.addOption(propertyArg);
        Option jsonArg = new Option("j", true, "Defines the override properties for Geogaddi operations in JSON format");
        jsonArg.setArgs(1);
        options.addOption(jsonArg);
        
        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("p")) {
            	String propertiesLocation = cmd.getOptionValues("p")[0];
                propertiesManager = PropertiesManager.createFromPropertiesFile(propertiesLocation);
            } else if (cmd.hasOption("j")) {
            	String propertiesLocation = cmd.getOptionValues("j")[0];
            	propertiesManager = PropertiesManager.createFromJSON(propertiesLocation);
            }
            
            boolean all = cmd.hasOption("a");
            boolean fetch =  cmd.hasOption("f");
            boolean transform = cmd.hasOption("t");
            boolean clean = cmd.hasOption("c");
            boolean unzip = cmd.hasOption("u");
            boolean integrate = cmd.hasOption("i");
            
            List<String> csvSources = new ArrayList<String>();
            
            if (all || fetch) {
                csvSources = Fetcher.multiFetch(propertiesManager.getFetchUrls(), propertiesManager.getDumpDir(), unzip);
            }
            
            if (all || transform) {
            	// check if there is output from the previous step
            	if (csvSources.isEmpty()) {
            		csvSources = propertiesManager.getParcelerSources();
            	}
            	
            	if (clean) {
            		File destDir = new File(propertiesManager.getDestinatonDir());
            		
            		if (clean && destDir.exists()) {
            			System.out.println("Cleaning directory " + propertiesManager.getDestinatonDir());
            			FileUtils.cleanDirectory(destDir);
            			System.out.println("... directory cleaned");
            		}
            	}
            	
            	for (String csvSource : csvSources) {
            		Map<String, Map<String, Set<String>>> parcelMap = Parceler.parcel(csvSource, 
            				propertiesManager.getDestinatonDir(), propertiesManager.getWhiteListSource(), propertiesManager.getWhiteListIdx(), 
            				propertiesManager.getFolderIdx(), propertiesManager.getFileIdx(), propertiesManager.getDataIdxArr(), unzip);
            		
            		Writer.write(parcelMap, propertiesManager.getDestinatonDir());
            	}
            }
            
            if (all || integrate) {
            	String destDir;
            	if (!all && !transform) {
            		destDir = propertiesManager.getIntegratorSourceDir();
            	} else {
            		destDir = propertiesManager.getDestinatonDir();
            	}
            	Integrator.integrate(propertiesManager.getCredentials(), destDir, propertiesManager.getBucketName(), clean);
            }
            
        } catch (ParseException | IOException ex) {
            Logger.getLogger(Geogaddi.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        long end = System.currentTimeMillis();
        System.out.println("Processed in " + (end-start)/1000f + " seconds");
    }
}
