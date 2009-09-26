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

import java.util.Collection;
import java.util.List;

import javax.swing.text.*;

/**An element that allows its child element list to be manually constructed.
	Child elements can be added by using the standard {@link List} interface
	methods.
	This class is useful for creating anonymous elements to wrap inline elements
	that are alongside block elements in a parent block element.
@author Garret Wilson
@see List
*/
public class AnonymousElement implements Element  //TODO isn't there a better name for this, that doesn't reflect just this one use?
{

	/**The parent element of this element.*/
	protected final Element parentElement;

	/**The attribute set of this element.*/
	protected final AttributeSet attributeSet;

	/**The child elements this element contains.*/
	final Element[] childElementArray;

	/**Creates an anonymous element enclosing the given elements as children.
		The elements are stored locally, with no reference to the original collection.
	@param parent The parent element of which this element owns a subset of child
		views.
	@param attributes The attributes of this element.
	@param childElementCollection The collection of elements this element should contain.
	*/
	public AnonymousElement(final Element parent, final AttributeSet attributes, final Collection<Element> childElementCollection)
	{
		this(parent, attributes, (Element[])childElementCollection.toArray(new Element[childElementCollection.size()]));	//construct the element with the child elements in an array 
	}

	/**Creates an anonymous element enclosing the given elements as children.
		The elements are stored locally, with no reference to the original array.
	@param parent The parent element of which this element owns a subset of child
		views.
	@param attributes The attributes of this element.
	@param childElements The elements this element should contain.
	*/
	public AnonymousElement(final Element parent, final AttributeSet attributes, final Element[] childElements)
	{
		this(parent, attributes, childElements, 0, childElements.length); //initialize the element with all the elements provided
	}

	/**Creates an anonymous element that contains a subset of the child elements
		specified. The elements are stored locally, with no reference to the original
		array.
	@param parent The parent element of which this element owns a subset of child
		views.
	@param attributes The attributes of this element.
	@param childElements The elements this element should contain.
	@param childElementStartIndex The index of the first child element to use
		(0&lt;=<code>childElementStartIndex</code>&lt;<code>childElements.length</code>).
	@param childElementCount The number of child elements to use.
	*/
	public AnonymousElement(final Element parent, final AttributeSet attributes, final Element[] childElements, final int childElementStartIndex, final int childElementCount)
	{
//TODO del		super(parent.getElementCount()); //create a list with an initial size of the number of children the parent element has; since we'll own a subset of those child elements, we shouldn't need more than that number
		parentElement=parent;  //store the parent element
		attributeSet=attributes;  //store the attributes
/*TODO del; we always want to copy
		if(childElementStartIndex==0 && childElementCount==childElements.length)  //if they want to use all the elements
			childElementArray=childElements;  //store the array as it is
		else  //if they only want to use a subset of child elements
		{
*/
			childElementArray=new Element[childElementCount];  //create an array for the elements
		  System.arraycopy(childElements, childElementStartIndex, childElementArray, 0, childElementCount); //copy the array subset to our array
//TODO del		}
	}

	/**@return The document associated with this element; delegates to the parent
		element.*/
	public Document getDocument() {return getParentElement().getDocument();}

	/**@return The parent element, or <code>null</code> if this is a root level
		element.
	*/
	public Element getParentElement() {return parentElement;}

	/**@return The name of the element ("anonymous").*/
	public String getName() {return "anonymous";} //TODO use a constant here

	/**@return The collection of attributes this element contains.*/
	public AttributeSet getAttributes() {return attributeSet;}

	/**Fetches the offset from the beginning of the document that this element
		begins at. The elements are assumed to be stored in logical order, so this
		returns the offset of the first child.
	@return The starting offset (>=0).
	*/
	public int getStartOffset()
	{
		return childElementArray[0].getStartOffset();  //return the offset of the first element
	}

	/**Fetches the offset from the beginning of the document that this element
		ends at. The elements are assumed to be stored in logical order, so this
		returns the end offset of the last child.
		<p>All the default Document implementations descend from AbstractDocument.
		AbstractDocument models an implied break at the end of the document. As a
		result of this, it is possible for this to return a value greater than the
		length of the document.</p>
	@return The ending offset (>=0).
	@see AbstractDocument
	*/
	public int getEndOffset()
	{
		return childElementArray[childElementArray.length-1].getEndOffset();  //return the ending offset of the last element
	}

	/**Gets the child element index closest to the given offset.
		The offset is specified relative to the beginning of the document.
	@param offset The specified offset (>=0).
	@return The element index (>=0), or -1 if .
	*/
	public int getElementIndex(final int offset)
	{
		for(int childElementIndex=getElementCount()-1; childElementIndex>=0; --childElementIndex) //look at each element
		{
			final Element childElement=childElementArray[childElementIndex]; //get a reference to this child element
			if(offset>=childElement.getStartOffset() && offset<childElement.getEndOffset())  //if this element contains the given offset
				return childElementIndex; //return the index of the child element
		}
		return offset>=0 ? childElementArray.length-1 : 0;  //if we couldn't find a match, assume the offset was closest to either the first or last child element
	}

	/**Returns the number of child elements contained by this element.
	@return The number of child elements (>=0).
	*/
	public int getElementCount() {return childElementArray.length;}

  /**Fetches the child element at the given index.
	@param index The specified index (>=0).
	@return The child element requested.
	*/
  public Element getElement(final int index) {return childElementArray[index];}

	/**@return <code>falsle</code> as anonymous elements should never be leaf
		elements.
	*/
	public boolean isLeaf() {return false;}

}