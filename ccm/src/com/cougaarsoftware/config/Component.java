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
import org.cougaar.core.util.UniqueObject;
/**
 * A simple component in the configuration
 * 
 * @author mabrams
 */
public interface Component extends Serializable, UniqueObject {
	public final static int HEALTHY = 0;
	public final static int UNKNOWN = 1;
	public final static int DEAD = 2;
	/**
	 * get the name of the component
	 * 
	 * @return the name of the component
	 */
	public String getName();
	/**
	 * set the name of the component
	 * 
	 * @param name
	 */
	public void setName(String name);
	/**
	 * set the class name for the component
	 * 
	 * @param className
	 */
	public void setClassName(String className);
	/**
	 * get the class name for the component
	 * 
	 * @return
	 */
	public String getClassName();
	/**
	 * set the priority
	 * 
	 * @param priority
	 */
	public void setPriority(String priority);
	/**
	 * get the priority
	 * 
	 * @return
	 */
	public String getPriority();
	/**
	 * set the insertion point
	 * 
	 * @param insertionPoint
	 */
	public void setInsertionpoint(String insertionPoint);
	/**
	 * get the insertion point
	 * 
	 * @return
	 */
	public String getInsertionpoint();
	public int getStatus();
	public void setStatus(int status);
}