package com.garretwilson.swing;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;
import com.garretwilson.io.*;
import com.garretwilson.model.Model;
import com.garretwilson.util.*;

import static com.garretwilson.io.ContentTypeConstants.*;

/**A panel to edit plain text.
@author Garret Wilson
*/
public class TextPanel extends ModelPanel
{

	private final JScrollPane scrollPane;
	private final JTextPane textPane;

	/**@return <code>true</code> if the text can be edited.*/
	public boolean isEditable() {return textPane.isEditable();}
	
	/**Sets whether or not the text can be edited.
	@param editable <code>true</code> if the text can be edited, or
		<code>false</code> if the information should be read-only.
	*/
	public void setEditable(final boolean editable)
	{
		textPane.setEditable(editable);	//show whether or not the text pane is editable
		textPane.setBackground(editable ? Color.WHITE : Color.LIGHT_GRAY);	//set the color to match
//G***del		textPane.setEnabled(editable);	//set the enabled status to match
	}

	/**@return The data model for which this component provides a view.
	@see ModelViewablePanel#getModel()
	*/
	public LocaleText getLocaleText() {return (LocaleText)getModel();}

	/**Sets the data model.
	@param model The data model for which this component provides a view.
	@see #setModel(Model)
	*/
	public void setLocaleText(final LocaleText model)
	{
		setModel(model);	//set the model
	}

	/**Sets the data model.
	@param newModel The data model for which this component provides a view.
	@exception ClassCastException Thrown if the model is not <code>Localetext</code>.
	*/
	public void setModel(final Model newModel)
	{
		super.setModel((LocaleText)newModel);	//set the model in the parent class
	}

	/**Default constructor.*/
	public TextPanel()
	{
		this(new LocaleText(""));	//construct the panel with no text
	}

	/**Text model constructor.
	@param model The model containing the text to display
	*/
	public TextPanel(final LocaleText model)
	{
		super(new BasicGridBagLayout(), model, false);  //create the panel with the model without initializing
		textPane=new JTextPane();	//create a new text pane
		scrollPane=new JScrollPane(textPane);	//create the text pane
		initialize(); //initialize the panel
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		textPane.setContentType(ContentTypeUtilities.toString(TEXT, PLAIN_SUBTYPE));	//set the content type to "text/plain" G***maybe allow this panel to support multiple MIME types, and put the setting of the type back into TextResourceKit
		textPane.getDocument().addDocumentListener(getModifyDocumentListener());	//show that we're modified whenever the text is edited (we never change the document, we just load and save text into the document)
		add(scrollPane, BorderLayout.CENTER);	//place the text pane, in its scroll pane, in the center of the panel
	}

	/**Loads the data from the model to the view, if necessary.
	@exception IOException Thrown if there was an error loading the model.
	*/
	public void loadModel() throws IOException
	{
		super.loadModel();	//do the default loading
		textPane.setText(getLocaleText().getText());	//get the text from the model
	}

	/**Stores the current data being edited to the model, if necessary.
	@exception IOException Thrown if there was an error loading the model.
	*/
	public void saveModel() throws IOException
	{
		super.saveModel();	//do the default saving
		getLocaleText().setText(textPane.getText());	//change the model's text to whatever is in the text pane 
	}

}