/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.FPScanner;

import com.dermalog.imaging.capturing.valuetype.DeviceIdentity;
/**
 *
 * @author BA07190
 */
public class FPScannerLF1 extends FPScannerSingleFinger {

	public FPScannerLF1(int index) throws Exception {
		super(DeviceIdentity.FG_LF1, index);
	}
	
	//Implementation of abstract methods from super-class
	public void setGreenLed(boolean enable)
    {
        // LF1 - no settable green led
    }

    public void setRedLed(boolean enable)
    {
        // LF1 - no settable Ged led
    }
}