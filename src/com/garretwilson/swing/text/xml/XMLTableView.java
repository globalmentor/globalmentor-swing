package com.garretwilson.swing.text.xml;

import java.awt.*;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
/*G***del
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.TableView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
*/
import java.util.*; //G***del if not needed
//G***del import com.garretwilson.swing.text.TestTableView; //G***del maybe
import com.garretwilson.swing.text.AnonymousElement;
import com.garretwilson.swing.text.DefaultViewFactory;
import com.garretwilson.swing.text.xml.css.XMLCSSStyleUtilities;
import com.garretwilson.swing.text.xml.css.XMLCSSViewPainter;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSConstants;
//G***del import com.garretwilson.text.xml.stylesheets.css.XMLCSSPrimitiveValue;
import com.garretwilson.text.xml.stylesheets.css.XMLCSSStyleDeclaration;  //G***del if we can
import com.garretwilson.text.xml.stylesheets.css.XMLCSSUtilities;
import com.garretwilson.util.Debug;
import javax.swing.text.html.HTML;  //G***del
import org.w3c.dom.css.*;

/**Table view for elements with the CSS display attribute "table".
Modified from javax.swing.text.html.TableView.
@author Garret Wilson
@see javax.swing.text.TableView
@see javax.swing.text.html.TableView
*/
class XMLTableView extends TableView implements Cloneable
{

/*G***del
  public void setSize(float width, float height)	//G***del; testing tableflow
	{
System.out.println("XMLTableView.setSize(), width: "+width+" height: "+height);	//G***del
		super.setSize(width, height);
	}
*/


	public int testGetViewCount() {return getViewCount();}  //G***del; testnig

	public int testGetSpan(int axis, int row) {return getSpan(axis, row);}


	/**Constructs an <code>XMLTablelView</code> for the given element.
	@param element The element that this view is responsible for.
	*/
	public XMLTableView(final Element element)
	{
		super(element);	//construct the parent class

//G***del	super(element, View.Y_AXIS);
	rows = new Vector();
	gridValid = false;

		setPropertiesFromAttributes();	//set the properties from the attributes, recognizing CSS properties
	}

	/**Sets this view's parent. This is guaranteed to be called before any other
		methods if the parent view is functioning properly. This is implemented to
		forward to the superclass as well as call the
		<a href="#setPropertiesFromAttributes">setPropertiesFromAttributes</a> method
		to set the paragraph properties from the CSS attributes. The call is made at
		this time to ensure the ability to resolve upward through the parents view
		attributes.
	@param parent The new parent, or null if the view is being removed from a parent
		it was previously added to.
	*/
	public void setParent(View parent)
	{
		super.setParent(parent);	//set the parent normally
/*G***fix or del
			//if there are children, indicate preference changes and notify children for correct flowing (newswing)
		if ((parent!=null) && (getViewCount()!=0))	//if there is a parent view and there are children (newswing)
		{
//G***del Debug.trace("Changing the table view preferences.");	//G***del

		  preferenceChanged(null, true, true);	//show that our preferences have changed
//G***del Debug.trace("This table has "+getViewCount()+" children; now setting their parents.");	//G***del
			for(int i=0; i<getViewCount(); ++i)	//look at each child view
			{
				getView(i).setParent(this);	//tell it which parent it has (this function will also notify that its preferences have changed)
			}
		}
*/
		setPropertiesFromAttributes();	//get our CSS properties from the attributes
	}


//G***del	public static XMLTableRowView createXMLTableRowView(Element elem) {return new XMLTableRowView(elem);}	//G***testing; comment


		/**
		 * Creates a new table row.
		 *
		 * @param elem an element
		 * @return the row
		 */
/*G***del if not needed
	protected TableRow createTableRow(Element elem) {
	// PENDING(prinz) need to add support for some of the other
	// elements, but for now just ignore anything that is not
	// a TR.
	Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute );
	if (o == HTML.Tag.TR) {
			return new RowView(elem);
	}
	return null;
		}

		protected StyleSheet getStyleSheet() {
	HTMLDocument doc = (HTMLDocument) getDocument();
	return doc.getStyleSheet();
		}
*/

		/**
		 * Update any cached values that come from attributes.
		 */
	/**Updates our cached CSS property values from the attributes.*/
	protected void setPropertiesFromAttributes()
	{
/*G***fix
	StyleSheet sheet = getStyleSheet();
	attr = sheet.getViewAttributes(this);
	painter = sheet.getBoxPainter(attr);
	if (attr != null) {
			setInsets((short) painter.getInset(TOP, this),
					(short) painter.getInset(LEFT, this),
				(short) painter.getInset(BOTTOM, this),
					(short) painter.getInset(RIGHT, this));
	}
*/
	}

	/**Calculates the requirements of the table along the minor axis (i.e. the major
		axis of the rows). This is implemented to provide the superclass behavior and
		then adjust it if the CSS width or height attribute is specified and applicable
		to the axis.
	@param axis The axis for calculating requirements.
	@param r The requirements.
	*/
/*G***fix
	protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r)
	{
		final SizeRequirements superRequirements=super.calculateMinorAxisRequirements(axis, r);	//get the parent class requirements
//G***fix	adjustSizeForCSS(axis, rr);
		return superRequirements;	//return our requirements
	}
*/

/*G***fix
		void adjustSizeForCSS(int axis, SizeRequirements r) {
	if (axis == X_AXIS) {
			Object widthValue = attr.getAttribute(CSS.Attribute.WIDTH);
			if (widthValue != null) {
		int width = (int) ((CSS.LengthValue)widthValue).getValue();
		r.minimum = r.preferred = width;
		r.maximum = Math.max(r.maximum, width);
			}
	} else {
			Object heightValue = attr.getAttribute(CSS.Attribute.HEIGHT);
			if (heightValue != null) {
		int height = (int) ((CSS.LengthValue)heightValue).getValue();
		r.minimum = r.preferred = height;
		r.maximum = Math.max(r.maximum, height);
			}
	}
		}
*/

	/**Returns the <code>ViewFactory</code> to use for the table. This is
		implemented to create a new factory that creates special views for
		special internal table elements and uses the given view factory to create
		all other views.
	@param defaultViewFactory The view factory to use for non-table-specific elements.
	@return The view factory that will create views or delegate to the default view factory.
	*/
	ViewFactory createViewFactory(ViewFactory defaultViewFactory)
	{
		return new XMLTableFactory(defaultViewFactory);	//create our own view factory to create table-related views G***don't create this on the fly
	}

	/* ***View method*** */


/*G***fix
    public float getPreferredSpan(int axis) {
Debug.trace("XMLTableView.getPreferredSpan axis: "+axis+" ="+super.getPreferredSpan(axis)); //G***del
		return super.getPreferredSpan(axis);	//return the preferred span

    }
*/

//G***important all these span methods really need to be fixed; why are the calculated spans getting changed?

    public float getPreferredSpan(int axis) { //G***del; testing
//G***del Debug.trace("XMLTableView.getPreferredSpan axis: "+axis+" ="+super.getPreferredSpan(axis)); //G***del



		final float span=super.getPreferredSpan(axis);	//G***del
/*G***fix
	long min = 0; //G***testing
	long pref = 0;
	long max = 0;
	for (int i = 0; i < columnRequirements.length; i++) {
	    SizeRequirements req = columnRequirements[i];
	    min += req.minimum;
	    pref += req.preferred;
	    max += req.maximum;
	}
return pref;
*/

//G***bring back		return super.getPreferredSpan(axis);	//return the preferred span
		  return span;
    }

