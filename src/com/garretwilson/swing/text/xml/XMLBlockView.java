package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.SizeRequirements;

import com.garretwilson.lang.CharSequenceUtilities;
import com.garretwilson.swing.text.AnonymousElement;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;	//G***maybe change to XMLStyleConstants
import com.garretwilson.swing.text.xml.css.XMLCSSView;
import com.garretwilson.swing.text.xml.css.XMLCSSViewPainter;
import com.garretwilson.text.CharacterConstants;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSConstants;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSStyleDeclaration;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSUtilities;
import com.garretwilson.util.Debug;
import org.w3c.dom.css.CSSStyleDeclaration;

/**A view that arranges its children into a box shape by tiling its children
along an axis, with no constraints.
This class is a extension, rewrite, and bug fix of javax.swing.text.BoxView
by Timothy Prinzing version 1.44 02/02/00 and is based on code from that class.
@author Garret Wilson
@see javax.swing.text.BoxView
*/
public class XMLBlockView extends com.garretwilson.swing.text.CompositeView implements XMLCSSView	//G***newswing
{

	/**Whether we're allowed to expand horizontally.*/
	protected final boolean isExpandX;

	/**Whether we're allowed to expand vertically.*/
	protected final boolean isExpandY;

	/**Constructs an XMLBlockView expandable on the flowing (non-tiling) axis.
		@param elem The element this view is responsible for.
		@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	*/
	public XMLBlockView(Element elem, int axis)
	{
		this(elem, axis, axis!=X_AXIS, axis!=Y_AXIS); //default to not flowing on the tiling axis
	}

	/**Constructs an XMLBlockView, specifying whether the view should be allowed
		to expand to a maximum size for the given axes.
	@param elem The element this view is responsible for.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	*/
	public XMLBlockView(Element elem, int axis, final boolean expandX, final boolean expandY)
	{
	super(elem);
	tempRect = new Rectangle();
	this.axis = axis;
	xOffsets = new int[0];
	xSpans = new int[0];
	xValid = false;
	xAllocValid = false;
	yOffsets = new int[0];
	ySpans = new int[0];
	yValid = false;
	yAllocValid = false;
	isExpandX=expandX;  //set whether we're allowed to expand horizontally
	isExpandY=expandY;  //set whether we're allowed to expand vertically
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
		final View[] createdViews=createBlockElementChildViews(getElement(), viewFactory);  //create the child views
		replace(0, 0, createdViews);  //load our created views as children
/*G***del when works
Debug.traceStack(); //G***del
Debug.trace("Loading children for block element: ", XMLCSSStyleConstants.getXMLElementName(getElement().getAttributes()));  //G***del
Debug.trace("Block element has attributes: ", com.garretwilson.swing.text.AttributeSetUtilities.getAttributeSetString(getElement().getAttributes()));  //G***del
		if(viewFactory==null) //if there is no view factory, the parent view has somehow changed
			return; //don't create children
		final Document document=getDocument();  //get a reference to our document
		final Element element=getElement(); //get a reference to the element we represent
		final AttributeSet attributeSet=getAttributes();  //get our attributes
		final CSSStyleDeclaration cssStyle=XMLCSSStyleConstants.getXMLCSSStyle(attributeSet); //get the CSS style of the element

//G***we probably don't need to pre-check -- we can just check as we go, because the same view structure should be created either way

		//G***do we even need to check for block children? probably remove all the block children code, because if we didn't have block children and we had only inline views, we'd be a paragraph view
		boolean hasBlockChildren=false, hasInlineChildren=false;	//check to see if there are block and/or inline children
		for(int childIndex=element.getElementCount()-1; childIndex>=0 && !(hasBlockChildren && hasInlineChildren); --childIndex) //look at each child element (stop looking when we've found both block and inline child nodes)
		{
			final Element childElement=element.getElement(childIndex);  //get a reference to this child element
			final CSSStyleDeclaration childCSSStyle=XMLCSSStyleConstants.getXMLCSSStyle(childElement.getAttributes()); //get the CSS style of the element (this method make sure the attributes are present)
				//if this element is inline (text is always inline, regardless of what the display property says)
			if(XMLCSSUtilities.isDisplayInline(childCSSStyle) || AbstractDocument.ContentElementName.equals(childElement.getName()))
				hasInlineChildren=true;	//show that there are inline children
			else	//if this isn't inline, we'll assume it's some sort of block element
				hasBlockChildren=true;	//show that there are block children
		}

//G***del		final boolean isBlockElement=!XMLCSSUtilities.isDisplayInline(cssStyle);	//see if this is a block-level element
Debug.trace("Element has block children: "+hasBlockChildren); //G***del
Debug.trace("Element has inline children: "+hasInlineChildren); //G***del
		if(hasInlineChildren)	//if this element has both block and inline children, we'll have to create some anonymous blocks
		{
			final List childViewList=new ArrayList(element.getElementCount());  //create a list in which to store elements, knowing that we won't have more views than child elements
//G***del when works			XMLCSSSimpleAttributeSet anonymousAttributeSet=null;	//we'll create an anonymous attribute set each time we create and anonymous block
		  final Element[] inlineChildElements=new Element[element.getElementCount()]; //create an array of anonymous child elements; we know we'll never have more inline child elements than there are children of the original element
		  int inlineChildElementCount=0;  //show that we haven't started collecting inline child elements, yet
		  SimpleAttributeSet anonymousAttributeSet=null;	//we'll create an anonymous attribute set for each anonymous box when needed
//G***del		  Element[] inlineChildElements;  //we'll create an array of anonymous child elements when needed
//G***del		  AnonymousElement anonymousElement=null;  //we'll create an anonymous element when needed
//G***del			SimpleAttributeSet anonymousAttributeSet;	//we'll create an anonymous attribute set each time we create and anonymous block
//G***del			boolean buildingAnonymousElement=false;	//show that we haven't started started any anonymous blocks yet
		  final int childElementCount=element.getElementCount();  //find out how many child elements there are
			for(int childIndex=0; childIndex<childElementCount; ++childIndex) //look at each child element
			{
				final Element childElement=element.getElement(childIndex);  //get a reference to this child element
				final CSSStyleDeclaration childCSSStyle=XMLCSSStyleConstants.getXMLCSSStyle(childElement.getAttributes()); //get the CSS style of the element (this method make sure the attributes are present)
					//see if this child element is inline (text is always inline, regardless of what the display property says)
				final boolean childIsInline=XMLCSSUtilities.isDisplayInline(childCSSStyle) || AbstractDocument.ContentElementName.equals(childElement.getName());
				if(childIsInline) //if this is an inline child element
				{
					try
					{
	//G***bring back for efficiency					final Segment segment=new Segment();  //create a new segment to receive test
						//get the text of this inline child; if it is just whitespace, we'll create a hidden view for it
						final String text=document.getText(childElement.getStartOffset(), childElement.getEndOffset()-childElement.getStartOffset());
Debug.trace("Looking at inline text: '"+text+"' character code: "+Integer.toHexString(text.charAt(0)));  //G***del
	//G***bring back for efficiency				  document.getText(childElement.getStartOffset(), childElement.getEndOffset()-childElement.getStartOffset(), segment);
						if(text.trim().length()==0) //if there is nothing but whitespace in this inline element
						{
Debug.trace("found whitespace inside element: ", XMLStyleConstants.getXMLElementName(attributeSet)); //G***del
							if(inlineChildElementCount>0)	//if we've started but not finished an anonymous block, yet G***try to combine all these identical code sections
							{
									//create an anonymous element with the elements we've collected
								final Element anonymousElement=new AnonymousElement(element, anonymousAttributeSet, inlineChildElements, 0, inlineChildElementCount);
								childViewList.add(viewFactory.create(anonymousElement)); //create a view for the anonymous element and add the view to our list
								inlineChildElementCount=0;  //show that we're not building an anonymous element anymore
		//G***del						anonymousElement=null; //show that we're not building an anonymous element anymore
							}
							//G***fix: this does not currently compensate for elements like <pre>
//G***del; testing							childViewList.add(new XMLParagraphView(childElement));  //G***testing
							childViewList.add(new XMLHiddenView(childElement));  //create a hidden view for the whitespace inline element and add it to our list of views
						}
						else  //if there's more than whitespace here
						{
							if(inlineChildElementCount==0) //if we haven't started building an anonymous element, yet
							{
								anonymousAttributeSet=new SimpleAttributeSet();	//create an anonymous attribute set for this anonymous box
		//G***del when works						anonymousAttributeSet=new XMLCSSSimpleAttributeSet();	//create an anonymous attribute set for this anonymous box
		//G***fix						XMLCSSStyleConstants.setXMLElementName(anonymousAttributeSet, XMLStyleConstants.AnonymousNameValue);	//show by its name that this is an anonymous box
								XMLStyleConstants.setXMLElementName(anonymousAttributeSet, XMLCSSStyleConstants.AnonymousAttributeValue); //show by its name that this is an anonymous box G***maybe change this to setAnonymous
								final XMLCSSStyleDeclaration anonymousCSSStyle=new XMLCSSStyleDeclaration(); //create a new style declaration
								anonymousCSSStyle.setDisplay(XMLCSSConstants.CSS_DISPLAY_BLOCK);	//show that the anonymous element should be a block element
								XMLCSSStyleConstants.setXMLCSSStyle(anonymousAttributeSet, anonymousCSSStyle);	//store the constructed CSS style in the attribute set
		//G***del						anonymousAttributeSet.addAttribute(StyleConstants.NameAttribute, XMLCSSStyleConstants.AnonymousAttributeValue);	//show by its name that this is an anonymous box G***maybe change this to setAnonymous
		//G***del if not needed						XMLCSSStyleConstants.setParagraphView(anonymousAttributeSet, true);	//show that the anonymous block should be a paragraph view
		//G***del						elementSpecList.add(new DefaultStyledDocument.ElementSpec(anonymousAttributeSet, DefaultStyledDocument.ElementSpec.StartTagType));	//create the beginning of an anonyous block element
		//G***del when works						anonymousElement=new AnonymousElement(element, anonymousAttributeSet); //create an anonymous element
							}
							inlineChildElements[inlineChildElementCount++]=childElement;  //add the child element to the anonymous element and show that we've collected another one
						}
					}
					catch(BadLocationException badLocationException)  //if we tried to access an invalid location (this shouldn't happen unless there are problems internal to an element)
					{
						Debug.error(badLocationException);  //report the error
					}
//G***del					anonymousElement.add(childElement); //add the child element to the anonymous element
				}
				else  //if this is a block element
				{
					if(inlineChildElementCount>0)	//if we've started but not finished an anonymous block, yet
					{
						  //create an anonymous element with the elements we've collected
						final Element anonymousElement=new AnonymousElement(element, anonymousAttributeSet, inlineChildElements, 0, inlineChildElementCount);
						childViewList.add(viewFactory.create(anonymousElement)); //create a view for the anonymous element and add the view to our list
						inlineChildElementCount=0;  //show that we're not building an anonymous element anymore
//G***del						anonymousElement=null; //show that we're not building an anonymous element anymore
					}
					childViewList.add(viewFactory.create(childElement)); //create a view normally for the child element and add the view to our list
				}
			}
			if(inlineChildElementCount>0)	//if we started an anonymous block but never finished it (i.e. the last child was inline)
			{
					//create an anonymous element with the elements we've collected
				final Element anonymousElement=new AnonymousElement(element, anonymousAttributeSet, inlineChildElements, 0, inlineChildElementCount);
				childViewList.add(viewFactory.create(anonymousElement)); //create a view for the anonymous element and add the view to our list
				inlineChildElementCount=0;  //show that we're not building an anonymous element anymore
			}
			if(childViewList.size()>0)  //if we have views to add (we always should, because we got here by finding inline children with block children)
			{
				final View[] addedViews=(View[])childViewList.toArray(new View[childViewList.size()]);  //convert the list of views to an array
				replace(0, 0, addedViews);  //load our created views as children
			}
		}
		else	//if we don't have any inline children, we can load the children normally
			super.loadChildren(viewFactory);  //let the parent class load the children
*/
	}







