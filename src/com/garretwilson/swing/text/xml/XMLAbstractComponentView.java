package com.garretwilson.swing.text.xml;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import com.garretwilson.swing.text.ViewComponentManager;
import com.garretwilson.util.Debug;

/**Represents a Java component based upon <code>java.awt.Component</code>.
	A class must be derived from this abstract class with an appropriate
	<code>createComponent()</code> implemented, as well as the correct width and
	height set.
@see XMLObjectView#setWidth
@see XMLObjectView#setWidth
@see #createComponent
@author Garret Wilson
*/
public abstract class XMLAbstractComponentView extends XMLObjectView
{

	/**The object that manages the components this view owns.*/
	private final ViewComponentManager componentManager;

		/**@return The object that manages the components this view owns.*/
		public ViewComponentManager getComponentManager() {return componentManager;}

	/**The component this view represents.*/
	private Component component;

		/**@return The component this view represents, or <code>null</code> if the
		  component has not yet been created.
		*/
		public Component getComponent() {return component;}

		/**Sets the component represented by this view. Currently the procedure
			assumes the current thread is the AWT event thread.
			This method assumes that the view's parent has appropriately been set so
			that the container can be retrieved correctly.
		@param newComponent The component this view is to represent.
		*/
		protected void setComponent(final Component newComponent)
		{
//G***del System.out.println("XMLAbstractComponentView setting component: "+newComponent.getClass().getName());  //G***del
//G***del if not needed		  Debug.assert(SwingUtilities.isEventDispatchThread(), "XMLAbstractComponentView.setComponent() called from outside the AWT event thread.");  //G***fix; we may not even need this, if we know the size already, so we could even spin this off in a different thread as the Swing version does
		  if(newComponent!=null)  //if we are being given a component
			{
//G***del				final Dimension componentSize
//G***del				newComponent.getSize()
//G***del if not needed		    getComponentManager().add(newComponent);  //add the component to our component manager
				  //add the component to our component manager, specifying that the component should always stay the same size as the view
		    getComponentManager().add(newComponent, 0, 0, getWidth(), getHeight());
//G***fix				newComponent.validate();  //G***testing
			}
/*G***fix
			else  //if the component is being removed
				getComponentManager().re
*/
		  component=newComponent; //set the component we're assigned to
/*G***del if not needed
		  final Container container=getContainer();  //get the container we're place in G***this used to be parent.getContainer() inside setParent(); this probably won't make a difference
Debug.trace("Container: ", container);
		  if(container!=null) //if we have a valid container
				container.add(component); //add the component to the container
*/
		}

	/**Creates a new view that represents a component.
	@param element The element for which to create the view.
	*/
  public XMLAbstractComponentView(final Element element)
	{
   	super(element);	//do the default constructing
		componentManager=new ViewComponentManager(this);  //create a component manager to manage our components
	}

	/**Sets the parent of the view.
		After setting the parent using the superclass behavior, this version creates
		the component if it has not yet been created.
	@param parent The parent of the view, <code>null</code> if none.
	@see #createComponent
	*/
/*G***del
	public void setParent(final View parent)
	{
//G***del System.out.println("XMLAbstractComponentView setting parent");  //G***del
		super.setParent(parent);  //let the super class set the parent; we should do this first so setComponent() will be able to find a container to which to add the component
*/
/*G***del if not needed
		if(parent!=null && getComponent()==null) //if we've been given a parent, and we haven't yet created a component
		{
//G***del System.out.println("XMLAbstractComponentView creating component"); //G***del
//G***del Debug.trace("XMLComponentView.setParent() creating component; component before: "+component);
		  final Component component=createComponent();  //create the component
//G***fix		  component.setVisible(false); //don't show the component initially; it will be shown at the appropriate time using show() when called from paint()
		  setComponent(component);  //set the component, which will add the component to our container
		}
*/
//G***del	}

	/**Creates a component for displaying.
	@return A component to display.
	*/
	protected abstract Component createComponent();

