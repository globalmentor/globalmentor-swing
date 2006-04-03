package com.garretwilson.swing.text;

import java.awt.*;
import java.util.*;
import static java.lang.Math.*;
import static java.util.Collections.*;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
import static com.garretwilson.lang.ObjectUtilities.*;

import com.garretwilson.awt.Inset;
import com.garretwilson.util.Debug;

/**Manages AWT/Swing components for a particular view.
	Used with a view that contains AWT/Swing components.
	<p>Views that use the view manager should:
	  <ul>
			<li>call <code>ViewComponentManager.setLocation()</code> from within
				<code>View.paint()</code> after calling the view's default version.</li>
			<li>call <code>ViewComponentManager.setSize()</code> from within
				<code>View.setSize()</code> after calling the view's default version.</li>
			<li>call <code>ViewComponentManager.setShowing()</code> as needed.</li>
		</ul>
	</p>
	<p>If region-based positioning is used, the associated view should implement <code>Inset</code>.</p>
@author Garret Wilson
@see Inset
*/
public class ViewComponentManager //G***finish the class comments with examples of usage
{
	
	/**A component position.
	@author Garret Wilson
	*/
	public interface Position extends Cloneable
	{
		/**@return A clone of the position.
		@exception CloneNotSupportedException if the clone operation fails.
		*/
    public Object clone() throws CloneNotSupportedException;		
	}

	/**A region-based location along an axis.
	@author Garret Wilson
	*/
	public static class AxisLocation
	{
		/**The region along an axis relative to the origin.*/
		public enum Region
		{
			/**The inset before the center of the content; "left" for left-to-right orientation.*/
			BEFORE,
			/**The center of the content.*/
			MIDDLE,
			/**The inset after the center of the content; "right" for left-to-right orientation.*/
			AFTER
		}
		
		/**The region along the axis relative to the origin.*/
		private final Region region;
			
			/**@return The region along the axis relative to the origin.*/
			public Region getRegion() {return region;}
		
		/**The alignment along the axis (0.0 to 1.0, inclusive) relative to the origin.*/
		private final float alignment;
			
			/**@return The alignment along the axis (0.0 to 1.0, inclusive) relative to the origin.*/
			public float getAlignment() {return alignment;}
	
		/**Constructs a location along an axis aligned in a region.
		@param region The region along the axis relative to the origin.
		@param alignment The alignment along the axis (0.0 to 1.0, inclusive) relative to the origin.
		@exception IllegalArgumentException if the alignment is less than 0.0 or greater than 1.0.
		*/
		public AxisLocation(final Region region, final float alignment)
		{
			this.region=region;	//save the region
			this.alignment=alignment;	//save the alignment
			if(alignment<0 || alignment>1)	//if the alignment is not valid
			{
				throw new IllegalArgumentException("Alignment "+alignment+" must be constrained from 0.0 to 1.0, inclusive.");
			}
		}
		
		/**Determines the coordinate along the axis relative to the given origin and span based upon the region and alignment.
		@param spanBefore The span in the near inset. 
		@param spanMiddle The span in the middle inset.
		@param spanAfter The span in the far inset. 
		@param extent The extent of the object.
		@return The absolute coordinate along the axis.
		*/
		public float getCoordinate(final float spanBefore, final float spanMiddle, final float spanAfter, final float extent)
		{
			final float origin; 	//we'll find the origin coordinate along the axis
			final float span;	//we'll find the span into which the coordinate should be determined
			final Region region=getRegion();	//get our region
			switch(region)	//see which region is specified horizontally
			{
				case BEFORE:
					origin=0;
					span=spanBefore;
					break;
				case MIDDLE:
					origin=spanBefore;
					span=spanMiddle;
					break;
				case AFTER:
					origin=spanBefore+spanMiddle;
					span=spanAfter;
					break;
				default:
					throw new AssertionError("Unknown region "+region);
			}
			return origin+getAlignment()*(span-extent);	//align the object in the span and then compensate for the origin
		}
	}

	/**A position based upon a region.
	@author Garret Wilson
	*/
	public static class RegionPosition implements Position
	{
		/**The region-based location along the X axis.*/
		private final AxisLocation locationX;
		
			/**@return The region-based location along the X axis.*/
			public AxisLocation getLocationX() {return locationX;}
		
		/**The region-based location along the Y axis.*/
		private final AxisLocation locationY;
		
			/**@return The region-based location along the X axis.*/
			public AxisLocation getLocationY() {return locationY;}
			
