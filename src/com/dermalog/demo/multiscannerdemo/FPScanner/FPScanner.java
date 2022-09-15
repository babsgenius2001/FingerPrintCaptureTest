/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo.FPScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import com.dermalog.afis.fingercode3.Encoder;
import com.dermalog.afis.fingercode3.FC3Exception;
import com.dermalog.afis.fingercode3.Template;
import com.dermalog.afis.imagecontainer.DICException;
import com.dermalog.afis.imagecontainer.Decoder;
import com.dermalog.afis.imagecontainer.RawImage;
import com.dermalog.common.DermalogImage;
import com.dermalog.common.exception.DermalogException;
import com.dermalog.common.util.DibUtil;
import com.dermalog.imaging.capturing.Device;
import com.dermalog.imaging.capturing.DeviceManager;
import com.dermalog.imaging.capturing.OnDetectEventData;
import com.dermalog.imaging.capturing.OnDetectListenerEx;
import com.dermalog.imaging.capturing.OnErrorListener;
import com.dermalog.imaging.capturing.OnImageEventData;
import com.dermalog.imaging.capturing.OnImageListenerEx;
import com.dermalog.imaging.capturing.cwrap.vc.DeviceInfo;
import com.dermalog.imaging.capturing.exception.ListenerException;
import com.dermalog.imaging.capturing.valuetype.CaptureMode;
import com.dermalog.imaging.capturing.valuetype.DeviceIdentity;
import com.dermalog.imaging.capturing.valuetype.PropertyType;
/**
 *
 * @author BA07190
 */
public abstract class FPScanner {

	private Device m_oDevice;
	private DeviceIdentity m_oDeviceIdentity;

	private CaptureMode m_oCaptureMode;

	private Encoder m_oFingerprintEncoder;
	protected Decoder m_oImageDecoder;

	private CopyOnWriteArrayList<FPScannerEvents> m_oFPScannerEventListeners = new CopyOnWriteArrayList<FPScannerEvents>();

	public void addScannerEventListener(FPScannerEvents oFPScannerEvent) {
		m_oFPScannerEventListeners.add(oFPScannerEvent);
	}

	public void removeScannerEventListener(FPScannerEvents oFPScannerEvent) {
		m_oFPScannerEventListeners.remove(oFPScannerEvent);
	}

	private ExecutorService m_oExecutorService = Executors
			.newSingleThreadExecutor();

	public FPScanner(DeviceIdentity deviceIdentity, int index,
			CaptureMode captureMode) throws Exception {
		m_oDeviceIdentity = deviceIdentity;
		m_oCaptureMode = captureMode;

		initScanner(index);

		m_oFingerprintEncoder = new Encoder();
		m_oImageDecoder = new Decoder();
	}

	public FPScanner(DeviceIdentity deviceIdentity, int index) throws Exception {
		this(deviceIdentity, index, CaptureMode.PREVIEW_IMAGE_AUTO_DETECT);	
	}

	@SuppressWarnings("deprecation")
	public static FPScanner getFPScanner(DeviceIdentity deviceIdentity,
			int index) throws Exception {

		FPScanner scanner = null;

		switch (deviceIdentity) {
		case FG_LF1:
			scanner = new FPScannerLF1(index);
			break;
		case FG_LF10:
			scanner = new FPScannerLF10(index);
			break;
		case FG_PLS1:
			throw new Exception("FG_PLS1 is deprecated. Please use FG_ZF1");
		case FG_ZF1:
			scanner = new FPScannerZF1(deviceIdentity, index);
			break;
		case FG_ZF10:
			scanner = new FPScannerZF10(index);
			break;
		case FG_ZF2:
			scanner = new FPScannerZF2(index);
			break;
		default:
			throw new Exception("DeviceIdentity not supported: "
					+ deviceIdentity);
		}

		return scanner;
	}

	private void initScanner(int index) throws Exception {
		if (m_oDevice != null) {
			m_oDevice.dispose();
			m_oDevice = null;
		}

		m_oDevice = DeviceManager.getDevice(m_oDeviceIdentity, index);
		m_oDevice.setCaptureMode(m_oCaptureMode);

		m_oDevice.addOnImageListenerEx(m_oOnImageListenerEx);
		m_oDevice.addOnDetectListenerEx(m_oOnDetectListenerEx);
		m_oDevice.addOnErrorListener(m_oOnErrorListener);
	}

	private void deinitScanner() throws DermalogException {
		if (m_oDevice != null) {
			stopCapturing();

			m_oDevice.removeOnImageListenerEx(m_oOnImageListenerEx);
			m_oDevice.removeOnDetectListenerEx(m_oOnDetectListenerEx);
			m_oDevice.removeOnErrorListener(m_oOnErrorListener);

			m_oDevice.dispose();
			m_oDevice = null;
		}
	}

	public void dispose() throws Exception {
		m_oFPScannerEventListeners.clear();
		deinitScanner();

		if (m_oFingerprintEncoder != null) {
			m_oFingerprintEncoder.close();
			m_oFingerprintEncoder = null;
		}

		if (m_oImageDecoder != null) {
			m_oImageDecoder.close();
			m_oImageDecoder = null;
		}

	}

	// Scanner specific functions to implement
	public abstract void startCapturing() throws DermalogException;

	public abstract void stopCapturing() throws DermalogException;

