package org.nemac.geogaddi.config;

import com.amazonaws.auth.BasicAWSCredentials;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.nemac.geogaddi.config.element.TransformationProperty;

public interface PropertiesManager {
    
    PropertiesManager build() throws FileNotFoundException, IOException;
    
    List<TransformationProperty> getTransformations();
    
    String getDeriverSourceDir();
    
    boolean isDeriverEnabled();

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

    boolean isIntegratorCleanSource();

    boolean isIntegratorEnabled();

    boolean isParcelerCleanDestination();

    boolean isParcelerCleanSource();

    boolean isParcelerEnabled();

    boolean isParcelerExistingFromIntegrator();
    
    boolean isUncompress();

    boolean isUseAll();
    
    boolean isQuiet();
    
}
