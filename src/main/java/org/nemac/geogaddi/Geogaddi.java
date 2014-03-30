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
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.nemac.geogaddi.config.PropertiesManager;
import org.nemac.geogaddi.config.PropertiesManagerFactory;
import org.nemac.geogaddi.config.PropertyManagerTypeEnum;
import org.nemac.geogaddi.fetch.Fetcher;
import org.nemac.geogaddi.integrate.Integrator;
import org.nemac.geogaddi.parcel.Parceler;
import org.nemac.geogaddi.parcel.summary.Summarizer;
import org.nemac.geogaddi.write.Writer;

public class Geogaddi {

    private static PropertiesManager props;

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
        propertyArg.setOptionalArg(true);
        options.addOption(propertyArg);
        
        Option jsonArg = new Option("j", true, "Defines the override properties for Geogaddi operations in JSON format");
        jsonArg.setArgs(1);
        jsonArg.setOptionalArg(true);
        options.addOption(jsonArg);

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            PropertyManagerTypeEnum managerType;
            if (cmd.hasOption("p")) {
                managerType = PropertyManagerTypeEnum.SIMPLE;
            } else if (cmd.hasOption("j")) {
                managerType = PropertyManagerTypeEnum.JSON;
            } else {
                throw new MissingArgumentException("Must specify the properties file type with the \"p\" or \"j\" argument.");
            }
            
            props = PropertiesManagerFactory.createPropertyManager(managerType, cmd.getOptionValue(managerType.getArgValue())).build();

            boolean all = (props.isOverride() && props.isUseAll()) || cmd.hasOption("a");
            boolean fetch = (props.isOverride() && props.isFetcherEnabled()) || cmd.hasOption("f");
            boolean transform = (props.isOverride() && props.isParcelerEnabled()) || cmd.hasOption("t");
            boolean integrate = (props.isOverride() && props.isIntegratorEnabled()) || cmd.hasOption("i");

            // fetcher
            boolean uncompressFetcher = (props.isOverride() && props.isFetcherUncompress()) || (!props.isOverride() && cmd.hasOption("u"));
            boolean cleanFetcherSource = props.isParcelerCleanSource();
            boolean cleanDestinationBeforeWrite = (props.isOverride() && props.isParcelerCleanDestination()) || (!props.isOverride() && cmd.hasOption("c"));

            // TODO: implement these
            boolean uncompressParceler = (props.isOverride() && props.isParcelerUncompress()) || (!props.isOverride() && cmd.hasOption("u"));
            boolean existingSourcesFromIntegrator = props.isOverride() && props.isParcelerExistingFromIntegrator();
            boolean cleanIntegratorSource = props.isIntegratorCleanSource();

            List<String> csvSources = new ArrayList<String>();

            if (all || fetch) {
                csvSources = Fetcher.multiFetch(props.getFetchUrls(), props.getDumpDir(), uncompressFetcher);
            }

            if (all || transform) {
                // check if there is output from the previous step
                if (csvSources.isEmpty()) {
                    csvSources = props.getParcelerSources();
                }

                if (cleanDestinationBeforeWrite) {
                    File destDir = new File(props.getDestinatonDir());

                    if (cleanDestinationBeforeWrite && destDir.exists()) {
                        System.out.println("Cleaning directory " + props.getDestinatonDir());
                        FileUtils.cleanDirectory(destDir);
                        System.out.println("... directory cleaned");
                    }
                }

                Summarizer summarizer = new Summarizer();
                
                for (String csvSource : csvSources) {
                    Map<String, Map<String, Set<String>>> parcelMap = Parceler.parcel(csvSource,
                            props.getDestinatonDir(), props.getWhiteListSource(), props.getWhiteListIdx(),
                            props.getFolderIdx(), props.getFileIdx(), props.getDataIdxArr(), uncompressFetcher);

                    Writer.write(parcelMap, props.getDestinatonDir(), uncompressParceler, summarizer);

                    // clean up downloaded file
                    if (cleanFetcherSource) {
                        FileUtils.deleteQuietly(new File(csvSource));
                    }
                }
                
                // write out summary
                FileUtils.writeStringToFile(new File(props.getDestinatonDir() + "/summary.json"), summarizer.jsonSummary());
            }

            if (all || integrate) {
                String destDir;
                if (!all && !transform) {
                    destDir = props.getIntegratorSourceDir();
                } else {
                    destDir = props.getDestinatonDir();
                }

                Integrator.integrate(props.getCredentials(), destDir, props.getBucketName(), cleanDestinationBeforeWrite);

            	// Implement once figure out integrator blocking, etc
            	/*
                 if (cleanIntegratorSource) {
                 FileUtils.deleteDirectory(new File(destDir));
                 }
                 */
            }

        } catch (ParseException | IOException ex) {
            Logger.getLogger(Geogaddi.class.getName()).log(Level.SEVERE, null, ex);
        }

        long end = System.currentTimeMillis();
        
        //TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start);
        System.out.println("Processed in " + (end - start) / 1000f + " seconds");
    }
}
