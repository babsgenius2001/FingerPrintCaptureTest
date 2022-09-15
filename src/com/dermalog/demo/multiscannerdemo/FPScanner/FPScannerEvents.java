/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.FPScanner;

import java.util.List;

import com.dermalog.imaging.capturing.OnDetectEventData;
import com.dermalog.imaging.capturing.OnImageEventData;

/**
 *
 * @author BA07190
 */
public interface FPScannerEvents {

    void OnScannerImage(OnImageEventData oEventData);

    void OnScannerDetect(OnDetectEventData oEventData);

    void OnScannerError(Throwable oThrowable);

    void OnFingerprintsDetected(List<Fingerprint> oFingerprints);
}
