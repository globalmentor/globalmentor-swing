package com.garretwilson.swing;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.*;
import javax.swing.*;
import com.garretwilson.text.xml.XMLSerializer;
import com.garretwilson.text.xml.qti.*;
import com.garretwilson.lang.BooleanUtilities;
import com.garretwilson.rdf.*;
import com.garretwilson.util.*;
import org.w3c.dom.*;

/**An application panel that contains a document for multiple document interface
	architecture.
@author Garret Wilson
*/
public abstract class AbstractMDIDocumentPanel extends ApplicationPanel implements RDFResourceState	//G***eventually delete this class and replace it with ResourceApplicationPanel
{
//TODO make this class implement com.garretwilson.io.Streamable as well (create that class)

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

	/**Whether the data has been modified; the default is not modified.*/
	private boolean modified=false;

		/**@return Whether the datahas been modified.*/
		public boolean isModified() {return modified;}

		/**Sets whether the data has been modified.
			This is a bound property.
		@param newModified The new modification status.
		*/
		public void setModified(final boolean newModified)
		{
			final boolean oldModified=modified; //get the old modified value
			if(oldModified!=newModified)  //if the value is really changing
			{
				modified=newModified; //update the value
					//show that the modified property has changed
				firePropertyChange(MODIFIED_PROPERTY_NAME, BooleanUtilities.toBoolean(oldModified), BooleanUtilities.toBoolean(newModified));
			}
		}

	/**The resource that dscribes this document.*/
	private RDFResource resource;

		/**@return The non-<code>null</code> resource that describes this document.*/
		public RDFResource getRDFResource() {return resource;}

		/**@return The non-<code>null</code> resource being described.*/
		public Resource getResource() {return getRDFResource();}

		/**@return The non-<code>null</code> object being described.*/
		public Object getObject() {return getRDFResource();}		

		/**Sets the resource that describes this document.
		@param resource The resource that describes this document.
		*/
		public void setRDFResource(final RDFResource resource) {this.resource=resource;}	//G***maybe throw an exception if the resource is null

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
//G***del		description=new DefaultDocumentDescription();	//create a new document description
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

	/**Loads the resource by reading the contents of the resource from the given
		input stream. Convenience implementation to read with no description.
	@param inputStream The source of the content.
	@exception IOException Thrown if there is an error reading the contents.
	*/
	public void read(final InputStream inputStream) throws IOException
	{
//G***fix		read(inputStream, null);	//read with no description
	}

}
