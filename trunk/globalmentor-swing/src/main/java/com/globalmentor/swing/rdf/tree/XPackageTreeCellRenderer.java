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

package com.globalmentor.swing.rdf.tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import com.globalmentor.log.Log;
import com.globalmentor.net.ContentType;
import com.globalmentor.rdf.*;
import com.globalmentor.swing.tree.IconTreeNode;

/**A tree cell renderer that can render different icons and strings for user
	objects that are XPackage RDF resources.
	<p>If the a tree node is an {@link IconTreeNode} that has an icon, its icon
	is used. If not, specified icons are used for the specified types of
	RDF resource user objects, using first the XPackage content type and then, if
	there is no content type, the RDF resource type. If the user object is not an
	RDF resource, the specified class of user object is used to find an icon.
	If a tree node is not a {@link DefaultMutableTreeNode}, the default
	icons are used from the parent class.</p>
@author Garret Wilson
*/
public class XPackageTreeCellRenderer extends RDFResourceTreeCellRenderer
{

		/**Registers an open icon to be used with a particular media type.
		@param contentType The media type for the icon.
		@param icon The icon to register with the media type, or <code>null</code>
			if no icon should be associated with the media type.
		*/
		public void registerOpenIcon(final ContentType contentType, final Icon icon)
		{
			registerOpenIcon((Object)contentType.getBaseType(), icon); //put the icon in the map, keyed to the base media type
		}

		/**Registers a closed icon to be used with a particular media type.
		@param contentType The media type for the icon.
		@param icon The icon to register with the media type, or <code>null</code>
			if no icon should be associated with the media type.
		*/
		public void registerClosedIcon(final ContentType contentType, final Icon icon)
		{
			registerClosedIcon((Object)contentType.getBaseType(), icon); //put the icon in the map, keyed to the base media type
		}

		/**Registers a leaf icon to be used with a particular media type.
		@param contentType The media type for the icon.
		@param icon The icon to register with the media type, or <code>null</code>
			if no icon should be associated with the media type.
		*/
		public void registerLeafIcon(final ContentType contentType, final Icon icon)
		{
			registerLeafIcon((Object)contentType.getBaseType(), icon); //put the icon in the map, keyed to the base media type
		}

	/**Default constructor.*/
	public XPackageTreeCellRenderer()
	{
		super();  //construct the parent object
	}

	/**Retrieves the key used to lookup data, such as icons, specific for this
		user object.
		<p>If the user object is an RDF resource, the base content type of the content type will be returned.
		If the resource has no content type, the RDF resource type will be returned.
		If the resource has multiple types it is undefined which type will be
		returned.</p>
		<p>If the user object is not an RDF resource, its class will be returned.</p>
	@param userObject The user object for which a key should be returned.
	@return The key for looking up data for the user object, or <code>null</code>
		if no key could be determined for the user object or if the user object
		is <code>null</code>.
	@see com.globalmentor.io.ContentTypes#getBaseContentType(ContentType)
	*/
	protected Object getUserObjectKey(final Object userObject)
	{
		if(userObject instanceof RDFResource) //if this is an RDF resource
		{
			final RDFResource rdfResource=(RDFResource)userObject;  //cast the object to an RDF resource
Log.trace("getting user object key for user object", RDFResources.toString(rdfResource));
/*TODO fix with URF
			final ContentType mediaType=Marmot.getMediaType(rdfResource);  //see if there is a media type for the resource
		  if(mediaType!=null) //if there is a media type
			{
				return mediaType.getBaseType(); //use the base content type of the media type as the key
			}
*/
		}
		return super.getUserObjectKey(userObject);  //if we can't find a media type, use the default key
	}
}