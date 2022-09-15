/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.FPScanner;

import java.util.ArrayList;
import java.util.List;

import com.dermalog.afis.imagecontainer.RawImage;
import com.dermalog.afis.nistqualitycheck.Functions;
import com.dermalog.afis.twoprint.segmentation.TwoPrintSegment;
import com.dermalog.afis.twoprint.segmentation.TwoPrintSegmentation;
import com.dermalog.common.DermalogImage;
import com.dermalog.common.exception.DermalogException;
import com.dermalog.imaging.capturing.valuetype.DeviceIdentity;
import com.dermalog.imaging.capturing.valuetype.PropertyType;

/**
 *
 * @author BA07190
 */
public class FPScannerZF2 extends FPScanner {

    private TwoPrintSegmentation mTwoPrintSegmentation;

    public FPScannerZF2(int index) throws Exception {
        super(DeviceIdentity.FG_ZF2, index);

        mTwoPrintSegmentation = null;
    }

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

    public void setLeds(boolean enable) throws DermalogException {
        setGreenLed(enable);
        setRedLed(enable);
    }

    public void setGreenLed(boolean enable) throws DermalogException {
        int value = enable ? 1 : 0;
        super.setDeviceProperty(PropertyType.FG_GREEN_LED, value);
    }

    public void setRedLed(boolean enable) throws DermalogException {
        int value = enable ? 1 : 0;
        super.setDeviceProperty(PropertyType.FG_RED_LED, value);
    }

    @Override
    protected void DoWork(DermalogImage image) {
        try {
            setGreenLed(false);
            setRedLed(true);

            List<Fingerprint> fps = new ArrayList<Fingerprint>();

            TwoPrintSegment[] segments = mTwoPrintSegmentation
                    .getSegments(image);
            for (int i = 0; i < segments.length; i++) {
                TwoPrintSegment segment = segments[i];

                RawImage rawImg = DecodeImage(segment.getRawImageData());

                Fingerprint fp = new Fingerprint();
                fp.Image = segment.getImage().getImage();
                fp.Template = EncoderFinger(rawImg);
                fp.NFIQ = Functions.CheckNfiq2(rawImg);

                rawImg.close();

                fps.add(fp);
            }

            setGreenLed(true);
            setRedLed(false);

            super.invokeFingerprintsDetected(fps);
        } catch (Exception ex) {
            super.invokeOnScannerError(new Throwable("Processing error: "
                    + ex.getMessage()));
        }
    }

}
