package com.garretwilson.swing.text.xml;

import java.awt.Shape;
import java.util.*;
import javax.mail.internet.ContentType;
import javax.swing.event.*;
import javax.swing.text.*;

import com.garretwilson.swing.text.*;
import com.garretwilson.swing.text.xml.xhtml.XHTMLSwingTextUtilities;
import com.garretwilson.text.xml.xhtml.XHTMLConstants;
import com.garretwilson.util.Debug;

/**A view to represent an entire section. This view understands that its
	child elements represent XML document trees.
<p>This implementation performs special processing on XHTML document, hiding
	content before and after the body element.</p>
@author Garret Wilson
*/
public class XMLSectionView extends XMLBlockView
{

	/**Constructs a seciton view expandable on the flowing (non-tiling) axis.
		The section tiles on the Y axis.
	@param element The element this view is responsible for.
	*/
	public XMLSectionView(final Element element)
	{
		super(element, Y_AXIS); //default to tiling on the Y axis G***do we want to allow this to be specifie?
	}

	/**Loads all of the children to initialize the view.
		This is called by the <a href="#setParent">setParent</a> method.
		A block view knows that, should there be both inline and block children,
		the views for the inline children should not be created normally but should
		be wrapped in one or more anonymous views. Furthermore, inline views
		consisting only of whitespace will be given hidden views.
	@param viewFactory The view factory.
	@see CompositeView#setParent
	*/
	protected void loadChildren(final ViewFactory viewFactory)
	{
		if(viewFactory==null) //if there is no view factory, we can't load the children
			return; //we can't do anything
		final Element parentElement=getElement();	//get the parent element 
		final Element[] childElements=getSectionChildElements(parentElement, getStartOffset(), getEndOffset()); //get the child elements that fall within our range
		final View[] views=XMLBlockView.createBlockViews(parentElement, childElements, viewFactory);  //create the child views, ensuring they are block elements
		replace(0, getViewCount(), views);  //add the views as child views to this view pool
	}

	/**Returns all child elements for which views should be created in a section.
		If a section view holds multiple documents, for example, the children of those
		document elements will be included. An XHTML document, furthermore, will
		return the contents of its <code>&lt;body&gt;</code> element.
		It is assumed that the ranges precisely enclose any child elements within
		that range, so any elements that start within the given range will be
		included.
	@param sectionElement The element representing a section.
	@param startOffset This range's starting offset.
	@param endOffset This range's ending offset.
	@return An array of elements for which views should be created for a section.
	*/
	public static Element[] getSectionChildElements(final Element sectionElement, final int startOffset, final int endOffset)
	{
		final List<Element> elementList=new ArrayList<Element>();  //create a list in which to store the elements as we find them
		final int documentElementCount=sectionElement.getElementCount();  //find out how many child elements there are (representing XML documents)
			//look at each element representing an XML document, skipping the last dummy '\n' element, which does not represent a document
		for(int documentElementIndex=0; documentElementIndex<documentElementCount-1; ++documentElementIndex)
		{
			final Element documentElement=sectionElement.getElement(documentElementIndex); //get a reference to this child element
	    	//if this document's range overlaps with our range
			if(documentElement.getStartOffset()<endOffset && documentElement.getEndOffset()>startOffset)
			{
				final AttributeSet documentAttributeSet=documentElement.getAttributes();  //get the attributes of the document element
				if(XMLStyleUtilities.isPageBreakView(documentAttributeSet)) //if this is a page break element
				{
					elementList.add(documentElement);  //add this element to our list of elements; it's not a top-level document like the others TODO this is a terrible hack; fix
				}
				else
				{
//G***del if not needed final boolean isHTMLDocument=XHTMLSwingTextUtilities.isHTMLDocumentElement(documentAttributeSet);	//see if this is an HTML document
	//G***del if not needed				Element baseElement=documentElement;  //we'll find out which element to use as the parent; in most documents, that will be the document element; in HTML elements, it will be the <body> element
					final ContentType documentMediaType=XMLStyleUtilities.getMediaType(documentAttributeSet);  //get the media type of the document
					final String documentElementLocalName=XMLStyleUtilities.getXMLElementLocalName(documentAttributeSet);  //get the document element local name
					final String documentElementNamespaceURI=XMLStyleUtilities.getXMLElementNamespaceURI(documentAttributeSet);  //get the document element local name
					final int childElementCount=documentElement.getElementCount();  //find out how many children are in the document
					for(int childIndex=0; childIndex<childElementCount; ++childIndex)  //look at the children of the document element
					{
						final Element childElement=documentElement.getElement(childIndex); //get a reference to the child element					  
						if(childElement.getStartOffset()<endOffset && childElement.getEndOffset()>startOffset)	//if this child element's range overlaps with our range
						{
							final AttributeSet childAttributeSet=childElement.getAttributes();  //get the child element's attributes
							final String childElementLocalName=XMLStyleUtilities.getXMLElementLocalName(childAttributeSet);  //get the child element local name
		Debug.trace("Looking at child: ", childElementLocalName); //G***del
								//if this element is an HTML <body> element 
							if(XHTMLConstants.ELEMENT_BODY.equals(childElementLocalName) && XHTMLSwingTextUtilities.isHTMLElement(childAttributeSet, documentAttributeSet))  
							{
								final int bodyChildElementCount=childElement.getElementCount(); //find out how many children the body element has
								for(int bodyChildIndex=0; bodyChildIndex<bodyChildElementCount; ++bodyChildIndex) //look at each of the body element's children
								{
		Debug.trace("Adding body child element: ", bodyChildIndex);
									final Element bodyChildElement=childElement.getElement(bodyChildIndex); //get this child element of the body element
									if(bodyChildElement.getStartOffset()>=startOffset && bodyChildElement.getEndOffset()<endOffset) //if this child element falls within our range (we can't check for simply an overlap, because we have no way of breaking)
									{
										elementList.add(bodyChildElement);  //add this body child element to our list of elements
									}
								}
							}
							else  //if this element is not an XHTML <body> element
							{
								elementList.add(childElement);  //add this child element to our list of elements
							}
						}
					}
				}
			}
		}
		elementList.add(sectionElement.getElement(documentElementCount-1));	//add the last element normally, as it is not a document at all but a dummy hierarchy added by Swing TODO test
		return (Element[])elementList.toArray(new Element[elementList.size()]); //return the views as an array of views
	}


