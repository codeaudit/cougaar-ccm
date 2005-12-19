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
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.service.AgentIdentificationService;
import org.cougaar.core.service.UIDService;
import com.cougaarsoftware.config.lp.ConfigurationDirectiveImpl;
import com.cougaarsoftware.config.lp.NewConfigurationDirective;
/**
 * Implementation of the <code>ConfigurationFactory</code>
 * 
 * @author mabrams
 * 
 * @see com.cougaarsoftware.core.configuration.domain.ConfigurationFactory
 */
public class ConfigurationFactoryImpl implements ConfigurationFactory {
	private ServiceBroker sb;
	private UIDService uidService;
	private AgentIdentificationService aidService;
	private MessageAddress messageAddress;
	public static final String STATUS_PROTOTYPE = "com.cougaarsoftware.core.configuration.domain.assets.Status";
	/**
	 * constructor
	 * 
	 * @param broker
	 */
	public ConfigurationFactoryImpl(ServiceBroker broker) {
		this.sb = broker;
		this.uidService = (UIDService) sb.getService(this, UIDService.class, null);
		this.aidService = (AgentIdentificationService) sb.getService(this,
				AgentIdentificationService.class, null);
		this.messageAddress = aidService.getMessageAddress();
	}
	/**
	 * create a new configuration directive
	 * 
	 * @return new configuration directive
	 */
	public NewConfigurationDirective createNewConfigurationDirective() {
		NewConfigurationDirective ncd = new ConfigurationDirectiveImpl(uidService
				.nextUID());
		ncd.setSource(messageAddress);
		return ncd;
	}
}