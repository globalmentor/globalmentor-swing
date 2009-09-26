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

import java.util.*;
import javax.swing.ImageIcon;

import com.globalmentor.collections.*;

/**Manages icon resources bundled with an application. This class keeps weak
	references to the icons it loads so that they may be reused if they have not
	been garbage collected.
@author Garret Wilson
*/
public class IconResources
{

	/**The filename of the icon indicating acceptance.*/
	public final static String ACCEPT_ICON_FILENAME="accept.gif"; 
	/**The filename of a plus sign icon.*/
	public final static String ADD_ICON_FILENAME="add.gif";
	/**The filename of an icon representing an animation.*/
	public final static String ANIMATION_ICON_FILENAME="animation.gif";
	/**The filename of the icon representing audio.*/
	public final static String AUDIO_ICON_FILENAME="audio.gif"; 
	/**The filename of an icon representing a closed book.*/
	public final static String BOOK_CLOSED_ICON_FILENAME="book_closed.gif";
	/**The filename of an icon representing an open book.*/
	public final static String BOOK_OPEN_ICON_FILENAME="book_open.gif";
	/**The filename of an icon representing a book with a question mark.*/
	public final static String BOOK_QUESTION_ICON_FILENAME="book_question.gif";
	/**The filename of an icon representing reverting to an earlier version.*/
	public final static String BACK_VERSION_ICON_FILENAME="back_version.gif";
	/**The filename of a bookmark icon.*/
	public final static String BOOKMARK_ICON_FILENAME="bookmark.gif";
	/**The filename of a bookmark delete icon.*/
	public final static String BOOKMARK_DELETE_ICON_FILENAME="bookmark_delete.gif";
	/**The filename of an icon representing a boolean value.*/
	public final static String BOOLEAN_ICON_FILENAME="boolean.gif";
	/**The filename of the icon representing a cardfile.*/
	public final static String CARDFILE_ICON_FILENAME="cardfile.gif";
	/**The filename of the icon representing a cardfile with a card removed.*/
	public final static String CARDFILE_REMOVED_ICON_FILENAME="cardfile_removed.gif";
	/**The filename of the icon representing a roll of business cards.*/
	public final static String CARD_ROLL_ICON_FILENAME="card_roll.gif";
	/**The filename of a the icon representing a checkmark on an item.*/
	public final static String CHECK_ICON_FILENAME="check.gif";
	/**The filename of a the icon representing a checkmark on multiple items.*/
	public final static String CHECK_MULTIPLE_ICON_FILENAME="check_multiple.gif";
	/**The filename of the icon representing choice of items.*/
	public final static String CHOICE_ICON_FILENAME="choice.gif";
	/**The filename of the icon representing colors.*/
	public final static String COLOR_ICON_FILENAME="colors.gif";
	/**The filename of an icon representing one collumn.*/
	public final static String COLUMNS1_ICON_FILENAME="columns1.gif";
	/**The filename of an icon representing two columns.*/
	public final static String COLUMNS2_ICON_FILENAME="columns2.gif";
	/**The filename of the copy icon.*/
	public final static String COPY_ICON_FILENAME="copy.gif";
	/**The filename of the cut icon.*/
	public final static String CUT_ICON_FILENAME="cut.gif";
	/**The filename of the delete icon.*/
	public final static String DELETE_ICON_FILENAME="delete.gif";	//TODO maybe remove this file and just use the new reject icon 
	/**The filename of the icon representing a document.*/
	public final static String DOCUMENT_ICON_FILENAME="document.gif";
	/**The filename of the icon representing a document with content.*/
	public final static String DOCUMENT_CONTENT_ICON_FILENAME="document_content.gif";
	/**The filename of the icon representing a new document.*/
	public final static String DOCUMENT_NEW_ICON_FILENAME="document_new.gif";
	/**The filename of the icon representing previewing a document.*/
	public final static String DOCUMENT_PREVIEW_ICON_FILENAME="document_preview.gif";
	/**The filename of an icon representing a document with a question mark.*/
	public final static String DOCUMENT_QUESTION_ICON_FILENAME="document_question.gif";
	/**The filename of an icon representing a new document with a question mark.*/
	public final static String DOCUMENT_QUESTION_NEW_ICON_FILENAME="document_question_new.gif";
	/**The filename of the icon representing a document with richcontent.*/
	public final static String DOCUMENT_RICH_CONTENT_ICON_FILENAME="document_rich_content.gif";
	/**The filename of the icon representing a stack of documents.*/
	public final static String DOCUMENT_STACK_ICON_FILENAME="document_stack.gif";
	/**The filename of an icon representing a closed door.*/
	public final static String DOOR_CLOSED_ICON_FILENAME="door_closed.gif";
	/**The filename of an icon representing an open door.*/
	public final static String DOOR_OPEN_ICON_FILENAME="door_open.gif";
	/**The filename of the icon for downloading a resource.*/
	public final static String DOWNLOAD_ICON_FILENAME="download.gif";
	/**The filename of the edit icon.*/
	public final static String EDIT_ICON_FILENAME="edit.gif";
	/**The filename of an icon representing email.*/
	public final static String EMAIL_ICON_FILENAME="email.gif";
	/**The filename of the icon representing the action of entering.*/
	public final static String ENTER_ICON_FILENAME="enter.gif";
	/**The filename of the icon representing the action of exiting.*/
	public final static String EXIT_ICON_FILENAME="exit.gif";
	/**The filename of an icon for exporting.*/
	public final static String EXPORT_ICON_FILENAME="export.gif";
	/**The filename of the icon representing a lightning flash.*/
	public final static String FLASH_ICON_FILENAME="flash.gif";
	/**The filename of the icon representing an open folder cabinet.*/
	public final static String FOLDER_CABINET_OPEN_ICON_FILENAME="folder_cabinet_open.gif";
	/**The filename of the icon representing a closed folder cabinet.*/
	public final static String FOLDER_CABINET_CLOSED_ICON_FILENAME="folder_cabinet_closed.gif";
	/**The filename of the icon representing a document within a folder.*/
	public final static String FOLDER_DOCUMENT_ICON_FILENAME="folder_document.gif";
	/**The filename of the icon representing a folder.*/
	public final static String FOLDER_ICON_FILENAME="folder.gif";
	/**The filename of the icon representing multiple folders.*/
	public final static String FOLDERS_ICON_FILENAME="folders.gif";
	/**The filename of the icon representing a new folder.*/
	public final static String FOLDER_NEW_ICON_FILENAME="folder_new.gif";
	/**The filename of the icon representing an open folder.*/
	public final static String FOLDER_OPEN_ICON_FILENAME="folder_open.gif";
	/**The filename of the icon representing a tree of folders.*/
	public final static String FOLDER_TREE_ICON_FILENAME="folder_tree.gif";
	/**The filename of the icon representing navigation up a tree of folders.*/
	public final static String FOLDER_TREE_UP_ICON_FILENAME="folder_tree_up.gif";
	/**The filename of the icon representing gears.*/
	public final static String GEARS_ICON_FILENAME="gears.gif";
	/**The filename of the icon representing a globw.*/
	public final static String GLOBE_ICON_FILENAME="globe.gif";
	/**The filename of the icon representing a bar graph.*/
	public final static String GRAPH_BAR_ICON_FILENAME="graph_bar.gif";
	/**The filename of the icon representing a graphed line.*/
	public final static String GRAPH_LINE_ICON_FILENAME="graph_line.gif";
	/**The filename of the icon representing a pie graph.*/
	public final static String GRAPH_PIE_ICON_FILENAME="graph_pie.gif";
	/**The filename of the icon representing graphed points.*/
	public final static String GRAPH_POINTS_ICON_FILENAME="graph_points.gif";
	/**The filename of the icon representing a group of objects.*/
	public final static String GROUP_ICON_FILENAME="group.gif";
	/**The filename of an icon representing a hand pointing down.*/
	public final static String HAND_POINT_DOWN_ICON_FILENAME="hand_point_down.gif";
	/**The filename of an icon representing a hand pointing left.*/
	public final static String HAND_POINT_LEFT_ICON_FILENAME="hand_point_left.gif";
	/**The filename of an icon representing a hand pointing right.*/
	public final static String HAND_POINT_RIGHT_ICON_FILENAME="hand_point_right.gif";
	/**The filename of an icon representing a hand pointing up.*/
	public final static String HAND_POINT_UP_ICON_FILENAME="hand_point_up.gif";
	/**The filename of an icon representing help.*/
	public final static String HELP_ICON_FILENAME="help.gif";
	/**The filename of an icon representing a hot spot.*/
	public final static String HOT_SPOT_ICON_FILENAME="hot_spot.gif";
	/**The filename of an icon representing an image.*/
	public final static String IMAGE_ICON_FILENAME="image.gif";
	/**The filename of an icon representing an image being deleted.*/
	public final static String IMAGE_DELETE_ICON_FILENAME="image_delete.gif";
	/**The filename of the icon representing previewing an image.*/
	public final static String IMAGE_PREVIEW_ICON_FILENAME="image_preview.gif";
	/**The filename of an icon for an image size.*/
	public final static String IMAGE_SIZE_ICON_FILENAME="image_size.gif";
	/**The filename of an icon for importing.*/
	public final static String IMPORT_ICON_FILENAME="import.gif";
	/**The filename of an icon representing information.*/
	public final static String INFO_ICON_FILENAME="info.gif";
	/**The filename of an icon representing a key.*/
	public final static String KEY_ICON_FILENAME="key.gif";
	/**The filename of a green LED that is off.*/
	public final static String LIGHT_GREEN_OFF="light_green_off.gif";
	/**The filename of a green LED that is on.*/
	public final static String LIGHT_GREEN_ON="light_green_on.gif";
	/**The filename of a red LED that is off.*/
	public final static String LIGHT_RED_OFF="light_red_off.gif";
	/**The filename of a red LED that is on.*/
	public final static String LIGHT_RED_ON="light_RED_on.gif";
	/**The filename of a yellow LED that is off.*/
	public final static String LIGHT_YELLOW_OFF="light_yellow_off.gif";
	/**The filename of a yellow LED that is on.*/
	public final static String LIGHT_YELLOW_ON="light_yellow_on.gif";
	/**The filename of the icon representing a list of items.*/
	public final static String LIST_ICON_FILENAME="list.gif";
	/**The filename of an icon representing a list with a hand pointing down.*/
	public final static String LIST_HAND_POINT_DOWN_ICON_FILENAME="list_hand_point_down.gif";
	/**The filename of an icon representing a list with a hand pointing up.*/
	public final static String LIST_HAND_POINT_UP_ICON_FILENAME="list_hand_point_up.gif";
	/**The filename of an icon representing mail.*/
	public final static String MAIL_ICON_FILENAME="mail.gif";
	/**The filename of an icon representing mail containing audio.*/
	public final static String MAIL_AUDIO_ICON_FILENAME="mail_audio.gif";
	/**The filename of an icon representing mail burning.*/
	public final static String MAIL_HOT_ICON_FILENAME="mail_hot.gif";
	/**The filename of an icon representing open mail.*/
	public final static String MAIL_OPEN_ICON_FILENAME="mail_open.gif";
	/**The filename of an icon representing mail containing a picture.*/
	public final static String MAIL_PICTURE_ICON_FILENAME="mail_picture.gif";
	/**The filename of an icon representing mail containing text.*/
	public final static String MAIL_TEXT_ICON_FILENAME="mail_text.gif";
	/**The filename of an icon representing mail containing a video.*/
	public final static String MAIL_VIDEO_ICON_FILENAME="mail_video.gif";
	/**The filename of the icon representing text markup.*/
	public final static String MARKUP_ICON_FILENAME="markup.gif";
	/**The filename of a computer monitor icon.*/
	public final static String MONITOR_ICON_FILENAME="monitor.gif";
	/**The filename of an icon representing a note.*/
	public final static String NOTE_ICON_FILENAME="note.gif";
	/**The filename of a notepad icon.*/
	public final static String NOTEPAD_ICON_FILENAME="notepad.gif";
	/**The filename of a notepad delete icon.*/
	public final static String NOTEPAD_DELETE_ICON_FILENAME="notepad_delete.gif";
	/**The filename of the icon representing a number.*/
	public final static String NUMBER_ICON_FILENAME="number.gif";
	/**The filename of an icon representing a paintbrush painting.*/
	public final static String PAINT_ICON_FILENAME="paint.gif";
	/**The filename of the paste icon.*/
	public final static String PASTE_ICON_FILENAME="paste.gif";
	/**The filename of the icon representing a telephone.*/
	public final static String PHONE_ICON_FILENAME="phone.gif";
	/**The filename of the icon representing a telephone ringing.*/
	public final static String PHONE_RING_ICON_FILENAME="phone_ring.gif";
	/**The filename of a polygon with curves for some sides.*/
	public final static String POLYGON_CURVED_ICON_FILENAME="polygon_curved.gif";
	/**The filename of a polygon with visible points.*/
	public final static String POLYGON_POINTS_ICON_FILENAME="polygon_points.gif";
	/**The filename of an icon representing a popup window.*/
	public final static String POPUP_ICON_FILENAME="popup.gif";
	/**The filename of an icon representing a resource property.*/
	public final static String PROPERTY_ICON_FILENAME="property.gif";
	/**The filename of the question mark icon.*/
	public final static String QUESTION_ICON_FILENAME="question.gif";
	/**The filename of the icon representing a new question.*/
	public final static String QUESTION_NEW_ICON_FILENAME="question_new.gif";
	/**The filename of a circular redo arrow.*/
	public final static String REDO_ICON_FILENAME="redo.gif";
	/**The filename of the icon indicating rejectance.*/
	public final static String REJECT_ICON_FILENAME="reject.gif"; 
	/**The filename of an icon representing a resource.*/
	public final static String RESOURCE_ICON_FILENAME="resource.gif";
	/**The filename of the save as icon.*/
	public final static String SAVE_AS_ICON_FILENAME="save_as.gif";
	/**The filename of the save icon.*/
	public final static String SAVE_ICON_FILENAME="save.gif";
	/**The filename of the search icon.*/
	public final static String SEARCH_ICON_FILENAME="search.gif";
	/**The filename of an icon for searching again.*/
	public final static String SEARCH_AGAIN_ICON_FILENAME="search_again.gif";
	/**The filename of the icon showing a hand pointing to text on a page.*/
	public final static String SHOW_ICON_FILENAME="show.gif";
	/**The filename of the icon representing a slider bar widget.*/
	public final static String SLIDER_ICON_FILENAME="slider.gif";
	/**The filename of the icon representing a round speech balloon.*/
	public final static String SPEECH_CIRCLE_ICON_FILENAME="speech_circle.gif";
	/**The filename of the icon representing a round speech balloon with text.*/
	public final static String SPEECH_CIRCLE_TEXT_ICON_FILENAME="speech_circle_text.gif";
	/**The filename of the icon representing a rectangle speech balloon.*/
	public final static String SPEECH_RECTANBLE_ICON_FILENAME="speech_rectangle.gif";
	/**The filename of the icon representing a rectangle speech balloon with text.*/
	public final static String SPEECH_RECTANGLE_TEXT_ICON_FILENAME="speech_rectangle_text.gif";
	/**The filename of the stop icon.*/
	public final static String STOP_ICON_FILENAME="stop.gif";
	/**The filename of the icon representing a string.*/
	public final static String STRING_ICON_FILENAME="string.gif";
	/**The filename of the icon representing a string being edited.*/
	public final static String STRING_EDIT_ICON_FILENAME="string_edit.gif";
	/**The filename of the submit icon, a hand holding a sheet of paper.*/
	public final static String SUBMIT_ICON_FILENAME="submit.gif";
	/**The filename of a minus sign icon.*/
	public final static String SUBTRACT_ICON_FILENAME="subtract.gif";
	/**The filename of an icon representing a data table.*/
	public final static String TABLE_ICON_FILENAME="table.gif";
	/**The filename of an icon representing a tack.*/
	public final static String TACK_ICON_FILENAME="tack.gif";
	/**The filename of the icon representing a tree data object.*/
	public final static String TREE_ICON_FILENAME=FOLDER_TREE_ICON_FILENAME;
	/**The filename of an icon of a triangle pointing down.*/
	public final static String TRIANGLE_DOWN_ICON_FILENAME="triangle_down.gif";
	/**The filename of an icon of a triangle pointing right.*/
	public final static String TRIANGLE_RIGHT_ICON_FILENAME="triangle_right.gif";
	/**The filename of a the icon representing an item without a checkmark.*/
	public final static String UNCHECK_ICON_FILENAME="uncheck.gif";
	/**The filename of a the icon representing multiple items without checkmarks.*/
	public final static String UNCHECK_MULTIPLE_ICON_FILENAME="uncheck_multiple.gif";
	/**The filename of the icon for uploading a resource.*/
	public final static String UPLOAD_ICON_FILENAME="upload.gif";
	/**The filename of an icon representing a single user.*/
	public final static String USER_ICON_FILENAME="user.gif";
	/**The filename of an icon for removing a user.*/
	public final static String USER_REMOVE_ICON_FILENAME="user_remove.gif";
	/**The filename of an icon representing multiple users.*/
	public final static String USERS_ICON_FILENAME="users.gif";
	/**The filename of an icon depicting a window at the topmost z-order.*/
	public final static String WINDOW_TOP_ICON_FILENAME="window_top.gif";
	/**The filename of an icon for enlargement.*/
	public final static String ZOOM_IN_ICON_FILENAME="zoom_in.gif";
	/**The filename of an icon for reduction.*/
	public final static String ZOOM_OUT_ICON_FILENAME="zoom_out.gif";

