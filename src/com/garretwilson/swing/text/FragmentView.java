package com.garretwilson.swing.text;

/**Represents information about a view that is a fragment of another view.
@author Garret Wilson
*/
public interface FragmentView
{

	/**Returns the whether this is the first fragment in relation to the original
	  view. A paragraph, for example, might be fragmented into several views, but
	  only the first fragment should be indented.
	@return The index of this fragment in relation to the original unbroken view.
	*/
//G***del	public int getFragmentIndex();

	/**Returns whether this is the first fragment in relation to the original
	  view. A paragraph, for example, might be fragmented into several views, but
	  only the first fragment should be indented.
	@return <code>true</code> if this is the first fragment of the original view.
	*/
	public boolean isFirstFragment();

	/**Returns whether this is the last fragment in relation to the original
	  view.
	@return <code>true</code> if this is the last fragment of the original view.
	*/
//G***del if not needed	public boolean isLastFragment();

}