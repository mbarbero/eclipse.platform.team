/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.ui.Policy;
import org.eclipse.team.internal.ccvs.ui.operations.AddOperation;

/**
 * AddAction performs a 'cvs add' command on the selected resources. If a
 * container is selected, its children are recursively added.
 */
public class AddAction extends WorkspaceAction {
	
	/* (non-Javadoc)
	 * @see org.eclipse.team.internal.ccvs.ui.actions.CVSAction#execute(org.eclipse.jface.action.IAction)
	 */
	public void execute(IAction action) throws InterruptedException, InvocationTargetException {
		if (!promptForAddOfIgnored()) return;
		new AddOperation(getTargetPart(), getSelectedResources()).run();
	}
	
	/**
	 * Method promptForAddOfIgnored.
	 */
	private boolean promptForAddOfIgnored() {
		IResource[] resources = getSelectedResources();
		boolean prompt = false;
		for (int i = 0; i < resources.length; i++) {
			ICVSResource resource = CVSWorkspaceRoot.getCVSResourceFor(resources[i]);
			try {
				if (resource.isIgnored()) {
					prompt = true;
					break;
				} 
			} catch (CVSException e) {
				handle(e);
			}
		}
		if (prompt) {
			return MessageDialog.openQuestion(getShell(), Policy.bind("AddAction.addIgnoredTitle"), Policy.bind("AddAction.addIgnoredQuestion")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return true;
	}

	/**
	 * @see org.eclipse.team.internal.ccvs.ui.actions.WorkspaceAction#isEnabledForManagedResources()
	 */
	protected boolean isEnabledForManagedResources() {
		return false;
	}

	/**
	 * @see org.eclipse.team.internal.ccvs.ui.actions.WorkspaceAction#isEnabledForUnmanagedResources()
	 */
	protected boolean isEnabledForUnmanagedResources() {
		return true;
	}

	/**
	 * @see org.eclipse.team.internal.ccvs.ui.actions.WorkspaceAction#isEnabledForIgnoredResources()
	 */
	protected boolean isEnabledForIgnoredResources() {
		return true;
	}

	/**
	 * @see org.eclipse.team.internal.ccvs.ui.actions.WorkspaceAction#isEnabledForCVSResource(org.eclipse.team.internal.ccvs.core.ICVSResource)
	 */
	protected boolean isEnabledForCVSResource(ICVSResource cvsResource) throws CVSException {
		// Add to version control should never be enabled for linked resources
		IResource resource = cvsResource.getIResource();
		if (resource.isLinked()) return false;
		return super.isEnabledForCVSResource(cvsResource);
	}

}
