package com.garretwilson.swing.text.xml;

import java.awt.*;
import javax.swing.text.*;

import com.garretwilson.swing.text.ViewHidable;
import com.garretwilson.util.Debug;

/**Provides a block view with one or more components embedded. The children will
	be loaded as with a normal block view, except that the added components will
	be visible in the view. The components are added using the constants from
	<code>BorderLayout</code> except for <code>BorderLayout.CENTER</code>.
	Implements <code>ViewHidable</code> so that it can be notified if the view
	is being hidden so that it can hide the object.
@see BorderLayout
@author Garret Wilson
//G***maybe rename to XMLBorderComponentBlockView

//G***later make a way to free the embedded component, just as we should do with the XMLAbstractComponentView and children
//G***this could perhaps be generalized even more to an XMLPrefixedComponentView or Something
*/
public class XMLComponentBlockView extends XMLBlockView implements ViewHidable
{

	/**Whether this is the first fragment of the original view.*/
	protected boolean isFirstFragment=true;

		/**@return <code>true</code> if this is the first fragment of the original view.*/
		public final boolean isFirstFragment() {return isFirstFragment;}

	/**Whether this is the last fragment of the original view.*/
	protected boolean isLastFragment=true;

		/**@return <code>true</code> if this is the last fragment of the original view.*/
		public boolean isLastFragment() {return isLastFragment;}

	/**The component to embed in the view.*/
//G***del	private final Component viewComponent;

	/**The component that appears after the last line.*/
	protected Component afterLastLineComponent=null;

	/**The component that appears after the line ends.*/
	protected Component afterLineEndsComponent=null;

	/**The component that appears in the east.*/
	protected Component eastComponent=null;

	/**The component that appears in the north.*/
	protected Component northComponent=null;

	/**The component that appears in the south.*/
	protected Component southComponent=null;

	/**The component that appears in the west.*/
	protected Component westComponent=null;

	/**The component that appears before the first line.*/
	protected Component beforeFirstLineComponent=null;

	/**The component that appears in the before the line begins.*/
	protected Component beforeLineBeginsComponent=null;

		/**@return The component for the left side, taking into account the
			component orientation.
		 */
		protected Component getLeftComponent()
		{
		  final Container container=getContainer();  //get the container we're place in G***this used to be parent.getContainer() inside setParent(); this probably won't make a difference
//G***del Debug.trace("Container: ", container);
		  if(container!=null) //if we have a valid container
			{
		    if(container.getComponentOrientation().isLeftToRight()) //if the component is laid our left-to-right
				{
					if(beforeLineBeginsComponent!=null) //if we have a component for before the line begins
						return beforeLineBeginsComponent;  //return the component
				}
			}
			return westComponent; //if we couldn't get the orientation, or we didn't have a relative component, return the absolute component, if present
		}

		/**@return The component for the right side, taking into account the
			component orientation.
		 */
		protected Component getRightComponent()
		{
		  final Container container=getContainer();  //get the container we're place in G***this used to be parent.getContainer() inside setParent(); this probably won't make a difference
//G***del Debug.trace("Container: ", container);
		  if(container!=null) //if we have a valid container
			{
		    if(container.getComponentOrientation().isLeftToRight()) //if the component is laid our left-to-right
				{
					if(afterLineEndsComponent!=null) //if we have a component for after the line ends
						return afterLineEndsComponent;  //return the component
				}
			}
			return eastComponent; //if we couldn't get the orientation, or we didn't have a relative component, return the absolute component, if present
		}

		/**@return The component for the top. Component vertical orientation is not
		  currently supported.*/
		protected Component getTopComponent()
		{
			if(beforeFirstLineComponent!=null) //if we have a component for before the first line
				return beforeFirstLineComponent;  //return the component
			return northComponent; //if we couldn't get the orientation, or we didn't have a relative component, return the absolute component, if present
		}

