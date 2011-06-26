package com.durianberry.weather.view;

import javax.microedition.pim.FieldEmptyException;

import com.durianberry.weather.app.WeatherApp;
import com.durianberry.weather.model.Weather;
import com.durianberry.weather.model.WeatherResponse;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class WeatherScreen extends MainScreen {
	public static int CLOUDY = 0;
	public static int FOGGY = 1;
	public static int RAINY = 2;
	public static int SNOWY = 3;
	public static int STORMY = 4;
	public static int SUNNY = 5;
	
	public WeatherScreen(WeatherResponse response) {
		super(NO_VERTICAL_SCROLL);
		
		Weather[] weathers = response.getWeathers();
		int len = weathers.length;
		String[] tabs = new String[len];
		Field[] fields = new VerticalFieldManager[len];
		int i = 0;
		
		String cityName = WeatherApp.get().getLocation().getCity();
		String weatherDescription, temperature, windDescription, time;
		
		while (i<len) {
			time = "\n" + (i == 0?"Today":(i==1?"Tomorrow":"Day + "+i));
			tabs[i] = getDate(weathers[i].getDate());
			weatherDescription = weathers[i].getWeatherDescription();
			temperature = String.valueOf(weathers[i].getTempMinInC())+getDegreeChar()+" C - "+String.valueOf(weathers[i].getTempMaxInC())+getDegreeChar()+" C";
			windDescription = weathers[i].getWeatherDescription()+" with "+getWindDirection(weathers[i]).toLowerCase()+" wind about "+weathers[i].getWindSpeedInKm()+" km/h speed. Chance of precipitation is "+weathers[i].getPrecipitation()+" mm.";
			fields[i] = new WeatherManager(cityName + time, weatherDescription, temperature, windDescription);
			i++;
		}
		
		add(new TabManager(tabs, fields));
	}
	
	private String getDate(String d) {
		return d.substring(d.indexOf('-')+1, d.indexOf('-')+3)+"/"+d.substring(d.lastIndexOf('-')+1, d.lastIndexOf('-')+3);
	}
	private String getDegreeChar() {
		return String.valueOf((char)176);
	}
	public String getWindDirection(Weather weather) {
		if (weather.getWindDirection().equals("N")) return "North";
		else if (weather.getWindDirection().equals("NNE")) return "North North East";
		else if (weather.getWindDirection().equals("NE")) return "North East";
		else if (weather.getWindDirection().equals("ENE")) return "East North East";
		else if (weather.getWindDirection().equals("E")) return "East";
		else if (weather.getWindDirection().equals("ESE")) return "East South East";
		else if (weather.getWindDirection().equals("SE")) return "South East";
		else if (weather.getWindDirection().equals("SSE")) return "South South East";
		else if (weather.getWindDirection().equals("S")) return "South";
		else if (weather.getWindDirection().equals("SSW")) return "South South West";
		else if (weather.getWindDirection().equals("SW")) return "South West";
		else if (weather.getWindDirection().equals("WSW")) return "West South West";
		else if (weather.getWindDirection().equals("W")) return "West";
		else if (weather.getWindDirection().equals("WNW")) return "West North West";
		else if (weather.getWindDirection().equals("NW")) return "North West";
		else if (weather.getWindDirection().equals("NNW")) return "North North West";
		else return "North";
	}
	public int getState(Weather weather) {
		int resp = CLOUDY;
		switch (weather.getWeatherCode()) {
		case 395: resp = SNOWY; break;
		case 392: resp = SNOWY; break;
		case 389: resp = RAINY; break;
		case 386: resp = RAINY; break;
		case 377: resp = RAINY; break;
		case 374: resp = RAINY; break;
		case 371: resp = SNOWY; break;
		case 368: resp = SNOWY; break;
		case 365: resp = SNOWY; break;
		case 362: resp = SNOWY; break;
		case 359: resp = RAINY; break;
		case 356: resp = RAINY; break;
		case 353: resp = RAINY; break;
		case 350: resp = RAINY; break;
		case 338: resp = SNOWY; break;
		case 335: resp = SNOWY; break;
		case 332: resp = SNOWY; break;
		case 329: resp = SNOWY; break;
		case 326: resp = SNOWY; break;
		case 323: resp = SNOWY; break;
		case 320: resp = SNOWY; break;
		case 317: resp = SNOWY; break;
		case 314: resp = RAINY; break;
		case 311: resp = RAINY; break;
		case 308: resp = RAINY; break;
		case 305: resp = RAINY; break;
		case 302: resp = RAINY; break;
		case 299: resp = RAINY; break;
		case 296: resp = RAINY; break;
		case 293: resp = RAINY; break;
		case 284: resp = RAINY; break;
		case 281: resp = RAINY; break;
		case 266: resp = RAINY; break;
		case 263: resp = RAINY; break;
		case 260: resp = FOGGY; break;
		case 248: resp = FOGGY; break;
		case 230: resp = STORMY; break;
		case 227: resp = SNOWY; break;
		case 200: resp = STORMY; break;
		case 185: resp = RAINY; break;
		case 182: resp = SNOWY; break;
		case 179: resp = SNOWY; break;
		case 176: resp = RAINY; break;
		case 143: resp = FOGGY; break;
		case 122: resp = CLOUDY; break;
		case 119: resp = CLOUDY; break;
		case 116: resp = CLOUDY; break;
		case 113: resp = SUNNY; break;
		default: resp = CLOUDY; break;
		}
		return resp;
	}
	
	private MenuItem about = new MenuItem("About", 1, 1) {
		public void run() {
			WeatherApp.get().showAbout();
		}
	};
	private MenuItem feature = new MenuItem("Request for Feature", 1, 1) {
		public void run() {
			WeatherApp.get().showFeatureRequest();
		}
	};
	private MenuItem other = new MenuItem("Other app by DurianApp", 1, 1) {
		public void run() {
			WeatherApp.get().showOtherApp();
		}
	};
	private MenuItem facebook = new MenuItem("Share to Facebook", 1, 1) {
		public void run() {
			WeatherApp.get().shareToFacebook();
		}
	};
	private MenuItem twitter = new MenuItem("Share to Twitter", 1, 1) {
		public void run() {
			WeatherApp.get().shareToTwitter();
		}
	};
	private MenuItem email = new MenuItem("Share by Email", 1, 1) {
		public void run() {
			WeatherApp.get().shareByEmail();
		}
	};
	protected void makeMenu(Menu menu, int instance) {
		menu.add(about);
		menu.addSeparator();
		menu.add(feature);
		menu.add(other);
		menu.addSeparator();
		menu.add(facebook);
		menu.add(twitter);
		menu.add(email);
	}
}
