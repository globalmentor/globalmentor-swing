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

import java.awt.Shape;
import java.util.*;
import javax.swing.event.*;
import javax.swing.text.*;

import static com.globalmentor.swing.text.rdf.RDFStyles.*;

import com.globalmentor.log.Log;
import com.globalmentor.swing.text.*;
import com.globalmentor.swing.text.xml.xhtml.XHTMLSwingText;
import com.globalmentor.w3c.spec.HTML;

/**
 * A view to represent an entire section. This view understands that its child elements represent XML document trees.
 * <p>
 * This implementation performs special processing on XHTML document, hiding content before and after the body element.
 * </p>
 * @author Garret Wilson
 */
public class XMLSectionView extends XMLBlockView {

	/**
	 * Constructs a seciton view expandable on the flowing (non-tiling) axis. The section tiles on the Y axis.
	 * @param element The element this view is responsible for.
	 */
	public XMLSectionView(final Element element) {
		super(element, Y_AXIS); //default to tiling on the Y axis TODO do we want to allow this to be specifie?
	}

	/**
	 * Sets the parent of the view. This version hides the entire hierarchy if the parent is being set to <code>null</code>, meaning that the view hierchy is
	 * being unloaded. (This assumes this view is the direct parent of the UI root view.
	 * @param parent The parent of the view, <code>null</code> if none.
	 * @see Views#hideView(View)
	 */
	public void setParent(final View parent) { //TODO maybe put this in some more primitive parent class
		super.setParent(parent); //set the parent normally
		if(parent == null) { //if this view is being uninstalled
			Views.hideView(this); //hide this entire view hierarchy (this is important for component views, for instance)			
		}
	}

	/**
	 * Loads all of the children to initialize the view. This is called by the <a href="#setParent">setParent</a> method. A block view knows that, should there be
	 * both inline and block children, the views for the inline children should not be created normally but should be wrapped in one or more anonymous views.
	 * Furthermore, inline views consisting only of whitespace will be given hidden views.
	 * @param viewFactory The view factory.
	 * @see CompositeView#setParent
	 */
	protected void loadChildren(final ViewFactory viewFactory) {
		if(viewFactory == null) //if there is no view factory, we can't load the children
			return; //we can't do anything
		final Element parentElement = getElement(); //get the parent element 
		final Element[] childElements = getSectionChildElements(parentElement, getStartOffset(), getEndOffset()); //get the child elements that fall within our range
		final View[] views = XMLBlockView.createBlockViews(parentElement, childElements, viewFactory); //create the child views, ensuring they are block elements
		//TODO del when works		final View[] views=createChildViews(getStartOffset(), getEndOffset(), viewFactory);
		replace(0, getViewCount(), views); //add the views as child views to this view pool
	}

