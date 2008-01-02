package com.garretwilson.swing.text;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import com.garretwilson.util.Debug;
import com.globalmentor.java.JavaUtilities;

/**Represents a bookmark in a document that can survive edits and can be stored.
	<p>A bookmark may or may not be attached to a document. If the bookmark is
	attached, its offset is continually updated through edits. If a bookmark is
	not attached to a document, it retains a constant offset.</p>
	<p>A bookmark may or may not have an ending offset attached. If it has an
	ending offset, it will be updated as will be the starting offset. If there is
	no ending offset attached, the ending offset will always reflect the beginning
	offset.</p>
@author Garret Wilson
*/
public class Bookmark implements Comparable, Position
{

	/**The optional name of the bookmark.*/
	private String name=null;

		/**@return The name of the bookmark, or <code>null</code> if this bookmark
		  has no name.
		*/
		public String getName() {return name;}

		/**Sets the name of the bookmark.
		@param newName The name of the bookmark, or <code>null</code> if there
			should be no name.
		*/
		public void setName(final String newName) {name=newName;}

	/**The offset used if the bookmark is not attached to a document.*/
	private int offset=0;

		/**Sets the offset of the bookmark. If the document is attached to a
		  document, it is detached.
		@param newOffset The offset of the bookmark.
		*/
		public void setOffset(final int newOffset)
		{
			detach(); //make sure the bookmark isn't attached to a document
			offset=newOffset; //update the offset
		}

	/**The ending offset used if the bookmark is not attached to a document.*/
	private int endOffset=0;

		/**Sets the ending offset of the bookmark. If the document is attached to a
		  document, it is detached.
		@param newEndOffset The ending offset of the bookmark.
		*/
		public void setEndOffset(final int newEndOffset)
		{
			detach(); //make sure the bookmark isn't attached to a document
			endOffset=newEndOffset; //update the ending offset
		}

	/**The position within the document the bookmark represents.*/
	private Position position=null;

		/**@return The position within the document the bookmark represents, or
		  <code>null</code> if the bookmark is not attached to a document.
		*/
		private Position getPosition() {return position;}

		/**Sets the position of the bookmark in the document.
		@param newPosition The new position in the document.
		*/
		private void setPosition(final Position newPosition) {position=newPosition;}

	/**The ending position within the document the bookmark represents.*/
	private Position endPosition=null;

		/**@return The ending position within the document the bookmark represents,
		  or <code>null</code> if the bookmark is not attached to a document.
		*/
		private Position getEndPosition() {return endPosition;}

		/**Sets the ending position of the bookmark in the document.
		@param newEndPosition The new ending position in the document.
		*/
		private void setEndPosition(final Position newEndPosition) {endPosition=newEndPosition;}

	/**Determines whether the bookmark contains a given offset, meaning that the
		the offset is equal to or greater than the start offset and less than the
		ending offset. For the special condition for which the bookmark has no
		ending offset (that is, the ending offset is equal to the beginning offset),
		this method returns <code>true</code> if the given offset equals the
		beginning offset.
	@param An offset in a document.
	@return <code>true</code> if the bookmark contains the given offset, or if
		the bookmark has no ending position and the bookmark's beginning offset
		matching the given offset.
	*/
	public boolean contains(final int offset)
	{
		return getEndOffset()!=getStartOffset() ? //if there is an ending offset
			offset>=getStartOffset() && offset<getEndOffset() : //return whether the offset falls within the bookmark range
		  offset==getStartOffset(); //if there is no ending offset, see if the starting offset matches the given offset
	}

	/**Attaches the bookmark to a document at a particular location in a document.
		If the bookmark is already attached to a document at a different location,
		it is detached and reattached to the specified location.
	@param document The document to which the bookmark should be attached.
	@param offset The offset in the document at which the bookmark should be attached.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location in the document.
	*/
	public void attach(final Document document, final int offset) throws BadLocationException
	{
		if(!isAttached() || (isAttached() && getOffset()!=offset)) //only reattach if we're not attached already, or we're attached but at a different offset
		{
			detach(); //make sure the bookmark is not attached to a document
			if(document!=null)  //if a valid document was passed
				setPosition(document.createPosition(offset)); //create a position in the document and store the position
		}
	}

	/**Attaches the bookmark to a document at particular starting and ending
		locations in a document.
		If the bookmark is already attached to a document at a different location,
		it is detached and reattached to the specified location.
	@param document The document to which the bookmark should be attached.
	@param startOffset The starting offset in the document at which the bookmark
		should be attached.
	@param endOffset The ending offset in the document at which the bookmark
		should be attached.
	@exception BadLocationException Thrown if one of the given positions does not
		represent a valid location in the document.
	*/
	public void attach(final Document document, final int startOffset, final int endOffset) throws BadLocationException
	{
		if(!isAttached() || //only reattach if we're not attached already...
				(isAttached() && (getStartOffset()!=startOffset || getEndOffset()!=endOffset))) //...or we're attached but at a different offset
		{
			detach(); //make sure the bookmark is not attached to a document
			if(document!=null)  //if a valid document was passed
			{
				setPosition(document.createPosition(startOffset)); //create a position in the document and store it as the starting position
				setEndPosition(document.createPosition(endOffset)); //create a position in the document and store it as the ending position
			}
		}
	}

