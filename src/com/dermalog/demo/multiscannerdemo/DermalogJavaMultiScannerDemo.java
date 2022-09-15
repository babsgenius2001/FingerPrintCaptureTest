/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.dermalog.afis.fingercode3.FC3Exception;
import com.dermalog.common.exception.DermalogException;
import com.dermalog.demo.multiscannerdemo.FPScanner.FPScanner;
import com.dermalog.demo.multiscannerdemo.FPScanner.FPScannerEvents;
import com.dermalog.demo.multiscannerdemo.FPScanner.Fingerprint;
import com.dermalog.demo.multiscannerdemo.LocalAFIS.LocalAFIS;
import com.dermalog.demo.multiscannerdemo.LocalAFIS.LocalAFIS.AFISVerificationResult;
import com.dermalog.demo.multiscannerdemo.LocalAFIS.LocalDB;
import com.dermalog.demo.multiscannerdemo.LocalAFIS.LocalUser;
import com.dermalog.imaging.capturing.OnDetectEventData;
import com.dermalog.imaging.capturing.OnImageEventData;
import com.dermalog.imaging.capturing.cwrap.vc.DeviceInfo;
import com.dermalog.imaging.capturing.valuetype.DeviceIdentity;
/**
 *
 * @author BA07190
 */
public class DermalogJavaMultiScannerDemo extends JFrame implements
		FPScannerEvents {

	private static final long serialVersionUID = 1L;

	public static final Color COLOR_DERMALOG_BLUE = new Color(0, 66, 137, 255);
	public static final Color COLOR_DERMALOG_GREEN = new Color(17, 170, 17, 255);
	public static final Color COLOR_DERMALOG_RED = new Color(255, 5, 17, 255);
	public static final Color COLOR_DERMALOG_GRAY = new Color(209, 209, 209,
			255);

	// GUI Components
	private JLabel m_oLabelStatus;
	private JButton m_oButtonEnrollUser;
	private ImagePanel m_oImagePanelOnDetect, m_oImagePanelOnImage;
	private JList m_oListUsers;
	private DefaultListModel m_oListUsersModel;
	private JPanel m_oPanelCenterRightBottomBottom;

	private FPScanner m_oFPScanner;
	private LocalAFIS m_oAFIS;
	private LocalUser m_oSelectedUser;
	
	double verificationThreshold = 32.0; //FingerCode3 verification threshold

	public static void main(String[] args) {
		DermalogJavaMultiScannerDemo oDemo = new DermalogJavaMultiScannerDemo();
		try {
			oDemo.init();
		} catch (FC3Exception e) {
			e.printStackTrace();
		}
	}

	public void init() throws FC3Exception {
		m_oAFIS = new LocalAFIS();

		showDemo();
		updateUserList();
		showInitialDialogs();
	}

	private void showInitialDialogs() {
		try {
			EnableGUI(false);

			if (m_oFPScanner != null)
				m_oFPScanner.dispose();

			displayMessage("Device configuration");
			DeviceIdentity oSelectedDeviceIdentity = showSelectFrameGrabber();
			DeviceInfos oSelectedDeviceInfo = showSelectDevice(oSelectedDeviceIdentity);
			displayMessage("Opening device...");

			m_oFPScanner = FPScanner.getFPScanner(oSelectedDeviceIdentity,
					oSelectedDeviceInfo.getIndex());
			m_oFPScanner
					.addScannerEventListener(DermalogJavaMultiScannerDemo.this);

			resetGUI(true);

			EnableGUI(true);
		} catch (Exception e) {
			e.printStackTrace();
			displayError(e.getMessage());

			EnableGUI(false);
		}
	}

	private void resetGUI(boolean clearSelection) {
		if (m_oAFIS.IsEmpty()) {
			displayMessage("Press 'Enroll User'");
		} else {
			displayMessage("Select User to verify");
		}

		if (clearSelection)
			m_oListUsers.clearSelection();

		m_oImagePanelOnImage.clearFingerprint();
		m_oImagePanelOnDetect.clearFingerprint();
		m_oPanelCenterRightBottomBottom.removeAll();
		m_oPanelCenterRightBottomBottom.repaint();
	}

	private void EnableGUI(boolean bEnabled) {
		m_oButtonEnrollUser.setEnabled(bEnabled);
		m_oListUsers.setEnabled(bEnabled);
	}

	private DeviceInfos showSelectDevice(DeviceIdentity deviceIndentity)
			throws Exception {
		
		DeviceInfo[] aDeviceInfoArray = FPScanner
				.GetAttachedDevices(deviceIndentity);
		
		DeviceInfos[] aDeviceInfosArray = new DeviceInfos[aDeviceInfoArray.length];
		
		for (int i = 0; i < aDeviceInfoArray.length; i++) {
			DeviceInfo oDeviceInfo = aDeviceInfoArray[i];
			DeviceInfos oDeviceInfos = new DeviceInfos(oDeviceInfo.getIndex(),
					oDeviceInfo.getName());
			aDeviceInfosArray[i] = oDeviceInfos;
		}		
		
		DeviceInfos oSelectedDeviceInfo = (DeviceInfos) JOptionPane
				.showInputDialog(this, "Select a Device:", "Available Devices",
						JOptionPane.PLAIN_MESSAGE, null, aDeviceInfosArray, "");
		if (oSelectedDeviceInfo == null)
			throw new Exception("No device selected");
		System.out.println("Selected Device: " + oSelectedDeviceInfo.getName());
		
		return oSelectedDeviceInfo;
	}

	public class DeviceInfos {
		private int m_nIndex;
		private String m_sName;

		public DeviceInfos(int index, String name) {
			this.m_nIndex = index;
			this.m_sName = name;
		}

		@Override
		public String toString() {
			return m_sName;
		}

		public int getIndex() {
			return m_nIndex;
		}

		public String getName() {
			return m_sName;
		}
	}

	private DeviceIdentity showSelectFrameGrabber() throws Exception {
		DeviceIdentity[] aDeviceIdentityArray = FPScanner.getDevices();
		DeviceIdentity oSelectedDeviceIdentity = (DeviceIdentity) JOptionPane
				.showInputDialog(this, "Select a Frame-Grabber:",
						"Available Frame-Grabbers", JOptionPane.PLAIN_MESSAGE,
						null, aDeviceIdentityArray, "");
		if (oSelectedDeviceIdentity == null)
			throw new Exception("No Frame-Grabber selected");
		System.out.println("Selected Frame-Grabber: "
				+ oSelectedDeviceIdentity.name());
		return oSelectedDeviceIdentity;
	}

	private void showDemo() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exception) {
			// use default look and feel
		}

		final Dimension oSize = new Dimension(800, 500);
		setSize(oSize);

		JPanel oContentPanel = new JPanel();
		oContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		oContentPanel.setBackground(Color.WHITE);
		setContentPane(oContentPanel);

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JMenuBar oMenuBar = new JMenuBar();
		JMenu oMenu = new JMenu("File");
		oMenuBar.add(oMenu);

		JMenuItem oMenuItemSelectFG = new JMenuItem("Select Frame-Grabber");
		oMenuItemSelectFG.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showInitialDialogs();
			}
		});
		oMenu.add(oMenuItemSelectFG);
		JMenuItem oMenuItemResetUserData = new JMenuItem("Reset User data");
		oMenuItemResetUserData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					m_oFPScanner.stopCapturing();
					LocalDB.deleteRecursive(LocalDB.StoragePath);
					m_oAFIS = new LocalAFIS();
					updateUserList();
					resetGUI(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		oMenu.add(oMenuItemResetUserData);
		oMenu.addSeparator();
		JMenuItem oMenuItemExit = new JMenuItem("Exit");
		oMenuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		oMenu.add(oMenuItemExit);
		setJMenuBar(oMenuBar);

		JPanel oPanelTop = new JPanel();
		// oPanelTop.setBorder(BorderFactory.createLineBorder(DERMALOG_BLUE,
		// 2));
		oPanelTop.setBorder(new LineBorder(COLOR_DERMALOG_BLUE, 2, true));
		// oPanelTop.setLayout(new BoxLayout(oPanelTop, BoxLayout.Y_AXIS));
		oPanelTop.setLayout(new BorderLayout());
		oPanelTop.setMaximumSize(new Dimension(10000, 40));
		oPanelTop.setBackground(Color.WHITE);

		m_oLabelStatus = new JLabel("Status", SwingConstants.CENTER);
		m_oLabelStatus.setMaximumSize(oPanelTop.getSize());
		m_oLabelStatus.setForeground(COLOR_DERMALOG_BLUE);
		// m_oLabelStatus.setHorizontalAlignment(SwingConstants.CENTER);
		Font oLabelStatusFont = m_oLabelStatus.getFont();
		m_oLabelStatus.setFont(new Font(oLabelStatusFont.getName(), Font.PLAIN,
				25));
		oPanelTop.add(m_oLabelStatus);

		JPanel oPanelCenter = new JPanel();
		// oPanelCenter.setAlignmentY(Component.TOP_ALIGNMENT);
		oPanelCenter.setLayout(new BoxLayout(oPanelCenter, BoxLayout.X_AXIS));
		// oPanelCenter.setOpaque(true);
		// oPanelCenter.setBackground(Color.RED);
		oPanelCenter.setBackground(Color.WHITE);

		JPanel oPanelCenterLeft = new JPanel();
		// oPanelCenterLeft.setAlignmentY(Component.TOP_ALIGNMENT);
		// oPanelCenterLeft.setBorder(BorderFactory.createLineBorder(Color.blue));
		oPanelCenterLeft
				.setBorder(new LineBorder(COLOR_DERMALOG_BLUE, 2, true));
		oPanelCenterLeft.setLayout(new BorderLayout());
		oPanelCenter.setBackground(Color.WHITE);

		m_oButtonEnrollUser = new JButton("Enroll User");
		m_oButtonEnrollUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					m_oFPScanner.stopCapturing();
				} catch (DermalogException e) {
					e.printStackTrace();
					displayError(e.getMessage());
				}
				resetGUI(true);
				m_oFPScanner
						.removeScannerEventListener(DermalogJavaMultiScannerDemo.this);
				// open enrollment window
				UserEnrollmentDialog oUserEnrollmentWindow = new UserEnrollmentDialog(
						m_oFPScanner, m_oAFIS);
				oUserEnrollmentWindow
						.setLocationRelativeTo(DermalogJavaMultiScannerDemo.this);
				oUserEnrollmentWindow.showDialog();
				m_oFPScanner.removeScannerEventListener(oUserEnrollmentWindow);
				updateUserList();
				m_oFPScanner
						.addScannerEventListener(DermalogJavaMultiScannerDemo.this);
			}
		});
		m_oListUsersModel = new DefaultListModel();
		m_oListUsers = new JList(m_oListUsersModel);
		m_oListUsers.setLayoutOrientation(JList.VERTICAL);
		m_oListUsers.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent oListSelectionEvent) {
				if (oListSelectionEvent.getValueIsAdjusting() == false) {

					LocalUser oSelectedUser = (LocalUser) m_oListUsers
							.getSelectedValue();

					if (oSelectedUser != null) {
						resetGUI(false);

						m_oSelectedUser = oSelectedUser;

						displayMessage("Please place finger(s) onto scanner");
						try {
							m_oFPScanner.startCapturing();
						} catch (DermalogException e) {
							e.printStackTrace();
							displayError(e.getMessage());
						}
					}
				}

			}
		});

		oPanelCenterLeft.add(m_oButtonEnrollUser, BorderLayout.NORTH);
		oPanelCenterLeft.add(m_oListUsers, BorderLayout.CENTER);

		JPanel oPanelCenterRight = new JPanel();
		// oPanelCenterRight.setBorder(new LineBorder(DERMALOG_BLUE, 2, true));
		// addMarginToBorder(oPanelCenterRight, 10);
		oPanelCenterRight.setLayout(new BorderLayout());
		oPanelCenterRight.setBackground(Color.WHITE);

		JPanel oPanelCenterRightTop = new JPanel();
		oPanelCenterRightTop.setBorder(new LineBorder(COLOR_DERMALOG_BLUE, 2,
				true));
		oPanelCenterRightTop.setLayout(new BoxLayout(oPanelCenterRightTop,
				BoxLayout.X_AXIS));
		addPaddingToBorder(oPanelCenterRightTop, 10);
		oPanelCenterRightTop.setBackground(Color.WHITE);

		// final PicturePanel oPicturePanelScannerOnImage = new PicturePanel(new
		// Dimension(200, 160));
		// // picturePanelScannerOnImage.setMinimumSize(new Dimension(200,
		// 160));
		// // picturePanelScannerOnImage.setMaximumSize(new Dimension(200,
		// 160));
		// m_oLabelScannerOnImage = new JLabel();
		// oPicturePanelScannerOnImage.add(m_oLabelScannerOnImage);
		// oPicturePanelScannerOnImage.setBorder(new LineBorder(DERMALOG_GRAY,
		// 2, true));
		// addMarginToBorder(oPicturePanelScannerOnImage, 10);

		JPanel oPanelCenterRightTopLeft = new JPanel();
		oPanelCenterRightTopLeft.setBorder(new LineBorder(COLOR_DERMALOG_GRAY,
				2, true));
		oPanelCenterRightTopLeft.setLayout(new BoxLayout(
				oPanelCenterRightTopLeft, BoxLayout.Y_AXIS));
		oPanelCenterRightTopLeft.setBackground(Color.WHITE);

		m_oImagePanelOnImage = new ImagePanel();
		m_oImagePanelOnImage.setBorder(new LineBorder(COLOR_DERMALOG_GRAY, 2,
				true));
		m_oImagePanelOnImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		m_oImagePanelOnImage.setPreferredSize(new Dimension(160, 160));

		JLabel oLabelOnImage = new JLabel("Preview");
		oLabelOnImage.setAlignmentX(Component.CENTER_ALIGNMENT);

		oPanelCenterRightTopLeft.add(m_oImagePanelOnImage);
		oPanelCenterRightTopLeft.add(oLabelOnImage);

		JPanel oPanelCenterRightTopRight = new JPanel();
		oPanelCenterRightTopRight.setBorder(new LineBorder(COLOR_DERMALOG_GRAY,
				2, true));
		oPanelCenterRightTopRight.setLayout(new BoxLayout(
				oPanelCenterRightTopRight, BoxLayout.Y_AXIS));
		oPanelCenterRightTopRight.setBackground(Color.WHITE);

		m_oImagePanelOnDetect = new ImagePanel();
		m_oImagePanelOnDetect.setBorder(new LineBorder(COLOR_DERMALOG_GRAY, 2,
				true));
		m_oImagePanelOnDetect.setAlignmentX(Component.CENTER_ALIGNMENT);
		m_oImagePanelOnDetect.setPreferredSize(new Dimension(160, 160));

		JLabel oLabelOnDetect = new JLabel("Detect");
		oLabelOnDetect.setAlignmentX(Component.CENTER_ALIGNMENT);

		oPanelCenterRightTopRight.add(m_oImagePanelOnDetect);
		oPanelCenterRightTopRight.add(oLabelOnDetect);

		oPanelCenterRightTop.add(oPanelCenterRightTopLeft);
		oPanelCenterRightTop.add(Box.createRigidArea(new Dimension(10, 0)));
		oPanelCenterRightTop.add(oPanelCenterRightTopRight);

		JPanel oPanelCenterRightBottom = new JPanel();
		oPanelCenterRightBottom.setLayout(new BorderLayout());
		oPanelCenterRightBottom.setBackground(Color.WHITE);

		Component oBox = Box.createRigidArea(new Dimension(0, 10));

		// Fingerprints
		m_oPanelCenterRightBottomBottom = new JPanel();
		m_oPanelCenterRightBottomBottom.setBorder(new LineBorder(
				COLOR_DERMALOG_BLUE, 2, true));
		m_oPanelCenterRightBottomBottom.setLayout(new GridBagLayout());
		// addMarginToBorder(oPanelCenterRightBottomButtom, 10);
		m_oPanelCenterRightBottomBottom.setBackground(Color.WHITE);

		oPanelCenterRightBottom.add(oBox, BorderLayout.NORTH);
		oPanelCenterRightBottom.add(m_oPanelCenterRightBottomBottom,
				BorderLayout.CENTER);

		oPanelCenterRight.add(oPanelCenterRightTop, BorderLayout.NORTH);
		oPanelCenterRight.add(oPanelCenterRightBottom, BorderLayout.CENTER);

		oPanelCenter.add(oPanelCenterLeft);
		oPanelCenter.add(Box.createRigidArea(new Dimension(10, 0)));
		oPanelCenter.add(oPanelCenterRight);

		add(oPanelTop);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(oPanelCenter);

		addWindowListener(new CloseWindowAdapter());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getRootPane().setDefaultButton(m_oButtonEnrollUser);
		setLocationRelativeTo(null);
		setResizable(true);
		setVisible(true);
		setTitle("DERMALOG - MultiScannerDemo");
	}

	protected void updateUserList() {
		HashMap<Long, LocalUser> oUserList = m_oAFIS.GetUserList();
		m_oListUsersModel.clear();
		for (LocalUser oUser : oUserList.values()) {
			m_oListUsersModel.addElement(oUser);
		}
	}

	private void addPaddingToBorder(JComponent oComponent, int nBorderWidth) {
		addPaddingToBorder(oComponent, nBorderWidth, nBorderWidth,
				nBorderWidth, nBorderWidth);
	}

	private void addPaddingToBorder(JComponent oComponent, int nTop, int nLeft,
			int nBottom, int nRight) {
		Border oBorder = oComponent.getBorder();
		Border oMargin = new EmptyBorder(nTop, nLeft, nBottom, nRight);
		oComponent.setBorder(new CompoundBorder(oBorder, oMargin));
	}

	class CloseWindowAdapter extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent oEvent) {
			close();
		}
	}

	/**
	 * Closes the application.
	 */
	private void close() {
		try {
			if (m_oFPScanner != null) {
				m_oFPScanner.dispose();
			}
		} catch (Exception e) {
			// ignore, only closing
		}

		closeWindow();
	}

	private void closeWindow() {
		setVisible(false);
		getContentPane().removeAll();
		dispose();
		System.exit(0);
	}

	private void displayMessage(String sMessage) {
		displayMessage(sMessage, COLOR_DERMALOG_BLUE);
	}

	private void displayError(String sError) {
		sError = (sError == null ? "Unkown error" : sError);
		displayMessage(sError, COLOR_DERMALOG_RED);
	}

	private void displayMessage(String sMessage, Color oColor) {
		m_oLabelStatus.setForeground(oColor);
		// m_oLabelStatus.setText(sMessage.toUpperCase());
		// m_oLabelStatus.setText(String.format("<html><div WIDTH=%d>%s</div><html>",
		// m_oLabelStatus.getWidth(), sMessage.toUpperCase()));
		m_oLabelStatus.setText("<html><div style='text-align: center;'>"
				+ sMessage.toUpperCase() + "</div></html>");
	}

	@Override
	public void OnScannerImage(OnImageEventData oEventData) {
		try {
			m_oImagePanelOnImage.setFingerprint(oEventData.getDermalogImage()
					.getImage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnScannerDetect(OnDetectEventData oEventData) {
		try {
			m_oImagePanelOnDetect.setFingerprint(oEventData.getDermalogImage()
					.getImage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnScannerError(Throwable oThrowable) {
		displayError(oThrowable.getMessage());
	}
	
	@Override
	public void OnFingerprintsDetected(List<Fingerprint> oFingerprints) {

		if (m_oSelectedUser != null) {
			try {
				// Verify User
				displayMessage("Verifying Templates");
				AFISVerificationResult result = m_oAFIS.VerifyUser(
						m_oSelectedUser.ID, oFingerprints, verificationThreshold); 
				String scoreString = String.format("%.2f", result.Score);

				if (result.Hit)
					displayMessage(
							String.format("User verified (%s)", scoreString),
							COLOR_DERMALOG_GREEN);
				else
					displayMessage(String.format("User not verified (%s)",
							scoreString), COLOR_DERMALOG_RED);
			} catch (Exception e) {
				displayError(e.getMessage());
			}
		}

		// GUI - Display Fingerprints
		m_oPanelCenterRightBottomBottom.removeAll();
		m_oPanelCenterRightBottomBottom.repaint();

		ArrayList<ImagePanel> oImagePanelList = new ArrayList<ImagePanel>();
		for (int i = 0; i < oFingerprints.size(); i++) {
			JPanel oPanel = new JPanel();
			oPanel.setLayout(new GridBagLayout());
			oPanel.setBackground(Color.WHITE);

			ImagePanel oImagePanel = new ImagePanel();
			oImagePanelList.add(oImagePanel);

			GridBagConstraints oGBConstraints = new GridBagConstraints();
			oGBConstraints.fill = GridBagConstraints.BOTH;
			oGBConstraints.weightx = 1.0 / (double) oFingerprints.size();
			oGBConstraints.weighty = 1.0;
			oGBConstraints.gridx = i;
			oGBConstraints.gridy = 0;
			// oGBConstraints.insets = new Insets(5, 5, 5, 5);

			int nNFIQ = oFingerprints.get(i).NFIQ;
			JLabel oLabelNFIQ = new JLabel(String.valueOf("NIFQ2: " + nNFIQ));
			Font oLabelFont = oLabelNFIQ.getFont();
			oLabelNFIQ.setFont(new Font(oLabelFont.getName(), Font.BOLD, 18));
			oLabelNFIQ.setHorizontalAlignment(SwingConstants.CENTER);
			oLabelNFIQ.setForeground(getColorFromNFIQ(nNFIQ));

			GridBagConstraints oGBConstraintsPanel = new GridBagConstraints();
			oGBConstraintsPanel.fill = GridBagConstraints.BOTH;
			oGBConstraintsPanel.weightx = 1.0;
			oGBConstraintsPanel.weighty = 0.9;
			oGBConstraintsPanel.gridx = 0;
			oGBConstraintsPanel.gridy = 0;

			oPanel.add(oImagePanel, oGBConstraintsPanel);

			oGBConstraintsPanel = new GridBagConstraints();
			oGBConstraintsPanel.fill = GridBagConstraints.BOTH;
			oGBConstraintsPanel.weightx = 1.0;
			oGBConstraintsPanel.weighty = 0.1;
			oGBConstraintsPanel.gridx = 0;
			oGBConstraintsPanel.gridy = 1;

			oPanel.add(oLabelNFIQ, oGBConstraintsPanel);

			m_oPanelCenterRightBottomBottom.add(oPanel, oGBConstraints);
		}
		validate();
		for (int i = 0; i < oImagePanelList.size(); i++) {
			ImagePanel oImagePanel = oImagePanelList.get(i);
			oImagePanel.setFingerprint(oFingerprints.get(i).Image);
		}

		// dispose allocated fingerprint templates
		for (Fingerprint fingerprint : oFingerprints) {
			fingerprint.dispose();
		}

		try {
			m_oFPScanner.freeze(false);
		} catch (DermalogException e) {
			e.printStackTrace();
		}
	}

	private Color getColorFromNFIQ(int nNFIQ) {
		Color oColor = COLOR_DERMALOG_GREEN;
		switch (nNFIQ) {
		case 1:
			oColor = COLOR_DERMALOG_GREEN;
			break;
		case 2:
			oColor = Color.ORANGE;
			break;
		case 3:
			oColor = Color.ORANGE;
			break;
		case 4:
			oColor = Color.ORANGE;
			break;
		case 5:
			oColor = COLOR_DERMALOG_RED;
			break;
		}
		return oColor;
	}

}