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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.cougaar.core.component.ComponentDescription;
import org.cougaar.core.mts.MessageAddress;
import org.cougaar.util.ConfigFinder;
import org.cougaar.util.log.Logger;
import org.cougaar.util.log.LoggerFactory;

import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Capability;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.NodeComponent;
import com.cougaarsoftware.config.Society;

/**
 * Main panel for the configuration gui
 * 
 * @author mabrams
 */
public class ConfigViewerPanel extends JPanel {

    private final static String FRAME_NAME = "Configuration Viewer";

    /**
     * cougaar plugin that controls this GUI
     */
    private ConfigViewerApplicationPlugin controller;

    /**
     * the scroll pane containing the tree view of the society
     */
    private TreePanel treeScrollPane;

    /**
     * a split pane with the tree view and the graph view of the society
     */
    private JSplitPane splitPane;

    /**
     * the pane with the graph view of the society
     */
    private ConfigViewerGraphPanel graphPanel;

    /**
     * logging
     */
    private static Logger logging;
    static {
        logging = LoggerFactory.getInstance().createLogger(
                ConfigViewerPanel.class);
    }

    /**
     * constructor
     * 
     * @param controller -
     *            the controller plugin for the gui
     */
    public ConfigViewerPanel(ConfigViewerApplicationPlugin controller) {
        super();
        this.controller = controller;
        //this.setJMenuBar(createMenuBar());
        this.setLayout(new GridLayout());
        addSplitPane();
    }

