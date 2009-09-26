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

import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import com.garretwilson.awt.ColorUtilities;
import com.globalmentor.java.Java;
import com.globalmentor.java.Objects;

/**Represents a marked section with an optional length in a document that can
	survive edits and can be stored. An annotation maintains a highlight color and
	can contain an optional note.
@see Bookmark
@author Garret Wilson
*/
public class Annotation extends Bookmark
{

	/**The optional text of the annotation.*/
	private String text=null;

		/**@return The text of the bookmark, or <code>null</code> if this annotation
		  has not text.
		*/
		public String getText() {return text;}

		/**Sets the text of the bookmark.
		@param newText The text of the annotation, or <code>null</code> if there
			should be no text.
		*/
		public void setText(final String newText) {text=newText;}

	/**The highlight color, or <code>null</code> if this annotation is not
		highlighted.*/
	private Color color=null;

		/**@return The highlight color, or <code>null</code> if this annotation is
		  not highlighted.*/
		public Color getColor() {return color;}

		/**Sets the highlight color.
		@param newColor The new highlight color, or <code>null</code> if there
			should be no highlight.
		*/
		public void setColor(final Color newColor) {color=newColor;}

	/**Default constructor that does not link the annotation to a document.*/
	public Annotation() {}

	/**Constructs an annotation attached to a document at a particular offset.
	@param document The document to which the bookmark should be attached.
	@param offset The offset in the document at which the annotation should be attached.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location in the document.
	*/
	public Annotation(final Document document, final int offset) throws BadLocationException
	{
		super(document, offset);  //construct the parent
	}

	/**Constructs an annotation attached to a document at particular offsets with
		no color.
	@param document The document to which the bookmark should be attached.
	@param startOffset The starting offset in the document at which the annotation
		should be attached.
	@param endOffset The ending offset in the document at which the annotation
		should be attached.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location in the document.
	*/
	public Annotation(final Document document, final int startOffset, final int endOffset) throws BadLocationException
	{
		super(document, startOffset, endOffset);  //construct the parent
	}

	/**Constructs an annotation attached to a document at particular offsets with
		a given color.
	@param document The document to which the bookmark should be attached.
	@param startOffset The starting offset in the document at which the annotation
		should be attached.
	@param endOffset The ending offset in the document at which the annotation
		should be attached.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location in the document.
	*/
	public Annotation(final Document document, final int startOffset, final int endOffset, final Color color) throws BadLocationException
	{
		super(document, startOffset, endOffset);  //construct the parent
		setColor(color);  //set the highlight color
	}

	/**Constructs an annotation with a note attached to a document at a
		particular offset.
	@param text The new text of the annotation.
	@param document The document to which the annotation should be attached.
	@param offset The offset in the document at which the annotation should be attached.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location in the document.
	*/
	public Annotation(final String text, final Document document, final int offset) throws BadLocationException
	{
		this(document, offset); //do the default construction
		setText(text); //set the text of the annotation
	}

	/**Constructs an annotation at a particular offset, but not attached to a document.
	@param offset The offset in the document at which the annotation should be attached.
	*/
	public Annotation(final int offset) throws BadLocationException
	{
		super(offset);  //construct the parent
	}

	/**Constructs an annotation with text at a particular offset, but not attached
		to a document.
	@param text The text of the annotation.
	@param offset The offset in the document at which the bookmark should be attached.
	*/
	public Annotation(final String text, final int offset) throws BadLocationException
	{
		super(offset);  //construct the parent
		setText(text); //set the text of the annotation
	}

	/**If <code>object</code> is a <code>Annotation</code>, compares the
		bookmark-related attributes then compares the name and color.
		Otherwise, compares the objects using the superclass functionality.
	@param object The object with which to compare this annotation; should be a
		<code>Annotation</code>
	@return <code>true<code> if this annotation equals that specified in
		<code>object</code>.
	@see #getText
	@see #getEndOffset
	@see #getName
	*/
	public boolean equals(Object object)
	{
		if(object instanceof Annotation)	//if we're being compared with another annotation
		{
			final Annotation annotation=(Annotation)object;  //cast the other object to an annotation
			if(!super.equals(annotation)) //if the bookmark-related items are not equal
				return false; //show the object do not match
		  if(!Objects.equals(getText(), annotation.getText())) //if the text isn't the same
				return false; //show the object do not match
			return Objects.equals(getColor(), annotation.getColor());  //compare the colors
		}
		else	//if we're being compared with anything else
			return super.equals(object);	//use the default compare
	}

	/**Compares this annotation to another annotation.
		This method determines order identically to <code>Bookmark</code>, and if
		bookmark-related items are equal the color and then the text is compared,
		with <code>null</code> values ranking before non-null values.
	@param object The object with which to compare the annotation. This must be
		another <code>Annnotation</code> object.
	@return A negative integer, zero, or a positive integer as this annotation is
		less than, equal to, or greater than the specified annotation, respectively.
	@exception ClassCastException Thrown if the specified object's type is not
		an <code>Annotation</code>.
	*/
	public int compareTo(Object object) throws ClassCastException
	{
		final Annotation annotation=(Annotation)object;  //cast the object to an annotation
		int result=super.compareTo(annotation); //compare the bookmark-related items
		if(result==0) //if the bookmark-related items are equal
		{
		  result=ColorUtilities.compareTo(getColor(), annotation.getColor());  //compare the colors
			if(result==0) //if the colors are equal
			{
			  result=Java.compareTo(getText(), annotation.getText());  //compare the text
			}
		}
		return result;  //return the final comparison result
	}


	/**@return A string representation of the annotation.*/
/*TODO fix or del
	public String toString()
	{


	}
*/

}