	/**Creates child views of a block element.
		This is called by the <a href="#loadChildren">loadChildren</a> method as
		well as other block-like classes such as tables.
		Inline children will not be created normally but will be wrapped in one or
		more anonymous views. Inline views consisting only of whitespace will be
		given hidden views.
	@param element The element representing a block view.
//G***del	@param attributeSet The attributes of the element, which may or may not be the
		same as <code>element.getAttributes()</code>.
	@param viewFactory The factory used to create child views.
	@return An array of created child views, which may be empty if there are no
		child elements or an invalid view factory was passed.
	@see CompositeView#setParent
	@see #loadChildren
	*/
	public static View[] createBlockElementChildViews(final Element element, /*G***del final AttributeSet attributeSet, */final ViewFactory viewFactory)
	{
/*G***del
Debug.traceStack(); //G***del
Debug.trace("Loading children for block element: ", XMLCSSStyleConstants.getXMLElementName(element.getAttributes()));  //G***del
Debug.trace("Block element has attributes: ", com.garretwilson.swing.text.AttributeSetUtilities.getAttributeSetString(element.getAttributes()));  //G***del
*/

		if(viewFactory!=null) //if we have a view factory
		{
			final Document document=element.getDocument();  //get a reference to the document
			final Element[] childElements=getChildElementsAsBlockElements(element);	//get the child elements, making sure they are all block elements
			final int childElementCount=childElements.length;  //find out how many child elements there are
			if(childElementCount>0)	//if there are child elements
			{
				final List childViewList=new ArrayList(childElementCount);  //create a list in which to store elements, knowing that we won't have more views than child elements
				for(int i=0; i<childElementCount; ++i)	//look at each of the child elements
				{
					final Element childElement=childElements[i];	//get a reference to his child element
					final AttributeSet childAttributeSet=childElement.getAttributes();	//get the attribute set of this child
					final String elementName=XMLStyleUtilities.getXMLElementName(childAttributeSet); //get the child element's name G***maybe later change this to isAnonymous()
/*G***fix
					//see if this is the dummy ending '\n' hierarchy added by Swing 
			if(AbstractDocument.ParagraphElementName.equals(childElement.getName()))	//if this is is a generic paragraph element
			{
				final Element parentElement=element.getParentElement();	//get the element's parent
				if(AbstractDocument.SectionElementName.equals(parentElement.getName()))	//if this element is a direct child of the section
				{
						//if this element is the last child element of the section element, it's the dummy '\n' element---create a hidden view for it
					if(parentElement.getElementCount()>0 && parentElement.getElement(parentElement.getElementCount()-1)==childElement)
					{
//G****testing
childViewList.add(new XMLHiddenView(childElement));	//create a hidden view to hide the dummy ending '\n' hierarchy added by Swing
						continue;	//skip further processing of this child and go to the next one
					}
				}
			}
*/

//TODO comment all this in-depth
				if(i==0 && childElementCount==2)	//if there are only two child elements, see if the last one is the implied "\n" 
				{
					if("$implied".equals(XMLStyleUtilities.getXMLElementName(childElements[childElementCount-1].getAttributes())))	//G***use a constant
					{
						childViewList.add(viewFactory.create(childElement)); //create a view normally for the child element, even if it's just whitespace
						continue;	//G***comment						
					}
				}

					if(childElementCount>1 && XMLCSSStyleUtilities.AnonymousAttributeValue.equals(elementName))	//if this is an anonymous element, but it's not the only child element
					{
						try
						{
							final String text=document.getText(childElement.getStartOffset(), childElement.getEndOffset()-childElement.getStartOffset());
							//G***bring back for efficiency				  document.getText(childElement.getStartOffset(), childElement.getEndOffset()-childElement.getStartOffset(), segment);
									//if there are no visible characters (or the end-of-element character mark), and this isn't really just an empty element
							if(CharSequenceUtilities.notCharIndexOf(text, CharacterConstants.WHITESPACE_CHARS+CharacterConstants.CONTROL_CHARS+XMLDocument.ELEMENT_END_CHAR)<0	
									&& !XMLStyleUtilities.isXMLEmptyElement(childAttributeSet))
							{
		//G***del Debug.trace("found whitespace inside element: ", XMLStyleConstants.getXMLElementName(attributeSet)); //G***del
								childViewList.add(new XMLHiddenView(childElement));  //create a hidden view for the whitespace inline elements and add it to our list of views
								continue;	//skip further processing of this child and go to the next one								
		//G***fix	childViewList.add(viewFactory.create(childElement)); //create a view normally for the child element and add the view to our list
		//G***fix							childViewList.add(new XMLParagraphView(childElement));  //G***testing
							}
						}
						catch(BadLocationException badLocationException)  //if we tried to access an invalid location (this shouldn't happen unless there are problems internal to an element)
						{
							Debug.error(badLocationException);  //report the error
						}
					}

					childViewList.add(viewFactory.create(childElement)); //create a view normally for the child element and add the view to our list						
/*G***fix
					else	//if this isn't an anonymous element
					{
						childViewList.add(viewFactory.create(childElement)); //create a view normally for the child element and add the view to our list						
					}
*/
				}
				return (View[])childViewList.toArray(new View[childViewList.size()]);  //convert the list of views to an array
			}
		}
		return new View[]{}; ////if there is no view factory (the parent view has somehow changed) or no elements, just return an empty array of views
	}









