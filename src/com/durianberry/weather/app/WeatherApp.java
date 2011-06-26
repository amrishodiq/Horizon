/**
 * Copyright © 1998-2010 Research In Motion Ltd.
 *
 * Note:
 *
 * 1. For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 *
 * 2. The sample serves as a demonstration of principles and is not intended for a
 * full featured application. It makes no guarantees for completeness and is left to
 * the user to use it as sample ONLY.
 */

package com.durianberry.weather.app;

import com.durianberry.bbcommons.helper.NativeTools;
import com.durianberry.bbcommons.task.Queue;
import com.durianberry.bbcommons.task.Task;
import com.durianberry.bbcommons.task.TaskProgressListener;
import com.durianberry.bbcommons.task.TasksRunner;
import com.durianberry.weather.model.WeatherResponse;
import com.durianberry.weather.task.LocationCoordinates;
import com.durianberry.weather.task.TaskLocationGetter;
import com.durianberry.weather.task.TaskWeatherGetter;
import com.durianberry.weather.view.SplashScreen;
import com.durianberry.weather.view.TabManagerScreen;
import com.durianberry.weather.view.WeatherScreen;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * 
 * 
 * @author rhaq
 * @version 1.00 Aug 24, 2010 Initial submission.
 * @since TabManagerDemo
 * 
 */
public class WeatherApp extends UiApplication implements TaskProgressListener {
	private static boolean setupPermissions() {
		ApplicationPermissionsManager man = ApplicationPermissionsManager
				.getInstance();
		int[] requiredPerms = new int[] {
				ApplicationPermissions.PERMISSION_SERVER_NETWORK, // connect via
																	// MDS
				ApplicationPermissions.PERMISSION_LOCATION_DATA, // getting
																	// location,
																	// cid and
																	// lac
				ApplicationPermissions.PERMISSION_INTERNET // connect via direct
															// tcp or
		};
		ApplicationPermissions perms = man.getApplicationPermissions();
		boolean change = false;
		for (int i = 0; i < requiredPerms.length; i++) {
			if (perms.containsPermissionKey(requiredPerms[i])) {
				if (perms.getPermission(requiredPerms[i]) != ApplicationPermissions.VALUE_ALLOW) {
					change = true;
					perms.addPermission(requiredPerms[i]);
				}
			} else {
				change = true;
				perms.addPermission(requiredPerms[i]);
			}
		}
		if (change) {
			return man.invokePermissionsRequest(perms);
		} else {
			return true;
		}
	}

	public static void main(String[] args) {
		if (setupPermissions()) {
			WeatherApp.get().enterEventDispatcher();
		}
	}

	private static WeatherApp instance;

	public static WeatherApp get() {
		if (instance == null)
			instance = new WeatherApp();
		return instance;
	}

	/**
     * 
     */
	private WeatherApp() {
		pushScreen(new SplashScreen());
		init();
	}

	private TasksRunner tasksRunner = null;
	private LocationCoordinates location = null;
	private WeatherResponse weatherResponse;

	public TasksRunner getTaskRunner() {
		if (tasksRunner == null) {
			tasksRunner = new TasksRunner(new Queue());
			tasksRunner.startWorker(); // start the task runner thread
		}
		return tasksRunner;
	}

	public WeatherResponse getWeatherResponse() {
		return weatherResponse;
	}

	public LocationCoordinates getLocation() {
		return location;
	}

	public void init() {
		int lac = GPRSInfo.getCellInfo().getLAC();
		int cellId = GPRSInfo.getCellInfo().getCellId();

		if (DeviceInfo.isSimulator()) {
			cellId = 4390;
			lac = 10001;
		}

		Task task = new TaskLocationGetter(lac, cellId);
		task.setProgressListener(this);
		getTaskRunner().enqueue(task);
	}
	
