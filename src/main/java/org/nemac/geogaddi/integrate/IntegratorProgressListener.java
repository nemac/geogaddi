/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nemac.geogaddi.integrate;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;

/**
 *
 * @author richdallas
 */
public class IntegratorProgressListener implements ProgressListener {

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        System.out.println("... transferred bytes: " + progressEvent.getBytesTransferred());
    }

}
