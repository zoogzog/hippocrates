package edu.hpc.andrey.zmask.gui.form;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;


/**
 * Toolbar of the main GUI window
 * @author Andrey 
 */

public class FormMainToolbar
{
	private JToolBar toolbar;

	private JButton buttonImportDirectory;
	private JButton buttonImportFile;
	private JButton buttonAnonymize;
	private JButton buttonPaintbrush;
	private JButton buttonPalette;
	private JButton buttonEraser;
	private JButton buttonReverse;
	private JButton buttonMove;
	private JButton buttonRefresh;
	private JButton buttonQuickSave;
	private JButton buttonNextTask;
	private JComboBox  comboboxPaintbrushSize;

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

	public JComboBox getComponentPaintbrushSize ()
	{
		return comboboxPaintbrushSize;
	}

	//----------------------------------------------------------------

	private void generateToolbar (FormMainHandler handler)
	{
		generateButtonImportDirectory(handler);
		generateButtonImportFile(handler);
		generateButtonAnonymize(handler);
		generateButtonGetNextTask(handler);
		generateButtonQuickSave(handler);

		toolbar.addSeparator();

		generateButtonPaintbrush(handler);
		generateButtonPalette(handler);
		generateButtonEraser(handler);
		generateButtonReverse(handler);
		generateButtonBrushSize(handler);

		toolbar.addSeparator();
		generateButtonMove(handler);
		generateButtonRefresh(handler);

	}