		/**@return The component for the bottom. Component vertical orientation is not
		  currently supported.*/
		protected Component getBottomComponent()
		{
			if(afterLastLineComponent!=null) //if we have a component for after the last line
				return afterLastLineComponent;  //return the component
			return southComponent; //if we couldn't get the orientation, or we didn't have a relative component, return the absolute component, if present
		}


	/**Whether the object is showing; this defaults to <code>false</code>.*/
	private boolean showing=false;

		/**Returns whether the object is showing. This is not necessarily the same
			as visible, because the associated object could be set as visible, yet
			not be showing because it is displayed in a paged view, for example.
		@return Whether the object is showing.
		*/
		public boolean isShowing() {return showing;}

		/**Sets whether or not an view is showing. This method is called to show
			the view when needed if <code>isShowing()</code> returns <code>false</code>,
			and is called when the view is being hidden by a parent that hides views,
			such as a paged view.
			If this view is overridden, this version should be called to correctly
			update the variable for <code>isShowing()</code> to function correctly.
		@param newShowing <code>true</code> if the view is beginning to be shown,
			<code>false</code> if the view is beginning to be hidden.
		@see #isShowing
		*/
		public void setShowing(final boolean newShowing)
		{
Debug.trace("making component visible: "+newShowing);  //G***del

//G***add a way to put the component back into the container when it's time to show it again

		  if(getLeftComponent()!=null && (getAxis()!=X_AXIS || isFirstFragment()))  //if we have this component and we're showing it
				getLeftComponent().setVisible(newShowing);  //set the component's visibility
		  if(getRightComponent()!=null && (getAxis()!=X_AXIS || isLastFragment()))  //if we have this component and we're showing it
				getRightComponent().setVisible(newShowing);  //set the component's visibility
		  if(getTopComponent()!=null && (getAxis()!=Y_AXIS || isFirstFragment()))  //if we have this component and we're showing it
				getTopComponent().setVisible(newShowing);  //set the component's visibility
		  if(getBottomComponent()!=null && (getAxis()!=Y_AXIS || isLastFragment()))  //if we have this component and we're showing it
				getBottomComponent().setVisible(newShowing);  //set the component's visibility


			//G***testing a way to remove the component from the container
		  if(showing && !newShowing)  //if we're already showing and we're now being hidden
			{
				final Container container=getContainer();  //get the container we're place in G***this used to be parent.getContainer() inside setParent(); this probably won't make a difference
Debug.trace("Container: ", container);  //G***del
				if(container!=null) //if we have a valid container
				{
					if(getLeftComponent()!=null && (getAxis()!=X_AXIS || isFirstFragment()))  //if we have this component and we're showing it
						container.remove(getLeftComponent()); //remove the component from the container
					if(getRightComponent()!=null && (getAxis()!=X_AXIS || isLastFragment()))  //if we have this component and we're showing it
						container.remove(getRightComponent()); //remove the component from the container
					if(getTopComponent()!=null && (getAxis()!=Y_AXIS || isFirstFragment()))  //if we have this component and we're showing it
						container.remove(getTopComponent()); //remove the component from the container
					if(getBottomComponent()!=null && (getAxis()!=Y_AXIS || isLastFragment()))  //if we have this component and we're showing it
						container.remove(getBottomComponent()); //remove the component from the container
				}
			}


/*G***del when works
		  if(getBottomComponent()!=null && isShowsBottom())  //if we have this component
				getBottomComponent().setVisible(newShowing);  //set the component's visibility
		  if(northComponent!=null)  //if we have this component
				northComponent.setVisible(newShowing);  //set the component's visibility
		  if(southComponent!=null)  //if we have this component
				southComponent.setVisible(newShowing);  //set the component's visibility
		  if(westComponent!=null)  //if we have this component
				westComponent.setVisible(newShowing);  //set the component's visibility
		  if(beforeFirstLineComponent!=null)  //if we have this component
				beforeFirstLineComponent.setVisible(newShowing);  //set the component's visibility
		  if(beforeLineBeginsComponent!=null)  //if we have this component
				beforeLineBeginsComponent.setVisible(newShowing);  //set the component's visibility
//G***del			viewComponent.setVisible(newShowing); //show or hide the component appropriately
*/
			showing=newShowing; //update the showing state variable
		}

