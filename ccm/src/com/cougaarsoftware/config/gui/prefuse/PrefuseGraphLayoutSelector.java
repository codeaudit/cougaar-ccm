/*
 * <copyright> Copyright 2000-2004 Cougaar Software, Inc. All Rights Reserved
 * </copyright>
 */
package com.cougaarsoftware.config.gui.prefuse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefusex.layout.BalloonTreeLayout;
import edu.berkeley.guir.prefusex.layout.CircleLayout;
import edu.berkeley.guir.prefusex.layout.FruchtermanReingoldLayout;
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;

/**
 * @author mhelmstetter
 * @version $Revision: 1.1 $
 *  
 */
public class PrefuseGraphLayoutSelector extends JPanel {

    private Vector layoutItems;

    private JComboBox layoutCombo;

    private ActionList layoutAction;

    private Layout selectedLayout;

    public PrefuseGraphLayoutSelector(ActionList layoutAction) {
        super();
        this.layoutAction = layoutAction;
        layoutItems = initLayoutItems();
        layoutCombo = new JComboBox(layoutItems);
        layoutCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GraphLayoutItem selected = (GraphLayoutItem) layoutCombo
                        .getSelectedItem();
                selectLayout(selected, true);
            }
        });
        this.add(new JLabel("Layout:"));
        this.add(layoutCombo);
        this.setMaximumSize(this.getPreferredSize());
        this.setOpaque(false);
        selectLayout((GraphLayoutItem) layoutCombo.getSelectedItem(), false);
    }

    public void selectLayout(GraphLayoutItem selected, boolean run) {
        layoutAction.remove(selectedLayout);
        selectedLayout = selected.getLayout();
        layoutAction.add(selectedLayout);
        if (run) {
            layoutAction.runNow();
        }
    }

    private Vector initLayoutItems() {
        layoutItems = new Vector();

        //        ForceSimulator fsim = new ForceSimulator();
        //        fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
        //        fsim.addForce(new SpringForce(4E-5f, 75f));
        //        fsim.addForce(new DragForce(-0.005f));
        //        layoutItems.add(new GraphLayoutItem(new ForceDirectedLayout(fsim,
        //                false, false), "Force Directed"));
        layoutItems.add(new GraphLayoutItem(new FruchtermanReingoldLayout(),
                "Fruchterman-Reingold"));
        layoutItems.add(new GraphLayoutItem(new BalloonTreeLayout(),
                "Balloon Tree"));
        RadialTreeLayout r = new RadialTreeLayout();
        r.setStartTheta(90);
        layoutItems.add(new GraphLayoutItem(r, "Radial"));
        layoutItems.add(new GraphLayoutItem(new CircleLayout(), "Circle"));
        return layoutItems;
    }

    class GraphLayoutItem {

        private Layout layout;

        private Class graphLayoutClass;

        private String displayName;

        public GraphLayoutItem(Layout layout, String displayName) {
            this.graphLayoutClass = layout.getClass();
            this.layout = layout;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Class getGraphLayoutClass() {
            return graphLayoutClass;
        }

        public Layout getLayout() {
            return layout;
        }

        public String toString() {
            return displayName;
        }
    }
}