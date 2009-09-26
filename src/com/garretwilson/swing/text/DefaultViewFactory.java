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

package com.garretwilson.swing.text;

import java.util.List;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import com.globalmentor.java.Objects;

/**A default implementation of a view factory that allows a fallback view
	factory to be invoked if this view factory doesn't know how to create a
	particular view.
	<p>As this class implements {@link ViewsFactory}, which allows multiple
	views to be created, one should usually override
	{@link #create(Element, boolean)} and use the default implementation of
	the other methods.</p>
@author Garret Wilson
*/
public abstract class DefaultViewFactory implements ViewsFactory //TODO maybe use this as a basis for XMLViewFactory
{

	/**The view factory to use as a fallback in case this view factory cannot
		create a view for the given element.
	*/
	private final ViewFactory fallbackViewFactory;

	/**@return The view factory to use as a fallback in case this view factory
		cannot create a view for the given element.
	*/
	protected ViewFactory getFallbackViewFactory() {return fallbackViewFactory;}

	/**Creates a view factory with no fallback view factory.*/
	public DefaultViewFactory()
	{
		this(null); //create a view factory with no default
	}

	/**Creates a view factory with a fallback view factory.
	@param fallback The view factory to be used as a fallback if this view factory
		cannnot create a view.
	 */
	public DefaultViewFactory(final ViewFactory fallback)
	{
		fallbackViewFactory=fallback; //save the fallback view factory
	}

	/**Creates a view for the given element..
	@param element The element this view will represent.
	@return A view to represent the given element.
	*/
	public View create(final Element element)
	{
		return create(element, false);  //return a single view to represent the given view, giving no indication if multiple views are needed
	}

	/**Creates one or more views for the given element, storing the views in
		the given list.
		This method allows one element (such as a nested inline element within a
		paragraph) to be represented by one level of multiple views rather than
		a hierarchy of views.
	@param element The element the view or views will represent.
	@param addViewList The list of views to which the views should be added.
	*/
	public void create(final Element element, final List<View> addViewList)
	{
		final View view=create(element, true);  //create a view for the element, if we can, but get an indication of if there should be several views
		if(view!=null)  //if there is only one view
		{
			addViewList.add(view);  //add the list to the view, mimicking the normal functionality
		}
		else  //if there should be multiple views
		{
			int childElementCount=element.getElementCount();  //see how many child elements there are
			for(int i=0; i<childElementCount; ++i)  //look at each child element
				create(element.getElement(i), addViewList); //create one or more views for this child element
		}
	}

	/**Creates a view for the given element. If the element specifies a
		namespace and a view factory has been registered for the given namespace,
		the view creation will be delegated to the designated view factory.
		As this class implements <code>ViewsFactory</code>, which allows multiple
		views to be created, this method can optionally indicate multiple views
		are needed by returning <code>null</code>.
		<p>The default implementation of this class attempts to call the
		corresponding method in the fallback view factory if the fallback view
		factory is a <code>ViewFactory</code>. If not, the fallback view's normal
		<code>create()</code> method is called. If there is no fallback view
		factory, an illegal argument exception is thrown.</p>
	@param element The element this view will represent.
	@param indicateMultipleViews Whether <code>null</code> should be returned to
		indicate multiple views should represent the given element.
	@return A view to represent the given element, or <code>null</code>
		indicating the element should be represented by multiple views.
	@exception IllegalArgumentException Thrown if the element is not recognized
		and there is no fallback view factory.
	@see com.garretwilson.swing.text.ViewsFactory
	*/
	public View create(final Element element, final boolean indicateMultipleViews)
	{
		final ViewFactory fallbackViewFactory=getFallbackViewFactory(); //get the fallback view factory
		if(fallbackViewFactory!=null) //if we have a fallback view factory
		{
			if(fallbackViewFactory instanceof ViewsFactory) //if the fallback view factory knows how to create multiple views
				return ((ViewsFactory)fallbackViewFactory).create(element, indicateMultipleViews); //allow the fallback view factory to indicate it needs to create multiple views, if it wishes
			else  //if the fallback view factory is a normal view factory
		  	return getFallbackViewFactory().create(element);  //let the fallback view factory create the view normally
		}
		else  //if we have no fallback view factory
		  throw new IllegalArgumentException(Objects.toString(element));  //show that we don't know what to do with the element
//TODO del			return new XMLInlineView(element);	//everything we don't know what to do with gets to be an inline view TODO should this be the default?
	}

}