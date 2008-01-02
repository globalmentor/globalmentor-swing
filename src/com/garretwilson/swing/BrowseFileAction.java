package com.garretwilson.swing;

import java.awt.Component;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import com.garretwilson.resources.icon.IconResources;
import com.globalmentor.java.*;

/**An action for browsing to choose a file.
<p>Bound properties:</p>
<dl>
	<dt><code>SELECTED_FILE_PROPERTY</code> (<code>File</code>)</dt>
	<dd>Indicates that the selected file property has been changed.</dd>
</dl>
@author Garret Wilson
*/
public class BrowseFileAction extends AbstractAction
{

	/**The name of the file property.*/
	public final static String SELECTED_FILE_PROPERTY=BrowseFileAction.class.getName()+Java.PACKAGE_SEPARATOR+"file";

	/**The selected file, or <code>null</code> if no file is selected.*/
	private File selectedFile=null;

		/**@return The selected file, or <code>null</code> if no file is selected.*/
		public File getSelectedFile() {return selectedFile;}

		/**Sets the selected file.
		@param newFile The new selected file, or <code>null</code> if no file
			should be selected.
		*/
		public void setSelectedFile(final File newFile)
		{
			final File oldFile=getSelectedFile();	//get the current value
			if(!Objects.equals(oldFile, newFile))	//if the files are different
			{
				selectedFile=newFile;	//change the file
				firePropertyChange(SELECTED_FILE_PROPERTY, oldFile, newFile);	//show that the value changed
			}
		}

	/**The component to use as the parent of the file
		selection dialog, or <code>null</code> if there is no parent component.
	*/
	private final Component parentComponent;

		/**@return The component to use as the parent of the file
			selection dialog, or <code>null</code> if there is no parent component.
		*/
		protected Component getParentComponent() {return parentComponent;}

	/**The method for choosing files.*/
	private JFileChooser fileChooser;

		/**@return The method for choosing files.*/
		public JFileChooser getFileChooser() {return fileChooser;}

		/**Sets the method for choosing files.
		@param fileChooser The method for choosing files.
		*/
		public void setFileChooser() {this.fileChooser=fileChooser;}

	/**Default constructor with a default file chooser.*/
	public BrowseFileAction()
	{
		this((Component)null);	//construct the action with no parent component
	}

	/**Parent component constructor with a default file chooser.
	@param parentComponent The component to use as the parent of the file
		selection dialog, or <code>null</code> if there is no parent component.
	*/
	public BrowseFileAction(final Component parentComponent)
	{
		this(new JFileChooser(), parentComponent);	//create an action with a default file chooser
	}

	/**File chooser constructor.
	@param fileChooser The method for choosing files.
	*/
	public BrowseFileAction(final JFileChooser fileChooser)
	{
		this(fileChooser, null);	//construct the action with no parent component
	}

	/**File chooser and parent component constructor.
	@param fileChooser The method for choosing files.
	@param parentComponent The component to use as the parent of the file
		selection dialog, or <code>null</code> if there is no parent component.
	*/
	public BrowseFileAction(final JFileChooser fileChooser, final Component parentComponent)
	{
		super("Browse");	//create the base class G***i18n
		putValue(SHORT_DESCRIPTION, "Browse for a file");	//set the short description G***i18n
		putValue(LONG_DESCRIPTION, "Browse the file system to locate a file.");	//set the long description G***i18n
		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));  //set the mnemonic key G***i18n
		putValue(SMALL_ICON, IconResources.getIcon(IconResources.FOLDER_TREE_ICON_FILENAME)); //load the correct icon
		this.fileChooser=fileChooser;	//save teh file chooser
		this.parentComponent=parentComponent;	//save the parent component
	}

	/**Called when the action should be performed.
	@param actionEvent The event causing the action.
	*/
	public void actionPerformed(final ActionEvent actionEvent)
	{
		final File file=getSelectedFile();	//get the file as it appears in the text field
		fileChooser.setSelectedFile(file); //show the selected directory
			//show the file chooser dialog; if they approve of the new directory
		if(fileChooser.showDialog(parentComponent, null)==JFileChooser.APPROVE_OPTION)	//ask the user to select a file  
		{
			try	//try to use a canonical version of the file
			{ 
				setSelectedFile(fileChooser.getSelectedFile().getCanonicalFile());	//set the new selected file, using its canonical form
			}
			catch(IOException ioException)	//if there was an error retrieving the canonical format
			{
				setSelectedFile(fileChooser.getSelectedFile());	//just use the file as it is---this will get verified again before we finish, and since browsing is just an alternate to text entry, which they can modify later, it doesn't help to worry too much about ensuring quality here
			}		
		}
	}
}
