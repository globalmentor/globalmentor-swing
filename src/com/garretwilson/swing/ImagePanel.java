package com.garretwilson.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import com.garretwilson.resources.icon.IconResources;

/**A panel that displays an image along with an optional toolbar.
@author Garret Wilson
@see ImageComponent
*/
public class ImagePanel extends JPanel
{

	/**The relative amount to increase or decrease the zoom.*/
	protected final double ZOOM_DELTA_FACTOR=1.5;

	/**The action for fitting the image to its container.*/
	private final Action fitImageAction=new FitImageAction();

		/**@return The action for fitting the image to its container.*/
		public Action getFitImageAction() {return fitImageAction;}

	/**The action for enlarging the image.*/
	private final Action zoomInAction=new ZoomInAction();

		/**@return The action for enlarging the image.*/
		public Action getZoomInAction() {return zoomInAction;}

	/**The action for reducing the image.*/
	private final Action zoomOutAction=new ZoomOutAction();

		/**@return The action for reducing the image.*/
		public Action getZoomOutAction() {return zoomOutAction;}

	/**The image to be displayed.*/
	private Image image;

		/**@return The image to be displayed.*/
		public Image getImage() {return imageComponent.getImage();}

		/**Sets the image to be displayed.
		@param newImage The new image to be displayed.
		*/
		public void setImage(final Image newImage) {imageComponent.setImage(newImage);}

  /**The panel toolbar.*/
	private final JToolBar toolBar=new JToolBar();

		/**@return The panel toolbar.*/
		public JToolBar getToolBar() {return toolBar;}

  /**The image component used to display the image.*/
	private final ImageComponent imageComponent=new ImageComponent();

		/**@return The image component used to display the image.*/
		public ImageComponent getImageComponent() {return imageComponent;}

  BorderLayout borderLayout = new BorderLayout();
  JScrollPane scrollPane = new JScrollPane();
	JLabel zoomLabel=new JLabel();

	/**Default constructor.*/
	public ImagePanel()
	{
		jbInit(); //initialize the user interface
		updateDisplay(); //update the labels
	}

	/**Image constructor. G***fix Automatically fits the image in a separate thread.
	@param image The image to be displayed.
	*/
	public ImagePanel(final Image image)
	{
		this(); //do the default construction
		setImage(image);  //set the image
//G***fix		imageComponent.fitImage();  //fit the image
	}

	/**Initialize the user interface.*/
  private void jbInit()
  {
		//setup the toolbar
		toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);	//G***fix with constants or utility function
    toolBar.add(getZoomInAction()); //zoom in
		toolBar.add(zoomLabel); //1X
		toolBar.add(getZoomOutAction()); //zoom out
		toolBar.addSeparator();	//--
		toolBar.add(getFitImageAction()); //fit
			//update the display when the image component zoom factor changes
		imageComponent.addPropertyChangeListener(imageComponent.ZOOM_FACTOR_PROPERTY, new PropertyChangeListener()
		{
      public void propertyChange(PropertyChangeEvent e)
      {
				updateDisplay();  //update the display when the zoom factor changes
      }
	  });
    this.setLayout(borderLayout);
    this.add(toolBar, BorderLayout.NORTH);
    this.add(scrollPane, BorderLayout.CENTER);
    scrollPane.getViewport().add(imageComponent, null);
  }

	/**Updates the user interface, such as the zoom label.*/
	protected void updateDisplay()
	{
		zoomLabel.setText(String.valueOf(Math.round(imageComponent.getZoomFactor()*100))+'%');  //update the zoom percentage on the label
//G***del		zoomLabel.setText(String.valueOf(MathUtilities.round(imageComponent.getZoomFactor(), 2))+'X");
	}

	/**Action for zooming in.*/
	protected class ZoomInAction extends AbstractAction
	{
		/**Default constructor.*/
		public ZoomInAction()
		{
			super("Zoom in");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Zoom In");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Increase the zoom factor.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.ZOOM_IN_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			imageComponent.setZoomFactor(imageComponent.getZoomFactor()*ZOOM_DELTA_FACTOR);  //increase the zoom factor the correct amount
//G***del		  updateDisplay(); //update the zoom label
		}
	}

	/**Action for zooming out.*/
	protected class ZoomOutAction extends AbstractAction
	{
		/**Default constructor.*/
		public ZoomOutAction()
		{
			super("Zoom out");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Zoom Out");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Decrease the zoom factor.");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.ZOOM_OUT_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			imageComponent.setZoomFactor(imageComponent.getZoomFactor()/ZOOM_DELTA_FACTOR);  //decrease the zoom factor the correct amount
//G***del		  updateDisplay(); //update the zoom label
		}
	}

	/**Action for fitting the image to the container size.*/
	protected class FitImageAction extends AbstractAction
	{
		/**Default constructor.*/
		public FitImageAction()
		{
			super("Fit image");	//create the base class G***Int
			putValue(SHORT_DESCRIPTION, "Fit image to View");	//set the short description G***Int
			putValue(LONG_DESCRIPTION, "Fit the image to the view..");	//set the long description G***Int
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));  //set the mnemonic key G***i18n
			putValue(SMALL_ICON, IconResources.getIcon(IconResources.IMAGE_SIZE_ICON_FILENAME)); //load the correct icon
		}

		/**Called when the action should be performed.
		@param e The event causing the action.
		*/
		public void actionPerformed(ActionEvent e)
		{
			imageComponent.fitImage(); //fit the image to the container
//G***del		  updateDisplay(); //update the zoom label
		}
	}
}