	/**Gathers child elements of a block element, ensuring that each child element
		is a block element.
		Inline child elements will be wrapped in one or more anonymous views.
	@param element The element representing a block view.
	@return A list of child elements, which may be empty if there are no
		child elements.
	@see CompositeView#setParent
	@see #loadChildren
	*/
	public static Element[] getChildElementsAsBlockElements(final Element element)
	{
		final Document document=element.getDocument();  //get a reference to the document
//G***del		final CSSStyleDeclaration cssStyle=XMLCSSStyleConstants.getXMLCSSStyle(attributeSet); //get the CSS style of the element
		final int childElementCount=element.getElementCount();  //find out how many child elements there are
		//TODO maybe just send back a NO_ELEMENTS constant if there are no children
		final List childElementList=new ArrayList(childElementCount);  //create a list in which to store elements, knowing that we won't have more views than child elements
		final List inlineChildElementList=new ArrayList(childElementCount);  //create a list in which to store inline child elements; we know we'll never have more inline child elements than there are children of the original element
		for(int childIndex=0; childIndex<childElementCount; ++childIndex) //look at each child element
		{
			final Element childElement=element.getElement(childIndex);  //get a reference to this child element
/*G***fix
					//see if this is the dummy ending '\n' hierarchy added by Swing 
			if(AbstractDocument.ParagraphElementName.equals(childElement.getName()))	//if this is is a generic paragraph element
			{
				final Element parentElement=element.getParentElement();	//get the element's parent
				if(AbstractDocument.SectionElementName.equals(parentElement.getName()))	//if this element is a direct child of the section
				{
						//if this element is the last child element of the section element, it's the dummy '\n' element---create a hidden view for it
					if(parentElement.getElementCount()>0 && parentElement.getElement(parentElement.getElementCount()-1)==childElement)
					{
//G***testing							childViewList.add(new XMLHiddenView(childElement));	//create a hidden view to hide the dummy ending '\n' hierarchy added by Swing
//G***fix							childViewList.add(new XMLParagraphView(childElement));	//G***testing
						if(inlineChildElementList.size()>0)	//if we've started but not finished an anonymous block, yet
							childElementList.add(createAnonymousBlockElement(element, inlineChildElementList));	//create an anonymous block element and clear the list
childElementList.add(childElement);	//G***testing
//G***del childViewList.add(new XMLHiddenView(childElement));	//create a hidden view to hide the dummy ending '\n' hierarchy added by Swing
//G***fix							childViewList.add(new XMLBlockView(childElement, View.Y_AXIS));	//G***testing
						continue;	//skip further processing of this child and go to the next one
					}
				}
			}
*/

					//see if this is the dummy ending '\n' hierarchy added by Swing 
			if(AbstractDocument.ParagraphElementName.equals(childElement.getName()))	//if this is is a generic paragraph element
			{
				final Element parentElement=element.getParentElement();	//get the element's parent
				if(AbstractDocument.SectionElementName.equals(parentElement.getName()))	//if this element is a direct child of the section
				{
						//if this element is the last child element of the section element, it's the dummy '\n' element---create a hidden view for it
					if(parentElement.getElementCount()>0 && parentElement.getElement(parentElement.getElementCount()-1)==childElement)
					{
//G***testing							childViewList.add(new XMLHiddenView(childElement));	//create a hidden view to hide the dummy ending '\n' hierarchy added by Swing
//G***fix							childViewList.add(new XMLParagraphView(childElement));	//G***testing
						if(inlineChildElementList.size()>0)	//if we've started but not finished an anonymous block, yet
							childElementList.add(createAnonymousBlockElement(element, inlineChildElementList));	//create an anonymous block element and clear the list
						inlineChildElementList.add(childElement);	//G***testing
						childElementList.add(createAnonymousBlockElement(element, inlineChildElementList));	//create an anonymous block element and clear the list

//G***del childViewList.add(new XMLHiddenView(childElement));	//create a hidden view to hide the dummy ending '\n' hierarchy added by Swing
//G***fix							childViewList.add(new XMLBlockView(childElement, View.Y_AXIS));	//G***testing
						continue;	//skip further processing of this child and go to the next one
					}
				}
			}


//G***del Debug.trace("looking at block view child: ", XMLCSSStyleConstants.getXMLElementLocalName(childElement.getAttributes()));  //G***del
			final AttributeSet childAttributeSet=childElement.getAttributes();	//get the attributes of the child element
			final CSSStyleDeclaration childCSSStyle=XMLCSSStyleUtilities.getXMLCSSStyle(childAttributeSet); //get the CSS style of the element (this method make sure the attributes are present)
				//see if this child element is inline (text is always inline, regardless of what the display property says)
			final boolean childIsInline=XMLCSSUtilities.isDisplayInline(childCSSStyle) || AbstractDocument.ContentElementName.equals(childElement.getName());
			if(childIsInline) //if this is an inline child element
			{
				inlineChildElementList.add(childElement);  //add the child element to the inline element list
			}
			else  //if this is a block element
			{
				if(inlineChildElementList.size()>0)	//if we've started but not finished an anonymous block, yet
					childElementList.add(createAnonymousBlockElement(element, inlineChildElementList));	//create an anonymous block element and clear the list
				childElementList.add(childElement); //add the child element normally
			}
		}
		if(inlineChildElementList.size()>0)	//if we started an anonymous block but never finished it (i.e. the last child was inline)
			childElementList.add(createAnonymousBlockElement(element, inlineChildElementList));	//create an anonymous block element and clear the list
		return (Element[])childElementList.toArray(new Element[childElementList.size()]);  //convert the list of elements to an array
	}









	/*Creates an anonymous view representing the given child elements.
	<p>All elements are removed from the collection after the anonymous view has
		been created.</p>
	<p>If no child elements are visible, a hidden view will be created instead.</p> 
	@param parentElement The parent element of which this element owns a subset of child
		views.
	@param childElementCollection The collection of elements to be a child of the
		anonymous view.
	@param viewFactory The view factory used to create the anonymous view.
	*/
	protected static View createAnonymousBlockView(final Element parentElement, final Collection childElementCollection, final ViewFactory viewFactory)
	{
		final Element anonymousElement=createAnonymousBlockElement(parentElement, childElementCollection);	//create an anonymous element from the child elements, removing them from the collection
		return viewFactory.create(anonymousElement); //create a view for the anonymous element and return that view, discarding our reference to the element
	}

	/*Creates an anonymous element representing the given child elements.
	<p>All elements are removed from the collection after the anonymous element
		has been created.</p>
	<p>If no child elements are visible, the anonymous element will be marked
		as hidden.</p> 
	@param parentElement The parent element of which this element owns a subset
		of child views.
	@param childElementCollection The collection of elements to be a child of the
		anonymous element.
	*/
	protected static Element createAnonymousBlockElement(final Element parentElement, final Collection childElementCollection)
	{
		final MutableAttributeSet anonymousAttributeSet=new SimpleAttributeSet();	//create an anonymous attribute set for this anonymous box
		XMLStyleUtilities.setXMLElementName(anonymousAttributeSet, XMLCSSStyleUtilities.AnonymousAttributeValue); //show by its name that this is an anonymous box G***maybe change this to setAnonymous
		final XMLCSSStyleDeclaration anonymousCSSStyle=new XMLCSSStyleDeclaration(); //create a new style declaration
		anonymousCSSStyle.setDisplay(XMLCSSConstants.CSS_DISPLAY_BLOCK);	//show that the anonymous element should be a block element
		XMLCSSStyleUtilities.setXMLCSSStyle(anonymousAttributeSet, anonymousCSSStyle);	//store the constructed CSS style in the attribute set
				//put the child elements into an array
		final Element[] childElements=(Element[])childElementCollection.toArray(new Element[childElementCollection.size()]);
		boolean isHidden=true;	//this anonymous view should be hidden unless we find at least one visible view




		if(childElements.length==1)	//G***testing
		{
			final Element childElement=childElements[0];
					//see if this is the dummy ending '\n' hierarchy added by Swing 
			if(AbstractDocument.ParagraphElementName.equals(childElement.getName()))	//if this is is a generic paragraph element
			{
				final Element parentParentElement=parentElement.getParentElement();	//get the element's parent
				if(AbstractDocument.SectionElementName.equals(parentParentElement.getName()))	//if this element is a direct child of the section
				{
						//if this element is the last child element of the section element, it's the dummy '\n' element---create a hidden view for it
					if(parentParentElement.getElementCount()>0 && parentParentElement.getElement(parentParentElement.getElementCount()-1)==childElement)
					{
						XMLStyleUtilities.setXMLElementName(anonymousAttributeSet, "$implied");	//G***fix; use a constant XMLCSSStyleUtilities.AnonymousAttributeValue); //show by its name that this is an anonymous box G***maybe change this to setAnonymous
						isHidden=false;	//G***testing
					}
				}
			}
		}







		for(int i=childElements.length-1; isHidden && i>=0; --i)	//see if any child elements are visible; stop when we find a visible one
		{
			final Element childElement=childElements[i];	//get a reference to this child element
			final AttributeSet childAttributeSet=childElement.getAttributes();	//get the child element attributes
				//if this child element doesn't have a CSS display of "none", it's visible unless something else (the editor kit, for instance) specified it to be hidden 
			if(!XMLCSSConstants.CSS_DISPLAY_NONE.equals(XMLCSSStyleUtilities.getDisplay(childAttributeSet)))
			{
				if(XMLStyleUtilities.isVisible(childAttributeSet))	//if we haven't for some reason we've explicitly set this view to be hidden
				{
					isHidden=false;	//we've found a visible child, so we can't make the anonymous element hidden
//G***del when works					break;	//stop looking for visible children
				} 
			}
		}
		if(isHidden)	//if no child elements are visible
		{
			XMLStyleUtilities.setVisible(anonymousAttributeSet, false);	//hide the anonymous element
		}
			//create an anonymous element with the elements we've collected
		final Element anonymousElement=new AnonymousElement(parentElement, anonymousAttributeSet, childElementCollection);
		childElementCollection.clear();	//remove all the child elements from the collection
		return anonymousElement;	//return the anonymous element we created
	}



	/**Invalidates the layout and asks the container to repaint itself.
	This is a convenience function for <code>layoutChanged()</code> and
	<code>Component.repaint()</code>
	@see #layoutChanged
	@see Container#repaint
	*/
	public void relayout()
	{
		layoutChanged(X_AXIS);	//invalidate our horizontal axis
		layoutChanged(Y_AXIS);	//invalidate our vertical axis
//G***del if not needed		setParent(getParent()); //G***testing
//G***del		strategy.insertUpdate( this, null, null );
//G***fix; doesn't work		changedUpdate(null, null, getViewFactory());  //send a synthetic changeUpdate() so that all the children and layout strategies can get a chance to reinitialize
		final Container container=getContainer();	//get a reference to our container
		if(container!=null)	//if we're in a container
			container.repaint();	//repaint our container
	}

    /**
     * Fetch the axis property.
     *
     * @return the major axis of the box, either
     *  View.X_AXIS or View.Y_AXIS.
     */
    public int getAxis() {
	return axis;
    }

    /**
     * Set the axis property.
     *
     * @param axis either View.X_AXIS or View.Y_AXIS
     */
    public void setAxis(int axis) {
	this.axis = axis;
	preferenceChanged(null, true, true);
    }

    /**
     * Invalidate the layout along an axis.  This happens
     * automatically if the preferences have changed for
     * any of the child views.  In some cases the layout
     * may need to be recalculated when the preferences
     * have not changed.  The layout can be marked as
     * invalid by calling this method.  The layout will
     * be updated the next time the setSize method is called
     * on this view (typically in paint).
     *
     * @param axis either View.X_AXIS or View.Y_AXIS
     */
    public void layoutChanged(int axis) {
//G***del Debug.stackTrace(); //G***del
/*G***del
if(this instanceof XMLPagedView)  //G***del
{
	Debug.traceStack();
}
*/
//G***del Debug.trace(getClass().getName());  //G***del
 	if (axis == X_AXIS) {
 	    xAllocValid = false;
 	} else {
 	    yAllocValid = false;
 	}
    }

    /**
     * Paints a child.  By default
     * that is all it does, but a subclass can use this to paint
     * things relative to the child.
     *
     * @param g the graphics context
     * @param alloc the allocated region to paint into
     * @param index the child index, >= 0 && < getViewCount()
     */
    protected void paintChild(Graphics g, Rectangle alloc, int index) {


/*G***find out why we're getting a nullpointerexception when the child tries to get its parent
if(Debug.isDebug() && getAttributes()!=null)  //G***del
	Debug.trace("Inside XMLBlockView.paintChild(), name: "+getAttributes().getAttribute(StyleConstants.NameAttribute));	//G***del
*/

	View child = getView(index);

//G***del Debug.trace("XMLBlockView.paintChild() child "+index+" parent: "+child.getParent()); //G***testing

/*G***find out why the views have no parent to begin with
if(child.getParent()==null) //G***testing
	child.setParent(this);  //G***testing
*/

	child.paint(g, alloc);
    }