		/**Constructs a position aligned in a region.
		@param locationX The region-based location along the X axis.
		@param locationY The region-based location along the Y axis.
		*/
		public RegionPosition(final AxisLocation locationX, final AxisLocation locationY)
		{
			this.locationX=locationX;	//save the region location along the X axis
			this.locationY=locationY;	//save the region location along the Y axis
		}
		
		/**@return A clone of the position.
		@exception CloneNotSupportedException if the clone operation fails.
		*/
    public Object clone() throws CloneNotSupportedException
		{
			return super.clone();	//return a clone of the position
		}
	}
	
	/**A position based upon a location.
	@author Garret Wilson
	*/
	public static class LocationPosition implements Position
	{
	
		/**The location coordinates.*/
		private Point location;
		
			/**@return The location coordinates.*/
			public Point getLocation() {return location;}

		/**Whether the component should be centered at its location.*/
		private final boolean centered;

			/**@return Whether the component should be centered at its location.*/
			public boolean isCentered() {return centered;}
	
		/**Constructs a position at a location.
		@param location The location coordinates.
		@param centered Whether the component should be centered at its location.
		@exception NullPointerException if the location is <code>null</code>.
		*/
		public LocationPosition(final Point location, final boolean centered)
		{
			this.location=checkInstance(location, "Location cannot be null.");	//save the location
			this.centered=centered;	//save the centered specification
		}

		/**@return A clone of the position.
		@exception CloneNotSupportedException if the clone operation fails.
		*/
    public Object clone() throws CloneNotSupportedException
		{
			final LocationPosition locationPosition=(LocationPosition)super.clone();	//create a clone of the position
			locationPosition.location=(Point)location.clone();	//clone the location
			return locationPosition;	//return the location
		}
	}
	
	/**The view for which components will be managed.*/
	private final View view;

		/**@return The view for which components will be managed.*/
		protected View getView() {return view;}

	/**The map of component information, each keyed to a component being managed.
	@see #ComponentInfo
	*/
	protected final Map<Component, ComponentInfo> componentInfoMap=new HashMap<Component, ComponentInfo>();

	/**@return A read-only set of components managed by this object.*/ 
	public Set<Component> getComponents()
	{
		return unmodifiableSet(componentInfoMap.keySet());	//return a read-only set of components
	}

	/**@return A read-only collection of component information managed by this object.*/ 
	public Collection<ComponentInfo> getComponentInfos()
	{
		return unmodifiableCollection(componentInfoMap.values());	//return a read-only set of component information
	}

		/**Retrieves the component information for a specific component.
		@param component The component for which information should be retrieved.
		@return The managed component's information, <code>null</code> if there is no
			information stored for the specified component.
		*/
/*G***del if not needed
		protected ComponentInfo getComponentInfo(final Component component)
		{
			return componentInfoMap.get(component); //get the component information for the component
		}
*/

	/**The current location of the view.*/
	protected final Point location=new Point();

	/**The unscaled width.*/
	protected float fullWidth=-1;

	/**The unscaled height.*/
	protected float fullHeight=-1;

	/**The current scaled width.*/
	protected float scaledWidth=-1;

	/**The current scaled height.*/
	protected float scaledHeight=-1;

	/**The current ratio of scaled width to full width.*/
	protected float xMultiplier=1.0f;

	/**The current ratio of scaled height to full height.*/
	protected float yMultiplier=1.0f;

	/**Whether we are currently being shown. Used so that new components
		can be shown or hidden appropriately when they are first added.
	*/
	private boolean showing=false;

	/**The minimum space needed for components in the left inset.*/
	private int minimumLeftInset=0;

		/**@return The minimum space needed for components in the left inset.*/
		public int getMinimumLeftInset() {return minimumLeftInset;}

	/**The minimum space needed for components in the right inset.*/
	private int minimumRightInset=0;

		/**@return The minimum space needed for components in the right inset.*/
		public int getMinimumRightInset() {return minimumRightInset;}

	/**The minimum space needed for components in the top inset.*/
	private int minimumTopInset=0;

		/**@return The minimum space needed for components in the top inset.*/
		public int getMinimumTopInset() {return minimumTopInset;}

	/**The minimum space needed for components in the bottom inset.*/
	private int minimumBottomInset=0;

		/**@return The minimum space needed for components in the bottom inset.*/
		public int getMinimumBottomInset() {return minimumBottomInset;}

	/**Constructor that specifies a view for which components will be managed.
	@param ownerView The view that will contain Java AWT and/or Swing components.
	*/
	public ViewComponentManager(final View ownerView)
	{
		view=ownerView; //save the view for which components will be managed
	}

