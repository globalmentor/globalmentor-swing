package com.garretwilson.swing;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import javax.swing.*;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.text.xml.qti.*;
import com.garretwilson.rdf.*;
import com.garretwilson.swing.*;
import org.w3c.dom.*;

/**An application panel that contains a document for multiple document interface
	architecture.
@author Garret Wilson
*/
public abstract class AbstractMDIDocumentPanel extends ApplicationPanel implements MDIDocument
{

	/**The document file.*/
//G***del when works	private File file=null;

	/**@return The file used by this document, or <code>null</code> if unknown.*/
//G***del when works	public File getFile() {return file;}

	/**Sets the file used by this document.
	@param newFile The file used, or <code>null</code> if unknown.
	*/
//G***del when works	public void setFile(final File newFile) {file=newFile;}

//G***del when works	private final RDF rdf;  //G***testing

//G***del when works	protected RDF getRDF() {return rdf;}  //G***testing; comment

	/**The resource that dscribes this document.*/
//G***del when works	private RDFResource resource;

	/**@return The resource that describes this document.*/
//G***del when works	public RDFResource getResource() {return resource;}

	/**Sets the reference URI of the document. This results in the resource
		returned by <code>getResource()</code> being changed to another object,
		invalidating the old resource description.
	@param referenceURI The new reference URI of the document.
	@exception IllegalArgumentException Thrown if the provided reference URI is
		<code>null</code>.
	*/
/*G***del when works
	public void setReferenceURI(final URI referenceURI) throws IllegalArgumentException
	{
		resource=new DefaultRDFResource(resource, referenceURI); //create a new resource with a different URI G***change to get this from the data model
	}
*/



	/**The description of the document.*/
	private final DocumentDescribable description;

		//Comparable methods
		
	/**Compares this object to another object.
		This method determines order based upon the reference URI of the resource.
	@param object The object with which to compare the component. This must be
		another <code>Resource</code> object.
	@return A negative integer, zero, or a positive integer as this resource
		reference URI is less than, equal to, or greater than the reference URI of
		the specified resource, respectively.
	@exception ClassCastException Thrown if the specified object's type is not
		a <code>Resource</code>.
	@see #getReferenceURI
	*/
	public int compareTo(Object object) throws ClassCastException {return description.compareTo(object);}

		//RDFResource methods
		
	/**@return The resource identifier URI.*/
	public URI getReferenceURI() {return description.getReferenceURI();}

	/**@return The XML namespace URI used in serialization, or <code>null</code>
		if no namespace URI was used or there was no namespace.*/
	public URI getNamespaceURI() {return description.getNamespaceURI();}

	/**@return The XML local name used in serialization, or <code>null</code>
		if no namespace URI and local name was used.*/
	public String getLocalName() {return description.getLocalName();}

	/**@return The number of properties this resource has.*/
	public int getPropertyCount() {return description.getPropertyCount();}

	/**@return An iterator that allows traversal of all properties, each of which
		is a <code>NameValuePair</code>, with the name being the property predicate
		and the value being the property value.
	*/
	public ListIterator getPropertyIterator() {return description.getPropertyIterator();}

	/**Searches and returns the first occurring property value that appears as
		an RDF statement object with a predicate of <code>propertyResource</code>.
	@param propertyResource The property resource.
	@return The value of the property, either a <code>RDFResource</code> or a
		<code>Literal</code>, or <code>null</code> if this resource has no such
		property.
	*/
	public RDFObject getPropertyValue(final RDFResource propertyResource) {return description.getPropertyValue(propertyResource);}

	/**Searches and returns the first occurring property value that appears as
		an RDF statement object with a predicate of <code>propertyURI</code>.
	@param propertyURI The reference URI of the property resource.
	@return The value of the property, either a <code>RDFResource</code> or a
		<code>Literal</code>, or <code>null</code> if this resource has no such
		property.
	*/
	public RDFObject getPropertyValue(final URI propertyURI) {return description.getPropertyValue(propertyURI);}

	/**Searches and returns the first occurring property value that appears as
		an RDF statement object with a predicate of a property URI formed by the
		given namespace URI and local name. This is a convenience function that
		creates a property URI from an XML qualified name automatically for searching.
	@param namespaceURI The XML namespace URI used in the serialization.
	@param localName The XML local name used in the serialization.
	@return The value of the property, either a <code>RDFResource</code> or a
		<code>Literal</code>, or <code>null</code> if this resource has no such
		property.
	*/
	public RDFObject getPropertyValue(final URI namespaceURI, final String localName) {return description.getPropertyValue(namespaceURI, localName);}

	/**Determines if the resource has the given property with the given value.
		Each matching property is compared to the property value using the
		property's <code>equal()</code> method.
	@param propertyURI The reference URI of the property resource.
	@param propertyValue The object to which the property should be compared,
		such a resource reference URI, a resource, or a literal.
	@return <code>true</code> if the specified property is set to the specified
		value.
	*/
	public boolean hasPropertyValue(final URI propertyURI, final Object propertyValue) {return description.hasPropertyValue(propertyURI, propertyValue);}

