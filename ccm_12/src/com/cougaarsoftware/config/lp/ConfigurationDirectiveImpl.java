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
package com.cougaarsoftware.config.lp;
import org.cougaar.core.blackboard.DirectiveImpl;
import org.cougaar.core.util.UID;
/**
 * Implementation of the <code>ConfigurationDirective</code> interface.
 * 
 * @see com.cougaarsoftware.core.configuration.directives.ConfigurationDirective
 * 
 * @author mabrams
 */
public class ConfigurationDirectiveImpl extends DirectiveImpl
		implements
			ConfigurationDirective,
			NewConfigurationDirective {
	private String fType = null;
	private boolean fIsSent = false;
	private UID fUid = null;
	private Object payload;
	public ConfigurationDirectiveImpl(UID uid) {
		setUID(uid);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.NewConfigurationChangeDirective#setType(java.lang.String)
	 */
	public void setType(String type) {
		fType = type;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.ConfigurationChangeDirective#getType()
	 */
	public String getType() {
		return fType;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.ConfigurationChangeDirective#isSent()
	 */
	public boolean isSent() {
		return fIsSent;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cougaarsoftware.config.ConfigurationChangeDirective#markSent()
	 */
	public void markSent() {
		fIsSent = true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.util.UniqueObject#getUID()
	 */
	public UID getUID() {
		return fUid;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cougaar.core.util.UniqueObject#setUID(org.cougaar.core.util.UID)
	 */
	public void setUID(UID uid) {
		if (fUid != null) {
			throw new IllegalArgumentException("UID already set");
		}
		fUid = uid;
	}
	public void setPayload(Object payload) {
		this.payload = payload;
	}
	public Object getPayload() {
		return this.payload;
	}
}