	/**Constructs a block view with the given component.
	@param element The element this view is responsible for.
	@param axis The tiling axis, either <code>View.X_AXIS</code> or
		<code>View.Y_AXIS</code>.
	@param component The component to embed in the block view. G***del param comment
	*/
	public XMLComponentBlockView(final Element element, final int axis/*G***del when works, final Component component*/) //G***probably add the expansion variables here
	{
		super(element, axis, true, true); //construct the parent, allowing expansion in both direction
/*G***del when works
		viewComponent=component;  //save the component to embed in the view
		viewComponent.setSize(viewComponent.getPreferredSize());  //G***del; testing
*/
	}

	/**Adds a component to be displayed, constrained to one of the borders. The
		background color of the component will be updated to match the background
		color of this element.
	@component The component to be displayed.
	@constraints One of the <code>BorderLayout</code> constraints, except for
		<code>BorderLayout.CENTER</code>.
	@see BorderLayout#AFTER_LAST_LINE
	@see BorderLayout#AFTER_LINE_ENDS
	@see BorderLayout#EAST
	@see BorderLayout#NORTH
	@see BorderLayout#SOUTH
	@see BorderLayout#WEST
	@see BorderLayout#BEFORE_FIRST_LINE
	@see BorderLayout#BEFORE_LINE_BEGINS
	@see Component#setBackground
	@see XMLBlockView#getBackgroundColor
	*/
	public void add(final Component component, final Object constraints)
	{
		if(BorderLayout.AFTER_LAST_LINE.equals(constraints))  //if they want the component after the last line
			afterLastLineComponent=component;  //store the component in the correct variable
		else if(BorderLayout.AFTER_LINE_ENDS.equals(constraints))  //if they want the component after the line ends
			afterLineEndsComponent=component;  //store the component in the correct variable
		else if(BorderLayout.NORTH.equals(constraints))  //if they want the component in the north
			northComponent=component;  //store the component in the correct variable
		else if(BorderLayout.SOUTH.equals(constraints))  //if they want the component in the south
			southComponent=component;  //store the component in the correct variable
		else if(BorderLayout.WEST.equals(constraints))  //if they want the component in the west
			westComponent=component;  //store the component in the correct variable
		else if(BorderLayout.BEFORE_FIRST_LINE.equals(constraints))  //if they want the component before the first line
			beforeFirstLineComponent=component;  //store the component in the correct variable
		else if(BorderLayout.BEFORE_LINE_BEGINS.equals(constraints))  //if they want the component before the line begins
			beforeLineBeginsComponent=component;  //store the component in the correct variable
		else  //if we don't recognize the constraint
		  throw new IllegalArgumentException("Cannot add component: unknown constraint: "+constraints);
		component.setVisible(false); //don't show the component initially; it will be shown at the appropriate time using show() when called from paint()
		component.setSize(component.getPreferredSize());  //set the component's size to whatever it prefers
//G***fix		component.setBackground(getBackgroundColor());  //set the background color of the component G***decide if we *really* want to do this here or not -- it may no be best for buttons and such
	}

