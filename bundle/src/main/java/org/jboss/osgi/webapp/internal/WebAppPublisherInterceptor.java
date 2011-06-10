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

import org.jboss.logging.Logger;
import org.jboss.osgi.deployment.interceptor.AbstractLifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.InvocationContext;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptorException;
import org.jboss.osgi.spi.util.BundleContextHelper;
import org.ops4j.pax.web.extender.war.internal.WebAppPublisherExt;
import org.ops4j.pax.web.extender.war.internal.model.WebApp;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * The WebApp lifecycle interceptor.
 *
 * @author thomas.diesler@jboss.com
 * @since 20-Oct-2009
 */
public class WebAppPublisherInterceptor extends AbstractLifecycleInterceptor implements LifecycleInterceptor
{
   // Provide logging
   private static final Logger log = Logger.getLogger(WebAppPublisherInterceptor.class);

   private WebAppPublisherExt publisher;

   public WebAppPublisherInterceptor(WebAppPublisherExt publisher)
   {
      this.publisher = publisher;

      // Add the required input
      addInput(WebApp.class);
   }

   public void invoke(int state, InvocationContext context) throws LifecycleInterceptorException
   {
      if (state == Bundle.STARTING)
      {
         log.debug("Publish WebApp metadata");

         // Gracefully wait 5000ms for the HttpService to become available
         BundleContextHelper bcHelper = new BundleContextHelper(context.getSystemContext());
         ServiceReference sref = bcHelper.getServiceReference(HttpService.class.getName(), 5000);
         if (sref == null)
            throw new IllegalStateException("HttpService not available");

         ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
         try {
             WebApp webApp = context.getAttachment(WebApp.class);
             publisher.publish(webApp);
         } finally {
             // [AS7-903] 3rd party code may leak TCCL
             Thread.currentThread().setContextClassLoader(ctxLoader);
         }
      }
      else if (state == Bundle.STOPPING)
      {
         log.debug("Unpublish WebApp metadata");
         ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
         try {
             WebApp webApp = context.getAttachment(WebApp.class);
             publisher.unpublish(webApp);
         } finally {
             // [AS7-903] 3rd party code may leak TCCL
             Thread.currentThread().setContextClassLoader(ctxLoader);
         }
      }
   }
}