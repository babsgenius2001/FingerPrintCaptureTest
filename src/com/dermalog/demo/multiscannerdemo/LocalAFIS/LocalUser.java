/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.LocalAFIS;

import java.util.ArrayList;
import java.util.List;

import com.dermalog.demo.multiscannerdemo.FPScanner.Fingerprint;

/**
 *
 * @author BA07190
 */
public class LocalUser {

    public long ID;
    public String Name;
    public List<Fingerprint> Fingerprints;

    public LocalUser() {
        
        Fingerprints = new ArrayList<Fingerprint>();
    }

    @Override
    public String toString() {
        
        return String.format("%s (%d)", Name, ID);
    }
}