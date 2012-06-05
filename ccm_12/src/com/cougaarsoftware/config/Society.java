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

import java.util.Collection;

/**
 * a container for societies and their children
 * 
 * @author mabrams
 */
public interface Society extends Component {
//	/**
//	 * get a list of the Node Component objects in this society
//	 */
//	public Map getNodeMap();
	public Collection getNodeComponents();

	/**
	 * check to see if the society contains the component
	 */
	public boolean contains(Component component);
	/**
	 * add a node to the society
	 */
	public NodeComponent addNode(NodeComponent node);
	/**
	 * remove a node from the society
	 */
	public NodeComponent removeNode(NodeComponent node);
	/**
	 * remove a agent from the society
	 */
	public boolean removeAgent(AgentComponent agent);
	/**
	 * get an agent from the society
	 */
	public AgentComponent getAgent(AgentComponent agent);
	/**
	 * get an agent from the society
	 */
	public AgentComponent getAgent(String agentName);
	/**
	 * get the node with the name specified as a parameter
	 * 
	 * @param nodeName
	 * @return the node component with the name specified or null if not found
	 */
	public NodeComponent getNode(String nodeName);
}