	/**Called when the view is being hidden by a parent that hides views, such
		as a paged view. If the view is being shown, the component is created if
		needed and added to the container. If the view is being hidden, the
		component is hidden and removed from the container.
	@param showing <code>true</code> if the view is beginning to be shown,
		<code>false</code> if the view is beginning to be hidden.
	@see #getComponent
	*/
	public void setShowing(final boolean showing)
	{
		super.setShowing(showing);  //update showing in the parent class
		if(showing) //if we should be showing now
		{
/*G***del
System.out.println("Setting showing, current width: "+getCurrentWidth());  //G***del
System.out.println("Setting showing, current height: "+getCurrentHeight());  //G***del
*/
			if(getComponent()==null) //if we haven't yet created a component  //G***testing
			{
	//G***del System.out.println("XMLAbstractComponentView creating component"); //G***del
	//G***del Debug.trace("XMLComponentView.setParent() creating component; component before: "+component);
				final Component component=createComponent();  //create the component
//G***del System.out.println("component size after createComponent(): "+component.getSize().getWidth()+" height: "+component.getSize().getHeight());  //G***del
	//G***fix		  component.setVisible(false); //don't show the component initially; it will be shown at the appropriate time using show() when called from paint()
				setComponent(component);  //set the component, which will add the component to our container
			}
		}
		componentManager.setShowing(showing); //tell the component manager our new status
/*G***del when works
		final Component component=getComponent(); //get our component
		if(component!=null) //if we have a valid component
		{


//G***add a way to put the component back into the container when it's time to show it again

			component.setVisible(showing); //show or hide the component appropriately
			//G***testing a way to remove the component from the container
		  if(isShowing() && !showing)  //if we're already showing and we're now being hidden
			{
				final Container container=getContainer();  //get the container we're place in G***this used to be parent.getContainer() inside setParent(); this probably won't make a difference
Debug.trace("Container: ", container);  //G***del
				if(container!=null) //if we have a valid container
					container.remove(component); //remove the component from the container
			}


		}
		super.setShowing(showing);  //update showing in the parent class
*/
	}

	/**Performs the parent rendering functionality and ensures the component has
		the correct bounds and is laid out correctly.
	@param graphics The rendering surface to use.
	@param allocation The allocated region to render into.
	@see XMLObjectView#paint
	@see Component#setBounds
	*/
	public void paint(final Graphics graphics, final Shape allocation)
	{
/*G***del
Debug.trace("appletView.paint() currentWidth: "+currentWidth+" currentHeight: "+currentHeight);
Rectangle bounds=allocation.getBounds();  //G***testing
Debug.trace("appletView.paint() bounds: "+bounds);
*/
		super.paint(graphics, allocation);  //do the default painting, which will update our dimensions if needed
		componentManager.setLocation(allocation); //tell the component manager our new location
/*G***del
		  //get the bounding rectangle of the painting area G***use a utility function
		final Rectangle rectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();
		componentManager.setLocation(rectangle.x, rectangle.y); //tell the component manager our new location, even though we're managing the component
		final Component component=getComponent(); //get the associated component
		if(component!=null) //if we have a component
		{
			component.setBounds(rectangle.x, rectangle.y, getCurrentWidth(), getCurrentHeight()); //make sure the component has the correct bounds
			component.validate(); //tell the component to validate itself, laying out its child components if needed
		}
*/

/*G***del when works

		super.paint(graphics, allocation); //do the default painting
		final Component component=getComponent(); //get the associated component
		if(component!=null) //if we have a component
		{
			final Rectangle rectangle=(allocation instanceof Rectangle) ? (Rectangle)allocation : allocation.getBounds();  //get the bounding rectangle of the painting area
			component.setBounds(rectangle.x, rectangle.y, getCurrentWidth(), getCurrentHeight()); //make sure the component has the correct bounds
			component.validate(); //tell the component to validate itself, laying out its child components if needed


*/
/*G***del
Debug.trace("Just set bounds: "+bounds.x+" "+bounds.y+" "+currentWidth+" "+currentHeight);  //G***fix
Debug.trace("Bounds results: "+component.getBounds()); //G***testing
//G***fix component.invalidate(); //G***testing
*/
//G***del component.doLayout(); //G***testing
//G***del }

//G**del when works		}
	}

	/**Sets the size of the object, while keeping the object in the same proportions.
		Informs the component manager that it should update the components if needed.
	@param width The width (>=0).
	@param height The height (>=0).
	*/
	public void setSize(float width, float height)
	{
		super.setSize(width, height); //do the default size setting
		componentManager.setSize(getWidth(), getHeight(), getCurrentWidth(), getCurrentHeight()); //G***testing
	}

}