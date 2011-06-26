package com.durianberry.weather.view;

import com.durianberry.weather.app.Config;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class WeatherManager extends VerticalFieldManager {
	private ColoredLabelField city, weatherDesc, temp, wind;
	
	public WeatherManager(String cityName, String weatherDescription, 
			String temperature, String windDescription) {
		city = new ColoredLabelField(cityName, 0xffffff, FIELD_LEFT);
		weatherDesc = new ColoredLabelField(weatherDescription, 0xffffff, FIELD_LEFT | FIELD_VCENTER);
		temp = new ColoredLabelField(temperature, 0xffffff, FIELD_LEFT);
		wind = new ColoredLabelField(windDescription, 0xffffff, FIELD_LEFT);
		
		city.setFont(Config.getMediumFont());
		weatherDesc.setFont(Config.getLargeFont());
		temp.setFont(Config.getSmallFont());
		wind.setFont(Config.getSmallFont());
		
		VerticalFieldManager v = new VerticalFieldManager();
		v.add(temp);
		v.add(wind);
		
		JustifiedVerticalFieldManager jv = new JustifiedVerticalFieldManager(city, weatherDesc, v);
		jv.setMargin(8, 8, 8, 8);
		add(jv);
	}
	
	protected boolean navigationMovement(final int dx, final int dy, final int status, final int time) {
        if (getManager() != null && getManager() instanceof TabManager) {
        	if (dx>0)
        		((TabManager)getManager()).right();
        	else
        		((TabManager)getManager()).left();
        }

        return super.navigationMovement(dx, dy, status, time);
    }
	
	public void paintBackground(Graphics g) {
		g.setBackgroundColor(0x6699ff);
		g.clear();
	}
	
	private class ColoredLabelField extends LabelField {
		private int color;
		public ColoredLabelField(String text, int color, long style) {
			super(text, style);
			this.color = color;
		}
		public void paint(Graphics g) {
			int color = g.getColor();
			g.setColor(this.color);
			super.paint(g);
			g.setColor(color);
		}
	}
}