	/**Attaches the bookmark to a document at the location at which the document
		was last attached.
		If the bookmark is already attached to a document, it is detached and
		reattached to another location.
	@param document The document to which the bookmark should be attached.
	@exception BadLocationException Thrown if that last attached position is no
		longer valid.
	*/
/*G***del
	public void attach(final Document document) throws BadLocationException
	{
		attach(document, getOffset());  //attach ourselves to the document at our last recorded position
	}
*/

	/**Attaches the bookmark to a document at the location at which the document
		was last attached. If the bookmark had an ending offset greater than its
		starting offset, that position is attached as well.
		If the bookmark is already attached to a document, it is detached and
		reattached to another location.
	@param document The document to which the bookmark should be attached.
	@exception BadLocationException Thrown if that last attached position is no
		longer valid.
	*/
	public void attach(final Document document) throws BadLocationException
	{
		if(getEndOffset()>getStartOffset())  //if we had a valid ending offset
			attach(document, getStartOffset(), getEndOffset());  //attach ourselves to the document at our last recorded staring and ending position
		else  //if we only were attached at the beginning offset
			attach(document, getOffset());  //attach ourselves to the document at our last recorded position
	}

	/**Detaches the bookmark from the document. After this point, the offset will
		reflect the last attached position in the document. If the bookmark did not
		have an ending position attached, its ending offset will be set equal to
		theh starting offset.
	*/
	public void detach()
	{
		final Position position=getPosition();  //get the current position, if we have one
		if(position!=null)  //if we have a position
		{
			offset=position.getOffset();  //update our local offset so that it will reflect our last document position
			setPosition(null);  //throw away any position we had
			final Position endPosition=getEndPosition();  //get the current ending position, if we have one (we should only ever have one when we have a starting position as well)
			if(endPosition!=null)  //if we have an ending position
			{
				endOffset=endPosition.getOffset();  //update our local ending offset so that it will reflect our last document ending position
				setEndPosition(null);  //throw away any position we had
			}
			else  //if the end offset was not attached
			{
				endOffset=offset; //make sure the ending offset is equal to the beginning offset
			}
		}
	}

	/**Default constructor that does not link the bookmark to a document.*/
	public Bookmark() {}

	/**Constructor that links the bookmark to a position that already exists in
		the document. It is assumed that the position argument will be automatically
		updated. If not, this class will function as if it were not attached to
		a document.
	@param position The position in the document.
	*/
/*G***del if we don't need
	public Bookmark(final Position position)
	{
	  setPosition(position);  //set the position
	}
*/

	/**Constructs a bookmark attached to a document at a particular offset.
	@param document The document to which the bookmark should be attached.
	@param offset The offset in the document at which the bookmark should be attached.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location in the document.
	*/
	public Bookmark(final Document document, final int offset) throws BadLocationException
	{
		attach(document, offset); //attach the bookmark to the document
	}

	/**Constructs a bookmark attached to a document at particular starting and
		ending offsets.
	@param document The document to which the bookmark should be attached.
	@param startOffset The staring offset in the document at which the bookmark
		should be attached.
	@param endOffset The ending offset in the document at which the bookmark
		should be attached.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location in the document.
	*/
	public Bookmark(final Document document, final int startOffset, final int endOffset) throws BadLocationException
	{
		attach(document, startOffset, endOffset); //attach the bookmark to the document
	}

	/**Constructs a named bookmark attached to a document at a particular offset.
	@param newName The new name of the bookmark.
	@param document The document to which the bookmark should be attached.
	@param offset The offset in the document at which the bookmark should be attached.
	@exception BadLocationException Thrown if the given position does not
		represent a valid location in the document.
	*/
	public Bookmark(final String newName, final Document document, final int offset) throws BadLocationException
	{
		setName(newName); //set the name of the bookmark
		attach(document, offset); //attach the bookmark to the document
	}

	/**Constructs a bookmark at a particular offset, but not attached to a document.
	@param offset The offset in the document at which the bookmark should be attached.
	*/
	public Bookmark(final int offset) throws BadLocationException
	{
		setOffset(offset);  //set our local offset
	}