	protected void start() throws DermalogException {
		if (m_oDevice == null)
			return;

		if (!m_oDevice.isCapturing()) {
			synchronized (m_oDevice) {
				m_oDevice.start();
			}
		}
	}

	// / <summary>
	// / Stop capturing, Don't call this from Scanner-Thread -> Deadlock!
	// / </summary>
	protected void stop() throws DermalogException {
		if (m_oDevice == null)
			return;

		synchronized (m_oDevice) {
			m_oDevice.stop();
		}
	}

	public void freeze(boolean freeze) throws DermalogException {
		m_oDevice.setFreeze(freeze);
	}

	public Device getDevice() {
		return m_oDevice;
	}

	public DeviceIdentity getDeviceIdentity() {
		return m_oDeviceIdentity;
	}

	public Encoder getFingerprintEncoder() {
		return m_oFingerprintEncoder;
	}

	public Template EncoderFinger(RawImage rawImg) throws FC3Exception,
			DICException {
		return m_oFingerprintEncoder.Encode(rawImg);
	}

	public RawImage DecodeImage(DermalogImage image) throws DICException,
			IOException {
		return DecodeImage(DibUtil.convertImageToByteArray(image));
	}

	public RawImage DecodeImage(byte[] bitmapData) throws DICException,
			IOException {
		return m_oImageDecoder.Decode(bitmapData);
	}

	private static Comparator<Object> m_oDeviceComparator = new Comparator<Object>() {
		public int compare(Object x, Object y) {
			return x.toString().compareTo(y.toString());
		}
	};

	public static DeviceIdentity[] getDevices() throws DermalogException {
		DeviceIdentity[] available = DeviceManager.getAvailableDevices();
		DeviceIdentity[] filtered = FilterImplementedDeviceIdentities(available);

		Arrays.sort(filtered, m_oDeviceComparator);

		return filtered;
	}

	public static DeviceInfo[] GetAttachedDevices(DeviceIdentity id)
			throws DermalogException {
		return DeviceManager.getAttachedDevices(id);
	}

	static DeviceIdentity[] FilterImplementedDeviceIdentities(
			DeviceIdentity[] ids) {
		DeviceIdentity[] implemented = GetImplementedDeviceIdentities();
		List<DeviceIdentity> filtered = new ArrayList<DeviceIdentity>();
		for (DeviceIdentity id : ids) {
			if (Arrays.asList(implemented).indexOf(id) > -1) {
				filtered.add(id);
			}
		}
		return filtered.toArray(new DeviceIdentity[0]);
	}

	static DeviceIdentity[] GetImplementedDeviceIdentities() {
		DeviceIdentity[] ids = { DeviceIdentity.FG_LF1, DeviceIdentity.FG_LF10,
				DeviceIdentity.FG_ZF1, DeviceIdentity.FG_ZF10,
				DeviceIdentity.FG_ZF2 };
		return ids;
	}

	public void setDeviceProperty(PropertyType propertyType, int value)
			throws DermalogException {
		if (m_oDevice != null)
			m_oDevice.setProperty(propertyType, value);
	}

	private OnImageListenerEx m_oOnImageListenerEx = new OnImageListenerEx() {
		@Override
		public void onImage(final OnImageEventData oEventData)
				throws ListenerException {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (FPScannerEvents oFPScannerEvent : m_oFPScannerEventListeners)
						oFPScannerEvent.OnScannerImage(oEventData);
				}
			});
		}
	};

	private OnDetectListenerEx m_oOnDetectListenerEx = new OnDetectListenerEx() {
		@Override
		public void onDetect(final OnDetectEventData oEventData) {
			try {
				freeze(true);
			} catch (DermalogException e1) {
				e1.printStackTrace();
			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (FPScannerEvents oFPScannerEvent : m_oFPScannerEventListeners)
						oFPScannerEvent.OnScannerDetect(oEventData);
				}
			});

			try {
				OnDetect(oEventData.getDermalogImage());
			} catch (IOException e) {
				e.printStackTrace();
				for (FPScannerEvents oFPScannerEvent : m_oFPScannerEventListeners)
					oFPScannerEvent.OnScannerError(e);
			}
		}
	};

	private OnErrorListener m_oOnErrorListener = new OnErrorListener() {
		@Override
		public void onError(Device device, int channelNo, String error,
				Throwable oThrowable) {
			invokeOnScannerError(oThrowable);
		}
	};

	protected void invokeOnScannerError(final Throwable oThrowable) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (FPScannerEvents oFPScannerEvent : m_oFPScannerEventListeners)
					oFPScannerEvent.OnScannerError(oThrowable);
			}
		});
	}

	protected void invokeFingerprintsDetected(
			final List<Fingerprint> oFingerprints) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (FPScannerEvents oFPScannerEvent : m_oFPScannerEventListeners)
					oFPScannerEvent.OnFingerprintsDetected(oFingerprints);
			}
		});
	}

	private boolean mIsWorking = false;

	private void OnDetect(final DermalogImage image) {
		if (!mIsWorking) {
			mIsWorking = true;
			m_oExecutorService.submit(new Runnable() {

				@Override
				public void run() {
					DoWork(image);
					mIsWorking = false;
				}
			});
		}
	}

	protected abstract void DoWork(DermalogImage image);
}