    public float getMinimumSpan(int axis) {

//G***del Debug.trace("XMLTableView.getMinimum axis: "+axis+" ="+super.getMinimumSpan(axis)); //G***del

		final float span=super.getMinimumSpan(axis);	//G***del
/*G***fix
	long min = 0; //G***testing
	long pref = 0;
	long max = 0;

	for (int i = 0; i < columnRequirements.length; i++) {
	    SizeRequirements req = columnRequirements[i];
	    min += req.minimum;
	    pref += req.preferred;
	    max += req.maximum;
	}
return min;
*/
//G***bring back		return super.getMinimumSpan(axis);	//return the preferred span
		  return span;
    }

	/**Determines the maximum span for the view along an axis. This implementation
		returns the preferred view, as the table will not stretch beyond that amount.
	@param axis The axis for the size (either View.X_AXIS or View.Y_AXIS).
	@return The maximum span the view can be rendered into.
	@see TableView#getPreferredSpan
	*/
	public float getMaximumSpan(int axis)
	{
//G***del Debug.trace("XMLTableView.getMaximumSpan axis: "+axis+" ="+super.getMaximumSpan(axis)); //G***del
		final float span=super.getMaximumSpan(axis);	//G***del
/*G***fix
	long min = 0; //G***testing
	long pref = 0;
	long max = 0;
	for (int i = 0; i < columnRequirements.length; i++) {
	    SizeRequirements req = columnRequirements[i];
	    min += req.minimum;
	    pref += req.preferred;
	    max += req.maximum;
	}
//G***bring back		return super.getMaximumSpan(axis);	//return the preferred span
//G***fix		return getPreferredSpan(axis);	//return the preferred span
return max;
*/
		return span;
	}

		/**
		 * Fetches the attributes to use when rendering.  This is
		 * implemented to multiplex the attributes specified in the
		 * model with a StyleSheet.
		 */
/*G***fix
		public AttributeSet getAttributes() {
	return attr;
		}
*/

		/**
		 * Renders using the given rendering surface and area on that
		 * surface.  This is implemented to delegate to the css box
		 * painter to paint the border and background prior to the
		 * interior.
		 *
		 * @param g the rendering surface to use
		 * @param allocation the allocated region to render into
		 * @see View#paint
		 */
/*G***fix
		public void paint(Graphics g, Shape allocation) {
	Rectangle a = (Rectangle) allocation;
	painter.paint(g, a.x, a.y, a.width, a.height, this);
	super.paint(g, a);
		}
*/

	/**Renders using the given rendering surface and area on that surface. This is
		implemented to paint the style attributes.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see BoxView#paint
	*/
	public void paint(final Graphics graphics, final Shape allocation)
	{
//G***delDebug.trace("Inside XMLBlockView.paint()");
		XMLCSSViewPainter.paint(graphics, allocation, this, getAttributes());	//paint our CSS-specific parts (newswing)
		super.paint(graphics, allocation);  //do the default painting
	}

	/**Returns the the view factory, which is our <code>XMLTableFactory</code> that
		creates table-related views or delegates the responsibility to the default
		view factory.
	@return The view factory for the table.
	*/
	public ViewFactory getViewFactory()
	{
		return createViewFactory(super.getViewFactory());	//create our own view factory with the parent class' view factory as the default to create views for elements we don't deal with
	}

