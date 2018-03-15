package edu.hpc.andrey.zmask.controller;

import java.awt.Rectangle;

import edu.hpc.andrey.zmask.data.DatabaseTable;
import edu.hpc.andrey.zmask.gui.form.FormMain;
import edu.hpc.andrey.zmask.gui.form.FormMainHandler;
import edu.hpc.andrey.zmask.gui.form.FormMainMouse;
import edu.hpc.andrey.zmask.gui.panel.TaskProgressController;


public class MonetMainController 
{
	private static MonetMainController instance = null;
	
	//----------------------------------------------------------------
	
	private FormMain windowMain = null;
	private FormMainHandler windowMainHandler = null;
	private FormMainMouse windowMainMouseHandler = null;
	
	private DatabaseTable dbtbl = null;
	
	//----------------------------------------------------------------
	
	public static MonetMainController getInstance ()
	{
		if (instance == null) 
		{
			instance = new MonetMainController ();
		}
		
		return instance;
	}
	
	private MonetMainController ()
	{
		
	}
	
	//----------------------------------------------------------------
	
	public void launchMainWindowGUI (Rectangle screenResolution)
	{
		if (windowMain == null) 
		{ 	
			dbtbl = new DatabaseTable();
			
			windowMainHandler = new FormMainHandler();
			windowMainMouseHandler = new FormMainMouse();
			windowMain = FormMain.getInstance(screenResolution, windowMainHandler, windowMainMouseHandler); 

			windowMainHandler.linkFormMain(windowMain);
			windowMainHandler.linkPatientDatabase(dbtbl);
			windowMainHandler.linkMouseHandler(windowMainMouseHandler);
			
			windowMainMouseHandler.init(windowMain);

			
			TaskProgressController.establishLink(windowMain.getComponentPanelDown().getComponentStatusBar());
		}
	}
}
