package com.garretwilson.swing;

import java.awt.*;
import javax.swing.*;
import com.garretwilson.awt.WindowUtilities;

/**An improved dialog class with basic needed functionality.
<p>This class improves its packing functionality by proportionally resizing
	dialogs that prefer to be larger than the screen and contain a scrollpane.</p>
@author Garret Wilson
*/
public class BasicDialog extends JDialog
{
  /**
	 * Creates a non-modal dialog without a title and without a specified
	 * <code>Frame</code> owner.  A shared, hidden frame will be
	 * set as the owner of the dialog.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.     
	 * 
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog() throws HeadlessException
	{
		this((Frame) null, false);
	}
	/**
	 * Creates a non-modal dialog without a title with the
	 * specified <code>Frame</code> as its owner.  If <code>owner</code>
	 * is <code>null</code>, a shared, hidden frame will be set as the
	 * owner of the dialog.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog(Frame owner) throws HeadlessException
	{
		this(owner, false);
	}
	/**
	 * Creates a modal or non-modal dialog without a title and
	 * with the specified owner <code>Frame</code>.  If <code>owner</code>
	 * is <code>null</code>, a shared, hidden frame will be set as the
	 * owner of the dialog.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.     
	 *
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param modal  true for a modal dialog, false for one that allows
	 *               others windows to be active at the same time
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog(Frame owner, boolean modal) throws HeadlessException
	{
		this(owner, null, modal);
	}
	/**
	 * Creates a non-modal dialog with the specified title and
	 * with the specified owner frame.  If <code>owner</code>
	 * is <code>null</code>, a shared, hidden frame will be set as the
	 * owner of the dialog.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.     
	 *
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title  the <code>String</code> to display in the dialog's
	 *			title bar
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog(Frame owner, String title) throws HeadlessException
	{
		this(owner, title, false);
	}
	/**
	 * Creates a modal or non-modal dialog with the specified title 
	 * and the specified owner <code>Frame</code>.  If <code>owner</code>
	 * is <code>null</code>, a shared, hidden frame will be set as the
	 * owner of this dialog.  All constructors defer to this one.
	 * <p>
	 * NOTE: Any popup components (<code>JComboBox</code>,
	 * <code>JPopupMenu</code>, <code>JMenuBar</code>)
	 * created within a modal dialog will be forced to be lightweight.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.     
	 *
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title  the <code>String</code> to display in the dialog's
	 *			title bar
	 * @param modal  true for a modal dialog, false for one that allows
	 *               other windows to be active at the same time
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog(Frame owner, String title, boolean modal) throws HeadlessException
	{
		super(owner, title, modal);
	}
	/**
	 * Creates a modal or non-modal dialog with the specified title, 
	 * owner <code>Frame</code>, and <code>GraphicsConfiguration</code>.
	 * 
	 * <p>
	 * NOTE: Any popup components (<code>JComboBox</code>,
	 * <code>JPopupMenu</code>, <code>JMenuBar</code>)
	 * created within a modal dialog will be forced to be lightweight.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.     
	 *
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title  the <code>String</code> to display in the dialog's
	 *                  title bar
	 * @param modal  true for a modal dialog, false for one that allows
	 *               other windows to be active at the same time
	 * @param gc the <code>GraphicsConfiguration</code> 
	 * of the target screen device.  If <code>gc</code> is 
	 * <code>null</code>, the same
	 * <code>GraphicsConfiguration</code> as the owning Frame is used.    
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 * @since 1.4
	 */
	public BasicDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc)
	{
		super(owner, title, modal, gc);
	}
	/**
	 * Creates a non-modal dialog without a title with the
	 * specified <code>Dialog</code> as its owner.
	 * <p>
	 * This constructor sets the component's locale property to the value 
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog(Dialog owner) throws HeadlessException
	{
		this(owner, false);
	}
	/**
	 * Creates a modal or non-modal dialog without a title and
	 * with the specified owner dialog.
	 * <p>
	 * This constructor sets the component's locale property to the value 
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
	 * @param modal  true for a modal dialog, false for one that allows
	 *               other windows to be active at the same time
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog(Dialog owner, boolean modal) throws HeadlessException
	{
		this(owner, null, modal);
	}
	/**
	 * Creates a non-modal dialog with the specified title and
	 * with the specified owner dialog.
	 * <p>
	 * This constructor sets the component's locale property to the value 
	 * returned by <code>JComponent.getDefaultLocale</code>.
	 *
	 * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
	 * @param title  the <code>String</code> to display in the dialog's
	 *			title bar
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog(Dialog owner, String title) throws HeadlessException
	{
		this(owner, title, false);
	}
	/**
	 * Creates a modal or non-modal dialog with the specified title 
	 * and the specified owner frame. 
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.     
	 *
	 * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
	 * @param title  the <code>String</code> to display in the dialog's
	 *			title bar
	 * @param modal  true for a modal dialog, false for one that allows
	 *               other windows to be active at the same time
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 */
	public BasicDialog(Dialog owner, String title, boolean modal) throws HeadlessException
	{
		super(owner, title, modal);
	}
	/**
	 * Creates a modal or non-modal dialog with the specified title, 
	 * owner <code>Dialog</code>, and <code>GraphicsConfiguration</code>.
	 * 
	 * <p>
	 * NOTE: Any popup components (<code>JComboBox</code>,
	 * <code>JPopupMenu</code>, <code>JMenuBar</code>)
	 * created within a modal dialog will be forced to be lightweight.
	 * <p>
	 * This constructor sets the component's locale property to the value
	 * returned by <code>JComponent.getDefaultLocale</code>.     
	 *
	 * @param owner the <code>Dialog</code> from which the dialog is displayed
	 * @param title  the <code>String</code> to display in the dialog's
	 *			title bar
	 * @param modal  true for a modal dialog, false for one that allows
	 *               other windows to be active at the same time
	 * @param gc the <code>GraphicsConfiguration</code> 
	 * of the target screen device.  If <code>gc</code> is 
	 * <code>null</code>, the same
	 * <code>GraphicsConfiguration</code> as the owning Dialog is used.    
	 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JComponent#getDefaultLocale
	 * returns true.
	 * @since 1.4
	 */
	public BasicDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) throws HeadlessException
	{
		super(owner, title, modal, gc);
	}

	/**Causes this window to be sized to fit the preferred size and layouts of
	its subcomponents.
	<p>This version proportionally resizes the dialog if it prefers to be larger
		than the screen and contains a scrollpane.</p>
	@see WindowUtilities#constrainSize(Window)
	*/
	public void pack()
	{
		super.pack();	//pack the window normally
		WindowUtilities.constrainSize(this);	//make sure this dialog isn't too large for the graphics configuration 
	}
	
}