	/**
	 * Returns all child elements for which views should be created in a section. If a section view holds multiple documents, for example, the children of those
	 * document elements will be included. An XHTML document, furthermore, will return the contents of its <code>&lt;body&gt;</code> element. It is assumed that
	 * the ranges precisely enclose any child elements within that range, so any elements that start within the given range will be included.
	 * @param sectionElement The element representing a section.
	 * @param startOffset This range's starting offset.
	 * @param endOffset This range's ending offset.
	 * @return An array of elements for which views should be created for a section.
	 */
	public static Element[] getSectionChildElements(final Element sectionElement, final int startOffset, final int endOffset) {
		final List<Element> elementList = new ArrayList<Element>(); //create a list in which to store the elements as we find them
		final int documentElementCount = sectionElement.getElementCount(); //find out how many child elements there are (representing XML documents)
		//look at each element representing an XML document, skipping the last dummy '\n' element, which does not represent a document
		//TODO fix; the dummy hierarchy is no longer present; find out why; perhaps because the old document creation algorithm removes it		for(int documentElementIndex=0; documentElementIndex<documentElementCount-1; ++documentElementIndex)
		for(int documentElementIndex = 0; documentElementIndex < documentElementCount; ++documentElementIndex) {
			final Element documentElement = sectionElement.getElement(documentElementIndex); //get a reference to this child element
			//if this document's range overlaps with our range
			if(documentElement.getStartOffset() < endOffset && documentElement.getEndOffset() > startOffset) {
				final AttributeSet documentAttributeSet = documentElement.getAttributes(); //get the attributes of the document element
				if(XMLStyles.isPageBreakView(documentAttributeSet)) { //if this is a page break element
					elementList.add(documentElement); //add this element to our list of elements; it's not a top-level document like the others TODO this is a terrible hack; fix
				} else if(getRDFResource(documentAttributeSet) != null) { //if this hierarchy represents a pseudo-XML representation of an RDF tree
					elementList.add(documentElement); //add this entire RDF tree TODO another hack; eventually, we'll probably add all elements and let the view factory worry about returning child views
				} else {
					//TODO del if not needed					else if(XHTMLSwingTextUtilities.isHTMLDocumentElement(documentAttributeSet))	//if this is an HTML document TODO this would probably go better in some XHTML-specific location
					//TODO del if not needed final boolean isHTMLDocument=XHTMLSwingTextUtilities.isHTMLDocumentElement(documentAttributeSet);	//see if this is an HTML document
					//TODO del if not needed				Element baseElement=documentElement;  //we'll find out which element to use as the parent; in most documents, that will be the document element; in HTML elements, it will be the <body> element
					final int childElementCount = documentElement.getElementCount(); //find out how many children are in the document
					for(int childIndex = 0; childIndex < childElementCount; ++childIndex) { //look at the children of the document element
						final Element childElement = documentElement.getElement(childIndex); //get a reference to the child element					  
						if(childElement.getStartOffset() < endOffset && childElement.getEndOffset() > startOffset) { //if this child element's range overlaps with our range
							final AttributeSet childAttributeSet = childElement.getAttributes(); //get the child element's attributes
							final String childElementLocalName = XMLStyles.getXMLElementLocalName(childAttributeSet); //get the child element local name
							Log.trace("Looking at child: ", childElementLocalName); //TODO del
							//if this element is an HTML <body> element 
							if(HTML.ELEMENT_BODY.equals(childElementLocalName) && XHTMLSwingText.isHTMLElement(childAttributeSet, documentAttributeSet)) {
								final int bodyChildElementCount = childElement.getElementCount(); //find out how many children the body element has
								for(int bodyChildIndex = 0; bodyChildIndex < bodyChildElementCount; ++bodyChildIndex) { //look at each of the body element's children
									Log.trace("Adding body child element: ", bodyChildIndex);
									final Element bodyChildElement = childElement.getElement(bodyChildIndex); //get this child element of the body element
									if(bodyChildElement.getStartOffset() >= startOffset && bodyChildElement.getEndOffset() < endOffset) { //if this child element falls within our range (we can't check for simply an overlap, because we have no way of breaking)
										elementList.add(bodyChildElement); //add this body child element to our list of elements
									}
								}
							} else { //if this element is not an XHTML <body> element
								elementList.add(childElement); //add this child element to our list of elements
							}
						}
					}
				}
			}
		}
		//TODO fix or del		elementList.add(sectionElement.getElement(documentElementCount-1));	//add the last element normally, as it is not a document at all but a dummy hierarchy added by Swing TODO test
		return (Element[])elementList.toArray(new Element[elementList.size()]); //return the views as an array of views
	}

	/**
	 * Updates the child views in response to receiving notification that the model changed, and there is change record for the element this view is responsible
	 * for. This is implemented to assume the child views are directly responsible for the child elements of the element this view represents. The ViewFactory is
	 * used to create child views for each element specified as added in the ElementChange, starting at the index specified in the given ElementChange. The number
	 * of child views representing the removed elements specified are removed.
	 *
	 * @param ec The change information for the element this view is responsible for. This should not be null if this method gets called.
	 * @param e the change information from the associated document
	 * @param f the factory to use to build child views
	 * @return whether or not the child views represent the child elements of the element this view is responsible for. Some views create children that represent
	 *         a portion of the element they are responsible for, and should return false. This information is used to determine if views in the range of the
	 *         added elements should be forwarded to or not.
	 * @see #insertUpdate
	 * @see #removeUpdate
	 * @see #changedUpdate
	 * @since 1.3
	 */
	protected boolean updateChildren(DocumentEvent.ElementChange ec, DocumentEvent e, ViewFactory f) {
		Log.trace("Section updating children"); //TODO fix
		//TODO fix ((XMLDocument)getElement().getDocument()).applyxStyles();  //TODO testing

		//TODO testing; brought up from View
		Element[] removedElems = ec.getChildrenRemoved();
		Log.trace("children removed: ", removedElems.length);
		Element[] addedElems = ec.getChildrenAdded();
		Log.trace("children added: ", addedElems.length);
		View[] added = null;
		if(addedElems != null) {
			added = new View[addedElems.length];
			for(int i = 0; i < addedElems.length; i++) {
				Log.trace("Creating added element: ", addedElems[i].getClass().getName()); //TODO del
				added[i] = f.create(addedElems[i]);
			}
		}
		int nremoved = 0;
		int index = ec.getIndex();

		//TODO del
		final String ourLocalName = XMLStyles.getXMLElementLocalName(getElement().getAttributes()); //TODO testing

		//TODO del
		final String elementLocalName = XMLStyles.getXMLElementLocalName(ec.getElement().getAttributes()); //TODO testing

		//TODO testing
		//as the section view can conflate the structure and place XHTML elements before the body, update our index accordingly
		//TODO del	final Element parentElement=ec.getElement().getParentElement();	//get the parent element
		//TODO del	final String parentElementLocalName=XMLStyleConstants.getXMLElementLocalName(parentElement.getAttributes());  //TODO testing
		if("body".equals(elementLocalName)) {
			final Element parentElement = ec.getElement().getParentElement(); //get the parent element
			//TODO del		final Element grandparentElement=parentElement.getParentElement();	//get the grandparent element
			final int extraElementCount = parentElement.getElementIndex(ec.getElement().getStartOffset()); //find out how many elements were added that are outside the body
			index += extraElementCount; //update the index to reflect the views before the body  
		}

		if(removedElems != null) {
			nremoved = removedElems.length;
		}
		replace(index, nremoved, added);
		return true;

		//TODO fix		  return super.updateChildren(ec, e, f); //TODO fix
	}

