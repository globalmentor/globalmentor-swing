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

package com.globalmentor.swing.text;

import javax.swing.text.*;

/**Convenience methods for accessing Swing text components.
@author Garret Wilson
*/
public class TextComponents	//TODO do we really want this and SwingText?
{

	/**The name of the property that indicates the current document.*/
	public final static String DOCUMENT_PROPERTY="document";

	/**Default constructor.*/
	public TextComponents() {}

	/**Removes all highlights using a particular highlight painter from a text
	  component.
	@param textComponent The text component which has the highlights.
	@param hightlightPainter The painter the highlights of which should be removed.
	@return The number of highlights removed.
	*/
	public static int removeHighlights(final JTextComponent textComponent, final Highlighter.HighlightPainter highlightPainter)
	{
		int removedHighlightCount=0; //show that we have not removed any highlights, yet
		final Highlighter highlighter=textComponent.getHighlighter(); //get the current highlighter
		final Highlighter.Highlight[] highlightArray=highlighter.getHighlights(); //get an array of highlights
		for(int i=highlightArray.length-1; i>=0; --i)  //look at each highlight
		{
			final Highlighter.Highlight highlight=highlightArray[i];  //get a reference to this highlight
			if(highlight.getPainter()==highlightPainter) //if this is highlight uses the specified painter
			{
				highlighter.removeHighlight(highlight);		//remove the highlight
				++removedHighlightCount;  //show that we removed another highlight
			}
		}
		return removedHighlightCount; //show how many highlights we removed
	}

	/**Removes a highlight from a text component if the highlight uses a particular
		highlight painter and has a particular offset and length.
	@param textComponent The text component which has the highlights.
	@param hightlightPainter The painter a highlight of which should be removed.
	@para
	@return The number of highlights removed.
	*/
/*TODO del if not needed
	public static int removeHighlight(final JTextComponent textComponent, final Highlighter.HighlightPainter highlightPainter)
	{
		int removedHighlightCount=0; //show that we have not removed any highlights, yet
		final Highlighter highlighter=textComponent.getHighlighter(); //get the current highlighter
		final Highlighter.Highlight[] highlightArray=highlighter.getHighlights(); //get an array of highlights
		for(int i=highlightArray.length-1; i>=0; --i)  //look at each highlight
		{
			final Highlighter.Highlight highlight=highlightArray[i];  //get a reference to this highlight
			if(highlight.getPainter()==highlightPainter) //if this is highlight uses the specified painter
			{
				highlighter.removeHighlight(highlight);		//remove the highlight
				++removedHighlightCount;  //show that we removed another highlight
			}
		}
		return removedHighlightCount; //show how many highlights we removed
	}
*/

}