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
import org.nemac.geogaddi.config.element.TransformationProperty;
import org.nemac.geogaddi.derive.Deriver;
import org.nemac.geogaddi.fetch.Fetcher;
import org.nemac.geogaddi.integrate.Integrator;
import org.nemac.geogaddi.parcel.Parceler;
import org.nemac.geogaddi.parcel.summary.Summarizer;
import org.nemac.geogaddi.write.Writer;

public class Geogaddi {

    private static PropertiesManager props;

    // command-line util 
    public static void main(String args[]) throws InterruptedException, java.text.ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        long start = System.currentTimeMillis();

        // command-line args
        Options options = new Options();
        
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
            
            // get PropertiesManager for JSON or Simple types
            props = PropertiesManagerFactory.createPropertyManager(managerType, cmd.getOptionValue(managerType.getArgValue())).build();

            boolean all = props.isUseAll();
            boolean fetch = props.isFetcherEnabled();
            boolean parcel = props.isParcelerEnabled();
            boolean integrate = props.isIntegratorEnabled();
            boolean derive = props.isDeriverEnabled() && cmd.hasOption("j"); // currently only implemented for JSON-based properties
            
            boolean uncompress = props.isUncompress();

            // fetcher
            boolean cleanFetcherSource = props.isParcelerCleanSource();
            boolean cleanDestinationBeforeWrite = props.isParcelerCleanDestination();

            boolean cleanIntegratorSource = props.isIntegratorCleanSource();

            boolean quiet = props.isQuiet();
            
            // TODO: implement these
            boolean existingSourcesFromIntegrator = props.isParcelerExistingFromIntegrator();

            List<String> csvSources = new ArrayList<String>();
            
            // TODO: make summarizer more sophisticated so as not to clobber the summary on using deriver only
            Summarizer summarizer = new Summarizer();

            if (all || fetch) {
                csvSources = Fetcher.multiFetch(props.getFetchUrls(), props.getDumpDir(), uncompress, quiet);
            }

            if (all || parcel) {
                // check if there is output from the previous step
                if (csvSources.isEmpty()) {
                    csvSources = props.getParcelerSources();
                }

                if (cleanDestinationBeforeWrite) {
                    File destDir = new File(props.getDestinatonDir());

                    if (cleanDestinationBeforeWrite && destDir.exists()) {
                        if (!quiet) System.out.println("Cleaning directory " + props.getDestinatonDir());
                        FileUtils.cleanDirectory(destDir);
                        if (!quiet) System.out.println("... directory cleaned");
                    }
                }
                
                for (String csvSource : csvSources) {
                    Map<String, Map<String, Set<String>>> parcelMap = Parceler.parcel(csvSource,
                            props.getDestinatonDir(), props.getFolderWhiteListSource(), props.getFolderWhiteListIdx(),
                            props.getFolderIdx(), props.getFileWhiteListSource(), props.getFileWhiteListIdx(), props.getFileIdx(), 
                            props.getDataIdxArr(), uncompress, quiet);

                    Writer.write(parcelMap, props.getDestinatonDir(), uncompress, summarizer, quiet);

                    // clean up downloaded file
                    if (cleanFetcherSource) {
                        FileUtils.deleteQuietly(new File(csvSource));
                    }
                }
            }
            
            if ((all && cmd.hasOption("j")) || derive) {
                for (TransformationProperty transformation : props.getTransformations()) {
                    Map<String, Map<String, Set<String>>> derived = Deriver.derive(props.getDeriverSourceDir(), transformation, uncompress);
                    Writer.write(derived, props.getDestinatonDir(), uncompress, summarizer, quiet);
                }
            }

            if (all || parcel || derive) {
                // write out summary
                FileUtils.writeStringToFile(new File(props.getDestinatonDir() + "/summary.json"), summarizer.jsonSummary());
            }
            
            if (all || integrate) {
                String destDir;
                if (!all && !parcel) {
                    destDir = props.getIntegratorSourceDir();
                } else {
                    destDir = props.getDestinatonDir();
                }

                Integrator.integrate(props.getCredentials(), destDir, props.getBucketName(), cleanDestinationBeforeWrite, uncompress, quiet);

                 if (cleanIntegratorSource) {
                     if (!quiet) System.out.println("Deleting " + destDir);
                     FileUtils.deleteDirectory(new File(destDir));
                 }
            }
            
            
        } catch (ParseException | IOException ex) {
            Logger.getLogger(Geogaddi.class.getName()).log(Level.SEVERE, null, ex);
        }

        long end = System.currentTimeMillis();
        System.out.println("Processed in " + (end - start) / 1000f + " seconds");
    }
}