	/**Adds a component to be managed.
		Sets the component's visibility based upon the current showing status.
	@param component The component to be managed.
	*/
/*TODO del
	public synchronized void add(final Component component)
	{
		add(new ComponentInfo(component)); //add the component with default component info
	}
*/

	/**Adds a component to be managed, along with its location, which will also be managed.
		The component location will automatically be scaled when the view size
		changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param x The horizontal position of the component, relative to the view.
	@param y The vertical position of the component, relative to the view.
	*/
	public synchronized void add(final Component component, final int x, final int y)
	{
		add(new ComponentInfo(component, x, y)); //add the component with its component info
	}

	/**Adds a component to be managed, along with its location, which will also
		be managed.
		The component location will automatically be scaled when the view size
		changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param location The position of the component, relative to the view.
	*/
	public synchronized void add(final Component component, final Point location)
	{
		add(component, location.x, location.y); //add the component with the location information
	}

	/**Adds a component to be managed, along with its location, which will also
		be managed. The component is specified as centered or not centered around
		the location.
		The component location will automatically be scaled when the view size
		changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param location The position of the component, relative to the view.
	@param centered Whether the component should be centered at its location.
	*/
	public synchronized void add(final Component component, final Point location, final boolean centered)
	{
		add(component, location.x, location.y, centered); //add the component with the location information
	}
	
	/**Adds a component to be managed, along with its location, which will also
		be managed. The component is specified as centered or not centered around
		the location.
		Specifies a location for the component, which will automatically be scaled
		when the view size changes, provided the manager is notified of the size
		change.
	@param component The component to be managed.
	@param x The horizontal position of the component, relative to the view.
	@param y The vertical position of the component, relative to the view.
	@param centered Whether the component should be centered at its location.
	*/
	public synchronized void add(final Component component, final int x, final int y, final boolean centered)
	{
		add(new ComponentInfo(component, x, y, centered)); //add the component with its component info
	}
	/**Adds a component to be managed, along with its location and size, which
		will also be managed.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param x The horizontal position of the component, relative to the view.
	@param y The vertical position of the component, relative to the view.
	@param width The width of the component, relative to the view width.
	@param height The height of the component, relative to the view width.
	*/
	public synchronized void add(final Component component, final int x, final int y, final int width, final int height)
	{
		add(new ComponentInfo(component, x, y, width, height)); //add the component with its component info
	}

	/**Adds a component to be managed, along with its location and size, which
		will also be managed.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param location The position of the component, relative to the view.
	@param size The size of the component, relative to the view size.
	*/
	public synchronized void add(final Component component, final Point location, final Dimension size)
	{
		add(component, location.x, location.y, size.width, size.height); //add the component with its location and size information
	}

	/**Adds a component to be managed, along with its location and size, which
		will also be managed.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param rectangle The location and size of the component, relative to the view.
	*/
	public synchronized void add(final Component component, final Rectangle rectangle)
	{
		add(component, rectangle.x, rectangle.y, rectangle.width, rectangle.height); //add the component with its location and size information
	}

	/**Adds a component to be managed, along with its location and size, which
		will also be managed. The component is specified as centered or not centered
		around the location.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param location The position of the component, relative to the view.
	@param size The size of the component, relative to the view size.
	@param centered Whether the component should be centered at its location.
	*/
	public synchronized void add(final Component component, final Point location, final Dimension size, final boolean centered)
	{
		add(component, location.x, location.y, size.width, size.height, centered); //add the component with its location and size information
	}
	
	/**Adds a component to be managed, along with its location and size, which
		will also be managed. The component is specified as centered or not
		centered around the location.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param x The horizontal position of the component, relative to the view.
	@param y The vertical position of the component, relative to the view.
	@param width The width of the component, relative to the view width.
	@param height The height of the component, relative to the view width.
	@param centered Whether the component should be centered at its location.
	*/
	public synchronized void add(final Component component, final int x, final int y, final int width, final int height, final boolean centered)
	{
		add(new ComponentInfo(component, x, y, width, height, centered)); //add the component with its component info
	}

