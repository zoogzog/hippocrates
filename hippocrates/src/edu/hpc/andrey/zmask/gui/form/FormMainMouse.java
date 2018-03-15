package edu.hpc.andrey.zmask.gui.form;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


/**
 * Mouse movement handler for the main GUI form of the application.
 * @author Andrey
 *
 */
public class FormMainMouse implements MouseMotionListener, MouseWheelListener, MouseListener
{
	public final static double DEFAULT_ZOOM_DELTA = 0.05;
	public final static double DEFAULT_ZOOM_MAX = 5;
	public final static double DEFAULT_ZOOM_MIN = 0.1;
	
	public static final int MOUSE_MODE_MOVEIMG = 1;
	public static final int MOUSE_MODE_PAINBRUSH = 2;
	
	private int mouseMode = MOUSE_MODE_MOVEIMG;

	//----------------------------------------------------------------

	//---- Form to control 
	private FormMain mainFormLink;

	private Point pointStart = new Point(-1, -1);
	private Point handlerMouseListenerPoint = new Point(-1, -1);


	//---- Current scale of the displayed image
	public static double imageViewZoomScale = 1.0;

	//----------------------------------------------------------------

	public FormMainMouse ()
	{

	}

	public void init (FormMain mainForm)
	{
		mainFormLink = mainForm;
	}

	//----------------------------------------------------------------

	@Override
	/**
	 * Handler for mouse dragging motion. If currently sample selection is turned on, then
	 * allow user to chose the area of the sample. If sample selection is turned off, but moving
	 * sample is turned on, then move the sample. Otherwise, move the entire image.
	 */
	public void mouseDragged(MouseEvent e) 
	{

		switch (mouseMode)
		{
		case MOUSE_MODE_MOVEIMG: selectModeMouseMove(e.getPoint()); break;
		case MOUSE_MODE_PAINBRUSH: selectModePaintbrush(e.getPoint()); break;
		
		
		}
	}

	@Override
	/**
	 * Responds to mouse moving. Displays coordinates of the mouse pointer, changes cursors if it is in the are 
	 * of a movable sample.
	 */
	public void mouseMoved(MouseEvent e) 
	{
		pointStart = e.getPoint();


			Point pointToCheck = mainFormLink.getComponentPanelCenter().getComponentPanelImageView().getRealPoint(pointStart);


			mainFormLink.getComponentPanelCenter().getComponentPanelImageView().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		

	}

	@Override
	/**
	 * Responds to moving the mouse wheel. Zooms out or in the displayed picture.
	 */
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		int mouseWheelDirection = e.getWheelRotation();

		if (mouseWheelDirection < 0)
		{
			//---- ZoomOut

			if (imageViewZoomScale - DEFAULT_ZOOM_DELTA > DEFAULT_ZOOM_MIN)
			{
				double exportZoom = mainFormLink.getComponentPanelCenter().getComponentPanelImageView().transformZoom(-DEFAULT_ZOOM_DELTA);
				mainFormLink.getComponentPanelCenter().getComponentPanelImageView().repaint();

				imageViewZoomScale = exportZoom;

			}

		}
		else
		{
			//---- ZoomIn

			if (imageViewZoomScale + DEFAULT_ZOOM_DELTA < DEFAULT_ZOOM_MAX)
			{
				double exportZoom = mainFormLink.getComponentPanelCenter().getComponentPanelImageView().transformZoom(+DEFAULT_ZOOM_DELTA);
				mainFormLink.getComponentPanelCenter().getComponentPanelImageView().repaint();

				imageViewZoomScale = exportZoom;

			}

		}
	}

	//----------------------------------------------------------------

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		Point pt = e.getPoint();

		Point realPointStart = mainFormLink.getComponentPanelCenter().getComponentPanelImageView().getRealPoint(pt);

		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().maskAddPoint(realPointStart);
		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().maskAddPoint(realPointStart);

		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().displayImage();
		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().repaint();

	}

	@Override 
	public void mouseEntered(MouseEvent e) 
	{

	}

	@Override
	public void mouseExited(MouseEvent e) 
	{ 

	}

	//----------------------------------------------------------------

	@Override
	public void mousePressed(MouseEvent e) 
	{

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{

	}
	
	//----------------------------------------------------------------
	
	private void selectModeMouseMove (Point ptr)
	{
		int moveX = ptr.x - handlerMouseListenerPoint.x;
		int moveY = ptr.y - handlerMouseListenerPoint.y;

		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().transformMove(moveX, moveY);
		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().repaint();

		handlerMouseListenerPoint = ptr;
	}
	
	private void selectModePaintbrush (Point ptr)
	{
		Point realPointStart = mainFormLink.getComponentPanelCenter().getComponentPanelImageView().getRealPoint(pointStart);
		Point realPointEnd = mainFormLink.getComponentPanelCenter().getComponentPanelImageView().getRealPoint(ptr);


		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().maskAddPoint(realPointStart);
		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().maskAddPoint(realPointEnd);

		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().displayImage();
		mainFormLink.getComponentPanelCenter().getComponentPanelImageView().repaint();

		pointStart = ptr;	
	}

	public void switchMouseMode (int mode)
	{
		mouseMode = mode;
	}
}
