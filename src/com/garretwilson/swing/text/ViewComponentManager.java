package com.garretwilson.swing.text;

import java.awt.*;
import java.util.*;
import static java.util.Collections.*;
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

	/**A designation of a border position.*/
	public enum Border
	{
		/**The top position.*/
		NORTH,
		/**The bottom position.*/
		SOUTH,
		/**The right position.*/
		EAST,
		/**The left position.*/
		WEST,
		/**The center position.*/
		CENTER,
		/**Before the first line of the layout's content; for left-to-right, top-to-bottom orientation, equivalient to <code>NORTH</code>.*/
		PAGE_START,
		/**After the first line of the layout's content; for left-to-right, top-to-bottom orientation, equivalient to <code>SOUTH</code>.*/
		PAGE_END,
		/**Beginning of the line direction for the layout; for left-to-right, top-to-bottom orientation, equivalient to <code>WEST</code>.*/
		LINE_START,
		/**End of the line direction for the layout; for left-to-right, top-to-bottom orientation, equivalient to <code>EAST</code>.*/
		LINE_END;
		
	}

	/**The view for which components will be managed.*/
	protected final View view;

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
		add(new ComponentInfo(component)); //add the component with default component info
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

	/**Adds a component to be managed, along with a border position.
	@param component The component to be managed.
	@param border The optional border of the component, which overrides its position, or <code>null</code> for no border position.
	*/
	public synchronized void add(final Component component, final Border border)
	{
		add(new ComponentInfo(component, border)); //add the component with its border information
	}

	/**Adds a component to be managed, along with its size, which
		will also be managed. The border position is also specified.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param size The size of the component, relative to the view size.
	@param border The optional border of the component, which overrides its position, or <code>null</code> for no border position.
	*/
	public synchronized void add(final Component component, final Dimension size, final Border border)
	{
		add(component, size.width, size.height, border); //add the component with its size and border information
	}

	/**Adds a component to be managed, along with its size, which
		will also be managed. The border position is also specified.
		The component location and size will automatically be scaled when the view
		size changes, provided the manager is notified of the size change.
	@param component The component to be managed.
	@param width The width of the component, relative to the view width.
	@param height The height of the component, relative to the view width.
	@param border The optional border of the component, which overrides its position, or <code>null</code> for no border position.
	*/
	public synchronized void add(final Component component, final int width, final int height, final Border border)
	{
		add(new ComponentInfo(component, width, height, border)); //add the component with its component info
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
		if(componentInfo!=null) //if we have valid component information
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
			final Border border=componentInfo.getBorder();	//see if there is a border specified
			if(border!=null)	//if a border position is specified
			{
				final int width=actualSize.width;	//get the actual width and height
				final int height=actualSize.height;	
				final int x, y;	//determine the coordinates of the component
				switch(border)	//see which border is specified
				{
					case NORTH:
					case PAGE_START:	//TODO i81n
						x=Math.round((scaledWidth-width)/2);	//center the component horizontally
						y=0;	//place the component at the top
						break;
					case SOUTH:
					case PAGE_END:	//TODO i81n
						x=Math.round((scaledWidth-width)/2);	//center the component horizontally
						y=Math.round(scaledHeight-height);	//place the component at the bottom
						break;
					case WEST:
					case LINE_START:	//TODO i81n
						x=0;	//place the component on the left
						y=Math.round((scaledHeight-height)/2);	//center the component vertically TODO decide if we want to move this up or down
						break;
					case EAST:
					case LINE_END:	//TODO i81n
						x=Math.round(scaledWidth-width);	//place the component on the right
						y=Math.round((scaledHeight-height)/2);	//center the component vertically TODO decide if we want to move this up or down
						break;
					case CENTER:
						x=Math.round((scaledWidth-width)/2);	//center the component horizontally
						y=Math.round((scaledHeight-height)/2);	//center the component vertically
					default:	//we should have covered all the options
						throw new AssertionError("Unknown border position "+border);
				}
				componentInfo.setScaledLocation(new Point(x, y));  //store the scaled location
			}
			else	//if no border position is specified
			{
				final Point location=componentInfo.getLocation(); //get the relative location of this component, if we have it
				if(location!=null)  //if we have a preferred location for this component
				{
					final Point scaledLocation=new Point(location); //create a new scaled location based on the preferred location
					scaledLocation.x=Math.round(scaledLocation.x*xMultiplier);  //scale the position horizontally
					scaledLocation.y=Math.round(scaledLocation.y*yMultiplier);  //scale the position vertically
					componentInfo.setScaledLocation(scaledLocation);  //store the scaled location
				}
			}
			updateComponentPosition(componentInfo); //update the component's absolute location and size
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
		if(componentInfo!=null) //if we have valid component information TODO is this test needed anymore?
		{
			final Component component=componentInfo.getComponent();	//get the component being managed
			final Point scaledLocation=componentInfo.getScaledLocation();  //get the scaled location of this component, if we have it
			if(scaledLocation!=null)  //if we have a scaled location for this component
			{
				if(location.x>=0 && location.y>=0)  //if we have a valid view position
				{
					int x=location.x+scaledLocation.x;  //offset the component from the horizontal view origin
					int y=location.y+scaledLocation.y;  //offset the component from the vertial view origin
					if(componentInfo.isCentered())  //if we should center the component at the location
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
	public static class ComponentInfo implements Cloneable
	{
		/**The component being managed.*/
		private final Component component;

			/**@return The component being managed.*/
			public Component getComponent() {return component;}

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

		/**The optional border of the component, which overrides its position, or <code>null</code> for no border position.*/
		private final Border border;
			
			/**@erturn The optional border of the component, which overrides its position, or <code>null</code> for no border position.*/
			public Border getBorder() {return border;}

		/**Whether the component should be centered at its location.*/
		private final boolean centered;

			/**@return Whether the component should be centered at its location.*/
			public boolean isCentered() {return centered;}

		/**Component constructor.
		@param component The component being managed.
		*/
		public ComponentInfo(final Component component)
		{
			this.component=component;	//save the component
			border=null;	//show that there is no border specified
			centered=false;	//show that the component is not centered
		}

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
			location=new Point(x, y); //store the component's preferred location
			centered=newCentered; //store the centering status
			border=null;	//show that no border position is specified
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
		@param border The optional border of the component, which overrides its position, or <code>null</code> for no border position.
		*/
		public ComponentInfo(final Component component, final Border border)
		{
			this.component=component;	//save the component
			this.border=border; //store the border position
			centered=false;	//show that the component is not centered
		}

		/**Size constructor with optional border specification.
		@param component The component being managed.
		@param x The horizontal position of the component, relative to the view.
		@param y The vertical position of the component, relative to the view.
		@param width The width of the component, relative to the view width.
		@param height The height of the component, relative to the view width.
		@param border The optional border of the component, which overrides its position, or <code>null</code> for no border position.
		*/
		public ComponentInfo(final Component component, final int width, final int height, final Border border)  //G***does this constructor even make sense?
		{
			this(component, border);	//do the default construction
			size=new Dimension(width, height);  //store the component's preferred size
		}

		/**@return A deep clone of the component info, while keeping a reference to the same component.
		@exception CloneNotSupportedException if the clone operation fails.
		*/
    public Object clone() throws CloneNotSupportedException
		{
			final ComponentInfo componentInfo=(ComponentInfo)super.clone();	//create a clone of the component info
			if(componentInfo.location!=null)	//if there is a location associated with this component
			{
				componentInfo.location=(Point)location.clone();	//clone the location
			}
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