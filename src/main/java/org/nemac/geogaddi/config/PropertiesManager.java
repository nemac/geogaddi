/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nemac.geogaddi.config;

import com.amazonaws.auth.BasicAWSCredentials;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface PropertiesManager {
    PropertiesManager build() throws FileNotFoundException, IOException;

    boolean isOverride();

    boolean isUseAll();

    boolean isFetcherEnabled();

    boolean isFetcherUncompress();

    List<String> getFetchUrls();

    String getDumpDir();

    boolean isParcelerEnabled();

    boolean isParcelerUncompress();

    boolean isParcelerCleanSource();

    boolean isParcelerCleanDestination();

    boolean isParcelerExistingFromIntegrator();

    List<String> getParcelerSources();

    String getDestinatonDir();

    String getWhiteListSource();

    int getWhiteListIdx();

    int getFolderIdx();

    int getFileIdx();

    int[] getDataIdxArr();

    boolean isIntegratorEnabled();

    boolean isIntegratorCleanSource();

    String getIntegratorSourceDir();

    BasicAWSCredentials getCredentials();

    String getBucketName();
}
