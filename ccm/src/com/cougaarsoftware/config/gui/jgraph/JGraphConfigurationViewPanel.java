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
package com.cougaarsoftware.config.gui.jgraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.cougaar.core.component.ComponentDescription;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.jgraph.layout.JGraphLayoutAlgorithm;
import org.jgraph.layout.RadialTreeLayoutAlgorithm;
import org.jgraph.util.JGraphUtilities;

import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.NodeComponent;
import com.cougaarsoftware.config.Society;
import com.cougaarsoftware.config.gui.ConfigViewerApplicationPlugin;
import com.cougaarsoftware.config.gui.ConfigViewerController;
import com.cougaarsoftware.config.gui.ConfigViewerGraphPanel;
import com.cougaarsoftware.config.gui.touchgraph.ConfigNode;
import com.touchgraph.graphlayout.Node;

/**
 * JGraph view of a Cougaar society
 * 
 * @author mhelmstetter
 */
public class JGraphConfigurationViewPanel extends ConfigViewerGraphPanel {

    private JGraph graph;

    private GraphModel model;

    private Map graphCellAttributes;

    private Society society;

    private ConfigViewerApplicationPlugin controller;

    public JGraphConfigurationViewPanel(ConfigViewerController controller) {
        super(controller);
    }
    
	/**
	 * construct GUI
	 */
	protected void initGUI() {
        // Construct Model and Graph
        model = new DefaultGraphModel();
        graph = new JGraph(model) {

            public void updateAutoSize(CellView view) {
                if (view != null
                        && GraphConstants.isAutoSize(view.getAttributes())) {
                    Rectangle bounds = (Rectangle) GraphConstants
                            .getBounds(view.getAttributes());
                    if (bounds != null) {
                        Dimension d = (Dimension) getUI().getPreferredSize(
                                this, view);
                        bounds.setSize(d);
                        GraphConstants.setAutoSize(view.getAttributes(), false);
                    }
                }
            }
        };
        graph.setSelectNewCells(true);
        graph.setBounds(this.getBounds());
        // Create Nested Map (from Cells to Attributes)
        graphCellAttributes = new Hashtable();
        this.add(new JScrollPane(graph));
	}    

    // Insert a new Vertex at point
    //    public void insert(Point2D point, String label) {
    //        DefaultGraphCell vertex = createDefaultGraphCell(point, label);
    //        // Construct a Map from cells to Maps (for insert)
    //        //Hashtable attributes = new Hashtable();
    //        insert(point, vertex);
    //    }

    public synchronized void insert(Point2D point, DefaultGraphCell vertex) {
        // Insert the Vertex and its Attributes (can also use model)
        model.insert(new Object[] { vertex}, graphCellAttributes, null, null,
                null);
        //		graph.getGraphLayoutCache().insert(
        //			new Object[] { vertex },
        //			graphCellAttributes,
        //			null,
        //			null,
        //			null);
    }

