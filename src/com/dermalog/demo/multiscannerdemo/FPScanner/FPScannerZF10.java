/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.FPScanner;

import java.util.ArrayList;
import java.util.List;

import com.dermalog.afis.fingercode3.Template;
import com.dermalog.afis.fourprint.segmentation.FourprintSegmentation;
import com.dermalog.afis.fourprint.segmentation.SegmentedFingerprint;
import com.dermalog.afis.fourprint.segmentation.SegmentedFingerprint.HandPosition;
import com.dermalog.afis.imagecontainer.RawImage;
import com.dermalog.afis.nistqualitycheck.Functions;
import com.dermalog.common.DermalogImage;
import com.dermalog.common.exception.DermalogException;
import com.dermalog.imaging.capturing.valuetype.DeviceIdentity;
import com.dermalog.imaging.capturing.valuetype.PropertyType;
import com.dermalog.imaging.capturing.valuetype.zf10.Zf10LedColor;
import com.dermalog.imaging.capturing.valuetype.zf10.Zf10MultiLed;

/**
 *
 * @author BA07190
 */
public class FPScannerZF10 extends FPScanner {

    private FourprintSegmentation mFourprintSegmentation;

    public FPScannerZF10(int index) throws Exception {
        super(DeviceIdentity.FG_ZF10, index);

        mFourprintSegmentation = new FourprintSegmentation();
    }

    public void setAllFingerLeds(Zf10LedColor color) throws DermalogException {
        int leds = Zf10MultiLed.LEFT_LITTLE.getValue();
        leds += (int) Zf10MultiLed.LEFT_RING.getValue();
        leds += (int) Zf10MultiLed.LEFT_MIDDLE.getValue();
        leds += (int) Zf10MultiLed.LEFT_INDEX.getValue();
        leds += (int) Zf10MultiLed.LEFT_THUMB.getValue();
        leds += (int) Zf10MultiLed.RIGHT_THUMB.getValue();
        leds += (int) Zf10MultiLed.RIGHT_INDEX.getValue();
        leds += (int) Zf10MultiLed.RIGHT_MIDDLE.getValue();
        leds += (int) Zf10MultiLed.RIGHT_RING.getValue();
        leds += (int) Zf10MultiLed.RIGHT_LITTLE.getValue();
        leds += (int) color.getValue();

        super.setDeviceProperty(PropertyType.FG_LEDS, leds);
    }

    public int getLed(HandPosition hand, int position) {
        int led = 0;
        switch (hand) {
            case LEFT:
                switch (position) {
                    case 1:
                        led = (int) Zf10MultiLed.LEFT_LITTLE.getValue();
                        break;
                    case 2:
                        led = (int) Zf10MultiLed.LEFT_RING.getValue();
                        break;
                    case 3:
                        led = (int) Zf10MultiLed.LEFT_MIDDLE.getValue();
                        break;
                    case 4:
                        led = (int) Zf10MultiLed.LEFT_INDEX.getValue();
                        break;
                    default:
                        break;
                }
                break;
            case RIGHT:
                switch (position) {
                    case 1:
                        led = (int) Zf10MultiLed.RIGHT_INDEX.getValue();
                        break;
                    case 2:
                        led = (int) Zf10MultiLed.RIGHT_MIDDLE.getValue();
                        break;
                    case 3:
                        led = (int) Zf10MultiLed.RIGHT_RING.getValue();
                        break;
                    case 4:
                        led = (int) Zf10MultiLed.RIGHT_LITTLE.getValue();
                        break;
                    default:
                        break;
                }
                break;
            case THUMBS:
                switch (position) {
                    case 1:
                        led = (int) Zf10MultiLed.LEFT_THUMB.getValue();
                        break;
                    case 2:
                        led = (int) Zf10MultiLed.RIGHT_THUMB.getValue();
                        break;
                    default:
                        break;
                }
                break;
            case UNKNOWN:
                break;
            default:
                break;
        }

        return led;
    }

    public void SetLeds(HandPosition hand, int[] positions, Zf10LedColor color)
            throws DermalogException {
        int leds = 0;

        for (int position : positions) {
            leds += getLed(hand, position);
        }

        leds += (int) color.getValue();

        super.setDeviceProperty(PropertyType.FG_LEDS, leds);
    }

    @Override
    public void startCapturing() throws DermalogException {
        super.start();
    }

    @Override
    public void stopCapturing() throws DermalogException {
        super.stop();

        setAllFingerLeds(Zf10LedColor.OFF);
    }

    @Override
    protected void DoWork(DermalogImage image) {
        try {
            int count = mFourprintSegmentation.getSegmentationCount(image);
            List<Fingerprint> fps = new ArrayList<Fingerprint>();
            for (int i = 0; i < count; i++) {
                SegmentedFingerprint finger = mFourprintSegmentation
                        .getSegmentedFingerprint(i);
                if (finger == null) {
                    continue;
                }

                RawImage rawImg = DecodeImage(finger.getRawImage());

                Template template = EncoderFinger(rawImg);
                int nQC = Functions.CheckNfiq2(rawImg);

                Fingerprint fp = new Fingerprint();
                fp.Image = finger.getImage().getImage();
                fp.Template = template;
                fp.NFIQ = nQC;
                fp.Hand = finger.getHandPosition();
                fp.Position = finger.getPosition();

                if (template != null) {
                    fps.add(fp);
                }
            }

            setAllFingerLeds(Zf10LedColor.OFF);

            int[] positions = new int[fps.size()];
            for (int i = 0; i < positions.length; i++) {
                positions[i] = fps.get(i).Position;
            }
            SetLeds(fps.get(0).Hand, positions, Zf10LedColor.GREEN);

            super.invokeFingerprintsDetected(fps);
        } catch (Exception ex) {
            super.invokeOnScannerError(new Throwable("Processing error: "
                    + ex.getMessage()));
        }
    }
}
