package com.durianberry.weather.task;

import javax.microedition.location.Coordinates;

public class LocationCoordinates extends Coordinates {
	private String city, address;
	public LocationCoordinates(double latitude, double longitude, float altitude, String city) {
		super(latitude, longitude, altitude);
		setCity(city);
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
