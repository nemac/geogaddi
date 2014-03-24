package org.nemac.geogaddi.integrate;

import com.amazonaws.AmazonClientException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;

/**
 * Efficient delete of version enabled bucket.
 *
 * This code will delete all keys from gives bucket and all previous versions of
 * given key. This is done in an efficient multi-threaded ExecutorService
 * environment
 *
 * @author Jeff Hicks
 * @author Maxim Veksler <maxim@vekslers.org>
 * @author Mark S. Kolich
 * http://mark.koli.ch/2010/09/recursively-deleting-large-amazon-s3-buckets.html
 * @version 0.1 - First pseudo code version, Written by Mark S. Kolich.
 * @version 0.2 - Maxim Veksler - Fix paging. Implement deletion of versioned
 * keys.
 * @version 0.3 - Jeff Hicks - Modified to empty bucket instead of delete it.
 * Takes an S3 object as input.
 *
 */
public class BucketDestroy {

    public static void emptyBucket(final AmazonS3 s3, final String bucketName) {
        // Set up a new thread pool to delete 20 objects at a time.
        ExecutorService _pool = Executors.newFixedThreadPool(20);

        // List all key in the bucket
        VersionListing versionListing = s3.listVersions(bucketName, "");
        List<S3VersionSummary> versionSummaries = versionListing.getVersionSummaries();
        while (versionSummaries != null && versionSummaries.size() > 0) {
            final CountDownLatch latch = new CountDownLatch(versionSummaries.size());
            for (final S3VersionSummary objectSummary : versionSummaries) {
                _pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        String keyName = null;
                        String versionId = null;
                        try {
                            keyName = objectSummary.getKey();
                            versionId = objectSummary.getVersionId();
                            s3.deleteVersion(new DeleteVersionRequest(bucketName, keyName, versionId));
                        } catch (AmazonClientException e) {
                            String err = ">>>> FAILED delete: (" + bucketName + "/" + keyName + "@" + versionId + ")";
                            System.err.println(err);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }

            // After sending current batch of delete tasks we block until Latch
            // reaches zero, this allows to not over populate ExecutorService tasks queue.
            try {
                latch.await();
            } catch (InterruptedException exception) {
                System.err.println(exception);
            }

            // Paging over all S3 keys...
            versionListing = s3.listNextBatchOfVersions(versionListing);
            versionSummaries = versionListing.getVersionSummaries();
        }

        _pool.shutdown();

        System.out.println("... bucket " + bucketName + " emptied");
    }
}
