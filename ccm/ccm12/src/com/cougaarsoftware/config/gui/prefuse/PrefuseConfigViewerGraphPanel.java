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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileFilter;

import com.cougaarsoftware.config.AgentComponent;
import com.cougaarsoftware.config.Component;
import com.cougaarsoftware.config.Society;
import com.cougaarsoftware.config.gui.ConfigViewerController;
import com.cougaarsoftware.config.gui.ConfigViewerGraphPanel;
import com.touchgraph.graphlayout.Node;

/**
 * Prefuse graph panel for viewing Cougaar societies
 * 
 * @author mhelmstetter
 */
public class PrefuseConfigViewerGraphPanel extends ConfigViewerGraphPanel {

    protected PrefuseViewPanel cvp;

    private Society society;

    protected JComboBox localityRadiusCombo;

    protected String selectedComponentName;

    protected JTextPane textPane;

    protected JButton statusButton;

    protected JPopupMenu popupMenu;

    public static int INITIAL_RADIUS = -1;

    public static boolean INITIAL_SHOW_BACKLINKS = false;

    /**
     * Creates a new ConfigViewerGUI object.
     * 
     * @param controller
     *            DOCUMENT ME!
     */
    public PrefuseConfigViewerGraphPanel(ConfigViewerController controller) {
        super(controller);
    }

    /**
     * construct GUI
     */
    protected void initGUI() {
        cvp = new PrefuseViewPanel();
        buildPanel();
        setVisible(true);
        if (INITIAL_RADIUS >= 0 && INITIAL_RADIUS <= 6) {
            localityRadiusCombo.setSelectedIndex(INITIAL_RADIUS);
        }

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
        setLayout(new BorderLayout());
        ToolTipManager.sharedInstance().setInitialDelay(0);
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        localityRadiusCombo = new JComboBox(new String[] { "0", "1", "2", "3",
                "4", "5", "6"});
        localityRadiusCombo.setSelectedIndex(1);
        localityRadiusCombo
                .setToolTipText("Show nodes reachable by following Radius# edges");
        localityRadiusCombo.setPreferredSize(new Dimension(50, 20));
        c.gridx++;
        topPanel.add(new JLabel("Radius"), c);
        c.gridx++;
        topPanel.add(localityRadiusCombo, c);
        c.gridx++;
        topPanel.add(new JLabel("Zoom"), c);
        c.gridx++;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 0, 5);
        //topPanel.add(scrollPanel, c);

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
        add(scrollPanel, BorderLayout.CENTER);
        popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Toggle Controls");
        ActionListener toggleControlsAction = new ActionListener() {

            boolean controlsVisible = true;

            public void actionPerformed(ActionEvent e) {
                controlsVisible = !controlsVisible;
                //horizontalSB.setVisible(controlsVisible);
                //verticalSB.setVisible(controlsVisible);
                topPanel.setVisible(controlsVisible);
            }
        };
        menuItem.addActionListener(toggleControlsAction);
        popupMenu.add(menuItem);
    }
    
    /**
     * @param parent
     */
    public void loadGraph(java.awt.Component parentComponent) {
        JFileChooser chooser = new JFileChooser();
        // Add a custom file filter and disable the default
	    // (Accept All) file filter.
        chooser.addChoosableFileFilter(new XmlFileFilter());
        chooser.setAcceptAllFileFilterUsed(false);

        int returnVal = chooser.showOpenDialog(parentComponent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            cvp.loadGraph(chooser.getSelectedFile());
        }
    }    

    public void saveGraph(java.awt.Component parentComponent) {
        JFileChooser chooser = new JFileChooser();
        // Add a custom file filter and disable the default
	    // (Accept All) file filter.
        chooser.addChoosableFileFilter(new XmlFileFilter());
        chooser.setAcceptAllFileFilterUsed(false);

        int returnVal = chooser.showSaveDialog(parentComponent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            cvp.saveGraph(chooser.getSelectedFile());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cougaarsoftware.config.gui.ConfigViewerGraphPanel#setLocale(com.touchgraph.graphlayout.Node)
     */
    public void setLocale(Node n) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cougaarsoftware.config.gui.ConfigViewerGraphPanel#setFocus(java.lang.String)
     */
    public void setFocus(String nodeID) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cougaarsoftware.config.gui.ConfigViewerGraphPanel#update(com.cougaarsoftware.config.Society)
     */
    protected void update(Society society) {
        // TODO Auto-generated method stub

    }

    public class XmlFileFilter extends FileFilter {

        public final static String XML = "xml";

        //Accept all directories and all gif, jpg, tiff, or png files.
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            if (extension != null) {
                if (extension.equals(XML)) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }

        //The description of this filter
        public String getDescription() {
            return "*.xml";
        }

        /*
         * Get the extension of a file.
         */
        public String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }
    }

    /* (non-Javadoc)
     * @see com.cougaarsoftware.config.gui.ConfigViewerGraphPanel#addAgentComponent(com.cougaarsoftware.config.AgentComponent, java.lang.Object)
     */
    public void addAgentComponent(AgentComponent agentComponent, Object object) {
        // TODO Auto-generated method stub
        
    }
}