    /**
     * adds menu to the main gui
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        //create the menu
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        menu.addSeparator();
        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (logging.isDebugEnabled()) {
                    logging.debug("Exit Config Viewer");
                }
                System.exit(0);
            }
        });
        menu.add(menuItem);
        menu = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_T);
        menuBar.add(menu);
        menuItem = new JMenuItem("Options", KeyEvent.VK_O);
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                showOptionsGUI();
            }
        });
        menu.add(menuItem);
        return menuBar;
    }

    private void showOptionsGUI() {
        // TODO Auto-generated method stub
    }

    /**
     * add the split pane to the main frame
     *  
     */
    private void addSplitPane() {
        treeScrollPane = new TreePanel();
        String graphPanelClass = controller.getGraphPanelClass();
        if (graphPanelClass != null) {
            try {
                Class cl = Class.forName(graphPanelClass);
                Class[] paramTypes = { ConfigViewerApplicationPlugin.class};
                Constructor constructor = cl.getConstructor(paramTypes);
                Object[] argList = { controller};
                graphPanel = (ConfigViewerGraphPanel) constructor
                        .newInstance(argList);
            } catch (Exception e) {
                if (logging.isErrorEnabled()) {
                    logging.error("Error instantiating graph panel class '"
                            + graphPanelClass + "'", e);
                }
            }
        } else {
            if (logging.isErrorEnabled()) {
                logging.error("Graph panel class not specified");
            }
            return;
        }

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane,
                graphPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);
        Dimension minimumSize = new Dimension(100, 50);
        treeScrollPane.setMinimumSize(minimumSize);
        graphPanel.setMinimumSize(minimumSize);
        splitPane.setPreferredSize(new Dimension(600, 600));
        if (splitPane != null) {
            add(splitPane);
        }
    }

    /**
     * called by the controller when changes or updates to the society are
     * detected
     * 
     * @param society
     */
    public void updateTreeData(Society society) {
        treeScrollPane.populateTree(society);
    }

    /**
     * @param string
     */
    public void updateAgentData(AgentComponent agentComponent) {
        treeScrollPane.updateAgentComponent(agentComponent);
    }

    public void updateNodeData(NodeComponent nodeComponent) {
        treeScrollPane.updateNodeComponent(nodeComponent);
    }

    /**
     * Tree panel containing hierarchical tree of society components
     * 
     * @author mabrams
     */
    private class TreePanel extends JPanel implements TreeSelectionListener {

        private JTree tree;

        protected DefaultMutableTreeNode rootNode;

        protected DefaultTreeModel treeModel;

        public TreePanel() {
            super(new GridLayout(1, 0));
            //Create a tree that allows one selection at a time.
            rootNode = new DefaultMutableTreeNode("Society");
            treeModel = new DefaultTreeModel(rootNode);
            treeModel.addTreeModelListener(new MyTreeModelListener());
            tree = new JTree(treeModel);
            tree.addTreeSelectionListener(this);
            tree.addMouseListener(new PopupListener());
            tree.getSelectionModel().setSelectionMode(
                    TreeSelectionModel.SINGLE_TREE_SELECTION);
            //          Enable tool tips.
            ToolTipManager.sharedInstance().registerComponent(tree);
            ImageIcon greenIcon = createImageIcon("green.gif");
            ImageIcon redIcon = createImageIcon("red.gif");
            ImageIcon yellowIcon = createImageIcon("yellow.gif");
            if (greenIcon != null && yellowIcon != null && redIcon != null) {
                tree.setCellRenderer(new MyRenderer(greenIcon, yellowIcon,
                        redIcon));
            } else {
                if (logging.isWarnEnabled()) {
                    logging.warn("Missing health status icons");
                }
            }
            //Create the scroll pane and add the tree to it.
            JScrollPane treeView = new JScrollPane(tree);
            this.add(treeView);
        }

        /**
         * gets the popup menu for the selected component. if there are not menu
         * options available for that component NULL is returned
         * 
         * @return the popupmenu for the selected component or null if there are
         *         not command options
         */
        private JPopupMenu getPopupMenu(AgentComponent ac) {
            JPopupMenu popupMenu = new JPopupMenu();
            Map commandMap = ac.getCommands();
            if (commandMap != null) {
                Set keys = commandMap.keySet();
                Iterator i = keys.iterator();
                while (i.hasNext()) {
                    String component = (String) i.next();
                    Vector commandList = (Vector) commandMap.get(component);
                    Iterator j = commandList.iterator();
                    String subMenuStr = component.substring(component
                            .lastIndexOf('.') + 1);
                    JMenu subMenu = new JMenu(subMenuStr);
                    popupMenu.add(subMenu);
                    while (j.hasNext()) {
                        final Capability command = (Capability) j.next();
                        String displayName = command.getDisplayName();
                        JMenuItem menuItem = new JMenuItem(displayName);
                        menuItem.addActionListener(new CommandActionListener(
                                command, ac.getMessageAddress()));
                        subMenu.add(menuItem);
                    }
                }
                return popupMenu;
            }
            return null;
        }

        class CommandActionListener implements ActionListener {

            private Capability command;

            private MessageAddress address;

            public CommandActionListener(Capability command,
                    MessageAddress agentAddress) {
                this.command = command;
                this.address = agentAddress;
            }

            public void actionPerformed(ActionEvent e) {
                controller.executeCommand(command, address);
            }
        };

        /**
         * @param agentComponent
         */
        public void updateAgentComponent(AgentComponent agentComponent) {
            boolean updated = false;
            int childCount = rootNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode cNode = (DefaultMutableTreeNode) rootNode
                        .getChildAt(i);
                Component node = (Component) cNode.getUserObject();
                if (node.getName().equals(agentComponent.getParentNode())) {
                    int nodeChildCount = cNode.getChildCount();
                    for (int j = 0; j < nodeChildCount; j++) {
                        DefaultMutableTreeNode cAgent = (DefaultMutableTreeNode) cNode
                                .getChildAt(j);
                        Component agent = (Component) cAgent.getUserObject();
                        if (agent.getName().equals(agentComponent.getName())) {
                            this.removeObject(cAgent);
                            this.addAgent(cNode, agentComponent);
                            updated = true;
                            i = childCount;
                            j = nodeChildCount;
                        }
                    }
                    if (!updated) {
                        this.addAgent(cNode, agentComponent);
                    }
                }
            }
        }

        /**
         * @param agentComponent
         */
        public void updateNodeComponent(NodeComponent nodeComponent) {
            boolean updated = false;
            int childCount = rootNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode cNode = (DefaultMutableTreeNode) rootNode
                        .getChildAt(i);
                Component node = (Component) cNode.getUserObject();
                if (node.getName().equals(nodeComponent.getName())) {
                    this.removeObject(cNode);
                    this.addNode(rootNode, nodeComponent);
                    updated = true;
                    i = childCount;
                }
            }
            if (!updated) {
                this.addNode(rootNode, nodeComponent);
            }
        }

        protected void populateTree(Society society) {
            rootNode.setUserObject(society);
            Collection nodes = society.getNodeComponents();
            if (nodes != null) {
                Iterator i = nodes.iterator();
                while (i.hasNext()) {
                    updateNodeComponent((NodeComponent) i.next());
                }
            }
        }

        private void addAgent(DefaultMutableTreeNode parent,
                AgentComponent agent) {
            DefaultMutableTreeNode aNode = this.addObject(parent, agent);
            List components = agent.getChildComponents();
            if (components != null) {
                Iterator k = components.iterator();
                while (k.hasNext()) {
                    ComponentDescription cd = (ComponentDescription) k.next();
                    addObject(aNode, cd.getName());
                }
            }
        }

        private void addNode(DefaultMutableTreeNode parent, NodeComponent node) {
            DefaultMutableTreeNode pNode = this.addObject(parent, node, true);
            Map agentMap = node.getAgents();
            if (agentMap != null) {
                Set keys = agentMap.keySet();
                if (keys != null) {
                    Iterator j = keys.iterator();
                    synchronized (agentMap) {
                        while (j.hasNext()) {
                            String agentName = (String) j.next();
                            AgentComponent agent = (AgentComponent) agentMap
                                    .get(agentName);
                            this.addAgent(pNode, agent);
                        }
                    }
                }
            }
        }

        /** Add child to the currently selected node. */
        public DefaultMutableTreeNode addObject(Object child) {
            DefaultMutableTreeNode parentNode = null;
            TreePath parentPath = tree.getSelectionPath();
            if (parentPath == null) {
                parentNode = rootNode;
            } else {
                parentNode = (DefaultMutableTreeNode) (parentPath
                        .getLastPathComponent());
            }
            return addObject(parentNode, child, true);
        }

        public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                Object child) {
            return addObject(parent, child, false);
        }

        public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                Object child, boolean shouldBeVisible) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            if (parent == null) {
                parent = rootNode;
            }
            treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
            //Make sure the user can see the lovely new node.
            if (shouldBeVisible) {
                tree.scrollPathToVisible(new TreePath(childNode.getPath()));
            }
            return childNode;
        }

        public void removeObject(DefaultMutableTreeNode node) {
            treeModel.removeNodeFromParent(node);
        }

        class MyTreeModelListener implements TreeModelListener {

            public void treeNodesChanged(TreeModelEvent e) {
            }

            public void treeNodesInserted(TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
            }
        }

        class PopupListener extends MouseAdapter {

            public PopupListener() {
            }

            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int selectedRow = tree
                            .getRowForLocation(e.getX(), e.getY());
                    TreePath selectedPath = tree.getPathForLocation(e.getX(), e
                            .getY());
                    if (selectedRow != -1) {
                        DefaultMutableTreeNode tn = (DefaultMutableTreeNode) (selectedPath
                                .getLastPathComponent());
                        Object userObject = tn.getUserObject();
                        if (userObject instanceof AgentComponent) {
                            JPopupMenu popup = getPopupMenu((AgentComponent) userObject);
                            popup.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
         */
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                    .getLastSelectedPathComponent();
            if (node == null)
                return;
            Object nodeInfo = node.getUserObject();
            if (nodeInfo instanceof Component) {
                graphPanel.displayGraph((Component) nodeInfo);
                graphPanel.setFocus(((Component) nodeInfo).getName());
            } else if (nodeInfo instanceof String) {
                graphPanel.setFocus((String) nodeInfo);
            }
            if (logging.isDebugEnabled()) {
                logging.debug("User selected TreeNode: " + nodeInfo);
            }
        }
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = null;
        try {
            imgURL = ConfigFinder.getInstance().find(path);
        } catch (IOException e) {
            if (logging.isDebugEnabled()) {
                logging.error("Could not find gree image icon", e);
            }
        }
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            return null;
        }
    }

    private class MyRenderer extends DefaultTreeCellRenderer {

        Icon red;

        Icon green;

        Icon yellow;

        public MyRenderer(Icon healthy, Icon unknown, Icon notHealthy) {
            red = notHealthy;
            green = healthy;
            this.yellow = unknown;
        }

        public java.awt.Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean sel, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded,
                    leaf, row, hasFocus);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof Component) {
                Component c = (Component) userObject;
                int status = Math.max(c.getStatus(), childHealthStatus(node));
                status = Math.max(status, parentHealthStatus(node, status));
                setIcon(status);
            }
            return this;
        }

        private void setIcon(int status) {
            if (status == Component.HEALTHY) {
                setToolTipText("This component is healthy.");
                setIcon(green);
            } else if (status == Component.UNKNOWN) {
                setToolTipText("This component's health is unknwon.");
                setIcon(yellow);
            } else {
                setToolTipText("This component is dead.");
                setIcon(red);
            }
        }

        private int childHealthStatus(DefaultMutableTreeNode node) {
            int ret = 0;
            int childCount = node.getChildCount();
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
                        .getChildAt(i);
                Object childObject = child.getUserObject();
                if (childObject instanceof Component) {
                    Component childC = (Component) childObject;
                    ret = Math.max(ret, childC.getStatus());
                }
                if (child.getChildCount() > 0) {
                    ret = Math.max(ret, childHealthStatus(child));
                }
            }
            return ret;
        }

        private int parentHealthStatus(DefaultMutableTreeNode node, int status) {
            int ret = status;
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node
                    .getParent();
            if (parentNode != null) {
                Component parent = (Component) parentNode.getUserObject();
                ret = Math.max(parent.getStatus(), ret);
            }
            return ret;
        }
    }
}