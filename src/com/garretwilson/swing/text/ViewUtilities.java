package com.garretwilson.swing.text;

import java.awt.Container;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.*;

import com.garretwilson.util.Debug;

/**Utilities for manipulating views and view hierarchies.
@author Garret Wilson
*/
public class ViewUtilities
{

	/**Default constructor.*/
	private ViewUtilities() {}

	/**Creates views for the given elements.
	<p>If the given view factory implements <code>ViewsFactory</code>, this
		implementation will ask the views factory to create as many views as
		needed for the entire element hierarchy, essentially collapsing all child
		elements into a single level.</p> 
	@param elements The elements for each of which one or more views should be
		created.
	@param viewFactory The view factory to use for creating views. For correct
		formatting, this should be a <code>ViewsFactory</code>.
	@return An array of views representing the given elements.
	@see ViewsFactory
	*/
	public static View[] createViews(final Element[] elements, final ViewFactory viewFactory)
	{
		final List<View> viewList=new ArrayList<View>(elements.length); //create a new list in which to store views to add; we know there will be at least as many views as elements---maybe more
		if(viewFactory instanceof ViewsFactory) //if this view factory knows how to create multiple views
		{
			final ViewsFactory viewsFactory=(ViewsFactory)viewFactory;  //cast the view factory to a views factory
			for(int i=0; i<elements.length; ++i)  //look at each child element
			{
				viewsFactory.create(elements[i], viewList);  //create one or more views and add them to our list
			}
		}
		else  //if this is a normal view factory
		{
			for(int i=0; i<elements.length; ++i)  //look at each child element
			{
				viewList.add(viewFactory.create(elements[i]));  //create a single view for this element and add it to our list
			}
		}
		//create an array of views, making it the correct size (we now know how many views there will be), and placing the contents of the list into the array
		return viewList.toArray(new View[viewList.size()]);
	}

	/**Creates one or more views for the given element and adds them to the given list.
	<p>If the given view factory implements <code>ViewsFactory</code>, this
		implementation will ask the views factory to create as many views as
		needed for the entire element hierarchy, essentially collapsing all child
		elements into a single level.</p> 
	@param elements The elements for which one or more views should be created.
	@param viewFactory The view factory to use for creating views, preferably a <code>ViewsFactory</code>.
	@param viewList The list of views to which the views should be added.
	@see ViewsFactory
	*/
	public static void createViews(final Element element, final ViewFactory viewFactory, final List<View> viewList)
	{
		if(viewFactory instanceof ViewsFactory) //if this view factory knows how to create multiple views
		{
			final ViewsFactory viewsFactory=(ViewsFactory)viewFactory;  //cast the view factory to a views factory
			viewsFactory.create(element, viewList);  //create one or more views and add them to our list
		}
		else  //if this is a normal view factory
		{
			viewList.add(viewFactory.create(element));  //create a single view for this element and add it to our list
		}
	}

	/**Determines the logical parent of the view.
	This returns the actual parent unless the view is a fragment,
		in which case the original, unbroken view is returned.
	@param view The view for which a logical parent should be found.
	@return The logical parent of the view, or <code>null</code> if the view has no parent.
	@see View#getParent()
	@see FragmentView#getWholeView()
	*/
	public static View getLogicalParent(final View view)
	{
		final View parent=view.getParent();	//get the normal parent
		return parent instanceof FragmentView ? ((FragmentView)parent).getWholeView() : parent;	//return the parent's unbroken view, if the parent is a fragment; otherwise, just return the parent 
	}
	
	/**If the view implements <code>ViewHidable</code> it is notified that it
		is about to be hidden. All child views of the view are notified as well, and
		so on down the hierarchy.
	@param view The view which should be hidden, along with all its children.
	*/
	public static void hideView(final View view)
	{
//G***del Debug.trace("Hiding view: ", view); //G***del
		if(view!=null)  //if we have a valid view
		{
			if(view instanceof ViewHidable) //if this is a hidable view
			{
//G***del Debug.trace("Inside hideview(), found hidable: "+view);  //G**del
				((ViewHidable)view).setShowing(false); //tell the view to hide itself
			}
			final int viewCount=view.getViewCount();  //get the number of child views
			for(int i=0; i<viewCount; ++i)  //look at each child view
				hideView(view.getView(i));  //tell this child view to hide itself
		}
	}

