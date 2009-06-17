/*******************************************************************************
 * Copyright (c) 2000, 2006, 2007 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Red Hat Inc. - convert to use with Automake editor
 *******************************************************************************/
package org.eclipse.linuxtools.cdt.autotools.internal.editors.automake;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.make.core.IMakeTarget;
import org.eclipse.cdt.make.core.IMakeTargetManager;
import org.eclipse.cdt.make.core.MakeCorePlugin;
import org.eclipse.cdt.make.core.makefile.ITargetRule;
import org.eclipse.cdt.make.ui.dialogs.MakeTargetDialog;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.linuxtools.cdt.autotools.AutotoolsPlugin;
import org.eclipse.linuxtools.cdt.autotools.internal.ui.MakeUIMessages;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;


/**
 */
public class AddBuildTargetAction extends Action {

	MakefileContentOutlinePage fOutliner;
	static final ITargetRule[] EMPTY_TARGET_RULES = {
	};

	public AddBuildTargetAction(MakefileContentOutlinePage outliner) {
		super(MakeUIMessages.getResourceString("AddBuildTargetAction.title")); //$NON-NLS-1$
		setDescription(MakeUIMessages.getResourceString("AddBuildTargetAction.description")); //$NON-NLS-1$
		setToolTipText(MakeUIMessages.getResourceString("AddBuildTargetAction.tooltip")); //$NON-NLS-1$
		fOutliner = outliner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		IMakeTargetManager manager = MakeCorePlugin.getDefault().getTargetManager();
		IFile file = getFile();
		Shell shell = fOutliner.getControl().getShell();
		ITargetRule[] rules = getTargetRules(fOutliner.getSelection());
		if (file != null && rules.length > 0 && shell != null) {
			StringBuffer sbBuildName = new StringBuffer();
			StringBuffer sbMakefileTarget = new StringBuffer();
			for (int i = 0; i < rules.length; i++) {
				String name = rules[i].getTarget().toString().trim();
				if (sbBuildName.length() == 0) {
					sbBuildName.append(name);
				} else {
					sbBuildName.append('_').append(name);
				}
				if (sbMakefileTarget.length() == 0) {
					sbMakefileTarget.append(name);
				} else {
					sbMakefileTarget.append(' ').append(name);
				}
			}
			String buildName = generateUniqueName(file.getParent(), sbBuildName.toString());
			String makefileTarget = sbMakefileTarget.toString();
			IMakeTarget target = null;
			try {
				String[] ids = manager.getTargetBuilders(file.getProject());
				if (ids.length > 0) {
					target = manager.createTarget(file.getProject(), buildName, ids[0]);
					target.setBuildAttribute(IMakeTarget.BUILD_TARGET, makefileTarget);
					target.setContainer(file.getParent());
				}
			} catch (CoreException e) {
				AutotoolsPlugin.errorDialog(shell, MakeUIMessages.getResourceString("AddBuildTargetAction.exception.internal"), e.toString(), e); //$NON-NLS-1$
				target = null;
			}

			// Always popup the dialog.
			if (target != null) {
				MakeTargetDialog dialog;
				try {
					dialog = new MakeTargetDialog(shell, target);
					dialog.open();
				} catch (CoreException e) {
					AutotoolsPlugin.errorDialog(shell, MakeUIMessages.getResourceString("AddBuildTargetAction.exception.internal"), e.toString(), e); //$NON-NLS-1$
				}
			}
		}
	}

	private String generateUniqueName(IContainer container, String targetString) {
		String newName = targetString;
		int i = 0;
		IMakeTargetManager manager = MakeCorePlugin.getDefault().getTargetManager();
		try {
			while (manager.findTarget(container, newName) != null) {
				i++;
				newName = targetString + " (" + Integer.toString(i) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (CoreException e) {
		}
		return newName;
	}
	
	public boolean canActionBeAdded(ISelection selection) {
		ITargetRule[] rules = getTargetRules(selection);
		for (int i = 0; i < rules.length; i++) {
			IFile file = getFile();
			if (file == null)
				return false;
			if (!MakeCorePlugin.getDefault().getTargetManager().hasTargetBuilder(file.getProject()))
				return false;
		}
		return true;
	}

	private IFile getFile() {
		Object input = fOutliner.getInput();
		if (input instanceof IFileEditorInput) {
			return ((IFileEditorInput)input).getFile();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ITargetRule[] getTargetRules(ISelection sel) {
		if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
			List<Object> list = ((IStructuredSelection)sel).toList();
			if (list.size() > 0) {
				List<Object> targets = new ArrayList<Object>(list.size());
				Object[] elements = list.toArray();
				for (int i = 0; i < elements.length; i++) {
					if (elements[i] instanceof ITargetRule) {
						targets.add(elements[i]);
					}
				}
				return (ITargetRule[])targets.toArray(EMPTY_TARGET_RULES);
			}
		}
		return EMPTY_TARGET_RULES;
	}

}