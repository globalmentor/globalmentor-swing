package com.garretwilson.swing.text.xml;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.text.*;

import com.garretwilson.lang.CharSequenceUtilities;
import com.garretwilson.swing.text.*;
import static com.garretwilson.swing.text.SwingTextUtilities.*;
import static com.garretwilson.swing.text.ViewUtilities.*;
import com.garretwilson.swing.text.xml.css.*;
import com.garretwilson.text.CharacterConstants;
import com.garretwilson.text.xml.stylesheets.css.*;
import com.garretwilson.util.Debug;
import org.w3c.dom.css.CSSStyleDeclaration;

/**A view that arranges its children into a box shape by tiling its children along an axis.
Any non-block child views are wrapped in anonymous block views.
@author Garret Wilson
@see javax.swing.text.BoxView
*/
public class XMLBlockView extends ContainerBoxView implements XMLCSSView, FragmentViewFactory
{

	/**The shared empty array of elements.*/
	protected final static Element[] NO_ELEMENTS=new Element[0];

	/**Whether we're allowed to expand horizontally.*/
	protected final boolean isExpandX;	//TODO is this used anymore?

	/**Whether we're allowed to expand vertically.*/
	protected final boolean isExpandY;	//TODO is this used anymore?

	/**Constructs an XMLBlockView expandable on the flowing (non-tiling) axis.
	@param element The element this view is responsible for.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	*/
	public XMLBlockView(final Element element, final int axis)
	{
		this(element, axis, axis!=X_AXIS, axis!=Y_AXIS); //default to not flowing on the tiling axis
	}

	/**Constructs an XMLBlockView, specifying whether the view should be allowed
		to expand to a maximum size for the given axes.
	@param element The element this view is responsible for.
	@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
	*/
	public XMLBlockView(final Element element, final int axis, final boolean expandX, final boolean expandY)
	{
		super(element, axis);	//construct the parent class
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
		final Element parentElement=getElement();	//get the parent element
		final Element[] childElements=getChildElements(parentElement);	//put our child elements into an array
		final View[] views=createBlockViews(parentElement, childElements, viewFactory);  //create the child views
		replace(0, getViewCount(), views);  //load our created views as children
	}

	/**Creates block views for given elements.
		This is called by the <a href="#loadChildren">loadChildren</a> method as
		well as other block-like classes such as tables.
		Inline children will not be created normally but will be wrapped in one or
		more anonymous views. Inline views consisting only of whitespace will be
		given hidden views.
	@param parentElement The element that will be used as the parent of any anonymous block children created.
	@param elements The elements to convert to block elements and then create block views.
	@param viewFactory The factory used to create child views.
	@return An array of created child views, which may be empty if there are no elements or an invalid view factory was passed.
	@see #getBlockElements(Element, Element[])
	*/
	public static View[] createBlockViews(final Element parentElement, Element[] elements, final ViewFactory viewFactory)
	{
		if(viewFactory!=null) //if we have a view factory
		{
			final Document document=parentElement.getDocument();  //get a reference to the document
			elements=getBlockElements(parentElement, elements);	//making sure all the elements are block elements
			final int elementCount=elements.length;  //find out how many child elements there are
			if(elementCount>0)	//if there are child elements
			{
				final List<View> viewList=new ArrayList<View>(elementCount);  //create a list in which to store elements, knowing that we won't have more views than elements
				for(int i=0; i<elementCount; ++i)	//look at each of the child elements
				{
					final Element element=elements[i];	//get a reference to this element
					final AttributeSet attributeSet=element.getAttributes();	//get the attribute set of this element
					if(elementCount>1 && XMLStyleUtilities.isAnonymous(attributeSet))	//if this is an anonymous element, but it's not the only element
					{
						try
						{
							final String text=document.getText(element.getStartOffset(), element.getEndOffset()-element.getStartOffset());
							//TODO bring back for efficiency				  document.getText(childElement.getStartOffset(), childElement.getEndOffset()-childElement.getStartOffset(), segment);
									//if there are no visible characters (or the end-of-element character mark), and this isn't really just an empty element
							if(CharSequenceUtilities.notCharIndexOf(text, CharacterConstants.WHITESPACE_CHARS+CharacterConstants.CONTROL_CHARS+XMLDocument.ELEMENT_END_CHAR)<0	
									&& !XMLStyleUtilities.isXMLEmptyElement(attributeSet))
							{
		//G***del Debug.trace("found whitespace inside element: ", XMLStyleConstants.getXMLElementName(attributeSet)); //G***del
								viewList.add(new InvisibleView(element));  //create a hidden view for the whitespace inline elements and add it to our list of views
								continue;	//skip further processing of this child and go to the next one								
							}
						}
						catch(BadLocationException badLocationException)  //if we tried to access an invalid location (this shouldn't happen unless there are problems internal to an element)
						{
							throw new AssertionError(badLocationException);  //report the error
						}
					}
					createViews(element, viewFactory, viewList); //create as many views as needed for the child element and add the views to our list						
				}
				return viewList.toArray(new View[viewList.size()]);  //convert the list of views to an array
			}
		}
		return NO_VIEWS; //if there is no view factory (the parent view has somehow changed) or no elements, just return an empty array of views
	}

