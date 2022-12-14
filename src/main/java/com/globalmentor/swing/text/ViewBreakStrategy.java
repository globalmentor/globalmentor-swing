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

import javax.swing.text.BoxView;
import javax.swing.text.View;

/**
 * Represents a strategy for breaking views.
 * @author Garret Wilson
 */
public interface ViewBreakStrategy {

	/**
	 * Breaks a view on the given axis at the given length.
	 * @param view The view to break.
	 * @param axis The axis to break along, either <code>View.X_AXIS</code> or <code>View.Y_AXIS</code>.
	 * @param offset the location in the model where the fragment should start its representation (&gt;=0).
	 * @param pos The position along the axis that the broken view would occupy (&gt;=0).
	 * @param length The distance along the axis where a potential break is desired (&gt;=0).
	 * @param fragmentViewFactory The source of fragment views.
	 * @return The fragment of the view that represents the given span, if the view can be broken. If the view doesn't support breaking behavior, the view itself
	 *         is returned.
	 * @see View#breakView
	 */
	public View breakView(final BoxView view, final int axis, final int offset, final float pos, final float length, final FragmentViewFactory fragmentViewFactory);

	/**
	 * Creates a view that represents a portion of the element. If the view doesn't support fragmenting, the view itself will be returned.
	 * @param view The view to break.
	 * @param p0 The starting offset (&gt;=0). This should be a value greater or equal to the element starting offset and less than the element ending offset.
	 * @param p1 The ending offset (&gt;p0). This should be a value less than or equal to the elements end offset and greater than the elements starting offset.
	 * @param fragmentViewFactory The source of fragment views.
	 * @return The view fragment, or the view itself if the view doesn't support breaking into fragments.
	 * @see View#createFragment
	 */
	public View createFragment(final BoxView view, int p0, int p1, final FragmentViewFactory fragmentViewFactory);
}