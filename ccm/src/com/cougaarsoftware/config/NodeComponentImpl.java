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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.cougaar.core.util.UID;
import com.cougaarsoftware.config.util.HashCodeUtil;

/**
 * @author mabrams
 *  
 */
public class NodeComponentImpl extends ComponentImpl implements NodeComponent {
	private Map childComponents;

	public NodeComponentImpl(String name, UID uid) {
		super(name, uid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.NodeComponent#getChildComponents()
	 */
	public Map getAgents() {
		Map agents = null;
		if (childComponents != null) {
			Set keys = childComponents.keySet();
			synchronized (childComponents) {
				Iterator i = keys.iterator();
				while (i.hasNext()) {
					String key = (String)i.next();
					Object o = childComponents.get(key);
					if (o instanceof Component) {
						AgentComponent child = (AgentComponent) o;
						if (agents == null) {
							agents = new HashMap();
						}
						agents.put(child.getName(), child);
					}
				}
			}
		}
		return agents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.NodeComponent#addAgent(org.cougaar.core.component.ComponentDescription)
	 */
	public AgentComponent addAgent(AgentComponent childComponent) {
		if (childComponents == null) {
			childComponents = Collections.synchronizedMap(new HashMap());
		}
		return (AgentComponent) childComponents.put(childComponent.getName(),
				childComponent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.NodeComponent#contains(org.cougaar.core.component.ComponentDescription)
	 */
	public boolean contains(AgentComponent component) {
		if (childComponents != null
				&& childComponents.containsKey(component.getName())) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.NodeComponent#removeAgent(org.cougaar.core.component.ComponentDescription)
	 */
	public boolean removeAgent(AgentComponent childComponent) {
		if (childComponents != null) {
			Object o = childComponents.remove(childComponent.getName());
			if (o != null) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return this.getName();
	}

	public boolean equals(Object o) {
		if (o instanceof NodeComponent) {
			NodeComponent cNode = (NodeComponent) o;
			if (cNode.getName().equals(this.getName())) {
				Map cAgents = cNode.getAgents();
				Map agents = this.getAgents();
				if (cAgents == null && agents == null) {
					return true;
				} else if (cAgents != null && agents != null) {
					if (cAgents.size() == agents.size()) {
						Set keys = agents.keySet();
						synchronized (agents) {
							Iterator i = keys.iterator();
							while (i.hasNext()) {
								String agentName = (String) i.next();
								AgentComponent aComponent = (AgentComponent) agents
										.get(agentName);
								AgentComponent cComponent = (AgentComponent) cAgents
										.get(agentName);
								if (aComponent != null && cComponent != null) {
									if (!aComponent.equals(cComponent)) {
										return false;
									}
								} else {
									return false;
								}
							}
						}
					} else {
						return false;
					}
				} else if (cAgents != null && agents == null || cAgents == null
						&& agents != null) {
					return false;
				}
			} else {
				return false;
			}
			return true;
		} else {
			return (this == o);
		}
	}

	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, getName());
		result = HashCodeUtil.hash(result, childComponents.size());
		result = HashCodeUtil.hash(result, childComponents);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.NodeComponent#getAgent(com.cougaarsoftware.config.AgentComponent)
	 */
	public AgentComponent getAgent(AgentComponent agentComponent) {
		if (childComponents != null) {
			return getAgent(agentComponent.getName());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.NodeComponent#getAgent(java.lang.String)
	 */
	public AgentComponent getAgent(String agentName) {
		if (childComponents != null) {
			AgentComponent ac = (AgentComponent) childComponents.get(agentName);
			return ac;
		}
		return null;
	}
}