/*******************************************************************************
 * Copyright (c) 2009 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Francois Chouinard - Initial API and implementation
 *******************************************************************************/

package org.eclipse.linuxtools.tmf.event;

/**
 * <b><u>TmfTimeRange</u></b>
 * <p>
 * A utility class to define time ranges.
 */
public class TmfTimeRange {

    // ========================================================================
    // Constants
    // ========================================================================

	public static TmfTimeRange Eternity = new TmfTimeRange(TmfTimestamp.BigBang, TmfTimestamp.BigCrunch);
	
    // ========================================================================
    // Attributes
    // ========================================================================

	private final TmfTimestamp fStartTime;
	private final TmfTimestamp fEndTime;

    // ========================================================================
    // Constructors
    // ========================================================================

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private TmfTimeRange() {
		fStartTime = null;
		fEndTime   = null;
	}

	/**
	 * @param startTime
	 * @param endTime
	 */
	public TmfTimeRange(TmfTimestamp startTime, TmfTimestamp endTime) {
		fStartTime = startTime;
		fEndTime   = endTime;
	}
	
	/**
	 * @param other
	 */
	public TmfTimeRange(TmfTimeRange other) {
		assert(other != null);
		fStartTime = other.fStartTime;
		fEndTime   = other.fEndTime;
	}

    @Override
    public boolean equals(Object other) {
    	if (other instanceof TmfTimeRange) {
    		TmfTimeRange range = (TmfTimeRange) other;
    		return range.fStartTime.equals(fStartTime) &&
    		       range.fEndTime.equals(fEndTime);
    	}
    	return false;
    }

    // ========================================================================
    // Accessors
    // ========================================================================

	/**
	 * @return The time range start time
	 */
	public TmfTimestamp getStartTime() {
		return fStartTime;
	}

	/**
	 * @return The time range end time
	 */
	public TmfTimestamp getEndTime() {
		return fEndTime;
	}

    // ========================================================================
    // Predicates
    // ========================================================================

	/**
	 * Check if the timestamp is within the time range
	 * 
	 * @param ts
	 * @return
	 */
	public boolean contains(TmfTimestamp ts) {
		boolean result = (fStartTime.compareTo(ts, true) <= 0) && (fEndTime.compareTo(ts, true) >= 0);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[TmfTimeRange(" + fStartTime.toString() + ":" + fEndTime.toString() + ")]";
	}

}