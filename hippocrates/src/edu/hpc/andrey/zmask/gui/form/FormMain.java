package edu.hpc.andrey.zmask.gui.form;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * JFrame based class to launching the main GUI of the app
 * @author Andrey
 *
 */
public class FormMain 
{
	private static final String DEFAULT_WINDOW_TITLE = "HIPPOCRATES 1.1.4";
	private static final String DEFAULT_WINDOW_ICON = FormSettings.RESOURCE_ICON_PATH_MAIN;

	private static final int DEFAULT_WINDOW_WIDTH = 800;
	private static final int DEFAULT_WINDOW_HEIGHT = 600;

	private boolean isFullScreen = false;

	//----------------------------------------------------------------
	//---- Environment variables

	private static FormMain instance = null;

	private JFrame frameMain;

	private int windowStartX = 0;
	private int windowStartY = 0;
	private int windowWidth = 0;
	private int windowHeight = 0;

	//----------------------------------------------------------------
	//---- Components 

	private FormMainMenu menuBar = null;
	private FormMainToolbar toolbar = null;

	private FormMainPanelLeft panelLeft = null;
	private FormMainPanelCenter panelCenter = null;
	private FormMainPanelDown panelDown = null;

	//----------------------------------------------------------------
	
	private JPanel panelMain = null;

	private FormMain (Rectangle screenResolution, FormMainHandler handler, FormMainMouse handlerMouse)
	{
		panelLeft = new FormMainPanelLeft(handler);
		panelCenter = new FormMainPanelCenter(handlerMouse);
		panelDown = new FormMainPanelDown();
		
		
		if (isFullScreen)
		{
			windowStartX = screenResolution.x;
			windowStartY = screenResolution.y;
			windowWidth = screenResolution.width;
			windowHeight = screenResolution.height;
		}
		else
		{
			windowStartX = (screenResolution.width - DEFAULT_WINDOW_WIDTH) / 2;
			windowStartY = (screenResolution.height - DEFAULT_WINDOW_HEIGHT) / 2;
			windowWidth = DEFAULT_WINDOW_WIDTH;
			windowHeight = DEFAULT_WINDOW_HEIGHT;
		}

		menuBar = new FormMainMenu(handler);
		toolbar = new FormMainToolbar(handler, DEFAULT_WINDOW_WIDTH);


		panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());
		addComponents(panelMain);

		panelMain.add(toolbar.get(), BorderLayout.PAGE_START);

		//---- Finalize creation of the main window
		frameMain = new JFrame();
		frameMain.setTitle(DEFAULT_WINDOW_TITLE);
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMain.setJMenuBar(menuBar.get());
		frameMain.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(DEFAULT_WINDOW_ICON)));
		//frameMain.getContentPane().setBackground(FormStyle.COLOR_PANEL);
		frameMain.setSize(windowWidth, windowHeight);
		frameMain.setLocation(windowStartX, windowStartY);
		frameMain.setContentPane(panelMain);

		frameMain.setVisible(true);
	}

	public static FormMain getInstance (Rectangle screenResolution, FormMainHandler handler, FormMainMouse handlerMouse)
	{
		if (instance == null) { instance = new FormMain(screenResolution, handler, handlerMouse); }

		
		return instance;
	}

	//----------------------------------------------------------------
	
	private void addComponents (JPanel panel)
	{
		panel.add(toolbar.get(), BorderLayout.PAGE_START);
		panel.add(panelLeft.get(), BorderLayout.LINE_START);
		panel.add(panelCenter.get(), BorderLayout.CENTER);
		panel.add(panelDown.get(), BorderLayout.PAGE_END);
	}
	
	//----------------------------------------------------------------
	
	public JFrame getComponentFrame ()
	{
		return frameMain;
	}
	
	public FormMainMenu getComponentMainMenu ()
	{
		return menuBar;
	}
	
	public FormMainToolbar getComponentToolbar ()
	{
		return toolbar;
	}
	
	public FormMainPanelLeft getComponentPanelLeft ()
	{
		return panelLeft;
	}

	public FormMainPanelCenter getComponentPanelCenter ()
	{
		return panelCenter;
	}
	
	public FormMainPanelDown getComponentPanelDown ()
	{
		return panelDown;
	}
	
	public JFrame getFrameMain ()
	{
		return frameMain;
	}

	
	//----------------------------------------------------------------
	
	public void resetState ()
	{
		panelLeft.resetState();
		
		panelCenter.resetState();
	}

}
