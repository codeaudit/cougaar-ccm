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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.component.ComponentDescription;
import org.cougaar.core.component.StateTuple;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.node.NodeIdentificationService;
import org.cougaar.core.qos.metrics.ParameterizedPlugin;
import org.cougaar.core.service.AgentContainmentService;
import org.cougaar.core.service.DomainService;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.ThreadService;
import org.cougaar.core.service.UIDService;
import org.cougaar.multicast.AttributeBasedAddress;
import org.cougaar.planning.ldm.PlanningFactory;
import org.cougaar.planning.ldm.plan.NewTask;
import org.cougaar.planning.ldm.plan.Task;
import org.cougaar.util.UnaryPredicate;
import com.cougaarsoftware.config.domain.ConfigurationDomain;
import com.cougaarsoftware.config.domain.ConfigurationFactory;
import com.cougaarsoftware.config.lp.ConfigurationDirective;
import com.cougaarsoftware.config.lp.NewConfigurationDirective;

/**
 * This plugin composes and sends the list of components on this agent to a
 * specified configuration community at a specified interval.
 * 
 * @author mabrams
 * 
 * @param UPDATE_INTERVAL_PARAM -
 *                 interval between sending configuration updates
 * @param CONFIG_COMMUNITY_PARAM -
 *                 the community to send configuration updates from this agent to
 */
public class AgentConfigurationPlugin extends ParameterizedPlugin {

    private LoggingService logging;

    private AgentContainmentService acs;

    /**
     * interval to send configuration updates
     */
    protected final static String UPDATE_INTERVAL_PARAM = "UPDATE_INTERVAL_PARAM";

    /**
     * the param for the community to send configuration updates to
     */
    protected final static String CONFIG_COMMUNITY_PARAM = "CONFIG_COMMUNITY_PARAM";

    /**
     * default update interval
     */
    protected final static int DEFAULT_UPDATE_INTERVAL = 5000;

    /**
     * the interval to update agent configuration
     */
    private int updateInterval;

    /**
     * the domain service
     */
    private DomainService domainService;

    /**
     * the thread service
     */
    private ThreadService threadService;

    /**
     * subscription to agent configuration update tasks
     */
    private IncrementalSubscription agentUpdateSubscription;

    /**
     * the community to send this agent's configuration to
     */
    private String configCommunity;

    /**
     * the UID service
     */
    private UIDService uidService;

    /**
     * list of commands components on this agent are capable of performing
     */
    private Map commands;

    /**
     * subscription to add command tasks
     */
    private IncrementalSubscription addCommandSubscription;

    /**
     * configurationf factory for generating config related assets and objects
     */
    private ConfigurationFactory configFactory;

    private NodeIdentificationService nodeIdentificationService;

    /**
     * Set LoggingService at load time
     * 
     * @param service
     *                 LoggingService
     */
    public void setLoggingService(LoggingService service) {
        this.logging = service;
    }

    /**
     * Get LoggingService
     * 
     * @return LoggingService
     */
    public LoggingService getLoggingService() {
        return this.logging;
    }

    public void setAgentContainmentService(AgentContainmentService acs) {
        this.acs = acs;
    }

    public AgentContainmentService getAgentContainmentService() {
        return this.acs;
    }

    public void setThreadService(ThreadService s) {
        this.threadService = s;
    }

    public ThreadService getThreadService() {
        return this.threadService;
    }

    public NodeIdentificationService getNodeIdentificationService() {
        return this.nodeIdentificationService;
    }

    public void setNodeIdentificationService(
            NodeIdentificationService nodeIDService) {
        this.nodeIdentificationService = nodeIDService;
    }

    /**
     * set UIDService at load time
     * 
     * @param service
     *                 UIDService
     */
    public void setUIDService(UIDService service) {
        this.uidService = service;
    }

    /**
     * Get UIDService
     * 
     * @return UIDService
     */
    public UIDService getUIDService() {
        return this.uidService;
    }

    /**
     * set DomainService at load time
     * 
     * @param service
     *                 DomainService
     */
    public void setDomainService(DomainService service) {
        this.domainService = service;
    }

    /**
     * Get DomainService
     * 
     * @return DomainService
     */
    public DomainService getDomainService() {
        return this.domainService;
    }

    /**
     * Conveience method for getting the PlanningFactory
     * 
     * @return the PlanningFactory
     */
    protected PlanningFactory getPlanningFactory() {
        return ((PlanningFactory) getDomainService().getFactory("planning"));
    }

    /**
     * get the update interval and configruation community from the plugin
     * parameters
     */
    public void load() {
        super.load();
        String updateIntervalStr = getParameter(AgentConfigurationPlugin.UPDATE_INTERVAL_PARAM);
        // if an update interval is specified, use it, otherwise use the
        // default value
        if (updateIntervalStr != null) {
            updateInterval = Integer.parseInt(updateIntervalStr);
        } else {
            updateInterval = AgentConfigurationPlugin.DEFAULT_UPDATE_INTERVAL;
        }
        configCommunity = getParameter(AgentConfigurationPlugin.CONFIG_COMMUNITY_PARAM);
    }

