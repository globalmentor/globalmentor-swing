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

package com.globalmentor.swing.text.xml;

import java.io.*;
import java.net.*;
import java.security.*;

import com.globalmentor.io.Files;
import com.globalmentor.io.InputStreams;
import com.globalmentor.java.Classes;

import static com.globalmentor.java.Java.*;
import static com.globalmentor.net.URIs.*;

/**
 * Class loader for retrieving Java classes needed in an XML document. This class loader can be used to retrieve not only classes, but other types of resources
 * as well.
 * <p>
 * This class received inspiration from the <code>JavaRunnerLoader</code> class by Scott Oaks in the book, <cite>Java Security</cite>, Cambridge: O'Reilly,
 * 1998. ISBN 1-56592-403-7.
 * </p>
 * <p>
 * Also referenced was {@link URLClassLoader} version 1.70, 03/09/00 by David Connelly.
 * </p>
 * @author Garret Wilson
 */
public class XMLClassLoader extends SecureClassLoader {

	/** The XML document to use to retrieve class bytes. */
	protected XMLDocument xmlDocument;

	/*The context to be used when loading classes and resources.*/
	private AccessControlContext accessControlContext;

	/**
	 * The base URI to use when loading classes, or <code>null</code> if no base URI should be used.
	 */
	protected final URI baseURI;

	/**
	 * Constructor that takes a Swing XML document which will be used for retrieving class bytes.
	 * @param document The Swing XML document to use in loading classes.
	 * @param parent The parent class loader which will be checked to attempt to load a particular class.
	 */
	public XMLClassLoader(final XMLDocument document, final ClassLoader parent) {
		this(document, parent, null); //create an XML class loader without a base URI
	}

	/**
	 * Constructor that takes a Swing XML document which will be used for retrieving class bytes. Accepts a designated base URI to which all classes will be
	 * considered relative.
	 * @param document The Swing XML document to use in loading classes.
	 * @param parent The parent class loader which will be checked to attempt to load a particular class.
	 * @param newBaseURI The base URI to which the classes will be considered relative.
	 */
	public XMLClassLoader(final XMLDocument document, final ClassLoader parent, final URI newBaseURI) {
		//TODO perhaps use the document's parent instead of passing a parent
		super(parent); //create the parent class
		xmlDocument = document; //store a reference to the XML document
		accessControlContext = AccessController.getContext(); //get the current access controller context TODO check
		baseURI = newBaseURI; //store the base URI
	}

	/**
	 * Finds and loads the class with the specified name from the XML document. //TODO fix: using the URI search path. Any URIs referring to JAR files are loaded
	 * and opened as needed until the class is found.
	 * @param name The name of the class (not the class file).
	 * @return The resulting class.
	 * @throws ClassNotFoundException Thrown if the class could not be found.
	 */
	protected Class findClass(final String name) throws ClassNotFoundException {
		try {
			//replace '.' with '/' and append ".class"
			String href = name.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR).concat(String.valueOf(Files.FILENAME_EXTENSION_SEPARATOR))
					.concat(Classes.CLASS_NAME_EXTENSION);
			if(baseURI != null) //if we have a base URI
				href = createURI(baseURI, href).toString(); //create an href relative to the base URI
			final String finalHRef = href; //put the href in a variable we wont' modify
			//TODO del Log.trace("XMLClassLoader.findClass() name: "+name+" href: "+href); //TODO del
			return (Class)AccessController.doPrivileged(new PrivilegedExceptionAction() {

				public Object run() throws ClassNotFoundException {
					try {

						//TODO del							final URL classURL=xmlDocument.getRe
						//get an input stream to the class file, if we can
						final InputStream inputStream = xmlDocument.getResourceAsInputStream(finalHRef);
						if(inputStream != null) { //if we have an input stream TODO should we check for null?
							try {
								//read the class bytes from the input stream
								final byte[] classBytes = InputStreams.getBytes(inputStream);
								//TODO del Log.trace("XMLClassLoader read class bytes, byte count: "+classBytes.length); //TODO del
								//create a class from the bytes and return the class
								return defineClass(name, classBytes, 0, classBytes.length, (CodeSource)null); //TODO check about using a CodeSource
							} finally {
								inputStream.close(); //close the input stream
							}
						} else
							//if we didn't receive an input stream
							throw new ClassNotFoundException(name); //show that we can't find the specified class
					} catch(IOException ioException) { //if we have problems reading the class
						throw new ClassNotFoundException(name, ioException); //show that we can't load the class because of an I/O error
					} catch(URISyntaxException uriSyntaxException) {
						throw new ClassNotFoundException(uriSyntaxException.getMessage(), uriSyntaxException); //show that the class wasn't found
					}
				}
			}, accessControlContext);
		} catch(URISyntaxException uriSyntaxException) {
			throw new ClassNotFoundException(uriSyntaxException.getMessage(), uriSyntaxException); //show that the class wasn't found
		}
		/*TODO del if not needed
				catch(MalformedURLException malformedURLException)
				{
			    throw new ClassNotFoundException(malformedURLException.getMessage()); //show that the class wasn't found
				}
		*/
		catch(java.security.PrivilegedActionException pae) { //if we're not allowed to load this class
			throw new ClassNotFoundException(pae.getException().getMessage()); //show that the class wasn't found
		}
		//TODO fix		return super.findClass(name); //allow the parent class to attempt to find the class, throwing ClassNotFoundException if the class cannot be found
	}

}