	private void generateButtonImportDirectory (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_IMPORT));

		buttonImportDirectory = new JButton(iconButton);
		buttonImportDirectory.setSize(30, 30);
		buttonImportDirectory.setToolTipText("Import directory");
		buttonImportDirectory.addActionListener(handler);
		buttonImportDirectory.setActionCommand(FormMainHandler.COMMAND_IMPORT_DIRECTORY);
		toolbar.add(buttonImportDirectory);
	}

	private void generateButtonImportFile (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_IMPORT_EDB));

		buttonImportFile = new JButton(iconButton);
		buttonImportFile.setSize(30, 30);
		buttonImportFile.setToolTipText("Import file");
		buttonImportFile.addActionListener(handler);
		buttonImportFile.setActionCommand(FormMainHandler.COMMAND_IMPORT_FILE);
		toolbar.add( buttonImportFile);
	}

	private void generateButtonAnonymize (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_ANONYMIZE));

		buttonAnonymize = new JButton(iconButton);
		buttonAnonymize.setSize(30, 30);
		buttonAnonymize.setToolTipText("Anonymize");
		buttonAnonymize.setActionCommand(FormMainHandler.COMMAND_ANONYMIZE_SET);
		buttonAnonymize.addActionListener(handler);

		toolbar.add(buttonAnonymize);
	}
	
	private void generateButtonGetNextTask (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_NEXTTASK));

		buttonNextTask = new JButton(iconButton);
		buttonNextTask.setSize(30, 30);
		buttonNextTask.setToolTipText("Get next task");
		buttonNextTask.setActionCommand(FormMainHandler.COMMAND_GETNEXTTASK);
		buttonNextTask.addActionListener(handler);

		toolbar.add(buttonNextTask);
	}
	
	private void generateButtonQuickSave (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_QUICKSAVE));
		
		buttonQuickSave = new JButton(iconButton);
		buttonQuickSave.setSize(30, 30);
		buttonQuickSave.setToolTipText("Save mask");
		buttonQuickSave.setActionCommand(FormMainHandler.COMMAND_QUICKSAVE);
		buttonQuickSave.addActionListener(handler);
		
		toolbar.add(buttonQuickSave);
	}

	private void generateButtonPaintbrush (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_PAINTBRUSH));

		buttonPaintbrush = new JButton(iconButton);
		buttonPaintbrush.setSize(30, 30);
		buttonPaintbrush.setToolTipText("Paintbrush");
		buttonPaintbrush.setActionCommand(FormMainHandler.COMMAND_ACTIVATE_PAINTBRUSH);
		buttonPaintbrush.addActionListener(handler);

		toolbar.add(buttonPaintbrush);

	}


	private void generateButtonReverse (FormMainHandler handler)
	{
		ImageIcon iconReverse = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_REVERSE));

		buttonReverse = new JButton(iconReverse);
		buttonReverse.setSize(30, 30);
		buttonReverse.setToolTipText("Undo");
		buttonReverse.setActionCommand(FormMainHandler.COMMAND_PAINTBRUSH_REVERSE);
		buttonReverse.addActionListener(handler);

		toolbar.add(buttonReverse);
	}

	private void generateButtonEraser (FormMainHandler handler)
	{
		ImageIcon iconEraser = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_ERASER));

		buttonEraser = new JButton(iconEraser);
		buttonEraser.setSize(30, 30);
		buttonEraser.setToolTipText("Erase all");
		buttonEraser.setActionCommand(FormMainHandler.COMMAND_PAINTBRUSH_ERASER);
		buttonEraser.addActionListener(handler);

		toolbar.add(buttonEraser);
	}

	private void generateButtonPalette (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_PALETTE));

		buttonPalette = new JButton(iconButton);
		buttonPalette.setSize(30, 30);
		buttonPalette.setToolTipText("Palette");
		buttonPalette.setActionCommand(FormMainHandler.COMMAND_PALETTE_SHOW);
		buttonPalette.addActionListener(handler);

		toolbar.add(buttonPalette);
	}

	private void generateButtonMove (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_MOVE));

		buttonMove = new JButton(iconButton);
		buttonMove.setSize(30, 30);
		buttonMove.setToolTipText("Move");
		buttonMove.setActionCommand(FormMainHandler.COMMAND_ACTIVATE_MOVE);
		buttonMove.addActionListener(handler);

		toolbar.add(buttonMove);
	}

	private void generateButtonBrushSize (FormMainHandler handler)
	{
		comboboxPaintbrushSize = new JComboBox<>();

		comboboxPaintbrushSize.setSize(50, 30);
		comboboxPaintbrushSize.setMinimumSize(new Dimension(50, 30));
		comboboxPaintbrushSize.setMaximumSize(new Dimension(50, 30));
		comboboxPaintbrushSize.addActionListener(handler);
		comboboxPaintbrushSize.setActionCommand(FormMainHandler.COMMAND_PAINTBRUSH_SIZE);

		int[] sizeValue = {10, 12, 14, 16, 18, 20, 24, 30, 50, 60};

		for (int i = 0; i < sizeValue.length; i++)
		{
			comboboxPaintbrushSize.addItem(sizeValue[i]);
		}

		toolbar.add(comboboxPaintbrushSize);
	}

	private void generateButtonRefresh (FormMainHandler handler)
	{
		ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_REFRESH));

		buttonRefresh = new JButton(iconButton);
		buttonRefresh.setSize(30, 30);
		buttonRefresh.setToolTipText("Reset view");
		buttonRefresh.setActionCommand(FormMainHandler.COMMAND_VIEW_REFRESH);
		buttonRefresh.addActionListener(handler);

		toolbar.add(buttonRefresh);
	}

	//----------------------------------------------------------------

	public void switchButtonPatinbrush (boolean isActive)
	{
		//---- Display different icon if paintbrush mode is active
		if (isActive)
		{
			ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_PAINTBRUSH_ACTIVE));

			buttonPaintbrush.setIcon(iconButton);
		}
		else
		{
			ImageIcon iconButton = new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_PAINTBRUSH));

			buttonPaintbrush.setIcon(iconButton);

		}
	}

	public void switchButtonMove (boolean isActive)
	{
		//---- Display different icon if the mode is image moving
		if (isActive)
		{
			ImageIcon iconButton =  new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_MOVE_ACTIVE));

			buttonMove.setIcon(iconButton);
		}
		else
		{
			ImageIcon iconButton =  new ImageIcon (getClass().getResource(FormSettings.RESOURCE_ICON_PATH_MOVE));

			buttonMove.setIcon(iconButton);
		}
	}
	
	//----------------------------------------------------------------	
	
	public void switchBlockON ()
	{
		buttonImportDirectory.setEnabled(false);
		buttonImportFile.setEnabled(false);
		buttonAnonymize.setEnabled(false);
	}
	
	public void switchBlockOFF ()
	{
		buttonImportDirectory.setEnabled(true);
		buttonImportFile.setEnabled(true);
		buttonAnonymize.setEnabled(true);	
	}
	
}
