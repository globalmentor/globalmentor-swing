package com.garretwilson.swing.text;

import java.io.PrintStream;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.JTextComponent;
import javax.swing.text.Document;
import javax.swing.text.View;
import com.garretwilson.util.Debug;

/**Utilities for manipulating views and view hierarchies.
@author Garret Wilson
*/
public class ViewUtilities
{

	/**Default constructor.*/
	public ViewUtilities() {} //G***do we want to allow the view utilities class to be instantiated?

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
	public static void setParentHierarchyNull(final View view)
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