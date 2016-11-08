/*
 * Copyright © 1996-2009 GlobalMentor, Inc. <http://www.globalmentor.com/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.globalmentor.swing.text;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.*;

import com.globalmentor.log.Log;

/**
 * Utilities for manipulating views and view hierarchies.
 * @author Garret Wilson
 */
public class Views {

	/** Default constructor. */
	private Views() {
	}

	/**
	 * Creates views for the given elements.
	 * <p>
	 * If the given view factory implements <code>ViewsFactory</code>, this implementation will ask the views factory to create as many views as needed for the
	 * entire element hierarchy, essentially collapsing all child elements into a single level.
	 * </p>
	 * @param elements The elements for each of which one or more views should be created.
	 * @param viewFactory The view factory to use for creating views. For correct formatting, this should be a <code>ViewsFactory</code>.
	 * @return An array of views representing the given elements.
	 * @see ViewsFactory
	 */
	public static View[] createViews(final Element[] elements, final ViewFactory viewFactory) {
		final List<View> viewList = new ArrayList<View>(elements.length); //create a new list in which to store views to add; we know there will be at least as many views as elements---maybe more
		if(viewFactory instanceof ViewsFactory) { //if this view factory knows how to create multiple views
			final ViewsFactory viewsFactory = (ViewsFactory)viewFactory; //cast the view factory to a views factory
			for(int i = 0; i < elements.length; ++i) { //look at each child element
				viewsFactory.create(elements[i], viewList); //create one or more views and add them to our list
			}
		} else { //if this is a normal view factory
			for(int i = 0; i < elements.length; ++i) { //look at each child element
				viewList.add(viewFactory.create(elements[i])); //create a single view for this element and add it to our list
			}
		}
		//create an array of views, making it the correct size (we now know how many views there will be), and placing the contents of the list into the array
		return viewList.toArray(new View[viewList.size()]);
	}

	/**
	 * Creates one or more views for the given element and adds them to the given list.
	 * <p>
	 * If the given view factory implements <code>ViewsFactory</code>, this implementation will ask the views factory to create as many views as needed for the
	 * entire element hierarchy, essentially collapsing all child elements into a single level.
	 * </p>
	 * @param element The element for which one or more views should be created.
	 * @param viewFactory The view factory to use for creating views, preferably a <code>ViewsFactory</code>.
	 * @param viewList The list of views to which the views should be added.
	 * @see ViewsFactory
	 */
	public static void createViews(final Element element, final ViewFactory viewFactory, final List<View> viewList) {
		if(viewFactory instanceof ViewsFactory) { //if this view factory knows how to create multiple views
			final ViewsFactory viewsFactory = (ViewsFactory)viewFactory; //cast the view factory to a views factory
			viewsFactory.create(element, viewList); //create one or more views and add them to our list
		} else { //if this is a normal view factory
			viewList.add(viewFactory.create(element)); //create a single view for this element and add it to our list
		}
	}

	/**
	 * Determines the logical parent of the view. This returns the actual parent unless the view is a fragment, in which case the original, unbroken view is
	 * returned.
	 * @param view The view for which a logical parent should be found.
	 * @return The logical parent of the view, or <code>null</code> if the view has no parent.
	 * @see View#getParent()
	 * @see FragmentView#getWholeView()
	 */
	public static View getLogicalParent(final View view) {
		final View parent = view.getParent(); //get the normal parent
		return parent instanceof FragmentView ? ((FragmentView)parent).getWholeView() : parent; //return the parent's unbroken view, if the parent is a fragment; otherwise, just return the parent 
	}

	/**
	 * If the view implements <code>ViewHidable</code> it is notified that it is about to be hidden. All child views of the view are notified as well, and so on
	 * down the hierarchy.
	 * @param view The view which should be hidden, along with all its children.
	 */
	public static void hideView(final View view) {
		//TODO del Log.trace("Hiding view: ", view); //TODO del
		if(view != null) { //if we have a valid view
			if(view instanceof ViewHidable) { //if this is a hidable view
			//TODO del Log.trace("Inside hideview(), found hidable: "+view);  //G**del
				((ViewHidable)view).setShowing(false); //tell the view to hide itself
			}
			final int viewCount = view.getViewCount(); //get the number of child views
			for(int i = 0; i < viewCount; ++i)
				//look at each child view
				hideView(view.getView(i)); //tell this child view to hide itself
		}
	}

	/**
	 * Invalidates the layout of an entire view hierarchy. For each leaf view in the hierarchy, the parent <code>preferenceChanged()</code> method is called. This
	 * will result in many branch views being repeatedly notified of invalidation, but it guarantees that all views are invalidated. This method internally
	 * reparents all views as needed.
	 * @param view The parent of the view hierarchy to invalidate.
	 * @see #reparentHierarchy(View)
	 */
	public static void invalidateHierarchy(final View view) {
		final int viewCount = view.getViewCount(); //see how many child views there are
		if(viewCount > 0) { //if there are children
			for(int i = view.getViewCount() - 1; i >= 0; --i) { //look at each child view
				final View childView = view.getView(i); //get a reference to the child view
				/*TODO del; doesn't work
								if(childView instanceof FlowView) {	//TODO testing
									childView.removeAll();
									childView.preferenceChanged(null, true, true);
									childView.setParent(view);
								}
								else
				*/
				{
					if(childView.getParent() != view) { //if this view has a different parent than this one
						childView.setParent(view); //set this view's parent to the parent view
					}
					invalidateHierarchy(childView); //invalidate the child hierarchy
				}
			}
		} else { //if there are no children (i.e. this is a leaf view)
			final View parent = view.getParent(); //get this view's parent
			if(parent != null) { //if this view has a parent (it always should have, if we reparent above)
				parent.preferenceChanged(view, true, true); //tell this leaf view's parent that its preferences have changed
			}
		}
	}

