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
		super(name, label);
		
		this.agentAddress = agentAddress;
	}

	public ConfigNode(String name, String label, int type, MessageAddress agentAddress,
			Map commandMap) {
		this(name, label, type, agentAddress);
		this.componentType = type;
		this.commandMap = commandMap;		
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
			int tagY = iy - h / 2 - 3;
			String hiddenEdgeStr = String.valueOf(edgeCount() - visibleEdgeCount());
			g.setColor(Color.red);		
			g.fillRect(tagX, tagY, 3 + 5 * hiddenEdgeStr.length(), 8);
			g.setColor(Color.white);
			g.setFont(SMALL_TAG_FONT);
			g.drawString(hiddenEdgeStr, tagX + 2, tagY + 7);
		}
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
			return backColor.darker().darker();
		if (justMadeLocal)
			return myBrighter(backColor);
		return backColor;
	}

	public Color getPaintTextColor(TGPanel tgPanel) {
		if (this == tgPanel.getSelect()) {
			return getPaintUnselectedBackColor();
		} else {
			return textColor;
		}
	}

	public Color getPaintBackColor(TGPanel tgPanel) {
		if (this == tgPanel.getSelect()) {
			return BACK_SELECT_COLOR;
		} else {
			return getPaintUnselectedBackColor();
		}
	}

	public Color getPaintBorderColor(TGPanel tgPanel) {
		if (this == tgPanel.getSelect()) {
			if (fixed)
				return BACK_FIXED_COLOR;
			if (markedForRemoval)
				return new Color(100, 60, 40);
			if (justMadeLocal)
				return new Color(255, 220, 200);
			return backColor;
		} else {
			return super.getPaintBorderColor(tgPanel);
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