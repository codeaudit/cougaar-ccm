/*
 * <copyright>
 * 
 * Copyright 2000-2004 Cougaar Software, Inc. under sponsorship of the Defense
 * Advanced Research Projects Agency (DARPA).
 * 
 * You can redistribute this software and/or modify it under the terms of the
 * Cougaar Open Source License as published on the Cougaar Open Source Website
 * (www.cougaar.org).
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * </copyright>
 */
package com.cougaarsoftware.config.gui;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.Society;
import com.touchgraph.graphlayout.Node;

/**
 * the main gui for viewing cougaar societies from within cougaar
 * 
 * @author mabrams
 */
public abstract class ConfigViewerGraphPanel extends JPanel {
	protected ConfigViewerController controller;    
	protected boolean showComponents = true;

	/**
	 * Creates a new ConfigViewerGUI object.
	 * 
	 * @param controller
	 *          DOCUMENT ME!
	 */
	public ConfigViewerGraphPanel(ConfigViewerController controller) {
		super();
		this.controller = controller;
		initGUI();
	}

	/**
	 * construct GUI
	 */
	protected abstract void initGUI();

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getPanelName() {
		return "ConfigViewer";
	}

	public abstract void setLocale(Node n);

	

	/**
	 * @param component
	 */
	public abstract void setFocus(String nodeID);

	/**
	 *  
	 */
	public void showRemoveAgentDialog(String agentName, String nodeName) {
		if (controller.getAgentId().equals(
				agentName.substring(agentName.lastIndexOf(':') + 1))) {
			JOptionPane.showMessageDialog(this, "You have attempted to remove the "
					+ "agent that this gui is running on.  " + "This is not allowed");
		} else if (agentName.substring(agentName.lastIndexOf(':') + 1).equals(
				nodeName)) {
			JOptionPane.showMessageDialog(this, "You have attempted to remove the "
					+ "node agent.  If you want " + "to remove the node, right click "
					+ "on the node and select \'Remove Node\'.");
		} else {
			int n = JOptionPane.showConfirmDialog(this, "Remove Agent: " + agentName
					+ "?", "Remove Agent", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				controller.removeAgent(agentName, nodeName);
			}
			Society society = controller.getSocietyConfiguration(false);
			update(society);
		}
	}

    /**
     * @param society
     */
    protected abstract void update(Society society);
    
    public void setShowComponents(boolean show) {
        this.showComponents = show;
    }

    /**
     * @param component
     */
    public abstract void displayGraph(Component component);
    
    public ConfigViewerController getController() {
        return controller;
    }

    /**
     * @param agentComponent
     * @param object
     * @param popupMenu
     */
    public void addAgentComponent(AgentComponent agentComponent, Object object, JPopupMenu popupMenu) {
    }
       
}