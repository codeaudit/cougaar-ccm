/*
 * <copyright> Copyright 2000-2004 Cougaar Software, Inc. All Rights Reserved
 * </copyright>
 */
package com.cougaarsoftware.config.gui.prefuse;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.cougaar.core.component.ComponentDescription;

import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.NodeComponent;
import com.cougaarsoftware.config.Society;
import com.touchgraph.graphlayout.TGException;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.TreeFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;

/**
 * @author mhelmstetter
 * @version $Revision: 1.1 $
 *  
 */
public class PrefuseViewPanel extends JPanel {

    private ItemRegistry registry;

    private Display display;

    private Graph graph;

    private Society society;
    
    private Map nodeMap;
    
    private ActionList layout;
    
    private ActionList forces;
    
    private ActionList filter;

    public PrefuseViewPanel() {
        super();
        graph = new DefaultGraph(Collections.EMPTY_LIST, false);

        registry = new ItemRegistry(graph);
        registry.setGraph(graph);
        display = new Display();
        //Controller controller = new Controller();
        nodeMap = Collections.synchronizedMap(new HashMap());

        //      initialize renderers
        Renderer nodeRenderer = new TextImageItemRenderer();
        Renderer edgeRenderer = new DefaultEdgeRenderer() {

            protected int getLineWidth(VisualItem item) {
                try {
                    String wstr = item.getAttribute("weight");
                    return Integer.parseInt(wstr);
                } catch (Exception e) {
                    return m_width;
                }
            } //
        };
        registry.setRendererFactory(new DefaultRendererFactory(nodeRenderer,
                edgeRenderer, null));

        // initialize display
        display.setItemRegistry(registry);
        display.setSize(600, 600);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        //display.setFont(curFont);
        //display.getTextEditor().addKeyListener(controller);
        //display.addControlListener(controller);
        display.addControlListener(new DragControl(true));
        display.addControlListener(new FocusControl(0));
        
        layout = new ActionList(registry);
        layout.add(new RadialTreeLayout());
        layout.add(new RepaintAction());
        
//      create a filter to map input data into visual items
        filter = new ActionList(registry);
        //filter.add(new GraphFilter());
        filter.add(new TreeFilter());
        
        // create a force simulator using anti-gravity (n-body force),
        //  a spring force on edges, and a drag (friction) force
        ForceSimulator fsim = new ForceSimulator();
        fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
        fsim.addForce(new SpringForce(2E-5f, 75f));
        fsim.addForce(new DragForce(-0.01f));
        
        // create a list of actions that 
        // (a) use the force simulator to continuously update the 
        //     position and speed of items, 
        // (b) set item colors, and 
        // (c) repaint the display.
        //
        // The -1 indicates that the list should continuously re-run
        //  infinitely, while the 20 tells it to wait at least 20
        //  milliseconds between runs.
        forces = new ActionList(registry,-1,10000);
        forces.add(new ForceDirectedLayout(fsim, false, false));
        //forces.add(new DemoColorFunction());
        forces.add(new RepaintAction());        
        
        this.setLayout(new GridLayout());
        this.add(display);
        
        // filter the input graph into visualized content
        filter.runNow();        
    }
    
	/**
	 * set the society object used by this panel and rebuild the graph
	 * 
	 * @param society
	 *          The society to set.
	 */
	public void setSociety(Society society) {
		this.society = society;
	}

	/**
	 * @param nc
	 */
	public void processConfiguration(Component comp) {
		try {
			NodeComponent nc = null;
			if (comp instanceof NodeComponent) {
				nc = (NodeComponent) comp;
			} else if (comp instanceof AgentComponent) {
				AgentComponent ac = (AgentComponent) comp;
				String parentNode = ac.getParentNode();
				nc = society.getNode(parentNode);
			}
			if (nc != null) {
				processNode(nc, null, nc.getName());
			}
		} catch (TGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param node
	 */
	private void processNode(NodeComponent node, DefaultTreeNode societyNode,
			String nodeName) throws TGException {
	    DefaultTreeNode nodeNode = (DefaultTreeNode)nodeMap.get(nodeName);
		
		int status = node.getStatus();
		if (nodeNode == null && status == Component.HEALTHY
				|| status == Component.UNKNOWN) {
			nodeNode = addNodeNode(nodeNode, societyNode, nodeName);
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
					AgentComponent ac = (AgentComponent) agentMap.get(agentIter.next());
					String agentName = ac.getName();
					DefaultTreeNode agentNode = (DefaultTreeNode)nodeMap.get(nodeName+"."+agentName);
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
//		if (this.getAllNodes() != null && this.getAllNodes().hasNext()) {
//			TGConfigNode initialNode = (TGConfigNode) this.getAllNodes().next();
//			if (initialNode != null) {
//				this.setSelect(initialNode);
//				gui.setLocale(initialNode);
//			}
//		}
		update();
		
	}
	
	/**
	 * @param ac
	 * @param nodeNode
	 * @param agentName
	 */
	private void processAgent(DefaultTreeNode agentNode, AgentComponent ac,
	        DefaultTreeNode nodeNode, NodeComponent node, String agentName) throws TGException {   
		int status = ac.getStatus();
		if (agentNode == null && status == Component.HEALTHY
				|| status == Component.UNKNOWN) {
			agentNode = new DefaultTreeNode();
			graph.addNode(agentNode);
			nodeMap.put(node.getName()+"."+agentName, agentNode);
			DefaultEdge edge = new DefaultEdge(nodeNode, agentNode);
			graph.addEdge(edge);
		} else if (agentNode != null && status == Component.DEAD) {
			//gui.completeEltSet.deleteNode(agentNode);
		}
		if (status == Component.HEALTHY || status == Component.UNKNOWN) {
			Vector childComponents = ac.getChildComponents();
			if (childComponents != null) {
				Iterator agentCompIter = ac.getChildComponents().iterator();
				while (agentCompIter.hasNext()) {
					ComponentDescription cd = (ComponentDescription) agentCompIter.next();
					String compName = getShortName(cd.getClassname());
					String key = node.getName() + "." + agentName + "." + compName;
					DefaultTreeNode compNode = (DefaultTreeNode) nodeMap.get(key);
					
					if (compNode == null) {
						compNode = new DefaultTreeNode();
						compNode.setAttribute("label", compName);
						nodeMap.put(key, compNode);
						graph.addNode(compNode);
						DefaultEdge edge = new DefaultEdge(agentNode, compNode);
						graph.addEdge(edge);
					}
				}
				agentCompIter = null;
			}
		}
		//update();
		
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
	
	private DefaultTreeNode addNodeNode(DefaultTreeNode nodeNode, DefaultTreeNode societyNode,
			String nodeName) throws TGException {
		nodeNode = new DefaultTreeNode();
		//nodeNode.setAttribute("id", nodeName);
		nodeNode.setAttribute("label", nodeName);
		nodeMap.put(nodeName, nodeNode);
		graph.addNode(nodeNode);
		if (societyNode != null) {
			DefaultEdge edge = new DefaultEdge(societyNode, nodeNode);
			graph.addEdge(edge);
		}
		update();
		
		return nodeNode;
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
}