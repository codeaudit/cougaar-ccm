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

import java.util.Calendar;
import junit.framework.TestCase;
import org.cougaar.core.component.ComponentDescription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.core.plugin.PluginManager;
import org.cougaar.core.util.UID;
import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.AgentComponentImpl;
import com.cougaarsoftware.config.NodeComponent;
import com.cougaarsoftware.config.NodeComponentImpl;
import com.cougaarsoftware.config.Society;
import com.cougaarsoftware.config.SocietyImpl;
import com.touchgraph.graphlayout.graphelements.GraphEltSet;

/**
 * @author mabrams
 */
public class TGConfigurationViewPanelTest extends TestCase {
	private TGConfigurationViewPanel panel;

	public static void main(String[] args) {
		TGConfigurationViewPanelTest test = new TGConfigurationViewPanelTest();
		try {
			test.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.testProcessConfiguration();
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		ConfigViewerApplicationPlugin controller = new ConfigViewerApplicationPlugin();
		ConfigViewerGUI gui = new ConfigViewerGUI(controller);
		GraphEltSet completeEltSet = new GraphEltSet();
		panel = new TGConfigurationViewPanel(gui);
		panel.setGraphEltSet(completeEltSet);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		panel = null;
	}

	public void testRebuildGraph() {
	}

	public void testProcessConfiguration() {
		Society society = createTestSociety();
		Calendar startTime = Calendar.getInstance();
		panel.processConfiguration(society);
		Calendar endTime = Calendar.getInstance();
		System.out.println("Graph processing time = "
				+ (endTime.getTimeInMillis() - startTime.getTimeInMillis()));
		System.exit(0);
	}

	/**
	 * @return
	 */
	private Society createTestSociety() {
		UID uid = new UID();
		Society society = new SocietyImpl("TestSociety", uid);
		int numNodes = 10;
		int numAgents = 100;
		int numComponents = 10;
		
		for (int i = 0; i < numNodes; i++) {
			NodeComponent nc = createNodeComponent("TestNode" + i);
			for (int j = 0; j < numAgents; j++) {
				AgentComponent ac = createAgentComponent("TestAgent" + j);				
				for (int k = 0; k < numComponents; k++) {
					ComponentDescription cd = createComponentDescription("TestComponent" + k);
					ac.addChildComponent(cd);
				}
				nc.addAgent(ac);
			}
			society.addNode(nc);
		}
			
		return society;
	}
	
	private NodeComponent createNodeComponent(String name) {
		UID uid = new UID();
		NodeComponent nodeComponent = new NodeComponentImpl(name, uid);
		return nodeComponent;
	}
	
	private AgentComponent createAgentComponent(String name) {
		UID uid = new UID();
		AgentComponent agentComponent = new AgentComponentImpl(name, uid,
				MessageAddress.getMessageAddress(name));
		return agentComponent;
	}
	
	private ComponentDescription createComponentDescription(String name) {
		ComponentDescription cd = new ComponentDescription(name,
				PluginManager.INSERTION_POINT, "className", null, null, null, null,
				null);
		return cd;
	}
}