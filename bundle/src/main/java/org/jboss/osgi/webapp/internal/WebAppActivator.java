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

import org.jboss.osgi.deployment.interceptor.LifecycleInterceptor;
import org.jboss.osgi.webapp.WebAppService;
import org.ops4j.pax.web.extender.war.internal.WebAppPublisherExt;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The WebApp support activator
 * 
 * @author thomas.diesler@jboss.com
 * @since 20-Oct-2009
 */
public class WebAppActivator implements BundleActivator
{
   public void start(BundleContext context)
   {
      // Register the marker service
      WebAppService service = new WebAppService() {};
      context.registerService(WebAppService.class.getName(), service, null);

      // Register the web.xml parser interceptor
      LifecycleInterceptor webappParser = new WebXMLParserInterceptor();
      context.registerService(LifecycleInterceptor.class.getName(), webappParser, null);

      // Register the WebApp publisher interceptor
      WebAppPublisherExt publisher = new WebAppPublisherExt();
      LifecycleInterceptor webappPublisher = new WebAppPublisherInterceptor(publisher);
      context.registerService(LifecycleInterceptor.class.getName(), webappPublisher, null);
   }

   public void stop(BundleContext context)
   {
   }
}