package com.globalmentor.net;

import java.net.*;

import static com.garretwilson.swing.BasicOptionPane.*;
import com.garretwilson.swing.UserPasswordPanel;

/**Authenticator class which uses Swing to authenticate users.
@author Garret Wilson
*/
public class SwingAuthenticator extends AbstractAuthenticable	//TODO move to a Swing package
{

	/**Determines user and password information.
	@return The password authentication collected from the user, or <code>null</code> if none is provided.
	*/
/*G***fix
	public PasswordAuthentication getPasswordAuthentication()
	{
		return getPasswordAuthentication(null);	//get password authentication, allowing any user to be specified
	}
*/

	/**Determines password information for a given user.
	The user must not be allowed to change the username.
	@param username The user for which password information should be gathered, or <code>null</code> if the username is not restricted.
	@return The password authentication collected from the user, or <code>null</code> if none is provided.
	*/
/*G***fix
	public PasswordAuthentication getPasswordAuthentication(final String username)
	{
		final URI uri=new URI(getRquestingScheme(), getRequestingHost(), )
    private String requestingHost;
    private InetAddress requestingSite;
    private int requestingPort;
    private String requestingProtocol;
    private String requestingPrompt;
    private String requestingScheme;
    private URL requestingURL;
    private RequestorType requestingAuthType;
	}
*/

	/**Determines password information for a given user in relation to a given URI and description.
	The user must not be allowed to change the username, if one is provided.
	@param uri The URI for which authentication is requested, or <code>null</code> if there is no relevant URI.
	@param prompt A description of the authentication.
	@param username The user for which password information should be gathered, or <code>null</code> if the username is not restricted.
	@return The password authentication collected from the user, or <code>null</code> if none is provided.
	*/
	public PasswordAuthentication getPasswordAuthentication(final URI uri, final String prompt, final String username)
	{
		return askPasswordAuthentication(uri!=null ? uri.toString() : prompt, prompt, username);
	}
	
	
	/**Asks password authentication.
	@param title The dialog title.
	@param prompt A prompt string to present the user.
	@param username The user for which password information should be gathered, or <code>null</code> if the username is not restricted.
	@return The password authentication collected from the user, or <code>null</code> if none is provided.
	*/
	protected PasswordAuthentication askPasswordAuthentication(final String title, final String prompt, final String username)
	{
		final UserPasswordPanel userPasswordPanel=new UserPasswordPanel();	//create a password panel with verification
		if(username!=null)	//if a username was given
		{
			userPasswordPanel.setUsername(username);	//set the username in the panel
			userPasswordPanel.setUsernameEditable(false);	//don't allow the username to be edited
		}
		//TODO set prompt
		//ask the user for a new password; if the user accepts the change
		if(showConfirmDialog(null, userPasswordPanel, title, OK_CANCEL_OPTION)==OK_OPTION)
		{
			return new PasswordAuthentication(userPasswordPanel.getUsername(), userPasswordPanel.getPassword());	//return the password authentication from the panel
		}
		else	//if the user cancels
		{
			return null;	//show that the user canceled
		}
		
	}

}
