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
import org.cougaar.core.util.UniqueObject;
/**
 * class to wrap society configurations sent from one config manager to another
 * 
 * @author mabrams
 */
public class SocietyWrapper implements UniqueObject {
	private UID uid;
	private Society society;
	/**
	 * @param localSociety
	 * @param uid2
	 */
	public SocietyWrapper(Society society, UID uid) {
		this.society = society;
		this.uid = uid;
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
	/**
	 * @return Returns the society.
	 */
	public Society getSociety() {
		return society;
	}
	/**
	 * @param society
	 *          The society to set.
	 */
	public void setSociety(Society society) {
		this.society = society;
	}
}