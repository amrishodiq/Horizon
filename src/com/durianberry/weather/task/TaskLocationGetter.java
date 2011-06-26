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
import com.durianberry.weather.task.LocationException;

public class TaskLocationGetter extends TaskImpl {
	private int lac, cellId;
	
	public TaskLocationGetter(int lac, int cellId) {
		this.lac = lac;
		this.cellId = cellId;
	}

	/**
	 * TaskImpl's execute method
	 */
	public void execute() {
		String url = "http://www.durianapp.com/index.php/api/location/geocode/" + String.valueOf(lac) + "/" + String.valueOf(cellId);
		
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
			if (jo.getJSONObject("response").has("lat") && jo.getJSONObject("response").has("lng")) {
				double lat = jo.getJSONObject("response").getDouble("lat");
				double lng = jo.getJSONObject("response").getDouble("lng");
				String city = jo.getJSONObject("response").getString("city");
				String address = jo.getJSONObject("response").getString("address");
				
				LocationCoordinates response = new LocationCoordinates(lat, lng, 0, city);
				response.setAddress(address);
				
				System.out.println("Updating progress listener");
				if(progressListener != null)
					progressListener.taskUpdate(response);
				
				System.out.println("Done");
			} else {
				if (jo.getJSONObject("meta").has("message"))
				if(progressListener != null)
					progressListener.taskComplete(new LocationException(jo.getJSONObject("meta").optInt("status"), jo.getJSONObject("meta").optString("message")));
			}
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