		/**
		 * Gives notification that something was inserted into
		 * the document in a location that this view is responsible for.
		 * This replaces the ViewFactory with an implementation that
		 * calls through to the createTableRow and createTableCell
		 * methods.   If the element given to the factory isn't a
		 * table row or cell, the request is delegated to the factory
		 * passed as an argument.
		 *
		 * @param e the change information from the associated document
		 * @param a the current allocation of the view
		 * @param f the factory to use to rebuild if the view has children
		 * @see View#insertUpdate
		 */
	//G***comment
	public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f)
	{
		super.insertUpdate(e, a, createViewFactory(f));
	}

		/**
		 * Gives notification that something was removed from the document
		 * in a location that this view is responsible for.
		 * This replaces the ViewFactory with an implementation that
		 * calls through to the createTableRow and createTableCell
		 * methods.   If the element given to the factory isn't a
		 * table row or cell, the request is delegated to the factory
		 * passed as an argument.
		 *
		 * @param e the change information from the associated document
		 * @param a the current allocation of the view
		 * @param f the factory to use to rebuild if the view has children
		 * @see View#removeUpdate
		 */
	//G***comment
	public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f)
	{
		super.removeUpdate(e, a, createViewFactory(f));
	}

		/**
		 * Gives notification from the document that attributes were changed
		 * in a location that this view is responsible for.
		 * This replaces the ViewFactory with an implementation that
		 * calls through to the createTableRow and createTableCell
		 * methods.   If the element given to the factory isn't a
		 * table row or cell, the request is delegated to the factory
		 * passed as an argument.
		 *
		 * @param e the change information from the associated document
		 * @param a the current allocation of the view
		 * @param f the factory to use to rebuild if the view has children
		 * @see View#changedUpdate
		 */
	//G***comment
	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f)
	{
		super.changedUpdate(e, a, createViewFactory(f));
	}

	/**Creates a shallow copy. This is used by the
		<code>createFragment()</code> and <code>breakView()</code> methods.
	@return The copy of this view.
	*/
	protected final Object clone() throws CloneNotSupportedException
	{
		return new XMLTableView(getElement());	//create a view based on the same element
	}

	/**Each fragment is a subset of the content in the breaking view.
	@return The starting offset of this view, which is the starting offset of the
		view with the lowest starting offset
	@see View#getRange
	*/
	public int getStartOffset()
	{
		int startOffset=Integer.MAX_VALUE;	//we'll start out with a high number, and we'll end up with the lowest starting offset of all the views
		final int numViews=getViewCount();	//find out how many view are on this page
		if(numViews>0)	//if we have child views
		{
			for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
			{
				final View view=getView(viewIndex);	//get a reference to this view
				startOffset=Math.min(startOffset, view.getStartOffset());	//if this view has a lower starting offset, use its starting offset
			}
			return startOffset;	//return the starting offset we found
		}
		else	//if we don't have any child views
			return super.getStartOffset();	//return the default starting offset
	}

	/**Each fragment is a subset of the content in the breaking view.
	@return The ending offset of this view, which is the ending offset of the
		view with the largest ending offset
	@see View#getRange
	*/
	public int getEndOffset()
	{
		int endOffset=0;	//start out with a low ending offset, and we'll wind up with the largest ending offset
		final int numViews=getViewCount();	//find out how many view are on this page
		if(numViews>0)	//if we have child views
		{
			for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
			{
				final View view=getView(viewIndex);	//get a reference to this view
				endOffset=Math.max(endOffset, view.getEndOffset());	//if this view has a larger ending offset, use that instead
			}
			return endOffset;	//return the largest ending offset we found
		}
		else	//if we don't have any child views
			return super.getEndOffset();	//return the default ending offset
	}

	/**Determines how attractive a break opportunity in this view is.
		This is implemented to forward to the superclass for the A axis. Along the
		Y axis, <code>GoodBreakWeight</code> will be returned.
	@param axis The breaking axis, either View.X_AXIS or View.Y_AXIS.
	@param pos The potential location of the start of the broken view (>=0).
		This may be useful for calculating tab positions.
	@param len Specifies the relative length from <em>pos</em> where a potential
		break is desired (>=0).
	@return The weight, which should be a value between View.ForcedBreakWeight
		and View.BadBreakWeight.
	@see XMLBlockView
	@see GoodBreakWeight
	*/
	public int getBreakWeight(int axis, float pos, float len)
	{
return BadBreakWeight;  //G***testing
/*G***fix
		if(axis==Y_AXIS)	//if they want to break along the Y axis
			return GoodBreakWeight;	//show that this break spot will work
		else	//if they want to break along another axis besides the one we know about
			return super.getBreakWeight(axis, pos, len);	//return the default break weight
*/
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
		if(axis==Y_AXIS)	//if they want to break along the Y axis
		{
			if(len>=getPreferredSpan(axis))	//if the break is as large or larger than the view
				return this;	//just return ourselves; there's no need to try to break anything
			else if(getViewCount()>0)	//if we have child views
			{
//G***bring back if needed				final XMLBlockView fragmentView=(XMLBlockView)clone();	//create a clone of this view
				try
				{
					final View fragmentView=(View)clone();	//create a clone of this view
//G***del					((XMLTableView)fragmentView).setSize(getWidth(), getHeight());	//G***testing tableflow
					float totalSpan=0;	//we'll use this to accumulate the size of each view to be included
					int startOffset=p0;	//we'll continually update this as we create new child view fragments
					int childIndex;	//start looking at the first child to find one that can be included in our break
					for(childIndex=0; childIndex<getViewCount() && getView(childIndex).getEndOffset()<=startOffset; ++childIndex);	//find the first child that ends after our first model location
					for(; childIndex<getViewCount() && totalSpan<len; ++childIndex)	//look at each child view at and including the first child we found that will go inside this fragment, and keep adding children until we find enough views to fill up the space or we run out of views
					{
						View childView=getView(childIndex);	//get a reference to this child view; we may change this variable if we have to break one of the child views

/*G***del if not needed tableflow
						childView.preferenceChanged(null, true, true);	//G***del; testing tableflow
						childView.setSize(getWidth(), getHeight());	//G***testing tableflow
*/

						if(totalSpan+childView.getPreferredSpan(axis)>len)	//if this view is too big to fit into our space
						{
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
						}
						if(childView!=null)	//if we have something to add
						{
							fragmentView.append(childView);	//add this child view, which could have been chopped up into a fragment itself

					fragmentView.preferenceChanged(null, true, true);	//G***del; testing tableflow
					fragmentView.setSize(getWidth(), getHeight());	//G***testing tableflow


							totalSpan+=childView.getPreferredSpan(axis);	//show that we've used up more space
						}
						else	//if we needed more room but couldn't break a view
							break;	//stop trying to fit things
					}
					fragmentView.setParent(getParent());  //make sure the fragment has the correct parent
//G***del					((XMLTableView)fragmentView).invalidateGrid();	//G***testing tableflow
//G***del					fragmentView.preferenceChanged(null, true, true);	//G***testing tableflow
					return fragmentView;	//return the new view that's a fragment of ourselves
				}
				catch(CloneNotSupportedException e)	//if cloning isn't supported by this node
				{
					return this;	//don't even try to break ourselves
				}
			}
		}
		return this;	//if they want to break along another axis or we weren't able to break, return our entire view
	}


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
		if(p0<=getStartOffset() && p1>=getEndOffset())	//if the range they want encompasses all of our view
			return this;	//return ourselves; there's no use to try to break ourselves up
		else	//if the range they want only includes part of our view
		{
			try
			{
				final View fragmentView=(View)clone();	//create a clone of this view
//G***del				((XMLTableView)fragmentView).setSize(getWidth(), getHeight());	//G***testing tableflow
				for(int i=0; i<getViewCount(); ++i)	//look at each child view
				{
					final View childView=getView(i);	//get a reference to this child view

/*G***del if not needed tableflow
					childView.preferenceChanged(null, true, true);	//G***del; testing tableflow
					childView.setSize(getWidth(), getHeight());	//G***testing tableflow
*/

					if(childView.getStartOffset()<p1 && childView.getEndOffset()>p0)	//if this view is within our range
					{
						final int startPos=Math.max(p0, childView.getStartOffset());	//find out where we want to start, staying within this child view
						final int endPos=Math.min(p1, childView.getEndOffset());	//find out where we want to end, staying within this child view
						fragmentView.append(childView.createFragment(startPos, endPos));	//add a portion (or all) of this child to our fragment


					fragmentView.preferenceChanged(null, true, true);	//G***del; testing tableflow
					fragmentView.setSize(getWidth(), getHeight());	//G***testing tableflow


					}
				}
				fragmentView.setParent(getParent());  //make sure the fragment has the correct parent
//G***del				fragmentView.preferenceChanged(null, true, true);	//G***testing tableflow
				return fragmentView;	//return the fragment view we constructed
			}
			catch(CloneNotSupportedException e)	//if cloning isn't supported by this node
			{
				return this;	//don't even try to break ourselves
			}
		}
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
		final View[] createdViews=XMLBlockView.createBlockElementChildViews(getElement(), viewFactory);  //create the child views
		replace(0, 0, createdViews);  //load our created views as children
	}


