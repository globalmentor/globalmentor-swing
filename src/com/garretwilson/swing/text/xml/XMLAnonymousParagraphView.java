package com.garretwilson.swing.text.xml;

/**A paragraph view for wrapping around a subset of child elements because other
	block child elements exist.
@author Garret Wilson
*/
public class XMLAnonymousParagraphView  //G***del this class if we don't need
{
	/**The attributes of the anonymous block view.*/
//G***del if not needed	protected final AttributeSet attributeSet;

	/**Fetches the attributes to use when rendering.
		This view uses its own anonymous attributes instead of the attributes of
		the element it represents.
	@return The attributes of the anonymous block view.
	*/
/*G***del if not needed
	public AttributeSet getAttributes()
	{
		return attributeSet;  //return our attributes
	}
*/

	/**The child elements for which this anonymous view is responsible.*/
//G***del if not needed	protected final Element[] ownedElements;

	/**Constructs an anonymous paragraph view for the given element.
	@param element The element this view is responsible for, although in most
		cases an anonymous view is not responsible for the entire element.
	@param attributes The attributes for the anonymous view.
	@param childElements The children of <code>element</code> for which this
		anonymous view is responsible.
	*/
/*G***del if not needed
	public XMLAnonymousParagraphView(final Element element, final AttributeSet attributes, final Element[] childElements)
	{
		super(element); //construct the parent view
		attributeSet=attributes;  //set the attributes for this view
		ownedElements=childElements;  //save our children
	}
*/


	/**Loads all of the children to initialize the view.
		This is called by the <code>setParent</code> method.
		This is reimplemented to not load any children directly, as they are created
		in the process of formatting.
		If the layoutPool variable is <code>null</code>, an instance of
		<code>LogicalView</code> is created to represent the logical view that is
		used in the process of formatting.
		This version of <code>loadChildren</code> is implemented to return
		specifically a <code>XMLParagraphView.LogicalView</code>, so that that view
		can implement its own special version of child view construction.
	@param viewFactory The view factory to use for child view creation.
	*/
/*G***del if not needed
	protected void loadChildren(ViewFactory f)
	{
		if(layoutPool==null)  //if there is no layout pool
		{
//G***del Debug.trace("XMLParagraphView.loadChildren() creating new logical view");
		  layoutPool=new LogicalView(getElement()); //create our own brand of logical view that will correctly create paragraph child views
		}
		//we only need to load views if we previously had no views in the pool;
		//this is to compensate for FlowView, which unconditionally loads children
		//when there are no child views, even if there are views in the pool
		final boolean needsLoading=layoutPool.getViewCount()==0;
		layoutPool.setParent(this); //tell the layout pool that we're its parent, so that it can begin creating its child views
//G***del Debug.trace("layout pool has "+layoutPool.getViewCount()+" children.");
//G***del Debug.trace("layout child is of type: "+layoutPool.getView(0).getClass().getName());
		if(needsLoading)  //if we really need to load things
		{
//G***del Debug.trace("Updating the strategy and stuff");
		  final int poolViewCount=layoutPool.getViewCount();  //find out how many views are in the pool
				//see if there are only object views present, how many inline views there are, etc.
		  boolean onlyObjectsPresent=true; //assume there are only objects present in this paragraph
			int inlineViewCount=0;  //we'll keep a record of how many inline views there are
//G***fix			boolean firstInlineViewHasMultipleWords=false;  //assume the first inline view has multiple words
		  for(int i=0; i<poolViewCount; ++i)  //look at each view in the pool
			{
				final View pooledView=layoutPool.getView(i);  //get a reference to this pooled view
				if(!(pooledView instanceof XMLObjectView)) //if this isn't an object
					onlyObjectsPresent=false; //show that there are other things besides objects present
				else if(pooledView instanceof XMLInlineView)
				{
					++inlineViewCount;  //show that we've found another inline view
				}
			}
			final View parentView=getParent();  //get our parent view
			final String parentDisplay=XMLCSSStyleConstants.getDisplay(parentView.getAttributes()); //see what kind of parent we have
			final boolean isInTableCell=XMLCSSConstants.CSS_DISPLAY_TABLE_CELL.equals(parentDisplay);  //see if we're inside a table cell
	//G***del Debug.trace("only objects present: "+onlyObjectPresent);
//G***del when works			setFirstLineIndented(!onlyObjectPresent); //if there are only objects present in this paragraph, we won't indent
			setFirstLineIndented(!onlyObjectsPresent && (inlineViewCount>1 || !isInTableCell)); //if there are only objects present in this paragraph, or if there's only one inline view in a table cell, we won't indent
			//This synthetic insertUpdate call gives the strategy a chance to initialize.
			strategy.insertUpdate( this, null, null );
		}
	}
*/

}