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
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import org.cougaar.core.blackboard.IncrementalSubscription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.node.NodeIdentificationService;
import org.cougaar.core.qos.metrics.ParameterizedPlugin;
import org.cougaar.core.service.DomainService;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.ThreadService;
import org.cougaar.core.service.UIDService;
import org.cougaar.core.service.community.CommunityResponse;
import org.cougaar.core.service.community.CommunityResponseListener;
import org.cougaar.core.service.community.CommunityService;
import org.cougaar.core.thread.Schedulable;
import org.cougaar.multicast.AttributeBasedAddress;
import org.cougaar.planning.ldm.PlanningFactory;
import org.cougaar.planning.ldm.plan.NewPrepositionalPhrase;
import org.cougaar.planning.ldm.plan.NewTask;
import org.cougaar.planning.ldm.plan.Task;
import org.cougaar.util.UnaryPredicate;
import com.cougaarsoftware.config.domain.ConfigurationDomain;
import com.cougaarsoftware.config.domain.ConfigurationFactory;
import com.cougaarsoftware.config.lp.ConfigurationDirective;
import com.cougaarsoftware.config.lp.NewConfigurationDirective;
/**
 * @author mabrams
 * 
 * @param UPDATE_INTERVAL_PARAM -
 *          interval between sending configuration updates
 * @param CONFIG_COMMUNITY_PARAM -
 *          the community this plugin joins as a configuration manager
 * @param PARENT_CONFIG_COMMUNITY_PARAM -
 *          the community this plugin should send its aggregated configuration
 *          information to
 */
