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
package com.cougaarsoftware.config.gui.prefuse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.cougaar.core.component.ComponentDescription;

import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.NodeComponent;
import com.cougaarsoftware.config.Society;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.animate.ColorAnimator;
import edu.berkeley.guir.prefuse.action.animate.PolarLocationAnimator;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.WindowedTreeFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultTree;
import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.GraphWriter;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphWriter;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;

/**
 * Prefuse graph panel for viewing Cougaar societies
 * 
 * @author mhelmstetter
 * @version $Revision: 1.1 $
 *  
 */
public class PrefuseViewPanel extends JPanel {

    private ItemRegistry registry;

    private Display display;

    private Graph graph;

    private Society society;

    private Map nodeMap = Collections.synchronizedMap(new HashMap());

    private ActionList filter, layout, update, animate;

    private DefaultTreeNode rootNode;
    
    public static WindowedTreeFilter feye;

    public PrefuseViewPanel() {
        super();
        graph = new DefaultTree();
        rootNode = new DefaultTreeNode();
        rootNode.setAttribute("label", "root");
        ((DefaultTree) graph).setRoot(rootNode);

        registry = new ItemRegistry(graph);
        registry.setGraph(graph);
        display = new Display();
        Renderer nodeRenderer = new TextImageItemRenderer();
        Renderer edgeRenderer = new DefaultEdgeRenderer() {

            protected int getLineWidth(VisualItem item) {
                try {
                    String wstr = item.getAttribute("weight");
                    return Integer.parseInt(wstr);
                } catch (Exception e) {
                    return m_width;
                }
            }
        };
        registry.setRendererFactory(new DefaultRendererFactory(nodeRenderer,
                edgeRenderer, null));

        //      initialize action pipelines
        filter = new ActionList(registry);
        //filter.add(new TreeFilter());
        filter.add((feye=new WindowedTreeFilter(-4)));

        layout = new ActionList(registry);
        layout.add(new RepaintAction());
        //layout.add(new FruchtermanReingoldLayout());

        update = new ActionList(registry);
        update.add(new DemoColorFunction());
        //update.add(new RepaintAction());

        animate = new ActionList(registry, 1500, 20);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new PolarLocationAnimator());
        animate.add(new ColorAnimator());
        animate.add(new RepaintAction());

        // initialize display
        display.setItemRegistry(registry);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        display.addControlListener(new FocusControl());
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new NeighborHighlightControl(update));
        
        //this.setLayout(new GridLayout());
        //this.add(display);
        JToolBar toolbar = new JToolBar();
        toolbar.add(new PrefuseGraphLayoutSelector(layout));
        JButton layoutButton = new JButton("Layout");
        layoutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        toolbar.add(layoutButton);
        this.setLayout(new BorderLayout());
        this.add(toolbar, BorderLayout.NORTH);
        this.add(display, BorderLayout.CENTER);
        
        Dimension pref = getPreferredSize();
        display.setSize(new Dimension(pref.width-60, pref.height-toolbar.getPreferredSize().height));

        // run filter, layout, and perform initial animation
        filter.runNow();
        layout.runNow();
        animate.runNow();
    }

    /**
     * set the society object used by this panel and rebuild the graph
     * 
     * @param society
     *            The society to set.
     */
    public void setSociety(Society society) {
        this.society = society;
    }

    /**
     * @param nc
     */
    public void processConfiguration(Component comp) {
        NodeComponent nc = null;
        if (comp instanceof NodeComponent) {
            nc = (NodeComponent) comp;
        } else if (comp instanceof AgentComponent) {
            AgentComponent ac = (AgentComponent) comp;
            String parentNode = ac.getParentNode();
            nc = society.getNode(parentNode);
        }
        if (nc != null) {
            processNode(nc, nc.getName());
        }
    }

    /**
     * @param node
     */
    private void processNode(NodeComponent node, String nodeName) {
        DefaultTreeNode nodeNode = (DefaultTreeNode) nodeMap.get(nodeName);

        int status = node.getStatus();
        if (nodeNode == null && status == Component.HEALTHY
                || status == Component.UNKNOWN) {
            //nodeNode = addNodeNode(nodeNode, societyNode, nodeName);
            nodeNode = createNode(nodeName, null);
        } else if (nodeNode != null && status == Component.DEAD) {
            //gui.completeEltSet.deleteNode(nodeNode);
        }
        if (status == Component.HEALTHY || status == Component.UNKNOWN) {
            Vector oldAgents = null;
            if (nodeNode != null) {
                oldAgents = getExistingAgents(nodeNode);
            }
            Map agentMap = node.getAgents();
            Vector newAgents = null;
            if (agentMap != null) {
                Set agentKeys = agentMap.keySet();
                Iterator agentIter = agentKeys.iterator();
                while (agentIter.hasNext()) {
                    AgentComponent ac = (AgentComponent) agentMap.get(agentIter
                            .next());
                    String agentName = ac.getName();
                    DefaultTreeNode agentNode = (DefaultTreeNode) nodeMap
                            .get(nodeName + "." + agentName);
                    if (newAgents == null) {
                        newAgents = new Vector();
                    }
                    newAgents.add(agentNode);
                    processAgent(agentNode, ac, nodeNode, node, agentName);
                }
                agentIter = null;
            }
            //checkForRemovedNodes(newAgents, oldAgents);
        }
        update();
    }

    /**
     * @param ac
     * @param nodeNode
     * @param agentName
     */
    private void processAgent(DefaultTreeNode agentNode, AgentComponent ac,
            DefaultTreeNode nodeNode, NodeComponent node, String agentName) {
        int status = ac.getStatus();
        if (agentNode == null && status == Component.HEALTHY
                || status == Component.UNKNOWN) {
            agentNode = createNode(agentName,
                    nodeNode);
        } else if (agentNode != null && status == Component.DEAD) {
            //gui.completeEltSet.deleteNode(agentNode);
        }
        if (status == Component.HEALTHY || status == Component.UNKNOWN) {
            Collection childComponents = ac.getChildComponents();
            if (childComponents != null) {
                Iterator agentCompIter = ac.getChildComponents().iterator();
                while (agentCompIter.hasNext()) {
                    ComponentDescription cd = (ComponentDescription) agentCompIter
                            .next();
                    String compName = getShortName(cd.getClassname());
                    String key = node.getName() + "." + agentName + "."
                            + compName;
                    DefaultTreeNode compNode = (DefaultTreeNode) nodeMap
                            .get(key);

                    if (compNode == null) {
                        compNode = createNode(key, compName, agentNode);
                    }
                }
                agentCompIter = null;
            }
        }
        //update();

    }

    /**
     * Create a new node, add it to the graph and to the node map
     * 
     * @param key
     * @param name
     * @param parent
     * @return The new node
     */
