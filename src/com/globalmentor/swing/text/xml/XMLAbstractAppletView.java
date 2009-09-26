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

import java.applet.*;
import java.awt.Component;
import java.awt.Container;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.text.*;
import com.globalmentor.java.Strings;
import com.globalmentor.log.Log;
import com.globalmentor.net.URIs;

/**A view that displays an applet. Implements {@link AppletStub} so
	that it can manage requests to the environment made by the applet.
	A class must be derived from this abstract class that correctly sets the
	class name, as well as correctly sets the correct width and height.
	Although not required, it is recommended that the derived clas override
	loadParameters() to correctly set the applet parameters, although it is
	conceivable that a particular implementation will not need parameters for the
	associated applet to function correctly.
@see #setClassName(String)
@see #setWidth(int)
@see #setWidth(int)
@author Garret Wilson
*/
public abstract class XMLAbstractAppletView extends XMLAbstractComponentView implements AppletStub
{

	/**Convenience function to return the component on which this view is based,
		automatically casting the component to an applet.
	@return The applet which this view represents.
	@see XMLComponentView#getComponent
	*/
	public Applet getApplet() {return (Applet)getComponent();}

	/**The href of the class to load, including any ending ".class" extension,
		relative to the document base URI.
	*/
	private String classHRef=null;

		/**@return The href of the class to load, including any ending ".class"
		  extension, relative to the document base URI.
		*/
		public String getClassHRef() {return className;}

		/**Sets the path of the Java applet class to load. This path will be used
			both to determine the applet codebase and to load the applet.
		  This method does nothing if the Java class has already been created.
		@param newClassHRef The href of the class to load, including any ending
			".class" extension, relative to the document base URI.
		@see #setClassName
		*/
		protected void setClassHRef(final String newClassHRef)
		{
		  classHRef=newClassHRef; //store the class href
			final String classPostfix=".class"; //the ending postfix the classid URI may have TODO use a constant here
		  final String className=Strings.trimEnd(classHRef, classPostfix);  //remove the ".class" postfix if present
		  setClassName(className);  //set the class name we constructed
		}

	/**The name of the class to load.*/
	private String className=null;

		/**@return The name of the class to load.*/
		public String getClassName() {return className;}

		/**Sets the name of the Java applet class to load. This method does nothing
		  if the Java class has already been created.
		@param newClassName The name of the Java applet class to load.
		@see #getApplet
		*/
		private void setClassName(final String newClassName)
		{
			if(getApplet()==null) //if we haven't created the associated applet yet
				className=newClassName; //update the class name
		}

	/**Whether parameters have been loaded.*/
	private boolean parametersLoaded=false;

		/**@return Whether parameters have been loaded.*/
		protected boolean isParametersLoaded() {return parametersLoaded;}

	/**The map of parameter objects.*/
	private Map parameterMap=new HashMap();

		/**Gets a parameter object with the specified name.
		@param name The name of the parameter to return.
		@return The parameter with the specified name, or <code>null</code> if that
			parameter does not exist.
		*/
		protected Parameter getParameterObject(final String name)
		{
		  return (Parameter)parameterMap.get(name);  //return this parameter
		}

		/**Sets a parameter by its name. If a parameter with that name already
		  exists, it will be replaced.
		@param parameter The parameter to be set.
		*/
		protected void setParameter(final Parameter parameter)
		{
			parameterMap.put(parameter.getName(), parameter); //set the parameter, keyed by name
		}

	/**Indicates whether the applet is active -- from applet <code>start()</code>
		to <code>stop()</code>.
	*/
	private boolean active=false;

	/**Creates a new view that represents an applet.
	@param element The element for which to create the view.
	*/
  public XMLAbstractAppletView(final Element element)
	{
   	super(element);	//do the default constructing
	}

	/**Starts the applet, if the applet is not yet started.
	@see #isActive
	*/
	protected synchronized void start() //TODO maybe change this to setActive()
	{
		if(!isActive()) //if the applet isn't started
		{
			final Applet applet=getApplet();  //get our associated applet
			if(applet!=null) //if we have an applet
			{
				active=true;  //show that the applet is active
/*TODO del
final Dimension d3=fApplet.getSize();  //TODO del; testing
Log.trace("Applet size before start() width: "+d3.getWidth()+" height: "+d3.getHeight());
*/
//TODO del System.out.println("Applet size before start() width: "+applet.getSize().getWidth()+" height: "+applet.getSize().getHeight());  //TODO del
				applet.start(); //inform the applet that we're starting it
			}
		}
	}

	/**Stops the applet, if the applet has been started.
	@see #isActive
	*/
	protected synchronized void stop()
	{
		if(isActive()) //if the applet is started
		{
			final Applet applet=getApplet();  //get our associated applet
			if(applet!=null) //if we have an applet
			{
				active=false;  //show that the applet is not active anymore
				applet.stop(); //inform the applet that we're stopping it
			}
		}
	}