    /**
     * Invalidates the layout and resizes the cache of
     * requests/allocations.  The child allocations can still
     * be accessed for the old layout, but the new children
     * will have an offset and span of 0.
     *
     * @param index the starting index into the child views to insert
     *   the new views.  This should be a value >= 0 and <= getViewCount.
     * @param length the number of existing child views to remove.
     *   This should be a value >= 0 and <= (getViewCount() - offset).
     * @param views the child views to add.  This value can be null
     *   to indicate no children are being added (useful to remove).
     */
    public void replace(int index, int length, View[] elems) {
	super.replace(index, length, elems);
/*G***del
if(this instanceof XMLPagedView)  //G***del
{
	Debug.traceStack();
}
*/

	// invalidate cache
	int nInserted = (elems != null) ? elems.length : 0;
	xOffsets = updateLayoutArray(xOffsets, index, nInserted);
	xSpans = updateLayoutArray(xSpans, index, nInserted);
	xValid = false;
	xAllocValid = false;
	yOffsets = updateLayoutArray(yOffsets, index, nInserted);
	ySpans = updateLayoutArray(ySpans, index, nInserted);
	yValid = false;
	yAllocValid = false;
    }

    /**
     * Resize the given layout array to match the new number of
     * child views.  The current number of child views are used to
     * produce the new array.  The contents of the old array are
     * inserted into the new array at the appropriate places so that
     * the old layout information is transferred to the new array.
     */
    int[] updateLayoutArray(int[] oldArray, int offset, int nInserted) {
Debug.trace("updating layout array with inserted: ", nInserted);  //G***del

	int n = getViewCount();
Debug.trace("view count: ", n);  //G***del
	int[] newArray = new int[n];

	System.arraycopy(oldArray, 0, newArray, 0, offset);
	System.arraycopy(oldArray, offset,
			 newArray, offset + nInserted, n - nInserted - offset);
	return newArray;
    }

    /**
     * Forward the given DocumentEvent to the child views
     * that need to be notified of the change to the model.
     * If a child changed it's requirements and the allocation
     * was valid prior to forwarding the portion of the box
     * from the starting child to the end of the box will
     * be repainted.
     *
     * @param ec changes to the element this view is responsible
     *  for (may be null if there were no changes).
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see #insertUpdate
     * @see #removeUpdate
     * @see #changedUpdate
     */
    protected void forwardUpdate(DocumentEvent.ElementChange ec,
				     DocumentEvent e, Shape a, ViewFactory f) {
Debug.trace("forward updating class: ", getClass().getName());  //G***del
//G***del	setPropertiesFromAttributes();	//reload our cached properties from the attributes (newswing)
	cacheSynchronized=false;	//show that our cached values need to be reloaded (newswing) G***make an accessor function to do this, like invalidateChache
	boolean wasValid = isAllocationValid();
	super.forwardUpdate(ec, e, a, f);

	// determine if a repaint is needed
	if (wasValid && (! isAllocationValid())) {
	    // repaint is needed, if there is a hosting component and
	    // and an allocated shape.
	    Component c = getContainer();
	    if ((a != null) && (c != null)) {
		int pos = e.getOffset();
		int index = getViewIndexAtPosition(pos);
		Rectangle alloc = getInsideAllocation(a);
		if (axis == X_AXIS) {
		    alloc.x += xOffsets[index];
		    alloc.width -= xSpans[index];
		} else {
		    alloc.y += yOffsets[index];
		    alloc.height -= ySpans[index];
		}
		c.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
	    }
	}
    }

    // --- View methods ---------------------------------------------

    /**
     * This is called by a child to indicated its
     * preferred span has changed.  This is implemented to
     * throw away cached layout information so that new
     * calculations will be done the next time the children
     * need an allocation.
     *
     * @param child the child view
     * @param width true if the width preference should change
     * @param height true if the height preference should change
     */
    public void preferenceChanged(View child, boolean width, boolean height) {
if(this instanceof XMLPagedView)  //G***del
{
	Debug.traceStack();
}
	if (width) {
	    xValid = false;
	    xAllocValid = false;
	}
	if (height) {
	    yValid = false;
	    yAllocValid = false;
	}
	super.preferenceChanged(child, width, height);
    }

