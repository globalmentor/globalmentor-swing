package com.garretwilson.swing.text;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.text.*;
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
@author Garret Wilson
*/
public class ViewComponentManager //G***finish the class comments with examples of usage
{

	/**The view for which components will be managed.*/
	protected final View view;

	/**The map of component information, each keyed to a component being managed.
	@see #ComponentInfo
	*/
	protected final Map componentInfoMap=new HashMap();

		/**Retrieves the component information for a specific component.
		@param component The component for which information should be retrieved.
		@return The managed component's information, <code>null</code> if there is no
			information stored for the specified component.
		*/
/*G***del if not needed
		protected ComponentInfo getComponentInfo(final Component component)
		{
			return (ComponentInfo)componentInfoMap.get(component); //get the component information for the component
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

	/**The list of managed components.*/
//G***del	protected final List componentList=new ArrayList(); //should we just use a set or something?

	/**The map of points, keyed to components. Each location specifies the
		preferred location of the component relative to the original size of the
		view. Only components the locations of which should be managed have points
		stored here.
	*/
//G***del	protected final Map componentLocationMap=new HashMap();

	/**The map of points, keyed to components. Each location specifies the
		scaled location of the component relative to the scaled size of the
		view. Only components the locations of which should be managed have points
		stored here.
		<p>These values are calculated each time the view is scaled, but these points
		do not represent actual screen locations as they must be offset from the
		view origin at painting time.</p>
	@see #paint
	*/
//G***del	protected final Map componentScaledLocationMap=new HashMap();

	/**Whether we are currently being shown. Used so that new components
		can be shown or hidden appropriately when they are first added.
	*/
	private boolean showing=false;

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
	public synchronized void add(final Component component)
	{
		add(component, new ComponentInfo()); //add the component with default component info
	}

