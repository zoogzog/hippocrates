package edu.hpc.andrey.dicom.anon.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import edu.hpc.andrey.dicom.anon.data.FileTable;
import edu.hpc.andrey.dicom.anon.task.TaskBackground;


/**
 * Button handler for the GUI 
 * @author Andrey
 */

public class FormMainHandler implements ActionListener
{
	//---- Command list, handled by this handler
	public static final String COMMAND_SELECT_IMPORT_SOURCE = "cmdSelectorImport";
	public static final String COMMAND_SELECT_EXPORT_DESTINATION = "cmdSelectorExport";
	public static final String COMMAND_EXPORT_LAUNCH = "cmdLaunchExport";


	//----------------------------------------------------------------

	//---- Form to control 
	private FormMain linkFormMain = null;

	//---- Link to the data structures
	private FileTable linkDataFileList = null;

	//----------------------------------------------------------------

	public FormMainHandler ()
	{

	}

	public void linkFormMain (FormMain link)
	{
		linkFormMain = link;
	}

	public void linkDataFileList (FileTable link)
	{
		linkDataFileList = link;
	}

	//----------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent source)
	{
		if (linkFormMain != null && linkDataFileList != null)
		{
			String commandCodeName = source.getActionCommand();

			switch (commandCodeName)
			{
			case COMMAND_SELECT_IMPORT_SOURCE: 			actionSelectImportSource(); 		return;
			case COMMAND_SELECT_EXPORT_DESTINATION: 	actionSelectExportDestination();  	return;
			case COMMAND_EXPORT_LAUNCH: 				actionExportLaunch(); 				return;

			}
		}
	}

	/**
	 * Show file chooser dialog for specifying file or folder to import data from.
	 */
	private void actionSelectImportSource ()
	{
		try
		{
			JFileChooser fileChooserDriver = new JFileChooser();
			fileChooserDriver.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

			if (isFileSelected == JFileChooser.APPROVE_OPTION)
			{
				linkDataFileList.reset();
				
				String filePath = fileChooserDriver.getSelectedFile().getPath();
				
				linkFormMain.getComponentMainPanel().getComponentTFImportData().setText(filePath);
			}
		}
		catch (Exception e) {}
	}

	/**
	 * Show file chooser dialog for specifying folder, where exported data will be put
	 */
	private void actionSelectExportDestination ()
	{
		try
		{
			JFileChooser fileChooserDriver = new JFileChooser();
			fileChooserDriver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

			if (isFileSelected == JFileChooser.APPROVE_OPTION)
			{
				String filePath = fileChooserDriver.getSelectedFile().getPath();
							
				linkFormMain.getComponentMainPanel().getComponentTFExportData().setText(filePath);
			}
		}
		catch (Exception e) {}
	}

	private void actionExportLaunch ()
	{
		String pathImport = linkFormMain.getComponentMainPanel().getComponentTFImportData().getText();
		String pathExport = linkFormMain.getComponentMainPanel().getComponentTFExportData().getText();
		
		
		//---- Show a message to the user, explaining that import and export paths 
		//---- have to be selected
		if (pathImport.equals("") ||  pathExport.equals("")) 
		{ 
			JOptionPane.showMessageDialog(linkFormMain.getFrameMain(), "Select import and export paths!", "", JOptionPane.ERROR_MESSAGE);
			return; 
		}
		
		//---- No export formats were selected
		if (!linkFormMain.getComponentMainPanel().getComponeCBDicomFile().isSelected() &&
			!linkFormMain.getComponentMainPanel().getComponentCBBinaryImage().isSelected() &&
			!linkFormMain.getComponentMainPanel().getComponentCBBitmapImage().isSelected() &&
			!linkFormMain.getComponentMainPanel().getComponentCBDicomAttributes().isSelected() &&
			!linkFormMain.getComponentMainPanel().getComponentCBDataRaw().isSelected())
		{
			JOptionPane.showMessageDialog(linkFormMain.getFrameMain(), "Select at least one export format!", "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//---- Launch exporter
		TaskBackground taskDriver = new TaskBackground();
		
		taskDriver.setLinkData(linkDataFileList);
		
		//---- Set export settings
		
		//---- Export settings: is export dicom attributes
		if (linkFormMain.getComponentMainPanel().getComponentCBDicomAttributes().isSelected()) 
		{ taskDriver.setFlagExportDicomTags(true); } else { taskDriver.setFlagExportDicomTags(false); }
		
		//---- Export settings: is export image data as binary
		if (linkFormMain.getComponentMainPanel().getComponentCBBinaryImage().isSelected())
		{ taskDriver.setFlagExportImageBinary(true); } else { taskDriver.setFlagExportImageBinary(false); }
		
		//---- Export settings: is export image data as a bitmap file
		if (linkFormMain.getComponentMainPanel().getComponentCBBitmapImage().isSelected())
		{ taskDriver.setFlagExportImageBitmap(true); } else { taskDriver.setFlagExportImageBitmap(false); }
		
		//---- Export settings: is export patient's data in a separate file
		if (linkFormMain.getComponentMainPanel().getComponeCBDicomFile().isSelected())
		{ taskDriver.setFlagExportDicom(true); } else { taskDriver.setFlagExportDicom(false); }
		
		//---- Export settings: is export raw image data
		if (linkFormMain.getComponentMainPanel().getComponentCBDataRaw().isSelected())
		{ taskDriver.setFlagExportRawData(true); } else { taskDriver.setFlagExportRawData(false); }
		
		taskDriver.setPathImport(pathImport);
		taskDriver.setPathExport(pathExport);
		
		taskDriver.execute();
	}
}
