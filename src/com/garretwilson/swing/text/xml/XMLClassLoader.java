package com.garretwilson.swing.text.xml;

import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.security.PrivilegedExceptionAction;
import static com.garretwilson.net.URIConstants.*;
import com.garretwilson.net.URIs;
import com.globalmentor.io.InputStreams;

import static com.globalmentor.io.FileConstants.*;
import static com.globalmentor.java.Java.*;

/**Class loader for retrieving Java classes needed in an XML document. This
	class loader can be used to retrieve not only classes, but other types of
	resources as well.
	<p>This class received inspiration from the <code>JavaRunnerLoader</code> class
	by Scott Oaks in the book, <em>Java Security</em>, Cambridge: O'Reilly, 1998.
	ISBN 1-56592-403-7.</p>
	<p>Also referenced was <code>java.io.URLClassLoader</code> version 1.70,
	03/09/00 by David Connelly.</p>
@author Garret Wilson
*/
public class XMLClassLoader extends SecureClassLoader
{

	/**The XML document to use to retrieve class bytes.*/
	protected XMLDocument xmlDocument;

	/*The context to be used when loading classes and resources.*/
	private AccessControlContext accessControlContext;

	/**The base URI to use when loading classes, or <code>null</code> if no base
		URI should be used.
	*/
	protected final URI baseURI;

	/**Constructor that takes a Swing XML document which will be used for
		retrieving class bytes.
	@param document The Swing XML document to use in loading classes.
	@param parent The parent class loader which will be checked to attempt to load
		a particular class.
	*/
  public XMLClassLoader(final XMLDocument document, final ClassLoader parent)
  {
		this(document, parent, null); //create an XML class loader without a base URI
  }

	/**Constructor that takes a Swing XML document which will be used for
		retrieving class bytes. Accepts a designated base URI to which all classes
		will be considered relative.
	@param document The Swing XML document to use in loading classes.
	@param parent The parent class loader which will be checked to attempt to load
		a particular class.
	@param newBaseURI The base URI to which the classes will be considered relative.
	*/
  public XMLClassLoader(final XMLDocument document, final ClassLoader parent, final URI newBaseURI)
  {
//G***perhaps use the document's parent instead of passing a parent
		super(parent);  //create the parent class
		xmlDocument=document; //store a reference to the XML document
		accessControlContext=AccessController.getContext();  //get the current access controller context G***check
		baseURI=newBaseURI; //store the base URI
  }


	/**Finds and loads the class with the specified name from the XML document.
//G***fix: using the URI search path. Any URIs referring to JAR files are loaded and opened as needed until the class is found.
	@param name The name of the class (not the class file).
	@return The resulting class.
	@exception ClassNotFoundException Thrown if the class could not be found.
	*/
	protected Class findClass(final String name) throws ClassNotFoundException
	{
		try
		{
				//replace '.' with '/' and append ".class"
			String href=name.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR).concat(String.valueOf(EXTENSION_SEPARATOR)).concat(CLASS_EXTENSION);
			if(baseURI!=null) //if we have a base URI
				href=URIs.createURI(baseURI, href).toString(); //create an href relative to the base URI
			final String finalHRef=href;  //put the href in a variable we wont' modify
//G***del Debug.trace("XMLClassLoader.findClass() name: "+name+" href: "+href); //G***del
		  return (Class)AccessController.doPrivileged(new PrivilegedExceptionAction()
				  {
						public Object run() throws ClassNotFoundException
						{
							try
							{

//G***del							final URL classURL=xmlDocument.getRe
									//get an input stream to the class file, if we can
								final InputStream inputStream=xmlDocument.getResourceAsInputStream(finalHRef);
								if(inputStream!=null) //if we have an input stream G***should we check for null?
								{
									try
									{
											//read the class bytes from the input stream
										final byte[] classBytes=InputStreams.getBytes(inputStream);
//G***del Debug.trace("XMLClassLoader read class bytes, byte count: "+classBytes.length); //G***del
											//create a class from the bytes and return the class
										return defineClass(name, classBytes, 0, classBytes.length, (CodeSource)null); //G***check about using a CodeSource
									}
									finally
									{
										inputStream.close();  //close the input stream
									}
								}
								else  //if we didn't receive an input stream
									throw new ClassNotFoundException(name); //show that we can't find the specified class
							}
							catch (IOException ioException) //if we have problems reading the class
							{
								throw new ClassNotFoundException(name, ioException);  //show that we can't load the class because of an I/O error
							}
							catch(URISyntaxException uriSyntaxException)
							{
								throw new ClassNotFoundException(uriSyntaxException.getMessage(), uriSyntaxException); //show that the class wasn't found
							}
						}
				}, accessControlContext);
		}
		catch(URISyntaxException uriSyntaxException)
		{
			throw new ClassNotFoundException(uriSyntaxException.getMessage(), uriSyntaxException); //show that the class wasn't found
		}
/*G***del if not needed
		catch(MalformedURLException malformedURLException)
		{
	    throw new ClassNotFoundException(malformedURLException.getMessage()); //show that the class wasn't found
		}
*/
		catch (java.security.PrivilegedActionException pae) //if we're not allowed to load this class
		{
	    throw new ClassNotFoundException(pae.getException().getMessage()); //show that the class wasn't found
		}
//G***fix		return super.findClass(name); //allow the parent class to attempt to find the class, throwing ClassNotFoundException if the class cannot be found
	}

}