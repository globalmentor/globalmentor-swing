package com.garretwilson.swing.text;

import java.awt.Rectangle;
import javax.swing.SwingConstants;
import javax.swing.text.*;

/**Routines for working with Swing text classes.
@author Garret Wilson
*/
public class SwingTextUtilities
{

	/**Determines the beginning model position of the text component, ensuring
		that the returned value is not represented by a hidden view.
	@param textComponent The editor.
	@return The position (>=0) if the request can be computed, otherwise
		a value of -1 will be returned.
	@exception BadLocationException Thrown if the offset is out of range.
	 */
	public static int getBegin(final JTextComponent textComponent) throws BadLocationException	//G***this might better to in XMLSectionView.getNextVisualPosition... or something
	{
		final View sectionView=textComponent.getUI().getRootView(textComponent).getView(0);	//get the section view
		final int endOffset=sectionView.getEndOffset();	//find out where the section view ends
		for(int pos=sectionView.getStartOffset(); pos<endOffset; ++pos)	//find the first position in the section that's visible
		{
			final int childViewIndex=sectionView.getViewIndex(pos, Position.Bias.Forward);	//find a view for this position
			if(childViewIndex>=0)	//if we found a valid child index
			{
				final View childView=sectionView.getView(childViewIndex);	//get a reference to this view
				if(childView.isVisible())	//if this child view is visible
					return pos;	//return this position
			}
		}
		return -1;	//show that we couldn't find a beginning position
	}

	/**Returns the child elements of the given parent element as an array of elements.
	@param parentElement The parent element for which children should be returned.
	@return An array containing the child elements of the given parent element.
	@see Element#getElementCount()
	@see Element#getElement(int)
	*/
	public static Element[] getChildElements(final Element parentElement)	//TODO maybe create a NO_ELEMENTS array to speed up the special case of no child elements
	{
		final Element[] childElements=new Element[parentElement.getElementCount()];	//create an array of elements in which to place the child elements	 
		for(int i=childElements.length-1; i>=0; --i)	//look at each child element
		{
			childElements[i]=parentElement.getElement(i);	//store this child element in the array
		}
		return childElements;	//return the child elements we found
	}

	/**Determines the ending model position of the text component, ensuring
		that the returned value is not represented by a hidden view.
	@param textComponent The editor.
	@return The position (>=0) if the request can be computed, otherwise
		a value of -1 will be returned.
	@exception BadLocationException Thrown if the offset is out of range.
	 */
	public static int getEnd(final JTextComponent textComponent) throws BadLocationException	//G***this might better to in XMLSectionView.getNextVisualPosition... or something
	{
		final View sectionView=textComponent.getUI().getRootView(textComponent).getView(0);	//get the section view
		final int startOffset=sectionView.getStartOffset();	//find out where the section view begins
		for(int pos=sectionView.getEndOffset()-1; pos>=startOffset; --pos)	//find the first last in the section that's visible
		{
			final int childViewIndex=sectionView.getViewIndex(pos, Position.Bias.Forward);	//find a view for this position
			if(childViewIndex>=0)	//if we found a valid child index
			{
				final View childView=sectionView.getView(childViewIndex);	//get a reference to this view
				if(childView.isVisible())	//if this child view is visible
					return pos;	//return this position
			}
		}
		return -1;	//show that we couldn't find a beginning position
	}

	/**Finds the last descendant view (the view with the topmost z-order) that
		represents the given position.
	@param view The view in the hierchy at which to start the search.
	@param pos The model position (>=0).
	@param bias The bias toward the previous character or the next character
		represented by the position, in case the position is a boundary of two
		views.
	@returns The deepest descendant view representing the given position, or
		<code>null</code> if no view represents that position.
	*/
	public static View getLeafView(final View view, final int pos, final Position.Bias bias)
	{
/*G***del
Log.trace("looking for leaf view at view: ", view);
Log.trace("view starts at: ", view.getStartOffset());
Log.trace("view ends at: ", view.getEndOffset());
*/
		if(view.getViewCount()>0) //if this view has children
		{
/*G***del
//G***fix		  final int viewIndex=((CompositeView)view).getViewIndexAtPosition(pos);  //G***testing
//G***fix			.getViewIndexAtPosition(pos); //get the view's child that represents the position
*/
		  final int viewIndex=view.getViewIndex(pos, bias); //get the view's child that represents the position
//G***del		  final int viewIndex=view.getViewIndex(pos, Position.Bias.Forward); //get the view's child that represents the position
			if(viewIndex>=0)  //if the index returned is valid
			{
			  return getLeafView(view.getView(viewIndex), pos, bias); //pass the request down to this child view
			}
		}
		return view;  //if there are no children, the view itself must be a leaf view
	}

