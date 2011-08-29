/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elliott Baron <ebaron@redhat.com> - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.linuxtools.internal.valgrind.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.cdt.utils.pty.PTY;
import org.eclipse.cdt.utils.spawner.ProcessFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import org.eclipse.debug.core.ILaunchConfiguration;

import org.eclipse.linuxtools.valgrind.core.IValgrindLocation;

public class ValgrindCommand {
	protected static final String WHICH_CMD = "which"; //$NON-NLS-1$
	protected static final String VALGRIND_CMD = "valgrind"; //$NON-NLS-1$
	protected static final String EXTENSION_POINT_PRIORITY = "priority"; //$NON-NLS-1$
	protected static final String EXTENSION_POINT_CLASS = "class"; //$NON-NLS-1$
	protected static final String EXTENSION_POINT_ID = "valgrindLocation"; //$NON-NLS-1$

	protected Process process;
	protected String[] args;
	protected ILaunchConfiguration config;

	public ValgrindCommand (ILaunchConfiguration config) {
		this.config = config;
	}

	public ValgrindCommand () {
		this(null);
	}

	private IConfigurationElement getHighestPriority(IConfigurationElement[] elements) {
		if (elements.length == 0)
			return null;

		int priority = Integer.parseInt(elements[0].getAttribute(EXTENSION_POINT_PRIORITY));
		IConfigurationElement highest = elements[0];
		for (int i = 1; i < elements.length; i++) {
			int priority2 = Integer.parseInt(elements[i].getAttribute(EXTENSION_POINT_PRIORITY));
			if (priority2 > priority) {
				priority = priority2;
				highest = elements[i];
			}
		}
		return highest;
	}

	private String getExtensionPointLocation() {
		IExtensionPoint ep = Platform.getExtensionRegistry().
					getExtensionPoint(PluginConstants.CORE_PLUGIN_ID, EXTENSION_POINT_ID);
		if (ep == null)
			return null;

		IConfigurationElement element = getHighestPriority(ep.getConfigurationElements());
		if (element == null)
			return null;

		try {
			return ((IValgrindLocation)element.createExecutableExtension(EXTENSION_POINT_CLASS)).getValgrindLocation(config);
		} catch (CoreException e) {
			return null;
		}
	}

	public String whichValgrind() throws IOException {
		// Valgrind binary location set in extension point overrides every other definition
		String valgrindExtensionPointLocation = getExtensionPointLocation();
		if (valgrindExtensionPointLocation != null)
			return valgrindExtensionPointLocation;

		// Valgrind binary location in preferences overrides default location
		String valgrindPreferedPath = ValgrindPlugin.getDefault().getPreferenceStore().getString(ValgrindPreferencePage.VALGRIND_PATH);
		if (!valgrindPreferedPath.equals(""))
			return valgrindPreferedPath;

		// No preference, check Valgrind exists in the user's PATH
		StringBuffer out = new StringBuffer();
		Process p = Runtime.getRuntime().exec(WHICH_CMD + " " + VALGRIND_CMD); //$NON-NLS-1$
		// Throws IOException if which command is unsuccessful
		readIntoBuffer(out, p);
		return out.toString().trim();
	}
	
	/**
	 * Returns whether Valgrind integration is enabled
	 * @since 0.8
	 */
	public boolean isEnabled() {
		// Check preference page for Valgrind enablement
		boolean enabled = ValgrindPlugin.getDefault().getPreferenceStore().getBoolean(ValgrindPreferencePage.VALGRIND_ENABLE);
		return enabled;
	}

	public String whichVersion(String whichValgrind) throws IOException {
		StringBuffer out = new StringBuffer();
		Process p = Runtime.getRuntime().exec(new String[] { whichValgrind, CommandLineConstants.OPT_VERSION });
		readIntoBuffer(out, p);
		return out.toString().trim();
	}
	
	public void execute(String[] commandArray, Object env, File wd, String exeFile, boolean usePty) throws IOException {
		args = commandArray;
		try {
			process = startProcess(commandArray, env, wd, exeFile, usePty);
		}
		catch (IOException e) {
			if (process != null) {
				process.destroy();
			}
			throw e;		
		}
	}

	public Process getProcess() {
		return process;
	}

	public String getCommandLine() {
		StringBuffer ret = new StringBuffer();
		for (String arg : args) {
			ret.append(arg + " "); //$NON-NLS-1$
		}
		return ret.toString().trim();
	}
	
	protected Process startProcess(String[] commandArray, Object env, File workDir, String binPath, boolean usePty) throws IOException {
		if (workDir == null) {
			return ProcessFactory.getFactory().exec(commandArray, (String[]) env);
		}
		if (PTY.isSupported() && usePty) {
			return ProcessFactory.getFactory().exec(commandArray, (String[]) env, workDir, new PTY());
		}
		else {
			return ProcessFactory.getFactory().exec(commandArray, (String[]) env, workDir);
		}
	}

	protected void readIntoBuffer(StringBuffer out, Process p) throws IOException {
		boolean success;
		InputStream in;
		try {
			if (success = (p.waitFor() == 0)) {
				in = p.getInputStream();
			}
			else {
				in = p.getErrorStream();
			}
			int ch;
			while ((ch = in.read()) != -1) {
				out.append((char) ch);
			}
			if (!success) {
				throw new IOException(out.toString());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