	/**Adds a component to be managed, along with a position.
	@param component The component to be managed.
	@param regionX The region along the X axis relative to the origin.
	@param alignmentX The alignment along the X axis (0.0 to 1.0, inclusive) relative to the origin.
	@param regionY The region along the Y axis relative to the origin.
	@param alignmentY The alignment along the Y axis (0.0 to 1.0, inclusive) relative to the origin.
	@exception IllegalArgumentException if the alignment is less than 0.0 or greater than 1.0.
	*/
	public synchronized void add(final Component component, final AxisLocation.Region regionX, final float alignmentX, final AxisLocation.Region regionY, final float alignmentY)
	{
		add(new ComponentInfo(component, regionX, alignmentX, regionY, alignmentY)); //add the component with its position information
	}

	/**Adds a component to be managed, along with its size, which
		will also be managed. The position is also specified.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param regionX The region along the X axis relative to the origin.
	@param alignmentX The alignment along the X axis (0.0 to 1.0, inclusive) relative to the origin.
	@param regionY The region along the Y axis relative to the origin.
	@param alignmentY The alignment along the Y axis (.0 to 1.0, inclusive) relative to the origin.
	@param size The size of the component, relative to the view size.
	@exception IllegalArgumentException if the alignment is less than 0.0 or greater than 1.0.
	*/
	public synchronized void add(final Component component, final AxisLocation.Region regionX, final float alignmentX, final AxisLocation.Region regionY, final float alignmentY, final Dimension size)
	{
		add(component, regionX, alignmentX, regionY, alignmentY, size.width, size.height); //add the component with its size and border information
	}

	/**Adds a component to be managed, along with its size, which
		will also be managed. The position is also specified.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param regionX The region along the X axis relative to the origin.
	@param alignmentX The alignment along the X axis (0.0 to 1.0, inclusive) relative to the origin.
	@param regionY The region along the Y axis relative to the origin.
	@param alignmentY The alignment along the Y axis (0.0 to 1.0, inclusive) relative to the origin.
	@param width The width of the component, relative to the view width.
	@param height The height of the component, relative to the view width.
		@exception IllegalArgumentException if the alignment is less than 0.0 or greater than 1.0.
	*/
	public synchronized void add(final Component component, final AxisLocation.Region regionX, final float alignmentX, final AxisLocation.Region regionY, final float alignmentY, final int width, final int height)
	{
		add(new ComponentInfo(component, regionX, alignmentX, regionY, alignmentY, width, height)); //add the component with its component info
	}

	/**Adds a component to be managed, along with its associated component information.
		Sets the component's visibility based upon the current showing status.
	@param componentInfo The information about the component, which will be managed as well
	*/
	public synchronized void add(final ComponentInfo componentInfo)
	{
		final Component component=componentInfo.getComponent();	//get the component to be managed
		componentInfoMap.put(component, componentInfo); //store the information in the component information map, keyed to the component we're managing
		component.setSize(component.getPreferredSize());  //set the component's size to whatever it prefers
		component.validate(); //tell the component to validate itself, laying out its child components if needed
		updateComponentScaledPosition(componentInfo); //update the component's scaled location and size
		setShowing(component, showing); //show or hide the component appropriately
	}

	/**Called when the view is being hidden by a parent that hides views, such
		as a paged view. All managed components will be hidden.
	@param newShowing <code>true</code> if the view is beginning to be shown,
		<code>false</code> if the view is beginning to be hidden.
	*/
	public synchronized void setShowing(final boolean newShowing)
	{
/*G***del
Debug.trace("****setShowing()");  //G***del
Debug.trace("set showing, old: ", new Boolean(showing)); //G***del
Debug.trace("set showing, new: ", new Boolean(newShowing)); //G***del
Debug.traceStack(); //G***del
*/
		showing=newShowing; //update our showing status
		for(final Component component:getComponents())  //for each component
		{
		  setShowing(component, newShowing);  //show or hide this component
		}
	}

	/**Sets a component to be showing or not showing appropriately.
		If the component should be showing, it is set to be visible and is added to
		the container if it isn't added already.
		If the component should not be showing, it is hidden and removed from the
		container.
	@param component The component to be shown or hidden.
	@param showing <code>true</code> if the component should be shown,
		<code>false</code> if its should be hidden.
	*/
	protected void setShowing(final Component component, final boolean showing)
	{
//G***del Debug.trace("setting component showing: ", new Boolean(showing)); //G***del
		component.setVisible(showing); //show or hide the component appropriately
		final Container container=getView().getContainer();  //get the container the view is placed in
		if(showing) //if we're now showing the component
		{
//G***del Debug.trace("showing component"); //G***del
			if(container!=null && component.getParent()!=container) //if we have a valid container, and the component isn't already in the container
			{
//G***del Debug.trace("component into container"); //G***del
//G***del System.out.println("component size before added to container: "+component.getSize().getWidth()+" height: "+component.getSize().getHeight());  //G***del
				container.add(component); //add the component to the container
//G***del System.out.println("component size after added to container: "+component.getSize().getWidth()+" height: "+component.getSize().getHeight());  //G***del

			}
		}
		else /*G***del if(!showing)*/  //if we're now being hidden (even if we were already hidden, container.remove(component) shouldn't hurt if the component was already removed)
		{
//G***del Debug.trace("hiding component"); //G***del
		//G***testing a way to remove the component from the container
//G***del Debug.trace("Container: ", container);  //G***del
			if(container!=null && component.getParent()==container) //if we have a valid container, and the component is in the container
			{
//G***del Debug.trace("removing component from container"); //G***del
				container.remove(component); //remove the component from the container
			}
		}
	}

