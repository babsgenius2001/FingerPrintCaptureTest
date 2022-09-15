/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author BA07190
 */
public class EnterUsernameDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	public String m_sUsername = "";
	
	public EnterUsernameDialog() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Enter Name");

		final Dimension oSize = new Dimension(300, 100);
		setSize(oSize);

		JPanel oContentPanel = new JPanel();
		oContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		oContentPanel.setBackground(Color.WHITE);
		setContentPane(oContentPanel);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints oGBConstraints = new GridBagConstraints();
		
		final JTextField oTextFieldName = new JTextField();
		
		oGBConstraints.fill = GridBagConstraints.HORIZONTAL;
		oGBConstraints.weightx = 1;
		oGBConstraints.weighty = 0.5;
		oGBConstraints.gridx = 0;
		oGBConstraints.gridy = 0;
		//oGBConstraints.insets = new Insets(5, 5, 5, 5);
		
		add(oTextFieldName, oGBConstraints);
		
		JButton oButtonOK = new JButton("OK");
		
		oButtonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_sUsername = oTextFieldName.getText();
				
				closeDialog();
			}
		});
		
		oGBConstraints.fill = GridBagConstraints.NONE;
		oGBConstraints.weightx = 1;
		oGBConstraints.weighty = 0.5;
		oGBConstraints.gridx = 0;
		oGBConstraints.gridy = 1;
		oGBConstraints.anchor = GridBagConstraints.LINE_END;
		//oGBConstraints.insets = new Insets(5, 5, 5, 5);
		
		add(oButtonOK, oGBConstraints);
		getRootPane().setDefaultButton(oButtonOK);
	}
	
	public String showDialog() {
	    setVisible(true);
	    return m_sUsername;
	}
	
	public void closeDialog() {
		setVisible(false);
		dispose();
	}
}