	/**Searches down an element hierarchy and finds the leaf element that contains
		the given position.
		Modified from <code>javax.swing.text.DefaultStyledDocument.getCharacterElement()</code>.
	@param rootElement The element representing the root of the hierarchy to search.
	@param pos The position in the document (>=0).
  @return The leaf element for the given position.
	@see javax.swing.text.DefaultStyledDocument#getCharacterElement
	*/
/*G***fix
	public static Element getLeafElement(final Element rootElement, final int pos)
	{
		Element element=null; //we haven't found the leaf element yet
		for(element=rootElement; !element.isLeaf();)  //start at the root, and go until we find a leaf
		{
	    final int index=element.getElementIndex(pos); //get the index of the element containing the position
	    element=element.getElement(index);  //change to that element and keep searching
		}
		return element; //return the element we found
	}
*/

	/**Determines the ending row model position of the row that contains
	the specified model position.  The component given must have a
	 * size to compute the result.  If the component doesn't have a size
	 * a value of -1 will be returned.
	 *
	 * @param c the editor
	 * @param offs the offset in the document >= 0
	 * @return the position >= 0 if the request can be computed, otherwise
	 *  a value of -1 will be returned.
	 * @exception BadLocationException if the offset is out of range
	 */
/**G**fix if needed	
	public static final int getRowEnd(JTextComponent c, int offs) throws BadLocationException
	{
		Rectangle r;
		if(c instanceof XMLTextPane)	//G***newswing
		{
			r = ((XMLTextPane)c).modelToView(offs, Position.Bias.Backward);	//G***newswing
		}
		else
		{
			r = c.modelToView(offs);
		}
if (r == null) {
		return -1;
}
int n = c.getDocument().getLength();
int lastOffs = offs;
int y = r.y;
while ((r != null) && (y == r.y)) {
		offs = lastOffs;
		lastOffs += 1;
		r = (lastOffs <= n) ? c.modelToView(lastOffs) : null;
}
return offs;
	}
*/

	/**Returns a document's text for the given element.
	@param element The element from which text should be retrieved.
	@return The document's text that lies within the element's range.
	@exception BadLocationException Thrown if the element's starting and ending
		offsets are invalid.
	@see Element#getDocument
	@see Element#getEndOffset
	@see Element#getStartOffset
	@see Element#getText
	*/
	public static String getText(final Element element) throws BadLocationException
	{
		final Document document=element.getDocument();  //get the element's document
		final int startOffset=element.getStartOffset(); //get the element's starting offset
		return document.getText(startOffset, element.getEndOffset()-startOffset); //return the document's text within the element's offset
	}

	/**Inserts an element into a parent element at the given index. If there is a
		leaf element in the hierarchy of <code>rootElement</code> the ending
		position of which is at the beginning insertion position in the model, that
		leaf element is recreated (as a <code>LeafElement</code>) with the same
		position it held before the insert.
		<p>This method is convenient for inserting elements that begin at the ending
		position of another element, but previous content insertion has, through
		automatic position update, caused that element's end to increase. This method
		allows an element to be inserted after another element, even after a content
		insertion.</p>
	@param rootElement The root element in the hierarchy that allows searches for
		any lower leaf elements that might have ended at the insertion point.
	@param parentElement The parent element into which the element should be
		inserted.
	@param element The element to insert.
	@param index The index in the parent at which the element should be inserted.
	*/
/*G***fix
	public static void insertElementBetween(final Element rootElement, final AbstractDocument.BranchElement parentElement, final DefaultStyledDocument.LeafElement element, final int index)
	{
		final Element[] elementBuffer=new Element[1]; //create an element buffer for this one element
		swingElementBuffer[0]=element; //place the leaf element in the buffer
		parentElement.replace(index, 0, elementBuffer); //insert the element into the parent


		final int insertBeginPos=element

						final LeafElement swingLeafElement=(LeafElement)createLeafElement(swingParentElement, contentAttributeSet, oldLength-1, oldLength+textStringBuffer.length()-1);  //G***testing

					  return swingLeafElement;  //G***testing
/*G***fix
						final Element[] swingElementBuffer=new Element[1]; //create an element buffer for this one element
						swingElementBuffer[0]=swingLeafElement; //place the leaf element in the buffer
						final int siblingCount=swingParentElement.getChildCount(); //find out how many children the parent already has
Log.trace("text sibling count: ", siblingCount);  //G***del
						swingParentElement.replace(siblingCount, 0, swingElementBuffer); //place the leaf element in the parent

*/

}