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
package com.cougaarsoftware.config;
import org.cougaar.core.util.UID;
/**
 * @author mabrams
 */
public abstract class ComponentImpl implements Component {
	private String name;
	private String className;
	private UID uid;
	/**
	 * the current status of the component, healthy, unknown, dead
	 */
	private int status;
	private String priority;
	private String insertionPoint;
	public ComponentImpl() {
	}
	public ComponentImpl(String name, UID uid) {
		this.name = name;
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UID getUID() {
		return uid;
	}
	public void setUID(UID uid) {
		if (uid != null) {
			throw new IllegalArgumentException("UID already set");
		}
		this.uid = uid;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Component#setClassName(java.lang.String)
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Component#getClassName()
	 */
	public String getClassName() {
		return className;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Component#setPriority(java.lang.String)
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Component#getPriority()
	 */
	public String getPriority() {
		return priority;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Component#setInsertionpoint(java.lang.String)
	 */
	public void setInsertionpoint(String insertionPoint) {
		this.insertionPoint = insertionPoint;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.Component#getInsertionpoint()
	 */
	public String getInsertionpoint() {
		return insertionPoint;
	}
	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status
	 *          The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}