	/**Sets the parent of the view.
		After setting the parent using the superclass behavior, this version creates
		the component if it has not yet been created.
	@param parent The parent of the view, <code>null</code> if none.
	@see #createComponent
	*/
	public void setParent(View parent)
	{
		super.setParent(parent);  //let the super class set the parent; we should do this first so setComponent() will be able to find a container to which to add the component
		if(parent!=null/*G***fix && getComponent()==null*/) //if we've been given a parent
		{
//G***del Debug.trace("XMLComponentView.setParent() creating component; component before: "+component);
//G***fix		  final Component component=createComponent();  //create the component
//G***del if not needed		  viewComponent.setVisible(false); //don't show the component initially; it will be shown at the appropriate time using show() when called from paint()
//G***fix		  setComponent(component);  //set the component, which will add the component to our container
		  final Container container=getContainer();  //get the container we're place in G***this used to be parent.getContainer() inside setParent(); this probably won't make a difference
//G***del Debug.trace("Container: ", container);
		  if(container!=null) //if we have a valid container
			{
//G***del Debug.trace("adding component to the container"); //G***del
				if(afterLastLineComponent!=null)  //if we have this component
					container.add(afterLastLineComponent);  //add the component to the container
				if(afterLineEndsComponent!=null)  //if we have this component
					container.add(afterLineEndsComponent);  //add the component to the container
				if(eastComponent!=null)  //if we have this component
					container.add(eastComponent);  //add the component to the container
				if(northComponent!=null)  //if we have this component
					container.add(northComponent);  //add the component to the container
				if(southComponent!=null)  //if we have this component
					container.add(southComponent);  //add the component to the container
				if(westComponent!=null)  //if we have this component
					container.add(westComponent);  //add the component to the container
				if(beforeFirstLineComponent!=null)  //if we have this component
					container.add(beforeFirstLineComponent);  //add the component to the container
				if(beforeLineBeginsComponent!=null)  //if we have this component
					container.add(beforeLineBeginsComponent);  //add the component to the container
//G***del				container.add(viewComponent); //add the component to the container
			}
		}
	}

	/**Loads all of the children to initialize the view. This version loads the
		children normally then adds a component view to the beginning of the views.
	@param viewFactory The view factory.
	@see #setParent
	@see XMLComponentView
	*/
/*G***fix
	protected void loadChildren(final ViewFactory viewFactory)
	{
		super.loadChildren(viewFactory);  //load the children normally
*/
/*G***fix
		final View[] addedViews=new View[1];  //create an array of views for the insertion G***why isn't there an insert() method that simply does this functionality?
		addedViews[0]=new XMLComponentView(getElement(), viewComponent); //create a component view to be inserted
		this.append(addedViews[0]); //G***testing
//G***bring back		replace(0, 0, addedViews);  //insert the component view at the beginning of the view
*/
//G***fix	}

