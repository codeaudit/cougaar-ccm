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
/**
 * constants used by the config manager project
 * 
 * @author mabrams
 */
public class Constants {
	/**
	 * prevent construction
	 * 
	 * @author mabrams
	 */
	private Constants() {
	}
	public static class Verb {
		private static final String AGENT_CONFIGURATION_UPDATE_STRING = "agentConfigChangeTask";
		public static final org.cougaar.planning.ldm.plan.Verb AGENT_CONFIGURATION_UPDATE = org.cougaar.planning.ldm.plan.Verb
				.get(AGENT_CONFIGURATION_UPDATE_STRING);
		private static final String START_CONFIG_VIEWER_STRING = "startConfigViewerTask";
		public static final org.cougaar.planning.ldm.plan.Verb START_CONFIG_VIEWER = org.cougaar.planning.ldm.plan.Verb
				.get(START_CONFIG_VIEWER_STRING);
		private static final String UPDATE_AGENT_CONFIG_TASK_STRING = "updateAgentTask";
		public static final org.cougaar.planning.ldm.plan.Verb UPDATE_AGENT_CONFIG_TASK = org.cougaar.planning.ldm.plan.Verb
				.get(UPDATE_AGENT_CONFIG_TASK_STRING);
		private static final String UPDATE_NODE_CONFIG_TASK_STRING = "updateNodeTask";
		public static final org.cougaar.planning.ldm.plan.Verb UPDATE_NODE_CONFIG_TASK = org.cougaar.planning.ldm.plan.Verb
				.get(UPDATE_NODE_CONFIG_TASK_STRING);
		private static final String SOCIETY_CONFIGURATION_UPDATE_STRING = "societyConfigChangeTask";
		public static final org.cougaar.planning.ldm.plan.Verb SOCIETY_CONFIGURATION_UPDATE = org.cougaar.planning.ldm.plan.Verb
				.get(SOCIETY_CONFIGURATION_UPDATE_STRING);
		private static final String ADD_COMMAND_STRING = "addCommand";
		public static final org.cougaar.planning.ldm.plan.Verb ADD_COMMAND = org.cougaar.planning.ldm.plan.Verb
				.get(ADD_COMMAND_STRING);
		
	}
	public static class Preposition {
		public static final String AGENT_COMPONENT = "AgentComponent";
		public static final String NODE_COMPONENT = "NodeComponent";
		public static final String COMMAND = "Command";
	}
}