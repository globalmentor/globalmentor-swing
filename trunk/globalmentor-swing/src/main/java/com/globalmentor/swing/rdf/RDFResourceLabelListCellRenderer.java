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

package com.globalmentor.swing.rdf;

import java.net.URI;

import com.globalmentor.rdf.RDFLiteral;
import com.globalmentor.rdf.RDFResource;
import com.globalmentor.rdf.RDFResources;
import com.globalmentor.rdf.dublincore.RDFDublinCore;
import com.globalmentor.rdf.rdfs.RDFS;
import com.globalmentor.swing.SimpleListCellRenderer;

/**
 * A list cell renderer which shows the resource label. The resource label is determined in the following order:
 * <ol>
 * <li>The <code>rdfs:label</code> property, if available.</li>
 * <li>The <code>dc:title</code> property, if available.</li>
 * <li>The resource reference URI, if available.</li>
 * <li>The <code>xpackage:location</code> <code>xlink:href</code>, if available.</li>
 * <li>The string value of the resource.</li>
 * </ol>
 * @author Garret Wilson
 */
public class RDFResourceLabelListCellRenderer extends SimpleListCellRenderer {

	/**
	 * Retrieves a label for a resource.
	 * @param value The value of this list item.
	 * @return The correct text for this list item.
	 * @throws ClassCastException if the value is not an RDF resource.
	 * @see RDFResource
	 */
	protected String getListCellRendererString(final Object value) {
		final RDFResource resource = (RDFResource)value; //cast the value to an RDF resource
		String label = null; //we'll try to find a label
		final RDFLiteral rdfsLabel = RDFS.getLabel(resource); //try to get an rdfs:label
		if(rdfsLabel != null) { //if there is an rdfs:label
			label = rdfsLabel.getLexicalForm(); //get the lexical form of the label
		} else { //if there is no rdfs:label
			final RDFLiteral dcTitle = RDFResources.asLiteral(RDFDublinCore.getTitle(resource)); //try to get a dc:title
			if(dcTitle != null) { //if there is a dc:title
				label = dcTitle.getLexicalForm(); //get the lexical form of the title
			} else { //if there is no dc:title
				final URI referenceURI = resource.getURI(); //try to get the reference URI
				if(referenceURI != null) { //if there is a reference URI
					label = referenceURI.toString(); //use the string form of the reference URI
				} else { //if there is no reference URI
					label = resource.toString(); //settle for the string version of the resource
				}
			}
		}
		return label; //return whatever label we found
	}
}
