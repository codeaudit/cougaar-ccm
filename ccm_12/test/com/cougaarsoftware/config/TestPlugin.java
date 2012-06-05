/*
 * Created on Apr 19, 2004
 */
package com.cougaarsoftware.config;

import java.util.Enumeration;

import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.node.NodeIdentificationService;
import org.cougaar.core.plugin.ComponentPlugin;
import org.cougaar.core.service.AgentContainmentService;
import org.cougaar.core.service.DomainService;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.UIDService;
import org.cougaar.planning.ldm.PlanningFactory;
import org.cougaar.planning.ldm.plan.NewTask;
import org.cougaar.planning.ldm.plan.Task;
import org.cougaar.planning.ldm.plan.Verb;
import org.cougaar.planning.ldm.predicate.TaskPredicate;

import com.cougaarsoftware.config.service.ConfigurationService;

/**
 * @author mabrams
 */
public class TestPlugin extends ComponentPlugin {
	private LoggingService logging;
	private AgentContainmentService acs;
	private DomainService domainService;
	private ConfigurationService configService;
	private NodeIdentificationService nodeIDService;
	private UIDService uidService;
	private static TaskPredicate testTaskPred = new TaskPredicate() {
		public boolean execute(Task t) {
			if (t.getVerb().equals(Verb.get("testVerb"))) {
				return true;
			}
			return false;
		}
	};
	private IncrementalSubscription testTaskSub;

	/**
	 * Set LoggingService at load time
	 * 
	 * @param service
	 *          LoggingService
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

	public void setAgentContainmentService(AgentContainmentService acs) {
		this.acs = acs;
	}

	public AgentContainmentService getAgentContainmentService() {
		return this.acs;
	}

	/**
	 * set DomainService at load time
	 * 
	 * @param service
	 *          DomainService
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
	 * Get node identification service at load time
	 * 
	 * @param service
	 *          NodeIdentificationService
	 */
	public void setNodeIdentificationService(NodeIdentificationService service) {
		this.nodeIDService = service;
	}

	/**
	 * Get Node Identification Service at load time
	 * 
	 * @return NodeIdentificationService
	 */
	public NodeIdentificationService getNodeIdentificationService() {
		return this.nodeIDService;
	}

	/**
	 * Conveience method for getting the PlanningFactory
	 * 
	 * @return the PlanningFactory
	 */
	protected PlanningFactory getPlanningFactory() {
		return ((PlanningFactory) getDomainService().getFactory("planning"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.plugin.ComponentPlugin#setupSubscriptions()
	 */
	protected void setupSubscriptions() {
		if (logging.isDebugEnabled()) {
			logging.debug(agentId.getAddress() + ": Setting up subscriptions");
		}
		Capability c = new Capability("testVerb", "Display Name", this.getClass()
				.getName(), getUIDService().nextUID());
		getBlackboardService().publishAdd(c);
		testTaskSub = (IncrementalSubscription) getBlackboardService().subscribe(
				testTaskPred);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.plugin.ComponentPlugin#execute()
	 */
	protected void execute() {
		if (logging.isDebugEnabled()) {
			logging.debug(agentId.getAddress() + ": Executing");
		}
		
		Enumeration e = testTaskSub.getAddedList();
		while (e.hasMoreElements()) {
			Task t = (Task) e.nextElement();
			System.out.println(t);
			return;
		}
		//start gui
		NewTask t = getPlanningFactory().newTask();
		t.setVerb(Constants.Verb.START_CONFIG_VIEWER);
		getBlackboardService().publishAdd(t);
		//        getConfigurationService().removeAgent("SampleAgent2",
		//                getNodeIdentificationService().getMessageAddress());
		
		
	}

	/**
	 * @return Returns the uidService.
	 */
	public UIDService getUIDService() {
		return uidService;
	}

	/**
	 * @param uidService
	 *          The uidService to set.
	 */
	public void setUIDService(UIDService uidService) {
		this.uidService = uidService;
	}
}