	/**Ensures that each given element is a block element.
	Inline child elements will be wrapped in one or more anonymous elements.
	@param parentElement The element that will be used as the parent of any anonymous block children created.
	@param elements The elements to convert to block elements.
	@return A list of child elements, which may be empty if there are no child elements.
	*/
	public static Element[] getBlockElements(final Element parentElement, final Element[] elements)
	{
		final int elementCount=elements.length;  //find out how many elements there are
		if(elementCount>0)	//if there are elements
		{
			final List<Element> elementList=new ArrayList<Element>(elementCount);  //create a list in which to store elements
			final List<Element> inlineElementList=new ArrayList<Element>(elementCount);  //create a list in which to store inline child elements
			for(int i=0; i<elementCount; ++i) //look at each child element
			{
				final Element element=elements[i];  //get a reference to this element	
				final AttributeSet attributeSet=element.getAttributes();	//get the attributes of the element
				final CSSStyleDeclaration cssStyle=XMLCSSStyleUtilities.getXMLCSSStyle(attributeSet); //get the CSS style of the element (this method make sure the attributes are present)
					//see if this element is inline (text is always inline, regardless of what the display property says)
				final boolean isInline=XMLCSSUtilities.isDisplayInline(cssStyle) || AbstractDocument.ContentElementName.equals(element.getName());
				if(isInline) //if this is an inline child element
				{
					inlineElementList.add(element);  //add the element to the inline element list
				}
				else  //if this is a block element
				{
					if(inlineElementList.size()>0)	//if we've started but not finished an anonymous block, yet
					{
						elementList.add(createAnonymousBlockElement(parentElement, inlineElementList));	//create an anonymous block element and clear the list
					}
					elementList.add(element); //add the child element normally
				}
			}
			if(inlineElementList.size()>0)	//if we started an anonymous block but never finished it (i.e. the last element was inline)
			{
				elementList.add(createAnonymousBlockElement(parentElement, inlineElementList));	//create an anonymous block element and clear the list
			}
			return elementList.toArray(new Element[elementList.size()]);  //convert the list of elements to an array
		}
		else	//if there are no child elements
		{
			return NO_ELEMENTS;	//indicate that there are no child elements, block or otherwise
		}
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
	protected static View createAnonymousBlockView(final Element parentElement, final Collection<Element> childElementCollection, final ViewFactory viewFactory)
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
	protected static Element createAnonymousBlockElement(final Element parentElement, final Collection<Element> childElementCollection)
	{
		final MutableAttributeSet anonymousAttributeSet=new SimpleAttributeSet();	//create an anonymous attribute set for this anonymous box
		XMLStyleUtilities.setAnonymous(anonymousAttributeSet);	//set the XML name of the attribute set to the anonymous name
		final XMLCSSStyleDeclaration anonymousCSSStyle=new XMLCSSStyleDeclaration(); //create a new style declaration
		anonymousCSSStyle.setDisplay(XMLCSSConstants.CSS_DISPLAY_BLOCK);	//show that the anonymous element should be a block element
		XMLCSSStyleUtilities.setXMLCSSStyle(anonymousAttributeSet, anonymousCSSStyle);	//store the constructed CSS style in the attribute set
				//put the child elements into an array
		final Element[] childElements=childElementCollection.toArray(new Element[childElementCollection.size()]);
		boolean isHidden=true;	//this anonymous view should be hidden unless we find at least one visible view
		for(int i=childElements.length-1; isHidden && i>=0; --i)	//see if any child elements are visible; stop when we find a visible one
		{
			final Element childElement=childElements[i];	//get a reference to this child element
			final AttributeSet childAttributeSet=childElement.getAttributes();	//get the child element attributes
				//if this child element doesn't have a CSS display of "none", it's visible unless something else (the editor kit, for instance) specified it to be hidden 
			if(!XMLCSSConstants.CSS_DISPLAY_NONE.equals(XMLCSSStyleUtilities.getDisplay(childAttributeSet)))
			{
				if(StyleUtilities.isVisible(childAttributeSet))	//if we haven't for some reason we've explicitly set this view to be hidden
				{
					isHidden=false;	//we've found a visible child, so we can't make the anonymous element hidden
				} 
			}
		}
		if(isHidden)	//if no child elements are visible
		{
			StyleUtilities.setVisible(anonymousAttributeSet, false);	//hide the anonymous element
		}
			//create an anonymous element with the elements we've collected
		final Element anonymousElement=new AnonymousElement(parentElement, anonymousAttributeSet, childElementCollection);
		childElementCollection.clear();	//remove all the child elements from the collection
		return anonymousElement;	//return the anonymous element we created
	}

	/**Renders the block view using the given rendering surface and area on that surface.
	Only the children that intersect the clip bounds of the given <code>Graphics</code> will be rendered.
	This version correctly paints XML CSS style.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see XMLCSSViewPainter#paint
	*/
  public void paint(final Graphics graphics, Shape allocation)
  {
		synchronize();	//make sure we have the correct cached property values
		XMLCSSViewPainter.paint(graphics, allocation, this, getAttributes());	//paint our CSS-specific parts (newswing) G***decide whether we want to pass the attributes or not
		super.paint(graphics, allocation);	//do the default painting
  }

	/**Determines how attractive a break opportunity in this view is.
	This is implemented to forward to the superclass for axis perpendicular to
	the flow axis. Along the flow axis, the best break weight of child views
	within the allowed span is returned.
	If this view does not like breaks after it, this method always returns <code>View.BadBreakWeight</code>.
	@param axis The breaking axis, either View.X_AXIS or View.Y_AXIS.
	@param pos The potential location of the start of the broken view (>=0).
		This may be useful for calculating tab positions.
	@param len Specifies the relative length from <var>pos</var> where a potential
		break is desired (>=0).
	@return The weight, which should be a value between <code>View.ForcedBreakWeight</code>
		and <code>View.BadBreakWeight.</code>
	*/
	public int getBreakWeight(int axis, float pos, float len)	//TODO add support for requiring a certain number of child views
	{
		if(axis==getAxis())	//if they want to break along our tiling axis
		{
			final String pageBreakAfter=XMLCSSStyleUtilities.getPageBreakAfter(getAttributes());	//see how the view considers breaking after it
				//if we should avoid breaking after this view, and the provided length is more than we need (i.e. we aren't being asked to break in our middle)
			if(XMLCSSConstants.CSS_PAGE_BREAK_AFTER_AVOID.equals(pageBreakAfter) && len>getPreferredSpan(axis))
			{
				return BadBreakWeight;	//don't allow breaking
			}
			else	//if we aren't break-averse, get the highest break weight available; this has the advantage of allowing invisible views to return their low break weight
			{
				final float marginSpan=(axis==X_AXIS) ? getLeftInset()+getRightInset() : getTopInset()+getBottomInset();	//see how much margin we have to allow for
				int bestBreakWeight=BadBreakWeight;	//start out assuming we can't break
				float spanLeft=len-marginSpan;	//find out how much span we have left (the margins will always be there)
				final int viewCount=getViewCount();	//find out how many child views there are
				for(int i=0; i<viewCount && spanLeft>0; ++i)	//look at each child view until we run out of span
				{
					final View view=getView(i);	//get this child view
					final int breakWeight=view.getBreakWeight(axis, pos, spanLeft);	//get the break weight of this view
					if(breakWeight>bestBreakWeight)	//if this break weight is better than the one we already have
					{
						bestBreakWeight=breakWeight;	//update our best break weight
					}
					spanLeft-=view.getPreferredSpan(axis);	//update the amount of span we've used by this point
				}
				return bestBreakWeight;	//return the best break weight we found
			}
		}
		else	//if they want to break along another axis
		{
			return super.getBreakWeight(axis, pos, len);	//return the default break weight
		}
	}

	/**Breaks this view on the given axis at the given length.
	This implementation delegates to the view break strategy.
	@param axis The axis to break along, either View.X_AXIS or View.Y_AXIS.
	@param offset The location in the model where the fragment should start its representation (>=0).
	@param pos The position along the axis that the broken view would occupy (>=0).
	@param length Specifies the distance along the axis where a potential break is desired (>=0).
	@return The fragment of the view that represents the given span, or the view itself if it cannot be broken
	@see ViewBreakStrategy#breakView()
	*/
	public View breakView(final int axis, final int offset, final float pos, final float length)
	{
		final float marginSpan=(axis==X_AXIS) ? getLeftInset()+getRightInset() : getTopInset()+getBottomInset();	//see how much margin we have to allow for
		return getBreakStrategy().breakView(this, axis, offset, pos, length-marginSpan, this);	//ask the view break strategy to break our view, using this view as the view fragment factory
	}
	
	/**Creates a view that represents a portion of the element.
	This implementation delegates to the view break strategy.
	@param p0 The starting offset (>=0). This should be a value greater or equal
		to the element starting offset and less than the element ending offset.
	@param p1 The ending offset (>p0).  This should be a value less than or
		equal to the elements end offset and greater than the elements starting offset.
	@return The view fragment, or itself if the view doesn't support breaking into fragments.
	@see ViewBreakStrategy#createFragment()
	*/
	public View createFragment(int p0, int p1)
	{
		return getBreakStrategy().createFragment(this, p0, p1, this);	//ask the view break strategy to break our view, using this view as the view fragment factory
	}

	/**Creates a fragment view into which pieces of this view will be placed.
	@param isFirstFragment Whether this fragment holds the first part of the
		original view.
	@param isLastFragment Whether this fragment holds the last part of the
		original view.
	*/
	public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
	{
	  return new XMLFragmentBlockView(getElement(), getAxis(), this, isFirstFragment, isLastFragment);	//create a fragment of this view
	}

	/**Whether or not the cached values have been synchronized.*/
	protected boolean cacheSynchronized=false;  //G***see if we should make this private with an accessor method

	/**Synchronizes the view's cached valiues with the model. This causes the
		font, metrics, color, etc to be recached if the cache has been invalidated.
	*/
	protected void synchronize()
	{
		if(!cacheSynchronized)	//if the cache isn't synchronized
			setPropertiesFromAttributes();	//calculate our properties from our attributes
	}

	/**Sets the cached properties from the attributes.*/
	protected void setPropertiesFromAttributes()
	{
/*G***del
Debug.trace("*****Inside XMLBlockView.setPropertiesFromAttributes()");
Debug.trace("element name is:", getElement().getName());	//G***del
Debug.trace("element is:", getElement());	//G***del
Debug.trace("XML element name is:", XMLStyleUtilities.getXMLElementName(getAttributes()));	//G***del
Debug.trace("element attributes is: ", getElement()!=null ? getElement().getAttributes() : null);	//G***del
Debug.trace("getAttributes() is: ", getAttributes());	//G***del
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
			//G***probably have some other external helper class that sets the margins based upon the attributes
			final short marginTop=(short)Math.round(XMLCSSStyleUtilities.getMarginTop(attributeSet)); //get the top margin from the attributes
			final short marginLeft=(short)Math.round(XMLCSSStyleUtilities.getMarginLeft(attributeSet, font)); //get the left margin from the attributes
Debug.trace("margin left:", marginLeft);
			final short marginBottom=(short)Math.round(XMLCSSStyleUtilities.getMarginBottom(attributeSet)); //get the bottom margin from the attributes
			final short marginRight=(short)Math.round(XMLCSSStyleUtilities.getMarginRight(attributeSet, font)); //get the right margin from the attributes
		  setInsets(marginTop, marginLeft, marginBottom, marginRight);	//G***fix; testing
			}
		}
		cacheSynchronized=true;	//show that we now have the most up-to-date information in the cache
	}

	/**@return The left inset (>=0), ensuring the information is up to date.*/
	protected short getLeftInset()
	{
		synchronize();	//make sure our cached properties are updated.
		return super.getLeftInset();	//return the value
	}

	/**@return The right inset (>=0), ensuring the information is up to date.*/
	protected short getRightInset()
	{
		synchronize();	//make sure our cached properties are updated.
		return super.getRightInset();	//return the value
	}

	/**@return The top inset (>=0), ensuring the information is up to date.*/
	protected short getTopInset()
	{
		synchronize();	//make sure our cached properties are updated.
		return super.getTopInset();	//return the value
	}

	/**@return The bottom inset (>=0), ensuring the information is up to date.*/
	protected short getBottomInset()
	{
		synchronize();	//make sure our cached properties are updated.
		return super.getBottomInset();	//return the value
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
