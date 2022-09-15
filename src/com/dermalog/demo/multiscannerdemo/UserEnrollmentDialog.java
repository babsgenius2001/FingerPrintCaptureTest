/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.dermalog.common.exception.DermalogException;
import com.dermalog.demo.multiscannerdemo.FPScanner.FPScanner;
import com.dermalog.demo.multiscannerdemo.FPScanner.FPScannerEvents;
import com.dermalog.demo.multiscannerdemo.FPScanner.Fingerprint;
import com.dermalog.demo.multiscannerdemo.LocalAFIS.LocalAFIS;
import com.dermalog.demo.multiscannerdemo.LocalAFIS.LocalUser;
import com.dermalog.imaging.capturing.OnDetectEventData;
import com.dermalog.imaging.capturing.OnImageEventData;
/**
 *
 * @author BA07190
 */
public class UserEnrollmentDialog extends JDialog implements FPScannerEvents {

	private static final long serialVersionUID = 1L;
	
	private JLabel m_oLabelStatus, m_oLabelNameSet;
	private ImagePanel m_oImagePanelOnDetect;
	private JPanel m_oPanelCenterFingerprints;
	private JButton m_oButtonSave;
	
	private FPScanner m_oFPScanner;
	private boolean m_bDisplayOnImage = true;
	private LocalAFIS m_oAFIS;
	private String m_sUsername;
	private List<Fingerprint> m_oUserFingerprints;
	private LocalUser m_oEnrolledUser;

	public UserEnrollmentDialog(FPScanner oFPScanner, LocalAFIS oAFIS) {
		m_oFPScanner = oFPScanner;
		m_oAFIS = oAFIS;
		
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);

		final Dimension oSize = new Dimension(450, 300);
		setSize(oSize);

		JPanel oContentPanel = new JPanel();
		oContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		oContentPanel.setBackground(Color.WHITE);
		setContentPane(oContentPanel);
		
		setLayout(new BorderLayout());
		
		JPanel oPanelTop = new JPanel();
		oPanelTop.setBorder(new LineBorder(DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE, 2, true));
		oPanelTop.setBackground(Color.WHITE);

		m_oLabelStatus = new JLabel("Status");
		m_oLabelStatus.setForeground(DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE);
		m_oLabelStatus.setHorizontalAlignment(SwingConstants.CENTER);
		Font oLabelStatusFont = m_oLabelStatus.getFont();
		m_oLabelStatus.setFont(new Font(oLabelStatusFont.getName(), Font.PLAIN, 21));
		oPanelTop.add(m_oLabelStatus);
		
		JPanel oPanelCenter = new JPanel();

		oPanelCenter.setLayout(new GridBagLayout());
		oPanelCenter.setBackground(Color.WHITE);
		
		JPanel oPanelCenterName = new JPanel();
		oPanelCenterName.setBorder(new LineBorder(DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE, 2, true));
		addPaddingToBorder(oPanelCenterName, 0, 10, 0, 10);
		oPanelCenterName.setLayout(new BoxLayout(oPanelCenterName, BoxLayout.X_AXIS));
		oPanelCenterName.setBackground(Color.WHITE);
		
		JLabel oLabelName = new JLabel("Name: ");
		oLabelName.setForeground(DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE);
		Font oLabelNameFont = oLabelName.getFont();
		oLabelName.setFont(new Font(oLabelNameFont.getName(), Font.PLAIN, 18));
		
		m_oLabelNameSet = new JLabel();
		m_oLabelNameSet.setForeground(DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE);
		Font oLabelNameSetFont = m_oLabelNameSet.getFont();
		m_oLabelNameSet.setFont(new Font(oLabelNameSetFont.getName(), Font.PLAIN, 18));
		
		oPanelCenterName.add(oLabelName);
		oPanelCenterName.add(m_oLabelNameSet);
		
		GridBagConstraints oGBConstraints = new GridBagConstraints();
		oGBConstraints.fill = GridBagConstraints.BOTH;
		oGBConstraints.weightx = 0.75;
		oGBConstraints.weighty = 0.2;
		oGBConstraints.gridx = 0;
		oGBConstraints.gridy = 0;
		oGBConstraints.insets = new Insets(5, 0, 5, 5);
		
		oPanelCenter.add(oPanelCenterName, oGBConstraints);
		
		m_oPanelCenterFingerprints = new JPanel();
		m_oPanelCenterFingerprints.setBorder(new LineBorder(DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE, 2, true));
		m_oPanelCenterFingerprints.setLayout(new GridBagLayout());//new BoxLayout(m_oPanelCenterFingerprints, BoxLayout.X_AXIS));
		m_oPanelCenterFingerprints.setBackground(Color.WHITE);
		
		oGBConstraints.fill = GridBagConstraints.BOTH;
		oGBConstraints.weightx = 0.6;
		oGBConstraints.weighty = 1;
		oGBConstraints.gridx = 0;
		oGBConstraints.gridy = 1;
		oGBConstraints.insets = new Insets(0, 0, 5, 5);
		
		oPanelCenter.add(m_oPanelCenterFingerprints, oGBConstraints);
		
		JPanel oPanelCenterOnDetect = new JPanel();
		oPanelCenterOnDetect.setBorder(new LineBorder(DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE, 2, true));
		oPanelCenterOnDetect.setLayout(new BorderLayout());
		oPanelCenterOnDetect.setBackground(Color.WHITE);
		