    /**
     * Gets the resize weight.  A value of 0 or less is not resizable.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @return the weight
     * @exception IllegalArgumentException for an invalid axis
     */
    public int getResizeWeight(int axis) {
	checkRequests();
        switch (axis) {
        case View.X_AXIS:
	    if ((xRequest.preferred != xRequest.minimum) &&
		(xRequest.preferred != xRequest.maximum)) {
		return 1;
	    }
	    return 0;
        case View.Y_AXIS:
	    if ((yRequest.preferred != yRequest.minimum) &&
		(yRequest.preferred != yRequest.maximum)) {
		return 1;
	    }
            return 0;
        default:
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    /**
     * Sets the size of the view.  If the size has changed, layout
     * is redone.  The size is the full size of the view including
     * the inset areas.
     *
     * @param width the width >= 0
     * @param height the height >= 0
     */
    public void setSize(float width, float height) {

		  //G***testing to see if we can update the insets before sizes are calculated
			synchronize();	//make sure we have the correct cached property values (newswing) //G***check to make sure this doesn't throw off calculations at the wrong time
//G***del Debug.stackTrace();  //G***del
if(this instanceof XMLPagedView)  //G***del
{
	Debug.trace("old width {0} new width {1} old height {2} new height {3}", new Object[]{new Float(this.width), new Float(width), new Float(this.height), new Float(height)});
	Debug.trace("xAllocValid: "+xAllocValid+" yAllocValid: "+yAllocValid);  //G***del
	Debug.trace(getClass().getName());
}
//G***del Debug.stackTrace(); //G***del
	if (((int) width) != this.width) {
	    xAllocValid = false;
	}
	if (((int) height) != this.height) {
	    yAllocValid = false;
	}
if(this instanceof XMLPagedView)  //G***del
{
Debug.trace("before layout");
Debug.trace("xAllocValid: "+xAllocValid+" yAllocValid: "+yAllocValid);  //G***del
}
	if ((! xAllocValid) || (! yAllocValid)) {
	    this.width = (int) width;
	    this.height = (int) height;
	    layout(this.width - getLeftInset() - getRightInset(),
		   this.height - getTopInset() - getBottomInset());
	}
if(this instanceof XMLPagedView)  //G***del
{
Debug.trace("after layout");
Debug.trace("xAllocValid: "+xAllocValid+" yAllocValid: "+yAllocValid);  //G***del
}
    }

    /**
     * Renders using the given rendering surface and area
     * on that surface.  Only the children that intersect
     * the clip bounds of the given Graphics will be
     * rendered.
     *
     * @param g the rendering surface to use
     * @param allocation the allocated region to render into
     * @see View#paint
     */
    public void paint(Graphics g, Shape allocation) {
Debug.trace("Inside XMLBlockView.paint()");
			synchronize();	//make sure we have the correct cached property values (newswing)

			XMLCSSViewPainter.paint(g, allocation, this, getAttributes());	//paint our CSS-specific parts (newswing) G***decide whether we want to pass the attributes or not

	Rectangle alloc = (allocation instanceof Rectangle) ?
	                   (Rectangle)allocation : allocation.getBounds();
	setSize(alloc.width, alloc.height);
	int n = getViewCount();
	int x = alloc.x + getLeftInset();
	int y = alloc.y + getTopInset();
	Rectangle clip = g.getClipBounds();
	for (int i = 0; i < n; i++) {
	    tempRect.x = x + xOffsets[i];
	    tempRect.y = y + yOffsets[i];
	    tempRect.width = xSpans[i];
	    tempRect.height = ySpans[i];
	if (tempRect.intersects(clip)) {
		  paintChild(g, tempRect, i);
	}
	}
    }

    /**
     * Fetches the allocation for the given child view.
     * This enables finding out where various views
     * are located.  This is implemented to return null
     * if the layout is invalid, otherwise the
     * superclass behavior is executed.
     *
     * @param index the index of the child, >= 0 && < getViewCount()
     * @param a  the allocation to this view.
     * @return the allocation to the child
     */
    public Shape getChildAllocation(int index, Shape a) {
	if (a != null) {
	    Shape ca = super.getChildAllocation(index, a);
	    if ((ca != null) && (! isAllocationValid())) {
		// The child allocation may not have been set yet.
		Rectangle r = (ca instanceof Rectangle) ?
		    (Rectangle) ca : ca.getBounds();
		if ((r.width == 0) && (r.height == 0)) {
		    return null;
		}
	    }
	    return ca;
	}
	return null;
    }

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.  This makes
     * sure the allocation is valid before letting the superclass
     * do its thing.
     *
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does
     *  not represent a valid location in the associated document
     * @see View#modelToView
     */
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	if (! isAllocationValid()) {
//G***del Debug.trace("XMLBlockView.modelToView() ready to get bounds.");	//G***del; testing image
	    Rectangle alloc = a.getBounds();
//G***del Debug.trace("XMLBlockView.modelToView() ready to set size.");	//G***del; testing image
	    setSize(alloc.width, alloc.height);
	}
	return super.modelToView(pos, a, b);
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param x   x coordinate of the view location to convert >= 0
     * @param y   y coordinate of the view location to convert >= 0
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point in the view >= 0
     * @see View#viewToModel
     */
    public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
/*G***del
Debug.trace("Inside XMLBlockView.viewToModel for position x: "+x+" y: "+y);
try{
Debug.trace(((JTextComponent)getContainer()).getDocument().getText(getStartOffset(), 5));
}catch(Exception e){};
*/
	if (! isAllocationValid()) {
	    Rectangle alloc = a.getBounds();
	    setSize(alloc.width, alloc.height);
	}
	return super.viewToModel(x, y, a, bias);
    }

    /**
     * Determines the desired alignment for this view along an
     * axis.  This is implemented to give the total alignment
     * needed to position the children with the alignment points
     * lined up along the axis orthoginal to the axis that is
     * being tiled.  The axis being tiled will request to be
     * centered (i.e. 0.5f).
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns the desired alignment >= 0.0f && <= 1.0f.  This should
     *   be a value between 0.0 and 1.0 where 0 indicates alignment at the
     *   origin and 1.0 indicates alignment to the full span
     *   away from the origin.  An alignment of 0.5 would be the
     *   center of the view.
     * @exception IllegalArgumentException for an invalid axis
     */
    public float getAlignment(int axis) {
	checkRequests();
	switch (axis) {
	case View.X_AXIS:
	    return xRequest.alignment;
	case View.Y_AXIS:
	    return yRequest.alignment;
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Determines the preferred span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis type
     */
    public float getPreferredSpan(int axis) {
//G***del Debug.stackTrace(); //G***del
Debug.trace("left inset: "+getLeftInset()+" right inset: "+getRightInset()+" top inset: "+getTopInset()+" bottom inset: "+getTopInset()); //G***del
	checkRequests();
	switch (axis) {
	case View.X_AXIS:
	    return ((float)xRequest.preferred) + getLeftInset() + getRightInset();
	case View.Y_AXIS:
	    return ((float)yRequest.preferred) + getTopInset() + getBottomInset();
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Determines the minimum span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis type
     */
    public float getMinimumSpan(int axis) {
	checkRequests();
	switch (axis) {
	case View.X_AXIS:
	    return ((float)xRequest.minimum) + getLeftInset() + getRightInset();
	case View.Y_AXIS:
	    return ((float)yRequest.minimum) + getTopInset() + getBottomInset();
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Determines the maximum span for this view along an
     * axis.
     *
     * @param axis may be either View.X_AXIS or View.Y_AXIS
     * @returns  the span the view would like to be rendered into >= 0.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.
     *           The parent may choose to resize or break the view.
     * @exception IllegalArgumentException for an invalid axis type
     */
    public float getMaximumSpan(int axis)
		{
			if((axis==X_AXIS && !isExpandX) || (axis==Y_AXIS && !isExpandY)) //if we can't expand along this axis (newswing)
				return getPreferredSpan(axis);  //return the preferred size as our maximum
else  //G***fix
{
	checkRequests();
	switch (axis) {
	case View.X_AXIS:
	    return ((float)xRequest.maximum) + getLeftInset() + getRightInset();
	case View.Y_AXIS:
	    return ((float)yRequest.maximum) + getTopInset() + getBottomInset();
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
}
    }

    // --- local methods ----------------------------------------------------

    /**
     * Are the allocations for the children still
     * valid?
     *
     * @return true if allocations still valid
     */
    protected boolean isAllocationValid() {
	return (xAllocValid && yAllocValid);
    }

    /**
     * Determines if a point falls before an allocated region.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param innerAlloc the allocated region.  This is the area
     *   inside of the insets.
     * @return true if the point lies before the region else false
     */
    protected boolean isBefore(int x, int y, Rectangle innerAlloc) {
	if (axis == View.X_AXIS) {
	    return (x < innerAlloc.x);
	} else {
	    return (y < innerAlloc.y);
	}
    }

    /**
     * Determines if a point falls after an allocated region.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param innerAlloc the allocated region.  This is the area
     *   inside of the insets.
     * @return true if the point lies after the region else false
     */
    protected boolean isAfter(int x, int y, Rectangle innerAlloc) {
	if (axis == View.X_AXIS) {
	    return (x > (innerAlloc.width + innerAlloc.x));
	} else {
	    return (y > (innerAlloc.height + innerAlloc.y));
	}
    }

    /**
     * Fetches the child view at the given point.
     *
     * @param x the X coordinate >= 0
     * @param y the Y coordinate >= 0
     * @param alloc the parents inner allocation on entry, which should
     *   be changed to the childs allocation on exit.
     * @return the view
     */
    protected View getViewAtPoint(int x, int y, Rectangle alloc) {
	int n = getViewCount();
	if (axis == View.X_AXIS) {
	    if (x < (alloc.x + xOffsets[0])) {
		childAllocation(0, alloc);
		return getView(0);
	    }
	    for (int i = 0; i < n; i++) {
		if (x < (alloc.x + xOffsets[i])) {
		    childAllocation(i - 1, alloc);
		    return getView(i - 1);
		}
	    }
	    childAllocation(n - 1, alloc);
	    return getView(n - 1);
	} else {
	    if (y < (alloc.y + yOffsets[0])) {
		childAllocation(0, alloc);
		return getView(0);
	    }
	    for (int i = 0; i < n; i++) {
		if (y < (alloc.y + yOffsets[i])) {
		    childAllocation(i - 1, alloc);
		    return getView(i - 1);
		}
	    }
	    childAllocation(n - 1, alloc);
	    return getView(n - 1);
	}
    }

    /**
     * Allocates a region for a child view.
     *
     * @param index the index of the child view to
     *   allocate, >= 0 && < getViewCount()
     * @param alloc the allocated region
     */
    protected void childAllocation(int index, Rectangle alloc) {
	alloc.x += xOffsets[index];
	alloc.y += yOffsets[index];
	alloc.width = xSpans[index];
	alloc.height = ySpans[index];
    }

    /**
     * Performs layout of the children.  The size is the
     * area inside of the insets.  This method calls
     * the methods
     * <a href="#layoutMajorAxis">layoutMajorAxis</a> and
     * <a href="#layoutMinorAxis">layoutMinorAxis</a> as
     * needed.  To change how layout is done those methods
     * should be reimplemented.
     *
     * @param width the width >= 0
     * @param height the height >= 0
     */
    protected void layout(int width, int height) {
	checkRequests();

	if (axis == X_AXIS) {
	    if (! xAllocValid) {
		layoutMajorAxis(width, X_AXIS, xOffsets, xSpans);
	    }
	    if (! yAllocValid) {
		layoutMinorAxis(height, Y_AXIS, yOffsets, ySpans);
	    }
	} else {
	    if (! xAllocValid) {
		layoutMinorAxis(width, X_AXIS, xOffsets, xSpans);
	    }
	    if (! yAllocValid) {
		layoutMajorAxis(height, Y_AXIS, yOffsets, ySpans);
	    }
	}
	xAllocValid = true;
	yAllocValid = true;

	// flush changes to the children
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    v.setSize((float) xSpans[i], (float) ySpans[i]);
	}
    }

    /**
     * The current width of the box.  This is the width that
     * it was last allocated.
     */
    public int getWidth() {
	return width;
    }

    /**
     * The current height of the box.  This is the height that
     * it was last allocated.
     */
    public int getHeight() {
	return height;
    }

    /**
     * Perform layout for the major axis of the box (i.e. the
     * axis that it represents).  The results of the layout should
     * be placed in the given arrays which represent the allocations
     * to the children along the major axis.
     *
     * @param targetSpan the total span given to the view, which
     *  whould be used to layout the children.
     * @param axis the axis being layed out.
     * @param offsets the offsets from the origin of the view for
     *  each of the child views.  This is a return value and is
     *  filled in by the implementation of this method.
     * @param spans the span of each child view.  This is a return
     *  value and is filled in by the implementation of this method.
     * @returns the offset and span for each child view in the
     *  offsets and spans parameters.
     */
    protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
	/*
	 * first pass, calculate the preferred sizes
	 * and the flexibility to adjust the sizes.
	 */
	long minimum = 0;
	long maximum = 0;
	long preferred = 0;
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    spans[i] = (int) v.getPreferredSpan(axis);
	    preferred += spans[i];
	    minimum += v.getMinimumSpan(axis);
	    maximum += v.getMaximumSpan(axis);
	}

	/*
	 * Second pass, expand or contract by as much as possible to reach
	 * the target span.
	 */

	// determine the adjustment to be made
	long desiredAdjustment = targetSpan - preferred;
	float adjustmentFactor = 0.0f;
	if (desiredAdjustment != 0) {
	    float maximumAdjustment = (desiredAdjustment > 0) ?
		maximum - preferred : preferred - minimum;
            if (maximumAdjustment == 0.0f) {
                adjustmentFactor = 0.0f;
            }
            else {
                adjustmentFactor = desiredAdjustment / maximumAdjustment;
                adjustmentFactor = Math.min(adjustmentFactor, 1.0f);
                adjustmentFactor = Math.max(adjustmentFactor, -1.0f);
            }
	}

	// make the adjustments
	int totalOffset = 0;
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    offsets[i] = totalOffset;
	    int availableSpan = (adjustmentFactor > 0.0f) ?
		(int) v.getMaximumSpan(axis) - spans[i] :
		spans[i] - (int) v.getMinimumSpan(axis);
            float adjF = adjustmentFactor * availableSpan;
            if (adjF < 0) {
                adjF -= .5f;
            }
            else {
                adjF += .5f;
            }
	    int adj = (int)adjF;
	    spans[i] += adj;
	    totalOffset = (int) Math.min((long) totalOffset + (long) spans[i], Integer.MAX_VALUE);
	}
    }

    /**
     * Perform layout for the minor axis of the box (i.e. the
     * axis orthoginal to the axis that it represents).  The results
     * of the layout should be placed in the given arrays which represent
     * the allocations to the children along the minor axis.
     *
     * @param targetSpan the total span given to the view, which
     *  whould be used to layout the children.
     * @param axis the axis being layed out.
     * @param offsets the offsets from the origin of the view for
     *  each of the child views.  This is a return value and is
     *  filled in by the implementation of this method.
     * @param spans the span of each child view.  This is a return
     *  value and is filled in by the implementation of this method.
     * @returns the offset and span for each child view in the
     *  offsets and spans parameters.
     */
    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    int min = (int) v.getMinimumSpan(axis);
	    int max = (int) v.getMaximumSpan(axis);
	    if (max < targetSpan) {
		// can't make the child this wide, align it
		float align = v.getAlignment(axis);
		offsets[i] = (int) ((targetSpan - max) * align);
		spans[i] = max;
	    } else {
		// make it the target width, or as small as it can get.
		offsets[i] = 0;
		spans[i] = Math.max(min, targetSpan);
	    }
	}
    }

    protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r) {
	// calculate tiled request
	float min = 0;
	float pref = 0;
	float max = 0;

	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    min += v.getMinimumSpan(axis);
	    pref += v.getPreferredSpan(axis);
	    max += v.getMaximumSpan(axis);
	}

	if (r == null) {
	    r = new SizeRequirements();
	}
	r.alignment = 0.5f;
	r.minimum = (int) min;
	r.preferred = (int) pref;
	r.maximum = (int) max;
	return r;
    }

    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
	int min = 0;
	long pref = 0;
	int max = Integer.MAX_VALUE;
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    min = Math.max((int) v.getMinimumSpan(axis), min);
	    pref = Math.max((int) v.getPreferredSpan(axis), pref);
	    max = Math.max((int) v.getMaximumSpan(axis), max);
	}

	if (r == null) {
	    r = new SizeRequirements();
	    r.alignment = 0.5f;
	}
	r.preferred = (int) pref;
	r.minimum = min;
	r.maximum = max;
	return r;
    }

		/**
		 * Checks the request cache and update if needed.
		 */
	void checkRequests()
	{
	if (axis == X_AXIS) {
	    if (! xValid) {
		xRequest = calculateMajorAxisRequirements(X_AXIS, xRequest);
	    }
	    if (! yValid) {
		yRequest = calculateMinorAxisRequirements(Y_AXIS, yRequest);
	    }
	} else {
	    if (! xValid) {
		xRequest = calculateMinorAxisRequirements(X_AXIS, xRequest);
	    }
	    if (! yValid) {
		yRequest = calculateMajorAxisRequirements(Y_AXIS, yRequest);
	    }
	}
	yValid = true;
	xValid = true;
    }


    protected void baselineLayout(int targetSpan, int axis, int[] offsets, int[] spans) {
	int totalBelow = (int) (targetSpan * getAlignment(axis));
	int totalAbove = targetSpan - totalBelow;
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    float align = v.getAlignment(axis);
	    int span = (int) v.getPreferredSpan(axis);
	    int below = (int) (span * align);
	    int above = span - below;
	    if (span > targetSpan) {
		// check compress
		if ((int) v.getMinimumSpan(axis) < span) {
		    below = totalBelow;
		    above = totalAbove;
		} else {
		    if ((v.getResizeWeight(axis) > 0) && (v.getMaximumSpan(axis) != span)) {
			throw new Error("should not happen: " + v.getClass());
		    }
		}
	    } else if (span < targetSpan) {
		// check expand
		if ((int) v.getMaximumSpan(axis) > span) {
		    below = totalBelow;
		    above = totalAbove;
		}
	    }
/*
	    if (v.getResizeWeight(axis) > 0) {
		below = totalBelow;
		above = totalAbove;
	    }
	    */
	    offsets[i] = totalBelow - below;
	    spans[i] = below + above;
	}
    }

    protected SizeRequirements baselineRequirements(int axis, SizeRequirements r) {
	int totalAbove = 0;
	int totalBelow = 0;
	int resizeWeight = 0;
	int n = getViewCount();
	for (int i = 0; i < n; i++) {
	    View v = getView(i);
	    int span = (int) v.getPreferredSpan(axis);
	    int below = (int) (v.getAlignment(axis) * span);
	    int above = span - below;
	    totalAbove = Math.max(above, totalAbove);
	    totalBelow = Math.max(below, totalBelow);
	    resizeWeight += v.getResizeWeight(axis);
	}

	if (r == null) {
	    r = new SizeRequirements();
	}
	r.preferred = totalAbove + totalBelow;
	if (resizeWeight != 0) {
	    r.maximum = Integer.MAX_VALUE;
	    r.minimum = 0;
	} else {
	    r.maximum = r.preferred;
	    r.minimum = r.preferred;
	}
	if (r.preferred > 0) {
	    r.alignment = (float) totalBelow / r.preferred;
	} else {
	    r.alignment = 0.5f;
	}
	return r;
    }

    /**
     * Fetch the offset of a particular childs current layout
     */
    protected int getOffset(int axis, int childIndex) {
	int[] offsets = (axis == X_AXIS) ? xOffsets : yOffsets;
	return offsets[childIndex];
    }

    /**
     * Fetch the span of a particular childs current layout
     */
    protected int getSpan(int axis, int childIndex) {
	int[] spans = (axis == X_AXIS) ? xSpans : ySpans;
	return spans[childIndex];
    }

