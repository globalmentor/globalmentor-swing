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

package com.globalmentor.swing.text.xml;

import javax.swing.text.*;

/**A block view that is used to contain several inline views within a block view.
@author Garret Wilson
*/
public class XMLAnonymousBlockView extends XMLBlockView //TODO do we even need this class, because wouldn't an anonymous block view with only inline views be an anonymous paragraph view?
{

	/**The attributes of the anonymous block view.*/
	protected final AttributeSet attributeSet;

	/**Fetches the attributes to use when rendering.
		This view uses its own anonymous attributes instead of the attributes of
		the element it represents.
	@return The attributes of the anonymous block view.
	*/
	public AttributeSet getAttributes()
	{
		return attributeSet;  //return our attributes
	}

	/**The child elements for which this anonymous view is responsible.*/
	protected final Element[] ownedElements;

	/**Constructs an anonymous block view expandable on the flowing (non-tiling) axis.
	@param element The element this view is responsible for, although in most
		cases an anonymous view is not responsible for the entire element.
	@param attributes The attributes for the anonymous block view.
	@param childElements The children of <code>element</code> for which this
		anonymous view is responsible.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	*/
	public XMLAnonymousBlockView(final Element element, final AttributeSet attributes, final Element[] childElements, final int axis)
	{
		this(element, attributes, childElements, axis, axis!=X_AXIS, axis!=Y_AXIS); //default to not flowing on the tiling axis
	}

	/**Constructs an anomymous block view, specifying whether the view should be
		allowed to expand to a maximum size for the given axes.
	@param element The element this view is responsible for, although in most
		cases an anonymous view is not responsible for the entire element.
	@param attributes The attributes for the anonymous block view.
	@param childElements The children of <code>element</code> for which this
		anonymous view is responsible.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	*/
	public XMLAnonymousBlockView(final Element element, final AttributeSet attributes, final Element[] childElements, final int axis, final boolean expandX, final boolean expandY)
	{
		super(element, axis, expandX, expandY); //do the default construction
		attributeSet=attributes;  //set the attributes for this view
		ownedElements=childElements;  //save our children
	}


	/**Loads all of the children to initialize the view.
		This is called by the <a href="#setParent">setParent</a> method.
		An anonymous block view only represents a subset of the element's children
		&mdash; the children we were requested to own. Rather than creating views
		based upon the element's children, therefore, this class uses the elements
		specifed when the class was constructed.
	@param viewFactory The view factory.
	@see CompositeView#setParent
	*/
	protected void loadChildren(final ViewFactory viewFactory)
	{
		if(viewFactory==null) //if there is no view factory (which most likely indicates the parent view has changed)
	    return; //don't proceed further
		if(ownedElements.length>0)  //if we have elements we own (we always should)
		{
		  final View[] addedViews=new View[ownedElements.length]; //create an array in which to store the views we create
//TODO del Log.trace(getClass().getName());  //TODO del
		  for(int i=0; i<ownedElements.length; ++i) //look at each of the elements we own
			{
//TODO del Log.trace("Element "+i+" attribute set: "+com.globalmentor.swing.text.AttributeSetUtilities.getAttributeSetString(e.getElement(i).getAttributes()));  //TODO del
				addedViews[i]=viewFactory.create(ownedElements[i]); //create a view for this element
	    }
	    replace(0, 0, addedViews);  //load our created views as children
	  }
	}

}
