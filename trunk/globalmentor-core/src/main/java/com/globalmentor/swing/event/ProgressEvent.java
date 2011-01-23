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

import java.util.EventObject;

/**Used to notify interested parties that progress has been made for a
	particular action.
@see ProgressListener
@author Garret Wilson
*/
public class ProgressEvent extends EventObject
{

	/**The task being performed, or an empty string if unknown.*/
	private String task="";

		/**@return The task being performed, or an empty string if unkown.*/
		public String getTask() {return task;}

		/**Sets the task being performed.
		@param newTask The task being performed.
		*/
		protected void setTask(final String newTask) {task=newTask;}

	/**The status of the progress.*/
	private String status="";

		/**@return The status of the progress made.*/
		public String getStatus() {return status;}

		/**Sets the status of the progress.
		@param newStatus The new status message.
		*/
		protected void setStatus(final String newStatus) {status=newStatus;}

	/**The current progress in relation to the maximum, or <code>-1</code> if not
		known.
	*/
	private float value=-1;

		/**@return The current progress, in relation to the goal, or <code>-1</code>
		  if not known.
		@see #getMaximum
		*/
		public float getValue() {return value;}

		/**Sets the current progress, in relation to the goal.
		@param newValue The progress in relation to the goal, or <code>-1</code>
		  if not known.
		@see #setValue
		*/
		protected void setValue(final float newValue) {value=newValue;}

	/**The goal, or <code>-1</code> if not known.*/
	private float maximum=-1;

		/**@return The goal, or <code>-1</code> if not known.
		@see #getValue
		*/
		public float getMaximum() {return maximum;}

		/**Sets the goal.
		@param newMaximum The goal, or <code>-1</code> if not known.
		@see #setValue
		*/
		protected void setMaximum(final float newMaximum) {maximum=newMaximum;}

	/**Returns whether the progress has reached its goal. If the progress is
		below zero, <code>false</code> will be returned.
	@see #getValue
	@see #getMaximum
	*/
	public boolean isFinished() {return getValue()>=0 && getValue()==getMaximum();}

	/**Creates a progress event with a status message.
	@param The source of the event (typically <code>this</code>).
	@param newStatus The new status message.
	*/
/*TODO del if not needed
	public ProgressEvent(final Object source, final String status)
	{
		super(source);	//let the parent class initialize
		setStatus(status);	//set the status
	}
*/

	/**Creates a progress event with a task and status, but an unknown progress.
	@param The source of the event (typically <code>this</code>).
	@param task The task being performed.
	@param status The status message.
	*/
	public ProgressEvent(final Object source, final String task, final String status)
	{
		super(source);	//let the parent class initialize
		setTask(task);	//set the task
		setStatus(status);	//set the status
	}

	/**Creates a progress event with a task and status. If <code>finished</code>
		is <code>true</code>, the progress event will indicate finished by setting
		both the value and the maximum to <code>1</code>.
	@param source The source of the event (typically <code>this</code>).
	@param task The task being performed.
	@param status The status message.
	@param finished Whether the progress is finished.
	@see #setValue
	@see #setMaximum
	*/
	public ProgressEvent(final Object source, final String task, final String status, final boolean finished)
	{
		super(source);	//let the parent class initialize
		setTask(task);	//set the task
		setStatus(status);	//set the status
		if(finished)  //if the progress is finished
		{
			setValue(1); //set the progress and the goal to a non-negative value
			setMaximum(1);
		}
	}

	/**Creates a progress event with a task, status, and progress.
	@param source The source of the event (typically <code>this</code>).
	@param task The task being performed.
	@param status The status message.
	@param value The progress, in relation to the goal.
	@param maximum The goal of the progress.
	*/
	public ProgressEvent(final Object source, final String task, final String status, final float value, final float maximum)
	{
		super(source);	//let the parent class initialize
		setTask(task);	//set the task
		setStatus(status);	//set the status
		setValue(value); //set the progress
		setMaximum(maximum);  //set the goal
	}

}

