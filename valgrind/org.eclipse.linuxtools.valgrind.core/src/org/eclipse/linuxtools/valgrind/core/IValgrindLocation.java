/*******************************************************************************
 * Copyright (c) 2011 IBM, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Otavio Pontes <obusatto@br.ibm.com> - initial API
 *******************************************************************************/
package org.eclipse.linuxtools.valgrind.core;

import org.eclipse.debug.core.ILaunchConfiguration;

public interface IValgrindLocation {

	public abstract String getValgrindLocation(ILaunchConfiguration configuration);
}