	/**Sets the parent view of the given view, and all views beneath it in the
		view hierarchy, to <code>null</code>. No views are actually removed from
		any others.
	@param view The view the parent of which should be set to <code>null</code>,
		along with the parents of all its children.
	@see View#setParent
	*/
	public static void setParentHierarchyNull(final View view)	//TODO del if not needed
	{
//G***del Debug.trace("View has child views: ", view.getViewCount()); //G***del
		for(int i=view.getViewCount()-1 ; i>=0; --i) //look at each child view
		{
//G***del 			final View childView=view.getView(i);  //G***del; testing
//G***del Debug.trace("Looking at view "+i+": ", view!=null ?	childView.getClass().getName() : "null");  //G***del
			setParentHierarchyNull(view.getView(i));  //set the hierarchy of the child view to null
		}
		view.setParent(null); //show that this view has no parent
	}

	/**Invalidates the given view and asks the container to repaint itself.
	@param boxView The box view the layout of which should be recalculated.
	@see BoxView#layoutChanged(int)
	*/
	public static void relayout(final BoxView boxView)
	{
		boxView.layoutChanged(BoxView.X_AXIS);	//invalidate the view's horizontal axis
		boxView.layoutChanged(BoxView.Y_AXIS);	//invalidate the view's our vertical axis
		final Container container=boxView.getContainer();	//get a reference to the view's container
		if(container!=null)	//if the view is in a container
		{
			container.repaint();	//tell the container to repaint itself
		}
	}

	/**Traverses the entire child hierarchy of the given view and, if any children
		reference a parent other than the parent that owns it, resets the parent of
		that child to correctly reference its parent.
	@param view The view the children of which should be reparented down the hierarchy.
	@see View#getParent
	@see View#setParent
	*/
	public static void reparentHierarchy(final View view)
	{
		for(int i=view.getViewCount()-1; i>=0; --i) //look at each child view
		{
			final View childView=view.getView(i); //get a reference to the child view
		  if(childView.getParent()!=view)  //if this view has a different parent than this one
		  {
				childView.setParent(view);	//set this view's parent to the parent view
/*G***del; trying to fix vertical bug
				if(childView instanceof BoxView)	//G***testing vertical layout
				{
					((BoxView)childView).layoutChanged(View.X_AXIS);
					((BoxView)childView).layoutChanged(View.Y_AXIS);
				}
*/
		  }
			reparentHierarchy(childView);  //reparent all views under this one
		}
	}

	/**Traverses the entire child hierarchy of the given view and, if any children
		have a parent of <code>null</code>, resets the parent of that child to
		correctly reference its parent.
	@param view The view the children of which should be reparented down the hierarchy.
	@see View#getParent
	@see View#setParent
	*/
/**G***del if not needed
	public static void reparentNullHierarchy(final View view)
	{
		for(int i=view.getViewCount()-1; i>=0; --i) //look at each child view
		{
			final View childView=view.getView(i); //get a reference to the child view
		  if(childView.getParent()==null)  //if this view does not have a parent
		  {
				childView.setParent(view);	//set this view's parent to the parent view
		  }
			reparentNullHierarchy(childView);  //reparent all views under this one
		}
	}
*/

	//G***testing; code modified from _Core Swing Advanced Programming_ by Kim Topley; comment
	public static void printViews(final JTextComponent component, final PrintStream printStream)
	{
		View rootView=component.getUI().getRootView(component);
		printView(rootView, printStream);
	}

	public static void printView(final View view, final PrintStream printStream)
	{
		printView(view, 0, printStream);
	}

