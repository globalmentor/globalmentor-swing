package com.garretwilson.swing.text.directory.vcard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
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

	/**The action for selecting the language of the categories.*/
	private final SelectLanguageAction selectCategoryLanguageAction;

		/**@return The action for selecting the language of the category.*/
		public SelectLanguageAction getSelectCategoryLanguageAction() {return selectCategoryLanguageAction;}

	/**The categories list.*/
	private final JList categoryList;

		/**@return The categories list.*/
		public JList getCategoryList() {return categoryList;}
	
	/**The label of the note text pane.*/
	private final JLabel noteLabel;

	/**The action for selecting the language of the note.*/
	private final SelectLanguageAction selectNoteLanguageAction;

		/**@return The action for selecting the language of the note.*/
		public SelectLanguageAction getSelectNoteLanguageAction() {return selectNoteLanguageAction;}

	/**The note text pane.*/
	private final JTextPane noteTextPane;

		/**@return The note text pane.*/
		public JTextPane getNoteTextPane() {return noteTextPane;}
	
	/**Sets the application categories.
	@param categories An array of application categories that should be selected.
	*/
	public void setCategories(final LocaleText[] categories)
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
	public LocaleText[] getCategories()
	{
		final Object[] selectedObjects=categoryList.getSelectedValues();	//get the selected categories
		final LocaleText[] selectedCategories=new LocaleText[selectedObjects.length];	//create a locale text array into which to place the selected categories
		System.arraycopy(selectedObjects, 0, selectedCategories, 0, selectedObjects.length);	//copy the categories into our string array
		return selectedCategories;	//return the selected categories
	}

	/**Sets the supplemental information.
	@param note The supplemental information, or <code>null</code> for no information.
	*/
	public void setNote(final LocaleText note)
	{
		if(note!=null)	//if there is a note
		{
			noteTextPane.setText(note.getText());	//set the text of the note text pane
			selectNoteLanguageAction.setLocale(note.getLocale());
		}
		else	//if there is no note, clear the fields
		{
			noteTextPane.setText("");
			selectNoteLanguageAction.setLocale(null);
		}
	}
	
	/**@return The edited supplemental information, or <code>null</code> for no information.*/
	public LocaleText getNote()
	{
		final String note=StringUtilities.getNonEmptyString(noteTextPane.getText().trim());
		return note!=null ? new LocaleText(note, selectNoteLanguageAction.getLocale()) : null;
	}

	/**Default constructor.*/
	public ExplanatoryPanel()
	{
		this((LocaleText)null);	//construct a panel with no categories and no note
	}

	/**Categories constructor.
	@param categories An array of application categories that should be selected.
	*/
	public ExplanatoryPanel(final LocaleText[] categories)
	{
		this(categories, null);	//construct the class with no notes
	}
	
	/**Note constructor.
	@param note The supplemental information, or <code>null</code> for no information.
	*/
	public ExplanatoryPanel(final LocaleText note)
	{
		this(new LocaleText[]{}, note);	//construct a panel with no categories
	}

	/**Categories and note constructor.
	@param categories An array of application categories that should be selected.
	@param note The supplemental information, or <code>null</code> for no information.
	*/
	public ExplanatoryPanel(final LocaleText[] categories, final LocaleText note)
	{
		super(new GridBagLayout(), false);	//construct the panel using a grid bag layout, but don't initialize the panel
		categoryLabel=new JLabel();
		categoryList=new JList();
		selectCategoryLanguageAction=new SelectCategoryLanguageAction();
		noteLabel=new JLabel();
		noteTextPane=new JTextPane();
		selectNoteLanguageAction=new SelectLanguageAction(null, noteTextPane);
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
		final JButton selectCategoryLanguageButton=new JButton(getSelectCategoryLanguageAction());
		selectCategoryLanguageButton.setText("");	//TODO create common routine for this
		selectCategoryLanguageButton.setBorder(null);
		categoryList.setUI(new ToggleListUI()); //allow the answers to be toggled on and off
		categoryList.setCellRenderer(new CheckBoxListCellRenderer());  //display the answers with checkboxes
		final JScrollPane categoryScrollPane=new JScrollPane(categoryList);
		noteLabel.setText("Note");	//G***i18n
		final JButton selectNoteLanguageButton=new JButton(getSelectNoteLanguageAction());
		selectNoteLanguageButton.setText("");	//TODO create common routine for this
		selectNoteLanguageButton.setBorder(null);
		final JScrollPane noteScrollPane=new JScrollPane(noteTextPane);
//G***del		noteTextPane.setMinimumSize(new Dimension(600, 200));
		add(categoryLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(selectCategoryLanguageButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(categoryScrollPane, new GridBagConstraints(0, 1, 2, 1, 0.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, NO_INSETS, 0, 0));
		add(noteLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(selectNoteLanguageButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, NO_INSETS, 0, 0));
		add(noteScrollPane, new GridBagConstraints(2, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, NO_INSETS, 0, 0));
	}

	/**Action for selecting a language for the selected category.
	@author Garret Wilson
	@see LanguagePanel
	@see Locale
	*/
	class SelectCategoryLanguageAction extends SelectLanguageAction
	{
		/**@return The locale that represents the language, or
			<code>null</code> if no language is indicated.
		*/
		public Locale getLocale()
		{
			final int leadSelectionIndex=categoryList.getLeadSelectionIndex();	//get the last selected category index
			final Object selectedValue=leadSelectionIndex>=0 ? categoryList.getModel().getElementAt(leadSelectionIndex) : null;	//get the last selected value
			return selectedValue instanceof LocaleText ? ((LocaleText)selectedValue).getLocale() : null;	//return the locale if locale text is selected
		}

		/**Sets the language.
		@param newLocale The locale that represents the language, or
			<code>null</code> if no language should be indicated.
		*/
		public void setLocale(final Locale newLocale)
		{
			final int leadSelectionIndex=categoryList.getLeadSelectionIndex();	//get the last selected category index
			final Object selectedValue=leadSelectionIndex>=0 ? categoryList.getModel().getElementAt(leadSelectionIndex) : null;	//get the last selected value
//G***del			final Object selectedValue=categoryList.getSelectedValue();	//get the selected value
			if(selectedValue instanceof LocaleText)	//if locale text is selected
			{
				final LocaleText localeText=(LocaleText)selectedValue;	//cast the selected object to locale text
				final Locale oldLocale=localeText.getLocale(); //get the old locale
				if(!ObjectUtilities.equals(oldLocale, newLocale))  //if the value is really changing
				{
					localeText.setLocale(newLocale); //update the value
					firePropertyChange(LocaleConstants.LOCALE_PROPERTY_NAME, oldLocale, newLocale);	//show that the locale property has changed
				}
			}
		}

		/**Default constructor.*/
		public SelectCategoryLanguageAction()
		{
			super(null, categoryList);	//construct the parent class with no locale using the category list as the parent component
		}

		/**Called when the action should be performed.
		@param actionEvent The event causing the action.
		*/
		public void actionPerformed(final ActionEvent actionEvent)
		{
			final int leadSelectionIndex=categoryList.getLeadSelectionIndex();	//get the last selected category index
			final Object selectedValue=leadSelectionIndex>=0 ? categoryList.getModel().getElementAt(leadSelectionIndex) : null;	//get the last selected value
			if(selectedValue instanceof LocaleText)	//if locale text is selected (really we're just checking to make sure something is selected---it should always be locale text, if anything)
			{
				super.actionPerformed(actionEvent);	//do the default language selection
			}
			
		}
	}
}
