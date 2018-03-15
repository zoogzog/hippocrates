package edu.hpc.andrey.zmask.gui.panel;

import edu.hpc.andrey.zmask.gui.panel.PanelStatusBar;

public class TaskProgressController 
{
	public static PanelStatusBar linkPanelStatusBar = null;
	
	private static boolean isBlockON = false;
	
	//----------------------------------------------------------------

	/**
	 * Binds a status panel bar to the controller.
	 * @param statusBar
	 */
	public static void establishLink (PanelStatusBar statusBar)
	{
		linkPanelStatusBar = statusBar;
	}

	//----------------------------------------------------------------
	
	public static void statusLaunch ()
	{
		if (linkPanelStatusBar == null) { return; }
		if (isBlockON)  { return; }
		linkPanelStatusBar.setIsDisplayAnimation(true);
	}
	
	public static void statusReset ()
	{
		if (linkPanelStatusBar == null) { return; }
		if (isBlockON)  { return; }
		linkPanelStatusBar.reset();
		linkPanelStatusBar.setTickCount(0);
	}
	
	public static void statusUpdate ()
	{
		if (linkPanelStatusBar == null) { return; }
		if (isBlockON)  { return; }
		linkPanelStatusBar.doTick();
	}
	
	public static void statusUpdate (String message)
	{
		if (linkPanelStatusBar == null) { return; }
		if (isBlockON)  { return; }
		linkPanelStatusBar.doTick(message);
	}
	
	public static void statusSetTaskCount (int count)
	{
		if (linkPanelStatusBar == null) { return; }
		if (isBlockON)  { return; }
		linkPanelStatusBar.setTickCount(count);
	}
	
	public static void statusTerminate ()
	{
		if (linkPanelStatusBar == null) { return; }
		if (isBlockON)  { return; }
		linkPanelStatusBar.setIsDisplayAnimation(false);
	}

	public static void statusSwitchBlockON ()
	{
		isBlockON = true;
	}
	
	public static void statusSwitchBlockOFF ()
	{
		isBlockON = false;
	}

}