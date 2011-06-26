package com.durianberry.bbcommons.connectionmanager;


public class ServiceBookConfig extends AbstractConfiguration {
	
    protected static final String CONFIG_DESCRIPTION = "Service book Configuration";

	public ServiceBookConfig() {
		super();
        setDescription(CONFIG_DESCRIPTION);
        
        //config url could change. we set it during open conn call
	}
}