	public void showMessage(final String message) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				Dialog.inform(message);
			}
		});
	}

	public void taskUpdate(Object obj) {
		if (obj instanceof LocationCoordinates) {
//			showMessage("Location received");
			
			System.out.println("Location recieved");
			location = (LocationCoordinates) obj;
			Task task = new TaskWeatherGetter(location);
			task.setProgressListener(this);
			getTaskRunner().enqueue(task);
		} else if (obj instanceof WeatherResponse) {
//			showMessage("Weather received");
			
			System.out.println("Weather recieved");

			weatherResponse = (WeatherResponse) obj;

			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					Screen splash = getActiveScreen();
					pushScreen(new WeatherScreen(weatherResponse));
					popScreen(splash);
				}
			});
		}
	}

	public void taskComplete(final Object obj) {
		if (obj instanceof Exception) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					Dialog.alert(((Exception) obj).getMessage());
				}
			});
		} else if (obj instanceof Throwable) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					Dialog.alert(((Throwable) obj).getMessage());
				}
			});
		}
	}
	
	private String appName = "Horizon";
	private String appDesc = "Knowing the weather, with Horizon, will help you plan your day wether to bring your umbrella, wear a jacket or stay at home because tomorrow will be a stormy day. Horizon is good cause it's light, small, just as you need.";
	private String version = "1.0.1";
	private String vendor = "Amri Shodiq";
	
	public void showAbout() {
		final PopupScreen popup = new PopupScreen(new VerticalFieldManager(VerticalFieldManager.NO_VERTICAL_SCROLL)) {
			public boolean keyDown(int keycode,
		            int time) {
		        if (keycode == 1769472) { // escape pressed
		            close();
		            return true;
		        }
		        return super.keyDown(keycode, time);
		    }
		};
		VerticalFieldManager v = new VerticalFieldManager(VerticalFieldManager.VERTICAL_SCROLL);
		popup.add(v);
		
		LabelField bold = new LabelField(appName);
		bold.setFont(Config.getSmallFont().derive(Font.BOLD));
		v.add(bold);
//		LabelField normal = new LabelField(appName);
//		normal.setFont(Config.getSmallFont());
//		v.add(normal);
		v.add(new SeparatorField());
		
		bold = new LabelField("Version");
		bold.setMargin(6, 0, 0, 0);
		bold.setFont(Config.getSmallFont().derive(Font.BOLD));
		v.add(bold);
		LabelField normal = new LabelField(version);
		normal.setFont(Config.getSmallFont());
		v.add(normal);
		
		bold = new LabelField("Vendor");
		bold.setMargin(6, 0, 0, 0);
		bold.setFont(Config.getSmallFont().derive(Font.BOLD));
		v.add(bold);
		normal = new LabelField(vendor);
		normal.setFont(Config.getSmallFont());
		v.add(normal);
		
		bold = new LabelField("Description");
		bold.setMargin(6, 0, 0, 0);
		bold.setFont(Config.getSmallFont().derive(Font.BOLD));
		TextField text = new TextField() {
			public void drawFocus(Graphics g, boolean on) {
			}
		};
		text.setFont(Config.getSmallFont());
		text.setText(appDesc);
		v.add(bold);
		v.add(text);
		
		ButtonField button = new ButtonField("OK", Field.FIELD_HCENTER) {
			// Invoke on ENTER
		    protected boolean keyChar( char character, int status, int time ) 
		    {
		        if( character == Characters.ENTER ) {
		            clickButton();
		            return true;
		        }
		        return super.keyChar( character, status, time );
		    }
		    
		    protected boolean navigationUnclick(int status, int time) {
		        clickButton(); 
		        return true;
		    }
		    
		    protected boolean invokeAction( int action ) 
		    {
		        switch( action ) {
		            case ACTION_INVOKE: {
		                clickButton(); 
		                return true;
		            }
		        }
		        return super.invokeAction( action );
		    }    

		    public void setDirty( boolean dirty ) { }
		     
		    public void setMuddy( boolean muddy ) { }
		         
		         
		    /* A public way to click this button  */
		    
		    public void clickButton(){
		        popup.close();
		    }
		    

		//#ifndef VER_4.6.1 | VER_4.6.0 | VER_4.5.0 | VER_4.2.1 | VER_4.2.0

		    protected boolean touchEvent(TouchEvent message)
		    {
		        boolean isOutOfBounds = touchEventOutOfBounds( message );
		        switch(message.getEvent()) {
		            case TouchEvent.UNCLICK:
		                if( !isOutOfBounds ) {
		                    clickButton();
		                    return true;
		                }
		            default : 
		                return false;
		        }
		    }
		    
		    private boolean touchEventOutOfBounds( TouchEvent message )
		    {
		        int x = message.getX( 1 );
		        int y = message.getY( 1 );
		        return ( x < 0 || y < 0 || x > getWidth() || y > getHeight() );
		    }
		//#endif
		};
		button.setMargin(6, 0, 0, 0);
		v.add(button);
		
		UiApplication.getUiApplication().pushScreen(popup);
	}
	public void showFeatureRequest() {
		NativeTools.sendEmail("amri.shodiq@gmail.com", "Feature Request for Horizon", "Horizon will be even better if it has the ability to:\n");
	}
	public void showOtherApp() {
		NativeTools.browse("http://web.durianapp.com");
	}
	public void shareToTwitter() {
		NativeTools.browse("http://twitter.com/share?count=horizontal&counturl=http://web.durianapp.com/2011/05/horizon-free-weather-forecast-app-for-blackberry/%2FHorizon+Weather+Forecast+for+Blackberry%2F&original_referer=http://web.durianapp.com/2011/05/horizon-free-weather-forecast-app-for-blackberry/%2Fshare-your-url-to-twitter%2F&text=Horizon,+Weather+Forecast+for+Blackberry%3A&url=http://www.durianapp.com/index.php/web/downloader/download/Horizontal.jad");
	}
	public void shareToFacebook() {
		NativeTools.browse("http://www.facebook.com/sharer.php?u=http://web.durianapp.com/2011/05/horizon-free-weather-forecast-app-for-blackberry/&t=Horizon, Weather Forecast for Blackberry");
	}
	public void shareByEmail() {
		NativeTools.sendEmail("", "Horizon, Weather Forecast for Blackberry", "Hi,\nI use Horizon and this weather forecast app helps me planning my days. It small and fast.\nDownload it for free at http://bit.ly/iG4OxU.");
	}

}