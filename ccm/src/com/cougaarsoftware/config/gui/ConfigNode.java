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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Map;

import org.cougaar.core.mts.MessageAddress;

import com.touchgraph.graphlayout.TGPanel;

/**
 * Extends normal TG Node by adding refrence to Cougaar component the node
 * represents
 * 
 * @author mabrams
 */
public class ConfigNode extends com.touchgraph.graphlayout.Node {
    
    public static final Font SMALL_TAG_FONT = new Font("Arial",Font.PLAIN,9);

    // Variables that store default values for colors + fonts + node type
     public static Color BACK_FIXED_COLOR        = Color.red;
     public static Color BACK_SELECT_COLOR       = Color.WHITE;
     public static Color BACK_DEFAULT_COLOR      = Color.WHITE;
     public static Color BACK_HILIGHT_COLOR      = Color.WHITE;
     
     public static Color MARKED_FOR_REMOVAL_COLOR = Color.WHITE;
     public static Color JUST_MADE_LOCAL_COLOR = Color.WHITE;
     
     public static Color BORDER_DRAG_COLOR       = Color.black;
     public static Color BORDER_MOUSE_OVER_COLOR = new Color(160,160,160);
     
     public static Color BORDER_UNSELECTED_COLOR   = Color.GRAY;
     public static Color BORDER_SELECTED_COLOR   = Color.GRAY;

     public static Color TEXT_COLOR              = Color.BLACK;
     
     public static Font TEXT_FONT = new Font("Arial",Font.BOLD,10);
     
     public static final int BORDER_SIZE = 1;
     
     public static Color NODE_COLOR = new Color(255, 255, 204);
     public static Color COMPONENT_COLOR = new Color(221, 221, 221);
     public static Color AGENT_COLOR = new Color(221, 206, 255);
    
    
    
    
	public final static int HOST_TYPE = 0;
	public final static int NODE_TYPE = 1;
	public final static int AGENT_TYPE = 2;
	public final static int COMPONENT_TYPE = 3;
	private int componentType;
	private Map commandMap;
	private MessageAddress agentAddress;

	public ConfigNode(String name, String label, int type) {
		this(name, label, type, null);
	}
	public ConfigNode(String name, String label, int type, MessageAddress agentAddress) {
	    this(name, label, type, agentAddress, null);
	}

	public ConfigNode(String name, String label, int type, MessageAddress agentAddress,
			Map commandMap) {
	    super(name, label);
		this.componentType = type;
		this.commandMap = commandMap;
		this.agentAddress = agentAddress;
	}

	/**
	 * @return Returns the componentType.
	 */
	public int getComponentType() {
		return componentType;
	}

	/**
	 * @param componentType
	 *          The componentType to set.
	 */
	public void setComponentType(int type) {
		this.componentType = type;
	}

	public void paint(Graphics g, TGPanel tgPanel) {
		if (!intersects(tgPanel.getSize()))
			return;
		paintNodeBody(g, tgPanel);
		int ix = (int) drawx;
		int iy = (int) drawy;
		int h = getHeight();
		int w = getWidth();
		if (visibleEdgeCount() < edgeCount()) {
			int tagX = ix + (w - 6) / 2 - 2 + w % 2;
			int tagY = iy - h / 2;
			String hiddenEdgeStr = String.valueOf(edgeCount() - visibleEdgeCount());
			g.setColor(Color.red);		
			g.fillRect(tagX, tagY, 3 + 5 * hiddenEdgeStr.length(), 8);
			g.setColor(Color.white);
			g.setFont(SMALL_TAG_FONT);
			g.drawString(hiddenEdgeStr, tagX + 2, tagY + 7);
		}
	}
	
