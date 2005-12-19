/*
 * <copyright> Copyright 2000-2004 Cougaar Software, Inc. All Rights Reserved
 * </copyright>
 */
package com.cougaarsoftware.config;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.ComponentPlugin;

import com.cougaarsoftware.config.service.ConfigurationService;
/**
 * @author mabrams
 */
public class AgentAdderPlugin extends ComponentPlugin {
	private ConfigurationService configService;
	private ConfigurationService getConfigurationService() {
		if (configService == null) {
			configService = (ConfigurationService) getServiceBroker()
					.getService(this, ConfigurationService.class, null);
		}
		return configService;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.plugin.ComponentPlugin#setupSubscriptions()
	 */
	protected void setupSubscriptions() {
		// TODO Auto-generated method stub
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.plugin.ComponentPlugin#execute()
	 */
	protected void execute() {
     
      ConfigurationService cs = getConfigurationService();
      if (cs != null) {
          cs.addAgent("TestAgent", MessageAddress
                  .getMessageAddress("TestNode"));         
      }
 
	}
}