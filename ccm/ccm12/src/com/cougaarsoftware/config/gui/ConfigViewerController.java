/*
 * <copyright> Copyright 2000-2004 Cougaar Software, Inc. All Rights Reserved
 * </copyright>
 */
package com.cougaarsoftware.config.gui;

import org.cougaar.core.mts.MessageAddress;

import com.cougaarsoftware.config.Capability;
import com.cougaarsoftware.config.Society;


/**
 * @author mhelmstetter
 * @version $Revision: 1.1 $
 * 
 */
public interface ConfigViewerController {
    
    public final static String PARAM_GRAPH_PANEL_CLASS = "graphPanelClass";
    
    public String getAgentId();
    
    public void removeAgent(String agentName, String nodeName);
    
    public Society getSocietyConfiguration(boolean inTransaction);
    
    public void executeCommand(Capability command, MessageAddress agentAddress);
    
    public String getGraphPanelClass();
    
}