	/**
	 * Gives notification that something was inserted into the document in a location that this view is responsible for. To reduce the burden to subclasses, this
	 * functionality is spread out into the following calls that subclasses can reimplement:
	 * <ol>
	 * <li><a href="#updateChildren">updateChildren</a> is called if there were any changes to the element this view is responsible for. If this view has child
	 * views that are represent the child elements, then this method should do whatever is necessary to make sure the child views correctly represent the model.
	 * <li><a href="#forwardUpdate">forwardUpdate</a> is called to forward the DocumentEvent to the appropriate child views.
	 * <li><a href="#updateLayout">updateLayout</a> is called to give the view a chance to either repair it's layout, to reschedule layout, or do nothing.
	 * </ol>
	 *
	 * @param e the change information from the associated document
	 * @param a the current allocation of the view
	 * @param f the factory to use to rebuild if the view has children
	 * @see View#insertUpdate
	 */
	public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		if(getViewCount() > 0) {
			Element elem = getElement();
			DocumentEvent.ElementChange ec = e.getChange(elem);

			//TODO combine and tidy all this section-specific calculation---probably best to even make an XHTMLSectionView that rides on top of this and is created in a view factory

			Log.trace("section view insert update"); //TODO del; testing
			if(ec == null) { //TODO testing; fix; comment; TODO we don't need this test; the test is in the for loop
				for(int i = elem.getElementCount() - 1; ec == null && i >= 0; --i) {
					final Element childElement = elem.getElement(i); //TODO testing
					if(childElement.getAttributes() != null) { //TODO del; we probably don't need this test
						final String childElementLocalName = XMLStyles.getXMLElementLocalName(childElement.getAttributes()); //TODO testing

						Log.trace("child " + i + " local name: ", childElementLocalName); //TODO del; testing

						if("html".equals(childElementLocalName)) { //TODO testing

							for(int j = childElement.getElementCount() - 1; ec == null && j >= 0; --j) {
								final Element grandchildElement = childElement.getElement(j); //TODO testing
								if(grandchildElement.getAttributes() != null) { //TODO del; we probably don't need this test
									final String grandchildElementLocalName = XMLStyles.getXMLElementLocalName(grandchildElement.getAttributes()); //TODO testing

									Log.trace("grandchild " + i + " local name: ", grandchildElementLocalName); //TODO del; testing

									if("body".equals(grandchildElementLocalName)) { //TODO testing
										ec = e.getChange(grandchildElement); //TODO testing
									}
								}
							}
						}
					}
				}
			}

			if(ec != null) {
				if(!updateChildren(ec, e, f)) {
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
	 * Gives notification that something was removed from the document in a location that this view is responsible for. To reduce the burden to subclasses, this
	 * functionality is spread out into the following calls that subclasses can reimplement:
	 * <ol>
	 * <li><a href="#updateChildren">updateChildren</a> is called if there were any changes to the element this view is responsible for. If this view has child
	 * views that are represent the child elements, then this method should do whatever is necessary to make sure the child views correctly represent the model.
	 * <li><a href="#forwardUpdate">forwardUpdate</a> is called to forward the DocumentEvent to the appropriate child views.
	 * <li><a href="#updateLayout">updateLayout</a> is called to give the view a chance to either repair it's layout, to reschedule layout, or do nothing.
	 * </ol>
	 *
	 * @param e the change information from the associated document
	 * @param a the current allocation of the view
	 * @param f the factory to use to rebuild if the view has children
	 * @see View#removeUpdate
	 */
	public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		if(getViewCount() > 0) {
			Element elem = getElement();
			DocumentEvent.ElementChange ec = e.getChange(elem);

			Log.trace("section view remove update"); //TODO del; testing
			if(ec == null) { //TODO testing; fix; comment; TODO we don't need this test; the test is in the for loop
				for(int i = elem.getElementCount() - 1; ec == null && i >= 0; --i) {
					final Element childElement = elem.getElement(i); //TODO testing
					if(childElement.getAttributes() != null) { //TODO del; we probably don't need this test
						final String childElementLocalName = XMLStyles.getXMLElementLocalName(childElement.getAttributes()); //TODO testing

						Log.trace("child " + i + " local name: ", childElementLocalName); //TODO del; testing

						if("html".equals(childElementLocalName)) { //TODO testing

							for(int j = childElement.getElementCount() - 1; ec == null && j >= 0; --j) {
								final Element grandchildElement = childElement.getElement(j); //TODO testing
								if(grandchildElement.getAttributes() != null) { //TODO del; we probably don't need this test
									final String grandchildElementLocalName = XMLStyles.getXMLElementLocalName(grandchildElement.getAttributes()); //TODO testing

									Log.trace("grandchild " + i + " local name: ", grandchildElementLocalName); //TODO del; testing

									if("body".equals(grandchildElementLocalName)) { //TODO testing
										ec = e.getChange(grandchildElement); //TODO testing
									}
								}
							}
						}
					}
				}
			}

			if(ec != null) {
				if(!updateChildren(ec, e, f)) {
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
	 * Gives notification from the document that attributes were changed in a location that this view is responsible for. To reduce the burden to subclasses, this
	 * functionality is spread out into the following calls that subclasses can reimplement:
	 * <ol>
	 * <li><a href="#updateChildren">updateChildren</a> is called if there were any changes to the element this view is responsible for. If this view has child
	 * views that are represent the child elements, then this method should do whatever is necessary to make sure the child views correctly represent the model.
	 * <li><a href="#forwardUpdate">forwardUpdate</a> is called to forward the DocumentEvent to the appropriate child views.
	 * <li><a href="#updateLayout">updateLayout</a> is called to give the view a chance to either repair it's layout, to reschedule layout, or do nothing.
	 * </ol>
	 *
	 * @param e the change information from the associated document
	 * @param a the current allocation of the view
	 * @param f the factory to use to rebuild if the view has children
	 * @see View#changedUpdate
	 */
	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		if(getViewCount() > 0) {
			Element elem = getElement();
			DocumentEvent.ElementChange ec = e.getChange(elem);

			Log.trace("section view changed update"); //TODO del; testing
			if(ec == null) { //TODO testing; fix; comment; TODO we don't need this test; the test is in the for loop
				for(int i = elem.getElementCount() - 1; ec == null && i >= 0; --i) {
					final Element childElement = elem.getElement(i); //TODO testing
					if(childElement.getAttributes() != null) { //TODO del; we probably don't need this test
						final String childElementLocalName = XMLStyles.getXMLElementLocalName(childElement.getAttributes()); //TODO testing

						Log.trace("child " + i + " local name: ", childElementLocalName); //TODO del; testing

						if("html".equals(childElementLocalName)) { //TODO testing

							for(int j = childElement.getElementCount() - 1; ec == null && j >= 0; --j) {
								final Element grandchildElement = childElement.getElement(j); //TODO testing
								if(grandchildElement.getAttributes() != null) { //TODO del; we probably don't need this test
									final String grandchildElementLocalName = XMLStyles.getXMLElementLocalName(grandchildElement.getAttributes()); //TODO testing

									Log.trace("grandchild " + i + " local name: ", grandchildElementLocalName); //TODO del; testing

									if("body".equals(grandchildElementLocalName)) { //TODO testing
										ec = e.getChange(grandchildElement); //TODO testing
									}
								}
							}
						}
					}
				}
			}

			if(ec != null) {
				if(!updateChildren(ec, e, f)) {
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