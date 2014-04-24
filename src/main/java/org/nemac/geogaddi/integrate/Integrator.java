package org.nemac.geogaddi.integrate;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import org.nemac.geogaddi.GeogaddiOptionDriver;
import org.nemac.geogaddi.config.options.IntegratorOptions;

import java.io.File;

public class Integrator extends GeogaddiOptionDriver {
    private static final IntegratorOptions INTEGRATOR_OPTIONS = geogaddiOptions.getIntegratorOptions();

    public static void integrate(boolean clean) throws AmazonClientException, InterruptedException  {
        BasicAWSCredentials credentials = new BasicAWSCredentials(INTEGRATOR_OPTIONS.getAwsAccessKeyId(), INTEGRATOR_OPTIONS.getAwsSecretKey());

        String bucketName = INTEGRATOR_OPTIONS.getBucketName();

        if (!geogaddiOptions.isQuiet()) System.out.println("Transferring content to S3");

        AmazonS3 s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1)); // TODO: parameterize?

        if (!s3.doesBucketExist(bucketName)) {
            s3.createBucket(bucketName);
        }

        if (clean) {
            BucketDestroy.emptyBucket(s3, bucketName, geogaddiOptions.isQuiet());
        }

        TransferManager transfer = new TransferManager(s3);
        
        MultipleFileUpload upload;
        String sourceDir = INTEGRATOR_OPTIONS.getSourceDir();
        if (geogaddiOptions.isUncompress()) {
            upload  = transfer.uploadDirectory(bucketName, null, new File(sourceDir), true);
        } else {
            ObjectMetadataProvider metadataProvider = new ObjectMetadataProvider() {
                @Override
                public void provideObjectMetadata(File file, ObjectMetadata metadata) {
                    if (file.getName().contains(".gz")) {
                        metadata.setHeader("content-encoding", "gzip");
                        metadata.setContentType("text/plain");
                    }
                    
                }
            };
            
            upload = transfer.uploadDirectory(bucketName, null, new File(sourceDir), true, metadataProvider);
        }
        
        if (!geogaddiOptions.isQuiet()) upload.addProgressListener(new IntegratorProgressListener());
        
        upload.waitForCompletion();
        
        transfer.shutdownNow();

        if (!geogaddiOptions.isQuiet()) System.out.println("... content transferred to S3");
    }
}
