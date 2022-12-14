/*
 * Copyright © 1996-2009 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.swing.text;

import javax.swing.text.View;

/**
 * Represents information about a view that is a fragment of another view.
 * @author Garret Wilson
 */
public interface FragmentView {

	/** @return The original, unfragmented view from which this fragment (or one or more intermediate fragments) was broken. */
	public View getWholeView();

	/**
	 * Returns the whether this is the first fragment in relation to the original view. A paragraph, for example, might be fragmented into several views, but only
	 * the first fragment should be indented.
	 * @return The index of this fragment in relation to the original unbroken view.
	 */
	//TODO del	public int getFragmentIndex();

	/**
	 * Returns whether this is the first fragment in relation to the original view. A paragraph, for example, might be fragmented into several views, but only the
	 * first fragment should be indented.
	 * @return <code>true</code> if this is the first fragment of the original view.
	 */
	public boolean isFirstFragment();

	/**
	 * Returns whether this is the last fragment in relation to the original view.
	 * @return <code>true</code> if this is the last fragment of the original view.
	 */
	public boolean isLastFragment();

	/**
	 * Establishes the parent view for this view.
	 * @param parent the new parent, or <code>null</code> if the view is being removed from a parent.
	 */
	public void setParent(final View parent);

	/** @return The number of child views (&gt;=0). */
	public int getViewCount();

	/**
	 * Retrieves the child view at the given index.
	 * @param index The index of the view to get (0&lt;=<var>index</var>&lt;<code>getViewCount()</code>)
	 * @return The child view at the given index.
	 */
	public View getView(final int index);

	/** Removes all of the children. This is a convenience call to <code>replace()</code>. */
	public void removeAll();

	/**
	 * Removes one of the children at the given position. This is a convenience call to <code>replace()</code>.
	 * @param index The index of the child view to remove.
	 */
	public void remove(final int index);

	/**
	 * Inserts a single child view. This is a convenience call to <code>replace()</code>.
	 * @param offset The offset of the view to insert before (&gt;=0).
	 * @param view The view to insert.
	 */
	public void insert(final int offset, final View view);

	/**
	 * Appends a single child view. This is a convenience call to <code>replace()</code>.
	 * @param view The view to append.
	 */
	public void append(final View view);

	/**
	 * Replaces child views. If there are no views to remove this acts as an insert. If there are no views to add this acts as a remove.
	 * @param offset The starting index into the child views to insert the new views (0&lt;=<var>offset</var>&lt;<code>getViewCount()</code>).
	 * @param length The number of existing child views to remove (0&lt;=<var>length</var>&lt;=<code>getViewCount()</code>-<var>offset</var>).
	 * @param views The child views to add, or <code>null</code> if no children are being added.
	 */
	public void replace(final int offset, final int length, final View[] views);

}