package edu.hpc.andrey.zmask.launcher;

import java.awt.Rectangle;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import edu.hpc.andrey.zmask.controller.MonetMainController;

/**
 * This class launches the GUI of the Monet application. Monet is designed to perform computer aided diagnosis
 * of lung cancer patients from CT images. Viewing, performing analysis with a neural network and observing 
 * 3D structure is possible with this application.
 * @author Andrey
 */
public class MainLauncher 
{
	public static void main (String[] args)
	{
		//---- Load opencv libraries
		System.loadLibrary("opencv_java310");

		//---- Launch the main GUI window
		SwingUtilities.invokeLater(new Runnable() {public void run() {launch();}});
		
	}
	
	public static void launch () 
	{
		//---- Set default fonts for the application
		UIManager.getLookAndFeelDefaults().put("defaultFont", new java.awt.Font("Times New Roman", 0, 12));

		//---- Grab the maximum screen resolution
		Rectangle screenResolution = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

		//---- Launch the application
		MonetMainController controller = MonetMainController.getInstance();
		controller.launchMainWindowGUI(screenResolution);
	}
}
