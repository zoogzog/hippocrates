package edu.hpc.andrey.dicom.anon.form;

import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * Main form menu
 * @author Andrey
 */

public class FormMainMenu
{
	private JMenuBar menu;

	public FormMainMenu (FormMainHandler handler)
	{
		menu = new JMenuBar();
		menu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		createMenu(handler);
	}

	//----------------------------------------------------------------

	public JMenuBar get ()
	{
		return menu;
	}

	private void createMenu (FormMainHandler handler)
	{
		menuFile(handler);
		menuRun(handler);
	}

	//----------------------------------------------------------------

	private void menuFile (FormMainHandler handler)
	{
		JMenu menuFile = new JMenu("File");
		//		menuFile.setFont(FormStyle.DEFAULT_FONT);
		menuFile.setBorder(new EmptyBorder(5, 5, 5, 5));
		menuFile.setMnemonic(KeyEvent.VK_F);
		menu.add(menuFile);

		//---- Menu for selecting import source
		JMenuItem menuImport = new JMenuItem("Select import source");
		menuImport.setBorder(new EmptyBorder(5, 5, 5, 25));
		menuImport.setActionCommand(FormMainHandler.COMMAND_SELECT_IMPORT_SOURCE);
		menuImport.addActionListener(handler);
		menuFile.add(menuImport);
		
		//---- Menu for selecting export destination
		JMenuItem menuExport = new JMenuItem("Select export destination");
		menuExport.setBorder(new EmptyBorder(5, 5, 5, 25));
		menuExport.setActionCommand(FormMainHandler.COMMAND_SELECT_EXPORT_DESTINATION);
		menuExport.addActionListener(handler);
		menuFile.add(menuExport);
	}
	
	private void menuRun (FormMainHandler handler)
	{
		JMenu menuRun = new JMenu("Run");
		menuRun.setBorder(new EmptyBorder(5, 5, 5, 5));
		menuRun.setMnemonic(KeyEvent.VK_R);
		menu.add(menuRun);
		
		JMenuItem menuRunExport = new JMenuItem("Export files");
		menuRunExport.setBorder(new EmptyBorder(5, 5, 5, 25));
		menuRun.add(menuRunExport);
	}
}
