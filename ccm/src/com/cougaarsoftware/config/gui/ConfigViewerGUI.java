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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.Society;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGAbstractLens;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGLensSet;
import com.touchgraph.graphlayout.TGPoint2D;
import com.touchgraph.graphlayout.graphelements.GraphEltSet;
import com.touchgraph.graphlayout.interaction.HVScroll;
import com.touchgraph.graphlayout.interaction.TGUIManager;
import com.touchgraph.graphlayout.interaction.ZoomScroll;

/**
 * the main gui for viewing cougaar societies from within cougaar
 * 
 * @author mabrams
 */
public class ConfigViewerGUI extends JPanel {
	protected ConfigViewerApplicationPlugin controller;
	protected TGConfigurationViewPanel cvp;
	protected GraphEltSet completeEltSet;
	protected TGLensSet tgLensSet;
	protected ZoomScroll zoomScroll;
	protected HVScroll hvScroll;
	protected Stack browseHistory = new Stack();
	protected JComboBox localityRadiusCombo;
	protected String selectedComponentName;
	protected JTextPane textPane;
	protected JButton statusButton;
	protected JPopupMenu popupMenu;
	public static int INITIAL_RADIUS = -1;
	public static boolean INITIAL_SHOW_BACKLINKS = false;
	private TGUIManager tgUIManager;

	/**
	 * Creates a new ConfigViewerGUI object.
	 * 
	 * @param controller
	 *          DOCUMENT ME!
	 */
	public ConfigViewerGUI(ConfigViewerApplicationPlugin controller) {
		super();
		this.controller = controller;
		initGUI();
	}

	/**
	 * construct GUI
	 */
	private void initGUI() {
		completeEltSet = new GraphEltSet();
		cvp = new TGConfigurationViewPanel(this);
		cvp.setGraphEltSet(completeEltSet);
		tgLensSet = new TGLensSet();
		hvScroll = new HVScroll(cvp, tgLensSet);
		zoomScroll = new ZoomScroll(cvp);
		buildPanel();
		buildLens();
		cvp.setLensSet(tgLensSet);
		addUIs();
		setVisible(true);
		zoomScroll.setZoomValue(4);
		if (INITIAL_RADIUS >= 0 && INITIAL_RADIUS <= 6)
			localityRadiusCombo.setSelectedIndex(INITIAL_RADIUS);
		cvp.fastFinishAnimation();
		cvp.resetDamper();
	}

	private void addUIs() {
		tgUIManager = new TGUIManager();
		ConfigNavigateUI navigateUI = new ConfigNavigateUI(this);
		tgUIManager.addUI(navigateUI, "Configuration");
		tgUIManager.activate("Configuration");
	}

