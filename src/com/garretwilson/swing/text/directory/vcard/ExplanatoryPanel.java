package com.garretwilson.swing.text.directory.vcard;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.*;
import javax.swing.*;
import com.garretwilson.lang.*;
import com.garretwilson.text.directory.vcard.*;
import com.garretwilson.swing.*;
import com.garretwilson.util.*;

/**A panel allowing entry of the explanatory types of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class ExplanatoryPanel extends DefaultPanel
{

	/**The label of the categories list.*/
	private final JLabel categoryLabel;

	/**The categories list.*/
	private final JList categoryList;

		/**@return The categories list.*/
		public JList getCategoryList() {return categoryList;}
	
	/**The label of the note text pane.*/
	private final JLabel noteLabel;

	/**The note text pane.*/
	private final JTextPane noteTextPane;

		/**@return The note text pane.*/
		public JTextPane getNoteTextPane() {return noteTextPane;}
	
	/**Sets the application categories.
	@param categories An array of application categories that should be selected.
	*/
	public void setCategories(final String[] categories)
	{
		final ModifiableSet availableCategorySet=Categories.getAvailableCategorySet();	//get the available categores
		CollectionUtilities.addAll(availableCategorySet, categories);	//add all the categories to the available category set, which will only add the ones that aren't there already
		final List availableCategoryList=new ArrayList(availableCategorySet);	//create a list of available categories
		Collections.sort(availableCategoryList);	//sort the list of available categories
		categoryList.setListData(availableCategoryList.toArray());	//put the available categories in the list
		for(int i=categories.length-1; i>=0; --i)	//look at each category
		{
			categoryList.setSelectedValue(categories[i], false);	//select this category
		}
	}
	
	/**@return An array of application categories selected.*/
	public String[] getCategories()
	{
		final Object[] selectedObjects=categoryList.getSelectedValues();	//get the selected categories
		final String[] selectedCategories=new String[selectedObjects.length];	//create a string array into which to place the selected categories
		System.arraycopy(selectedObjects, 0, selectedCategories, 0, selectedObjects.length);	//copy the categories into our string array
		return selectedCategories;	//return the selected categories
	}

	/**Sets the supplemental information.
	@param note The supplemental information, or <code>null</code> for no information.
	*/
	public void setNote(final String note)
	{
		noteTextPane.setText(note!=null ? note : "");	//set the text of the note text pane
	}
	
	/**@return The edited supplemental information, or <code>null</code> for no information.*/
	public String getNote()
	{
		return StringUtilities.getNonEmptyString(noteTextPane.getText().trim());
	}

	/**Default constructor.*/
	public ExplanatoryPanel()
	{
		this((String)null);	//construct a panel with no categories and no note
	}

	/**Categories constructor.
	@param categories An array of application categories that should be selected.
	*/
	public ExplanatoryPanel(final String[] categories)
	{
		this(categories, null);	//construct the class with no notes
	}
	
	/**Note constructor.
	@param note The supplemental information, or <code>null</code> for no information.
	*/
	public ExplanatoryPanel(final String note)
	{
		this(new String[]{}, note);	//construct a panel with no categories
	}

	/**Categories and note constructor.
	@param categories An array of application categories that should be selected.
	@param note The supplemental information, or <code>null</code> for no information.
	*/
	public ExplanatoryPanel(final String[] categories, final String note)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		categoryLabel=new JLabel();
		categoryList=new JList();
		noteLabel=new JLabel();
		noteTextPane=new JTextPane();
		setDefaultFocusComponent(noteTextPane);	//set the default focus component
		initialize();	//initialize the panel
		setCategories(categories);	//set the given categories
		setNote(note);	//set the given note
	}
	
	/**Initializes the user interface.*/
	public void initializeUI()
	{
		super.initializeUI();	//do the default user interface initialization
		categoryLabel.setText("Categories");	//G***i18n
		categoryList.setUI(new ToggleListUI()); //allow the answers to be toggled on and off
		categoryList.setCellRenderer(new CheckBoxListCellRenderer());  //display the answers with checkboxes
		final JScrollPane categoryScrollPane=new JScrollPane(categoryList);
		noteLabel.setText("Note");	//G***i18n
		final JScrollPane noteScrollPane=new JScrollPane(noteTextPane);
//G***del		noteTextPane.setMinimumSize(new Dimension(600, 200));
		add(categoryLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(categoryScrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, NO_INSETS, 0, 0));
		add(noteLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(noteScrollPane, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, NO_INSETS, 0, 0));
	}
}
