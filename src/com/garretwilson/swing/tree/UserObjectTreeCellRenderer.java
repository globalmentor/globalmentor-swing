package com.garretwilson.swing.tree;

import java.awt.Component;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/**A tree cell renderer that can render different icons and strings for various
	types of user objects.
	If the a tree node is a <code>IconTreeNode</code> that has an icon, its icon
	is used. If not, specified icons are used for the specified types of
	user objects if a tree node is a <code>DefaultMutableTreeCell</code>;
	otherwise, the default icons are used from the parent class.
	//G***fix to get strings just like we get icons: Specified strings are constructed from
@author Garret Wilson
*/
public class UserObjectTreeCellRenderer extends DefaultTreeCellRenderer
{

	/**The map used to hold the open icons, keyed to user object classes.*/
	private final Map openIconMap=new HashMap();

		/**Retrieves the open icon for a user object of the indicated key.
		@param userObjectKey The key of the user object.
		@return The icon to use for the user object, or <code>null</code> if no
			open icon is registered for this user object type.
		*/
		public Icon getOpenIcon(final Object userObjectKey)
		{
			return (Icon)openIconMap.get(userObjectKey);  //return whatever icon we find for this user object key
		}

		/**Registers an open icon to be used with a particular user object.
		@param userObjectKey The key of the user object.
		@param icon The icon to register with the user object, or <code>null</code>
			if no icon should be associated with the user object.
		*/
		protected void registerOpenIcon(final Object userObjectKey, final Icon icon)
		{
			openIconMap.put(userObjectKey, icon); //put the icon in the map
		}

		/**Registers an open icon to be used with a particular user object.
		@param userObjectClass The class of the user object.
		@param icon The icon to register with the user object, or <code>null</code>
			if no icon should be associated with the user object.
		*/
		public void registerOpenIcon(final Class userObjectClass, final Icon icon)
		{
			openIconMap.put(userObjectClass, icon); //put the icon in the map, keyed to the class of the user object
		}

	/**The map used to hold the closed icons, keyed to user object classes.*/
	private final Map closedIconMap=new HashMap();

		/**Retrieves the closed icon for a user object of the indicated key.
		@param userObjectKey The key of the user object.
		@return The icon to use for the user object, or <code>null</code> if no
			closed icon is registered for this user object type.
		*/
		public Icon getClosedIcon(final Object userObjectKey)
		{
			return (Icon)closedIconMap.get(userObjectKey);  //return whatever icon we find for this user object class
		}

		/**Registers a closed icon to be used with a particular user object.
		@param userObjectKey The key of the user object.
		@param icon The icon to register with the user object, or <code>null</code>
			if no icon should be associated with the user object.
		*/
		protected void registerClosedIcon(final Object userObjectKey, final Icon icon)
		{
			closedIconMap.put(userObjectKey, icon); //put the icon in the map
		}

		/**Registers a closed icon to be used with a particular user object.
		@param userObjectClass The class of the user object.
		@param icon The icon to register with the user object, or <code>null</code>
			if no icon should be associated with the user object.
		*/
		public void registerClosedIcon(final Class userObjectClass, final Icon icon)
		{
			closedIconMap.put(userObjectClass, icon); //put the icon in the map, keyed to the class of the user object
		}

	/**The map used to hold the leaf icons, keyed to user object classes.*/
	private final Map leafIconMap=new HashMap();

		/**Retrieves the leaf icon for a user object of the indicated key.
		@param userObjectKey The class of the user object.
		@return The icon to use for the user object, or <code>null</code> if no
			leaf icon is registered for this user object type.
		*/
		public Icon getLeafIcon(final Object userObjectKey)
		{
			return (Icon)leafIconMap.get(userObjectKey);  //return whatever icon we find for this user object class
		}

		/**Registers a leaf icon to be used with a particular user object.
		@param userObjectKey The key of the user object.
		@param icon The icon to register with the user object, or <code>null</code>
			if no icon should be associated with the user object.
		*/
		protected void registerLeafIcon(final Object userObjectKey, final Icon icon)
		{
			leafIconMap.put(userObjectKey, icon); //put the icon in the map
		}