//    private DefaultTreeNode createNode(String key, String name,
//            DefaultTreeNode parent) {
//        DefaultTreeNode node = new DefaultTreeNode();
//        node.setAttribute("label", name);
//        node.setAttribute("color", "#FFFFCC");
//        graph.addNode(node);
//        nodeMap.put(key, node);
//        if (parent != null) {
//            DefaultEdge edge = new DefaultEdge(parent, node);
//            graph.addEdge(edge);
//        }
//        return node;
//    }
    
    /**
     * Create a new node, add it to the graph and to the node map
     * 
     * @param key
     * @param name
     * @param parent
     * @return The new node
     */
    private DefaultTreeNode createNode(Object key, Object name,
            final DefaultTreeNode parent) {
        if (key == null) {
            key = name;
        }
        final DefaultTreeNode node = new DefaultTreeNode();
        node.setAttribute("label", name.toString());
        node.setAttribute("color", "#FFFFCC");
        nodeMap.put(key, node);
        if (parent != null) {
            DefaultEdge edge = new DefaultEdge(parent, node);
            edge.setAttribute("weight", "1");
            parent.addChild(edge);
        } else {
            DefaultEdge edge = new DefaultEdge(rootNode, node);
            rootNode.addChild(edge);
        }

        update();

        return node;
    }
    
    private DefaultTreeNode createNode(Object name, DefaultTreeNode parent) {
        return createNode(null, name, parent);
    }

    private void update() {
        //forces.runNow();
        filter.runNow();
        layout.runNow();
    }

    private Vector getExistingAgents(DefaultTreeNode node) {
        Iterator i = node.getEdges();
        Vector oldAgents = null;
        if (i != null) {
            while (i.hasNext()) {
                Edge configEdge = (Edge) i.next();
                if (configEdge.getSecondNode().equals(node)) {
                    if (oldAgents == null) {
                        oldAgents = new Vector();
                    }
                    oldAgents.add(configEdge.getFirstNode());
                }
            }
        }
        i = null;
        return oldAgents;
    }

    private String getShortName(String longName) {
        String shortName = "";
        if (longName.lastIndexOf('.') > 0) {
            shortName = longName.substring(longName.lastIndexOf('.') + 1);
        } else {
            shortName = longName;
        }
        return shortName;
    }
    
    public void loadGraph(File f) {
        XMLGraphReader gr = new XMLGraphReader();
		gr.setNodeType(DefaultTreeNode.class);
		try {
		    graph = gr.loadGraph(f);
		} catch ( Exception ex ) {
		    JOptionPane.showMessageDialog(
					display,
					"An error occurred while loading the graph.",
					"Error Loading Graph",
					JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
		}
		registry.setGraph(graph);
		update();
    }
    
	public void saveGraph(File f) {
		GraphWriter gw = new XMLGraphWriter();
		 try {					 
			gw.writeGraph(graph, f);
			//saveFile = f;
			//setEdited(false);
		 } catch ( Exception ex ) {
			JOptionPane.showMessageDialog(
				display,
				"An error occurred while saving the graph.",
				"Error Saving Graph",
				JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		 }
	}    

    class DemoColorFunction extends ColorFunction {

        private Color pastelRed = new Color(255, 125, 125);

        private Color pastelOrange = new Color(255, 200, 125);

        private Color lightGray = new Color(220, 220, 255);

        public Paint getColor(VisualItem item) {
            if (item instanceof EdgeItem) {
                EdgeItem edgeItem = (EdgeItem) item;
                edgeItem.getEntity().getAttributes();
                if (item.isHighlighted())
                    return pastelOrange;
                else
                    return Color.LIGHT_GRAY;
            } else {
                return Color.BLACK;
            }
        } //

        public Paint getFillColor(VisualItem item) {
            if (item.isHighlighted())
                return pastelOrange;
            else if (item instanceof NodeItem) {
                if (item.isFixed())
                    return pastelRed;
                else
                    return lightGray;
            } else {
                return Color.BLACK;
            }
        } //        
    } // end of inner class DemoColorFunction
}