//G***TableView copies for debugging

    /**
     * Creates a new table row.
     *
     * @param elem an element
     * @return the row
     */
    protected TableRow createTableRow(Element elem) {
	return new XMLTableRowView(elem);
    }

    /**
     * @deprecated Table cells can now be any arbitrary
     * View implementation and should be produced by the
     * ViewFactory rather than the table.
     *
     * @param elem an element
     * @return the cell
     */
    protected TableView.TableCell createTableCell(Element elem) {
	return new TableView.TableCell(elem);
    }

    /**
     * The number of columns in the table.
     */
    int getColumnCount() {
	return columnSpans.length;
    }

    /**
     * Fetches the span (width) of the given column.
     * This is used by the nested cells to query the
     * sizes of grid locations outside of themselves.
     */
    int getColumnSpan(int col) {
	return columnSpans[col];
    }

    /**
     * The number of rows in the table.
     */
    int getRowCount() {
	return rows.size();
    }

    /**
     * Fetches the span (height) of the given row.
     */
    int getRowSpan(int row) {
	View rv = getRow(row);
	if (rv != null) {
	    return (int) rv.getPreferredSpan(Y_AXIS);
	}
	return 0;
    }

    XMLTableRowView getRow(int row) {
	if (row < rows.size()) {
	    return (XMLTableRowView) rows.elementAt(row);
	}
	return null;
    }

    /**
     * Determines the number of columns occupied by
     * the table cell represented by given element.
     */
    /*protected*/ int getColumnsOccupied(View v) {
	// PENDING(prinz) this code should be in the html
	// paragraph, but we can't add api to enable it.
	AttributeSet a = v.getElement().getAttributes();
	String s = (String) a.getAttribute(HTML.Attribute.COLSPAN);
	if (s != null) {
	    try {
		return Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
		// fall through to one column
	    }
	}

	return 1;
    }

    /**
     * Determines the number of rows occupied by
     * the table cell represented by given element.
     */
    /*protected*/ int getRowsOccupied(View v) {
	// PENDING(prinz) this code should be in the html
	// paragraph, but we can't add api to enable it.
	AttributeSet a = v.getElement().getAttributes();
	String s = (String) a.getAttribute(HTML.Attribute.ROWSPAN);
	if (s != null) {
	    try {
		return Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
		// fall through to one row
	    }
	}

	return 1;
    }

    /*protected*/ void invalidateGrid() {
Debug.traceStack(); //G***del
	gridValid = false;
    }

    protected void forwardUpdate(DocumentEvent.ElementChange ec,
				     DocumentEvent e, Shape a, ViewFactory f) {
	super.forwardUpdate(ec, e, a, f);
	// A change in any of the table cells usually effects the whole table,
	// so redraw it all!
	if (a != null) {
	    Component c = getContainer();
	    if (c != null) {
		Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a :
		                   a.getBounds();
		c.repaint(alloc.x, alloc.y, alloc.width, alloc.height);
	    }
	}
    }

    /**
     * Change the child views.  This is implemented to
     * provide the superclass behavior and invalidate the
     * grid so that rows and columns will be recalculated.
     */
    public void replace(int offset, int length, View[] views) {
	super.replace(offset, length, views);
	invalidateGrid();
    }


    /**
     * Fill in the grid locations that are placeholders
     * for multi-column, multi-row, and missing grid
     * locations.
     */
    void updateGrid() {
	if (! gridValid) {
	    // determine which views are table rows and clear out
	    // grid points marked filled.
	    rows.removeAllElements();
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		if (v instanceof XMLTableRowView) {
		    rows.addElement(v);
		    XMLTableRowView rv = (XMLTableRowView) v;
		    rv.clearFilledColumns();
		    rv.setRow(i);
		}
	    }

	    int maxColumns = 0;
	    int nrows = rows.size();
	    for (int row = 0; row < nrows; row++) {
		XMLTableRowView rv = getRow(row);
		int col = 0;
		for (int cell = 0; cell < rv.getViewCount(); cell++, col++) {
		    View cv = rv.getView(cell);
		    // advance to a free column
		    for (; rv.isFilled(col); col++);
		    int rowSpan = getRowsOccupied(cv);
		    int colSpan = getColumnsOccupied(cv);
		    if ((colSpan > 1) || (rowSpan > 1)) {
			// fill in the overflow entries for this cell
			int rowLimit = row + rowSpan;
			int colLimit = col + colSpan;
			for (int i = row; i < rowLimit; i++) {
			    for (int j = col; j < colLimit; j++) {
				if (i != row || j != col) {
				    addFill(i, j);
				}
			    }
			}
			if (colSpan > 1) {
			    col += colSpan - 1;
			}
		    }
		}
		maxColumns = Math.max(maxColumns, col);
	    }

	    // setup the column layout/requirements
	    columnSpans = new int[maxColumns];
	    columnOffsets = new int[maxColumns];
	    columnRequirements = new SizeRequirements[maxColumns];
	    for (int i = 0; i < maxColumns; i++) {
		columnRequirements[i] = new SizeRequirements();
	    }
	    gridValid = true;
	}
    }

    /**
     * Mark a grid location as filled in for a cells overflow.
     */
    void addFill(int row, int col) {
	XMLTableRowView rv = getRow(row);
	if (rv != null) {
	    rv.fillColumn(col);
	}
    }

    /**
     * Layout the columns to fit within the given target span.
     *
     * @param targetSpan the given span for total of all the table
     *  columns.
     * @param reqs the requirements desired for each column.  This
     *  is the column maximum of the cells minimum, preferred, and
     *  maximum requested span.
     * @param spans the return value of how much to allocated to
     *  each column.
     * @param offsets the return value of the offset from the
     *  origin for each column.
     * @returns the offset from the origin and the span for each column
     *  in the offsets and spans parameters.
     */
    protected void layoutColumns(int targetSpan, int[] offsets, int[] spans,
				 SizeRequirements[] reqs) {
	// allocate using the convenience method on SizeRequirements
	SizeRequirements.calculateTiledPositions(targetSpan, null, reqs,
						 offsets, spans);
    }

    /**
     * Perform layout for the minor axis of the box (i.e. the
     * axis orthoginal to the axis that it represents).  The results
     * of the layout should be placed in the given arrays which represent
     * the allocations to the children along the minor axis.  This
     * is called by the superclass whenever the layout needs to be
     * updated along the minor axis.
     * <p>
     * This is implemented to call the
     * <a href="#layoutColumns">layoutColumns</a> method, and then
     * forward to the superclass to actually carry out the layout
     * of the tables rows.
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
Debug.trace("G***search");
	// make grid is properly represented
	updateGrid();
Debug.trace();

	// all of the row layouts are invalid, so mark them that way
	int n = getRowCount();
	for (int i = 0; i < n; i++) {
	    XMLTableRowView row = getRow(i);
	    row.layoutChanged(axis);
	}

Debug.trace();
	// calculate column spans
	layoutColumns(targetSpan, columnOffsets, columnSpans, columnRequirements);
Debug.trace();

	// continue normal layout
	super.layoutMinorAxis(targetSpan, axis, offsets, spans);
    }

    /**
     * Calculate the requirements for the minor axis.  This is called by
     * the superclass whenever the requirements need to be updated (i.e.
     * a preferenceChanged was messaged through this view).
     * <p>
     * This is implemented to calculate the requirements as the sum of the
     * requirements of the columns.
     */
    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
	updateGrid();

	// calculate column requirements for each column
	calculateColumnRequirements(axis);


	// the requirements are the sum of the columns.
	if (r == null) {
	    r = new SizeRequirements();
	}
	long min = 0;
	long pref = 0;
	long max = 0;
	for (int i = 0; i < columnRequirements.length; i++) {
	    SizeRequirements req = columnRequirements[i];
	    min += req.minimum;
	    pref += req.preferred;
	    max += req.maximum;
	}
	r.minimum = (int) min;
	r.preferred = (int) pref;
	if(max<Integer.MAX_VALUE) //G***testing; comment
		r.maximum = (int) max;
	else
		r.maximum = Integer.MAX_VALUE;
	r.alignment = 0;
	return r;
    }

    /*
    boolean shouldTrace() {
	AttributeSet a = getElement().getAttributes();
	Object o = a.getAttribute(HTML.Attribute.ID);
	if ((o != null) && o.equals("debug")) {
	    return true;
	}
	return false;
    }
    */

    /**
     * Calculate the requirements for each column.  The calculation
     * is done as two passes over the table.  The table cells that
     * occupy a single column are scanned first to determine the
     * maximum of minimum, preferred, and maximum spans along the
     * give axis.  Table cells that span multiple columns are excluded
     * from the first pass.  A second pass is made to determine if
     * the cells that span multiple columns are satisfied.  If the
     * column requirements are not satisified, the needs of the
     * multi-column cell is mixed into the existing column requirements.
     * The calculation of the multi-column distribution is based upon
     * the proportions of the existing column requirements and taking
     * into consideration any constraining maximums.
     */
    void calculateColumnRequirements(int axis) {
	// pass 1 - single column cells
	boolean hasMultiColumn = false;
	int nrows = getRowCount();
	for (int i = 0; i < nrows; i++) {
	    XMLTableRowView row = getRow(i);
	    int col = 0;
	    int ncells = row.getViewCount();
	    for (int cell = 0; cell < ncells; cell++, col++) {
		View cv = row.getView(cell);
		for (; row.isFilled(col); col++); // advance to a free column
		int rowSpan = getRowsOccupied(cv);
		int colSpan = getColumnsOccupied(cv);
		if (colSpan == 1) {
		    checkSingleColumnCell(axis, col, cv);
		} else {
		    hasMultiColumn = true;
		    col += colSpan - 1;
		}
	    }
	}

	// pass 2 - multi-column cells
	if (hasMultiColumn) {
	    for (int i = 0; i < nrows; i++) {
		XMLTableRowView row = getRow(i);
		int col = 0;
		int ncells = row.getViewCount();
		for (int cell = 0; cell < ncells; cell++, col++) {
		    View cv = row.getView(cell);
		    for (; row.isFilled(col); col++); // advance to a free column
		    int colSpan = getColumnsOccupied(cv);
		    if (colSpan > 1) {
			checkMultiColumnCell(axis, col, colSpan, cv);
			col += colSpan - 1;
		    }
		}
	    }
	}

	/*
	if (shouldTrace()) {
	    System.err.println("calc:");
	    for (int i = 0; i < columnRequirements.length; i++) {
		System.err.println(" " + i + ": " + columnRequirements[i]);
	    }
	}
	*/
    }

    /**
     * check the requirements of a table cell that spans a single column.
     */
    void checkSingleColumnCell(int axis, int col, View v) {
	SizeRequirements req = columnRequirements[col];
	req.minimum = Math.max((int) v.getMinimumSpan(axis), req.minimum);
	req.preferred = Math.max((int) v.getPreferredSpan(axis), req.preferred);
	req.maximum = Math.max((int) v.getMaximumSpan(axis), req.maximum);
    }

    /**
     * check the requirements of a table cell that spans multiple
     * columns.
     */
    void checkMultiColumnCell(int axis, int col, int ncols, View v) {
	// calculate the totals
	long min = 0;
	long pref = 0;
	long max = 0;
	for (int i = 0; i < ncols; i++) {
	    SizeRequirements req = columnRequirements[col + i];
	    min += req.minimum;
	    pref += req.preferred;
	    max += req.maximum;
	}

	// check if the minimum size needs adjustment.
	int cmin = (int) v.getMinimumSpan(axis);
	if (cmin > min) {
	    /*
	     * the columns that this cell spans need adjustment to fit
	     * this table cell.... calculate the adjustments.  The
	     * maximum for each cell is the maximum of the existing
	     * maximum or the amount needed by the cell.
	     */
	    SizeRequirements[] reqs = new SizeRequirements[ncols];
	    for (int i = 0; i < ncols; i++) {
		SizeRequirements r = reqs[i] = columnRequirements[col + i];
		r.maximum = Math.max(r.maximum, (int) v.getMaximumSpan(axis));
	    }
	    int[] spans = new int[ncols];
	    int[] offsets = new int[ncols];
	    SizeRequirements.calculateTiledPositions(cmin, null, reqs,
						     offsets, spans);
	    // apply the adjustments
	    for (int i = 0; i < ncols; i++) {
		SizeRequirements req = reqs[i];
		req.minimum = Math.max(spans[i], req.minimum);
		req.preferred = Math.max(req.minimum, req.preferred);
		req.maximum = Math.max(req.preferred, req.maximum);
	    }
	}

	// check if the preferred size needs adjustment.
	int cpref = (int) v.getPreferredSpan(axis);
	if (cpref > pref) {
	    /*
	     * the columns that this cell spans need adjustment to fit
	     * this table cell.... calculate the adjustments.  The
	     * maximum for each cell is the maximum of the existing
	     * maximum or the amount needed by the cell.
	     */
	    SizeRequirements[] reqs = new SizeRequirements[ncols];
	    for (int i = 0; i < ncols; i++) {
		SizeRequirements r = reqs[i] = columnRequirements[col + i];
	    }
	    int[] spans = new int[ncols];
	    int[] offsets = new int[ncols];
	    SizeRequirements.calculateTiledPositions(cpref, null, reqs,
						     offsets, spans);
	    // apply the adjustments
	    for (int i = 0; i < ncols; i++) {
		SizeRequirements req = reqs[i];
		req.preferred = Math.max(spans[i], req.preferred);
		req.maximum = Math.max(req.preferred, req.maximum);
	    }
	}

    }

    /**
     * Fetches the child view that represents the given position in
     * the model.  This is implemented to walk through the children
     * looking for a range that contains the given position.  In this
     * view the children do not necessarily have a one to one mapping
     * with the child elements.
     *
     * @param pos  the search position >= 0
     * @param a  the allocation to the table on entry, and the
     *   allocation of the view containing the position on exit
     * @returns  the view representing the given position, or
     *   null if there isn't one
     */
    protected View getViewAtPosition(int pos, Rectangle a) {
        int n = getViewCount();
        for (int i = 0; i < n; i++) {
            View v = getView(i);
            int p0 = v.getStartOffset();
            int p1 = v.getEndOffset();
            if ((pos >= p0) && (pos < p1)) {
                // it's in this view.
		if (a != null) {
		    childAllocation(i, a);
		}
                return v;
            }
        }
	if (pos == getEndOffset()) {
	    View v = getView(n - 1);
	    if (a != null) {
		this.childAllocation(n - 1, a);
	    }
	    return v;
	}
        return null;
    }



    int[] columnSpans;
    int[] columnOffsets;
    SizeRequirements[] columnRequirements;
    Vector rows;
    boolean gridValid;
    static final private BitSet EMPTY = new BitSet();
















