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

package com.garretwilson.swing.rdf.tree;

import javax.swing.*;
import com.garretwilson.resources.icon.IconResources;
import com.garretwilson.swing.tree.*;
import com.globalmentor.rdf.*;

/**A tree cell renderer that can render different icons and strings for user
	objects that are RDF resources. The class defaults to using a special icon
	for RDF literals.
	<p>If the a tree node is a <code>IconTreeNode</code> that has an icon, its
	icon is used. If not, specified icons are used for the specified types of
	RDF resource user objects, if the user object is not an RDF resource, the
	specified class of user object. If a tree node is not a
	<code>DefaultMutableTreeCell</code>, the default icons are used from the
	parent class.</p>
@author Garret Wilson
*/
public class RDFResourceTreeCellRenderer extends UserObjectTreeCellRenderer
{

		/**Registers an open icon to be used with a particular RDF resource type.
		@param resourceType The type of resource for the icon.
		@param icon The icon to register with the resource type, or <code>null</code>
			if no icon should be associated with the resource type.
		*/
		public void registerOpenIcon(final RDFResource resourceType, final Icon icon)
		{
			registerOpenIcon((Object)resourceType, icon); //put the icon in the map, keyed to the resource type
		}

		/**Registers a closed icon to be used with a particular RDF resource type.
		@param resourceType The type of resource for the icon.
		@param icon The icon to register with the resource type, or <code>null</code>
			if no icon should be associated with the resource type.
		*/
		public void registerClosedIcon(final RDFResource resourceType, final Icon icon)
		{
			registerClosedIcon((Object)resourceType, icon); //put the icon in the map, keyed to the resource type
		}

		/**Registers a leaf icon to be used with a particular RDF resource type.
		@param resourceType The type of resource for the icon.
		@param icon The icon to register with the resource type, or <code>null</code>
			if no icon should be associated with the resource type.
		*/
		public void registerLeafIcon(final RDFResource resourceType, final Icon icon)
		{
			registerLeafIcon((Object)resourceType, icon); //put the icon in the map, keyed to the resource type
		}

		/**Registers an icon to be used with all RDF literals.
		@param icon The icon to register with RDF literals, or <code>null</code>
			if no icon should be associated with RDF literals.
		*/
		public void registerRDFLiteralIcon(final Icon icon)
		{
			registerLeafIcon((Object)RDFLiteral.class, icon); //put the icon in the map, keyed to the Literal class (literals are always leaves)
		}


	/**Default constructor.*/
	public RDFResourceTreeCellRenderer()
	{
		super();  //construct the parent object
		  //setup the icon for RDF literals
		registerRDFLiteralIcon(IconResources.getIcon(IconResources.SPEECH_RECTANGLE_TEXT_ICON_FILENAME));
//TODO decide on which icon to use		registerRDFLiteralIcon(IconResources.getIcon(IconResources.STRING_ICON_FILENAME));
	}

	/**Retrieves the key used to lookup data, such as icons, specific for this
		user object.
		<p>If the user object is an RDF resource, its type will be returned.
		If the resource has multiple types it is undefined which type will be
		returned.</p>
		<p>If the user object is not an RDF resource, its class will be returned.</p>
	@param userObject The user object for which a key should be returned.
	@return The key for looking up data for the user object, or <code>null</code>
		if no key could be determined for the user object or if the user object
		is <code>null</code>.
	*/
	protected Object getUserObjectKey(final Object userObject)
	{
		if(userObject instanceof RDFResource) //if this is an RDF resource
		{
//TODO del			final RDFResource rdfResource=(RDFResource)userObject;  //cast the user object to an RDF resource
			return RDFResources.getType((RDFResource)userObject); //return the resource's type
		}
		else if(userObject instanceof RDFLiteral)  //if this is an RDF literal
		{
			return RDFLiteral.class; //return the Literal class as the lookup object
		}
		else  //if this is not an RDF resource or literal
			return super.getUserObjectKey(userObject);  //get the default key
	}
}