package com.durianberry.bbcommons.connectionmanager;


public class WiFiConfig extends AbstractConfiguration {
	
    protected static final String CONFIG_DESCRIPTION = "Wi-Fi Network";
  
	public WiFiConfig() {
		super();
		setUrlParameters(";interface=wifi");
		setDescription(CONFIG_DESCRIPTION);
	}
}