	/**Constructs a named bookmark at a particular offset, but not attached to a document.
	@param newName The new name of the bookmark.
	@param offset The offset in the document at which the bookmark should be attached.
	*/
	public Bookmark(final String newName, final int offset) throws BadLocationException
	{
		setName(newName); //set the name of the bookmark
		setOffset(offset);  //set our local offset
	}

	/**@return The current offset (>=0) within the document. This allows this
		class to fulfill the requirements of the <code>Position</code> interface.
	*/
	public int getOffset()
	{
		final Position position=getPosition();  //get the position
		return position!=null ? position.getOffset() : offset; //return the position's offset or, if there is no position, the last offset we know about
	}

	/**@return The current offset (>=0) within the document. This is a convenience
		method equivalent to <code>getStartOffset()</code> to provide the converse
		to <code>getEndOffset()</code>.
	@see #getOffset
	*/
	public final int getStartOffset()
	{
		return getOffset(); //return the offset as the starting offset
	}

	/**@return The current ending offset (>=0) within the document, which will be
		equal to <code>getStartOffset()</code> if the bookmark is attached but no
		ending offset is defined.*/
	public int getEndOffset()
	{
		if(isAttached())  //if the bookmark is attached
		{
			final Position endPosition=getEndPosition();  //get the ending position
			return endPosition!=null ? endPosition.getOffset() : getStartOffset(); //return the position's offset or, if there is no ending position, the starting position
		}
		else  //if the bookmark is not attached
		{
			return endOffset; //return the last ending offset we know about
		}
	}

	/**@return Whether the bookmark is attached to a document.*/
	public boolean isAttached() {return getPosition()!=null;}

	/**If <code>object</code> is a <code>Bookmark</code>, compares the name,
		start, and ending offsets. Otherwise, compares the objects using the
		superclass functionality.
	@param object The object with which to compare this bookmark; should be a
		<code>Bookmark</code>
	@return <code>true<code> if this bookmark equals that specified in
		<code>object</code>.
	@see #getStartOffset
	@see #getEndOffset
	@see #getName
	*/
	public boolean equals(Object object)
	{
Debug.trace("Comparing bookmark "+this+" to object: ", object); //G***del
		if(object instanceof Bookmark)	//if we're being compared with another bookmark
		{
			final Bookmark otherBookmark=(Bookmark)object;  //cast the other object to a bookmark
			if(getStartOffset()!=otherBookmark.getStartOffset()  //if the offsets don't match
				  || getEndOffset()!=otherBookmark.getEndOffset())
				return false;
			if(getName()!=null) //if we have a name
			  return getName().equals(otherBookmark.getName());  //compare this name with the other
			else  //if we don't have a name
				return otherBookmark.getName()==null; //see if the other bookmark also doesn't have a name
		}
		else	//if we're being compared with anything else
			return super.equals(object);	//use the default compare
	}

	/**Compares this bookmark to another bookmark.
		This method determines order based upon the offset and, if two offsets are
		the same, the ending offsets and then the order of the names, with no name
		considered lower.
	@param object The object with which to compare the component. This must be
		another <code>Bookmark</code> object.
	@return A negative integer, zero, or a positive integer as this bookmark is
		less than, equal to, or greater than the specified bookmark, respectively.
		If the bookmarks represent the same beginning and ending offsets, their
		names are compared.
	@exception ClassCastException Thrown if the specified object's type is not
		an <code>Bookmark</code>.
	@see #Offset
	*/
	public int compareTo(Object object) throws ClassCastException
	{
		final Bookmark bookmark=(Bookmark)object;  //cast the object to a bookmark
		final int offsetDifference=getOffset()-bookmark.getOffset();  //get the difference in offsets
		if(offsetDifference!=0) //if the offsets aren't the same
			return offsetDifference; //return the difference in offsets
		else  //if the offsets are the same, compare the ending offsets
		{
			final int endOffsetDifference=getEndOffset()-bookmark.getEndOffset();  //get the difference in ending offsets
			if(endOffsetDifference!=0) //if the ending offsets aren't the same
				return endOffsetDifference; //return the difference in ending offsets
			else  //if the ending offsets are the same, compare the names
			{
				return JavaUtilities.compareTo(getName(), bookmark.getName());  //compare the names
/*G***del
				if(getName()!=null && bookmark.getName()!=null) //if both bookmarks have names
					return getName().compareTo(bookmark.getName()); //compare the names
				else if(getName()==bookmark.getName())  //if neither bookmark has a name (we know at this point that one does not have a name, so if the names are equal then neither name exists)
					return 0; //the bookmarks are equal (but not necessarily identical)
				else  //if one bookmark has a name and the other doesn't
					return getName()==null ? -1 : 1;  //the missing name is lower
*/
			}
		}
	}

	/**@return A string representation of the bookmark.*/
/*G***fix or del
	public String toString()
	{


	}
*/

}