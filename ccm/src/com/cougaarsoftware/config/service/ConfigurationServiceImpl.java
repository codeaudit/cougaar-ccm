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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.cougaar.core.blackboard.BlackboardClient;
import org.cougaar.core.component.Binder;
import org.cougaar.core.component.ComponentDescription;
import org.cougaar.core.component.ComponentDescriptions;
import org.cougaar.core.component.ServiceAvailableEvent;
import org.cougaar.core.component.ServiceAvailableListener;
import org.cougaar.core.component.ServiceBroker;
import org.cougaar.core.component.ServiceListener;
import org.cougaar.core.component.StateTuple;
import org.cougaar.core.mobility.AddTicket;
import org.cougaar.core.mobility.RemoveTicket;
import org.cougaar.core.mobility.ldm.AgentControl;
import org.cougaar.core.mobility.ldm.MobilityFactory;
import org.cougaar.core.mobility.ldm.TicketIdentifier;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.PluginBase;
import org.cougaar.core.service.AgentContainmentService;
import org.cougaar.core.service.AgentIdentificationService;
import org.cougaar.core.service.BlackboardService;
import org.cougaar.core.service.DomainService;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.ThreadService;
import org.cougaar.core.service.UIDService;
import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Component;

/**
 * implementation of the ConfigurationService interface
 * 
 * @author mabrams
 */
