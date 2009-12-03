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

import org.jboss.osgi.deployment.interceptor.AbstractLifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.InvocationContext;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptor;
import org.jboss.osgi.deployment.interceptor.LifecycleInterceptorException;
import org.ops4j.pax.web.extender.war.internal.WebAppPublisherExt;
import org.ops4j.pax.web.extender.war.internal.model.WebApp;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WebApp lifecycle interceptor.
 * 
 * @author thomas.diesler@jboss.com
 * @since 20-Oct-2009
 */
public class WebAppPublisherInterceptor extends AbstractLifecycleInterceptor implements LifecycleInterceptor
{
   // Provide logging
   private Logger log = LoggerFactory.getLogger(WebAppPublisherInterceptor.class);
   
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
         WebApp webApp = context.getAttachment(WebApp.class);
         publisher.publish(webApp);
      }
      else if (state == Bundle.STOPPING)
      {
         log.debug("Unpublish WebApp metadata");
         WebApp webApp = context.getAttachment(WebApp.class);
         publisher.unpublish(webApp);
      }
   }
}