	/**
	 *  
	 */
	private void buildLens() {
		tgLensSet.addLens(hvScroll.getLens());
		tgLensSet.addLens(zoomScroll.getLens());
		tgLensSet.addLens(new HorizontalStretchLens());
		tgLensSet.addLens(cvp.getAdjustOriginLens());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getPanelName() {
		return "ConfigViewer";
	}

	public void displayGraph(Component c) {
		Society society = controller.getSocietyConfiguration(false);
		cvp.setSociety(society);
		cvp.processConfiguration(c);
		cvp.repaint();
	}

	/**
	 * DOCUMENT ME!
	 */
	public void prepareForDisplay() {
	}

	private void buildPanel() {
		final JScrollBar horizontalSB = hvScroll.getHorizontalSB();
		final JScrollBar verticalSB = hvScroll.getVerticalSB();
		final JScrollBar zoomSB = zoomScroll.getZoomSB();
		//final JScrollBar localitySB = localityScroll.getLocalitySB();
		setLayout(new BorderLayout());
		ToolTipManager.sharedInstance().setInitialDelay(0);
		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		final JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		localityRadiusCombo = new JComboBox(new String[]{"0", "1", "2", "3", "4",
				"5", "6"});
		localityRadiusCombo.setSelectedIndex(1);
		localityRadiusCombo
				.setToolTipText("Show nodes reachable by following Radius# edges");
		ActionListener setLocaleAL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLocale(cvp.getSelect());
			}
		};
		localityRadiusCombo.addActionListener(setLocaleAL);
		localityRadiusCombo.setPreferredSize(new Dimension(50, 20));
		c.gridx = 8;
		c.weightx = 0;
		topPanel.add(new Label("Radius", Label.RIGHT), c);
		c.gridx = 9;
		c.weightx = 0;
		topPanel.add(localityRadiusCombo, c);
		c.gridx = 11;
		c.weightx = 0;
		topPanel.add(new Label("Zoom", Label.RIGHT), c);
		c.gridx = 12;
		c.weightx = 0.5;
		c.insets = new Insets(0, 0, 0, 5);
		topPanel.add(zoomSB, c);
		c.insets = new Insets(0, 0, 0, 0);
		add(topPanel, BorderLayout.NORTH);
		//        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		scrollPanel.add(cvp, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		scrollPanel.add(verticalSB, c);
		c.gridx = 0;
		c.gridy = 2;
		scrollPanel.add(horizontalSB, c);
		add(scrollPanel, BorderLayout.CENTER);
		popupMenu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Toggle Controls");
		ActionListener toggleControlsAction = new ActionListener() {
			boolean controlsVisible = true;

			public void actionPerformed(ActionEvent e) {
				controlsVisible = !controlsVisible;
				horizontalSB.setVisible(controlsVisible);
				verticalSB.setVisible(controlsVisible);
				topPanel.setVisible(controlsVisible);
			}
		};
		menuItem.addActionListener(toggleControlsAction);
		popupMenu.add(menuItem);
	}

	public void setTextPane(String label) {
		selectedComponentName = label;
		new Thread() {
			public void run() {
				textPane.setText(selectedComponentName);
			}
		}.start();
	}

	public void setLocale(Node n) {
		try {
			int localityRadius = Integer.parseInt((String) localityRadiusCombo
					.getSelectedItem());
			cvp.setLocale(n, localityRadius);
			cvp.setSelect(n);
		} catch (TGException tge) {
			tge.printStackTrace();
		}
	}
	class HorizontalStretchLens extends TGAbstractLens {
		protected void applyLens(TGPoint2D p) {
			p.x = p.x * 1.5;
		}

		protected void undoLens(TGPoint2D p) {
			p.x = p.x / 1.5;
		}
	}

	/**
	 * @return
	 */
	public TGConfigurationViewPanel getTGConfigurationViewPanel() {
		return cvp;
	}

	/**
	 * @param component
	 */
	public void setFocus(String nodeID) {
		Node node = completeEltSet.findNode(nodeID);
		if (node != null) {
			setLocale(node);
			cvp.repaint();
		}
	}

	/**
	 *  
	 */
	public void showRemoveAgentDialog(String agentName, String nodeName) {
		if (controller.getAgentId().equals(
				agentName.substring(agentName.lastIndexOf(':') + 1))) {
			JOptionPane.showMessageDialog(this, "You have attempted to remove the "
					+ "agent that this gui is running on.  " + "This is not allowed");
		} else if (agentName.substring(agentName.lastIndexOf(':') + 1).equals(
				nodeName)) {
			JOptionPane.showMessageDialog(this, "You have attempted to remove the "
					+ "node agent.  If you want " + "to remove the node, right click "
					+ "on the node and select \'Remove Node\'.");
		} else {
			int n = JOptionPane.showConfirmDialog(this, "Remove Agent: " + agentName
					+ "?", "Remove Agent", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				controller.removeAgent(agentName, nodeName);
			}
			Society society = controller.getSocietyConfiguration(false);
			cvp.processConfiguration(society);
			cvp.repaint();
		}
	}
}