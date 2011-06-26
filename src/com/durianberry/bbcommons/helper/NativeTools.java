package com.durianberry.bbcommons.helper;

import java.io.IOException;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.blackberry.api.invoke.PhoneArguments;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.AccelerometerSensor;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.system.AccelerometerSensor.Channel;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.picker.FilePicker;

public class NativeTools {
	public static void makeACall(String phoneNumber) {
		PhoneArguments arg = new PhoneArguments(PhoneArguments.ARG_CALL, phoneNumber);
		Invoke.invokeApplication(Invoke.APP_TYPE_PHONE, arg);
	}
	
	public static void sendEmail(String address, String subject, String body) {
		MessageArguments arg = new MessageArguments(MessageArguments.ARG_NEW, address, subject, body);
		Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, arg);
	}
	
	public static void browse(String url) {
		BrowserSession browserSession = Browser.getDefaultSession();
		browserSession.displayPage(url);
		browserSession.showBrowser();
	}
	
}
