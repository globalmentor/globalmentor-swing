package com.garretwilson.swing;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import com.garretwilson.awt.BasicGridBagLayout;
import com.garretwilson.io.*;
import com.garretwilson.swing.unicode.UnicodeStatusBar;
import com.garretwilson.util.*;

import static com.garretwilson.io.ContentTypeConstants.*;

/**A panel to edit plain text.
@author Garret Wilson
*/
public class TextPanel extends ModelPanel<LocaleText>
{

	private final JScrollPane scrollPane;
	private final JTextPane textPane;

	/**The status bar showing information about the current Unicode character.*/
	private final UnicodeStatusBar unicodeStatusBar;

		/**@return The status bar showing information about the current Unicode character.*/
		protected final UnicodeStatusBar getUnicodeStatusBar() {return unicodeStatusBar;}

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
//TODO del when works		textPane=new JTextPane();	//create a new text pane
		textPane=new XMLTextPane();	//create a new text pane
		scrollPane=new JScrollPane(textPane);	//create the text pane
		unicodeStatusBar=new UnicodeStatusBar();	//create a new Unicode status bar
		initialize(); //initialize the panel
	}

	/**Initialize the user interface.*/
	protected void initializeUI()
	{
		super.initializeUI(); //do the default UI initialization
		textPane.setContentType(ContentTypeUtilities.toString(TEXT, PLAIN_SUBTYPE));	//set the content type to "text/plain" G***maybe allow this panel to support multiple MIME types, and put the setting of the type back into TextResourceKit
		textPane.getDocument().addDocumentListener(getModifyDocumentListener());	//show that we're modified whenever the text is edited (we never change the document, we just load and save text into the document)
		add(scrollPane, BorderLayout.CENTER);	//place the text pane, in its scroll pane, in the center of the panel
		add(unicodeStatusBar, BorderLayout.SOUTH);	//put the Unicode status bar in the south TODO make this an option
		unicodeStatusBar.attach(textPane);	//set the Unicode status bar to track the XML text pane 
	}

	/**Loads the data from the model to the view, if necessary.
	@exception IOException Thrown if there was an error loading the model.
	*/
	public void loadModel() throws IOException
	{
		super.loadModel();	//do the default loading
		textPane.setText(getModel().getText());	//get the text from the model
	}

	/**Stores the current data being edited to the model, if necessary.
	@exception IOException Thrown if there was an error loading the model.
	*/
	public void saveModel() throws IOException
	{
		super.saveModel();	//do the default saving
		getModel().setText(textPane.getText());	//change the model's text to whatever is in the text pane 
	}

}