/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kent Sebastian <ksebasti@redhat.com> - initial API and implementation 
 *******************************************************************************/ 
package org.eclipse.linuxtools.oprofile.ui.model;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.eclipse.linuxtools.oprofile.core.model.OpModelSample;
import org.eclipse.linuxtools.oprofile.core.model.OpModelSymbol;
import org.eclipse.linuxtools.oprofile.ui.OprofileUiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * Children of images in the view -- a function name in the profiled 
 *  image's source code. May or may not have child samples.
 */
public class UiModelSymbol implements IUiModelElement {
	private IUiModelElement _parent;	//parent element
	private OpModelSymbol _symbol;		//the node in the data model
	private UiModelSample _samples[];	//this node's children
	private int _totalCount;			//total count of samples for the parent session
	
	public UiModelSymbol(IUiModelElement parent, OpModelSymbol symbol, int totalCount) {
		_parent = parent;
		_symbol = symbol;
		_samples = null;
		_totalCount = totalCount;
		refreshModel();
	}	
	
	private void refreshModel() {
		ArrayList<UiModelSample> sampleList = new ArrayList<UiModelSample>();
		OpModelSample dataModelSamples []= _symbol.getSamples();
		
		for (int i = 0; i < dataModelSamples.length; i++) {
			//dont display samples with line number of 0, meaning no line number
			// was correlated, more likely that no source file exists
			if (dataModelSamples[i].getLine() != 0) {
				sampleList.add(new UiModelSample(this, dataModelSamples[i], _totalCount));
			}
		}
		
		_samples = new UiModelSample[sampleList.size()];
		sampleList.toArray(_samples);
	}
	
	@Override
	public String toString() {
		NumberFormat nf = NumberFormat.getPercentInstance();
		if (nf instanceof DecimalFormat) {
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);
		}

		double countPercentage = (double)_symbol.getCount() / (double)_totalCount;
		
		String percentage;
		if (countPercentage < OprofileUiPlugin.MINIMUM_SAMPLE_PERCENTAGE) {
			percentage = "<" + nf.format(OprofileUiPlugin.MINIMUM_SAMPLE_PERCENTAGE);
		} else {
			percentage = nf.format(countPercentage);
		}
		
		//a hack to get `basename` type functionality
		String fileName = (new File(_symbol.getFile())).getName();
//		String fileName = _symbol.getFile();

		return percentage + " in " + _symbol.getName() + (fileName.length() == 0 ? "" : " [" + fileName + "]");
	}
	
	public String getFileName() {
		return _symbol.getFile();
	}

	/** IUiModelElement functions **/
	public String getLabelText() {
		return toString();
	}

	public IUiModelElement[] getChildren() {
		return _samples;
	}

	public boolean hasChildren() {
		return (_samples == null || _samples.length == 0 ? false : true);
	}

	public IUiModelElement getParent() {
		return _parent;
	}

	public Image getLabelImage() {
		return OprofileUiPlugin.getImageDescriptor(OprofileUiPlugin.SYMBOL_ICON).createImage();
	}
}