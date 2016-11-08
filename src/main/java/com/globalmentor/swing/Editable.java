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

package com.globalmentor.swing;

import com.globalmentor.java.*;

/**
 * Indicates the object can allow editing
 * @author Garret Wilson
 */
public interface Editable {

	/** The name of the editable property, if it is bound in any editable object. */
	public final String EDITABLE_PROPERTY = Editable.class.getName() + Java.PACKAGE_SEPARATOR + "editable";

	/** @return Whether the object can be edited. */
	public boolean isEditable();

	/**
	 * Sets whether the object can be edited.
	 * @param newEditable The new editable state.
	 */
	public void setEditable(final boolean newEditable);

}