//G***fix		protected boolean flipEastAndWestAtEnds(int position, Position.Bias bias) {
		public boolean flipEastAndWestAtEnds(int position, Position.Bias bias) {	//G***newswing
	if(axis == Y_AXIS) {
			int testPos = (bias == Position.Bias.Backward) ?
							Math.max(0, position - 1) : position;
			int index = getViewIndexAtPosition(testPos);
			if(index != -1) {
		View v = getView(index);

		if(v != null && v instanceof com.garretwilson.swing.text.CompositeView) {	//G***newswing
				return ((com.garretwilson.swing.text.CompositeView)v).flipEastAndWestAtEnds(position,
										bias);
/*G***del newswing
		if(v != null && v instanceof CompositeView) {
				return ((CompositeView)v).flipEastAndWestAtEnds(position,
										bias);
*/
		}
	    }
	}
	return false;
    }

    // --- variables ------------------------------------------------

		int axis;
    int width;
    int height;

    /*
     * Request cache
		 */
    SizeRequirements xRequest;
    SizeRequirements yRequest;
/*G***del if not needed newswing
		protected SizeRequirements xRequest;	//G***newswing
		protected SizeRequirements yRequest;	//G***newswing
*/

    /*
     * Allocation cache
     */
		int[] xOffsets;
		int[] xSpans;
		int[] yOffsets;
		int[] ySpans;

		/** used in paint. */
		Rectangle tempRect;


	//G*** newswing ****

	/**Whether the horizontal allocation is valid or needs to be updated.*/
	private boolean xAllocValid;

		/**@return whether the horizontal allocation is valid or needs to be updated.*/
		public boolean isXAllocValid() {return xAllocValid;}

		/**Sets whether the horizontal allocation is valid or needs to be updated.
		@param <code>true</code> if the horizontal allocation is valid, or
			<code>false</code> if it needs to be updated.
		*/
		protected void setXAllocValid(final boolean xAllocationValid) {xAllocValid=xAllocationValid;}

	/**Whether the vertical allocation is valid or needs to be updated.*/
	private boolean yAllocValid;

		/**@return whether the vertical allocation is valid or needs to be updated.*/
		public boolean isYAllocValid() {return yAllocValid;}

		/**Sets whether the vertical allocation is valid or needs to be updated.
		@param <code>true</code> if the Vertical allocation is valid, or
			<code>false</code> if it needs to be updated.
		*/
		protected void setYAllocValid(final boolean yAllocationValid) {yAllocValid=yAllocationValid;}

	/**Whether the horizontal preference valid or needs to be updated.*/
	private boolean xValid;

		/**@return whether the horizontal preference is valid or needs to be updated.*/
		public boolean isXValid() {return xValid;}

		/**Sets whether the horizontal preference is valid or needs to be updated.
		@param <code>true</code> if the horizontal preference is valid, or
			<code>false</code> if it needs to be updated.
		*/
		protected void setXValid(final boolean xvalid) {xValid=xvalid;}

	/**Whether the vertical preference is valid or needs to be updated.*/
	private boolean yValid;

		/**@return whether the vertical preference is valid or needs to be updated.*/
		public boolean isYValid() {return yValid;}

		/**Sets whether the vertical preference is valid or needs to be updated.
		@param <code>true</code> if the Vertical allocation is valid, or
			<code>false</code> if it needs to be updated.
		*/
		protected void setYValid(final boolean yvalid) {yValid=yvalid;}

	/**Creates a fragment view into which pieces of this view will be placed.
	@param isFirstFragment Whether this fragment holds the first part of the
		original view.
	@param isLastFragment Whether this fragment holds the last part of the
		original view.
	*/
	protected View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
	{
	  return new XMLFragmentBlockView(getElement(), getAxis(), isFirstFragment, isLastFragment);	//create a fragment of the view
	}

	/**Determines how attractive a break opportunity in this view is. This can be
		used for determining which view is the most attractive to call
		<code>breakView</code> on in the process of formatting.  The higher the
		weight, the more attractive the break.  A value equal to or lower than
		<code>View.BadBreakWeight</code> should not be considered for a break. A
		value greater than or equal to <code>View.ForcedBreakWeight</code> should
		be broken.<br/>
		This is implemented to forward to the superclass for axis perpendicular to
		the flow axis. Along the flow axis the following values may be returned:
		<ul>
//G***fix			<li>View.ExcellentBreakWeight: If there is whitespace proceeding the desired break
//G***fix		 *   location.
			<li>View.BadBreakWeight: If the desired break location results in a break
				location of the starting offset (i.e. not even one child view can fit.</li>
			<li>View.GoodBreakWeight: If the other conditions don't occur.
		</ul>
		This will result in the view being broken with the maximum number of child
		views that can fit within the required span.
		@param axis The breaking axis, either View.X_AXIS or View.Y_AXIS.
		@param pos The potential location of the start of the broken view (>=0).
			This may be useful for calculating tab positions.
		@param len Specifies the relative length from <em>pos</em> where a potential
			break is desired (>=0).
		@return The weight, which should be a value between View.ForcedBreakWeight
			and View.BadBreakWeight.
		@see LabelView
		@see ParagraphView
		@see BadBreakWeight
		@see GoodBreakWeight
		@see ExcellentBreakWeight
		@see ForcedBreakWeight
	*/
	public int getBreakWeight(int axis, float pos, float len)
	{
//G***del System.out.println("Inside XMLBlockView.getBreakWeight axis: "+axis+" pos: "+pos+" len: "+len+" name: "+XMLStyleConstants.getXMLElementName(getElement().getAttributes()));	//G***del
//G***del Debug.trace("Inside XMLBlockView.getBreakWeight axis: "+axis+" pos: "+pos+" len: "+len+" name: "+XMLStyleConstants.getXMLElementName(getAttributes()));	//G***del

//G***del		final int tileAxis=getAxis();	//get our axis for tiling (this view's axis)
		if(axis==getAxis())	//if they want to break along our tiling axis
		{
//G***bring back when works			return View.GoodBreakWeight;	//show that this break spot will work
			return View.GoodBreakWeight;	//show that this break spot will work
/*G***fix
				//G***we should probably see if this is exactly the beginning of one of our views or something
			if(getViewCount()==0 || len>getView(0).getPreferredSpan(axis))	//if we have no child views, or we can fit at least one view
				return View.GoodBreakWeight;	//show that this break spot will work
			else	//if we can't even fit one view
				return View.BadBreakWeight;	//show that we don't want to break here
*/
		}
		else	//if they want to break along another axis besides the one we know about
			return super.getBreakWeight(axis, pos, len);	//return the default break weight
	}

	/**Breaks this view on the given axis at the given length. This is implemented
		to attempt to break on the largest number of child views that can fit within
		the given length.
	@param axis The axis to break along, either View.X_AXIS or View.Y_AXIS.
	@param p0 the location in the model where the fragment should start its
		representation (>=0).
	@param pos the position along the axis that the broken view would occupy (>=0).
		This may be useful for things like tab calculations.
	@param len Specifies the distance along the axis where a potential break is
		desired (>=0).
	@return The fragment of the view that represents the given span, if the view
		can be broken. If the view doesn't support breaking behavior, the view itself
		is returned.
	@see View#breakView
	*/
	public View breakView(int axis, int p0, float pos, float len)
	{
Debug.trace("Inside XMLBlockView.breakView axis: "+axis+" p0: "+p0+" pos: "+pos+" len: "+len+" name: "+XMLStyleUtilities.getXMLElementName(getAttributes()));	//G***del

	//G***important! if len is sufficiently large, just return ourselves
		if(axis==getAxis())	//if they want to break along our tiling axis
		{

//G***del		System.out.println("breakView() p0: "+p0+" pos: "+pos+" len: "+len+" preferredSpan: "+getPreferredSpan(axis));	//G***del

		  final int childViewCount=getViewCount();  //get the number of child views we have
			if(len>=getPreferredSpan(axis))	//if the break is as large or larger than the view
				return this;	//just return ourselves; there's no need to try to break anything
			else if(childViewCount>0)	//if we have child views
			{
//G***del				final XMLBlockView fragmentView=(XMLBlockView)clone();	//create a clone of this view
				final boolean isFirstFragment=p0<=getView(0).getStartOffset();  //see if we'll include the first child in any form; if so, we're the first fragment
				final boolean isLastFragment=false;  //G***fix; can we ever create the last fragment here?
//G***fix				final boolean isLastFragment=p1>=getView(childViewCount-1).getEndOffset();  //see if we'll include the last child in any form; if so, we're the last fragment
				final View fragmentView=createFragmentView(isFirstFragment, isLastFragment);	//create a fragment view
//G***del when works				final XMLFragmentBlockView fragmentView=new XMLFragmentBlockView(getElement(), getAxis(), isFirstFragment);	//create a fragment view
fragmentView.setParent(getParent());				  //G***testing; comment
//G***del when works				int p1=p0;	//we'll know if we've found views to include in our broken view when we change p1 to be greater than p0
				float totalSpan=0;	//we'll use this to accumulate the size of each view to be included
				int startOffset=p0;	//we'll continually update this as we create new child view fragments
				int childIndex;	//start looking at the first child to find one that can be included in our break
				for(childIndex=0; childIndex<childViewCount && getView(childIndex).getEndOffset()<=startOffset; ++childIndex);	//find the first child that ends after our first model location
				for(; childIndex<childViewCount && totalSpan<len; ++childIndex)	//look at each child view at and including the first child we found that will go inside this fragment, and keep adding children until we find enough views to fill up the space or we run out of views
				{
					View childView=getView(childIndex);	//get a reference to this child view; we may change this variable if we have to break one of the child views
Debug.trace("looking to include child view: ", childView.getClass().getName()); //G***del
Debug.trace("child view preferred span: "+childView.getPreferredSpan(axis)); //G***del
				  final int unbrokenChildEndOffset=childView.getEndOffset(); //see which content the unbroken view would cover
					if(totalSpan+childView.getPreferredSpan(axis)>len)	//if this view is too big to fit into our space
					{
							//G***new code; testing
/*G***bring back; see if we really need to add something -- we really only need to add something if this is the root XMLBlockView -- but how do we know that?
						if(fragmentView.getViewCount()==0)	//if we haven't fit any views into our fragment, we must have at least one, even if it doesn't fit
							childView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, len-totalSpan);	//break the view to fit, taking into account that the view might have started before our break point
*/
//G***bring back						else if(childView.getBreakWeight(axis, pos, len-totalSpan)>BadBreakWeight)	//if this view can be broken to be made to fit
						if(fragmentView.getViewCount()==0)	//if we haven't fit any views into our fragment, we must have at least one, even if it doesn't fit
							childView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, len-totalSpan);	//break the view to fit, taking into account that the view might have started before our break point
						else if(childView.getBreakWeight(axis, pos, len-totalSpan)>BadBreakWeight)	//if this view can be broken to be made to fit
						{
							childView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, len-totalSpan);	//break the view to fit, taking into account that the view might have started before our break point
							if(totalSpan+childView.getPreferredSpan(axis)>len)	//if this view is still too big to fit into our space
								childView=null;	//show that we don't want to add this child
						}
						else	//if this view can't break and we already have views in the fragment
							childView=null;	//show that we don't want to add this child


/*G***del; old, working code that sometimes overshot allocated boundaries
						if(childView.getBreakWeight(axis, pos, len-totalSpan)>BadBreakWeight || fragmentView.getViewCount()==0)	//if this view can be broken to be made to fit, or we haven't fit any views into our fragment
							childView=childView.breakView(axis, Math.max(childView.getStartOffset(), startOffset), pos, len-totalSpan);	//break the view to fit, taking into account that the view might have started before our break point
						else	//if this view can't break and we already have views in the fragment
							childView=null;	//show that we don't want to add this child
*/
					}
					if(childView!=null)	//if we have something to add
					{
						fragmentView.append(childView);	//add this child view, which could have been chopped up into a fragment itself
						totalSpan+=childView.getPreferredSpan(axis);	//show that we've used up more space
					  if(childView.getEndOffset()<unbrokenChildEndOffset) //if we were not able to return the whole view
						  break;  //stop trying to add more child views; more views may fit (especially hidden views), but they would be skipping content that we've already lost by breaking this child view G***can we be sure the original view was unbroken?
					}
					else	//if we needed more room but couldn't break a view
						break;	//stop trying to fit things
				}
//G***del; moved up fragmentView.setParent(getParent());				  //G***testing; comment
//G***del when works fragmentView.setParent(this);				  //G***testing; comment
				return fragmentView;	//return the new view that's a fragment of ourselves
			}
		}
		return this;	//if they want to break along another axis or we weren't able to break, return our entire view
	}


		/**
		 * Creates a shallow copy.  This is used by the
		 * createFragment and breakView methods.
		 *
		 * @return the copy
		 */
