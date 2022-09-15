/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.FPScanner;

import java.util.ArrayList;
import java.util.List;

import com.dermalog.afis.fingercode3.Template;
import com.dermalog.afis.imagecontainer.RawImage;
import com.dermalog.afis.nistqualitycheck.Functions;
import com.dermalog.common.DermalogImage;
import com.dermalog.common.exception.DermalogException;
import com.dermalog.imaging.capturing.valuetype.CaptureMode;
import com.dermalog.imaging.capturing.valuetype.DeviceIdentity;

/**
 *
 * @author BA07190
 */
public abstract class FPScannerSingleFinger extends FPScanner {

    public FPScannerSingleFinger(DeviceIdentity deviceIdentity, int index,
            CaptureMode captureMode) throws Exception {
        super(deviceIdentity, index, captureMode);
    }

    public FPScannerSingleFinger(DeviceIdentity deviceIdentity, int index)
            throws Exception {
        this(deviceIdentity, index, CaptureMode.PREVIEW_IMAGE_AUTO_DETECT);
    }

    public void setLeds(boolean enable) throws DermalogException {
        setGreenLed(enable);
        setRedLed(enable);
    }

    // SingleFinger-Scanner specific functions to implement
    public abstract void setGreenLed(boolean enable) throws DermalogException;

    public abstract void setRedLed(boolean enable) throws DermalogException;

    @Override
    public void startCapturing() throws DermalogException {
        super.start();

        setGreenLed(true);
    }

    @Override
    public void stopCapturing() throws DermalogException {
        super.stop();

        setLeds(false);
    }

    @Override
    protected void DoWork(DermalogImage image) {
        try {
            setGreenLed(false);
            setRedLed(true);

            RawImage rawImg = DecodeImage(image);
            Template template = EncoderFinger(rawImg);

            int nQC = Functions.CheckNfiq2(rawImg);

            Fingerprint fp = new Fingerprint();
            fp.Image = image.getImage();
            fp.Template = template;
            fp.NFIQ = nQC;

            List<Fingerprint> fps = new ArrayList<Fingerprint>();
            fps.add(fp);

            super.invokeFingerprintsDetected(fps);

            setGreenLed(true);
            setRedLed(false);
        } catch (Exception ex) {
            super.invokeOnScannerError(new Throwable("Processing error: "
                    + ex.getMessage()));
        }
    }

}
