package org.nemac.geogaddi.integrate;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;

public class IntegratorProgressListener implements ProgressListener {

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        System.out.println("... transferred bytes: " + progressEvent.getBytesTransferred());
    }

}
