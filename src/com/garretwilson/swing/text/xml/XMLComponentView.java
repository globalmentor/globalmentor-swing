package com.garretwilson.swing.text.xml;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.*;

import com.globalmentor.log.Log;

/**A view that displays a given component. The component must be created before
	this view is created, and this view merely stores the component until time
	for its view "creation".
@author Garret Wilson
*/
public class XMLComponentView extends XMLAbstractComponentView
{

	/**The already created component, which will be returned by the
		<code>createComponent()</code> method.*/
	private final Component precreatedComponent;

	/**Creates a new view that represents a component, supplying the component
		to display in the view.
	@param element The element for which to create the view.
	*/
  public XMLComponentView(final Element element, final Component component)
	{
   	super(element);	//do the default constructing
Log.trace("Component width: ", component.getWidth()); //G***del
Log.trace("Component height: ", component.getHeight()); //G***del
Log.trace("Component preferred width: ", new Double(component.getPreferredSize().getWidth())); //G***del
Log.trace("Component preferred height: ", new Double(component.getPreferredSize().getHeight())); //G***del
		setHeight(component.getPreferredSize().height);  //update the standard and current heights G***testing
		setWidth(component.getPreferredSize().width);  //update the standard and current widths
		precreatedComponent=component;  //store the precreated component
	}

	/**Creates a new component for this view to represent. Because this class
		requires a component to already be created before this view is created,
		this method really just returns the component that was already created.
	@return A component to display.
	*/
	protected Component createComponent()
	{
//G***del when works		  precreatedComponent.setSize(100, 50);  //G***testing

		return precreatedComponent; //return the component we already created
	}



	/**Determines the maximum span for this view along an axis. This currently
		returns the permanent size of the object, initialized by
		<code>setWidth()</code> and <code>setHeight()</code>.
	@param axis The axis, either <code>X_AXIS</code> or <code>Y_AXIS</code>.
	@returns The maximum span the view can be rendered into.
	@exception IllegalArgumentException Thrown if the axis is not recognized.
	@see View#getPreferredSpan
	@see #getHeight
	@see #getWidth
	*/
	public float getMinimumSpan(int axis) //G***testing
	{
		//G***fix all this with the ability to fix certain axes in the parent class
		switch(axis)  //see which axis we're looking at
		{
		  case View.X_AXIS: //if the x-axis is requested
				return getWidth();  //return the width of the object as the maximum width
		  case View.Y_AXIS: //if the y-axis is requested
				return getHeight();  //return the height of the object as the maximum height
			default:  //if we don't recognize the axis
				throw new IllegalArgumentException("Invalid axis: "+axis);  //report that we don't recognize the axis
		}
	}


}