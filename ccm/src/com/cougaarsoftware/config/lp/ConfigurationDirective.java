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
import java.io.Serializable;
import org.cougaar.core.blackboard.Directive;
import org.cougaar.core.blackboard.Publishable;
import org.cougaar.core.util.UniqueObject;
/**
 * Configuration directives contain information about the configuration of a
 * Cougaar Node or Agent.
 * 
 * @author mabrams
 */
public interface ConfigurationDirective
		extends
			Directive,
			UniqueObject,
			Publishable,
			Serializable {
	String AGENT_CONFIGURATION = "AGENT_CONFIGURATION";
	String SOCIETY_CONFIGURATION = "SOCIETY_CONFIGURATION";
	/**
	 * Describes the type of configuration object contained in this directive
	 * 
	 * @return the type of the configuration (node or agent)
	 */
	public String getType();
	/**
	 * indicates if this directive has been sent
	 * 
	 * @return true if the directive has been sent
	 */
	boolean isSent();
	/**
	 * marks this directive as sent
	 */
	void markSent();
	/**
	 * get the payload object containing the configuration information
	 * 
	 * @return the configuration object
	 */
	public Object getPayload();
}