	/**Indicates the view size is changing, and modifies the locations of all
		components appropriately that have registered a location relative to the
		absolute view size.
	This version sets the full size to match the scaled size.
	@param newScaledWidth The current scaled width (>=0).
	@param newScaledHeight The current scaled height (>=0).
	@see #setSize(float, float, float, float)
	*/
	public void setSize(final float newScaledWidth, final float newScaledHeight)
	{
		setSize(newScaledWidth, newScaledHeight, newScaledWidth, newScaledHeight);	//use the same full and scaled size
	}

	/**Indicates the view size is changing, and modifies the locations of all
		components appropriately that have registered a location relative to the
		absolute view size. The absolute size of the view must first have been set.
	@param newFullWidth The unscaled width (>=0).
	@param newFullHeight The unscaled height (>=0).
	@param newScaledWidth The current scaled width (>=0).
	@param newScaledHeight The current scaled height (>=0).
	@see #updateComponentScaledPositions()
	*/
	public void setSize(final float newFullWidth, final float newFullHeight, final float newScaledWidth, final float newScaledHeight)
	{
		if(fullWidth!=newFullWidth || fullHeight!=newFullHeight
			  || scaledWidth!=newScaledWidth || scaledHeight!=newScaledHeight)  //if one of the values are changing
		{
			fullWidth=newFullWidth;       //update the values
			fullHeight=newFullHeight;
			scaledWidth=newScaledWidth;
			scaledHeight=newScaledHeight;
		//if we have valid values for everything
			if(fullWidth>0 && fullHeight>0 && scaledWidth>0 && scaledHeight>0)
			{
				xMultiplier=scaledWidth/fullWidth; //calculate the radio of scaled with to full width
				yMultiplier=scaledHeight/fullHeight; //calculate the raio of scaled height to full height
				updateComponentScaledPositions();  //update all component locations and sizes with the new information
			}
			else  //if we don't have valid values for some of the widths and heights
			{
				xMultiplier=1.0f;  //don't scale horizontally
				yMultiplier=1.0f;  //don't scale vertically
			}
		}
	}


	/**Updates the allocation given to the view, which in turn updates the view location.
		All components the locations of which are managed are updated so that their
		locations are correctly scaled relative to the scaled size of the view.
		The size of the allocation is ignored.
		This is a convenience function so that <code>paint()</code> does not have
		to extract information from the <code>Shape</code> it receives.
	@param allocation The allocated region the view is to render into.
	@see #setLocation(int, int)
	*/
	public void setLocation(final Shape allocation)
	{
		  //get the bounding rectangle of the allocation
		final Rectangle rectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
		setLocation(rectangle.x, rectangle.y); //tell the component manager our new location, even though we're managing the component
	}

	/**Updates the view location.
		All components the locations of which are managed are updated so that their
		locations are correctly scaled relative to the scaled size of the view.
	@param x The new horizontal position of the view.
	@param y The new vertical position of the view.
	*/
	public void setLocation(final int x, final int y)
	{
		if(location.x!=x || location.y!=y)  //if the location is really changing
		{
			location.x=x; //update the position
			location.y=y;
		  updateComponentPositions(); //update the locations and sizes of the components
		}
	}

	/**Updates the scaled locations and sizes of all components we're keepting
		track of, based upon the current view size compared to its unscaled size.
		The absolute locations of the components are also updated.
	*/
	protected void updateComponentScaledPositions()
	{
		//if we have valid values for everything
		if(fullWidth>0 && fullHeight>0 && scaledWidth>0 && scaledHeight>0)
		{
			for(final ComponentInfo componentInfo:getComponentInfos())	//for each component information
			{
				updateComponentScaledPosition(componentInfo); //update this component's scaled location and size
			}
		}
	}

