package edu.hpc.andrey.dicom.anon.form;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;


/**
 * Main form toolbar panel
 * @author Andrey
 */

public class FormMainToolbar
{
	private JToolBar toolbar;

	private JButton buttonSelectImportSource;
	private JButton buttonSelectExportDest;
	private JButton buttonExportLaunch;

	//----------------------------------------------------------------

	public FormMainToolbar (FormMainHandler handler, int width)
	{
		toolbar = new JToolBar();
		toolbar.setLocation(0,0);
		toolbar.setSize(width, 35);
		toolbar.setFloatable(false);
		toolbar.setBorderPainted(true);
		toolbar.setRollover(true);
		toolbar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		generateToolbar(handler);
	}

	public JToolBar get()
	{
		return toolbar;
	}

	//----------------------------------------------------------------

	private void generateToolbar (FormMainHandler handler)
	{
		generateButtonSelectImportSource(handler);
		generateButtonSelectExportDest(handler);
		generateButtonExportLaunch(handler);
	}

	private void generateButtonSelectImportSource (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_IMPORT));

		buttonSelectImportSource = new JButton(iconButton);
		buttonSelectImportSource.setSize(30, 30);
		buttonSelectImportSource.setToolTipText("Path to input data");
		buttonSelectImportSource.setActionCommand(FormMainHandler.COMMAND_SELECT_IMPORT_SOURCE);
		buttonSelectImportSource.addActionListener(handler);

		toolbar.add(buttonSelectImportSource);
	}

	private void generateButtonSelectExportDest (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_EXPORT));
		
		buttonSelectExportDest = new JButton(iconButton);
		buttonSelectExportDest.setSize(30, 30);
		buttonSelectExportDest.setToolTipText("Path to output data");
		buttonSelectExportDest.setActionCommand(FormMainHandler.COMMAND_SELECT_EXPORT_DESTINATION);
		buttonSelectExportDest.addActionListener(handler);

		toolbar.add(buttonSelectExportDest);

	}

	private void generateButtonExportLaunch (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_EXPORT_LAUNCH));
		
		buttonExportLaunch = new JButton(iconButton);
		buttonExportLaunch.setSize(30, 30);
		buttonExportLaunch.setToolTipText("Export data");
		buttonExportLaunch.setActionCommand(FormMainHandler.COMMAND_EXPORT_LAUNCH);
		buttonExportLaunch.addActionListener(handler);

		toolbar.add(buttonExportLaunch);
	}
	
	//----------------------------------------------------------------
	
	public void dispose ()
	{
		
	}
}
