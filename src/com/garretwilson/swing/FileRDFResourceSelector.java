package com.garretwilson.swing;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import com.garretwilson.io.*;
import com.garretwilson.model.*;
import com.garretwilson.rdf.RDFFileProcessor;

/**An implementation of a resource selector that selects resources from a
	file system and returns RDF resources as descriptions of selected files.
@author Garret Wilson
*/
public class FileRDFResourceSelector extends DefaultURIAccessible implements ResourceSelector
{

	/**The component to serve as a parent for file dialogs.*/
	private final Component parentComponent;

		/**@return The component to serve as a parent for file dialogs.*/
		protected Component getParentComponent() {return parentComponent;}

	/**Parent component constructor.
	@param parentComponent The component to serve as a parent for file dialogs.
	*/
	public FileRDFResourceSelector(final Component parentComponent)
	{
		super();	//call the default constructor, using the default implementation for accessing files
		this.parentComponent=parentComponent;	//save the parent component
	}

	/**Selects a resource for input.
	@param oldResource The currently selected resource, if applicable, or
		<code>null</code> if there is no selected resource.
	@return The selected resource, or <code>null</code> if selection was
		canceled.
	@exception IOException Thrown if there is an error locating a resource.
	*/
	public Resource selectInputResource(final Resource oldResource) throws IOException
	{
			//if we were given a resource with a valid URI, create a file from that URI and get the parent directory of that file
		final File currentDirectory=oldResource!=null && oldResource.getReferenceURI()!=null ? new File(oldResource.getReferenceURI()).getParentFile() : null;
		final JFileChooser fileChooser=new JFileChooser(currentDirectory);	//create a new dialog for listing files TODO set the current directory
//G***fix current directory				fileChooser.setCurrentDirectory(getReaderConfig().getFileLocations().getCurrentDirectory());	//change the file chooser directory to the reader's current directory
		final int option=fileChooser.showOpenDialog(getParentComponent());	//show the open dialog
		if(option==JFileChooser.APPROVE_OPTION)	//if they chose a file
		{
//TODO update current directlry					getReaderConfig().getFileLocations().setCurrentDirectory(fileChooser.getCurrentDirectory());	//save the new directory they changed to
			final File selectedFile=fileChooser.getSelectedFile();	//get the file they chose
			if(selectedFile!=null)	//if they chose a file
			{
				return new RDFFileProcessor().createResource(selectedFile);	//return a resource representing the selected file
			}
		}
		return null;	//show that we couldn't select a file	
		
	}

	/**Selects a resource for output.
	@param oldResource The currently selected resource, if applicable, or
		<code>null</code> if there is no selected resource.
	@return The selected resource, or <code>null</code> if selection was
		canceled.
	@exception IOException Thrown if there is an error locating a resource.
	*/
	public Resource selectOutputResource(final Resource oldResource) throws IOException
	{
	return null;	//TODO fix save dialog
	}

}
