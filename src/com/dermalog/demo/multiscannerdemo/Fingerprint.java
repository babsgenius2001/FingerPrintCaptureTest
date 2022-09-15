/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo;

import java.awt.image.BufferedImage;

import com.dermalog.afis.fingercode3.FC3Exception;
import com.dermalog.afis.fingercode3.Template;

/**
 *
 * @author BA07190
 */
public class Fingerprint {

    public Template template;

    public BufferedImage Image;

    public int NFIQ;

    public int Position;

    public Fingerprint() {

    }

    public void Dispose() {
        if (template != null) {
            try {
                template.close();
            } catch (FC3Exception e) {
                e.printStackTrace();
            }
            template = null;
        }
    }
}
