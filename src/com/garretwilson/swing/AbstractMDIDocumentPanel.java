package com.garretwilson.swing;

import java.awt.*;
import java.io.*;
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
	private File file=null;

	/**@return The file used by this document, or <code>null</code> if unknown.*/
	public File getFile() {return file;}

	/**Sets the file used by this document.
	@param newFile The file used, or <code>null</code> if unknown.
	*/
	public void setFile(final File newFile) {file=newFile;}

	private final RDF rdf;  //G***testing

	protected RDF getRDF() {return rdf;}  //G***testing; comment

	/**The resource that dscribes this document.*/
	private RDFResource resource;

	/**@return The resource that describes this document.*/
	public RDFResource getResource() {return resource;}

	/**Sets the reference URI of the document. This results in the resource
		returned by <code>getResource()</code> being changed to another object,
		invalidating the old resource description.
	@param referenceURI The new reference URI of the document.
	@exception IllegalArgumentException Thrown if the provided reference URI is
		<code>null</code>.
	*/
	public void setReferenceURI(final String referenceURI) throws IllegalArgumentException
	{
		resource=new DefaultRDFResource(resource, referenceURI); //create a new resource with a different URI G***change to get this from the data model
	}

	/**Default constructor.*/
	public AbstractMDIDocumentPanel()
	{
//G***del		super();  //construct the parent
		this(true); //do the default construction, initializing the panel
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
		super(hasToolBar, hasStatusBar, initialize);  //construct the parent
		rdf=new RDF();  //G***testing; comment
		resource=createDefaultResource(); //create the default resource used for describing the document
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
	protected RDFResource createDefaultResource()
	{
		return getRDF().createResource();  //create a default resource
	}

}