	/**Paints the object. This version does not actually do any painting. Instead,
		the method shows the object if needed by calling <code>setShowing()</code>.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see View#paint
	@see #setShowing
	*/
	public void paint(Graphics graphics, Shape allocation)
	{
		//get the bounding rectangle of the painting area and update our bounds variable
//G***fix		getBounds().setBounds((allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds());
//G***del		final Component component=getComponent(); //get our component
//G***del		if(viewCOmponent!=null) //if we have a valid component
Debug.trace();  //G***del
		if(!isShowing())  //if the object isn't currently showing
			setShowing(true); //show the object
//G***del Debug.trace("Ready to call setShowing()");
//G***del if not needed			viewComponent.setVisible(showing); //show or hide the component appropriately
//G***del		super.setShowing(showing);  //update showing in the parent class
//G***fix viewComponent.setVisible(true); //G***fix
		super.paint(graphics, allocation);  //do the default painting
		final Rectangle rectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();  //get the bounding rectangle of the painting area
//G***fix		viewComponent.setBounds(10, 10, 50, 50); //G***testing
//G***del Debug.trace("Component preferred size: ", viewComponent.getPreferredSize());
		final Component leftComponent=getLeftComponent(); //get the component for the left
		if(leftComponent!=null && (getAxis()!=X_AXIS || isFirstFragment())) //if we have a component for the left
		{
		  leftComponent.setBounds(rectangle.x, rectangle.y, leftComponent.getWidth(), leftComponent.getHeight()); //make sure the component has the correct bounds
			leftComponent.validate(); //tell the componenet to validate itself, laying out its child components if needed
		}
		final Component rightComponent=getRightComponent(); //get the component for the right
		if(rightComponent!=null && (getAxis()!=X_AXIS || isLastFragment())) //if we have a component for the right
		{
		  rightComponent.setBounds(rectangle.x+rectangle.width-rightComponent.getWidth(), rectangle.y, rightComponent.getWidth(), rightComponent.getHeight()); //make sure the component has the correct bounds
			rightComponent.validate(); //tell the componenet to validate itself, laying out its child components if needed
		}
		final Component topComponent=getTopComponent(); //get the component for the top
		if(topComponent!=null && (getAxis()!=Y_AXIS || isFirstFragment())) //if we have a component for the top
		{
		  topComponent.setBounds(rectangle.x, rectangle.y, topComponent.getWidth(), topComponent.getHeight()); //make sure the component has the correct bounds
			topComponent.validate(); //tell the componenet to validate itself, laying out its child components if needed
		}
		final Component bottomComponent=getBottomComponent(); //get the component for the bottom
		if(bottomComponent!=null && (getAxis()!=Y_AXIS || isLastFragment())) //if we have a component for the bottom
		{
			final int componentWidth=bottomComponent.getWidth();  //get the component width
			final int componentHeight=bottomComponent.getHeight();  //get the component height
		  bottomComponent.setBounds(rectangle.x+(rectangle.width-componentWidth)/2, rectangle.y+rectangle.height-componentHeight, componentWidth, bottomComponent.getHeight()); //make sure the component has the correct bounds G***maybe use the alignment to align the component
			bottomComponent.validate(); //tell the componenet to validate itself, laying out its child components if needed
		}
//G***bring back and fix		viewComponent.setBounds(rectangle.x, rectangle.y, viewComponent.getWidth(), viewComponent.getHeight()); //make sure the component has the correct bounds
/*G***del when works
		viewComponent.setSize(viewComponent.getPreferredSize());  //G***del; testing
Debug.trace("Component bounds: ", viewComponent.getBounds());
Debug.trace("Component size: ", viewComponent.getSize());
*/
//G***del		viewComponent.validate(); //tell the componenet to validate itself, laying out its child components if needed
//G***del Debug.trace("Is component showing: "+isShowing());  //G***del
//G***del Debug.trace();  //G***del
	}


