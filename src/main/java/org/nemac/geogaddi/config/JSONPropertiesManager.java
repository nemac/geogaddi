//package org.nemac.geogaddi.config;
//
//import com.amazonaws.auth.BasicAWSCredentials;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import org.apache.commons.io.FileUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.nemac.geogaddi.model.TransformationOptions;
//import org.nemac.geogaddi.model.TransformationOptionsBuilder;
//import org.nemac.geogaddi.write.Utils;
//
//public class JSONPropertiesManager extends AbstractPropertiesManager {
//    private static final String DEFAULT_PROPERTIES_PATH = "geogaddi.json";
//
//    JSONPropertiesManager(final String propertiesSource) {
//        this.propertiesSource = propertiesSource == null ? DEFAULT_PROPERTIES_PATH : propertiesSource;
//    }
//
//    @Override
//    public PropertiesManager build() throws FileNotFoundException, IOException {
//        String input = FileUtils.readFileToString(new File(propertiesSource));
//        JSONObject rootNode = new JSONObject(input);
//
//        quiet = rootNode.getBoolean("quiet");
//        useAll = rootNode.getBoolean("useAll");
//        uncompress = rootNode.getBoolean("uncompress");
//
//        // fetcher
//        JSONObject fetcherNode = rootNode.getJSONObject("fetcher");
//        fetcherEnabled = fetcherNode.getBoolean("enabled");
//        JSONArray fetcherUrlsJSON = fetcherNode.getJSONArray("source");
//        fetcherUrls = new ArrayList<String>();
//        for (int i = 0; i < fetcherUrlsJSON.length(); i++) {
//            fetcherUrls.add(fetcherUrlsJSON.getString(i));
//        }
//        fetcherDumpDir = Utils.conformDirectoryString(fetcherNode.getString("dumpDir"));
//
//        // parceler
//        JSONObject parcelerNode = rootNode.getJSONObject("parceler");
//        parcelerEnabled = parcelerNode.getBoolean("enabled");
//        parcelerCleanSource = parcelerNode.getBoolean("cleanSource");
//        parcelerCleanDestination = parcelerNode.getBoolean("cleanDestination");
//        JSONArray sourceCsvs = parcelerNode.getJSONArray("sourceCsv");
//        parcelerSources = new ArrayList<String>();
//        for (int i = 0; i < sourceCsvs.length(); i++) {
//            parcelerSources.add(sourceCsvs.getString(i));
//        }
//        parcelerDestinatonDir = Utils.conformDirectoryString(parcelerNode.getString("outputDir"));
//        parcelerFolderWhiteListSource = parcelerNode.getString("folderWhiteList");
//        parcelerFolderWhiteListIdx = (int) parcelerNode.getLong("folderWhiteListIndex");
//        parcelerFolderIdx = (int) parcelerNode.getLong("folderIndex");
//        parcelerFileWhiteListSource = parcelerNode.getString("fileWhiteList");
//        parcelerFileWhiteListIdx = (int) parcelerNode.getLong("fileWhiteListIndex");
//        parcelerFileIdx = (int) parcelerNode.getLong("fileIndex");
//
//        JSONArray dataIndexes = parcelerNode.getJSONArray("dataIndex");
//        int[] dataIdxArr = new int[dataIndexes.length()];
//
//        for (int i = 0; i < dataIndexes.length(); i++) {
//            dataIdxArr[i] = (int) dataIndexes.getLong(i);
//        }
//
//        parcelerDataIdxArr = dataIdxArr;
//
//        // integrator
//        JSONObject integratorNode = rootNode.getJSONObject("integrator");
//        integratorEnabled = integratorNode.getBoolean("enabled");
//        integratorCleanSource = integratorNode.getBoolean("cleanSource");
//        integratorSourceDir = Utils.conformDirectoryString(integratorNode.getString("sourceDir"));
//        integratorCredentials = new BasicAWSCredentials(integratorNode.getString("awsAccessKeyId"), integratorNode.getString("awsSecretKey"));
//        integratorBucketName = integratorNode.getString("bucketName");
//
//        // deriver
//        JSONObject deriverNode = rootNode.getJSONObject("deriver");
//        deriverEnabled = deriverNode.getBoolean("enabled");
//        deriverSourceDir = deriverNode.getString("sourceDir");
//
//        transformations = new ArrayList<>();
//        JSONArray trans = deriverNode.getJSONArray("transformations");
//
//        for (int i = 0; i < trans.length(); i++) {
//            JSONObject ob = trans.getJSONObject(i);
//            TransformationOptions p = new TransformationOptionsBuilder()
//                    .setName(ob.getString("name"))
//                    .setTransformationSourceLib(ob.getString("transformationSourceLib"))
//                    .setTransformation(ob.getString("transformation"))
//                    .setFolder(ob.getString("folder"))
//                    .setFile(ob.getString("file"))
//                    .setNormalSource(ob.getString("normalDir"))
//                    .setDateIndex(ob.getInt("dateIndex"))
//                    .setDataIndex(ob.getInt("valueIndex"))
//                    .setOutName(ob.getString("outName"))
//                    .createTransformationOptions();
//            transformations.add(p);
//        }
//
//        return this;
//    }
//}
