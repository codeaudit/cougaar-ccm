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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.cougaar.core.component.ComponentDescription;
import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.NodeComponent;
import com.cougaarsoftware.config.Society;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPanel;

/**
 * Configuration view panel parses the society object from the blackboard and
 * creates a TouchGraph panel from the information
 * 
 * @author mabrams
 */
public class TGConfigurationViewPanel extends TGPanel {
	private Society society;
	private ConfigViewerGUI gui;

	/**
	 * creates a new TGConfigurationViewPanel object
	 */
	public TGConfigurationViewPanel(ConfigViewerGUI gui) {
		this.gui = gui;
	}

	/**
	 * remove all nodes and rebuild graph; called after changing the configuration
	 */
	public void rebuildGraph(Society society) {
		clearAll();
		processConfiguration(society);
	}

	/**
	 * build the graph of the configuration
	 */
	public void processConfiguration(Society society) {
		try {
			if (society != null) {
				ConfigNode societyNode = (ConfigNode) gui.completeEltSet
						.findNode("Society");
				if (societyNode == null) {
					societyNode = new ConfigNode("Society", "Society",
							ConfigNode.COMPONENT_TYPE);
					gui.completeEltSet.addNode(societyNode);
				}
				Map nodeMap = society.getNodeMap();
				if (nodeMap != null) {
					Set keys = nodeMap.keySet();
					Iterator nodeIter = keys.iterator();
					while (nodeIter.hasNext()) {
						String nodeName = (String) nodeIter.next();
						NodeComponent node = (NodeComponent) nodeMap.get(nodeName);
						processNode(node, societyNode, nodeName);
					}
				}
			}
		} catch (TGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ConfigNode addNodeNode(ConfigNode nodeNode, ConfigNode societyNode,
			String nodeName) throws TGException {
		nodeNode = new ConfigNode(nodeName, getShortName(nodeName),
				ConfigNode.NODE_TYPE);
		gui.completeEltSet.addNode(nodeNode);
		ConfigEdge edge = new ConfigEdge(societyNode, nodeNode, 30);
		gui.completeEltSet.addEdge(edge);
		nodeNode.setType(ConfigNode.TYPE_ELLIPSE);
		return nodeNode;
	}

	private Vector getExistingAgents(ConfigNode node) {		
		Iterator i = node.getEdges();
		Vector oldAgents = null;
		while (i.hasNext()) {
			ConfigEdge configEdge = (ConfigEdge) i.next();
			if (configEdge.getFrom().equals(node)) {
				if (oldAgents == null) {
					oldAgents = new Vector();
				}
				oldAgents.add(configEdge.getTo());
			}
		}
		i = null;
		return oldAgents;
	}

	/**
	 * @param node
	 */
	private void processNode(NodeComponent node, ConfigNode societyNode,
			String nodeName) throws TGException {
		ConfigNode nodeNode = (ConfigNode) gui.completeEltSet.findNode(nodeName);
		if (nodeNode == null && node.getStatus() == Component.HEALTHY
				|| node.getStatus() == Component.UNKNOWN) {
			nodeNode = addNodeNode(nodeNode, societyNode, nodeName);
		} else if (nodeNode != null && node.getStatus() == Component.DEAD) {
			gui.completeEltSet.deleteNode(nodeNode);
		}
		if (node.getStatus() == Component.HEALTHY
				|| node.getStatus() == Component.UNKNOWN) {
			
			Vector oldAgents = null;
			if (nodeNode != null) {
				oldAgents = getExistingAgents(nodeNode);
			}
			Map agentMap = node.getAgents();
			Vector newAgents = null;
			if (agentMap != null) {
				Set agentKeys = agentMap.keySet();
				Iterator agentIter = agentKeys.iterator();
				while (agentIter.hasNext()) {
					AgentComponent ac = (AgentComponent) agentMap.get(agentIter.next());
					String agentName = ac.getName();
					ConfigNode agentNode = (ConfigNode) gui.completeEltSet
							.findNode(agentName);
					if (newAgents == null) {
						newAgents = new Vector();
					}
					newAgents.add(agentNode);
					processAgent(agentNode, ac, nodeNode, agentName);
				}
				agentIter = null;
			}
			checkForRemovedNodes(newAgents, oldAgents);
		}
		if (this.getAllNodes().hasNext()) {
			ConfigNode initialNode = (ConfigNode) this.getAllNodes().next();
			if (initialNode != null) {
				this.setSelect(initialNode);
				gui.setLocale(initialNode);
			}
		}
	}

	/**
	 * @param newAgents
	 * @param oldAgents
	 */
	private void checkForRemovedNodes(Vector newAgents, Vector oldAgents) {
		if (newAgents != null && oldAgents != null) {
			Iterator iter = newAgents.iterator();
			while (iter.hasNext()) {
				ConfigNode agentNode = (ConfigNode) iter.next();
				oldAgents.remove(agentNode);
			}
			iter = null;
			iter = oldAgents.iterator();
			while (iter.hasNext()) {
				ConfigNode agentNode = (ConfigNode) iter.next();
				gui.completeEltSet.deleteNode(agentNode);
			}
		}
	}

	/**
	 * @param ac
	 * @param nodeNode
	 * @param agentName
	 */
	private void processAgent(ConfigNode agentNode, AgentComponent ac,
			ConfigNode nodeNode, String agentName) throws TGException {
		if (agentNode == null && ac.getStatus() == Component.HEALTHY
				|| ac.getStatus() == Component.UNKNOWN) {
			agentNode = new ConfigNode(agentName, getShortName(agentName),
					ConfigNode.AGENT_TYPE, ac.getMessageAddress(), ac.getCommands());
			gui.completeEltSet.addNode(agentNode);
			agentNode.setType(ConfigNode.TYPE_ROUNDRECT);
			ConfigEdge edge = new ConfigEdge(nodeNode, agentNode, 40);
			gui.completeEltSet.addEdge(edge);
		} else if (agentNode != null && ac.getStatus() == Component.DEAD) {
			gui.completeEltSet.deleteNode(agentNode);
		}
		if (ac.getStatus() == Component.HEALTHY
				|| ac.getStatus() == Component.UNKNOWN) {
			Iterator agentCompIter = ac.getChildComponents().iterator();
			while (agentCompIter.hasNext()) {
				ComponentDescription cd = (ComponentDescription) agentCompIter.next();
				ConfigNode compNode = (ConfigNode) gui.completeEltSet.findNode(nodeNode
						.getLabel()
						+ "." + agentName + "." + cd.getName());
				if (compNode == null) {
					compNode = new ConfigNode(nodeNode.getLabel() + "." + agentName + "."
							+ cd.getName(), getShortName(cd.getName()),
							ConfigNode.COMPONENT_TYPE);
					gui.completeEltSet.addNode(compNode);
					compNode.setType(ConfigNode.TYPE_RECTANGLE);
					ConfigEdge edge = new ConfigEdge(agentNode, compNode, 70);
					gui.completeEltSet.addEdge(edge);
				}
			}
			agentCompIter = null;
		}
	}

	private String getShortName(String longName) {
		String shortName = "";
		if (longName.lastIndexOf('.') > 0) {
			shortName = longName.substring(longName.lastIndexOf('.') + 1);
		} else {
			shortName = longName;
		}
		return shortName;
	}

	/**
	 * get the society object displayed in this panel
	 * 
	 * @return Returns the society.
	 */
	public com.cougaarsoftware.config.Society getSociety() {
		return society;
	}

	/**
	 * set the society object used by this panel and rebuild the graph
	 * 
	 * @param society
	 *          The society to set.
	 */
	public void setSociety(com.cougaarsoftware.config.Society society) {
		this.society = society;
	}
}