//G***testing
/*G***del
		protected final Object clone()
		{
			return new XMLBlockView(getElement(), getAxis());	//G***testing
*/
/*G***fix
	Object o;
	try {
			o = super.clone();
	} catch (CloneNotSupportedException cnse) {
			o = null;
	}
	return o;
*/
//G***ddl		}


	/**Creates a view that represents a portion of the element. This is
		potentially useful during formatting operations for taking measurements of
		fragments of the view. If the view doesn't support fragmenting, it should
		return itself.<br/>
		This view does support fragmenting. It is implemented to return a new view
		that contains the required child views.
		@param p0 The starting offset (>=0). This should be a value greater or equal
			to the element starting offset and less than the element ending offset.
		@param p1 The ending offset (>p0).  This should be a value less than or
			equal to the elements end offset and greater than the elements starting offset.
		@returns The view fragment, or itself if the view doesn't support breaking
			into fragments.
		@see View#createFragment
	*/
	public View createFragment(int p0, int p1)
	{
//G***del System.out.println("Inside createFragment(), p0: "+p0+" p1: "+p1+" name: "+XMLStyleConstants.getXMLElementName(getElement().getAttributes()));	//G***del
//G***del Debug.trace("Inside XMLBLockView.createFragment(), p0: "+p0+" p1: "+p1+" name: "+XMLStyleConstants.getXMLElementName(getAttributes())); //G***del)
//G***del Debug.trace("Our startOffset: "+getStartOffset()+" endOffset: "+getEndOffset());	//G***del
//G***del System.out.println("Inside createFragment(), p0: "+p0+" p1: "+p1);	//G***del
//G***del System.out.println("Our startOffset: : "+getStartOffset()+" endOffset: "+getEndOffset());	//G***del

//G***del		XMLBlockView fragmentView=(XMLBlockView)clone();	//create a clone of this view
		if(p0<=getStartOffset() && p1>=getEndOffset())	//if the range they want encompasses all of our view
			return this;	//return ourselves; there's no use to try to break ourselves up
		else	//if the range they want only includes part of our view
		{
//G***del			final BoxView fragmentView=new BoxView(getElement(), getAxis());	//G***testing! highly unstable! trying to fix vertical spacing bug

			final int childViewCount=getViewCount();  //find out how many child views there are
				//see if we'll include the first child in any form; if so, we're the first fragment
		  final boolean isFirstFragment=childViewCount>0 && p0<=getView(0).getStartOffset();
				//see if we'll include the last child in any form; if so, we're the first fragment
		  final boolean isLastFragment=childViewCount>0 && p1>=getView(childViewCount-1).getEndOffset();
			final View fragmentView=createFragmentView(isFirstFragment, isLastFragment);	//create a fragment view
//G***del when works			final XMLFragmentBlockView fragmentView=new XMLFragmentBlockView(getElement(), getAxis(), isFirstFragment);	//create a fragment of the view
fragmentView.setParent(getParent());				  //G***testing; comment
//G***del when works			final XMLBlockView fragmentView=(XMLBlockView)clone();	//create a clone of this view


	//G***fix		final XMLBlockFragmentView fragmentView=new XMLBlockFragmentView(this);	//create a fragment to hold part of our content
			for(int i=0; i<childViewCount; ++i)	//look at each child view
			{
				final View childView=getView(i);	//get a reference to this child view
				if(childView.getStartOffset()<p1 && childView.getEndOffset()>p0)	//if this view is within our range
				{
	//G***del when works			if(childView.getStartOffset()>=p0 && childView.getEndOffset()<=p1)	//if this view is within our range
					final int startPos=Math.max(p0, childView.getStartOffset());	//find out where we want to start, staying within this child view
					final int endPos=Math.min(p1, childView.getEndOffset());	//find out where we want to end, staying within this child view
					fragmentView.append(childView.createFragment(startPos, endPos));	//add a portion (or all) of this child to our fragment
				}
			}
//G***del; moved up fragmentView.setParent(getParent());				  //G***testing; comment
//G***del when works fragmentView.setParent(this);				  //G***testing; comment
			return fragmentView;	//return the fragment view we constructed
		}
	}

		/**Each fragment is a subset of the content in the breaking <code>XMLBlockView</code>.
		@return The starting offset of this page, which is the starting offset of the
			view with the lowest starting offset
		@see View#getRange
		*/