		m_oImagePanelOnDetect = new ImagePanel();
		
		oPanelCenterOnDetect.add(m_oImagePanelOnDetect, BorderLayout.CENTER);
		
		oGBConstraints.fill = GridBagConstraints.BOTH;
		oGBConstraints.weightx = 0.4;
		oGBConstraints.weighty = 1;
		oGBConstraints.gridx = 1;
		oGBConstraints.gridy = 0;
		oGBConstraints.gridheight = 2;
		oGBConstraints.insets = new Insets(5, 0, 5, 0);
		
		oPanelCenter.add(oPanelCenterOnDetect, oGBConstraints);
		
		m_oButtonSave = new JButton("SAVE");
		m_oButtonSave.setBorder(new LineBorder(DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE, 2, true));
		m_oButtonSave.setPreferredSize(new Dimension(m_oButtonSave.getWidth(), 30));
		m_oButtonSave.setEnabled(false);
		m_oButtonSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_oEnrolledUser = m_oAFIS.RegisterUser(m_sUsername, m_oUserFingerprints);
				
				closeDialog();
			}
		});
		
		add(oPanelTop, BorderLayout.NORTH);
		add(oPanelCenter, BorderLayout.CENTER);
		add(m_oButtonSave, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(m_oButtonSave);
	}
	
	private void addPaddingToBorder(JComponent oComponent, int nTop, int nLeft, int nBottom, int nRight) {
		Border oBorder = oComponent.getBorder();
		Border oMargin = new EmptyBorder(nTop, nLeft, nBottom, nRight);
		oComponent.setBorder(new CompoundBorder(oBorder, oMargin));
	}
	
	public LocalUser showDialog()
	{
		EnterUsernameDialog oEnterUsernameWindow = new EnterUsernameDialog();
		oEnterUsernameWindow.setLocationRelativeTo(this);
		m_sUsername = oEnterUsernameWindow.showDialog();
		if(!m_sUsername.equals(""))
			m_oLabelNameSet.setText(m_sUsername);
		else
		{
			setVisible(false);
			dispose();
			return null;
		}
		
		try {
			displayMessage("Please place finger(s) onto scanner.");
			m_oFPScanner.addScannerEventListener(this);
			m_oFPScanner.startCapturing();
		} catch (DermalogException e) {
			e.printStackTrace();
			displayError(e.getMessage());
		}
		
		setVisible(true);
		
		return m_oEnrolledUser;
	}
	
	public void closeDialog() {
		setVisible(false);
		dispose();
	}
	
	private void displayMessage(String sMessage) {
		displayMessage(sMessage, DermalogJavaMultiScannerDemo.COLOR_DERMALOG_BLUE);
	}

	private void displayError(String sError) {
		sError = (sError == null ? "Unkown error" : sError);
		displayMessage(sError, DermalogJavaMultiScannerDemo.COLOR_DERMALOG_RED);
	}

	private void displayMessage(String sMessage, Color oColor) {
		m_oLabelStatus.setForeground(oColor);
		m_oLabelStatus.setText(sMessage.toUpperCase());
	}

	@Override
	public void OnScannerImage(OnImageEventData oEventData) {
		if (!m_bDisplayOnImage)
            return;
		try {
			m_oImagePanelOnDetect.setFingerprint(oEventData.getDermalogImage().getImage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnScannerDetect(OnDetectEventData oEventData) {
		displayMessage("Extracting Templates");
		
		m_bDisplayOnImage = false;
		
		try {
			m_oImagePanelOnDetect.setFingerprint(oEventData.getDermalogImage().getImage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnScannerError(Throwable oThrowable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnFingerprintsDetected(List<Fingerprint> oFingerprints) {
		try {
			m_oFPScanner.stopCapturing();
		} catch (DermalogException e) {
			e.printStackTrace();
		}
		
		m_oUserFingerprints = oFingerprints;
		
		m_oPanelCenterFingerprints.removeAll();
		ArrayList<ImagePanel> oImagePanelList = new ArrayList<ImagePanel>();
		for(int i = 0; i < oFingerprints.size(); i++)
		{			
			ImagePanel oImagePanel = new ImagePanel();
			oImagePanelList.add(oImagePanel);
			
			GridBagConstraints oGBConstraints = new GridBagConstraints();
			oGBConstraints.fill = GridBagConstraints.BOTH;
			oGBConstraints.weightx = 1.0/(double)oFingerprints.size();
			oGBConstraints.weighty = 1.0;
			oGBConstraints.gridx = i;
			oGBConstraints.gridy = 0;
			//oGBConstraints.insets = new Insets(5, 5, 5, 5);
			
			m_oPanelCenterFingerprints.add(oImagePanel, oGBConstraints);
		}
		validate();
		for(int i = 0; i < oImagePanelList.size(); i++)
		{
			ImagePanel oImagePanel = oImagePanelList.get(i);
			oImagePanel.setFingerprint(oFingerprints.get(i).Image);
		}
		
		m_oButtonSave.setEnabled(true);
		displayMessage("Press save to finish enrollment");
		
		try {
			m_oFPScanner.freeze(false);
		} catch (DermalogException e) {
			e.printStackTrace();
		}
	}
}