	public static void printView(final View view, final int indent, final PrintStream printStream)
	{
		final Document document=view.getDocument();
		String name=view.getClass().getName();
		String indentString="";
		for(int i=0; i<indent; ++i)
			indentString+="\t";
//G***del			printStream.print("\t");
		int start=view.getStartOffset();
		int end=view.getEndOffset();
//G***del		float preferredSpanX=view.getPreferredSpan(View.X_AXIS);
//G***del		float preferredSpanY=view.getPreferredSpan(View.Y_AXIS);
//G***del		Debug.trace(indentString+name+"; offsets ["+start+", "+end+"] preferred spans ["+preferredSpanX+", "+preferredSpanY+"] parent: "+view.getParent());
		printStream.println(indentString+name+"; offsets ["+start+", "+end+"] parent: "+(view.getParent()!=null ? view.getParent().getClass().getName() : "null"));
//G***del		printStream.println(indentString+"  attributes: "+AttributeSetUtilities.getAttributeSetString(view.getAttributes()));  //G***del
//G***del		printStream.println(name+"; offsets ["+start+", "+end+"] preferred spans ["+preferredSpanX+", "+preferredSpanY+"]");
		int viewCount=view.getViewCount();
		if(viewCount==0)
		{
			int length=Math.min(32, end-start);
			try
			{
				String text=document.getText(start, length);
/*G***fix
				for(int i=0; i<indent+1; ++i)
					printStream.print("\t");
*/
				printStream.println(indentString+"["+text+"]");
//G***del				printStream.println("["+text+"]");
			}
			catch(BadLocationException e)
			{
				Debug.error(e);
		  }
		}
		else
		{
			for(int i=0; i<viewCount; ++i)
				printView(view.getView(i), indent+1, printStream);
		}
	}

	//G***testing; code modified from _Core Swing Advanced Programming_ by Kim Topley; comment
	public static String toString(final JTextComponent component)
	{
		return toString(component.getUI().getRootView(component));
	}

	public static String toString(final View view)
	{
		return format(view, 0, new StringBuilder()).toString();
	}

	protected static StringBuilder format(final View view, final int indent, final StringBuilder stringBuilder)
	{
		final Document document=view.getDocument();
		String name=view.getClass().getName();
		for(int i=0; i<indent; ++i)
		{
			stringBuilder.append('\t');
		}
//G***del			printStream.print("\t");
		int start=view.getStartOffset();
		int end=view.getEndOffset();
//G***del		float preferredSpanX=view.getPreferredSpan(View.X_AXIS);
//G***del		float preferredSpanY=view.getPreferredSpan(View.Y_AXIS);
//G***del		Debug.trace(indentString+name+"; offsets ["+start+", "+end+"] preferred spans ["+preferredSpanX+", "+preferredSpanY+"] parent: "+view.getParent());
		stringBuilder.append(name).append("; offsets [").append(start).append(", ").append(end).append("] parent: ");
		stringBuilder.append(view.getParent()!=null ? view.getParent().getClass().getName() : "null");
		stringBuilder.append('\n');
//G***del		printStream.println(indentString+"  attributes: "+AttributeSetUtilities.getAttributeSetString(view.getAttributes()));  //G***del
//G***del		printStream.println(name+"; offsets ["+start+", "+end+"] preferred spans ["+preferredSpanX+", "+preferredSpanY+"]");
		int viewCount=view.getViewCount();
		if(viewCount==0)
		{
			int length=Math.min(32, end-start);
			try
			{
				String text=document.getText(start, length);
				for(int i=0; i<indent; ++i)
				{
					stringBuilder.append('\t');
				}
				stringBuilder.append('[').append(text).append(']');
				stringBuilder.append('\n');
//G***del				printStream.println("["+text+"]");
			}
			catch(BadLocationException e)
			{
				Debug.error(e);
		  }
		}
		else
		{
			for(int i=0; i<viewCount; ++i)
				format(view.getView(i), indent+1, stringBuilder);
		}
		return stringBuilder;
	}

}