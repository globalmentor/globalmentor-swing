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

package com.globalmentor.swing;

import static java.nio.charset.StandardCharsets.*;

import java.io.*;
import java.net.URI;

import javax.swing.*;
import javax.swing.text.*;

import com.globalmentor.io.*;
import com.globalmentor.model.ModelView;
import com.globalmentor.model.ModelViewIOKit;
import com.globalmentor.model.Modifiable;
import com.globalmentor.model.Verifiable;
import com.globalmentor.xml.XMLDocumentModelIOKit;
import com.globalmentor.xml.XMLNodeModel;

/**
 * An implementation for loading information into a view or saving information from an XML panel using a model's I/O kit.
 * <p>
 * This implementation knows how to switch to the XML panel's source view if there is an error loading the XML data.
 * @author Garret Wilson
 * @see ModelViewIOKit
 */
public class XMLDocumentModelViewIOKit extends ModelViewIOKit<XMLNodeModel<org.w3c.dom.Document>> {

	/**
	 * URI input stream locator constructor.
	 * @param uriInputStreamable The implementation to use for accessing a URI for input, or <code>null</code> if the default implementation should be used.
	 */
	public XMLDocumentModelViewIOKit(final URIInputStreamable uriInputStreamable) {
		this(uriInputStreamable, null);
	}

	/**
	 * URI output stream locator constructor.
	 * @param uriOutputStreamable The implementation to use for accessing a URI for output, or <code>null</code> if the default implementation should be used.
	 */
	public XMLDocumentModelViewIOKit(final URIOutputStreamable uriOutputStreamable) {
		this(null, uriOutputStreamable);
	}

	/**
	 * Full constructor.
	 * @param uriInputStreamable The implementation to use for accessing a URI for input, or <code>null</code> if the default implementation should be used.
	 * @param uriOutputStreamable The implementation to use for accessing a URI for output, or <code>null</code> if the default implementation should be used.
	 */
	public XMLDocumentModelViewIOKit(final URIInputStreamable uriInputStreamable, final URIOutputStreamable uriOutputStreamable) {
		super(new XMLDocumentModelIOKit(uriInputStreamable, uriOutputStreamable)); //construct the parent class with an XML document I/O kit
	}

	/**
	 * Loads data into a view from an input stream.
	 * @param view The view into which the data should be loaded.
	 * @param inputStream The input stream from which to read the data.
	 * @param baseURI The base URI of the content, or <code>null</code> if no base URI is available.
	 * @throws IOException Thrown if there is an error reading the data.
	 */
	public void load(final ModelView<XMLNodeModel<org.w3c.dom.Document>> view, final InputStream inputStream, final URI baseURI) throws IOException {
		try {
			super.load(view, inputStream, baseURI); //try to load the model normally
		} catch(IOException ioException) { //if there was an error parsing the XML, show the source
			final XMLDocumentPanel xmlPanel = (XMLDocumentPanel)view; //cast the view to an XML panel
			xmlPanel.setModel(new XMLNodeModel<org.w3c.dom.Document>()); //clear all data from the panel
			xmlPanel.setModelView(XMLPanel.SOURCE_MODEL_VIEW); //switch to the source and try to load the source with no interpretation					
			xmlPanel.setModel(new XMLNodeModel<org.w3c.dom.Document>()); //clear all data from the panel again, because changing the data view might have created default XML
			final JTextPane sourceTextPane = xmlPanel.getSourceTextPane(); //get a reference to the source text pane TODO maybe make an accessor method for setting the source
			final Document document = sourceTextPane.getDocument(); //get the source document
			inputStream.reset(); //start back at the beginning of the stream
			//TODO del when works			final StringBuffer autodetectPrereadCharacters=new StringBuffer();	//this will receive whatever characters were read while prereading the encoding TODO it would be better to update the XML processor code to push these characters back automatically, as the InputStreamUtilities.getBOMEncoding() method does
			//see if we can determine the XML encoding before we we parse the stream
			//TODO del when works			final CharacterEncoding encoding=XMLProcessor.getXMLEncoding(inputStream, new StringBuffer(), autodetectPrereadCharacters);
			//use the character encoding we sensed to create a reader
			//TODO del when works			final Reader reader=new InputStreamReader(inputStream, encoding.toString());
			try {
				sourceTextPane.getEditorKit().read(inputStream, document, 0); //have the editor kit read the document from the reader
				//TODO del when works				sourceTextPane.getDocument().insertString(0, autodetectPrereadCharacters.toString(), null);	//insert the preread characters at the front of the document TODO check
			} catch(BadLocationException badLocationException) {
				throw (AssertionError)new AssertionError(badLocationException.getMessage()).initCause(badLocationException); //this should never occcur
			}
			sourceTextPane.setCaretPosition(0); //move the caret to the beginning of the document
			xmlPanel.setModified(false); //show that the panel is not modified
			AbstractSwingApplication.displayApplicationError(xmlPanel, ioException); //show the error that caused us to load the source
		}
	}

	/**
	 * Saves a view to an output stream.
	 * <p>
	 * If saving is successful and the view is <code>Modifiable</code>, the view's modified status is set to <code>false</code>.
	 * </p>
	 * <p>
	 * A calling program should first call the view's <code>verify()</code> method to ensure the data is valid and that the model reflects the currently entered
	 * data.
	 * </p>
	 * @param view The view the data of which will be written to the given output stream.
	 * @param outputStream The output stream to which to write the model content.
	 * @throws IOException Thrown if there is an error writing the data.
	 * @see Modifiable
	 * @see Verifiable#verify()
	 */
	public void save(final ModelView view, final OutputStream outputStream) throws IOException {
		final XMLPanel xmlPanel = (XMLPanel)view; //cast the view to an XML panel
		switch(xmlPanel.getModelView()) { //see which view is being shown
			case XMLPanel.SOURCE_MODEL_VIEW: //if the source is being edited
			{
				final Writer writer = new OutputStreamWriter(outputStream, UTF_8); //create a UTF-8 writer
				final JTextPane textPane = xmlPanel.getSourceTextPane(); //get a reference to the source text pane
				final Document document = textPane.getDocument(); //get the document currently loaded into the text pane
				try {
					textPane.getEditorKit().write(writer, document, 0, document.getLength()); //have the editor kit write the document to the writer
				} catch(BadLocationException badLocationException) {
					throw (AssertionError)new AssertionError(badLocationException.getMessage()).initCause(badLocationException); //this should never occcur
				}
				xmlPanel.setModified(false); //show that the information has not been modified, as we just saved it
			}
				break;
			case XMLPanel.WYSIWYG_MODEL_VIEW: //if the XML WYSIWYG view is being shown
			default: //if any other view is being shown
				super.save(view, outputStream); //save the model the default way
				break;
		}
	}

}