	/**Sets the cached properties from the attributes. This version updates the
		margins based upon the embedded components.
	*/
	protected void setPropertiesFromAttributes()
	{
		super.setPropertiesFromAttributes();  //set the properties the default way
//G***del Debug.trace("Setting properties from attributes, with first bottom inset: ", getBottomInset()); //G***del

/*G***fix
Debug.trace("setting properties, background color: ", getBackgroundColor());  //G***del
	//G***testing component background color
		  if(getLeftComponent()!=null && (getAxis()!=X_AXIS || isFirstFragment()))  //if we have this component and we're showing it
				getLeftComponent().setBackground(getBackgroundColor());  //set the background color of the component G***decide if we *really* want to do this here or not -- it may no be best for buttons and such
		  if(getRightComponent()!=null && (getAxis()!=X_AXIS || isLastFragment()))  //if we have this component and we're showing it
				getRightComponent().setBackground(getBackgroundColor());  //set the background color of the component G***decide if we *really* want to do this here or not -- it may no be best for buttons and such
		  if(getTopComponent()!=null && (getAxis()!=Y_AXIS || isFirstFragment()))  //if we have this component and we're showing it
				getTopComponent().setBackground(getBackgroundColor());  //set the background color of the component G***decide if we *really* want to do this here or not -- it may no be best for buttons and such
		  if(getBottomComponent()!=null && (getAxis()!=Y_AXIS || isLastFragment()))  //if we have this component and we're showing it
				getBottomComponent().setBackground(getBackgroundColor());  //set the background color of the component G***decide if we *really* want to do this here or not -- it may no be best for buttons and such
*/


		short leftInset=getLeftInset();
		final Component leftComponent=getLeftComponent(); //get the left component, if available
		if(leftComponent!=null && (getAxis()!=X_AXIS || isFirstFragment())) //if we have a left component
			leftInset+=leftComponent.getPreferredSize().width; //add the width of the component
		short rightInset=getRightInset();
		final Component rightComponent=getRightComponent(); //get the right component, if available
		if(rightComponent!=null && (getAxis()!=X_AXIS || isLastFragment())) //if we have a right component
			rightInset+=rightComponent.getPreferredSize().width; //add subtract the width of the component
		short topInset=getTopInset();
		final Component topComponent=getTopComponent(); //get the top component, if available
		if(topComponent!=null && (getAxis()!=Y_AXIS || isFirstFragment())) //if we have a top component
			topInset+=topComponent.getPreferredSize().height; //add subtract the height of the component
		short bottomInset=getBottomInset();
		final Component bottomComponent=getBottomComponent(); //get the bottom component, if available
		if(bottomComponent!=null && (getAxis()!=Y_AXIS || isLastFragment())) //if we have a bottom component
			bottomInset+=bottomComponent.getPreferredSize().height; //add subtract the height of the component
/*G***del when works
		  //G***we may want to set the insets in setPropertiesFromAttributes(); for
			//percentages, getPreferredeSpan(), etc. will have to look at the preferred
			//span and make calculations based upon the percentages
			//G***probably have some other exernal helper class that sets the margins based upon the attributes
			final short marginTop=(short)Math.round(XMLCSSStyleConstants.getMarginTop(attributeSet)); //get the top margin from the attributes
			final short marginLeft=(short)Math.round(XMLCSSStyleConstants.getMarginLeft(attributeSet, font)); //get the left margin from the attributes
			final short marginBottom=(short)Math.round(XMLCSSStyleConstants.getMarginBottom(attributeSet)); //get the bottom margin from the attributes
			final short marginRight=(short)Math.round(XMLCSSStyleConstants.getMarginRight(attributeSet, font)); //get the right margin from the attributes
*/
		setInsets(topInset, leftInset, bottomInset, rightInset);	//G***fix; testing
//G***del Debug.trace("Setting properties from attributes, with second bottom inset: ", getBottomInset()); //G***del
	}


	/**Determines the preferred span for this view along an axis. This returns the
		currently calculated spans.
	@param axis The axis, either <code>X_AXIS</code> or <code>Y_AXIS</code>.
	@returns  The span the view would like to be rendered into.
		Typically the view is told to render into the span that is returned,
		although there is no guarantee. The parent may choose to resize or break
		the view, although object views cannot normally be broken.
	@exception IllegalArgumentException Thrown if the axis is not recognized.
	@see #getCurrentHeight
	@see #getCurrentWidth
	*/  //G***fix comment
/*G***del when works
	public float getPreferredSpan(int axis)
	{
		switch(axis)  //see which axis is being requested
		{
		  case View.X_AXIS: //if the x-axis is requested
				{
					float span=super.getPreferredSpan(axis);  //get the normal preferred span
					final Component leftComponent=getLeftComponent(); //get the left component, if available
					if(leftComponent!=null) //if we have a left component
						span-=leftComponent.getPreferredSize().width; //subtract the width of the component
					final Component rightComponent=getRightComponent(); //get the right component, if available
					if(rightComponent!=null) //if we have a right component
						span-=rightComponent.getPreferredSize().width; //subtract the width of the component
				  return span;  //return the span minus the size of any components
//G***fix			  return super.getPreferredSpan(axis)+viewComponent.getPreferredSize().width; //G***testing
//G***del when works			    return super.getPreferredSpan(axis); //G***testing
				}
		  case View.Y_AXIS: //if the y-axis is requested
				{
					float span=super.getPreferredSpan(axis);  //get the normal preferred span
					final Component topComponent=getTopComponent(); //get the top component, if available
					if(topComponent!=null) //if we have a top component
						span-=topComponent.getPreferredSize().height; //subtract the height of the component
					final Component bottomComponent=getBottomComponent(); //get the bottom component, if available
					if(bottomComponent!=null) //if we have a bottom component
						span-=bottomComponent.getPreferredSize().height; //subtract the height of the component
				  return span;  //return the span minus the size of any components
//G***del when works		    return super.getPreferredSpan(axis)+viewComponent.getPreferredSize().height;  //G***fix for max of both heights
				}
			default:  //if we don't recognize the axis
				throw new IllegalArgumentException("Invalid axis: "+axis);  //report that we don't recognize the axis
		}
	}
*/