	/**
	 * Traverses the entire child hierarchy of the given view and, if any children reference a parent other than the parent that owns it, resets the parent of
	 * that child to correctly reference its parent.
	 * @param view The view the children of which should be reparented down the hierarchy.
	 * @see View#getParent
	 * @see View#setParent
	 */
	public static void reparentHierarchy(final View view) {
		for(int i = view.getViewCount() - 1; i >= 0; --i) { //look at each child view
			final View childView = view.getView(i); //get a reference to the child view
			if(childView.getParent() != view) { //if this view has a different parent than this one
				childView.setParent(view); //set this view's parent to the parent view
			}
			reparentHierarchy(childView); //reparent all views under this one
		}
	}

	//TODO testing; code modified from _Core Swing Advanced Programming_ by Kim Topley; comment
	public static void printViews(final JTextComponent component, final PrintStream printStream) {
		View rootView = component.getUI().getRootView(component);
		printView(rootView, printStream);
	}

	public static void printView(final View view, final PrintStream printStream) {
		printView(view, 0, printStream);
	}

	public static void printView(final View view, final int indent, final PrintStream printStream) {
		final Document document = view.getDocument();
		String name = view.getClass().getName();
		String indentString = "";
		for(int i = 0; i < indent; ++i)
			indentString += "\t";
		//TODO del			printStream.print("\t");
		int start = view.getStartOffset();
		int end = view.getEndOffset();
		//TODO del		float preferredSpanX=view.getPreferredSpan(View.X_AXIS);
		//TODO del		float preferredSpanY=view.getPreferredSpan(View.Y_AXIS);
		//TODO del		Log.trace(indentString+name+"; offsets ["+start+", "+end+"] preferred spans ["+preferredSpanX+", "+preferredSpanY+"] parent: "+view.getParent());
		printStream.println(indentString + name + "; offsets [" + start + ", " + end + "] parent: "
				+ (view.getParent() != null ? view.getParent().getClass().getName() : "null"));
		//TODO del		printStream.println(indentString+"  attributes: "+AttributeSetUtilities.getAttributeSetString(view.getAttributes()));  //TODO del
		//TODO del		printStream.println(name+"; offsets ["+start+", "+end+"] preferred spans ["+preferredSpanX+", "+preferredSpanY+"]");
		int viewCount = view.getViewCount();
		if(viewCount == 0) {
			int length = Math.min(32, end - start);
			try {
				String text = document.getText(start, length);
				/*TODO fix
								for(int i=0; i<indent+1; ++i)
									printStream.print("\t");
				*/
				printStream.println(indentString + "[" + text + "]");
				//TODO del				printStream.println("["+text+"]");
			} catch(BadLocationException e) {
				Log.error(e);
			}
		} else {
			for(int i = 0; i < viewCount; ++i)
				printView(view.getView(i), indent + 1, printStream);
		}
	}

	//TODO testing; code modified from _Core Swing Advanced Programming_ by Kim Topley; comment
	public static String toString(final JTextComponent component) {
		return toString(component.getUI().getRootView(component));
	}

	public static String toString(final View view) {
		return format(view, 0, new StringBuilder()).toString();
	}

	protected static StringBuilder format(final View view, final int indent, final StringBuilder stringBuilder) {
		final Document document = view.getDocument();
		String name = view.getClass().getName();
		for(int i = 0; i < indent; ++i) {
			stringBuilder.append('\t');
		}
		//TODO del			printStream.print("\t");
		int start = view.getStartOffset();
		int end = view.getEndOffset();
		//TODO del		float preferredSpanX=view.getPreferredSpan(View.X_AXIS);
		//TODO del		float preferredSpanY=view.getPreferredSpan(View.Y_AXIS);
		//TODO del		Log.trace(indentString+name+"; offsets ["+start+", "+end+"] preferred spans ["+preferredSpanX+", "+preferredSpanY+"] parent: "+view.getParent());
		stringBuilder.append(name).append("; offsets [").append(start).append(", ").append(end).append("] parent: ");
		stringBuilder.append(view.getParent() != null ? view.getParent().getClass().getName() : "null");
		stringBuilder.append('\n');
		//TODO del		printStream.println(indentString+"  attributes: "+AttributeSetUtilities.getAttributeSetString(view.getAttributes()));  //TODO del
		//TODO del		printStream.println(name+"; offsets ["+start+", "+end+"] preferred spans ["+preferredSpanX+", "+preferredSpanY+"]");
		int viewCount = view.getViewCount();
		if(viewCount == 0) {
			int length = Math.min(32, end - start);
			try {
				String text = document.getText(start, length);
				for(int i = 0; i < indent; ++i) {
					stringBuilder.append('\t');
				}
				stringBuilder.append('[').append(text).append(']');
				stringBuilder.append('\n');
				//TODO del				printStream.println("["+text+"]");
			} catch(BadLocationException e) {
				Log.error(e);
			}
		} else {
			for(int i = 0; i < viewCount; ++i)
				format(view.getView(i), indent + 1, stringBuilder);
		}
		return stringBuilder;
	}

}