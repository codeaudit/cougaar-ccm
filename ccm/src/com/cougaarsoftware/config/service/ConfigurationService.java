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
package com.cougaarsoftware.config.service;
import org.cougaar.core.blackboard.BlackboardClient;
import org.cougaar.core.component.ComponentDescription;
import org.cougaar.core.component.Service;
import org.cougaar.core.mts.MessageAddress;
import com.cougaarsoftware.config.AgentComponent;
/**
 * The ConfigurationService provides utility methods for adding, removing, and
 * modifying plugins and agents
 * 
 * @author mabrams
 */
public interface ConfigurationService extends Service, BlackboardClient {
	/**
	 * add a new agent to the node. the agent name specified must have a
	 * corresponding AGENT-NAME.xml file located int he config path for the node
	 * that the agent is going to be loaded on.
	 * 
	 * @param agentName -
	 *          the name of the agent to load
	 * @param nodeAgentId -
	 *          the destination node for the agent to be added to
	 */
	public void addAgent(String agentName, MessageAddress destNodeAgent);
	/**
	 * remove an agent from the specified node
	 * 
	 * @param agentName
	 *          the name of the agent to remove
	 * @param nodeAgentId
	 *          the name of the node that the agent resides on
	 */
	public void removeAgent(String agentName, MessageAddress nodeAgentId);
	/**
	 * initialize a new agent by adding the components defined in the agent value
	 * object representation to the actual Cougaar agent
	 * 
	 * @param agent
	 *          the value object representation of the agent
	 */
	public void initializeAgent(AgentComponent agent);
	/**
	 * add a component to the agent this service is running on
	 * 
	 * @param pluginClassName
	 */
	public void addComponent(String className);
	/**
	 * add a component to the agent this service is running on
	 * 
	 * @param componentDescription -
	 *          the component description for the component to be added
	 */
	public void addComponent(ComponentDescription componentDescription);
	/**
	 * remove a component from the agent where the service was invoked from
	 * 
	 * @param componentName
	 *          the compoenent to remove
	 * @return true if the component was removed
	 */
	public boolean removeComponent(String componentName);
}