	/**Updates the scaled location of given component, if the manager is keeping
		track of that component's location.
		The component's absolute location is also updated.
	@param componentInfo The component information that contains the scaled location information.
	@see #updateComponentPosition
	*/
	protected void updateComponentScaledPosition(final ComponentInfo componentInfo)
	{
		final Dimension relativeSize=componentInfo.getSize(); //get the relative size of this component, if we have it
		final Dimension actualSize;	//we'll determine the current display size, which will be the scaled size or the actual size if there is no scaled size
		if(relativeSize!=null)  //if we have a preferred size for this component
		{
			actualSize=new Dimension(relativeSize); //create a new scaled size based on the preferred location
			actualSize.width=Math.round(actualSize.width*xMultiplier);  //scale the size horizontally
			actualSize.height=Math.round(actualSize.height*yMultiplier);  //scale the size vertically
			componentInfo.setScaledSize(actualSize);  //store the scaled size
		}
		else	//if we don't have a preferred size for the component
		{
			actualSize=componentInfo.getComponent().getSize();	//just use the size of the component
		}
		final Position position=componentInfo.getPosition();	//get the position of the component
		if(position instanceof RegionPosition)	//if the position specifies a region
		{
			final RegionPosition regionPosition=(RegionPosition)position;	//get the position as a region position
			final View view=getView();	//get a reference to the view
			final Insets insets=view instanceof Inset ? ((Inset)view).getInsets() : new Insets(0, 0, 0, 0);	//get the insets of the view, if the view reports its insets
			final float x=regionPosition.getLocationX().getCoordinate(insets.left, scaledWidth-insets.left-insets.right, insets.right, actualSize.width);	//determine the X coordinate
			final float y=regionPosition.getLocationY().getCoordinate(insets.top, scaledHeight-insets.top-insets.bottom, insets.bottom, actualSize.height);	//determine the Y coordinate
			componentInfo.setScaledLocation(new Point(Math.round(x), Math.round(y)));  //store the scaled location
		}
		else if(position instanceof LocationPosition)	//if the position specifies an absolute location
		{
			final LocationPosition locationPosition=(LocationPosition)position;	//get the position as a location position
			final Point location=locationPosition.getLocation();	//get the location position's location
			float x=location.x*xMultiplier;  //scale the position horizontally
			float y=location.y*yMultiplier;  //scale the position vertically
			if(locationPosition.isCentered())	//if we should center the component at the location
			{
				x-=scaledWidth/2;	//center the component horizontally
				y-=scaledHeight/2;	//center the component vertically
			}
			componentInfo.setScaledLocation(new Point(Math.round(x), Math.round(y)));  //store the scaled location
		}
		updateComponentPosition(componentInfo); //update the component's absolute location and size
	}

	/**Updates the absolute locations of all components we're keepting track of,
		based upon the current view position and the scaled locations of the
		components.
	*/
	protected void updateComponentPositions()
	{
		//if we have valid values for everything
		if(fullWidth>0 && fullHeight>0 && scaledWidth>0 && scaledHeight>0)
		{
			for(final ComponentInfo componentInfo:getComponentInfos())	//for each component information
			{
				updateComponentPosition(componentInfo); //update this component's absolute location
			}
		}
	}

	/**Updates the absolute location of given component, if the manager is keeping
		track of that component's location.
	@param componentInfo The component information that contains the scaled location information.
	*/
	protected void updateComponentPosition(ComponentInfo componentInfo)
	{
		final Component component=componentInfo.getComponent();	//get the component being managed
		final Point scaledLocation=componentInfo.getScaledLocation();  //get the scaled location of this component, if we have it
		if(scaledLocation!=null)  //if we have a scaled location for this component
		{
			if(location.x>=0 && location.y>=0)  //if we have a valid view position
			{
				int x=location.x+scaledLocation.x;  //offset the component from the horizontal view origin
				int y=location.y+scaledLocation.y;  //offset the component from the vertial view origin
/*TODO del when works
					if(componentInfo.isCentered())  //if we should center the component at the location
					{
						x-=component.getWidth()/2;  //center the component horizontally
						y-=component.getHeight()/2;  //center the component vertically
					}
*/
				component.setLocation(x, y);  //update the component's absolute location to be its scaled location relative to the location of the view, centered if necessary
				final Dimension scaledSize=componentInfo.getScaledSize();  //get the scaled size of this component, if we have it
				if(scaledSize!=null)  //if we have a scaled size for this component
				{
					component.setSize(scaledSize);  //update the component's size (sizes are absolute, and do not have to be offset from the view origin)
				}
				component.validate(); //tell the component to validate itself, laying out its child components if needed
/*G***del; sizes are relative, and don't need to be updated relative to the view origin
					final Dimension scaledSize=componentInfo.getScaledSize();  //get the scaled size of this component, if we have it
					if(scaledSize!=null)  //if we have a scaled size for this component
					{
						int width=location.x+scaledLocation.x;  //offset the component from the horizontal view origin
						int y=location.y+scaledLocation.y;  //offset the component from the vertial view origin
						if(componentInfo.isCentered())  //if we should center the component
						{
							x-=component.getWidth()/2;  //center the component horizontally
							y-=component.getHeight()/2;  //center the component vertically
						}
						component.setLocation(x, y);  //update the component's absolute location to be its scaled location relative to the location of the view, centered if necessary
*/

			}
		}
		updateMinimumInsets();	//update all the inset calculations, now that a component has changed its position and/or size
	}