		/**
		 * Updates the child views in response to receiving notification
		 * that the model changed, and there is change record for the
		 * element this view is responsible for.  This is implemented
		 * to assume the child views are directly responsible for the
		 * child elements of the element this view represents.  The
		 * ViewFactory is used to create child views for each element
		 * specified as added in the ElementChange, starting at the
		 * index specified in the given ElementChange.  The number of
		 * child views representing the removed elements specified are
		 * removed.
		 *
		 * @param ec The change information for the element this view
		 *  is responsible for.  This should not be null if this method
		 *  gets called.
		 * @param e the change information from the associated document
		 * @param f the factory to use to build child views
		 * @return whether or not the child views represent the
		 *  child elements of the element this view is responsible
		 *  for.  Some views create children that represent a portion
		 *  of the element they are responsible for, and should return
		 *  false.  This information is used to determine if views
		 *  in the range of the added elements should be forwarded to
		 *  or not.
		 * @see #insertUpdate
		 * @see #removeUpdate
		 * @see #changedUpdate
		 * @since 1.3
		 */
		protected boolean updateChildren(DocumentEvent.ElementChange ec, DocumentEvent e, ViewFactory f)
		{
Debug.trace("Section updating children"); //G***fix
//G***fix ((XMLDocument)getElement().getDocument()).applyxStyles();  //G***testing


//G***testing; brought up from View
	Element[] removedElems = ec.getChildrenRemoved();
Debug.trace("children removed: ", removedElems.length);
	Element[] addedElems = ec.getChildrenAdded();
Debug.trace("children added: ", addedElems.length);
	View[] added = null;
	if (addedElems != null) {
			added = new View[addedElems.length];
			for (int i = 0; i < addedElems.length; i++) {
Debug.trace("Creating added element: ", addedElems[i].getClass().getName());  //G***del
		added[i] = f.create(addedElems[i]);
			}
	}
	int nremoved = 0;
	int index = ec.getIndex();


	//G***del
	final String ourLocalName=XMLStyleUtilities.getXMLElementLocalName(getElement().getAttributes());  //G***testing

	//G***del
	final String elementLocalName=XMLStyleUtilities.getXMLElementLocalName(ec.getElement().getAttributes());  //G***testing

				//G***testing
		//as the section view can conflate the structure and place XHTML elements before the body, update our index accordingly
//G***del	final Element parentElement=ec.getElement().getParentElement();	//get the parent element
//G***del	final String parentElementLocalName=XMLStyleConstants.getXMLElementLocalName(parentElement.getAttributes());  //G***testing
	if("body".equals(elementLocalName))
	{
		final Element parentElement=ec.getElement().getParentElement();	//get the parent element
//G***del		final Element grandparentElement=parentElement.getParentElement();	//get the grandparent element
		final int extraElementCount=parentElement.getElementIndex(ec.getElement().getStartOffset());	//find out how many elements were added that are outside the body
		index+=extraElementCount;	//update the index to reflect the views before the body  
	}

	if (removedElems != null) {
			nremoved = removedElems.length;
	}
	replace(index, nremoved, added);
	return true;


//G***fix		  return super.updateChildren(ec, e, f); //G***fix
		}