//G***testing
		public int getStartOffset()
		{
			int startOffset=Integer.MAX_VALUE;	//we'll start out with a high number, and we'll end up with the lowest starting offset of all the views
			final int numViews=getViewCount();	//find out how many view are on this page
//G***del System.out.println("getStartOffset() viewCount: "+numViews+" name: "+(String)getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" super: "+super.getStartOffset());	//G***del
			if(numViews>0)	//if we have child views
			{
				for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
				{
					final View view=getView(viewIndex);	//get a reference to this view
					startOffset=Math.min(startOffset, view.getStartOffset());	//if this view has a lower starting offset, use its starting offset
//G***del System.out.println("  View: "+(String)getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" child: "+(String)view.getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" startOffset: "+view.getStartOffset()+" New start offset: "+startOffset);	//G***del
				}
				return startOffset;	//return the starting offset we found
			}
			else	//if we don't have any child views
				return super.getStartOffset();	//return the default starting offset
		}

		/**Each fragment is a subset of the content in the breaking <code>XMLBlockView</code>.
		@return The ending offset of this page, which is the ending offset of the
			view with the largest ending offset
		@see View#getRange
		*/
//G***testing
		public int getEndOffset()
		{
			int endOffset=0;	//start out with a low ending offset, and we'll wind up with the largest ending offset
			final int numViews=getViewCount();	//find out how many view are on this page
//G***del System.out.println("getEndOffset() viewCount: "+numViews+" name: "+(String)getElement().getAttributes().getAttribute(XMLCSSStyleConstants.XMLElementNameName)+" super: "+super.getEndOffset());	//G***del
			if(numViews>0)	//if we have child views
			{
				for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
				{
					final View view=getView(viewIndex);	//get a reference to this view


					final int viewEndOffset=view.getEndOffset();  //G***testing
					if(viewEndOffset>endOffset) //G***testing
					{
//G***del Debug.trace("Transitioning from "+endOffset+" to "+viewEndOffset+" because of view of type: "+view.getClass().getName()+" of name: "+XMLStyleConstants.getXMLElementName(view.getElement().getAttributes()));  //G***del
						endOffset=viewEndOffset;  //G***del
//G***del Debug.trace("  View: "+XMLStyleConstants.getXMLElementName(getElement().getAttributes())+" child: "+XMLStyleConstants.getXMLElementName(view.getElement().getAttributes())+" endOffset: "+view.getEndOffset()+" New end offset: "+endOffset);	//G***del
					}

/*G***bring back
Debug.trace("old endOffset: ", endOffset);  //G***del
					endOffset=Math.max(endOffset, view.getEndOffset());	//if this view has a larger ending offset, use that instead
Debug.trace("new endOffset: ", endOffset);  //G***del
Debug.trace("  View: "+XMLStyleConstants.getXMLElementName(getElement().getAttributes())+" child: "+XMLStyleConstants.getXMLElementName(view.getElement().getAttributes())+" endOffset: "+view.getEndOffset()+" New end offset: "+endOffset);	//G***del
*/
				}
				return endOffset;	//return the largest ending offset we found
			}
			else	//if we don't have any child views
				return super.getEndOffset();	//return the default ending offset
		}


	//G***we may now want to put getViewIndexAtPosition, along with getStart/EndOffset(),
	//  in XMLFragmentBlockView;

	/**Returns the index of the child at the given model position in the pool.
	@param pos The position (>=0) in the model.
	@return The index of the view representing the given position, or -1 if there
		is no view on this pool which represents that position.
	*/
	protected int getViewIndexAtPosition(int pos)
	{
//G***del Debug.trace("looking for view at position: ", pos); //G***del
		//this is an expensive operation, but this class usually contains only a partial list of views, which may not correspond to the complete list of original elements
		if(pos<getStartOffset() || pos>=getEndOffset())	//if the position is before or after the content
			return -1;	//show that the given position is not on this page
		for(int viewIndex=getViewCount()-1; viewIndex>=0; --viewIndex)	//look at all the views from the last to the first
		{
			final View view=getView(viewIndex);	//get a reference to this view
//G***del Debug.trace("View "+viewIndex+" is of class: ", view.getClass().getName());
//G***del Debug.trace("startoffset: ", view.getStartOffset());
//G***del Debug.trace("endoffset: ", view.getEndOffset());
			if(pos>=view.getStartOffset() && pos<view.getEndOffset())	//if this view holds the requested position
				return viewIndex;	//return the index to this view
		}
		return -1;	//if we make it to this point, we haven't been able to find a view with the specified position
	}


/**G***fix; we shouldn't need this, since View.getAttributes() returns element.getAttributes.
This is mystifying: currently View.getAttributes() returns null inside setPropertiesFromAttributes(),
but getElement().getAttributes() returns the correct value.
*/
/*G***del
    public AttributeSet getAttributes() {
Debug.trace("XXXXXXXXXXXXXXXXXXX inside XMLBlockView.getAttributes()");	//G***del
	return getElement().getAttributes();
    }
*/

	/**Whether or not the cached values have been synchronized.*/
	protected boolean cacheSynchronized=false;  //G***see if we should make this private with an accessor method

	/**Synchronizes the view's cached valiues with the model. This causes the
		font, metrics, color, etc to be recached if the cache has been invalidated.
	*/
	protected void synchronize()
	{
//G***del Debug.stackTrace(); //G***del
		if(!cacheSynchronized)	//if the cache isn't synchronized
			setPropertiesFromAttributes();	//calculate our properties from our attributes
	}

	/**Sets the cached properties from the attributes.*/
	protected void setPropertiesFromAttributes()
	{
//G***del Debug.stackTrace(); //G***del
		cacheSynchronized=true;	//show that we have the most up-to-date information in the cache
/*G***del
Debug.trace("*****Inside XMLBlockView.setPropertiesFromAttributes()");
Debug.trace("element is: "+getElement().getName());	//G***del
Debug.trace("element is: "+Debug.getNullStatus(getElement()));	//G***del
Debug.trace("element attributes is: "+Debug.getNullStatus(getElement().getAttributes()));	//G***del
Debug.trace("getAttributes() is: "+Debug.getNullStatus(getAttributes()));	//G***del
*/
		final AttributeSet attributeSet=getAttributes();	//get our attributes
		if(attributeSet!=null)	//if we have attributes
		{
//G***del Debug.trace("*****Inside XMLBlockView.setPropertiesFromAttributes() for element: "+XMLStyleConstants.getXMLElementName(attributeSet));  //G***del
			setBackgroundColor(XMLCSSStyleUtilities.getBackgroundColor(attributeSet));	//set the background color from the attributes
//G***del Debug.trace("New background color: "+getBackgroundColor());

			final Document document=getDocument();	//get our document
			if(document instanceof StyledDocument)		//if this is a styled document
			{
				final StyledDocument styledDocument=(StyledDocument)document;	//cast the document to a styled document
				final Font font=styledDocument.getFont(attributeSet);	//let the document get the font from the attributes
//G***find some way to cache the font in the attributes

		  //G***we may want to set the insets in setPropertiesFromAttributes(); for
			//percentages, getPreferredeSpan(), etc. will have to look at the preferred
			//span and make calculations based upon the percentages
			//G***probably have some other exernal helper class that sets the margins based upon the attributes
			final short marginTop=(short)Math.round(XMLCSSStyleUtilities.getMarginTop(attributeSet)); //get the top margin from the attributes
			final short marginLeft=(short)Math.round(XMLCSSStyleUtilities.getMarginLeft(attributeSet, font)); //get the left margin from the attributes
			final short marginBottom=(short)Math.round(XMLCSSStyleUtilities.getMarginBottom(attributeSet)); //get the bottom margin from the attributes
			final short marginRight=(short)Math.round(XMLCSSStyleUtilities.getMarginRight(attributeSet, font)); //get the right margin from the attributes
		  setInsets(marginTop, marginLeft, marginBottom, marginRight);	//G***fix; testing
			}
		}
	}

	/**The background color of the block view.*/
	private Color backgroundColor;

		/**Gets the background color of the block view.
		@return The background color of the block view.
		*/
		public Color getBackgroundColor()
		{
			synchronize();	//make sure we have the correct cached property values
			return backgroundColor;
		}

		/**Sets the background color of the block view.
		@param newBackgroundColor The color of the background.
		*/
		protected void setBackgroundColor(final Color newBackgroundColor) {backgroundColor=newBackgroundColor;}
}