	/**Updates the minimum insets based upon the current positions of all components.
	The component positions of interest are primarily those with region-based positioning that are either in <code>BEFORE</code> or <code>AFTER</code> positions.
	The component's current actual size is used for calculating minimum insets, which means it is assumed that <code>updateComponentPosition()</code> has already been called.
	*/
	protected void updateMinimumInsets()
	{
		minimumLeftInset=minimumRightInset=minimumTopInset=minimumBottomInset=0;	//reset the insets to zero
		for(final ComponentInfo componentInfo:getComponentInfos())	//for each component information
		{
			final Dimension componentSize=componentInfo.getComponent().getSize();	//get the size of the component
			final Position position=componentInfo.getPosition();	//get the position of the component
			if(position instanceof RegionPosition)	//if the position specifies a region
			{
				final RegionPosition regionPosition=(RegionPosition)position;	//get the position as a region position TODO compensate for orientation for all the following updates
				switch(regionPosition.getLocationX().getRegion())	//check the horizontal region
				{
					case BEFORE:
						minimumLeftInset=max(minimumLeftInset, componentSize.width);	//update the minimum left inset
						break;
					case AFTER:
						minimumRightInset=max(minimumRightInset, componentSize.width);	//update the minimum right inset
						break;
				}
				switch(regionPosition.getLocationY().getRegion())	//check the vertical region
				{
					case BEFORE:
						minimumTopInset=max(minimumTopInset, componentSize.height);	//update the minimum top inset
						break;
					case AFTER:
						minimumBottomInset=max(minimumBottomInset, componentSize.height);	//update the minimum bottom inset
						break;
				}
			}
		}
	}

	/**The class which encapsulates information about a component being managed.*/
	public static class ComponentInfo implements Cloneable
	{
		/**The component being managed.*/
		private final Component component;

			/**@return The component being managed.*/
			public Component getComponent() {return component;}

		/**The position of the component.*/
		private Position position;

			/**@return The position of the component.*/
			public Position getPosition() {return position;}
		
		/**The scaled location of the component relative to the scaled size of the
		  view, or <code>null</code> if the scaled location isn't available.*/
		private Point scaledLocation=null;

			/**@return The scaled location of the component relative to the scaled size of the
				view, or <code>null</code> if the scaled location isn't available.*/
			public Point getScaledLocation() {return scaledLocation;}

			/**Sets the scaled location of the component.
		  @param newScaledLocation The new scaled location of the component
				relative to the scaled size of the view, or <code>null</code> if the
				scaled location isn't available.
		  */
			protected void setScaledLocation(final Point newScaledLocation) {scaledLocation=newScaledLocation;}

		/**The preferred size of the component relative to the original size of
		  the view, or <code>null</code> if the size isn't available.*/
		private Dimension size=null;

			/**@return The preferred size of the component relative to the original size of
				the view, or <code>null</code> if the size isn't available.*/
			public Dimension getSize() {return size;}

		/**The scaled size of the component relative to the scaled size of the
		  view, or <code>null</code> if the scaled size isn't available.*/
		private Dimension scaledSize=null;

			/**@return The scaled size of the component relative to the scaled size of the
				view, or <code>null</code> if the scaled size isn't available.*/
			public Dimension getScaledSize() {return scaledSize;}

