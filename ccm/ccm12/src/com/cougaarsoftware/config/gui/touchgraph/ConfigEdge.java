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
package com.cougaarsoftware.config.gui.touchgraph;
import java.awt.Color;
import java.awt.Graphics;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPanel;
/**
 * An extension of a touch graph Edge that has a custom paint method
 * 
 * @author mabrams
 */
public class ConfigEdge extends com.touchgraph.graphlayout.Edge {
    
    public static Color MOUSE_OVER_COLOR = new Color(255, 72, 72);
    public static Color DEFAULT_COLOR = new Color(153,153,153);
    
	/**
	 * @param arg0
	 * @param arg1
	 */
	public ConfigEdge(Node f, Node t) {
		this(f, t, DEFAULT_LENGTH);
	}
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public ConfigEdge(Node f, Node t, int len) {
		super(f, t, len);
	}
	
	public void paint(Graphics g, TGPanel tgPanel) {
		Color c;
		if (tgPanel.getMouseOverN() == from || tgPanel.getMouseOverE() == this)
			c = MOUSE_OVER_COLOR;
		else
			c = col;
		int x1 = (int) from.drawx;
		int y1 = (int) from.drawy;
		int x2 = (int) to.drawx;
		int y2 = (int) to.drawy;
		if (intersects(tgPanel.getSize())) {
		    g.setColor(c);
			g.drawLine(x1, y1, x2, y2);
		}
	}
}