	/**Called by the garbage collector when this class is no longer used.
		This version stops the applet, if present, and calls the applet's
		<code>destroy()</code> method. This method, as with any
		<code>finalize()</code> method in Java, is not guaranteed to ever be called.
	@throws Throwable Any <code>Exception</code> thrown by the method. This is
		ignored by the garbage collector.
	@see #stop
	@see Applet#stop
	@see Applet#destroy
	*/
	protected void finalize() throws Throwable
	{
		try
		{
			stop(); //stop the applet, if we have one
			final Applet applet=getApplet();  //get our applet
			if(applet!=null) //if we have an applet
			{
				applet.destroy();  //tell the applet it's being destroyed
			}
		}
		finally
		{
			super.finalize();	//always call the parent version
		}
	}

	/**Called when the view is being hidden by a parent that hides views, such
		as a paged view. This implementation includes the parent funtionality, as
		well as starting or stopping the associated applet as appropriate.
	@param showing <code>true</code> if the view is beginning to be shown,
		<code>false</code> if the view is beginning to be hidden.
	@see #getApplet
	*/
	public void setShowing(final boolean showing)
	{
		super.setShowing(showing);  //update showing in the parent class to actually show or hide the parent class
		if(showing) //if we should be showing the applet
			start(); //start the applet if needed TODO probably change to setActive()
		else  //if we shouldn't be showing the applet
			stop(); //stop the applet if needed TODO probably change to setActive()
	}

	/**Sets the parent of the view.
		This is reimplemented to provide the superclass behavior as well as loading
		whatever parameter child elements (if any) the applet has. Once parameters
		have been set, future calls to this method will not cause the parameters to
		be reloaded.
	@param parent The parent of the view, <code>null</code> if none.
	@see #loadParameters
	*/
	public void setParent(View parent)
	{
		if((parent!=null) && !isParametersLoaded()) //if we've been given a parent, and we haven't loaded the parameters, yet
		  loadParameters(); //load parameters; do this *before* we let the super class set the parent, because that will create an applet which might try to get the parameters TODO we might even want to load the parameters upon creation of the view
		super.setParent(parent);  //let the super class set the parent
	}

	/**Unconditionally loads all parameters from child elements. Called by the
		<code>setParent()</code> method.
	@see #setParent
	*/
	protected abstract void loadParameters();

	/**Creates an applet component for displaying according to the classname that
		has been set.
	@return An applet meant for displaying, or if an error occurs loading the
		applet, a component indicatin the error.
///TODO fix	@exception ClassCastException Thrown if the class returned by
	@see #getClass
	@see #getClassName
	*/
	protected Component createComponent()//TODO fix throws ClassCastException
	{
		try
		{
		  final Class appletClass=getClass(getClassName());  //get the applet class name, and load that class
		  final Object object=appletClass.newInstance();  //create a new instance of the applet class
		  assert object instanceof Applet : object.getClass().getName()+" is not an applet.";  //TODO fix
		  final Applet applet=(Applet)object; //cast the object to an applet
		  applet.setStub(this); //tell the applet that it can use this view as the applet stub
//TODO del Log.trace("applet setting size: currentWidth: "+currentWidth+" currentHeight: "+currentHeight);
		  applet.setSize(getCurrentWidth(), getCurrentHeight());  //set the applet's current width and height to the best we know so far TODO testing; this is probably not correct -- who know what the size will be at this point
//TODO del System.out.println("current bounds when creating applet: "+getBounds()); //TODO del
//TODO del		  applet.setBounds(getBounds());  //TODO testing
//TODO del		  applet.doLayout(); //tell the applet to lay itself out
/*TODO del
final Dimension d=fApplet.getSize();  //TODO del; testing
Log.trace("Applet size after setting size width: "+d.getWidth()+" height: "+d.getHeight());
Log.trace("applet: initializing");
*/
//TODO del System.out.println("applet size after applet.setSize(): "+applet.getSize().getWidth()+" height: "+applet.getSize().getHeight());  //TODO del
//TODO del		  applet.validate(); //tell the component to validate itself, laying out its child components if needed TODO testing
//TODO del			applet.doLayout();  //TODO testing
		  try
		  {
//TODO del System.out.println("applet size before applet.init(): "+applet.getSize().getWidth()+" height: "+applet.getSize().getHeight());  //TODO del
				applet.init();  //initialize the applet
//TODO del System.out.println("applet size after applet.init(): "+applet.getSize().getWidth()+" height: "+applet.getSize().getHeight());  //TODO del
				applet.doLayout();  //layout the applet after initialization
		  }
			catch(Throwable throwable)  //if any errors are thrown during applet initialization
			{
Log.error(throwable);		  //TODO fix; store errors in console of some sort
			}
/*TODO del
final Dimension d2=fApplet.getSize();  //TODO del; testing
Log.trace("Applet size after init() width: "+d2.getWidth()+" height: "+d2.getHeight());
*/
//TODO del		  applet.setVisible(false); //don't show
		  return applet; //return the applet we created
		}
		catch (ClassNotFoundException e)  //if the applet class could not be loaded
		{
Log.error(e);		  //TODO fix; store errors in console of some sort, as well as in text for the JTextComponent
	  }
	  catch(IllegalAccessException e) //if we're not allowed to load the applet class
		{
Log.error(e);		  //TODO fix; store errors in console of some sort, as well as in text for the JTextComponent
		}
		catch(InstantiationException e) //if we couldn't create the applet
		{
Log.error(e);		  //TODO fix; store errors in console of some sort, as well as in text for the JTextComponent
		}
//TODO fix	return getUnloadableRepresentation();
		return new JLabel("Missing applet");  //TODO fix
	}

