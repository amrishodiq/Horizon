package com.durianberry.weather.model;

import com.durianberry.bbcommons.json.JSONObject;

public class Weather {
	private JSONObject jo;
	public Weather(JSONObject jo) {
		this.jo = jo;
	}
	public String getDate() {
		return jo.optString("date");
	}
	public String getPrecipitation() {
		return jo.optString("precipMM");
	}
	public int getWeatherCode() {
		return Integer.parseInt(jo.optString("weatherCode"));
	}
	public int getTempMaxInC() {
		return Integer.parseInt(jo.optString("tempMaxC"));
	}
	public int getTempMinInC() {
		return Integer.parseInt(jo.optString("tempMinC"));
	}
	public int getTempMaxInF() {
		return Integer.parseInt(jo.optString("tempMaxF"));
	}
	public int getTempMinInF() {
		return Integer.parseInt(jo.optString("tempMinF"));
	}
	public String getWeatherDescription() {
		return jo.optJSONArray("weatherDesc").optJSONObject(0).optString("value");
	}
	public String getWindDirection() {
		return jo.optString("winddirection");
	}
	public int getWindSpeedInKm() {
		return Integer.parseInt(jo.optString("windspeedKmph"));
	}
	public int getWindSpeedInMiles() {
		return Integer.parseInt(jo.optString("windspeedMiles"));
	}
}