		/**Registers a leaf icon to be used with a particular user object.
		@param userObjectClass The class of the user object.
		@param icon The icon to register with the user object, or <code>null</code>
			if no icon should be associated with the user object.
		*/
		public void registerLeafIcon(final Class userObjectClass, final Icon icon)
		{
			leafIconMap.put(userObjectClass, icon); //put the icon in the map, keyed to the class of the user object
		}

	/**Default constructor.*/
	public UserObjectTreeCellRenderer()
	{
		super();  //construct the parent object
	}

	/**Configures the renderer based on the passed in components.
		The component is configured using the defaults of the parent class, except
		for the icon.
		If the value is a <code>DefaultMutableTreeNode</code>, the user object is
		examined and any icon registered for the user item's class is used.
		Otherwise, the default icon (that configured by the parent class) is used.
		Note that this version only works property with implementations of
		<code>DefaultTreeCellRenderer</code> that return an instance of
		<code>JLabel</code>.
	@param tree The tree component.
	@param value The value for which a renderer should be returned.
	@param isSelected Whether the value is selected.
	@param isExpanded Whether the value is expanded.
	@param isLeaf Whether the value is a leaf.
	@param row The row of the value in the tree.
	@param hasFocus Whether the value has the focus.
	@return The component to use for rendering.
	@see DefaultTreeCellRenderer#getTreeCellRendererComponent
	*/
	public Component getTreeCellRendererComponent(final JTree tree,
		  final Object value, final boolean isSelected, final boolean isExpanded,
			final boolean isLeaf, final int row, final boolean hasFocus)
	{
		  //get the default component to be used for rendering
		final Component component=super.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, isLeaf, row, hasFocus);
		if(value instanceof DefaultMutableTreeNode) //if the value is a DefaultMutableTreeNode, which stores user objects
		{
			if(component instanceof JLabel) //if the component is a label (as we expect from the default implementation of DefaultTreeCellRenderer)
			{
				final JLabel label=(JLabel)component; //cast the component to a label
				final DefaultMutableTreeNode defaultMutableTreeNode=(DefaultMutableTreeNode)value;  //cast the value to the type of node that will let us get the user object
				  //get the key associated with the user object
				final Object userObjectKey=getUserObjectKey(defaultMutableTreeNode.getUserObject());
//G***del				final Class userObjectClass=defaultMutableTreeNode.getUserObject().getClass(); //get the class of the user object being stored
				final Icon openIcon=getOpenIcon(userObjectKey); //get the open icon registered for this user object key
				final Icon closedIcon=getClosedIcon(userObjectKey); //get the closed icon registered for this user object key
				final Icon leafIcon=getLeafIcon(userObjectKey); //get the leaf icon registered for this user object key
				final Icon icon;  //we'll try to assign a new icon here
				if(value instanceof IconTreeNode && ((IconTreeNode)value).getIcon()!=null) //if the tree node is an icon tree node with an icon
				{
					icon=((IconTreeNode)value).getIcon(); //use the tree node's icon
				}
				else  //if the tree node is not an icon tree node
				{
					if(isLeaf)  //if this is a leaf node
						icon=leafIcon;  //set the icon to the leaf icon we found
					else if(isExpanded)  //if this value is expanded
						icon=openIcon;  //set the icon to the open icon we found
					else  //if we shouldn't look for a leaf or closed icon
						icon=closedIcon;  //we'll go with the closed icon we found
				}
				if(icon!=null)  //if we found a new icon to use
				{
					if(tree.isEnabled()) //if the tree is enabled
						label.setIcon(icon);  //set the enabled icon
					else  //if the tree is not enabled
						label.setDisabledIcon(icon);  //set the disabled icon
				}
			}
		}
		return component; //return the component, possibly with our modifications
	}

	/**Retrieves the key used to lookup data, such as icons, specific for this
		user object.
		<p>This version returns the class of the user object, but subclasses may
		override this method to return other keys. Such subclasses should also
		allow other ways of populating the icon map to allow for other keys.</p>
	@param userObject The user object for which a key should be returned.
	@return The key for looking up data for the user object, or <code>null</code>
		if no key could be determined for the user object or if the user object
		is <code>null</code>.
	@see Object#getClass
	*/
	protected Object getUserObjectKey(final Object userObject)
	{
		return userObject!=null ? userObject.getClass() : null; //return the class of the user object, if there is a valid user object
	}
}