/*G***del when works
		// --- variables ---------------------------------------

		private AttributeSet attr;
		private StyleSheet.BoxPainter painter;
*/

		/**
		 * An html row.  This adds storage of the appropriate
		 * css attributes to the superclass behavior.
		 */
	/**A row in an XML table. This version correctly gets attributes from CSS
		attribute properties
	@see javax.swing.text.TableView.TableRow
	@see javax.swing.text.html.TableView.RowView
	*/
	class XMLTableRowView extends TableView.TableRow
	{


/*G***del
  public void setSize(float width, float height)	//G***del; testing tableflow
	{
System.out.println("XMLTableRowView.setSize(), width: "+width+" height: "+height);	//G***del
		super.setSize(width, height);
	}
*/


		/**Constructs an <code>XMLRowView</code> for the given element.
		@param element The element this view is responsible for.
		*/
		public XMLTableRowView(final Element element)
		{
		  super(element);	//construct the parent class
//G***fix			XMLRowView.this.setPropertiesFromAttributes();
	    fillColumns = new BitSet();
		}




    public void preferenceChanged(View child, boolean width, boolean height)
		{
//G***del Debug.trace("XMLTableView.XMLTableRowView.preferenceChanged width: "+width+" height: "+height);
		  super.preferenceChanged(child, width, height);//G***testing
    }




    public float getPreferredSpan(int axis) { //G***del; testing
//G***del Debug.trace("XMLTableRowView.getPreferredSpan axis: "+axis+" ="+super.getPreferredSpan(axis)); //G***del
		final float span=super.getPreferredSpan(axis);	//G***del
		return super.getPreferredSpan(axis);	//return the preferred span

    }

    public float getMinimumSpan(int axis) { //G***del; testing
//G***del Debug.trace("XMLTableRowView.getMinimum axis: "+axis+" ="+super.getMinimumSpan(axis)); //G***del
		final float span=super.getMinimumSpan(axis);	//G***del
		return super.getMinimumSpan(axis);	//return the preferred span
    }

	public float getMaximumSpan(int axis) //G***del; testing
	{
//G***del Debug.trace("XMLTableRowView.getMaximumSpan axis: "+axis+" ="+super.getMaximumSpan(axis)); //G***del
		final float span=super.getMaximumSpan(axis);	//G***del
		return super.getMaximumSpan(axis);	//return the preferred span
//G***fix return 1000;  //G***fix G***why did we have this earlier?
//G***fix		return super.getMaximumSpan(axis);	//return the preferred span
//G***fix		return getPreferredSpan(axis);	//return the preferred span
	}




		/**Sets the parent of the view.
			This implementation also indicates that the preferences have changed and
			asks each child to reset the parent.
		@param parent The parent of the view, <code>null</code> if none.
		*/
		public void setParent(View parent)	//(newswing)
		{
			super.setParent(parent);	//do the default setting of the parent
		}

		/**Creates a shallow copy. This is used by the
			<code>createFragment()</code> and <code>breakView()</code> methods.
		@return The copy of this view.
		*/
/*G***del
		protected final Object clone() throws CloneNotSupportedException
		{
			return new XMLTableRowView(getElement());	//create a view based on the same element
		}
*/

		/**Each fragment is a subset of the content in the breaking view.
		@return The starting offset of this view, which is the starting offset of the
			view with the lowest starting offset
		@see View#getRange
		*/
		public int getStartOffset()
		{
			int startOffset=Integer.MAX_VALUE;	//we'll start out with a high number, and we'll end up with the lowest starting offset of all the views
			final int numViews=getViewCount();	//find out how many view are on this page
			if(numViews>0)	//if we have child views
			{
				for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
				{
					final View view=getView(viewIndex);	//get a reference to this view
					startOffset=Math.min(startOffset, view.getStartOffset());	//if this view has a lower starting offset, use its starting offset
				}
				return startOffset;	//return the starting offset we found
			}
			else	//if we don't have any child views
				return super.getStartOffset();	//return the default starting offset
		}

		/**Each fragment is a subset of the content in the breaking view.
		@return The ending offset of this view, which is the ending offset of the
			view with the largest ending offset
		@see View#getRange
		*/
		public int getEndOffset()
		{
			int endOffset=0;	//start out with a low ending offset, and we'll wind up with the largest ending offset
			final int numViews=getViewCount();	//find out how many view are on this page
			if(numViews>0)	//if we have child views
			{
				for(int viewIndex=0; viewIndex<numViews; ++viewIndex)	//look at each view on this page
				{
					final View view=getView(viewIndex);	//get a reference to this view
					endOffset=Math.max(endOffset, view.getEndOffset());	//if this view has a larger ending offset, use that instead
				}
				return endOffset;	//return the largest ending offset we found
			}
			else	//if we don't have any child views
				return super.getEndOffset();	//return the default ending offset
		}

		/**Determines how attractive a break opportunity in this view is.
			This is implemented to forward to the superclass for the A axis. Along the
			Y axis, <code>GoodBreakWeight</code> will be returned.
		@param axis The breaking axis, either View.X_AXIS or View.Y_AXIS.
		@param pos The potential location of the start of the broken view (>=0).
			This may be useful for calculating tab positions.
		@param len Specifies the relative length from <em>pos</em> where a potential
			break is desired (>=0).
		@return The weight, which should be a value between View.ForcedBreakWeight
			and View.BadBreakWeight.
		@see XMLBlockView
		@see GoodBreakWeight
		*/
		public int getBreakWeight(int axis, float pos, float len)
		{
//G***fix return BadBreakWeight;  //G***testing
			if(axis==Y_AXIS)	//if they want to break along the Y axis
			{
				//currently breaking only works for single-column tables
				return getColumnCount()==1 ? GoodBreakWeight : BadBreakWeight;
//G***fix				return GoodBreakWeight;	//show that this break spot will work
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
			if(axis==Y_AXIS)	//if they want to break along the Y axis
			{
			  final int childViewCount=getViewCount();  //get the number of child views we have
				if(len>=getPreferredSpan(axis))	//if the break is as large or larger than the view
					return this;	//just return ourselves; there's no need to try to break anything
				else if(childViewCount>0)	//if we have child views
				{
	//G***bring back if needed				final XMLBlockView fragmentView=(XMLBlockView)clone();	//create a clone of this view
					final boolean isFirstFragment=p0<=getView(0).getStartOffset();  //see if we'll include the first child in any form; if so, we're the first fragment
					final boolean isLastFragment=false;  //G***fix; can we ever create the last fragment here?
//G***fix				final boolean isLastFragment=p1>=getView(childViewCount-1).getEndOffset();  //see if we'll include the last child in any form; if so, we're the last fragment
					final XMLTableRowFragmentView fragmentView=new XMLTableRowFragmentView(getElement(), isFirstFragment, isLastFragment);	//create a fragment view
//G***del when works						final View fragmentView=(View)clone();	//create a clone of this view
					float totalSpan=0;	//we'll use this to accumulate the size of each view to be included
					int startOffset=p0;	//we'll continually update this as we create new child view fragments
					int childIndex;	//start looking at the first child to find one that can be included in our break
					for(childIndex=0; childIndex<childViewCount && getView(childIndex).getEndOffset()<=startOffset; ++childIndex);	//find the first child that ends after our first model location
					for(; childIndex<childViewCount && totalSpan<len; ++childIndex)	//look at each child view at and including the first child we found that will go inside this fragment, and keep adding children until we find enough views to fill up the space or we run out of views
					{
						View childView=getView(childIndex);	//get a reference to this child view; we may change this variable if we have to break one of the child views


						if(totalSpan+childView.getPreferredSpan(axis)>len)	//if this view is too big to fit into our space
						{
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
						}
						if(childView!=null)	//if we have something to add
						{
							fragmentView.append(childView);	//add this child view, which could have been chopped up into a fragment itself


							totalSpan+=childView.getPreferredSpan(axis);	//show that we've used up more space
						}
						else	//if we needed more room but couldn't break a view
							break;	//stop trying to fit things
					}
					fragmentView.setParent(getParent());  //make sure the fragment has the correct parent
//G***del; doesn't fix tableflow				  fragmentView.setSize(getPreferredSpan(X_AXIS), getPreferredSpan(Y_AXIS)); //G***testing
					return fragmentView;	//return the new view that's a fragment of ourselves
				}
			}
			return this;	//if they want to break along another axis or we weren't able to break, return our entire view
		}


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
			if(p0<=getStartOffset() && p1>=getEndOffset())	//if the range they want encompasses all of our view
				return this;	//return ourselves; there's no use to try to break ourselves up
			else	//if the range they want only includes part of our view
			{
				final int childViewCount=getViewCount();  //find out how many child views there are
					//see if we'll include the first child in any form; if so, we're the first fragment
				final boolean isFirstFragment=childViewCount>0 && p0<=getView(0).getStartOffset();
					//see if we'll include the last child in any form; if so, we're the first fragment
				final boolean isLastFragment=childViewCount>0 && p1>=getView(childViewCount-1).getEndOffset();
				final XMLTableRowFragmentView fragmentView=new XMLTableRowFragmentView(getElement(), isFirstFragment, isLastFragment);	//create a fragment of the view
//G***del				final View fragmentView=(View)clone();	//create a clone of this view
				for(int i=0; i<childViewCount; ++i)	//look at each child view
				{
					final View childView=getView(i);	//get a reference to this child view
					if(childView.getStartOffset()<p1 && childView.getEndOffset()>p0)	//if this view is within our range
					{
						final int startPos=Math.max(p0, childView.getStartOffset());	//find out where we want to start, staying within this child view
						final int endPos=Math.min(p1, childView.getEndOffset());	//find out where we want to end, staying within this child view
						fragmentView.append(childView.createFragment(startPos, endPos));	//add a portion (or all) of this child to our fragment


					}
				}
				fragmentView.setParent(getParent());  //make sure the fragment has the correct parent
//G***del; doesn't fix tableflow			  fragmentView.setSize(getPreferredSpan(X_AXIS), getPreferredSpan(Y_AXIS)); //G***testing
				return fragmentView;	//return the fragment view we constructed
			}
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
		final View[] createdViews=XMLBlockView.createBlockElementChildViews(getElement(), viewFactory);  //create the child views
		replace(0, 0, createdViews);  //load our created views as children
	}

//G***TableView.TableRow methods for debugging

	public void clearFilledColumns() {
	    fillColumns.and(EMPTY);
	}

	public void fillColumn(int col) {
	    fillColumns.set(col);
	}

	public boolean isFilled(int col) {
	    return fillColumns.get(col);
	}

	/** get location in the overall set of rows */
	public int getRow() {
	    return row;
	}

	/**
	 * set location in the overall set of rows, this is
	 * set by the TableView.updateGrid() method.
	 */
	void setRow(int row) {
	    this.row = row;
	}

	/**
	 * The number of columns present in this row.
	 */
	int getColumnCount() {
	    int nfill = 0;
	    int n = fillColumns.size();
	    for (int i = 0; i < n; i++) {
		if (fillColumns.get(i)) {
		    nfill ++;
		}
	    }
	    return getViewCount() + nfill;
	}

	/**
	 * Change the child views.  This is implemented to
	 * provide the superclass behavior and invalidate the
	 * grid so that rows and columns will be recalculated.
	 */
        public void replace(int offset, int length, View[] views) {
	    super.replace(offset, length, views);
	    invalidateGrid();
	}

	/**
	 * Perform layout for the major axis of the box (i.e. the
	 * axis that it represents).  The results of the layout should
	 * be placed in the given arrays which represent the allocations
	 * to the children along the major axis.
	 * <p>
	 * This is re-implemented to give each child the span of the column
	 * width for the table, and to give cells that span multiple columns
	 * the multi-column span.
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
	    int col = 0;
	    int ncells = getViewCount();
	    for (int cell = 0; cell < ncells; cell++, col++) {
		View cv = getView(cell);
		for (; isFilled(col); col++); // advance to a free column
		int colSpan = getColumnsOccupied(cv);
		spans[cell] = columnSpans[col];
		offsets[cell] = columnOffsets[col];
		if (colSpan > 1) {
		    int n = columnSpans.length;
		    for (int j = 1; j < colSpan; j++) {
			// Because the table may be only partially formed, some
			// of the columns may not yet exist.  Therefore we check
			// the bounds.
			if ((col+j) < n) {
			    spans[cell] += columnSpans[col+j];
			}
		    }
		    col += colSpan - 1;
		}
	    }
	}

	/**
	 * Perform layout for the minor axis of the box (i.e. the
	 * axis orthoginal to the axis that it represents).  The results
	 * of the layout should be placed in the given arrays which represent
	 * the allocations to the children along the minor axis.  This
	 * is called by the superclass whenever the layout needs to be
	 * updated along the minor axis.
	 * <p>
	 * This is implemented to delegate to the superclass, then adjust
	 * the span for any cell that spans multiple rows.
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
Debug.trace("G***search");
	    super.layoutMinorAxis(targetSpan, axis, offsets, spans);
	    int col = 0;
	    int ncells = getViewCount();
	    for (int cell = 0; cell < ncells; cell++, col++) {
		View cv = getView(cell);
		for (; isFilled(col); col++); // advance to a free column
		int colSpan = getColumnsOccupied(cv);
		int rowSpan = getRowsOccupied(cv);
		if (rowSpan > 1) {
		    for (int j = 1; j < rowSpan; j++) {
			// test bounds of each row because it may not exist
			// either because of error or because the table isn't
			// fully loaded yet.
			int row = getRow() + j;
			if (row < XMLTableView.this.testGetViewCount()) {
			    int span = XMLTableView.this.testGetSpan(Y_AXIS, getRow()+j);
			    spans[cell] += span;
			}
		    }
		}
		if (colSpan > 1) {
		    col += colSpan - 1;
		}
	    }
	}

	/**
	 * Determines the resizability of the view along the
	 * given axis.  A value of 0 or less is not resizable.
	 *
	 * @param axis may be either View.X_AXIS or View.Y_AXIS
	 * @return the resize weight
	 * @exception IllegalArgumentException for an invalid axis
	 */
        public int getResizeWeight(int axis) {
	    return 1;
	}

	/**
	 * Fetches the child view that represents the given position in
	 * the model.  This is implemented to walk through the children
	 * looking for a range that contains the given position.  In this
	 * view the children do not necessarily have a one to one mapping
	 * with the child elements.
	 *
	 * @param pos  the search position >= 0
	 * @param a  the allocation to the table on entry, and the
	 *   allocation of the view containing the position on exit
	 * @returns  the view representing the given position, or
	 *   null if there isn't one
	 */
        protected View getViewAtPosition(int pos, Rectangle a) {
	    int n = getViewCount();
	    for (int i = 0; i < n; i++) {
		View v = getView(i);
		int p0 = v.getStartOffset();
		int p1 = v.getEndOffset();
		if ((pos >= p0) && (pos < p1)) {
		    // it's in this view.
		    if (a != null) {
			childAllocation(i, a);
		    }
		    return v;
		}
	    }
	    if (pos == getEndOffset()) {
		View v = getView(n - 1);
		if (a != null) {
		    this.childAllocation(n - 1, a);
		}
		return v;
	    }
	    return null;
	}

	/** columns filled by multi-column or multi-row cells */
	BitSet fillColumns;
	/** the row within the overall grid */
	int row;



		/**The class that serves as a fragment if a table row is broken.
		@author Garret Wilson
		*/
		protected class XMLTableRowFragmentView extends XMLFragmentBlockView
		{

			/**Constructs a fragment view for the table row.
			@param element The element this view is responsible for.
			@param axis The tiling axis, either View.X_AXIS or View.Y_AXIS.
			@param firstFragment Whether this is the first fragement of the original view.
			@param lastFragment Whether this is the last fragement of the original view.
			*/
			public XMLTableRowFragmentView(Element element, final boolean firstFragment, final boolean lastFragment)
			{
				super(element, X_AXIS, firstFragment, lastFragment); //do the default construction
			}
		}

























	/**
	 * Fetches the attributes to use when rendering.  This is
	 * implemented to multiplex the attributes specified in the
	 * model with a StyleSheet.
	 */
/*G***del
				public AttributeSet getAttributes() {
			return attr;
	}

				protected StyleSheet getStyleSheet() {
			HTMLDocument doc = (HTMLDocument) getDocument();
			return doc.getStyleSheet();
	}

	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
			super.changedUpdate(e, a, createViewFactory(f));
			int pos = e.getOffset();
			if (pos <= getStartOffset() && (pos + e.getLength()) >=
		getEndOffset()) {
		RowView.this.setPropertiesFromAttributes();
			}
	}
*/
	/**
	 * Renders using the given rendering surface and area on that
	 * surface.  This is implemented to delegate to the css box
	 * painter to paint the border and background prior to the
	 * interior.
	 *
	 * @param g the rendering surface to use
	 * @param allocation the allocated region to render into
	 * @see View#paint
	 */
/*G***del
	public void paint(Graphics g, Shape allocation) {
			Rectangle a = (Rectangle) allocation;
			painter.paint(g, a.x, a.y, a.width, a.height, this);
			super.paint(g, a);
	}
*/
	/**
	 * Update any cached values that come from attributes.
	 */
/*G***del
	void setPropertiesFromAttributes() {
			StyleSheet sheet = getStyleSheet();
			attr = sheet.getViewAttributes(this);
			painter = sheet.getBoxPainter(attr);
	}

	private StyleSheet.BoxPainter painter;
				private AttributeSet attr;
*/
	}

		/**
		 * Default view of an html table cell.  This needs to be moved
		 * somewhere else.
		 */
