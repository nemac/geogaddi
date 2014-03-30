package org.nemac.geogaddi.config;

import com.amazonaws.auth.BasicAWSCredentials;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface PropertiesManager {
    
    PropertiesManager build() throws FileNotFoundException, IOException;

    String getBucketName();

    BasicAWSCredentials getCredentials();

    int[] getDataIdxArr();

    String getDestinatonDir();

    String getDumpDir();

    List<String> getFetchUrls();

    int getFileIdx();

    int getFileWhiteListIdx();

    String getFileWhiteListSource();

    int getFolderIdx();

    int getFolderWhiteListIdx();

    String getFolderWhiteListSource();

    String getIntegratorSourceDir();

    List<String> getParcelerSources();

    boolean isFetcherEnabled();

    boolean isFetcherUncompress();

    boolean isIntegratorCleanSource();

    boolean isIntegratorEnabled();

    boolean isOverride();

    boolean isParcelerCleanDestination();

    boolean isParcelerCleanSource();

    boolean isParcelerEnabled();

    boolean isParcelerExistingFromIntegrator();

    boolean isParcelerUncompress();

    boolean isUseAll();
    
}
