package com.durianberry.weather.task;

public class LocationException extends Exception {
	private int status = 0;
	private String message;
	public LocationException(int status, String message) {
		this.status = status;
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public int getStatus() {
		return status;
	}
}