		//document type icons
	/**The filename of the icon representing Microsoft Word documents.*/
	public final static String MICROSOFT_WORD_DOCUMENT_ICON_FILENAME="microsoft_word_document.gif";
	/**The filename of the icon representing Adobe PDF documents.*/
	public final static String ADOBE_PDF_DOCUMENT_ICON_FILENAME="adobe_pdf_document.gif";

		//function-based identifiers

	/**The filename of the general close icon.*/
	public final static String CLOSE_ICON_FILENAME=FOLDER_ICON_FILENAME;
	/**The filename of the general configuration/settings icon.*/
	public final static String CONFIGURATION_ICON_FILENAME=GEARS_ICON_FILENAME;
	/**The filename of an icon for moving an item down.*/
	public final static String MOVE_DOWN_ICON_FILENAME=LIST_HAND_POINT_DOWN_ICON_FILENAME;
	/**The filename of an icon for moving an item up.*/
	public final static String MOVE_UP_ICON_FILENAME=LIST_HAND_POINT_UP_ICON_FILENAME;
	/**The filename of the new icon.*/
	public final static String NEW_ICON_FILENAME=DOCUMENT_NEW_ICON_FILENAME;
	/**The filename of the general open icon.*/
	public final static String OPEN_ICON_FILENAME=FOLDER_OPEN_ICON_FILENAME;

	/**The thread-safe map of icons that will be released when no longer is use.*/
	protected static final Map<String, ImageIcon> iconMap=new DecoratorReadWriteLockMap<String, ImageIcon>(new PurgeOnWriteWeakValueHashMap<String, ImageIcon>());

	/**This class cannot be publicly instantiated.*/
	private IconResources() {}

	/**Loads an icon resource. The filename may either be the name of a file
		stored in /com/garretwilson/resources/icons, or the full path to an image
		file.
	@param filename The filename of the icon.
	@return An icon object representing the image.
	*/
	public static ImageIcon getIcon(final String filename)
	{
		ImageIcon imageIcon=iconMap.get(filename); //see if we have the icon
		if(imageIcon==null)  //if we haven't loaded this icon yet, or the icon has been garage collected
		{
		  imageIcon=new ImageIcon(IconResources.class.getResource(filename));	//load the icon
			iconMap.put(filename, imageIcon);  //store a weak reference to the icon
		}
		return imageIcon;  //return the icon that we loaded or already had loaded
	}

}