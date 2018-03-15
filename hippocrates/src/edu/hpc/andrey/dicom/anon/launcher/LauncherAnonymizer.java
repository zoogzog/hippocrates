package edu.hpc.andrey.dicom.anon.launcher;

import java.awt.Rectangle;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import edu.hpc.andrey.dicom.anon.form.FormMain;
import edu.hpc.andrey.dicom.anon.form.FormMainHandler;
import edu.hpc.andrey.dicom.anon.form.MainController;

/**
 * Main entry point of the application
 * @author ndrey
 */

public class LauncherAnonymizer 
{
	@SuppressWarnings("unused")
	private static MainController controller = null;

	//---- Main GUI form of the application and its controllers (handlers)
	@SuppressWarnings("unused")
	private FormMain windowMain = null;
	@SuppressWarnings("unused")
	private FormMainHandler windowMainHandler = null;
	
	//---- Put classes for handling data here
	//private Database database = null;
	
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

		MainController controller = MainController.getInstance();
		controller.launchMainWindow(screenResolution);

	}
}