	/**Adds a component to be managed, along with its location, which will also
		be managed.
		The component location will automatically be scaled when the view size
		changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param x The horizontal position of the component, relative to the view.
	@param y The vertical position of the component, relative to the view.
	*/
	public synchronized void add(final Component component, final int x, final int y)
	{
		add(component, new ComponentInfo(x, y)); //add the component with its component info
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
		add(component, new ComponentInfo(x, y, centered)); //add the component with its component info
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
		add(component, new ComponentInfo(x, y, width, height)); //add the component with its component info
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
	public synchronized void add(final Component component, final int x, final int y, final int width, final int height, final boolean centered)  //G***does this constructor even make sense?
	{
		add(component, new ComponentInfo(x, y, width, height, centered)); //add the component with its component info
	}

	/**Adds a component to be managed, along with its associated component
		information.
		Sets the component's visibility based upon the current showing status.
	@param component The component to be managed.
	@param componentInfo The information about the component, which will be
		managed as well
	*/
	protected synchronized void add(final Component component, final ComponentInfo componentInfo)
	{
		componentInfoMap.put(component, componentInfo); //store the information in the component information map, keyed to the component we're managing
		component.setSize(component.getPreferredSize());  //set the component's size to whatever it prefers
		component.validate(); //tell the component to validate itself, laying out its child components if needed
		updateComponentScaledPosition(component, componentInfo); //update the component's scaled location and size
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
		final Iterator componentIterator=componentInfoMap.keySet().iterator();  //get an iterator to look through the components
		while(componentIterator.hasNext())  //while there are more components
		{
		  final Component component=(Component)componentIterator.next();  //get the next component
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
		final Container container=view.getContainer();  //get the container the view is placed in
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
		absolute view size. The absolute size of the view must first have been set.
	@param newFullWidth The unscaled width (>=0).
	@param newFullHeight The unscaled height (>=0).
	@param newScaledWidth The current scaled width (>=0).
	@param newScaledHeight The current scaled height (>=0).
	@see updateComponentScaledPositions
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


	/**Updates the allocation given to the view. , which in turn updates the
view location.
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
		  final Iterator componentEntryIterator=componentInfoMap.entrySet().iterator(); //get an iterator to look through the component entries
			while(componentEntryIterator.hasNext())  //while there are more component entries
			{
				final Map.Entry componentEntry=(Map.Entry)componentEntryIterator.next(); //get the next component entry
				final Component component=(Component)componentEntry.getKey(); //get the component
				final ComponentInfo componentInfo=(ComponentInfo)componentEntry.getValue(); //get the associated component information
				updateComponentScaledPosition(component, componentInfo); //update this component's scaled location and size
			}
		}
	}

	/**Updates the scaled location of given component, if the manager is keeping
		track of that component's location.
		The component's absolute location is also updated.
	@param component The component the location of which should be updated
		relative to the current size of the scaled view.
	@param componentInfo The component information that contains the scaled
		location information.
	@see #updateComponentPosition
	*/
	protected void updateComponentScaledPosition(final Component component, final ComponentInfo componentInfo)
	{
		if(componentInfo!=null) //if we have valid component information
		{
			final Point location=componentInfo.getLocation(); //get the relative location of this component, if we have it
			if(location!=null)  //if we have a preferred location for this component
			{
				final Point scaledLocation=new Point(location); //create a new scaled location based on the preferred location
				scaledLocation.x=Math.round(scaledLocation.x*xMultiplier);  //scale the position horizontally
				scaledLocation.y=Math.round(scaledLocation.y*yMultiplier);  //scale the position vertically
				componentInfo.setScaledLocation(scaledLocation);  //store the scaled location
				final Dimension size=componentInfo.getSize(); //get the relative size of this component, if we have it
				if(size!=null)  //if we have a preferred size for this component
				{
					final Dimension scaledSize=new Dimension(size); //create a new scaled size based on the preferred location
					scaledSize.width=Math.round(scaledSize.width*xMultiplier);  //scale the size horizontally
					scaledSize.height=Math.round(scaledSize.height*yMultiplier);  //scale the size vertically
					componentInfo.setScaledSize(scaledSize);  //store the scaled size
				}
				updateComponentPosition(component, componentInfo); //update the component's absolute location and size
			}
		}
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
		  final Iterator componentEntryIterator=componentInfoMap.entrySet().iterator(); //get an iterator to look through the component entries
			while(componentEntryIterator.hasNext())  //while there are more component entries
			{
				final Map.Entry componentEntry=(Map.Entry)componentEntryIterator.next(); //get the next component entry
				final Component component=(Component)componentEntry.getKey(); //get the component
				final ComponentInfo componentInfo=(ComponentInfo)componentEntry.getValue(); //get the associated component information
				updateComponentPosition(component, componentInfo); //update this component's absolute location
			}
		}
	}

	/**Updates the absolute location of given component, if the manager is keeping
		track of that component's location.
	@param component The component the location of which should be updated
		relative to the current size of the scaled view.
	@param componentInfo The component information that contains the scaled
		location information.
	*/
	protected void updateComponentPosition(final Component component, final ComponentInfo componentInfo)
	{
		if(componentInfo!=null) //if we have valid component information
		{
			final Point scaledLocation=componentInfo.getScaledLocation();  //get the scaled location of this component, if we have it
			if(scaledLocation!=null)  //if we have a scaled location for this component
			{
				if(location.x>=0 && location.y>=0)  //if we have a valid view position
				{
					int x=location.x+scaledLocation.x;  //offset the component from the horizontal view origin
					int y=location.y+scaledLocation.y;  //offset the component from the vertial view origin
					if(componentInfo.isCentered())  //if we should center the component
					{
						x-=component.getWidth()/2;  //center the component horizontally
						y-=component.getHeight()/2;  //center the component vertically
					}
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
		}
	}

	/**The class which encapsulates information about a component being managed.*/
	protected static class ComponentInfo
	{

		/**The preferred location of the component relative to the original size of
		  the view, or <code>null</code> if the location isn't available.*/
		private Point location=null;

			/**@return The preferred location of the component relative to the original size of
				the view, or <code>null</code> if the location isn't available.*/
			public Point getLocation() {return location;}

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
			public void setScaledLocation(final Point newScaledLocation) {scaledLocation=newScaledLocation;}

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
			public void setScaledSize(final Dimension newScaledSize) {scaledSize=newScaledSize;}

		/**Whether the component should be centered at its location.*/
		private boolean centered=false;

			/**@return Whether the component should be centered at its location.*/
			public boolean isCentered() {return centered;}

		/**Default constructor.*/
		public ComponentInfo()
		{
		}

		/**Position constructor.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		*/
		public ComponentInfo(final int x, final int y)
		{
			this(x, y, false);  //do the default constructing, not centering the component
		}

		/**Position constructor that accepts whether the component wants to be
		  centered.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		@param newCentered Whether the component should be centered at its location.
		*/
		public ComponentInfo(final int x, final int y, final boolean newCentered)
		{
			location=new Point(x, y); //store the component's preferred location
			centered=newCentered; //store the centering status
		}

		/**Position and size constructor that accepts whether the component wants
		  to be centered.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		@param width The width of the component, relative to the view width.
		@param height The height of the component, relative to the view width.
		@param newCentered Whether the component should be centered at its location.
		*/
		public ComponentInfo(final int x, final int y, final int width, final int height, final boolean newCentered)  //G***does this constructor even make sense?
		{
			this(x, y, newCentered);  //do the default location construction
			size=new Dimension(width, height);  //store the component's preferred size
		}

		/**Position and size constructor.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		@param width The width of the component, relative to the view width.
		@param height The height of the component, relative to the view width.
		*/
		public ComponentInfo(final int x, final int y, final int width, final int height)
		{
			this(x, y, width, height, false);  //do the default construction, not centering the component
		}

	}

}