/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Kurtakov - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.rpm.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Initialize preferences.
 *
 */
public class RPMCoreInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = RPMCorePlugin.getDefault()
				.getPreferenceStore();
		String user_name = System.getProperty("user.name"); //$NON-NLS-1$
		store.setDefault(IRPMConstants.RPM_DISPLAYED_LOG_NAME, ".logfilename_" //$NON-NLS-1$
				+ user_name);
		store.setDefault(IRPMConstants.RPM_LOG_NAME, "rpmbuild.log"); //$NON-NLS-1$

		store.setDefault(IRPMConstants.RPM_CMD, "/bin/rpm"); //$NON-NLS-1$
		store.setDefault(IRPMConstants.RPMBUILD_CMD, "/usr/bin/rpmbuild"); //$NON-NLS-1$
		store.setDefault(IRPMConstants.DIFF_CMD, "/usr/bin/diff"); //$NON-NLS-1$
	}

}
