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
package com.cougaarsoftware.config.lp;
import java.util.Collection;
import org.cougaar.core.blackboard.Directive;
import org.cougaar.core.blackboard.EnvelopeTuple;
import org.cougaar.core.blackboard.SubscriberException;
import org.cougaar.core.component.ServiceBroker;
import org.cougaar.core.domain.EnvelopeLogicProvider;
import org.cougaar.core.domain.LogicProvider;
import org.cougaar.core.domain.MessageLogicProvider;
import org.cougaar.core.domain.RootPlan;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.util.UniqueObject;
/**
 * Looks for new outgoing configuration directives to send and publishes the
 * payload of incomming configuration directives to the local blackboard
 * 
 * @author mabrams
 */
public class ConfigurationLP
		implements
			LogicProvider,
			MessageLogicProvider,
			EnvelopeLogicProvider {
	private final MessageAddress self;
	private ServiceBroker serviceBroker;
	private RootPlan rootplan;
	private LoggingService logging;
	/**
	 * constructor
	 * 
	 * @param rootplan
	 * @param self
	 * @param broker
	 */
	public ConfigurationLP(RootPlan rootplan, MessageAddress self,
			ServiceBroker broker) {
		this.serviceBroker = broker;
		this.rootplan = rootplan;
		logging = (LoggingService) serviceBroker.getService(this,
				LoggingService.class, null);
		this.self = self;
	}
	/**
	 * Do nothing
	 */
	public void init() {
	}
	/**
	 * handle the directive message. Call examine to check for and handle objects
	 * that are relevant
	 * 
	 * @param directive
	 *          the Directive
	 * @param changeReports
	 *          the objects that need to be examied
	 */
	public void execute(Directive directive, Collection changeReports) {
		if (directive instanceof ConfigurationDirective) {
			ConfigurationDirective configurationDirective = (ConfigurationDirective) directive;
			if (logging.isDebugEnabled()) {
				logging.debug(self + ": Received a configurationDirective");
			}
			// make sure it isn't already there? Otherwise...
			Object o = configurationDirective.getPayload();
			UniqueObject exists = rootplan.findUniqueObject(((UniqueObject) o)
					.getUID());
			try {
				if ((exists != null) && exists.equals(o)) {
					rootplan.remove(directive);
				}
				rootplan.add(configurationDirective.getPayload());
				rootplan.remove(directive);
			} catch (SubscriberException se) {
				if (logging.isErrorEnabled()) {
					logging.error("Could not add ConfigurationDirective to logplan: "
							+ directive, se);
				}
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.domain.EnvelopeLogicProvider#execute(org.cougaar.core.blackboard.EnvelopeTuple,
	 *      java.util.Collection)
	 */
	public void execute(EnvelopeTuple m, Collection changeReports) {
		Object o; // = m.getObject();
		if ((m.isAdd() || m.isChange()) && ((o = m.getObject()) != null)
				&& o instanceof ConfigurationDirective) {
			ConfigurationDirective cd = (ConfigurationDirective) o;
			examine(cd);
		}
	}
	/**
	 * examine a configuration directive and send it to ensure it reaches the
	 * destination
	 * 
	 * @param directive
	 */
	private void examine(ConfigurationDirective directive) {
		if (logging.isDebugEnabled()) {
			logging.debug(self + ": processing configuration directive");
		}
		if (!directive.isSent()) {
			rootplan.sendDirective(directive);
			rootplan.remove(directive);
			directive.markSent();
		}
	}
}