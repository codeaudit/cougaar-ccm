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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.cougaarsoftware.config.Capability;
import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.interaction.DragNodeUI;
import com.touchgraph.graphlayout.interaction.TGAbstractDragUI;
import com.touchgraph.graphlayout.interaction.TGUserInterface;

/**
 * A touch graph user interface customized for cougaar configuration
 * 
 * @author mabrams
 */
public class ConfigNavigateUI extends TGUserInterface {
	private ConfigViewerGUI gui;
	private TGConfigurationViewPanel cvp;
	private ConfigMouseListener ml;
	private TGAbstractDragUI hvDragUI;
	private DragNodeUI dragNodeUI;
	JPopupMenu nodeNodePopup;
	JPopupMenu nodePopup;
	JPopupMenu nodeAgentPopup;
	JPopupMenu nodeComponentPopup;
	JPopupMenu edgePopup;
	ConfigNode popupNode;
	Edge popupEdge;

	public ConfigNavigateUI(ConfigViewerGUI gui) {
		this.gui = gui;
		cvp = gui.getTGConfigurationViewPanel();
		hvDragUI = gui.hvScroll.getHVDragUI();
		dragNodeUI = new DragNodeUI(cvp);
		ml = new ConfigMouseListener();
		setUpNodePopup();
		setUpNodeNodePopup();
		setUpNodeAgentPopup();
		setUpNodeComponentPopup();
		setUpEdgePopup();
	}
	class ConfigMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				triggerPopup(e);
			} else {
				Node mouseOverN = cvp.getMouseOverN();
				if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
					if (mouseOverN == null)
						hvDragUI.activate(e);
					else
						dragNodeUI.activate(e);
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			Node mouseOverN = cvp.getMouseOverN();
			if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
				if (mouseOverN != null) {
					if (e.getClickCount() == 1) {
						cvp.setSelect(mouseOverN);
					} else {
						cvp.setSelect(mouseOverN);
						gui.setLocale(mouseOverN);
					}
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				triggerPopup(e);
			}
		}

		public void triggerPopup(MouseEvent e) {
			popupNode = (ConfigNode) cvp.getMouseOverN();
			popupEdge = cvp.getMouseOverE();
			if (popupNode != null) {
				cvp.setMaintainMouseOver(true);
				if (popupNode.getComponentType() == ConfigNode.NODE_TYPE) {
					nodeNodePopup.show(e.getComponent(), e.getX(), e.getY());
				} else if (popupNode.getComponentType() == ConfigNode.AGENT_TYPE) {
					if (popupNode.getCommandMap() != null) {
						getNodeAgentPopup(popupNode, popupNode.getCommandMap());
					}
					nodeAgentPopup.show(e.getComponent(), e.getX(), e.getY());
				} else if (popupNode.getComponentType() == ConfigNode.COMPONENT_TYPE) {
					nodeComponentPopup.show(e.getComponent(), e.getX(), e.getY());
				} else {
					nodePopup.show(e.getComponent(), e.getX(), e.getY());
				}
			} else if (popupEdge != null) {
				cvp.setMaintainMouseOver(true);
				edgePopup.show(e.getComponent(), e.getX(), e.getY());
			} else {
				gui.popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	/**
	 * @param vector
	 */
	private void getNodeAgentPopup(ConfigNode node, Map commandMap) {
		nodeAgentPopup = getNodePopup();
		JMenuItem menuItem;
		menuItem = new JMenuItem("Add Component");
		nodeAgentPopup.add(menuItem);
		menuItem = new JMenuItem("Remove Agent");
		ActionListener removeAgentAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (popupNode != null) {
					ConfigNode node = null;
					Iterator i = popupNode.getEdges();
					while (i.hasNext()) {
						Edge edge = (Edge) i.next();
						node = (ConfigNode) edge.getFrom();
						if (node.getComponentType() == ConfigNode.NODE_TYPE) {
							break;
						}
					}
					gui.showRemoveAgentDialog(popupNode.getLabel(), node.getLabel());
				}
			}
		};
		menuItem.addActionListener(removeAgentAction);
		nodeAgentPopup.add(menuItem);
		nodeAgentPopup.addSeparator();
		Set keys = commandMap.keySet();
		Iterator i = keys.iterator();		
		while (i.hasNext()) {
			String component = (String)i.next();
			Vector commandList = (Vector)commandMap.get(component);
			Iterator j = commandList.iterator();
			String subMenuStr = component.substring(component.lastIndexOf('.')+1);
			JMenu subMenu = new JMenu(subMenuStr);
			nodeAgentPopup.add(subMenu);
			while (j.hasNext()) {
				final Capability command = (Capability) j.next();
				String displayName = command.getDisplayName();				
				menuItem = new JMenuItem(displayName);
				menuItem.addActionListener(new CommandActionListener(command, node));
				subMenu.add(menuItem);				
			}
		
		}
	}
	class CommandActionListener implements ActionListener {
		private Capability command;
		private ConfigNode node;

		public CommandActionListener(Capability command, ConfigNode node) {
			this.command = command;
			this.node = node;
		}

		public void actionPerformed(ActionEvent e) {
			if (popupNode != null) {
				gui.controller.executeCommand(command, node.getAgentAddress());
			}
		}
	};

	/**
	 * @see com.touchgraph.graphlayout.interaction.TGUserInterface#activate()
	 */
	public void activate() {
		cvp.addMouseListener(ml);
	}

	public void deactivate() {
		cvp.removeMouseListener(ml);
	}

	private void setUpNodeNodePopup() {
		nodeNodePopup = getNodePopup();
		JMenuItem menuItem;
		menuItem = new JMenuItem("Add Agent");
		nodeNodePopup.add(menuItem);
		menuItem = new JMenuItem("Remove All Child Agents");
		nodeNodePopup.add(menuItem);
	}

	private void setUpNodeAgentPopup() {
		nodeAgentPopup = getNodePopup();
		JMenuItem menuItem;
		menuItem = new JMenuItem("Add Component");
		nodeAgentPopup.add(menuItem);
		menuItem = new JMenuItem("Remove Agent");
		ActionListener removeAgentAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (popupNode != null) {
					ConfigNode node = null;
					Iterator i = popupNode.getEdges();
					while (i.hasNext()) {
						Edge edge = (Edge) i.next();
						node = (ConfigNode) edge.getFrom();
						if (node.getComponentType() == ConfigNode.NODE_TYPE) {
							break;
						}
					}
					gui.showRemoveAgentDialog(popupNode.getLabel(), node.getLabel());
				}
			}
		};
		menuItem.addActionListener(removeAgentAction);
		nodeAgentPopup.add(menuItem);
	}

	private void setUpNodeComponentPopup() {
	}

	private void setUpNodePopup() {
		nodePopup = getNodePopup();
	}

	private JPopupMenu getNodePopup() {
		JPopupMenu nodePopup = new JPopupMenu();
		JMenuItem menuItem;
		menuItem = new JMenuItem("Expand Node");
		ActionListener expandAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (popupNode != null) {
					cvp.expandNode(popupNode);
				}
			}
		};
		menuItem.addActionListener(expandAction);
		nodePopup.add(menuItem);
		menuItem = new JMenuItem("Collapse Node");
		ActionListener collapseAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (popupNode != null) {
					cvp.collapseNode(popupNode);
				}
			}
		};
		menuItem.addActionListener(collapseAction);
		nodePopup.add(menuItem);
		menuItem = new JMenuItem("Hide Node");
		ActionListener hideAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (popupNode != null) {
					cvp.hideNode(popupNode);
				}
			}
		};
		menuItem.addActionListener(hideAction);
		nodePopup.add(menuItem);
		nodePopup.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				cvp.setMaintainMouseOver(false);
				cvp.setMouseOverN(null);
				cvp.repaint();
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});
		return nodePopup;
	}

	private void setUpEdgePopup() {
		edgePopup = new JPopupMenu();
		JMenuItem menuItem;
		menuItem = new JMenuItem("Hide Edge");
		ActionListener hideAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (popupEdge != null) {
					cvp.hideEdge(popupEdge);
				}
			}
		};
		menuItem.addActionListener(hideAction);
		edgePopup.add(menuItem);
		edgePopup.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				cvp.setMaintainMouseOver(false);
				cvp.setMouseOverE(null);
				cvp.repaint();
				//                wikiNodeHintUI.activate();
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});
	}
}