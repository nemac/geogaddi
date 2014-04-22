package org.nemac.geogaddi;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.nemac.geogaddi.config.GeogaddiOptionsFactory;
import org.nemac.geogaddi.config.PropertiesManager;
import org.nemac.geogaddi.config.PropertyManagerTypeEnum;
import org.nemac.geogaddi.derive.Deriver;
import org.nemac.geogaddi.exception.PropertiesParseException;
import org.nemac.geogaddi.fetch.Fetcher;
import org.nemac.geogaddi.integrate.Integrator;
import org.nemac.geogaddi.model.GeogaddiOptions;
import org.nemac.geogaddi.model.TransformationOption;
import org.nemac.geogaddi.parcel.Parceler;
import org.nemac.geogaddi.parcel.summary.Summarizer;
import org.nemac.geogaddi.write.Writer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Geogaddi {

//    private static PropertiesManager props;

    private static GeogaddiOptions;


    // command-line util
    public static void main(String args[]) throws InterruptedException, java.text.ParseException, ClassNotFoundException, InstantiationException, IllegalAccessException, PropertiesParseException {
        long start = System.currentTimeMillis();

        // command-line args
        Options options = new Options();

        Option propertyArg = new Option("p", true, "Defines the override properties for Geogaddi operations in Java Properties");
        propertyArg.setArgs(1);
        propertyArg.setOptionalArg(true);
        options.addOption(propertyArg);

        Option jsonArg = new Option("j", true, "Defines the override properties for Geogaddi operations in JSON_PROPS format");
        jsonArg.setArgs(1);
        jsonArg.setOptionalArg(true);
        options.addOption(jsonArg);

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            PropertyManagerTypeEnum managerType;
            if (cmd.hasOption("p")) {
                managerType = PropertyManagerTypeEnum.JAVA_PROPS;
            } else if (cmd.hasOption("j")) {
                managerType = PropertyManagerTypeEnum.JSON_PROPS;
            } else {
                throw new MissingArgumentException("Must specify the properties file type with the \"p\" or \"j\" argument.");
            }

            // get PropertiesManager for JSON_PROPS or Simple types
            String propertiesSource = cmd.getOptionValue(managerType.getArgValue());
            GeogaddiOptions geogaddiOptions = GeogaddiOptionsFactory.instanceOf(managerType, propertiesSource);

            boolean allEnabled = geogaddiOptions.isUseAll();
            boolean fetchEnabled = geogaddiOptions.getFetcherOptions().isEnabled();
            boolean parcelEnabled = geogaddiOptions.getParcelerOptions().isEnabled();
            boolean integrateEnabled = geogaddiOptions.getIntegratorOptions().isEnabled();
            boolean deriverEnabled = geogaddiOptions.getDeriverOptions().isEnabled() && cmd.hasOption("j");

            boolean uncompress = geogaddiOptions.isUncompress();

            boolean cleanFetcherSource = geogaddiOptions.getParcelerOptions().isCleanSource();
            boolean cleanDestinationBeforeWrite = geogaddiOptions.getParcelerOptions().isCleanDestination();

            boolean cleanIntegratorSource = geogaddiOptions.getIntegratorOptions().isCleanSource();

            boolean quiet = geogaddiOptions.isQuiet();

            // TODO: implement these
            boolean existingSourcesFromIntegrator = geogaddiOptions.getParcelerOptions().isExistingFromIntegrator();

            List<String> csvSources = new ArrayList<String>();

            // TODO: make summarizer more sophisticated so as not to clobber the summary on using deriver only
            Summarizer summarizer = new Summarizer();

            if (allEnabled || fetchEnabled) {
//                csvSources = Fetcher.multiFetch(props.getFetchUrls(), props.getDumpDir(), uncompress, quiet);
                csvSources = Fetcher.multiFetch(geogaddiOptions.getFetcherOptions(), geogaddiOptions.isUncompress(), geogaddiOptions.isQuiet());
            }

            if (allEnabled || parcelEnabled) {
                // check if there is output from the previous step
                if (csvSources.isEmpty()) {
                    csvSources = geogaddiOptions.getParcelerOptions().getSourceCSVs();
                }

                if (cleanDestinationBeforeWrite) {
                    File destDir = new File(geogaddiOptions.getParcelerOptions().getOutputDir());

                    if (cleanDestinationBeforeWrite && destDir.exists()) {
                        if (!quiet) System.out.println("Cleaning directory " + destDir.getPath());
                                FileUtils.cleanDirectory(destDir);
                        if (!quiet) System.out.println("... directory cleaned");
                    }
                }

                for (String csvSource : csvSources) {
                    Map<String, Map<String, Set<String>>> parcelMap = Parceler.parcel(String csvSource, geogaddiOptions.getParcelerOptions(), uncompress, quiet);

                    Writer.write(parcelMap, props.getDestinatonDir(), uncompress, summarizer, quiet);

                    // clean up downloaded file
                    if (cleanFetcherSource) {
                        FileUtils.deleteQuietly(new File(csvSource));
                    }
                }
            }

            if ((allEnabled && cmd.hasOption("j")) || deriverEnabled) {
                for (TransformationOption transformation : props.getTransformationOptions()) {
                    Map<String, Map<String, Set<String>>> derived = Deriver.derive(props.getDeriverSourceDir(), transformation, uncompress);
                    Writer.write(derived, props.getDestinatonDir(), uncompress, summarizer, quiet);
                }
            }

            if (allEnabled || parcelEnabled || deriverEnabled) {
                // write out summary
                FileUtils.writeStringToFile(new File(props.getDestinatonDir() + "/summary.json"), summarizer.jsonSummary());
            }

            if (allEnabled || integrateEnabled) {
                String destDir;
                if (!allEnabled && !parcelEnabled) {
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