    protected void layoutGraph() {

        final RadialTreeLayoutAlgorithm controller = new RadialTreeLayoutAlgorithm();
        Properties props = new Properties();
        // Set the width/height to the width/height of this JComponent
        props.put(RadialTreeLayoutAlgorithm.KEY_WIDTH, String
                .valueOf(getWidth()));
        props.put(RadialTreeLayoutAlgorithm.KEY_HEIGHT, String
                .valueOf(getHeight()));
        //		props.put(RadialTreeLayoutAlgorithm.KEY_CENTRE_X,
        // centerXTextField.getText());
        //		props.put(RadialTreeLayoutAlgorithm.KEY_CENTRE_Y,
        // centerYTextField.getText());
        props.put(RadialTreeLayoutAlgorithm.KEY_RADIUS_X, String
                .valueOf(getWidth() / 2));
        props.put(RadialTreeLayoutAlgorithm.KEY_RADIUS_Y, String
                .valueOf(getHeight() / 2));
        controller.setConfiguration(props);

        // Fork the layout algorithm and leave UI dispatcher thread
        Thread t = new Thread("Layout Algorithm " + controller.toString()) {

            public void run() {
                try {
                    Object[] cells = JGraphUtilities.getAll(graph);
                    if (cells != null && cells.length > 0) {
                        JGraphLayoutAlgorithm.applyLayout(graph, cells,
                                controller);
                    }
                } finally {

                }
            }
        };
        t.start();
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

    public void displayGraph(Component c) {
        Society society = controller.getSocietyConfiguration(false);
        setSociety(society);
        processConfiguration(c);
    }

    private void processNode(NodeComponent node, ConfigNode societyNode,
            String nodeName) {
        DefaultGraphCell nodeNode = createDefaultGraphCell(new Point(10, 10),
                node.getName());
        //DefaultPort nodeNodePort = new DefaultPort();
        //nodeNode.add(nodeNodePort);

        int status = node.getStatus();

        if (status == Component.HEALTHY || status == Component.UNKNOWN) {
            Map agentMap = node.getAgents();
            Vector newAgents = null;
            if (agentMap != null) {
                Set agentKeys = agentMap.keySet();
                Iterator agentIter = agentKeys.iterator();
                while (agentIter.hasNext()) {
                    AgentComponent ac = (AgentComponent) agentMap.get(agentIter
                            .next());
                    String agentName = ac.getName();

                    DefaultGraphCell agentNode = createDefaultGraphCell(
                            new Point(20, 20), agentName);

                    if (newAgents == null) {
                        newAgents = new Vector();
                    }
                    newAgents.add(agentNode);
                    processAgent(agentNode, ac, nodeNode, agentName);
                    connect(nodeNode, agentNode);
                }
                layoutGraph();
                agentIter = null;
            }
        }
    }

    private void processAgent(DefaultGraphCell agentNode, AgentComponent ac,
            DefaultGraphCell nodeNode, String agentName) {
        int status = ac.getStatus();
        if (agentNode == null && status == Component.HEALTHY
                || status == Component.UNKNOWN) {
            //			agentNode = new ConfigNode(agentName, getShortName(agentName),
            //					ConfigNode.AGENT_TYPE, ac.getMessageAddress(), ac.getCommands());
            //			gui.completeEltSet.addNode(agentNode);
            //			agentNode.setType(ConfigNode.TYPE_ROUNDRECT);
            //			ConfigEdge edge = new ConfigEdge(nodeNode, agentNode, 40);
            //			gui.completeEltSet.addEdge(edge);
        } else if (agentNode != null && status == Component.DEAD) {
            //gui.completeEltSet.deleteNode(agentNode);
        }
        if (status == Component.HEALTHY || status == Component.UNKNOWN) {
            Vector childComponents = ac.getChildComponents();
            if (childComponents != null) {
                Iterator agentCompIter = ac.getChildComponents().iterator();
                while (agentCompIter.hasNext()) {
                    ComponentDescription cd = (ComponentDescription) agentCompIter
                            .next();
                    String compName = getShortName(cd.getClassname());
                    DefaultGraphCell compNode = createDefaultGraphCell(
                            new Point(20, 20), compName);
                    connect(agentNode, compNode);

                    //					ConfigNode compNode = (ConfigNode) gui.completeEltSet
                    //							.findNode(nodeNode.getLabel() + "." + agentName + "." +
                    // cName);
                    //					if (compNode == null) {
                    //						compNode = new ConfigNode(nodeNode.getLabel() + "." +
                    // agentName
                    //								+ "." + cd.getName(), getShortName(cName),
                    //								ConfigNode.COMPONENT_TYPE);
                    //						gui.completeEltSet.addNode(compNode);
                    //						compNode.setType(ConfigNode.TYPE_RECTANGLE);
                    //						ConfigEdge edge = new ConfigEdge(agentNode, compNode,
                    // 70);
                    //						// gui.completeEltSet.addEdge(edge);
                    //						gui.completeEltSet.addEdge(edge);
                    //					}
                }
                layoutGraph();
                agentCompIter = null;
            }
        }
    }

    private static String getShortName(String longName) {
        String shortName = "";
        if (longName.lastIndexOf('.') > 0) {
            shortName = longName.substring(longName.lastIndexOf('.') + 1);
        } else {
            shortName = longName;
        }
        return shortName;
    }

    private DefaultGraphCell createDefaultGraphCell(Point2D point, String label) {
        DefaultGraphCell vertex = new DefaultGraphCell(label);
        // Add one Floating Port
        vertex.add(new DefaultPort());
        // Create a Map that holds the attributes for the Vertex
        //AttributeMap map = graph.getModel().createAttributes();
        Map map = new Hashtable();
        graphCellAttributes.put(vertex, map);

        // Snap the Point to the Grid
        point = graph.snap((Point2D) point.clone());
        // Default Size for the new Vertex
        Dimension size = new Dimension(25, 25);
        // Add a Bounds Attribute to the Map
        GraphConstants.setBounds(map, new Rectangle2D.Double(
    			point.getX(), point.getY(), size.width, size.height));
        // Add a Border Color Attribute to the Map
        //GraphConstants.setBorderColor(map, Color.black);
        GraphConstants.setBorder(map, BorderFactory.createRaisedBevelBorder());

        GraphConstants.setBackground(map, Color.ORANGE);
        // Make Vertex Opaque
        GraphConstants.setOpaque(map, true);

        GraphConstants.setAutoSize(map, true);

        insert(point, vertex);
        return vertex;
    }

    private void connect(DefaultGraphCell hello, DefaultGraphCell world) {

        DefaultPort hp = new DefaultPort();
        hello.add(hp);
        DefaultPort wp = new DefaultPort();
        world.add(wp);

        DefaultEdge edge = new DefaultEdge();

        // Create Edge Attributes
        //
        Map edgeAttrib = new Hashtable();
        graphCellAttributes.put(edge, edgeAttrib);
        // Set Arrow
        int arrow = GraphConstants.ARROW_CLASSIC;
        GraphConstants.setLineEnd(edgeAttrib, arrow);
        GraphConstants.setEndFill(edgeAttrib, true);

        // Connect Edge
        ConnectionSet cs = new ConnectionSet(edge, hp, wp);
        Object[] cells = new Object[] { edge, hello, world};

        // Insert into Model
        //
        model.insert(cells, graphCellAttributes, cs, null, null);
    }

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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see com.cougaarsoftware.config.gui.ConfigViewerGraphPanel#setLocale(com.touchgraph.graphlayout.Node)
     */
    public void setLocale(Node n) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.cougaarsoftware.config.gui.ConfigViewerGraphPanel#setFocus(java.lang.String)
     */
    public void setFocus(String nodeID) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.cougaarsoftware.config.gui.ConfigViewerGraphPanel#update(com.cougaarsoftware.config.Society)
     */
    protected void update(Society society) {
        // TODO Auto-generated method stub
        
    }

}

