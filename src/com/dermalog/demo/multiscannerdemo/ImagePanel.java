/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dermalog.demo.multiscannerdemo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
/**
 *
 * @author BA07190
 */
public class ImagePanel extends JPanel {
private static final long serialVersionUID = 5468797872915184356L;
	
	private Image _Image;
	private double _Zoom;
	private Dimension _ImageSize;
	private Point _StartPoint;
	private Rectangle[] _SegmentedFingerRects;
	
	public ImagePanel() {
		super(true);
		_ImageSize = new Dimension();
		_StartPoint = new Point();
	}
	
	public void clearFingerprint() {
		_Image = null;
		_SegmentedFingerRects = null;
		this.repaint();
	}

	public void setFingerprint(BufferedImage image) {
		this.setFingerprint(image, true);
	}
	
	public void setFingerprint(BufferedImage image, boolean refresh) {
		_Image = image;
		double zoomx = (double)(this.getWidth()) / _Image.getWidth(null);
		double zoomy = (double)(this.getHeight()) / _Image.getHeight(null);
		_Zoom = Math.min(zoomx, zoomy);
		_ImageSize.width = (int) (_Image.getWidth(null) * _Zoom);
		_ImageSize.height = (int) (_Image.getHeight(null) * _Zoom);
		_StartPoint.x = (int) ((this.getWidth() - _ImageSize.width) / 2.0);
		_StartPoint.y = (int) ((this.getHeight() - _ImageSize.height) / 2.0);
		
		if (refresh) {
			this.repaint();
		}
	}
	
	public void setFingerprint(BufferedImage image, Rectangle[] fingerprints) {
		this.setFingerprint(image, false);
		
		_SegmentedFingerRects = new Rectangle[fingerprints.length];
		for (int i = 0; i < _SegmentedFingerRects.length; i++) {
			_SegmentedFingerRects[i] = new Rectangle();
			_SegmentedFingerRects[i].x = (int) (fingerprints[i].x * _Zoom + _StartPoint.x);
			_SegmentedFingerRects[i].y = (int) (fingerprints[i].y * _Zoom + _StartPoint.y);
			_SegmentedFingerRects[i].width = (int) (fingerprints[i].width * _Zoom);
			_SegmentedFingerRects[i].height = (int) (fingerprints[i].height * _Zoom);
		}
		
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D gfx = (Graphics2D) g;
		gfx.clearRect(0, 0, this.getWidth(), this.getHeight());
		gfx.drawImage(_Image, _StartPoint.x, _StartPoint.y, _ImageSize.width, _ImageSize.height, null);
		
		if (_SegmentedFingerRects != null) {
			gfx.setStroke(new BasicStroke(2.0f));
			gfx.setColor(Color.GREEN);
			for (int i = 0; i < _SegmentedFingerRects.length; i++) {
				gfx.drawRect(_SegmentedFingerRects[i].x, _SegmentedFingerRects[i].y, _SegmentedFingerRects[i].width, _SegmentedFingerRects[i].height);
			}
		}
		
//		gfx.setColor(Color.DARK_GRAY);
//		gfx.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
	}
}