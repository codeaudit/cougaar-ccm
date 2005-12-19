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
package com.cougaarsoftware.config.servlet;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cougaar.core.blackboard.BlackboardClient;
import org.cougaar.core.blackboard.PrivilegedClaimant;
import org.cougaar.core.component.ServiceBroker;
import org.cougaar.core.node.NodeIdentificationService;
import org.cougaar.core.service.AgentContainmentService;
import org.cougaar.core.service.BlackboardService;
import org.cougaar.core.service.DomainService;
import org.cougaar.core.service.LoggingService;
import org.cougaar.core.service.UIDService;
import org.cougaar.core.servlet.BaseServletComponent;
/**
 * @author mabrams
 */
public class ConfigViewerServlet extends BaseServletComponent
		implements
			BlackboardClient,
			PrivilegedClaimant {
	/** Servlet implementation */
	protected ComponentServlet componentServlet = null;
	/** Cougaar NodeIdentificationService */
	protected NodeIdentificationService nodeIdService;
	/** Cougaar AgentContainmentService */
	protected AgentContainmentService agentContainmentService;
	/** Cougaar ServiceBroker */
	protected ServiceBroker serviceBroker;
	/** Cougaar BlackboardService */
	protected BlackboardService blackboardService;
	/** Cougaar DomainService */
	protected DomainService domainService;
	/** Cougaar UIDService */
	protected UIDService uidService;
	/** Cougaar Logging Service */
	protected LoggingService logging;
	private Object parameter = null;
	/** action param */
	public static final String ACTION = "action";
	/**
	 * Component load method, initializes Cougaar services
	 */
	public void load() {
		this.serviceBroker = this.bindingSite.getServiceBroker();
		this.domainService = (DomainService) serviceBroker.getService(this,
				DomainService.class, null);
		this.blackboardService = (BlackboardService) serviceBroker.getService(this,
				BlackboardService.class, null);
		this.agentContainmentService = (AgentContainmentService) serviceBroker
				.getService(this, AgentContainmentService.class, null);
		this.uidService = (UIDService) serviceBroker.getService(this,
				UIDService.class, null);
		this.nodeIdService = (NodeIdentificationService) serviceBroker.getService(
				this, NodeIdentificationService.class, null);
		this.logging = (LoggingService) serviceBroker.getService(this,
				LoggingService.class, null);
		super.load();
	}
	/**
	 * Get current time for Blackboard
	 * 
	 * @return current time from the System
	 */
	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}
	/**
	 * Client name for blackboard client
	 * 
	 * @return This class name
	 */
	public String getBlackboardClientName() {
		return this.getClass().getName();
	}
	/**
	 * Required method for BlackboardClient
	 * 
	 * @param o
	 *          Object
	 * 
	 * @return whether to trigger event, this is always false
	 */
	public boolean triggerEvent(Object o) {
		return false;
	}
	/**
	 * Called just after construction (via introspection) by the loader if a
	 * non-null parameter Object was specified by the ComponentDescription.
	 * 
	 * @param param
	 *          DOCUMENT ME!
	 */
	public void setParameter(Object param) {
		parameter = param;
	}
	/**
	 * Get any Component parameters passed by the instantiator.
	 * 
	 * @return The parameter specified if it was a collection, a collection with
	 *         one element (the parameter) if it wasn't a collection, or an empty
	 *         collection if the parameter wasn't specified.
	 */
	public Collection getParameters() {
		if (parameter == null) {
			return new ArrayList(0);
		} else {
			if (parameter instanceof Collection) {
				return (Collection) parameter;
			} else {
				List l = new ArrayList(1);
				l.add(parameter);
				return l;
			}
		}
	}
	/**
	 * Get the path to this servlet
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getPath() {
		return "/configuration";
	}
	/**
	 * Create HTTP Servlet
	 * 
	 * @return Servlet instance
	 */
	protected Servlet createServlet() {
		componentServlet = new ComponentServlet();
		return (Servlet) componentServlet;
	}
	private void appendHeader(StringBuffer buffer) {
		buffer.append("<HTML><HEAD><TITLE>Configuration ");
		buffer.append("Viewer Servlet</TITLE><SCRIPT SRC=");
		buffer.append("\"/$SampleAgent/file?name=mktree.js\"");
		buffer.append(" LANGUAGE=\"JavaScript\"></SCRIPT>");
		buffer.append("<STYLE TYPE=\"text/css\">");
		buffer.append("@media screen {\n");
		buffer.append("ul.mktree  li { list-style: none; }\n");
		buffer
				.append("ul.mktree, ul.mktree ul , ul.mktree li { margin-left:10px; }\n");
		buffer.append("ul.mktree  li           .bullet { padding-left: 15px; }\n");
		buffer
				.append("ul.mktree  li.liOpen    .bullet { cursor: pointer; background: url(/$SampleAgent/file?name=minus.gif)  center left no-repeat; }\n");
		buffer
				.append("ul.mktree  li.liClosed  .bullet { cursor: pointer; background: url(/$SampleAgent/file?name=plus.gif)   center left no-repeat; }\n");
		buffer
				.append("ul.mktree  li.liBullet  .bullet { cursor: default; background: url(/$SampleAgent/file?name=bullet.gif) center left no-repeat; }\n");
		buffer.append("ul.mktree  li.liOpen    ul { display: block; }\n");
		buffer.append("ul.mktree  li.liClosed  ul { display: none; }\n");
		buffer.append("ul.mktree  li { font-size: 12pt; }\n");
		buffer.append("ul.mktree  li ul li { font-size: 10pt; }\n");
		buffer.append("ul.mktree  li ul li ul li { font-size: 8pt; }\n");
		buffer.append("ul.mktree  ul li ul li ul li li { font-size: 6pt; }\n");
		buffer.append(")}\n");
		buffer.append("</STYLE>");
		buffer.append("</HEAD><BODY>");
	}
	private void appendFooter(StringBuffer buffer) {
		buffer.append("</BODY></HTML>");
	}
	private void appendBody(StringBuffer buffer) {
		buffer.append("<UL class=\"mktree\">");
		buffer.append("<LI>test");
		buffer.append("<UL><LI>test2</LI></UL>");
		buffer.append("</LI>");
		buffer.append("<LI>test</LI>");
		buffer.append("<LI>test</LI>");
		buffer.append("<LI>test</LI>");
		buffer.append("</UL>");
	}
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		String action = request.getParameter(ACTION);
		String objecttype = request.getParameter("objecttype");
		StringBuffer buffer = new StringBuffer();
		appendHeader(buffer);
		appendBody(buffer);
		appendFooter(buffer);
		try {
			PrintWriter out = response.getWriter();
			out.write(buffer.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			if (logging.isErrorEnabled()) {
				logging.error("Error writing to out buffer", e);
			}
		}
	}
	/**
	 * Out HTTPServlet, just hands off processing of incoming request to the
	 * abstract execute() method
	 * 
	 * @author ttschampel
	 */
	private class ComponentServlet extends HttpServlet {
		/**
		 * Handle HTTP GET Request
		 * 
		 * @param request
		 * @param response
		 */
		public void doGet(HttpServletRequest request, HttpServletResponse response) {
			execute(request, response);
		}
		/**
		 * Handler HTTP POST Request
		 * 
		 * @param request
		 * @param response
		 */
		public void doPost(HttpServletRequest request, HttpServletResponse response) {
			execute(request, response);
		}
	}
	private void displayConfiguration(StringBuffer buffer) {
	}
}