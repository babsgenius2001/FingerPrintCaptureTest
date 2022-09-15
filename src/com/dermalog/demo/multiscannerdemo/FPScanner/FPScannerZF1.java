/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.FPScanner;

import com.dermalog.common.exception.DermalogException;
import com.dermalog.imaging.capturing.valuetype.CaptureMode;
import com.dermalog.imaging.capturing.valuetype.DeviceIdentity;
import com.dermalog.imaging.capturing.valuetype.PropertyType;

/**
 *
 * @author BA07190
 */
public class FPScannerZF1 extends FPScannerSingleFinger {

    public FPScannerZF1(DeviceIdentity id, int index, CaptureMode captureMode) throws Exception {
        super(id, index, captureMode);
    }

    public FPScannerZF1(DeviceIdentity id, int index) throws Exception {
        this(id, index, CaptureMode.PREVIEW_IMAGE_AUTO_DETECT);
    }

    public void setGreenLed(boolean enable) throws DermalogException {
        int value = enable ? 1 : 0;
        super.setDeviceProperty(PropertyType.FG_GREEN_LED, value);
    }

    public void setRedLed(boolean enable) throws DermalogException {
        int value = enable ? 1 : 0;
        super.setDeviceProperty(PropertyType.FG_RED_LED, value);
    }
}