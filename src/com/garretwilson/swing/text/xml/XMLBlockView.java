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
import com.garretwilson.swing.text.ContainerView;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;	//G***maybe change to XMLStyleConstants
import com.garretwilson.swing.text.xml.css.XMLCSSView;
import com.garretwilson.swing.text.xml.css.XMLCSSViewPainter;
import com.garretwilson.text.CharacterConstants;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSConstants;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSStyleDeclaration;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSUtilities;
import com.garretwilson.util.Debug;
import org.w3c.dom.css.CSSStyleDeclaration;

/**A view that arranges its children into a box shape by tiling its children along an axis, with no constraints.
Any non-block child views are wrapped in anonymous block views.
@author Garret Wilson
@see javax.swing.text.BoxView
*/
//G***fix public class XMLBlockView extends com.garretwilson.swing.text.CompositeView implements XMLCSSView	//G***newswing
public class XMLBlockView extends ContainerView implements XMLCSSView
{

	/**The shared empty array of elements.*/
	protected final static Element[] NO_ELEMENTS=new Element[0];

	/**Whether we're allowed to expand horizontally.*/
	protected final boolean isExpandX;

	/**Whether we're allowed to expand vertically.*/
	protected final boolean isExpandY;

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
		final View[] createdViews=createBlockElementChildViews(getElement(), viewFactory);  //create the child views
		replace(0, 0, createdViews);  //load our created views as children
	}

	/**Creates child views of a block element.
		This is called by the <a href="#loadChildren">loadChildren</a> method as
		well as other block-like classes such as tables.
		Inline children will not be created normally but will be wrapped in one or
		more anonymous views. Inline views consisting only of whitespace will be
		given hidden views.
	@param element The element representing a block view.
	@param viewFactory The factory used to create child views.
	@return An array of created child views, which may be empty if there are no
		child elements or an invalid view factory was passed.
	@see CompositeView#setParent
	@see #loadChildren
	*/
	public static View[] createBlockElementChildViews(final Element element, final ViewFactory viewFactory)
	{
		if(viewFactory!=null) //if we have a view factory
		{
			final Document document=element.getDocument();  //get a reference to the document
			final Element[] childElements=getChildElementsAsBlockElements(element);	//get the child elements, making sure they are all block elements
			final int childElementCount=childElements.length;  //find out how many child elements there are
			if(childElementCount>0)	//if there are child elements
			{
				final List<View> childViewList=new ArrayList<View>(childElementCount);  //create a list in which to store elements, knowing that we won't have more views than child elements
				for(int i=0; i<childElementCount; ++i)	//look at each of the child elements
				{
					final Element childElement=childElements[i];	//get a reference to his child element
					final AttributeSet childAttributeSet=childElement.getAttributes();	//get the attribute set of this child
					if(childElementCount>1 && XMLCSSStyleUtilities.isAnonymous(childAttributeSet))	//if this is an anonymous element, but it's not the only child element
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
							throw new AssertionError(badLocationException);  //report the error
						}
					}

					childViewList.add(viewFactory.create(childElement)); //create a view normally for the child element and add the view to our list						
				}
				return childViewList.toArray(new View[childViewList.size()]);  //convert the list of views to an array
			}
		}
		return NO_VIEWS; //if there is no view factory (the parent view has somehow changed) or no elements, just return an empty array of views
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
		final int childElementCount=element.getElementCount();  //find out how many child elements there are
		if(childElementCount>0)	//if there are child elements
		{
			final Document document=element.getDocument();  //get a reference to the document
			final List<Element> childElementList=new ArrayList<Element>(childElementCount);  //create a list in which to store elements, knowing that we won't have more views than child elements
			final List<Element> inlineChildElementList=new ArrayList<Element>(childElementCount);  //create a list in which to store inline child elements; we know we'll never have more inline child elements than there are children of the original element
			for(int childIndex=0; childIndex<childElementCount; ++childIndex) //look at each child element
			{
				final Element childElement=element.getElement(childIndex);  //get a reference to this child element	
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
					{
						childElementList.add(createAnonymousBlockElement(element, inlineChildElementList));	//create an anonymous block element and clear the list
					}
					childElementList.add(childElement); //add the child element normally
				}
			}
			if(inlineChildElementList.size()>0)	//if we started an anonymous block but never finished it (i.e. the last child was inline)
			{
				childElementList.add(createAnonymousBlockElement(element, inlineChildElementList));	//create an anonymous block element and clear the list
			}
			return childElementList.toArray(new Element[childElementList.size()]);  //convert the list of elements to an array
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
				if(XMLStyleUtilities.isVisible(childAttributeSet))	//if we haven't for some reason we've explicitly set this view to be hidden
				{
					isHidden=false;	//we've found a visible child, so we can't make the anonymous element hidden
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


	/**Renders the block view using the given rendering surface and area on that surface.
	Only the children that intersect the clip bounds of the given <code>Graphics</code> will be rendered.
	This version correctly paints XML CSS style.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see XMLCSSViewPainter#paint
	*/
  public void paint(final Graphics graphics, Shape allocation)
  {
		XMLCSSViewPainter.paint(graphics, allocation, this, getAttributes());	//paint our CSS-specific parts (newswing) G***decide whether we want to pass the attributes or not
		super.paint(graphics, allocation);	//do the default painting
  }


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
Debug.trace("Inside XMLBlockView.breakView axis: ", axis, "p0:", p0, "pos:", pos, "len:", len, "name:", XMLStyleUtilities.getXMLElementName(getAttributes()));	//G***del

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