//G***del    static class CellView extends BlockView {

	/**
	 * Constructs a TableCell for the given element.
	 *
	 * @param elem the element that this view is responsible for
	 */
/*G***del
				public CellView(Element elem) {
			super(elem, Y_AXIS);
	}
*/
	/**
	 * Perform layout for the major axis of the box (i.e. the
	 * axis that it represents).  The results of the layout should
	 * be placed in the given arrays which represent the allocations
	 * to the children along the major axis.  This is called by the
	 * superclass to recalculate the positions of the child views
	 * when the layout might have changed.
	 * <p>
	 * This is implemented to delegate to the superclass to
	 * tile the children.  If the target span is greater than
	 * was needed, the offsets are adjusted to align the children
	 * (i.e. position according to the html valign attribute).
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
/*G***del
				protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
			super.layoutMajorAxis(targetSpan, axis, offsets, spans);

	    // calculate usage
	    int used = 0;
	    int n = spans.length;
	    for (int i = 0; i < n; i++) {
		used += spans[i];
	    }

	    // calculate adjustments
	    int adjust = 0;
	    if (used < targetSpan) {
		// PENDING(prinz) change to use the css alignment.
		String valign = (String) getElement().getAttributes().getAttribute(
				HTML.Attribute.VALIGN);
		if (valign == null) {
		    AttributeSet rowAttr = getElement().getParentElement().getAttributes();
		    valign = (String) rowAttr.getAttribute(HTML.Attribute.VALIGN);
		}
		if ((valign == null) || valign.equals("middle")) {
				adjust = (targetSpan - used) / 2;
		} else if (valign.equals("bottom")) {
		    adjust = targetSpan - used;
		}
	    }

	    // make adjustments.
	    if (adjust != 0) {
		for (int i = 0; i < n; i++) {
		    offsets[i] += adjust;
		}
	    }
	}
*/
	/**
	 * Calculate the requirements needed along the major axis.
	 * This is called by the superclass whenever the requirements
	 * need to be updated (i.e. a preferenceChanged was messaged
	 * through this view).
	 * <p>
	 * This is implemented to delegate to the superclass, but
	 * indicate the maximum size is very large (i.e. the cell
	 * is willing to expend to occupy the full height of the row).
	 *
	 * @param axis the axis being layed out.
	 * @param r the requirements to fill in.  If null, a new one
	 *  should be allocated.
	 */
