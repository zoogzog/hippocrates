package edu.hpc.andrey.dicom.anon.form;

import java.awt.Rectangle;

import edu.hpc.andrey.dicom.anon.data.FileTable;
import edu.hpc.andrey.zmask.gui.panel.TaskProgressController;

/**
 * Controller class to launch the main GUI
 * @author Andrey
 */

public class MainController
{
	private static MainController controller = null;

	//---- Main GUI form of the application and its controllers (handlers)
	private FormMain windowMain = null;
	private FormMainHandler windowMainHandler = null;

	//---- Data structures
	private FileTable fileTable = null;

	//----------------------------------------------------------------

	private MainController ()
	{
		fileTable = new FileTable();
	}

	public static MainController getInstance ()
	{
		if (controller == null) { controller = new MainController(); }

		return controller;
	}

	//----------------------------------------------------------------

	public void launchMainWindow (Rectangle screenResolution)
	{
		//---- Init handlers, create window, launch.
		if (windowMain == null) 
		{ 

			windowMainHandler = new FormMainHandler();
			windowMain = FormMain.getInstance(screenResolution, windowMainHandler); 

			windowMainHandler.linkFormMain(windowMain);
			windowMainHandler.linkDataFileList(fileTable);
			
			TaskProgressController.establishLink(windowMain.getStatusBar());
		}
	}
}