	/**Determines if the resource has the given property with the given value.
		Each matching property is compared to the property value using the
		property's <code>equal()</code> method. This is a convenience function that
		creates a property URI from an XML qualified name automatically for searching.
	@param namespaceURI The XML namespace URI that represents part of the reference URI.
	@param localName The XML local name that represents part of the reference URI.
	@param propertyValue The object to which the property should be compared,
		such a resource reference URI, a resource, or a literal.
	@return <code>true</code> if the specified property is set to the specified
		value.
	*/
	public boolean hasPropertyValue(final URI namespaceURI, final String localName, final Object propertyValue) {return description.hasPropertyValue(namespaceURI, localName, propertyValue);}

	/**Adds a property by creating a <code>NameValuePair</code> from the given
		property and value. For each property, this resource serves as the subject
		of an RDF statement with the property as the predicate and the value as
		the object.
		<p>Note that the property is not simply a property URI &mdash; it is a
		resource that is identified by the property URI.</p>
		<p>If an equivalent property already exists, no action is taken.</p>
	@param property A property resource; the predicate of an RDF statement.
	@param value A property value; the object of an RDF statement.
	@return The added property value.
	*/
	public RDFObject addProperty(final RDFResource property, final RDFObject value) {return description.addProperty(property, value);}

	/**Adds a literal property from a string by creating a <code>NameValuePair</code>
		from the given property and value. For each property, this resource serves
		as the subject of an RDF statement with the property as the predicate and
		the value, stored as a literal, as the object.
		<p>Note that the property is not simply a property URI &mdash; it is a
		resource that is identified by the property URI.</p>
		<p>If an equivalent property already exists, no action is taken.</p>
	@param property A property resource; the predicate of an RDF statement.
	@param literalValue A literal property value that will be stored in a
		<code>Literal</code>; the object of an RDF statement.
	@return The added property value.
	*/
	public Literal addProperty(final RDFResource property, final String literalValue) {return description.addProperty(property, literalValue);}

	/**@return <code>true</code> if this resource is an anonymous resource;
		currently anonymous resources are those that either have no reference URI
		or that have a reference URI that begins with "anonymous:".
	*/
	public boolean isAnonymous() {return description.isAnonymous();}

		//Modifiable methods

	/**@return Whether the object has been modified.*/
	public boolean isModified() {return description.isModified();}
	
	/**Sets whether the object has been modified.
	@param newModified The new modification status.
	*/
	public void setModified(final boolean newModified) {description.setModified(newModified);}

		//DocumentDescribable methods

	/**Sets the reference URI of the resource.
	@param uri The new reference URI.
	*/
	public void setReferenceURI(final URI uri) {description.setReferenceURI(uri);}
	

	/**Default constructor.*/
	public AbstractMDIDocumentPanel()
	{
//G***del		super();  //construct the parent
		this(true); //do the default construction
	}

	/**Constructor with optional initialization.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	protected AbstractMDIDocumentPanel(final boolean initialize)
	{
//G***del		super();  //construct the parent
		this(false, false, initialize); //do the default construction, initializing the panel if needed
	}

	/**Application component constructor with optional initialization.
	@param applicationComponent The new component for the center of the panel.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	protected AbstractMDIDocumentPanel(final Component applicationComponent, final boolean initialize)
	{
//G***del		super(applicationComponent);  //construct the parent
		this(true, true, false);  //do the default construction without initializing
		setContentComponent(applicationComponent); //set the application component
		if(initialize)  //if we should initialize
		  initialize(); //initialize the panel
	}


	/**Application component constructor.
	@param applicationComponent The new component for the center of the panel.
	*/
	public AbstractMDIDocumentPanel(final Component applicationComponent)
	{
//G***del		super(applicationComponent);  //construct the parent
		this(applicationComponent, true); //construct and initialize the panel
	}

	/**Constructor that allows options to be set, such as the presence of a status
		bar.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	*/
	public AbstractMDIDocumentPanel(final boolean hasToolBar, final boolean hasStatusBar)
	{
		this(hasToolBar, hasStatusBar, true);  //do the default construction
	}

	/**Constructor that allows options to be set, such as the presence of a status
		bar.
	@param hasToolBar Whether this panel should have a toolbar.
	@param hasStatusBar Whether this panel should have a status bar.
	@param initialize <code>true</code> if the panel should initialize itself by
		calling the initialization methods.
	*/
	protected AbstractMDIDocumentPanel(final boolean hasToolBar, final boolean hasStatusBar, final boolean initialize)
	{
		super(hasToolBar, hasStatusBar, false);  //construct the parent, but don't initialize it, yet
/*G***del when works		
		rdf=new RDF();  //G***testing; comment
		resource=createDefaultResource(); //create the default resource used for describing the document
*/
		description=new DefaultDocumentDescription();	//create a new document description
		if(initialize)  //if we should initialize
		  initialize(); //initialize the panel
	}

	/**Creates any application objects and initializes data.
		Any class that overrides this method must call this version.
	*/
/*G***del
	protected void initializeData()
	{
		super.initializeData(); //do the default data initialization
		rdf=new RDF();  //G***testing; comment
		resource=createDefaultResource(); //create the default resource used for describing the document
	}
*/

	/**Creates the default resource used for describing the document.
	@return A resource for describing the document.
	*/
/*G***del when works	
	protected RDFResource createDefaultResource()
	{
		return getRDF().createResource();  //create a default resource
	}
*/

}