public class ConfigurationManagerPlugin extends ParameterizedPlugin {
	/**
	 * param for interval configuration updates
	 */
	protected final static String UPDATE_INTERVAL_PARAM = "UPDATE_INTERVAL_PARAM";
	/**
	 * the param to specifiy the communit this plugin manages the configuration
	 * for
	 */
	protected final static String CONFIG_COMMUNITY_PARAM = "CONFIG_COMMUNITY_PARAM";
	/**
	 * the param that specifies the community this plugin should send it's
	 * aggregated config info to
	 */
	protected final static String PARENT_CONFIG_COMMUNITY_PARAM = "PARENT_CONFIG_COMMUNITY_PARAM";
	/**
	 * default update interval
	 */
	protected final static int DEFAULT_UPDATE_INTERVAL = 5000;
	/**
	 * interval to send configuration updates
	 */
	private int updateInterval;
	/**
	 * the community to send configuration updates to
	 */
	private String configCommunity;
	/**
	 * subscription for agent configuration objects
	 */
	private IncrementalSubscription agentConfigurationSubscription;
	/**
	 * subscription for society configuration objects
	 */
	private IncrementalSubscription societyConfigurationSubscription;
	/**
	 * the community to send this manager's configuration to
	 */
	private String parentConfigCommunity;
	/**
	 * configurationf factory for generating config related assets and objects
	 */
	private ConfigurationFactory configFactory;
	/**
	 * the logging service
	 */
	private LoggingService logging;
	/**
	 * the community service
	 */
	private CommunityService communityService;
	/**
	 * The maximum elapsed time (in millis) permitted between status updates
	 * before a component is considered dead
	 */
	private long statusExpireTimeout = 20000;
	/**
	 * the time between a components status expiring and actually removing it from
	 * the society
	 */
	private long deadComponentTimeout = 60000;
	/**
	 * a represntation of this managers view of the society, containing all agent
	 * and node configurations that were sent to this components blackboard
	 */
	private Society localSociety;
	/**
	 * subscription to agent configuration update tasks
	 */
	private IncrementalSubscription societyUpdateSubscription;	
	private NodeIdentificationService nodeIdentificationService;
	private UIDService uidService;
	private DomainService domainService;
	private ThreadService threadService;
	/**
	 * hastable of components this plugin is tracking
	 */
	private Hashtable trackers = new Hashtable();
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
	 * Conveience method for getting the PlanningFactory
	 * 
	 * @return the PlanningFactory
	 */
	protected PlanningFactory getPlanningFactory() {
		return ((PlanningFactory) getDomainService().getFactory("planning"));
	}
	/**
	 * set UIDService at load time
	 * 
	 * @param service
	 *          UIDService
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
	 * Get a reference to the community service
	 * 
	 * @return the community service
	 */
	public CommunityService getCommunityService() {
		return this.communityService;
	}
	/**
	 * Set community service at load time
	 * 
	 * @param service
	 *          CommunityService
	 */
	public void setCommunityService(CommunityService service) {
		this.communityService = service;
	}
	public NodeIdentificationService getNodeIdentificationService() {
		return this.nodeIdentificationService;
	}
	public void setNodeIdentificationService(
			NodeIdentificationService nodeIDService) {
		this.nodeIdentificationService = nodeIDService;
	}
	public void setThreadService(ThreadService s) {
		this.threadService = s;
	}
	public ThreadService getThreadService() {
		return this.threadService;
	}
	/**
	 * call the super load and get the parameters for this plugin
	 */
	public void load() {
		super.load();
		String updateIntervalStr = getParameter(ConfigurationManagerPlugin.UPDATE_INTERVAL_PARAM);
		// if an update interval is specified, use it, otherwise use the
		// default value
		if (updateIntervalStr != null) {
			updateInterval = Integer.parseInt(updateIntervalStr);
		} else {
			updateInterval = ConfigurationManagerPlugin.DEFAULT_UPDATE_INTERVAL;
		}
		configCommunity = getParameter(ConfigurationManagerPlugin.CONFIG_COMMUNITY_PARAM);
		parentConfigCommunity = getParameter(ConfigurationManagerPlugin.PARENT_CONFIG_COMMUNITY_PARAM);
		ThreadService threadService = (ThreadService) getServiceBroker()
				.getService(this, ThreadService.class, null);
		Schedulable sched = threadService.getThread(this,
				new ComponentTrackerThread());
		sched.schedule(0, 10000);
	}
	protected void setupSubscriptions() {
		agentConfigurationSubscription = (IncrementalSubscription) getBlackboardService()
				.subscribe(agentConfigurationPredicate);
		societyConfigurationSubscription = (IncrementalSubscription) getBlackboardService()
				.subscribe(societyWrapperConfigurationPredicate);
		societyUpdateSubscription = (IncrementalSubscription) getBlackboardService()
				.subscribe(configUpdateTaskPredicate);
		joinCommunity(configCommunity);
		// make the task to periodically send this agents configuration to the
		// configuration managers
		//only create the task if there is a parent community to send the data
		// to
		if (parentConfigCommunity != null) {
			NewTask updateTask = getPlanningFactory().newTask();
			updateTask.setVerb(Constants.Verb.SOCIETY_CONFIGURATION_UPDATE);
			getThreadService().getThread(this, new PublishAddObject(updateTask))
					.schedule(updateInterval, updateInterval);
		}
		createLocalSociety();
		configFactory = (ConfigurationFactory) getDomainService().getFactory(
				ConfigurationDomain.DOMAIN_NAME);
	}
	protected void execute() {
		processAgentConfigurations(agentConfigurationSubscription.getAddedList());
		processSocietyConfigurations(societyConfigurationSubscription
				.getAddedList());
		processSocietyConfigUpdateTasks(societyUpdateSubscription.getAddedList());
	}
	/**
	 * @param enumeration
	 */
	private void processSocietyConfigUpdateTasks(Enumeration updateEnum) {
		while (updateEnum.hasMoreElements()) {
			Task t = (Task) updateEnum.nextElement();
			sendConfiguration();
			getBlackboardService().publishRemove(t);
		}
	}
	/**
	 * @param enumeration
	 */
	private void processSocietyConfigurations(Enumeration societyConfigEnum) {
		boolean societyModifed = false;
		while (societyConfigEnum.hasMoreElements()) {
			SocietyWrapper societyWrapper = (SocietyWrapper) societyConfigEnum
					.nextElement();
			getBlackboardService().publishRemove(societyWrapper);
			Society society = societyWrapper.getSociety();
			if (!society.equals(localSociety)) {
				Map nodeMap = society.getNodeMap();
				if (nodeMap != null) {
					Set keys = nodeMap.keySet();
					Iterator i = keys.iterator();
					synchronized (nodeMap) {
						while (i.hasNext()) {
							String nodeName = (String) i.next();
							NodeComponent node = (NodeComponent) nodeMap.get(nodeName);
							node.setStatus(Component.HEALTHY);
							ComponentTracker tracker = (ComponentTracker) trackers.get(node
									.getName());
							if (logging.isDebugEnabled()) {
								logging.debug("Configuration seen for component: " + node);
							}
							if (tracker == null) {
								tracker = new ComponentTracker(node);
								trackers.put(node.getName(), tracker);
							} else {
								tracker.incrementReadCount();
								tracker.setLastReadTime(Calendar.getInstance());
								tracker.setComponent(node);
							}
							NodeComponent oNode = localSociety.getNode(node.getName());
							if (!node.equals(oNode)) {
								localSociety.addNode(node);
								societyModifed = true;
								publishNodeUpdateTask(node);
							}
						}
					}
				}
			}
		}
		if (societyModifed) {
			getBlackboardService().publishChange(localSociety);
			sendConfiguration();
		}
	}
	/**
	 * @param enumeration
	 */
	private void processAgentConfigurations(Enumeration agentConfigEnum) {
		boolean societyModified = false;
		while (agentConfigEnum.hasMoreElements()) {
			if (logging.isDebugEnabled()) {
				logging.debug("Got updated or new agent configuration");
			}
			AgentComponent agentComponent = (AgentComponent) agentConfigEnum
					.nextElement();
			if (localSociety != null) {
				if (logging.isDebugEnabled()) {
					logging.debug("Local Society Avaialable, "
							+ "adding angent configuration to appropriate node");
				}
				NodeComponent node = localSociety.getNode(agentComponent
						.getParentNode());
				if (node != null) {
					if (logging.isDebugEnabled()) {
						logging.debug("Found the node in the society, "
								+ "adding or updating agent config in that node");
					}
					AgentComponent oldComponent = node.addAgent(agentComponent);
					if (oldComponent != null && oldComponent.getStatus() == 1) {
						System.out.println("here");
					}
					ComponentTracker tracker = (ComponentTracker) trackers
							.get(agentComponent.getName());
					if (tracker == null) {
						if (logging.isDebugEnabled()) {
							logging.debug("Configuration seen for component: " + node);
						}
						tracker = new ComponentTracker(agentComponent);
						trackers.put(agentComponent.getName(), tracker);
					} else {
						tracker.incrementReadCount();
						tracker.setLastReadTime(Calendar.getInstance());
						tracker.setComponent(agentComponent);
					}
					agentComponent.setStatus(Component.HEALTHY);
					if (oldComponent == null
							|| (oldComponent != null && !oldComponent.equals(agentComponent))) {
						publishAgentUpdateTask(agentComponent);
						societyModified = true;
					}
				} else {
					if (logging.isDebugEnabled()) {
						logging.debug("Did NOT find the node in the society, "
								+ "creating a new container for that node "
								+ "and adding it to the local society representation");
					}
					node = new NodeComponentImpl(agentComponent.getParentNode(),
							getUIDService().nextUID());
					node.setStatus(Component.HEALTHY);
					agentComponent.setStatus(Component.HEALTHY);
					node.addAgent(agentComponent);
					publishNodeUpdateTask(node);
					localSociety.addNode(node);
					societyModified = true;
				}
			} else {
				if (logging.isWarnEnabled()) {
					logging.warn("Local Society was null, this should never be null");
				}
			}
		}
		if (societyModified) {
			getBlackboardService().publishChange(localSociety);
			sendConfiguration();
		}
	}
	/**
	 * @param agentComponent
	 */
	private void sendConfiguration() {
		getBlackboardService().publishChange(localSociety);
		if (localSociety != null && parentConfigCommunity != null) {
			NewConfigurationDirective ncd = configFactory
					.createNewConfigurationDirective();
			MessageAddress target = AttributeBasedAddress.getAttributeBasedAddress(
					parentConfigCommunity, "Role", "Member");
			ncd.setDestination(target);
			ncd.setPayload(new SocietyWrapper(localSociety, uidService.nextUID()));
			ncd.setType(ConfigurationDirective.SOCIETY_CONFIGURATION);
			getBlackboardService().publishAdd(ncd);
		}
	}
	private void publishAgentUpdateTask(AgentComponent agentComponent) {
		NewTask updateAgentTask = getPlanningFactory().newTask();
		updateAgentTask.setVerb(Constants.Verb.UPDATE_AGENT_CONFIG_TASK);
		NewPrepositionalPhrase pp = getPlanningFactory().newPrepositionalPhrase();
		pp.setPreposition(Constants.Preposition.AGENT_COMPONENT);
		pp.setIndirectObject(agentComponent);
		updateAgentTask.addPrepositionalPhrase(pp);
		getBlackboardService().publishAdd(updateAgentTask);
	}
	private void publishNodeUpdateTask(NodeComponent nodeComponent) {
		NewTask updateNodeTask = getPlanningFactory().newTask();
		updateNodeTask.setVerb(Constants.Verb.UPDATE_NODE_CONFIG_TASK);
		NewPrepositionalPhrase pp = getPlanningFactory().newPrepositionalPhrase();
		pp.setPreposition(Constants.Preposition.NODE_COMPONENT);
		pp.setIndirectObject(nodeComponent);
		updateNodeTask.addPrepositionalPhrase(pp);
		getBlackboardService().publishAdd(updateNodeTask);
	}
	/**
	 * predicate for agent configuration
	 */
	private static UnaryPredicate agentConfigurationPredicate = new UnaryPredicate() {
		public boolean execute(Object o) {
			return o instanceof AgentComponent;
		}
	};
	/**
	 * predicate for agent configuration
	 */
	private static UnaryPredicate societyWrapperConfigurationPredicate = new UnaryPredicate() {
		public boolean execute(Object o) {
			return o instanceof SocietyWrapper;
		}
	};
	/**
	 * have the agent this plugin is on join community
	 */
	private void joinCommunity(String communityName) {
		if (logging.isDebugEnabled()) {
			logging.debug("Joining community: " + communityName);
		}
		// join the amiie community
		Attributes roleAttributes = new BasicAttributes();
		Attribute attr = new BasicAttribute("Role");
		attr.add("Manager");
		roleAttributes.put(attr);
		roleAttributes.put(new BasicAttribute("EntityType", "Agent"));
		communityService.joinCommunity(communityName, getAgentIdentifier()
				.getAddress(), CommunityService.AGENT, roleAttributes, true, null,
				new MyCommunityResponseListener());
	}
	private void createLocalSociety() {
		localSociety = new SocietyImpl(this.getAgentIdentifier().getAddress() + "."
				+ this.getClass().getName(), getUIDService().nextUID());
		NodeComponent node = new NodeComponentImpl(nodeIdentificationService
				.getMessageAddress().getAddress(), getUIDService().nextUID());
		node.setStatus(Component.HEALTHY);
		localSociety.addNode(node);
		getBlackboardService().publishAdd(localSociety);
	}
	class MyCommunityResponseListener implements CommunityResponseListener {
		/**
		 * @see org.cougaar.core.service.community.CommunityResponseListener#getResponse(org.cougaar.core.service.community.CommunityResponse)
		 */
		public void getResponse(CommunityResponse response) {
			if (logging.isDebugEnabled()) {
				logging.debug("Got response from community service: " + response);
			}
		}
	}
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
	/**
	 * predicate to subscribe to configuration update tasks
	 */
	private static UnaryPredicate configUpdateTaskPredicate = new UnaryPredicate() {
		public boolean execute(Object o) {
			if (o instanceof Task) {
				Task t = (Task) o;
				return t.getVerb().equals(Constants.Verb.SOCIETY_CONFIGURATION_UPDATE);
			}
			return false;
		}
	};
	/**
	 * inner class to track components that have been registered with this manager
	 * 
	 * @author mabrams
	 */
	private class ComponentTracker {
		/**
		 * the number of times this component has sent information to the manager
		 */
		private int readCount;
		/**
		 * the time of the last status update from this component
		 */
		private Calendar lastReadTime;
		/**
		 * the component this information is for
		 */
		private Object component;
		public ComponentTracker(Object component) {
			this.component = component;
			lastReadTime = Calendar.getInstance();
		}
		/**
		 * increment read count
		 */
		public void incrementReadCount() {
			setReadCount(readCount + 1);
		}
		/**
		 * @return Returns the component.
		 */
		public Object getComponent() {
			return component;
		}
		/**
		 * @param component
		 *          The component to set.
		 */
		public void setComponent(Object component) {
			this.component = component;
		}
		/**
		 * @return Returns the lastReadTime.
		 */
		public Calendar getLastReadTime() {
			return lastReadTime;
		}
		/**
		 * @param lastReadTime
		 *          The lastReadTime to set.
		 */
		public void setLastReadTime(Calendar lastReadTime) {
			this.lastReadTime = lastReadTime;
		}
		/**
		 * @return Returns the readCount.
		 */
		public int getReadCount() {
			return readCount;
		}
		/**
		 * @param readCount
		 *          The readCount to set.
		 */
		public void setReadCount(int readCount) {
			this.readCount = readCount;
		}
	}
	private class ComponentTrackerThread implements Runnable {
		public void run() {
			//List list = new ArrayList();
			for (Iterator it = trackers.values().iterator(); it.hasNext();) {
				ComponentTracker tracker = (ComponentTracker) it.next();
				Component comp = (Component) tracker.getComponent();
				if (comp.getStatus() == Component.HEALTHY) {
					long currentTime = Calendar.getInstance().getTimeInMillis();
					long timeDiff = currentTime
							- tracker.getLastReadTime().getTimeInMillis();
					if (timeDiff > statusExpireTimeout) {
						if (logging.isWarnEnabled()) {
							logging.warn("Haven not seen status update for component: "
									+ tracker.getComponent() + " for " + timeDiff
									+ " ms, marking the component UNKNOWN");
						}
						comp.setStatus(Component.UNKNOWN);
						if (comp instanceof AgentComponent) {
							getBlackboardService().openTransaction();
							publishAgentUpdateTask((AgentComponent) comp);
							getBlackboardService().closeTransaction();
						} else if (comp instanceof NodeComponent) {
							getBlackboardService().openTransaction();
							publishNodeUpdateTask((NodeComponent) comp);
							getBlackboardService().closeTransaction();
						}
					}
				} else if (comp.getStatus() == Component.UNKNOWN) {
					long currentTime = Calendar.getInstance().getTimeInMillis();
					long timeDiff = currentTime
							- tracker.getLastReadTime().getTimeInMillis();
					if (timeDiff > deadComponentTimeout) {
						if (logging.isWarnEnabled()) {
							logging.warn("Have not seen status update for component: "
									+ tracker.getComponent() + " for " + timeDiff
									+ " ms, marking the component DEAD");
						}
						comp.setStatus(Component.DEAD);
						publishUpdateTask(comp);
						it.remove();
					}
				} else if (comp.getStatus() == Component.DEAD) {
					publishUpdateTask(comp);
					it.remove();
				}
			}
		}
		private void publishUpdateTask(Component component) {
			try {
				getBlackboardService().openTransaction();
				if (component instanceof AgentComponent) {
					publishAgentUpdateTask((AgentComponent) component);
				} else if (component instanceof NodeComponent) {
					publishNodeUpdateTask((NodeComponent) component);
				}
			} finally {
				getBlackboardService().closeTransaction();
			}
		}
	}
}