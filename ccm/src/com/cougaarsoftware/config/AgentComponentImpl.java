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
import java.util.Vector;

import org.cougaar.core.component.ComponentDescription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.util.UID;

import com.cougaarsoftware.config.util.HashCodeUtil;

/**
 * @author mabrams
 */
public class AgentComponentImpl extends ComponentImpl implements AgentComponent {
	private Vector childComponents;
	private String parentNode;
	private boolean isNodeAgent = false;
	private String host;
	private Map commands;
	private MessageAddress messageAddress;

	/**
	 * default constructor
	 *  
	 */
	public AgentComponentImpl(String name, UID uid, MessageAddress messageAddress) {
		super(name, uid);
		this.messageAddress = messageAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#getChildComponents()
	 */
	public Vector getChildComponents() {
		return childComponents;
	}

	public void addChildComponent(ComponentDescription childComponent) {
		if (childComponents == null) {
			childComponents = new Vector();
		}
		childComponents.add(childComponent);
	}

	public boolean removeChildComponent(ComponentDescription childComponent) {
		if (childComponents != null) {
			return childComponents.remove(childComponent);
		}
		return false;
	}

	public boolean contains(ComponentDescription component) {
		if (childComponents != null) {
			Iterator i = this.childComponents.iterator();
			while (i.hasNext()) {
				ComponentDescription cd = (ComponentDescription) i.next();
				if (cd.equals(component)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#getParentNode()
	 */
	public String getParentNode() {
		return parentNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#setParentNode(java.lang.String)
	 */
	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	public boolean equals(Object o) {
		if (o instanceof AgentComponent) {
			return equals((AgentComponent) o);
		} else {
			return (this == o);
		}
	}

	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, parentNode);
		result = HashCodeUtil.hash(result, getName());
		result = HashCodeUtil.hash(result, childComponents.size());
		result = HashCodeUtil.hash(result, childComponents);
		return result;
	}

	public boolean equals(AgentComponent aComponent) {
		if (aComponent.getName().equals(this.getName())
				&& aComponent.getStatus() == this.getStatus()) {
			if (aComponent.getParentNode().equals(this.getParentNode())) {
				Vector aChildComponents = aComponent.getChildComponents();
				if (aChildComponents != null && this.childComponents != null) {
					Iterator i = aChildComponents.iterator();
					while (i.hasNext()) {
						ComponentDescription aCd = (ComponentDescription) i.next();
						Iterator j = this.childComponents.iterator();
						boolean matched = false;
						while (j.hasNext()) {
							ComponentDescription cd = (ComponentDescription) j.next();
							if (cd.equals(aCd)) {
								matched = true;
								break;
							}
						}
						if (!matched) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	public String toString() {
		return this.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#isNodeAgent()
	 */
	public boolean isNodeAgent() {
		return isNodeAgent;
	}

	public void setIsNodeAgent(boolean value) {
		this.isNodeAgent = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#setHost(java.lang.String)
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#getHost()
	 */
	public String getHost() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#getCommands()
	 */
	public Map getCommands() {
		return commands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#setCommands()
	 */
	public void setCommands(Map commands) {
		this.commands = commands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#addCommand(com.cougaarsoftware.config.Command)
	 */
	public void addCommand(Capability command) {
		if (commands == null) {
			commands = Collections.synchronizedMap(new HashMap());
		}
		Vector commandList = (Vector) commands.get(command.getComponentClass());
		if (commandList == null) {
			commandList = new Vector();
			commandList.add(command);
			commands.put(command.getComponentClass(), command);
		} else {
			commandList.add(command);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.AgentComponent#removeCommand(com.cougaarsoftware.config.Command)
	 */
	public void removeCommand(Capability command) {
		if (commands != null) {
			Vector commandList = (Vector) commands.get(command.getComponentClass());
			if (commandList != null) {
				commandList.remove(command);
			}			
		}		
	}

	/* (non-Javadoc)
	 * @see com.cougaarsoftware.config.AgentComponent#getMessageAddress()
	 */
	public MessageAddress getMessageAddress() {
		return this.messageAddress;
	}

	/* (non-Javadoc)
	 * @see com.cougaarsoftware.config.AgentComponent#setMessageAddress(org.cougaar.core.mts.MessageAddress)
	 */
	public void setMessageAddress(MessageAddress address) {
		this.messageAddress = address;
		
	}
}