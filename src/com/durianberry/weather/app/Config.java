package com.durianberry.weather.app;

import com.durianberry.bbcommons.helper.ScaleUtilities;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;

public class Config {
	private static FontFamily family;
	static {
		family = Font.getDefault().getFontFamily();
		try {
			family = FontFamily.forName("BBAlpha Sans");
		} catch (ClassNotFoundException e) {
		}
	}
	
	private static Font mediumFont = null;
	public static Font getMediumFont() {
		if (mediumFont == null) {
			mediumFont = ScaleUtilities.scaleFont(family.getFont(Font.PLAIN, 30));
		}
		return mediumFont;
	}

	private static Font largeFont = null;
	public static Font getLargeFont() {
		if (largeFont == null) {
			largeFont = ScaleUtilities.scaleFont(family.getFont(Font.PLAIN, 50));
		}
		return largeFont;
	}

	private static Font smallFont = null;
	public static Font getSmallFont() {
		if (smallFont == null) {
			smallFont = ScaleUtilities.scaleFont(family.getFont(Font.PLAIN, 22));
		}
		return smallFont;
	}
	
	private static EncodedImage logo;
	public static EncodedImage getLogo() {
		if (logo == null) {
			logo = ScaleUtilities.scaleImage(EncodedImage.getEncodedImageResource("img/logo.png"));
//			logo = EncodedImage.getEncodedImageResource("img/logo.png");
		}
		return logo;
	}
	
}
