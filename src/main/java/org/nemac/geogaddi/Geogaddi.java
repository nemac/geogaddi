package org.nemac.geogaddi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.nemac.geogaddi.config.PropertiesManager;
import org.nemac.geogaddi.fetch.Fetcher;
import org.nemac.geogaddi.parcel.Parceler;

public class Geogaddi {
    
    private static PropertiesManager propertiesManager;
    
    // command-line util
    public static void main(String args[]) {
    	// command-line args
        Options options = new Options();
        options.addOption("a", false, "Runs both the fetch and the transform operations");
        options.addOption("f", false, "Runs only the fetch operation");
        options.addOption("t", false, "Runs only the transform operation");
        Option propertyArg = new Option("p", true, "Defines the override properties for Geogaddi operations");
        propertyArg.setArgs(1);
        options.addOption(propertyArg);
        
        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String propertiesLocation = cmd.getOptionValues("p")[0];
            propertiesManager = new PropertiesManager(propertiesLocation);
            
            boolean all = cmd.hasOption("a");
            boolean fetch =  cmd.hasOption("f");
            boolean transform = cmd.hasOption("t");
            
            List<String> csvSources = new ArrayList<String>();
            
            if (all || fetch) {
                String fetchUrlPathProperty = propertiesManager.getProperty("fetcher.source.url");
                String[] fetchUrls = fetchUrlPathProperty.replace(" ", "").split(",");

                List<String> fetchUrlPaths = Arrays.asList(fetchUrls);                
                csvSources = Fetcher.multiFetch(fetchUrlPaths, propertiesManager.getProperty("fetcher.dump.dir"));
            }
            
            if (all || transform) {
            	// check if there is output from the previous step
            	if (csvSources.isEmpty()) {
            		String sourceCSVProperty = propertiesManager.getProperty("parceler.source.csv");
            		String[] sourceCSVs = sourceCSVProperty.replace(" ", "").split(",");
            		csvSources = Arrays.asList(sourceCSVs);
            	}
            	
            	String destinatonDir = propertiesManager.getProperty("parceler.output.dir");
            	String whiteListSource = propertiesManager.getProperty("parceler.parcel.whitelist.source");
            	int whiteListIdx = new Integer(propertiesManager.getProperty("parceler.parcel.whitelist.filter.index"));
            	int folderIdx = new Integer(propertiesManager.getProperty("parceler.parce.folder.index"));
            	int fileIdx = new Integer(propertiesManager.getProperty("parceler.parce.file.index"));
            	String dataIdxProperty = propertiesManager.getProperty("parceler.parce.data.index");
            	String[] dataIdxStrArr = dataIdxProperty.replace(" ", "").split(",");
            	int[] dataIdxArr = new int[dataIdxStrArr.length];
            	
            	for (int i = 0; i < dataIdxStrArr.length; i++) {
            		dataIdxArr[i] = Integer.parseInt(dataIdxStrArr[i]);
            	}
            	
                Parceler.parcel(csvSources, destinatonDir, whiteListSource, whiteListIdx, folderIdx, fileIdx, dataIdxArr);
            }
            
        } catch (ParseException | IOException ex) {
            Logger.getLogger(Geogaddi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
