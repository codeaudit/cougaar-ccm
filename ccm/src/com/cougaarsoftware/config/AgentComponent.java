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

import java.util.Map;
import java.util.Vector;

import org.cougaar.core.component.ComponentDescription;
import org.cougaar.core.mts.MessageAddress;

/**
 * @author mabrams
 */
public interface AgentComponent extends Component {
	/**
	 * get all child components for this agent
	 * 
	 * @return the list of child components for this agent
	 */
	public Vector getChildComponents();

	/**
	 * add a child component to this agent
	 * 
	 * @param childComponent
	 */
	public void addChildComponent(ComponentDescription childComponent);

	/**
	 * check to see if the component is a child of this agent
	 * 
	 * @param component
	 * @return true if the component is a child of this agent
	 */
	public boolean contains(ComponentDescription component);

	/**
	 * remove a child component from this agent
	 * 
	 * @param childComponent
	 * @return true if the component was removed
	 */
	public boolean removeChildComponent(ComponentDescription childComponent);

	/**
	 * get the parent node for this agent
	 * 
	 * @return the parent node for this agent
	 */
	public String getParentNode();

	/**
	 * set the parent node for this agent
	 * 
	 * @param parentNoe
	 */
	public void setParentNode(String parentNode);

	/**
	 * true if this agent is the node agent
	 * 
	 * @return
	 */
	public boolean isNodeAgent();

	/**
	 * set the value of the node agent flag
	 * 
	 * @param value
	 */
	public void setIsNodeAgent(boolean value);

	/**
	 * set the host this agent is on
	 * 
	 * @param host
	 */
	public void setHost(String host);

	/**
	 * get the host this agent is on
	 * 
	 * @return
	 */
	public String getHost();
	
	public Map getCommands();
	
	public void setCommands(Map commands);
	
	public void addCommand(Command command);
	public void removeCommand(Command command);
	
	public MessageAddress getMessageAddress();
	public void setMessageAddress(MessageAddress address);
}