package edu.hpc.andrey.zmask.gui.form;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;


/**
 *
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
		menuView(handler);
	}

	//----------------------------------------------------------------

	private void menuFile (FormMainHandler handler)
	{
		JMenu menuFile = new JMenu("File");
		menuFile.setBorder(new EmptyBorder(5, 5, 5, 5));
		menu.add(menuFile);
		
		
		//-------- NEW PROJECT
		
		JMenuItem menuNewProject = new JMenuItem("New ");
		menuNewProject.setBorder(new EmptyBorder(5, 5, 5, 5));
		menuNewProject.addActionListener(handler);
		menuNewProject.setActionCommand(FormMainHandler.COMMAND_NEWPROJECT);
		menuFile.add(menuNewProject);
		
		menuFile.addSeparator();
		
		//--------- IMPORT 
		
		Dimension DIMENSION_DEFAULT = new Dimension(150, menuNewProject.getPreferredSize().height);
		
		JMenu menuImport = new JMenu("Import");
		menuImport.setPreferredSize(DIMENSION_DEFAULT);
		menuFile.add(menuImport);
		
		JMenuItem menuImportFile = new JMenuItem ("Dicom file");
		menuImportFile.setPreferredSize(DIMENSION_DEFAULT);
		menuImportFile.addActionListener(handler);
		menuImportFile.setActionCommand(FormMainHandler.COMMAND_IMPORT_FILE);
		menuImport.add(menuImportFile);
		
		JMenuItem menuImportDirectory = new JMenuItem("From driectory");
		menuImportDirectory.setPreferredSize(DIMENSION_DEFAULT);
		menuImportDirectory.addActionListener(handler);
		menuImportDirectory.setActionCommand(FormMainHandler.COMMAND_IMPORT_DIRECTORY);
		menuImport.add(menuImportDirectory);
		
		JMenuItem menuImportDataset = new JMenuItem("From dataset");
		menuImportDataset.setPreferredSize(DIMENSION_DEFAULT);
		menuImportDataset.addActionListener(handler);
		menuImportDataset.setActionCommand(FormMainHandler.COMMAND_IMPORT_DATASET);
		menuImport.add(menuImportDataset);
		
		JMenuItem menuImportDatabaseFile = new JMenuItem("From database file");
		menuImportDatabaseFile.setPreferredSize(DIMENSION_DEFAULT);
		menuImportDatabaseFile.addActionListener(handler);
		menuImportDatabaseFile.setActionCommand(FormMainHandler.COMMAND_IMPORT_DATABASEFILE);
		menuImport.add(menuImportDatabaseFile);
		
		//--------- EXPORT
		
		JMenu menuExport = new JMenu("Export");
		menuExport.setPreferredSize(DIMENSION_DEFAULT);
		menuFile.add(menuExport);
		
		JMenuItem menuExportDatabaseFile = new JMenuItem ("Database file");
		menuExportDatabaseFile.setPreferredSize(DIMENSION_DEFAULT);
		menuExportDatabaseFile.addActionListener(handler);
		menuExportDatabaseFile.setActionCommand(FormMainHandler.COMMAND_EXPORT_DATABASEFILE);
		menuExport.add(menuExportDatabaseFile);
		
		//------- Save image
		
		JMenu menuSaveImage = new JMenu("Save image");
		menuSaveImage.setPreferredSize(DIMENSION_DEFAULT);
		menuFile.add(menuSaveImage);
		
		JMenuItem menuSaveImageOriginal = new JMenuItem ("Original image");
		menuSaveImageOriginal.setPreferredSize(DIMENSION_DEFAULT);
		menuSaveImageOriginal.addActionListener(handler);
		menuSaveImageOriginal.setActionCommand(FormMainHandler.COMMAND_SAVE_IMAGE_ORIGINAL);
		menuSaveImage.add(menuSaveImageOriginal);
		
		JMenuItem menuSaveImageTransformed = new JMenuItem("Transformed image");
		menuSaveImageTransformed.setPreferredSize(DIMENSION_DEFAULT);
		menuSaveImageTransformed.addActionListener(handler);
		menuSaveImageTransformed.setActionCommand(FormMainHandler.COMMAND_SAVE_IMAGE_TRANSFORMED);
		menuSaveImage.add(menuSaveImageTransformed);
		
		JMenuItem menuSaveImageMask = new JMenuItem("Image mask");
		menuSaveImageMask.setPreferredSize(DIMENSION_DEFAULT);
		menuSaveImageMask.addActionListener(handler);
		menuSaveImageMask.setActionCommand(FormMainHandler.COMMAND_SAVE_MASK);
		menuSaveImage.add(menuSaveImageMask);
		
		
		
		
	}
	
	private void menuView (FormMainHandler handler)
	{
		JMenu menuView = new JMenu("View");
		menuView.setBorder(new EmptyBorder(5, 5, 5, 5));
		menu.add(menuView);
		
		JMenuItem menuViewReset = new JMenuItem("Reset transform");
		menuViewReset.setPreferredSize(new Dimension(150, menuView.getPreferredSize().height));
		menuViewReset.setActionCommand(FormMainHandler.COMMAND_VIEW_REFRESH);
		menuViewReset.addActionListener(handler);
		menuView.add(menuViewReset);
	}
	
	private void menuRun (FormMainHandler handler)
	{
		JMenu menuRun = new JMenu("Run");
		menuRun.setBorder(new EmptyBorder(5, 5, 5, 5));
		menu.add(menuRun);
		
		JMenuItem menuAnonymData = new JMenuItem ("Anonymize data");
		menuAnonymData.setPreferredSize(new Dimension(150, menuAnonymData.getPreferredSize().height));
		menuAnonymData.setActionCommand(FormMainHandler.COMMAND_ANONYMIZE_SET);
		menuAnonymData.addActionListener(handler);
		menuRun.add(menuAnonymData);
		
		
	}
	
}
