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

import java.io.Serializable;
import org.cougaar.core.util.UID;
import org.cougaar.core.util.UniqueObject;
import com.cougaarsoftware.config.util.HashCodeUtil;

/**
 * @author mabrams
 */
public class Capability implements Serializable, UniqueObject {
	private String verb;
	private String displayName;
	private String componentClass;	
	private UID uid;

	public Capability(String verb, String displayName, String componentClass, UID uid) {
		this.verb = verb;
		this.displayName = displayName;
		this.componentClass = componentClass;
		this.uid = uid;
	}

	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, getVerb());
		result = HashCodeUtil.hash(result, getComponentClass());
		result = HashCodeUtil.hash(result, getDisplayName());
		return result;
	}

	public boolean equals(Object o) {
		if (o instanceof Capability) {
			Capability c = (Capability) o;
			if (c.getVerb().equals(this.getVerb())
					&& c.getDisplayName().equals(this.getDisplayName())
					&& c.getComponentClass().equals(this.getComponentClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return Returns the componentClass.
	 */
	public String getComponentClass() {
		return componentClass;
	}

	/**
	 * @param componentClass
	 *          The componentClass to set.
	 */
	public void setComponentClass(String componentClass) {
		this.componentClass = componentClass;
	}

	/**
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *          The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return Returns the verb.
	 */
	public String getVerb() {
		return verb;
	}

	/**
	 * @param verb
	 *          The verb to set.
	 */
	public void setVerb(String verb) {
		this.verb = verb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.util.UniqueObject#getUID()
	 */
	public UID getUID() {
		return uid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.util.UniqueObject#setUID(org.cougaar.core.util.UID)
	 */
	public void setUID(UID uid) {
		if (uid != null) {
			throw new RuntimeException(
					"Attempt to set uid of an object that already had one");		
		}
		this.uid = uid;
	}
}