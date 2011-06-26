package com.durianberry.weather.model;

import com.durianberry.bbcommons.json.JSONArray;
import com.durianberry.bbcommons.json.JSONException;
import com.durianberry.bbcommons.json.JSONObject;

public class WeatherResponse {
	private Weather[] weathers;
	
	public WeatherResponse(JSONObject jo) throws JSONException {
		JSONArray ar = jo.getJSONObject("data").getJSONArray("weather");
		int len = ar.length();
		int i = 0;
		weathers = new Weather[len];
		while (i<len) {
			weathers[i] = new Weather(ar.getJSONObject(i));
			i++;
		}
	}
	
	public Weather[] getWeathers() {
		return weathers;
	}
}