public class ConfigurationServiceImpl
		implements
			ConfigurationService,
			BlackboardClient {
	protected ServiceBroker sb;
	//logging service
	protected LoggingService logging;
	//BlackboardService
	protected BlackboardService blackboardService;
	//Thread Service
	protected ThreadService threadService;
	//Domain Service
	protected DomainService domainService;
	//AgentIdentificationService
	protected AgentIdentificationService aid;
	//UID Service
	protected UIDService uidService;
	//number of services
	protected int numberOfServices;
	//my listener for services I am interested
	protected ServiceListener serviceListener;
	private AgentContainmentService agentContainmentService;

	public ConfigurationServiceImpl(ServiceBroker sb) {
		this.sb = sb;
		init();
	}

	protected void init() {
		logging = (LoggingService) sb.getService(this, LoggingService.class, null);
		if (logging == null) {
			numberOfServices++;
		}
		threadService = (ThreadService) sb.getService(this, ThreadService.class,
				null);
		if (threadService == null) {
			numberOfServices++;
		}
		blackboardService = (BlackboardService) sb.getService(this,
				BlackboardService.class, null);
		if (blackboardService == null) {
			numberOfServices++;
		}
		domainService = (DomainService) sb.getService(this, DomainService.class,
				null);
		if (domainService == null) {
			numberOfServices++;
		}
		aid = (AgentIdentificationService) sb.getService(this,
				AgentIdentificationService.class, null);
		if (aid == null) {
			numberOfServices++;
		}
		uidService = (UIDService) sb.getService(this, UIDService.class, null);
		if (uidService == null) {
			numberOfServices++;
		}
		if (numberOfServices > 0) {
			serviceListener = new ServiceAvailableListener() {
				public void serviceAvailable(ServiceAvailableEvent event) {
					setupServices(event.getService());
				}
			};
			sb.addServiceListener(serviceListener);
		}
		agentContainmentService = (AgentContainmentService) sb.getService(this,
				AgentContainmentService.class, null);
		if (agentContainmentService == null) {
			numberOfServices++;
		}
	}

	/**
	 * Setup services called by a ServiceAvailableListener
	 * 
	 * @param serviceClass
	 *          The service class now available
	 */
	private void setupServices(Class serviceClass) {
		if (serviceClass.isAssignableFrom(BlackboardService.class)
				&& (blackboardService == null)) {
			blackboardService = (BlackboardService) sb.getService(this,
					BlackboardService.class, null);
			numberOfServices--;
		}
		if (serviceClass.isAssignableFrom(UIDService.class) && (uidService == null)) {
			uidService = (UIDService) sb.getService(this, UIDService.class, null);
			numberOfServices--;
		}
		if (serviceClass.isAssignableFrom(ThreadService.class)
				&& (threadService == null)) {
			threadService = (ThreadService) sb.getService(this, ThreadService.class,
					null);
			numberOfServices--;
		}
		if (serviceClass.isAssignableFrom(DomainService.class)
				&& (domainService == null)) {
			domainService = (DomainService) sb.getService(this, DomainService.class,
					null);
			numberOfServices--;
		}
		if (serviceClass.isAssignableFrom(AgentIdentificationService.class)
				&& (aid == null)) {
			aid = (AgentIdentificationService) sb.getService(this,
					AgentIdentificationService.class, null);
			numberOfServices--;
		}
		if (serviceClass.isAssignableFrom(LoggingService.class)
				&& (logging == null)) {
			logging = (LoggingService) sb
					.getService(this, LoggingService.class, null);
			numberOfServices--;
		}
		if (numberOfServices == 0) {
			sb.removeServiceListener(serviceListener);
			numberOfServices--;
		}
	}

	/**
	 * @see com.cougaarsoftware.core.configuration.services.ConfigurationService#addAgent(java.lang.String,
	 *      org.cougaar.core.mts.MessageAddress)
	 */
	public void addAgent(String agentName, MessageAddress destNodeAgent) {
		String agentClassName = "org.cougaar.core.agent.AgentImpl";
		ComponentDescription desc = new ComponentDescription(agentClassName,
				org.cougaar.core.agent.Agent.INSERTION_POINT, agentClassName, null,
				MessageAddress.getMessageAddress(agentName), null, null, null,
				ComponentDescription.PRIORITY_COMPONENT);
		//add the agent
		addAgent(agentName, destNodeAgent, desc);
	}

	/**
	 * @see com.cougaarsoftware.core.configuration.services.ConfigurationService#removeAgent(java.lang.String,
	 *      org.cougaar.core.mts.MessageAddress)
	 */
	public void removeAgent(String agentName, MessageAddress nodeAgentId) {
		if (logging.isDebugEnabled()) {
			logging.debug("Removing agent: " + agentName);
		}
		MobilityFactory mobilityFactory = (MobilityFactory) domainService
				.getFactory("mobility");
		MessageAddress mobileAgentAddress = MessageAddress
				.getMessageAddress(agentName);
		MessageAddress destNodeAddress = nodeAgentId;
		//Create ticket id
		TicketIdentifier ticketIdentifier = (TicketIdentifier) mobilityFactory
				.createTicketIdentifier();
		//Create remove ticket
		RemoveTicket removeticket = new RemoveTicket(ticketIdentifier,
				mobileAgentAddress, destNodeAddress);
		//Create Agent Control for removal of agent
		AgentControl removeControl = mobilityFactory.createAgentControl(null,
				destNodeAddress, removeticket);
		BlackboardAccess bbAccess = new BlackboardAccess(BlackboardAccess.ADD_TYPE);
		bbAccess.addObject(removeControl);
		threadService.getThread(this, bbAccess).schedule(0);
		if (logging.isDebugEnabled()) {
			logging
					.debug("Just sent Message thru MessageTransport to NodeAgent to remove agent - "
							+ agentName);
		}
	}
	/**
	 * Class for accessing the blackboard
	 * 
	 * @author ttschampel
	 */
	protected class BlackboardAccess implements Runnable {
		public static final int ADD_TYPE = 1;
		public static final int CHANGE_TYPE = 2;
		public static final int REMOVE_TYPE = 3;
		private int type;
		private Collection collection = new ArrayList();

		public BlackboardAccess(int type) {
			this.type = type;
		}

		public void addObject(Object o) {
			collection.add(o);
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			blackboardService.openTransaction();
			Iterator iterator = collection.iterator();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				switch (type) {
					case ADD_TYPE :
						blackboardService.publishAdd(obj);
						break;
					case CHANGE_TYPE :
						blackboardService.publishChange(obj);
						break;
					case REMOVE_TYPE :
						blackboardService.publishRemove(obj);
						break;
				}
			}
			blackboardService.closeTransactionDontReset();
		}
	}

	/**
	 * @see com.cougaarsoftware.core.configuration.services.ConfigurationService#initializeAgent(com.cougaarsoftware.core.configuration.vo.Agent)
	 */
	public void initializeAgent(AgentComponent agent) {
		ComponentDescription[] cds = makeComponents(agent.getChildComponents());
		for (int i = 0; i < cds.length; i++) {
			agentContainmentService.add(cds[i]);
		}
	}

	/**
	 * makes an array of ComponentDescriptions fom a vector of Component value
	 * objects
	 * 
	 * @param components
	 * 
	 * @return
	 */
	private ComponentDescription[] makeComponents(List components) {
		ComponentDescription[] ret = new ComponentDescription[components.size()];
		Iterator componentIter = components.iterator();
		int i = 0;
		while (componentIter.hasNext()) {
			Component comp = (Component) componentIter.next();
			ret[i++] = makeComponentDesc(comp.getName(), null, comp.getClassName(),
					comp.getPriority(), comp.getInsertionpoint());
		}
		return ret;
	}

	private ComponentDescription makeComponentDesc(String name, Vector vParams,
			String classname, String priority, String insertionPoint) {
		return new ComponentDescription(name, insertionPoint, classname, null, //codebase
				vParams, //params
				null, //certificate
				null, //lease
				null, //policy
				ComponentDescription.parsePriority(priority));
	}

	/**
	 * @see org.cougaar.core.blackboard.BlackboardClient#getBlackboardClientName()
	 */
	public String getBlackboardClientName() {
		return this.getClass().getName();
	}

	/**
	 * @see org.cougaar.core.blackboard.BlackboardClient#currentTimeMillis()
	 */
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * @see com.cougaarsoftware.core.configuration.services.ConfigurationService#addComponent(com.cougaarsoftware.core.configuration.vo.Component,
	 *      org.cougaar.core.mts.MessageAddress)
	 */
	public void addComponent(String pluginClassName) {
		ComponentDescription desc = createComponentDescriptionForPlugin(pluginClassName);
		addComponent(desc);
	}

	private void addAgent(String agentName, MessageAddress nodeAgentId,
			ComponentDescription compDescription) {
		if (logging.isDebugEnabled()) {
			logging.debug("Add agent " + agentName);
		}
		MobilityFactory mobilityFactory = (MobilityFactory) domainService
				.getFactory("mobility");
		MessageAddress mobileAgentAddress = MessageAddress
				.getMessageAddress(agentName);
		//String destNode= this.nodeAgentId.toString();
		MessageAddress destNodeAddress = nodeAgentId;
		//Create ticket id
		TicketIdentifier ticketIdentifier = (TicketIdentifier) mobilityFactory
				.createTicketIdentifier();
		//Create add ticket
		StateTuple st = null;
		//        if (compDescription != null) {
		//            st = new StateTuple(compDescription, null);
		//        }
		AddTicket addticket = new AddTicket(ticketIdentifier, mobileAgentAddress,
				destNodeAddress, compDescription, st);
		//Create Add Agent control
		AgentControl addControl = mobilityFactory.createAgentControl(null,
				destNodeAddress, addticket);
		BlackboardAccess bbAccess = new BlackboardAccess(BlackboardAccess.ADD_TYPE);
		bbAccess.addObject(addControl);
		threadService.getThread(this, bbAccess).schedule(0);
		if (logging.isDebugEnabled()) {
			logging
					.debug("Just sent Message thru MessageTransport to NodeAgent to load agent - "
							+ agentName);
		}
	}

	/**
	 * Create component description
	 * 
	 * @param pluginName
	 *          fully specified name of the plugin
	 * 
	 * @return the created ComponentDescription
	 */
	private ComponentDescription createComponentDescriptionForPlugin(
			String pluginName) {
		return new ComponentDescription(pluginName,
				"Node.AgentManager.Agent.PluginManager.Plugin", pluginName, null, //codebase
				// url
				null, //parameters
				null, null, null);
	}

	/**
	 * @see com.cougaarsoftware.core.configuration.services.ConfigurationService#addComponent(org.cougaar.core.component.ComponentDescription)
	 */
	public void addComponent(ComponentDescription componentDescription) {
		if (logging.isDebugEnabled()) {
			logging.debug("Adding plugin to agent ContainmentService:"
					+ componentDescription.getClassname());
		}
		agentContainmentService.add(componentDescription);
	}

	/**
	 * @see com.cougaarsoftware.core.configuration.services.ConfigurationService#removeComponent(java.lang.String)
	 */
	public boolean removeComponent(String componentClass) {
		if (logging.isDebugEnabled()) {
			logging.debug("Removing plugin from agentContainmentService: "
					+ componentClass);
		}
		ComponentDescription cd = getComponentDescription(componentClass);
		if (cd != null) {
			return agentContainmentService
					.remove(getComponentDescription(componentClass));
		} else {
			if (logging.isErrorEnabled()) {
				logging.error("Tried to remove a non-existent component: "
						+ componentClass);
			}
			return false;
		}
	}

	private ComponentDescription getComponentDescription(String className) {
		if (agentContainmentService != null) {
			List binders = agentContainmentService.listBinders();
			Iterator i = binders.iterator();
			while (i.hasNext()) {
				Binder binder = (Binder) i.next();
				Object state = binder.getState();
				if (state != null) {
					if (state instanceof ComponentDescriptions) {
						ComponentDescriptions cds = (ComponentDescriptions) state;
						List plugins = cds
								.extractInsertionPointComponent(PluginBase.INSERTION_POINT);
						Iterator j = plugins.iterator();
						while (j.hasNext()) {
							StateTuple tuple = (StateTuple) j.next();
							ComponentDescription cd = tuple.getComponentDescription();
							if (cd.getClassname().equals(className)) {
								return cd;
							}
						}
					}
				}
			}
		}
		return null;
	}
}