			/**Sets the scaled size of the component.
		  @param newScaledSize The new scaled size of the component
				relative to the scaled size of the view, or <code>null</code> if the
				scaled size isn't available.
		  */
			protected void setScaledSize(final Dimension newScaledSize) {scaledSize=newScaledSize;}

		/**Component constructor.
		@param component The component being managed.
		*/
/*TODO del
		public ComponentInfo(final Component component)
		{
			this.component=component;	//save the component
			border=null;	//show that there is no border specified
			centered=false;	//show that the component is not centered
		}
*/

		/**Position constructor.
		@param component The component being managed.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		*/
		public ComponentInfo(final Component component, final int x, final int y)
		{
			this(component, x, y, false);  //do the default constructing, not centering the component
		}

		/**Position constructor that accepts whether the component wants to be centered.
		@param component The component being managed.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		@param newCentered Whether the component should be centered at its location.
		*/
		public ComponentInfo(final Component component, final int x, final int y, final boolean newCentered)
		{
			this.component=component;	//save the component
			position=new LocationPosition(new Point(x, y), newCentered); //store the component's preferred location
		}

		/**Position and size constructor that accepts whether the component wants to be centered.
		@param component The component being managed.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		@param width The width of the component, relative to the view width.
		@param height The height of the component, relative to the view width.
		@param newCentered Whether the component should be centered at its location.
		*/
		public ComponentInfo(final Component component, final int x, final int y, final int width, final int height, final boolean newCentered)
		{
			this(component, x, y, newCentered);  //do the default location construction
			size=new Dimension(width, height);  //store the component's preferred size
		}

		/**Position and size constructor.
		@param component The component being managed.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		@param width The width of the component, relative to the view width.
		@param height The height of the component, relative to the view width.
		*/
		public ComponentInfo(final Component component, final int x, final int y, final int width, final int height)
		{
			this(component, x, y, width, height, false);  //do the default construction, not centering the component
		}

		/**Position constructor that accepts a border position.
		@param component The component being managed.
		@param regionX The region along the X axis relative to the origin.
		@param alignmentX The alignment along the X axis (0.0 to 1.0, inclusive) relative to the origin.
		@param regionY The region along the Y axis relative to the origin.
		@param alignmentY The alignment along the Y axis (0.0 to 1.0, inclusive) relative to the origin.
		@exception IllegalArgumentException if the alignment is less than 0.0 or greater than 1.0.
		*/
		public ComponentInfo(final Component component, final AxisLocation.Region regionX, final float alignmentX, final AxisLocation.Region regionY, final float alignmentY)
		{
			this.component=component;	//save the component
			position=new RegionPosition(new AxisLocation(regionX, alignmentX), new AxisLocation(regionY, alignmentY));	//create a position based upon the region
		}

		/**Size constructor with optional border specification.
		@param component The component being managed.
		@param regionX The region along the X axis relative to the origin.
		@param alignmentX The alignment along the X axis (0.0 to 1.0, inclusive) relative to the origin.
		@param regionY The region along the Y axis relative to the origin.
		@param alignmentY The alignment along the Y axis (0.0 to 1.0, inclusive) relative to the origin.
		@param width The width of the component, relative to the view width.
		@param height The height of the component, relative to the view width.
		@exception IllegalArgumentException if the alignment is less than 0.0 or greater than 1.0.
		*/
		public ComponentInfo(final Component component, final AxisLocation.Region regionX, final float alignmentX, final AxisLocation.Region regionY, final float alignmentY, final int width, final int height)
		{
			this(component, regionX, alignmentX, regionY, alignmentY);	//do the default construction
			size=new Dimension(width, height);  //store the component's preferred size
		}

		/**@return A deep clone of the component info, while keeping a reference to the same component.
		@exception CloneNotSupportedException if the clone operation fails.
		*/
    public Object clone() throws CloneNotSupportedException
		{
			final ComponentInfo componentInfo=(ComponentInfo)super.clone();	//create a clone of the component info
			componentInfo.position=(Position)position.clone();	//clone the position
			if(componentInfo.scaledLocation!=null)	//if there is a scaled location associated with this component
			{
				componentInfo.scaledLocation=(Point)scaledLocation.clone();	//clone the scaled location
			}
			if(componentInfo.size!=null)	//if there is a size associated with this component
			{
				componentInfo.size=(Dimension)size.clone();	//clone the size
			}
			if(componentInfo.scaledSize!=null)	//if there is a scaled size associated with this component
			{
				componentInfo.scaledSize=(Dimension)scaledSize.clone();	//clone the scaled size
			}
			return componentInfo;	//return the cloned component information
		}

	}

}