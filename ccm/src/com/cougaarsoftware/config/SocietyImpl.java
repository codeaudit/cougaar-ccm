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
package com.cougaarsoftware.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cougaar.core.util.UID;

/**
 * @author mabrams
 * 
 * implementation of the society container
 */
public class SocietyImpl extends ComponentImpl implements Society {
	private Map nodeMap;

	public SocietyImpl(String name, UID uid) {
		super(name, uid);
	}

	//	/*
	//	 * (non-Javadoc)
	//	 *
	//	 * @see com.cougaarsoftware.config.Society#getNodeList()
	//	 */
	//	public Map getNodeMap() {
	//		return nodeMap;
	//	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Society#contains(com.cougaarsoftware.config.Component)
	 */
	public boolean contains(Component component) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Society#addNode(com.cougaarsoftware.config.NodeComponent)
	 */
	public NodeComponent addNode(NodeComponent node) {
		if (nodeMap == null) {
			nodeMap = Collections.synchronizedMap(new HashMap());
		}
		return (NodeComponent) nodeMap.put(node.getName(), node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Society#removeNode(com.cougaarsoftware.config.NodeComponent)
	 */
	public NodeComponent removeNode(NodeComponent node) {
		if (nodeMap != null) {
			return (NodeComponent) nodeMap.remove(node.getName());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Society#getNode(java.lang.String)
	 */
	public NodeComponent getNode(String nodeName) {
		if (nodeMap != null) {
			return (NodeComponent) nodeMap.get(nodeName);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Society#removeAgent(com.cougaarsoftware.config.AgentComponent)
	 */
	public boolean removeAgent(AgentComponent agent) {
		NodeComponent nc = getNode(agent.getParentNode());
		if (nc != null) {
			return nc.removeAgent(agent);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Society#getAgent(com.cougaarsoftware.config.AgentComponent)
	 */
	public AgentComponent getAgent(AgentComponent agent) {
		NodeComponent nc = getNode(agent.getParentNode());
		if (nc != null) {
			return nc.getAgent(agent);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Society#getAgent(java.lang.String)
	 */
	public AgentComponent getAgent(String agentName) {
		if (nodeMap != null) {
			Set keys = nodeMap.keySet();
			synchronized (nodeMap) {
				Iterator i = keys.iterator();
				while (i.hasNext()) {
					String nodeName = (String) i.next();
					NodeComponent nc = (NodeComponent) nodeMap.get(nodeName);
					AgentComponent ac = nc.getAgent(agentName);
					if (ac != null) {
						return ac;
					}
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cougaarsoftware.config.Society#getNodeComponents()
	 */
	public Collection getNodeComponents() {
		List nodes = null;
		if (nodeMap != null) {
			synchronized(nodeMap) {
				Set keys = nodeMap.keySet();
				Iterator i = keys.iterator();
				while (i.hasNext()) {
					String nodeName = (String) i.next();
					NodeComponent nc = (NodeComponent) nodeMap.get(nodeName);
					if (nodes == null) {
						nodes = new ArrayList();
					}
					nodes.add(nc);
				}
			}
		}
		return nodes;
	}

}