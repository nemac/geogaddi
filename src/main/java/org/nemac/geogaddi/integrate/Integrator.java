package org.nemac.geogaddi.integrate;

import com.amazonaws.AmazonClientException;
import java.io.File;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.amazonaws.services.s3.transfer.TransferManager;

public class Integrator {
    
    public static void integrate(AWSCredentials credentials, String sourceDir, String bucketName, boolean clean, boolean uncompressed, boolean quiet) throws AmazonClientException, InterruptedException  {
        if (!quiet) System.out.println("Transferring content to S3");

        AmazonS3 s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1)); // TODO: parameterize?

        if (!s3.doesBucketExist(bucketName)) {
            s3.createBucket(bucketName);
        }

        if (clean) {
            BucketDestroy.emptyBucket(s3, bucketName, quiet);
        }

        TransferManager transfer = new TransferManager(s3);
        
        MultipleFileUpload upload;
        if (uncompressed) {
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
        
        if (!quiet) upload.addProgressListener(new IntegratorProgressListener());
        
        upload.waitForCompletion();
        
        transfer.shutdownNow();

        if (!quiet) System.out.println("... content transferred to S3");
    }
}