	/**@return The left inset (>=0), componsating for any components.*/
/*G***del when works
	protected short getLeftInset()
	{
		final Component leftComponent=getLeftComponent(); //get the left component, if available
		if(leftComponent!=null) //if we have a left component
			return (short)(super.getLeftInset()+leftComponent.getPreferredSize().width); //add the width of the component
		return super.getLeftInset();  //return the default inset if we don't have a component
	}
*/

	/**@return The right inset (>=0), componsating for any components.*/
/*G***del when works
	protected short getRightInset()
	{
		final Component rightComponent=getRightComponent(); //get the right component, if available
		if(rightComponent!=null) //if we have a right component
			return (short)(super.getRightInset()+rightComponent.getPreferredSize().width); //add subtract the width of the component
		return super.getRightInset();  //return the default inset if we don't have a component
	}
*/

	/**@return The top inset (>=0), componsating for any components.*/
/*G***del when works
	protected short getTopInset()
	{
		final Component topComponent=getTopComponent(); //get the top component, if available
		if(topComponent!=null) //if we have a top component
			return (short)(super.getTopInset()+topComponent.getPreferredSize().height); //add subtract the height of the component
		return super.getTopInset();  //return the default inset if we don't have a component
	}
*/

	/**@return The bottom inset (>=0), componsating for any components.*/
/*G***del when works
	protected short getBottomInset()
	{
		final Component bottomComponent=getBottomComponent(); //get the bottom component, if available
		if(bottomComponent!=null) //if we have a bottom component
			return (short)(super.getBottomInset()+bottomComponent.getPreferredSize().height); //add subtract the height of the component
		return super.getBottomInset();  //return the default inset if we don't have a component
	}
*/

	/**Creates a fragment view into which pieces of this view will be placed.
	@param isFirstFragment Whether this fragment holds the first part of the
		original view.
	@param isLastFragment Whether this fragment holds the last part of the
		original view.
	*/
	public View createFragmentView(final boolean isFirstFragment, final boolean isLastFragment)
	{
		//G***maybe instead of putting fragment functionality here, only copy components if they are to be used

			//create a component fragment view
	  final XMLFragmentComponentBlockView fragmentView=new XMLFragmentComponentBlockView(getElement(), getAxis(),
		afterLastLineComponent, //update the components in the fragment view
		afterLineEndsComponent,
		eastComponent,
		northComponent,
		southComponent,
		westComponent,
		beforeFirstLineComponent,
		beforeLineBeginsComponent,
		XMLComponentBlockView.this, isFirstFragment, isLastFragment);

//G***del; make sure works with new BreakStrategy framework		fragmentView.setParent(getParent());				  //G***testing; comment

/*G***del when works
		fragmentView.afterLastLineComponent=afterLastLineComponent; //update the components in the fragment view
		fragmentView.afterLineEndsComponent=afterLineEndsComponent;
		fragmentView.eastComponent=eastComponent;
		fragmentView.northComponent=northComponent;
		fragmentView.southComponent=southComponent;
		fragmentView.westComponent=westComponent;
		fragmentView.beforeFirstLineComponent=beforeFirstLineComponent;
		fragmentView.beforeLineBeginsComponent=beforeLineBeginsComponent;
		fragmentView.cacheSynchronized=false; //G***fix
		fragmentView.synchronize(); //G***fix
*/
		return fragmentView;  //return the fragment view
	}


}