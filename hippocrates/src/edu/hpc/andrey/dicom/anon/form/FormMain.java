package edu.hpc.andrey.dicom.anon.form;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.hpc.andrey.zmask.gui.panel.PanelStatusBar;

/**
 * GUI - main JFrame class
 * @author Andrey
 */

public class FormMain
{
	private static final String DEFAULT_WINDOW_TITLE = "HIPPOCRATES 1.1.4";
	private static final String DEFAULT_WINDOW_ICON = FormSettings.RESOURCE_ICON_PATH_MAIN;

	private static final int DEFAULT_WINDOW_WIDTH = 600;
	private static final int DEFAULT_WINDOW_HEIGHT = 280;

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
	private FormMainPanel panelMainField = null;
	private PanelStatusBar panelStatus = null;


	//----------------------------------------------------------------

	private FormMain (Rectangle screenResolution, FormMainHandler handler)
	{
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
		panelMainField = new FormMainPanel(handler);


		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BorderLayout());

		panelMain.add(toolbar.get(), BorderLayout.PAGE_START);
		panelMain.add(panelMainField.getPanel(), BorderLayout.CENTER);
		
		JPanel panelProgressBarLocation = new JPanel();
		panelStatus= new PanelStatusBar(panelProgressBarLocation, FormSettings.RESOURCE_ICO_PATH_IDLE, FormSettings.RESOURCE_ICO_PATH_PROCESSING);
		panelMain.add(panelProgressBarLocation, BorderLayout.PAGE_END);


		//---- Finalize creation of the main window
		frameMain = new JFrame();
		frameMain.setTitle(DEFAULT_WINDOW_TITLE);
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//FIXME sometimes Exception occurs on exit
		frameMain.setJMenuBar(menuBar.get());
		frameMain.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(DEFAULT_WINDOW_ICON)));
		//frameMain.getContentPane().setBackground(FormStyle.COLOR_PANEL);
		frameMain.setSize(windowWidth, windowHeight);
		frameMain.setLocation(windowStartX, windowStartY);
		frameMain.setContentPane(panelMain);

		frameMain.setVisible(true);
	}

	public static FormMain getInstance (Rectangle screenResolution, FormMainHandler handler)
	{
		if (instance == null) { instance = new FormMain(screenResolution, handler); }

		return instance;
	}

	//----------------------------------------------------------------

	public FormMainPanel getComponentMainPanel ()
	{
		return panelMainField;
	}
	
	public FormMainMenu getComponentMainMenu ()
	{
		return menuBar;
	}
	
	public FormMainToolbar getComponentToolbar ()
	{
		return toolbar;
	}
	
	public PanelStatusBar getStatusBar ()
	{
		return panelStatus;
	}

	public JFrame getFrameMain ()
	{
		return frameMain;
	}
	
	//----------------------------------------------------------------
	
	
}
