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
package org.ops4j.pax.web.extender.war.internal;

import org.jboss.osgi.spi.util.BundleContextHelper;
import org.ops4j.pax.web.extender.war.internal.model.WebApp;
import org.ops4j.pax.web.service.WebContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

//$Id$

/**
 * Public visibility for the package protected WebAppPublisher 
 * 
 * http://issues.ops4j.org/browse/PAXWEB-183
 * 
 * @author thomas.diesler@jboss.com
 * @since 20-Oct-2009
 */
public class WebAppPublisherExt extends WebAppPublisher
{
   public void publish(BundleContext context, WebApp webapp)
   {
      // Gracefully wait 5000ms for the WebContainer to become available
      ServiceReference sref = new BundleContextHelper(context).getServiceReference(WebContainer.class.getName(), 5000);
      if (sref == null)
         throw new IllegalStateException("WebContainer not available");

      // Publish the WebApp metadata
      super.publish(webapp);
   }

}