	/**Get a <code>Class</code> object to use for loading the applet.
	@param className The name of the class to load.
	@return The <code>Class</code> object of the applet to be instantiated.
	@exception ClassNotFoundException Thrown if the given class cannot be found.
	@see XMLClassLoader
	@see #getDocument
	*/
	protected Class getClass(final String className) throws ClassNotFoundException
	{
		final URI baseURI=XMLStyles.getBaseURI(getAttributes());  //get the defined base URI, if any
		//create a class loader to load the class from our document, with our document's class loaders as a parent class loader
		final XMLClassLoader xmlClassLoader=new XMLClassLoader((XMLDocument)getDocument(), getDocument().getClass().getClassLoader(), baseURI);
		return xmlClassLoader.loadClass(className);  //ask the class loader to load the class
	}

	//AppletStub methods

	/**Determines if the applet is active. An applet is active just before its
		<code>start</code> method is called. It becomes inactive just before its
		<code>stop</code> method is called.
	@return <code>true</code> if the applet is active; <code>false</code> otherwise.
	*/
	public boolean isActive() {return active;}

	/**Returns an absolute URL naming the directory of the document in which
		the applet is embedded. This implementation returns the URL of the XML
		document.
	@return The URL of the document that contains this applet.
	@see java.applet.AppletStub#getCodeBase()
	*/
	public URL getDocumentBase()
	{
		try
		{ 
			return XMLStyles.getBaseURI(getAttributes()).toURL();  //get the defined base URL, if any
		}
		catch(MalformedURLException malformedURLException)  //if the resulting URL is malformed
		{
			Log.warn(malformedURLException);  //TODO fix to log to a Java console
			return null;  //show that we can't determine the codebase
		}
	}

	/**Gets the base URL.
	@return The URL of the applet.
	*/
	public URL getCodeBase()
	{
		try
		{
			final URI codebaseURI=URIs.createURI(XMLStyles.getBaseURI(getAttributes()), getClassHRef());	//the codebase is the URL of the class relative to the document base
			return codebaseURI.toURL();	//convert the URI to a URL			
		}
		catch(URISyntaxException uriSyntaxException)  //if the resulting URI is not syntactically correct
		{
			Log.warn(uriSyntaxException);  //TODO fix to log to a Java console
			return null;  //show that we can't determine the codebase
		}
		catch(MalformedURLException malformedURLException)  //if the resulting URL is malformed
		{
			Log.warn(malformedURLException);  //TODO fix to log to a Java console
			return null;  //show that we can't determine the codebase
		}
	}

	/**Returns the value of the named parameter in a <code>&lt;param&gt;</code>
		element.
	@param name A parameter name.
	@return The value of the named parameter, <code>null</code> if not set.
	*/
	public String getParameter(String name)
	{
/*TODO del
Log.trace("getParameter(): "+name);
Log.trace("Current size width: "+currentWidth+" height: "+currentHeight);
final Dimension d=fApplet.getSize();  //TODO del; testing
Log.trace("Applet size width: "+d.getWidth()+" height: "+d.getHeight());
*/
		final Parameter parameter=getParameterObject(name); //try to get the requested parameter object
//TODO del Log.trace("Found param: "+parameter); //TODO del
		return parameter!=null ? parameter.getValue() : null; //if we found a parameter, return the value, else return null
	}

	/**Gets a handler to the applet's context.
	@return The applet's context.
	*/
	public AppletContext getAppletContext()
	{
//TODO del Log.trace("getting applet context");  //TODO del
		final Container container=getContainer(); //get the container in which this view is embedded
		if(container instanceof AppletContext)  //if the container is an applet context (XMLTextView is an AppletContext, and that should be what we're embedded in)
		{
			return (AppletContext)container;  //return the container as an applet context
		}
		else  //if the container isn't an applet context
			return null;  //show that we don't know the applet context
	}

	/**Called when the applet wants to be resized.
	@param width The new requested width for the applet.
	@param height The new requested height for the applet.
	*/
	public void appletResize(int width, int height)
	{
//TODO del Log.trace("appletResize() width: "+width+" height: "+height);  //TODO del
		//TODO fix
	}

	/**Internal class to manage parameters for this applet.*/
	protected static class Parameter
	{
		/**The parameter name.*/
		private String name;

			/**@return The parameter name.*/
			public String getName() {return name;}

		/**The parameter value.*/
		private String value;

			/**@return The parameter value.*/
			public String getValue() {return value;}

		/**Creates a parameter with a paricular name and value.
		@param newName The new parameter name.
		@param newValue The new parameter value.
		*/
		public Parameter(final String newName, final String newValue)
		{
			name=newName; //set the name
			value=newValue; //set the value
		}

	}
}