    /** Paints the background of the node, along with its label */
    public void paintNodeBody( Graphics g, TGPanel tgPanel) {
        g.setFont(TEXT_FONT);
        fontMetrics = g.getFontMetrics();
        
        int ix = (int)drawx;
        int iy = (int)drawy;
        int h = getHeight();
        int w = getWidth();
        int r = h/2+1; // arc radius

        Color borderCol = getPaintBorderColor(tgPanel);
        g.setColor(borderCol);

        if ( typ == TYPE_ROUNDRECT ) {             
            g.fillRoundRect(ix - w/2, iy - h / 2, w, h, r, r);
        } else if ( typ == TYPE_ELLIPSE ) {
            g.fillOval(ix - w/2, iy - h / 2, w, h );
        } else if ( typ == TYPE_CIRCLE ) { // just use width for both dimensions
            g.fillOval(ix - w/2, iy - w / 2, w, w );
        } else { // TYPE_RECTANGLE
            g.fillRect(ix - w/2, iy - h / 2, w, h);
        }

        Color backCol = getPaintBackColor(tgPanel);
        g.setColor(backCol);

        if ( typ == TYPE_ROUNDRECT ) {
            g.fillRoundRect(ix - w/2+BORDER_SIZE, iy - h / 2+BORDER_SIZE, w-BORDER_SIZE*2, h-BORDER_SIZE*2, r, r );
        } else if ( typ == TYPE_ELLIPSE ) {
            g.fillOval(ix - w/2+BORDER_SIZE, iy - h / 2+2, w-BORDER_SIZE*2, h-BORDER_SIZE*2 );
        } else if ( typ == TYPE_CIRCLE ) {
            g.fillOval(ix - w/2+BORDER_SIZE, iy - w / 2+BORDER_SIZE, w-BORDER_SIZE*2, w-BORDER_SIZE*2 );
        } else { // TYPE_RECTANGLE
            g.fillRect(ix - w/2+BORDER_SIZE, iy - h / 2+BORDER_SIZE, w-BORDER_SIZE*2, h-BORDER_SIZE*2);
        }

        Color textCol = getPaintTextColor(tgPanel);
        g.setColor(textCol);
        g.drawString(lbl, ix - fontMetrics.stringWidth(lbl)/2, iy + fontMetrics.getDescent() +1);
    }	

	public int getWidth() {
		if (fontMetrics != null && lbl != null) {
			if (typ != TYPE_ELLIPSE)
				return fontMetrics.stringWidth(lbl) + 8;
			else
				return fontMetrics.stringWidth(lbl) + 28;
		} else
			return 8;
	}

	public int getHeight() {
		if (fontMetrics != null)
			return fontMetrics.getHeight() + 2;
		else
			return 8;
	}

	Color myBrighter(Color c) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		if (b > r + 64 && b > g + 64) {
			r += 32;
			g += 32;
		}
		r = Math.min(r + 144, 255);
		g = Math.min(g + 144, 255);
		b = Math.min(b + 144, 255);
		return new Color(r, g, b);
	}

	public Color getPaintUnselectedBackColor() {
		if (fixed)
			return BACK_FIXED_COLOR;
		if (markedForRemoval)
			return MARKED_FOR_REMOVAL_COLOR;
		if (justMadeLocal)
			return JUST_MADE_LOCAL_COLOR;
		return BACK_DEFAULT_COLOR;
	}

	public Color getPaintTextColor(TGPanel tgPanel) {
		return TEXT_COLOR;
	}

	public Color getPaintBackColor(TGPanel tgPanel) {
//		if (this == tgPanel.getSelect()) {
//			return BACK_SELECT_COLOR;
//		} else {
//			return getPaintUnselectedBackColor();
//		}
	    if (componentType == COMPONENT_TYPE) {
	        return COMPONENT_COLOR;
	    } else if (componentType == AGENT_TYPE) {
	        return AGENT_COLOR;
	    } else if (componentType == NODE_TYPE) {
	        return NODE_COLOR;
	    } else {
	        return BACK_DEFAULT_COLOR;
	    }
	}

	public Color getPaintBorderColor(TGPanel tgPanel) {
		if (this == tgPanel.getSelect()) {
			return BORDER_SELECTED_COLOR;
		} else {
			return BORDER_UNSELECTED_COLOR;
		}
	}

	/**
	 * @return Returns the commandMap.
	 */
	public Map getCommandMap() {
		return commandMap;
	}

	/**
	 * @param commandMap
	 *          The commandMap to set.
	 */
	public void setCommandMap(Map commandMap) {
		this.commandMap = commandMap;
	}

	/**
	 * @return Returns the agentAddress.
	 */
	public MessageAddress getAgentAddress() {
		return agentAddress;
	}

	/**
	 * @param agentAddress
	 *          The agentAddress to set.
	 */
	public void setAgentAddress(MessageAddress agentAddress) {
		this.agentAddress = agentAddress;
	}
}