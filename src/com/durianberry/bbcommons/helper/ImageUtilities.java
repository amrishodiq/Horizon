package com.durianberry.bbcommons.helper;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;

public class ImageUtilities {
	public static EncodedImage scaleToFactor(EncodedImage encoded, int curSize,
			int newSize) {

		int scale = Fixed32.ONE;

		if (curSize != newSize) {
			int numerator = Fixed32.toFP(curSize);
			int denominator = Fixed32.toFP(newSize);
			scale = Fixed32.div(numerator, denominator);
		}
		if (scale == Fixed32.ONE)
			return encoded;
		else
			return encoded.scaleImage32(scale, scale);
	}
	
	public static EncodedImage scaleImageToWidth(EncodedImage encoded,
			int newWidth) {
		return scaleToFactor(encoded, encoded.getWidth(), newWidth);
	}

	public static EncodedImage scaleImageToHeight(EncodedImage encoded,
			int newHeight) {
		return scaleToFactor(encoded, encoded.getHeight(), newHeight);
	}
	
	public static void fillWithBitmap(Graphics g, Bitmap b, int left, int top,
			int width, int height) {
		int[] xPts = new int[] { left, left + width, left + width, left };
		int[] yPts = new int[] { top, top, top + height, top + height };
		byte[] pointTypes = new byte[] { Graphics.CURVEDPATH_END_POINT,
				Graphics.CURVEDPATH_END_POINT, Graphics.CURVEDPATH_END_POINT,
				Graphics.CURVEDPATH_END_POINT };
		int dux = Fixed32.toFP(1), dvx = Fixed32.toFP(0), duy = Fixed32.toFP(0), dvy = Fixed32
				.toFP(1);

		g.drawTexturedPath(xPts, yPts, pointTypes, null, left, top, dux, dvx,
				duy, dvy, b);
	}
}
