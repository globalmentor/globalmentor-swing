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

package com.globalmentor.swing.event;

import java.util.EventListener;

/**Represents an object which monitors the progress of a particular action by
	listening for {@link ProgressEvent}
@see ProgressEvent
@author Garret Wilson
*/
public interface ProgressListener extends EventListener
{
	/**Invoked when progress has been made.
	@param e The event object representing the progress made.
	*/
	void madeProgress(ProgressEvent e);
}
