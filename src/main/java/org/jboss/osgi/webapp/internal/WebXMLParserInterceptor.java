/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.osgi.webapp.internal;

//$Id$

import java.io.IOException;

import org.jboss.logging.Logger;
import org.jboss.osgi.deployment.interceptor.AbstractLifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.InvocationContext;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptorException;
import org.jboss.virtual.VirtualFile;
import org.ops4j.pax.web.extender.war.internal.model.WebApp;
import org.ops4j.pax.web.extender.war.internal.parser.dom.DOMWebXmlParser;
import org.osgi.framework.Bundle;

/**
 * The WebApp lifecycle interceptor.
 * 
 * @author thomas.diesler@jboss.com
 * @since 20-Oct-2009
 */
public class WebXMLParserInterceptor extends AbstractLifecycleInterceptor implements LifecycleInterceptor
{
   // Provide logging
   private static final Logger log = Logger.getLogger(WebXMLParserInterceptor.class);
   
   public WebXMLParserInterceptor()
   {
      // Advertise output
      addOutput(WebApp.class);
   }

   public void invoke(int state, InvocationContext context) throws LifecycleInterceptorException
   {
      WebApp webApp = context.getAttachment(WebApp.class);
      if (webApp == null && state == Bundle.STARTING)
      {
         try
         {
            VirtualFile root = context.getRoot();
            VirtualFile webXML = root.getChild("/WEB-INF/web.xml");
            if (webXML != null)
            {
               log.debug("Create and attach WebApp metadata");
               webApp = createWebAppMetadata(context, webXML);
               context.addAttachment(WebApp.class, webApp);
            }
         }
         catch (IOException ex)
         {
            throw new LifecycleInterceptorException("Cannot parse web.xml", ex);
         }
      }
   }

   private WebApp createWebAppMetadata(InvocationContext context, VirtualFile webXML) throws IOException
   {
      // Parse the web.xml
      DOMWebXmlParser parser = new DOMWebXmlParser();
      WebApp webApp = parser.parse(webXML.openStream());

      // Associate the Bundle with the WebApp metadata 
      Bundle bundle = context.getBundle();
      webApp.setBundle(bundle);

      // Set the context name as first looking for a manifest entry named Webapp-Context
      // if not set use bundle symbolic name
      String contextName = (String)bundle.getHeaders().get("Webapp-Context");
      if (contextName == null)
      {
         contextName = bundle.getSymbolicName();
      }
      if ("/".equals(contextName.trim()))
      {
         contextName = "";
      }
      webApp.setContextName(contextName);
      
      return webApp;
   }
}