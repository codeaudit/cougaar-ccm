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
package com.cougaarsoftware.config.domain;
import org.cougaar.core.component.ServiceBroker;
import org.cougaar.core.domain.DomainAdapter;
import org.cougaar.core.domain.Factory;
import org.cougaar.core.domain.RootPlan;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.service.AgentIdentificationService;
import org.cougaar.planning.ldm.LogPlan;
import org.cougaar.planning.ldm.LogPlanImpl;
import com.cougaarsoftware.config.lp.ConfigurationLP;
/**
 * The configuration domain contains logic providers for handling configuration
 * directives
 * 
 * @author mabrams
 */
public class ConfigurationDomain extends DomainAdapter {    										  
	public static final String DOMAIN_NAME = "configuration";
	private AgentIdentificationService agentIdService;
	private MessageAddress self;
	/**
	 * called by infrastructure to get domain name
	 * 
	 * @return the configuration domain name
	 */
	public String getDomainName() {
		return DOMAIN_NAME;
	}
	/**
	 * set the agent identification service
	 * 
	 * @param ais
	 *          the AgentIdentificationService
	 */
	public void setAgentIdentificationService(AgentIdentificationService ais) {
		this.agentIdService = ais;
		if (ais == null) {
			// Revocation
		} else {
			this.self = ais.getMessageAddress();
		}
	}
	/**
	 * @see org.cougaar.core.domain.DomainAdapter.load()
	 */
	public void load() {
		super.load();
	}
	/**
	 * releases the services used by the domain
	 */
	public void unload() {
		ServiceBroker sb = getBindingSite().getServiceBroker();
		if (agentIdService != null) {
			sb.releaseService(this, AgentIdentificationService.class, agentIdService);
			agentIdService = null;
		}
		super.unload();
	}
	/**
	 * loads the factory for creating configuration domain objects
	 */
	protected void loadFactory() {
		Factory f = new ConfigurationFactoryImpl(this.getBindingSite()
				.getServiceBroker());
		setFactory(f);
	}
	/**
	 *  
	 */
	protected void loadXPlan() {
		LogPlan logplan = new LogPlanImpl();
		setXPlan(logplan);
	}
	/**
	 * load the Configuration LPs
	 * 
	 * @throws RuntimeException
	 *           DOCUMENT ME!
	 */
	protected void loadLPs() {
		RootPlan rootplan = (RootPlan) getXPlanForDomain("root");
		if (rootplan == null) {
			throw new RuntimeException("Missing \"root\" plan!");
		}
		addLogicProvider(new ConfigurationLP(rootplan, self, this.getBindingSite()
				.getServiceBroker()));
	}
}