    protected void setupSubscriptions() {
        if (logging.isDebugEnabled()) {
            logging.debug(agentId.getAddress() + ": Setting up subscriptions");
        }

        configFactory = (ConfigurationFactory) getDomainService().getFactory(
                ConfigurationDomain.DOMAIN_NAME);
        if (configFactory != null) {
            agentUpdateSubscription = (IncrementalSubscription) getBlackboardService()
                    .subscribe(configUpdateTaskPredicate);
            addCommandSubscription = (IncrementalSubscription) getBlackboardService()
                    .subscribe(commandPredicate);
            // make the task to periodically send this agents configuration to
            // the
            // configuration managers
            NewTask updateTask = getPlanningFactory().newTask();
            updateTask.setVerb(Constants.Verb.AGENT_CONFIGURATION_UPDATE);
            getThreadService()
                    .getThread(this, new PublishAddObject(updateTask))
                    .schedule(updateInterval, updateInterval);
        } else {
            if (getLoggingService().isWarnEnabled()) {
                getLoggingService()
                        .warn(
                                getAgentIdentifier()
                                        + " loaded AgentConfiruationPlugin but the configuration domain was not\n"
                                        + "available.  Add configuration=com.cougaarsoftware.config.domain.ConfigurationDomain\n"
                                        + "to your LDMDomains File if you want to use this service");
            }
        }

    }

    protected void execute() {
        if (logging.isDebugEnabled()) {
            logging.debug(agentId.getAddress() + ": Executing");
        }
        //handle tasks to update and send agent configuration
        if (agentUpdateSubscription != null) {
            Enumeration e = agentUpdateSubscription.getAddedList();
            while (e.hasMoreElements()) {
                Task t = (Task) e.nextElement();
                composeAgentConfiguration();
                getBlackboardService().publishRemove(t);
            }
            e = null;
            e = addCommandSubscription.getAddedList();
            while (e.hasMoreElements()) {
                Capability c = (Capability) e.nextElement();
                if (commands == null) {
                    commands = Collections.synchronizedMap(new HashMap());
                }
                Vector commandList = (Vector) commands.get(c.getDisplayName());
                if (commandList == null) {
                    commandList = new Vector();
                    commandList.add(c);
                    commands.put(c.getComponentClass(), commandList);
                } else {
                    commandList.add(c);
                }
                getBlackboardService().publishRemove(c);
            }
        }
    }

    /**
     * compose the agent configuration
     */
    private void composeAgentConfiguration() {
        List pluginTuples = acs.listPlugins();
        Iterator tupleIter = pluginTuples.iterator();
        AgentComponent agentComponent = null;
        while (tupleIter.hasNext()) {
            if (agentComponent == null) {
                agentComponent = new AgentComponentImpl("Agent:"
                        + getAgentIdentifier().getAddress(), getUIDService()
                        .nextUID(), getAgentIdentifier());
                agentComponent.setStatus(Component.HEALTHY);
                if (getAgentIdentifier().equals(
                        getNodeIdentificationService().getMessageAddress())) {
                    agentComponent.setIsNodeAgent(true);
                    String hostName = "";
                    try {
                        hostName = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e) {
                        if (logging.isErrorEnabled()) {
                            logging.error("Error getting locahost: ", e);
                        }
                    }
                    agentComponent.setHost(hostName);
                }
                agentComponent.setParentNode(getNodeIdentificationService()
                        .getMessageAddress().getAddress());
            }
            StateTuple tuple = (StateTuple) tupleIter.next();
            ComponentDescription plugin = tuple.getComponentDescription();
            agentComponent.addChildComponent(plugin);
            //add the commands
            agentComponent.setCommands(commands);
        }
        if (agentComponent != null) {
            sendConfiguration(agentComponent);
        }
    }

    /**
     * @param agentComponent
     */
    private void sendConfiguration(AgentComponent agentComponent) {
        if (agentComponent != null && configCommunity != null) {
            NewConfigurationDirective ncd = configFactory
                    .createNewConfigurationDirective();
            MessageAddress target = AttributeBasedAddress
                    .getAttributeBasedAddress(configCommunity, "Role",
                            "Manager");
            ncd.setDestination(target);
            ncd.setPayload(agentComponent);
            ncd.setType(ConfigurationDirective.AGENT_CONFIGURATION);
            getBlackboardService().publishAdd(ncd);
        }
    }

    /**
     * predicate to subscribe to configuration update tasks
     */
    private static UnaryPredicate configUpdateTaskPredicate = new UnaryPredicate() {

        public boolean execute(Object o) {
            if (o instanceof Task) {
                Task t = (Task) o;
                return t.getVerb().equals(
                        Constants.Verb.AGENT_CONFIGURATION_UPDATE);
            }
            return false;
        }
    };

    private static UnaryPredicate commandPredicate = new UnaryPredicate() {

        public boolean execute(Object o) {
            return o instanceof Capability;
        }
    };

    /**
     * adds objects to the blackboard when invoked
     * 
     * @author mabrams
     */
    private class PublishAddObject implements Runnable {

        List list = null;

        public PublishAddObject(List publishList) {
            this.list = publishList;
        }

        public PublishAddObject(Object a) {
            list = new ArrayList();
            list.add(a);
        }

        public void run() {
            if (list != null && getBlackboardService() != null) {
                getBlackboardService().openTransaction();
                for (int i = 0; i < list.size(); i++) {
                    Object object = list.get(i);
                    getBlackboardService().publishAdd(object);
                }
                getBlackboardService().closeTransactionDontReset();
            }
        }
    }
}