/*G***del
				protected SizeRequirements calculateMajorAxisRequirements(int axis,
									SizeRequirements r) {
			SizeRequirements req = super.calculateMajorAxisRequirements(axis, r);
			req.maximum = Integer.MAX_VALUE;
			return req;
	}
		}
*/


	/**This is a custom view factory meant to create views for elements inside
		a table. Its constructor take a reference to the <code>ViewFactory</code>
		already in use, and this is used to create views for any unknown elements,
		thereby keeping the default action for non-table elements. The only need
		for this class seems to be that outside classes can't directly create
		such classes as <code>XMLTableRow</code>, and since <code>XMLTableRow</code>
		must be internal to inherit from <code>TableView.TableRow</code>, each
		row must be created within <code>XMLTableView</code>.
	*/
	class XMLTableFactory extends DefaultViewFactory
	{

		/**A reference to the view factory already in use to be used for normal elements.*/
//G***del when works		private ViewFactory DefaultViewFactory;

			/**@return A reference to the view factory already in use to be used for normal elements.*/
//G***del 			protected ViewFactory getDefaultViewFactory() {return DefaultViewFactory;}

		/*Constructor specifying the view factory already in use.
		@param fallbackViewFactory The view factory already in use.
		*/
		XMLTableFactory(final ViewFactory fallbackViewFactory)
		{
			super(fallbackViewFactory); //do the default construction
//G***del			this.DefaultViewFactory=defaultViewFactory;	//set the default view factory
		}

		/**Creates a view for the table rows. If the element does not represent a
			table row, the request will be delegated to the fallback view factory.
		@param element The element this view will represent.
		@param indicateMultipleViews Whether <code>null</code> should be returned to
			indicate multiple views should represent the given element.
		@return A view to represent the given element, or <code>null</code>
			indicating the element should be represented by multiple views.
		*/
		public View create(final Element element, final boolean indicateMultipleViews)
		{
			final String elementName=XMLStyleUtilities.getXMLElementName(element.getAttributes()); //G***del
Debug.trace("Table factory constructing view for a table element with name: ", elementName);	//G***del
Debug.trace("Indicate multiple views: "+indicateMultipleViews);	//G***del
Debug.traceStack(); //G***del
			final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
//G***del Debug.trace("Attribute set: "+attributeSet);	//G***del
			if(attributeSet!=null)	//if this element has attributes
			{
				final CSSPrimitiveValue cssDisplayProperty=(CSSPrimitiveValue)XMLCSSStyleUtilities.getCSSPropertyCSSValue(attributeSet, XMLCSSConstants.CSS_PROP_DISPLAY, false);	//get the display property for this element, but don't resolve up the attribute set parent hierarchy G***can we be sure this will be a primitive value?
//G***del when works				final XMLCSSPrimitiveValue cssDisplayProperty=(XMLCSSPrimitiveValue)attributeSet.getAttribute(XMLCSSConstants.CSS_PROP_DISPLAY);	//get the display property G***can we be sure this will be a primitive value?
				if(cssDisplayProperty!=null)	//if this element has a CSS display property
				{
					final String cssDisplayString=cssDisplayProperty.getStringValue();	//get the display value
//G***del Debug.trace("Has CSS display attribute: "+cssDisplayString);	//G***del
					if(cssDisplayString.equals(XMLCSSConstants.CSS_DISPLAY_TABLE_ROW))	//if this should be table row
						return new XMLTableRowView(element);	//create a table row view
				}
			}
			final View view=super.create(element, indicateMultipleViews);  //let the parent class create the view, which will automatically call the fallback view factory
Debug.trace("Created view: ", view!=null ? view.getClass().getName() : "null"); //G***del
//G***bring back when works			return super.create(element, indicateMultipleViews);  //let the parent class create the view, which will automatically call the fallback view factory
			return view;  //G***testing
/*G***del
Debug.trace("XMLTableView default view factory: ", getDefaultViewFactory().getClass().getName()); //G***del
			final View view=getDefaultViewFactory().create(element);	//if we don't recognize the element, let the default view factory create a view normally
Debug.trace("Created view: ", view.getClass().getName()); //G***del
			return view;  //G***testing
*/
//G***del			return getDefaultViewFactory().create(element);	//if we don't recognize the element, let the default view factory create a view normally
		}


		/**Overrides <code>ViewFactory.create()</code> to create table rows.
		@param element The element for which a view will be created.
		*/
/*G***del when works
		public View create(Element element)
		{
//G***fix			final String elementName=XMLCSSStyleConstants.getXMLElementName(element.getAttributes());	//G***del
			final String elementName=XMLStyleConstants.getXMLElementName(element.getAttributes()); //G***del
Debug.trace("Table factory constructing view for a table element with name: ", elementName);	//G***del
			final AttributeSet attributeSet=element.getAttributes();	//get the attributes of this element
//G***del Debug.trace("Attribute set: "+attributeSet);	//G***del
			if(attributeSet!=null)	//if this element has attributes
			{
				final XMLCSSPrimitiveValue cssDisplayProperty=(XMLCSSPrimitiveValue)XMLCSSStyleConstants.getCSSPropertyCSSValue(attributeSet, XMLCSSConstants.CSS_PROP_DISPLAY, false);	//get the display property for this element, but don't resolve up the attribute set parent hierarchy G***can we be sure this will be a primitive value?
//G***del when works				final XMLCSSPrimitiveValue cssDisplayProperty=(XMLCSSPrimitiveValue)attributeSet.getAttribute(XMLCSSConstants.CSS_PROP_DISPLAY);	//get the display property G***can we be sure this will be a primitive value?
				if(cssDisplayProperty!=null)	//if this element has a CSS display property
				{
					final String cssDisplayString=cssDisplayProperty.getStringValue();	//get the display value
//G***del Debug.trace("Has CSS display attribute: "+cssDisplayString);	//G***del
					if(cssDisplayString.equals(XMLCSSConstants.CSS_DISPLAY_TABLE_ROW))	//if this should be table row
						return new XMLTableRowView(element);	//create a table row view
				}
			}
Debug.trace("XMLTableView default view factory: ", getDefaultViewFactory().getClass().getName()); //G***del
			final View view=getDefaultViewFactory().create(element);	//if we don't recognize the element, let the default view factory create a view normally
Debug.trace("Created view: ", view.getClass().getName()); //G***del
			return view;  //G***testing
//G***del			return getDefaultViewFactory().create(element);	//if we don't recognize the element, let the default view factory create a view normally
		}
*/
	}

}
