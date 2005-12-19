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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JFrame;

import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.BlackboardQueryService;
import org.cougaar.core.service.DomainService;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.ThreadService;
import org.cougaar.planning.ldm.PlanningFactory;
import org.cougaar.planning.ldm.plan.NewTask;
import org.cougaar.planning.ldm.plan.PrepositionalPhrase;
import org.cougaar.planning.ldm.plan.Task;
import org.cougaar.planning.ldm.plan.Verb;
import org.cougaar.planning.ldm.predicate.TaskPredicate;
import org.cougaar.util.UnaryPredicate;

import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Capability;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.Constants;
import com.cougaarsoftware.config.NodeComponent;
import com.cougaarsoftware.config.Society;
import com.cougaarsoftware.config.service.ConfigurationService;

/**
 * @author mabrams
 */
public class ConfigViewerApplicationPlugin extends ComponentPlugin implements
        ConfigViewerController {

    protected BlackboardQueryService bbQueryService;

    protected ThreadService threadService;

    private ConfigViewerPanel configViewer;

    private LoggingService logging;

    private ConfigurationService configService;

    private String graphPanelClass;

    /**
     * Set LoggingService at load time
     * 
     * @param service
     *            LoggingService
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

    public ConfigurationService getConfigurationService() {
        if (this.configService == null) {
            this.configService = (ConfigurationService) getServiceBroker()
                    .getService(this, ConfigurationService.class, null);
        }
        return this.configService;
    }

    private IncrementalSubscription startConfigAppSubscription = null;

    private TaskPredicate startConfigAppPredicate = new TaskPredicate() {

        public boolean execute(Task o) {
            if (o.getVerb().equals(Constants.Verb.START_CONFIG_VIEWER)) {
                return true;
            }
            return false;
        }
    };

    private IncrementalSubscription updateAgentSubscription = null;

    private TaskPredicate updateAgentSubscriptionPredicate = new TaskPredicate() {

        public boolean execute(Task o) {
            if (o.getVerb().equals(Constants.Verb.UPDATE_AGENT_CONFIG_TASK)) {
                return true;
            }
            return false;
        }
    };

    private IncrementalSubscription updateNodeSubscription = null;

    private TaskPredicate updateNodeSubscriptionPredicate = new TaskPredicate() {

        public boolean execute(Task o) {
            if (o.getVerb().equals(Constants.Verb.UPDATE_NODE_CONFIG_TASK)) {
                return true;
            }
            return false;
        }
    };

    private IncrementalSubscription societySubscription = null;

    private UnaryPredicate societyPredicate = new UnaryPredicate() {

        public boolean execute(Object o) {
            return o instanceof Society;
        }
    };

    private DomainService domainService;

    /**
     * set DomainService at load time
     * 
     * @param service
     *            DomainService
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

    protected BlackboardQueryService getBlackboardQueryService() {
        if (bbQueryService == null) {
            bbQueryService = (BlackboardQueryService) getServiceBroker()
                    .getService(this, BlackboardQueryService.class, null);
        }
        return bbQueryService;
    }

    protected void setBlackboardQueryService(
            BlackboardQueryService bbQueryService) {
        this.bbQueryService = bbQueryService;
    }

    protected ThreadService getThreadService() {
        if (threadService == null) {
            threadService = (ThreadService) getServiceBroker().getService(this,
                    ThreadService.class, null);
        }
        return threadService;
    }

    protected void setTheadService(ThreadService threadService) {
        this.threadService = threadService;
    }

    protected void setupSubscriptions() {
        startConfigAppSubscription = (IncrementalSubscription) getBlackboardService()
                .subscribe(startConfigAppPredicate);
        updateAgentSubscription = (IncrementalSubscription) getBlackboardService()
                .subscribe(updateAgentSubscriptionPredicate);
        updateNodeSubscription = (IncrementalSubscription) getBlackboardService()
                .subscribe(updateNodeSubscriptionPredicate);
        societySubscription = (IncrementalSubscription) getBlackboardService()
                .subscribe(societyPredicate);
    }

    /**
     * Query the blackboard.
     * 
     * @param unaryPredicate
     *            predicate to match against
     * 
     * @return all objects matching the given predicate
     */
    public Collection queryBlackBoard(UnaryPredicate unaryPredicate) {
        if (bbQueryService == null) {
            getBlackboardQueryService();
        }
        return bbQueryService.query(unaryPredicate);
    }

    protected void execute() {
            if (configViewer == null) {
                configViewer = new ConfigViewerPanel(this);
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(configViewer);
                frame.setJMenuBar(configViewer.createMenuBar());
                frame.pack();
                frame.setVisible(true);
            }
        Enumeration e = societySubscription.getAddedList();
        while (e.hasMoreElements()) {
            Society society = (Society) e.nextElement();
            if (configViewer != null) {
                configViewer.updateTreeData(society);
            }
        }
        e = null;
        e = societySubscription.getChangedList();
        while (e.hasMoreElements()) {
            Society society = (Society) e.nextElement();
            if (configViewer != null) {
                configViewer.updateTreeData(society);
            }
        }
        e = null;
        e = updateAgentSubscription.getAddedList();
        while (e.hasMoreElements()) {
            Task t = (Task) e.nextElement();
            PrepositionalPhrase pp = t
                    .getPrepositionalPhrase(Constants.Preposition.AGENT_COMPONENT);
            if (configViewer != null) {
                configViewer.updateAgentData((AgentComponent) pp
                        .getIndirectObject());
            }
            getBlackboardService().publishRemove(t);
        }
        e = null;
        e = updateNodeSubscription.getAddedList();
        while (e.hasMoreElements()) {
            Task t = (Task) e.nextElement();
            PrepositionalPhrase pp = t
                    .getPrepositionalPhrase(Constants.Preposition.NODE_COMPONENT);
            if (configViewer != null) {
                configViewer.updateNodeData((NodeComponent) pp
                        .getIndirectObject());
            }
            getBlackboardService().publishRemove(t);
        }
    }

    /**
     * get the society configuration from the blackboard
     * 
     * @return DOCUMENT ME!
     */
    public Society getSocietyConfiguration(boolean inTransaction) {
        if (logging.isDebugEnabled()) {
            logging.debug("Get the society object from the blackboard");
        }
        if (!inTransaction) {
            getBlackboardService().openTransaction();
        }
        Collection c = blackboard.query(societyPredicate);
        Iterator iter = c.iterator();
        Society society = null;
        while (iter.hasNext()) {
            society = (Society) iter.next();
        }
        if (!inTransaction) {
            getBlackboardService().closeTransaction();
        }
        return society;
    }

    /**
     * @param agentName
     * @param nodeName
     */
    public void removeAgent(String agentName, String nodeName) {
        if (getConfigurationService() != null) {
            getBlackboardService().openTransaction();
            String cAgentName = agentName
                    .substring(agentName.lastIndexOf(':') + 1);
            getConfigurationService().removeAgent(cAgentName,
                    MessageAddress.getMessageAddress(nodeName));
            Society society = getSocietyConfiguration(true);
            if (society != null) {
                AgentComponent ac = society.getAgent(agentName);
                ac.setStatus(Component.DEAD);
                //				society.removeAgent(ac);
                if (configViewer != null) {
                    configViewer.updateAgentData(ac);
                }
                getConfigurationService().removeAgent(cAgentName,
                        MessageAddress.getMessageAddress(ac.getParentNode()));
            }
            getBlackboardService().closeTransaction();
        }
    }

    public String getAgentId() {
        return getAgentIdentifier().getAddress();
    }

    /**
     * @param command
     * @param node
     */
    public void executeCommand(Capability command, MessageAddress agentAddress) {
        try {
            NewTask task = getPlanningFactory().newTask();
            task.setVerb(Verb.get(command.getVerb()));
            task.setDestination(agentAddress);
            getBlackboardService().openTransaction();
            getBlackboardService().publishAdd(task);
        } finally {
            getBlackboardService().closeTransaction();
        }
    }

    public void load() {
        super.load();
        Collection p = getParameters();
        int idx;
        for (Iterator i = p.iterator(); i.hasNext();) {
            String s = (String) i.next();
            if ((idx = s.indexOf('=')) != -1) {
                String key = s.substring(0, idx);
                if (key.equals(PARAM_GRAPH_PANEL_CLASS)) {
                    graphPanelClass = s.substring(idx + 1, s.length());
                }
            }
        }
        if (graphPanelClass == null && getLoggingService().isErrorEnabled()) {
            getLoggingService().error(
                    "Required parameter '" + PARAM_GRAPH_PANEL_CLASS
                            + "' not specified.");
        }
    }

    public String getGraphPanelClass() {
        return graphPanelClass;
    }
    
}