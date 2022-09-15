/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.FPScanner;

import java.awt.image.BufferedImage;

import com.dermalog.afis.fingercode3.FC3Exception;
import com.dermalog.afis.fourprint.segmentation.SegmentedFingerprint.HandPosition;
import com.dermalog.common.IDisposable;

/**
 *
 * @author BA07190
 */
public class Fingerprint implements IDisposable {

    public com.dermalog.afis.fingercode3.Template Template;

    public BufferedImage Image;

    public int NFIQ;

    public HandPosition Hand = HandPosition.UNKNOWN;

    public int Position;

    @Override
    public void dispose() {
        if (Template != null) {
            try {
                Template.close();
            } catch (FC3Exception e) {
            }
            Template = null;
        }
    }

}
