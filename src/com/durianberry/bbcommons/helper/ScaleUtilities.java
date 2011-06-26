package com.durianberry.bbcommons.helper;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.Ui;

public class ScaleUtilities {
	private static final int STANDARD_DENSITY = 9708; // right now, we use onyx as standard, for bold use 8547
	private static final int RATIO32 = Fixed32.div(
			Fixed32.toFP(Display.getVerticalResolution()), 
			Fixed32.toFP(STANDARD_DENSITY)
		);
	// edited RATIO 32 to enable tilt
	
	public static boolean isHighResolution() {
		String type = DeviceInfo.getDeviceName();
		if (type.startsWith("89") || 
				type.startsWith("90") || 
				type.startsWith("89") || 
//				type.startsWith("93") || 
				type.startsWith("95") || 
				type.startsWith("96") ||
				type.startsWith("97") ||
				type.startsWith("99") ||
				type.startsWith("98"))
			return true;
		else
			return false;
		
	}
	
	public static EncodedImage scaleImage(EncodedImage original) {
		if (isHighResolution()) {
			return original;
		} else {
			return ImageUtilities.scaleImageToHeight(original, 
					Fixed32.toInt(original.getHeight() * RATIO32));
		}
	}
	
	public static Font scaleFont(Font f) {
		if (Touchscreen.isSupported()) 
			return f.derive(f.getStyle(), Fixed32.toInt(f.getHeight() * RATIO32) + 4, Ui.UNITS_px);
		else 
			return f.derive(f.getStyle(), Fixed32.toInt(f.getHeight() * RATIO32), 
				Ui.UNITS_px);
	}
	
	public static int scaleInt(int value) {
		return Fixed32.toInt(value * RATIO32);
	}
}