		/**
		 * Gives notification that something was inserted into
		 * the document in a location that this view is responsible for.
		 * To reduce the burden to subclasses, this functionality is
		 * spread out into the following calls that subclasses can
		 * reimplement:
		 * <ol>
		 * <li><a href="#updateChildren">updateChildren</a> is called
		 * if there were any changes to the element this view is
		 * responsible for.  If this view has child views that are
		 * represent the child elements, then this method should do
		 * whatever is necessary to make sure the child views correctly
		 * represent the model.
		 * <li><a href="#forwardUpdate">forwardUpdate</a> is called
		 * to forward the DocumentEvent to the appropriate child views.
		 * <li><a href="#updateLayout">updateLayout</a> is called to
		 * give the view a chance to either repair it's layout, to reschedule
		 * layout, or do nothing.
		 * </ol>
		 *
		 * @param e the change information from the associated document
		 * @param a the current allocation of the view
		 * @param f the factory to use to rebuild if the view has children
		 * @see View#insertUpdate
		 */
		public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	if (getViewCount() > 0) {
			Element elem = getElement();
			DocumentEvent.ElementChange ec = e.getChange(elem);


//TODO combine and tidy all this section-specific calculation---probably best to even make an XHTMLSectionView that rides on top of this and is created in a view factory

Debug.trace("section view insert update");  //G***del; testing
if(ec==null)  //G***testing; fix; comment; G***we don't need this test; the test is in the for loop
{
	for(int i=elem.getElementCount()-1; ec==null && i>=0; --i)
	{
		final Element childElement=elem.getElement(i);  //G***testing
		if(childElement.getAttributes()!=null)  //G***del; we probably don't need this test
		{
			final String childElementLocalName=XMLStyleUtilities.getXMLElementLocalName(childElement.getAttributes());  //G***testing

Debug.trace("child "+i+" local name: ", childElementLocalName);  //G***del; testing

			if("html".equals(childElementLocalName))  //G***testing
			{

				for(int j=childElement.getElementCount()-1; ec==null && j>=0; --j)
				{
					final Element grandchildElement=childElement.getElement(j);  //G***testing
					if(grandchildElement.getAttributes()!=null)  //G***del; we probably don't need this test
					{
						final String grandchildElementLocalName=XMLStyleUtilities.getXMLElementLocalName(grandchildElement.getAttributes());  //G***testing

			Debug.trace("grandchild "+i+" local name: ", grandchildElementLocalName);  //G***del; testing

						if("body".equals(grandchildElementLocalName))  //G***testing
						{
							ec=e.getChange(grandchildElement); //G***testing
						}
					}
				}
			}
		}
	}
}

			if (ec != null) {
		if (! updateChildren(ec, e, f)) {
				// don't consider the element changes they
				// are for a view further down.
				ec = null;
		}
			}
			forwardUpdate(ec, e, a, f);
			updateLayout(ec, e, a);
	}
		}

		/**
		 * Gives notification that something was removed from the document
		 * in a location that this view is responsible for.
		 * To reduce the burden to subclasses, this functionality is
		 * spread out into the following calls that subclasses can
		 * reimplement:
		 * <ol>
		 * <li><a href="#updateChildren">updateChildren</a> is called
		 * if there were any changes to the element this view is
		 * responsible for.  If this view has child views that are
		 * represent the child elements, then this method should do
		 * whatever is necessary to make sure the child views correctly
		 * represent the model.
		 * <li><a href="#forwardUpdate">forwardUpdate</a> is called
		 * to forward the DocumentEvent to the appropriate child views.
		 * <li><a href="#updateLayout">updateLayout</a> is called to
		 * give the view a chance to either repair it's layout, to reschedule
		 * layout, or do nothing.
		 * </ol>
		 *
		 * @param e the change information from the associated document
		 * @param a the current allocation of the view
		 * @param f the factory to use to rebuild if the view has children
		 * @see View#removeUpdate
		 */
		public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	if (getViewCount() > 0) {
			Element elem = getElement();
			DocumentEvent.ElementChange ec = e.getChange(elem);

		Debug.trace("section view remove update");  //G***del; testing
		if(ec==null)  //G***testing; fix; comment; G***we don't need this test; the test is in the for loop
		{
			for(int i=elem.getElementCount()-1; ec==null && i>=0; --i)
			{
				final Element childElement=elem.getElement(i);  //G***testing
				if(childElement.getAttributes()!=null)  //G***del; we probably don't need this test
				{
					final String childElementLocalName=XMLStyleUtilities.getXMLElementLocalName(childElement.getAttributes());  //G***testing

		Debug.trace("child "+i+" local name: ", childElementLocalName);  //G***del; testing

					if("html".equals(childElementLocalName))  //G***testing
					{

						for(int j=childElement.getElementCount()-1; ec==null && j>=0; --j)
						{
							final Element grandchildElement=childElement.getElement(j);  //G***testing
							if(grandchildElement.getAttributes()!=null)  //G***del; we probably don't need this test
							{
								final String grandchildElementLocalName=XMLStyleUtilities.getXMLElementLocalName(grandchildElement.getAttributes());  //G***testing

					Debug.trace("grandchild "+i+" local name: ", grandchildElementLocalName);  //G***del; testing

								if("body".equals(grandchildElementLocalName))  //G***testing
								{
									ec=e.getChange(grandchildElement); //G***testing
								}
							}
						}
					}
				}
			}
		}



			if (ec != null) {
		if (! updateChildren(ec, e, f)) {
				// don't consider the element changes they
				// are for a view further down.
				ec = null;
		}
			}
			forwardUpdate(ec, e, a, f);
			updateLayout(ec, e, a);
	}
		}

		/**
		 * Gives notification from the document that attributes were changed
		 * in a location that this view is responsible for.
		 * To reduce the burden to subclasses, this functionality is
		 * spread out into the following calls that subclasses can
		 * reimplement:
		 * <ol>
		 * <li><a href="#updateChildren">updateChildren</a> is called
		 * if there were any changes to the element this view is
		 * responsible for.  If this view has child views that are
		 * represent the child elements, then this method should do
		 * whatever is necessary to make sure the child views correctly
		 * represent the model.
		 * <li><a href="#forwardUpdate">forwardUpdate</a> is called
		 * to forward the DocumentEvent to the appropriate child views.
		 * <li><a href="#updateLayout">updateLayout</a> is called to
		 * give the view a chance to either repair it's layout, to reschedule
		 * layout, or do nothing.
		 * </ol>
		 *
		 * @param e the change information from the associated document
		 * @param a the current allocation of the view
		 * @param f the factory to use to rebuild if the view has children
		 * @see View#changedUpdate
		 */
		public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
	if (getViewCount() > 0) {
			Element elem = getElement();
			DocumentEvent.ElementChange ec = e.getChange(elem);

		Debug.trace("section view changed update");  //G***del; testing
		if(ec==null)  //G***testing; fix; comment; G***we don't need this test; the test is in the for loop
		{
			for(int i=elem.getElementCount()-1; ec==null && i>=0; --i)
			{
				final Element childElement=elem.getElement(i);  //G***testing
				if(childElement.getAttributes()!=null)  //G***del; we probably don't need this test
				{
					final String childElementLocalName=XMLStyleUtilities.getXMLElementLocalName(childElement.getAttributes());  //G***testing

		Debug.trace("child "+i+" local name: ", childElementLocalName);  //G***del; testing

					if("html".equals(childElementLocalName))  //G***testing
					{

						for(int j=childElement.getElementCount()-1; ec==null && j>=0; --j)
						{
							final Element grandchildElement=childElement.getElement(j);  //G***testing
							if(grandchildElement.getAttributes()!=null)  //G***del; we probably don't need this test
							{
								final String grandchildElementLocalName=XMLStyleUtilities.getXMLElementLocalName(grandchildElement.getAttributes());  //G***testing

					Debug.trace("grandchild "+i+" local name: ", grandchildElementLocalName);  //G***del; testing

								if("body".equals(grandchildElementLocalName))  //G***testing
								{
									ec=e.getChange(grandchildElement); //G***testing
								}
							}
						}
					}
				}
			}
		}

			if (ec != null) {
		if (! updateChildren(ec, e, f)) {
				// don't consider the element changes they
				// are for a view further down.
				ec = null;
		}
			}
			forwardUpdate(ec, e, a, f);
			updateLayout(ec, e, a);
	}
		}

}