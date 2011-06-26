package com.durianberry.weather.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.HttpConnection;
import javax.microedition.location.Coordinates;

import net.rim.device.api.ui.component.Dialog;

import com.durianberry.bbcommons.connectionmanager.ConnectionManager;
import com.durianberry.bbcommons.json.JSONException;
import com.durianberry.bbcommons.json.JSONObject;
import com.durianberry.bbcommons.task.TaskImpl;
import com.durianberry.weather.app.WeatherApp;
import com.durianberry.weather.model.WeatherResponse;

public class TaskWeatherGetter extends TaskImpl {
	private Coordinates location;
	private String KEY_WORLDWEATHERONLINE = "2effa1a408174002112005";
	
	public TaskWeatherGetter(Coordinates location) {
		this.location = location;
	}
	
	public void execute() {
		String url = "http://free.worldweatheronline.com/feed/weather.ashx?key="+KEY_WORLDWEATHERONLINE+"&q="+String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude())+"&num_of_days=5&format=json";
		
//		WeatherApp.get().showMessage("URL: "+url);
		
		HttpConnection conn = null;
		byte[] resp = null;
		int resCode = 0;
		String resMsg = "";
		InputStream in = null;
		
		try {
			conn = (HttpConnection) ConnectionManager.getInstance().open(url);

			resCode = conn.getResponseCode();
			resMsg = conn.getResponseMessage();
			
			System.out.println("RC: "+resCode);
			System.out.println("RM: "+resMsg);
			
			// whatever the response code, get the content
			in = conn.openInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int c;
			while ((c = in.read()) >= 0) {
				baos.write(c);
			}

			resp = baos.toByteArray();
			
			System.out.println("Baos size: "+baos.size());
			System.out.println("Resp: "+new String(resp));
			
//			WeatherApp.get().showMessage("Response: "+new String(resp));
			
			if (baos.size() == 0)
				throw new Exception(conn.getResponseMessage());
			
			JSONObject jo = new JSONObject(new String(resp));
			
			WeatherResponse response = new WeatherResponse(jo);
			
//			JSONArray ja = jo.getJSONObject("data").getJSONArray("weather");
//			int len = ja.length();
//			int i = 0;
//			Weather[] response = new Weather[len];
//			while (i<len) {
//				response[i] = new Weather(ja.getJSONObject(i));
//				i++;
//			}
			
			System.out.println("Updating progress listener");
			if(progressListener != null)
				progressListener.taskUpdate(response);
			
			System.out.println("Done");
		} catch (Exception e) {
			if(progressListener != null) {
				if (e instanceof JSONException)
					progressListener.taskComplete(new Exception("Oops, server is unavailable right now. Try again later."));
				else
					progressListener.taskComplete(e);
			}
			return;
		} catch (Throwable e) {
			if(progressListener != null)
				progressListener.taskComplete(e);
			
			return;
		} finally {
			System.out.println("Try to close connection");
			try {
			    if (conn != null) conn.close();
			    if (in != null) in.close();
			} catch (IOException ioe) {
			} finally {
			    conn = null;
			    in = null;
			}
		}
		
		if(progressListener != null)
			progressListener.taskComplete(null);
	}

}
