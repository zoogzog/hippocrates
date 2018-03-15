package edu.hpc.andrey.zmask.gui.form;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Mat;

import edu.hpc.andrey.dicom.core.DicomContainer;
import edu.hpc.andrey.dicom.core.DicomDecoder;
import edu.hpc.andrey.dicom.core.DicomDictionary;
import edu.hpc.andrey.dicom.core.DicomFile;
import edu.hpc.andrey.utils.ImageTransferDriver;
import edu.hpc.andrey.utils.OpencvConverter;
import edu.hpc.andrey.zmask.cache.ImageCache;
import edu.hpc.andrey.zmask.controller.BackgroundController;
import edu.hpc.andrey.zmask.controller.ExportController;
import edu.hpc.andrey.zmask.controller.BackgroundController.BackgroundTaskType;
import edu.hpc.andrey.zmask.data.DatabaseController;
import edu.hpc.andrey.zmask.data.DatabaseTable;
import edu.hpc.andrey.zmask.debugger.Debugger;

/**
 * Main GUI handler
 * @author Andrey
 */

public class FormMainHandler implements ActionListener, ChangeListener
{
	public static final String COMMAND_NEWPROJECT = "cmd-new-project";
	public static final String COMMAND_IMPORT_FILE = "cmd-import-file";
	public static final String COMMAND_IMPORT_DIRECTORY = "cmd-import-dir";
	public static final String COMMAND_IMPORT_DATASET = "cmd-import-dataset";
	public static final String COMMAND_IMPORT_DATABASEFILE = "cmd-import-dbfile";

	public static final String COMMAND_EXPORT_DATABASEFILE = "cmd-export-dbfile";

	public static final String COMMAND_CHANGED_COMBOBOX_FILEID = "cmd-chg-cmb-fid";
	public static final String COMMAND_CHANGED_COMBOBOX_DIRECTORY = "cmd-chg-cmb-dir";
	public static final String COMMAND_CHANGED_COMBOBOX_FILEINDIR = "cmd-chg-filedir";


	public static final String COMMAND_COMBOBOX_CH_SLIDE = "cmd_cmb_slider";
	public static final String COMMAND_COMBOBOX_LUT = "cmd_color_lut";
	public static final String COMMAND_RESET_IMAGE_TRANSFORM = "cmd_reset_imgtr";
	public static final String COMMAND_ANONYMIZE_SET = "cmd-anon-set";
	public static final String COMMAND_GETNEXTTASK = "cmd-get-next-task";

	//---- Image transformation related commands
	public static final String SLIDER_NAME_TRANSFORM_WINDOWWIDTH = "slider-width";
	public static final String SLIDER_NAME_TRANSFORM_WINDOWCENTER = "slider-center";
	public static final String SLIDER_NAME_TRANSFORM_RESCALEINTERCEPT = "slider-rescale";
	public static final String SLIDER_NAME_TRANSFORM_RESCALESLOPE = "slider-slope";
	public static final String SLIDER_NAME_IMAGECHOOSER = "slider-imgchoose";

	//---- Paint brush related commands
	public static final String COMMAND_ACTIVATE_PAINTBRUSH = "cmd_paint";
	public static final String COMMAND_PALETTE_SHOW = "cmd_palette";
	public static final String COMMAND_PAINTBRUSH_SIZE = "cmd_paint_size";
	public static final String COMMAND_PAINTBRUSH_ERASER = "cmd-eraser";
	public static final String COMMAND_PAINTBRUSH_REVERSE = "cmd-reverse";

	public static final String COMMAND_SWITCH_OPENGL_VIEW = "cmd-view";

	public static final String COMMAND_ACTIVATE_MOVE = "cmd_move";
	public static final String COMMAND_VIEW_REFRESH = "cmd_view_refresh";

	public static final String COMMAND_SAVE_IMAGE_ORIGINAL = "cmd_save_image_or";
	public static final String COMMAND_SAVE_IMAGE_TRANSFORMED = "cmd_save_image_tr";
	public static final String COMMAND_SAVE_MASK = "cmd_save_mask";
	public static final String COMMAND_QUICKSAVE = "cmd_quicksave";

	//----------------------------------------------------------------


	private FormMain linkFormMain = null;
	private FormMainMouse linkFormMainMouse = null;
	private DatabaseTable linkDatabase = null;

	boolean isPreventLoad = false;

	boolean isApplyTransform = false;
	int[] transformTagsDefault = {0, 0, 0, 0};
	int[] transformTagsCustom = {0, 0, 0, 0};


	public enum DataExportType {EXPORT_FILE, EXPORT_STUDY, EXPORT_PATIENT, EXPORT_DATABASE, NONE};
	public DataExportType dataExportType = DataExportType.NONE;

	private DicomContainer dicomData;
	private int dicomDataMonochromeMode;

	private ImageCache cacheImage = new ImageCache();

	DicomDictionary dictionary = DicomDictionary.getInstance();

	//----------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent source) 
	{
		if (linkFormMain != null)
		{
			String commandCodeName = source.getActionCommand();

			switch (commandCodeName)
			{

			//------ ACTIONS: IMPORT
			case COMMAND_NEWPROJECT: actionNewProject(); break;
			case COMMAND_IMPORT_FILE: actionImportFile(); break;
			case COMMAND_IMPORT_DIRECTORY: actionImportDirectory(); break;
			case COMMAND_IMPORT_DATASET: actionImportDataset(); break;
			case COMMAND_IMPORT_DATABASEFILE: actionImportDatabasefile(); break;

			case COMMAND_CHANGED_COMBOBOX_DIRECTORY: helperComboboxChangedDirectory(); break;
			case COMMAND_CHANGED_COMBOBOX_FILEINDIR: helperComboboxChangedFileInDirectory(); break;

			//------ ACTIONS: EXPORT
			case COMMAND_EXPORT_DATABASEFILE: actionExportDatabasefile(); break;

			//------ ACTIONS: PROCESSING

			case COMMAND_ANONYMIZE_SET: actionAnonymizeData(); break;
			case COMMAND_GETNEXTTASK: actionGetNextTask(); break;
		
			//------ ACTIONS: IMAGE TRANSFORMATION AND VISUALIZATION 

			case COMMAND_COMBOBOX_LUT: actionComboboxLutChanged (); break;

			case COMMAND_RESET_IMAGE_TRANSFORM: sliderTransformReset(); break;

			case COMMAND_ACTIVATE_MOVE: actionActivateModeMove(); break;
			case COMMAND_VIEW_REFRESH: actionViewRefresh(); break;

			//------- ACTIONS: PAINTBRUSH
			case COMMAND_ACTIVATE_PAINTBRUSH: actionActivatModePaintbrush(); break;
			case COMMAND_PALETTE_SHOW: actionColorChooser(); break;
			case COMMAND_PAINTBRUSH_SIZE: actionPaintbrushSize(); break;
			case COMMAND_PAINTBRUSH_ERASER: actionEraser(); break;
			case COMMAND_PAINTBRUSH_REVERSE: actionPaintbrushReverse(); break;

			case COMMAND_SAVE_IMAGE_ORIGINAL: actionSaveImageOriginal(); break;
			case COMMAND_SAVE_IMAGE_TRANSFORMED: actionSaveImageTransformed(); break;
			case COMMAND_SAVE_MASK: actionSaveMask(); break;
			case COMMAND_QUICKSAVE: actionQuickSave(); break;
			}
		}
	}

	//----------------------------------------------------------------

	public void linkFormMain (FormMain link)
	{
		linkFormMain = link;
	}

	public void linkPatientDatabase (DatabaseTable link)
	{
		linkDatabase = link;
	}

	public void linkMouseHandler (FormMainMouse mouseHandler)
	{
		linkFormMainMouse = mouseHandler;
	}

	//----------------------------------------------------------------

	private void actionNewProject ()
	{
		if (linkDatabase == null) { Debugger.log("Error: Database table is NULL!"); return; }
		
		//---- Reset the database, remove all elements from the table
		DatabaseController.executeDropTables(linkDatabase);
		
		linkFormMain.resetState();
	}

	private void actionImportFile ()
	{
		if (linkDatabase == null) { Debugger.log("Error: Database table is NULL!"); return; }

		/*!!*/Debugger.log("Info [GUI]: importing file process started");

		//---- Display dialog box to allow user select a file to import it.
		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String filePath = fileChooserDriver.getSelectedFile().getPath();

			//---- Block buttons
			linkFormMain.getComponentToolbar().switchBlockON();
			
			//---- Send request to background controller to execute a task
			BackgroundController task = new BackgroundController();
			task.setupEnvironment(linkFormMain, this, linkDatabase);
			task.setupTask(BackgroundTaskType.TASK_IMPORT_FILE, new String[] {filePath});
			task.execute();
		}
	}

	private void actionImportDirectory ()
	{
		if (linkDatabase == null) { Debugger.log("Error: Database table is NULL!"); return; }

		/*!!*/Debugger.log("Info [GUI]: importing files from directory process started");

		//---- Display dialog box to allow user select a file to import it.
		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String directoryPath = fileChooserDriver.getSelectedFile().getPath();

			//---- Block buttons
			linkFormMain.getComponentToolbar().switchBlockON();
			
			//---- Send request to background controller to execute a task
			BackgroundController task = new BackgroundController();
			task.setupEnvironment(linkFormMain, this, linkDatabase);
			task.setupTask(BackgroundTaskType.TASK_IMPORT_DIRECTORY, new String[] {directoryPath});
			task.execute();

		}

	}

	private void actionImportDataset ()
	{
		if (linkDatabase == null) { Debugger.log("Error [GUI]: Database table is NULL!"); return; }

		//---- Display dialog box to allow user select a file to import it.
		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String directoryPath = fileChooserDriver.getSelectedFile().getPath();

			//---- Block buttons
			linkFormMain.getComponentToolbar().switchBlockON();
			
			//---- Send request to background controller to execute a task
			BackgroundController task = new BackgroundController();
			task.setupEnvironment(linkFormMain, this, linkDatabase);
			task.setupTask(BackgroundTaskType.TASK_IMPORT_DATASET, new String[] {directoryPath});
			task.execute();
		}


	}

	private void actionImportDatabasefile ()
	{
		if (linkDatabase == null) { Debugger.log("Error: Database table is NULL!"); return; }

		//---- Display dialog box to allow user select a file to import it.
		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String filePath = fileChooserDriver.getSelectedFile().getAbsolutePath();

			//---- Block buttons
			linkFormMain.getComponentToolbar().switchBlockON();
			
			BackgroundController task = new BackgroundController();
			task.setupEnvironment(linkFormMain, this, linkDatabase);
			task.setupTask(BackgroundTaskType.TASK_IMPORT_DATABASEFILE, new String[] {filePath});
			task.execute();
		}
	}

	private void actionExportDatabasefile ()
	{
		if (linkDatabase == null) { Debugger.log("Error: Database table is NULL!"); return; }

		//---- Display dialog box to allow user select a file to import it.
		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String filePath = fileChooserDriver.getSelectedFile().getAbsolutePath();

			//---- Block buttons
			linkFormMain.getComponentToolbar().switchBlockON();
			
			BackgroundController task = new BackgroundController();
			task.setupEnvironment(linkFormMain, this, linkDatabase);
			task.setupTask(BackgroundTaskType.TASK_EXPORT_DATABASEFILE, new String[] {filePath});
			task.execute();
		}
	}

	private void actionQuickSave ()
	{
		if (linkDatabase == null) { Debugger.log("Error: Database table is NULL!"); return; }

		int directorySelected = linkFormMain.getComponentPanelLeft().getComponentComboboxDirectory().getSelectedIndex();
		int fileSelected = linkFormMain.getComponentPanelLeft().getComponentComboboxFileInDirectory().getSelectedIndex();

		String filePath = linkDatabase.getDirectory(directorySelected).getDirectoryPath();
		String fileName = linkDatabase.getDirectory(directorySelected).getFile(fileSelected).getFileName();
		
		//---- Remove extension
		if (fileName.contains(".")) { fileName = fileName.substring(0, fileName.lastIndexOf(".")); }
		
		String fileNameOutputMask = fileName + "-mask";
	//	String fileNameOutputImage = fileName + "-img";

		int[][] mask = linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskGetData();
		Image maskImage = linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskGetImage();

	//	int lutIndex = linkFormMain.getComponentPanelLeft().getComponentComboboxTableLUT().getSelectedIndex();
		
		ExportController.exportImageMask(dicomData, cacheImage, mask,  maskImage, filePath + File.separator + fileNameOutputMask);
		
		linkDatabase.getDirectory(directorySelected).getFile(fileSelected).setIsMaskSaved(true);
	//	ExportController.exportImageDisplayed(dicomData, transformTagsCustom, lutIndex, filePath + File.separator + fileNameOutputImage);
	}

	private void actionGetNextTask ()
	{
		int[] newtask = linkDatabase.getFileNoMask();
		
		if (newtask[0] >= 0 && newtask[1] >= 0)
		{
			linkFormMain.getComponentPanelLeft().getComponentComboboxDirectory().setSelectedItem(newtask[0]);
			linkFormMain.getComponentPanelLeft().getComponentComboboxFileInDirectory().setSelectedIndex(newtask[1]);
		}
	}
	
	
	//----------------------------------------------------------------


	private void actionLoadImage ()
	{
		if (linkDatabase.getIsLoaded())
		{
			int indexDirectorySelected = linkFormMain.getComponentPanelLeft().getComponentComboboxDirectory().getSelectedIndex();
			int indexFileSelected = linkFormMain.getComponentPanelLeft().getComponentComboboxFileInDirectory().getSelectedIndex();

			if (indexDirectorySelected < 0 || indexFileSelected < 0) { return; } 

			/*!!*/Debugger.log("Info [GUI]: display image [" + indexDirectorySelected + "," + indexFileSelected + "]");

			String fullPath = linkDatabase.getFile(indexDirectorySelected, indexFileSelected).getFilePath();

			dicomData = new DicomContainer();
			DicomFile dicomFile = new DicomFile();

			DicomDecoder.convertDICOM(fullPath, dicomData, dicomFile, dictionary, false);

			dicomDataMonochromeMode = dicomData.getMonochromeMode();

			helperLoadTagData(indexDirectorySelected, indexFileSelected);

			cacheImage.upload(dicomData.getImageBMP());
			
			//---- Check if it is necessary to load the mask
			if (linkDatabase.getFile(indexDirectorySelected, indexFileSelected).getIsMaskSaved())
			{
				String pathMaskImage = "";
				
				//---- Check if the mask exists in the same folder
				if (fullPath.contains(".dcm")) { pathMaskImage = fullPath.replace(".dcm", "-mask.png"); }
				else {  pathMaskImage = fullPath + "-mask.png"; }
				
				File fileMaskImage = new File (pathMaskImage);
				
				int imgW = cacheImage.getWidth();
				int imgH = cacheImage.getHeight();
				
				//----- Check 
				if (fileMaskImage.exists())
				{
					try
					{
						BufferedImage maskImageOriginal =  ImageIO.read(fileMaskImage);
									
						linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskSetImage(OpencvConverter.resizeBufferedImage(maskImageOriginal, imgW, imgH));
						
					}
					catch (Exception e) { e.printStackTrace(); }
				}
			}
			else { linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskSetImage(null); }

			//---- Update transform tags
			sliderTransformChanged();

			//---- Display from cache to show the image of an appropriate size
			actionDisplayImageCache();
		}
	}

	private void actionDisplayImageCache ()
	{

		Mat img  = null;

		if (dicomDataMonochromeMode == 1) { img = cacheImage.getImage(transformTagsCustom[0], transformTagsCustom[1], transformTagsCustom[2], transformTagsCustom[3], true); }
		else { img = cacheImage.getImage(transformTagsCustom[0], transformTagsCustom[1], transformTagsCustom[2], transformTagsCustom[3], false); }

		//----- Applying LUT transformation
		int lutIndex = linkFormMain.getComponentPanelLeft().getComponentComboboxTableLUT().getSelectedIndex();

		if (lutIndex > 0)
		{
			ImageTransferDriver driver = new ImageTransferDriver(lutIndex - 1);
			img = driver.transferIntensity(img);
		}


		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().loadImage(OpencvConverter.convertMatToBImage(img));
		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().displayImage();
	}

	private void actionComboboxLutChanged ()
	{
		if ( linkDatabase.getIsLoaded())
		{
			actionDisplayImageCache ();
		}
	}

	private void actionColorChooser ()
	{
		Color newColor = JColorChooser.showDialog(null, "Choose a color", Color.red);

		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskSetColor(newColor);
		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().displayImage();
		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().repaint();
	}

	private void actionPaintbrushSize ()
	{
		int paintbrushSize = (int) linkFormMain.getComponentToolbar().getComponentPaintbrushSize().getSelectedItem();

		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskSetPaintbrushSize(paintbrushSize);

	}

	private void actionActivatModePaintbrush ()
	{
		linkFormMain.getComponentToolbar().switchButtonMove(false);
		linkFormMain.getComponentToolbar().switchButtonPatinbrush(true);

		linkFormMainMouse.switchMouseMode(FormMainMouse.MOUSE_MODE_PAINBRUSH);
	}

	private void actionEraser ()
	{
		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskRemove();

		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().displayImage();
		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().repaint();
	}

	private void actionPaintbrushReverse ()
	{
		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskRemoveLastPoint();

		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().displayImage();
		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().repaint();
	}

	private void actionActivateModeMove ()
	{
		linkFormMain.getComponentToolbar().switchButtonMove(true);
		linkFormMain.getComponentToolbar().switchButtonPatinbrush(false);

		linkFormMainMouse.switchMouseMode(FormMainMouse.MOUSE_MODE_MOVEIMG);
	}

	private void actionViewRefresh ()
	{
		if ( linkDatabase.getIsLoaded())
		{
			linkFormMain.getComponentPanelCenter().getComponentPanelImageView().resetImagePosition();
		}
	}

	private void actionSaveImageOriginal ()
	{
		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String filePath = fileChooserDriver.getSelectedFile().getPath();
			
			ExportController.exportImageOriginal(dicomData, filePath);
		}
	}


	private void actionSaveImageTransformed ()
	{

		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String filePath = fileChooserDriver.getSelectedFile().getPath();


			int lutIndex = linkFormMain.getComponentPanelLeft().getComponentComboboxTableLUT().getSelectedIndex();

			ExportController.exportImageDisplayed(dicomData, transformTagsCustom, lutIndex, filePath);
		}
	}

	private void actionSaveMask ()
	{
		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int isFileSelected =  fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isFileSelected == JFileChooser.APPROVE_OPTION)
		{
			String filePath = fileChooserDriver.getSelectedFile().getPath();

			int[][] mask = linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskGetData();
			Image maskImage = linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskGetImage();
			
			//----- Inform the database that the mask will be saved
			int indexSelectedDirectory = linkFormMain.getComponentPanelLeft().getComponentComboboxDirectory().getSelectedIndex();
			int indexSelectedFile = linkFormMain.getComponentPanelLeft().getComponentComboboxFileInDirectory().getSelectedIndex();
			
			linkDatabase.getDirectory(indexSelectedDirectory).getFile(indexSelectedFile).setIsMaskSaved(true);
			
			//---- Export the mask image
			ExportController.exportImageMask(dicomData, cacheImage, mask, maskImage, filePath);
		}
	}

	private void actionAnonymizeData ()
	{
		JFileChooser fileChooserDriver = new JFileChooser();
		fileChooserDriver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int isDirectorySelected = fileChooserDriver.showOpenDialog(fileChooserDriver);

		if (isDirectorySelected == JFileChooser.APPROVE_OPTION)
		{
			String directoryPath = fileChooserDriver.getSelectedFile().getAbsolutePath();

			//---- Block buttons
			linkFormMain.getComponentToolbar().switchBlockON();
			
			BackgroundController task = new BackgroundController();
			task.setupEnvironment(linkFormMain, this, linkDatabase);
			task.setupTask(BackgroundTaskType.TASK_ANONYMIZE_DATASET, new String[] {directoryPath});
			task.execute();
		}
	}

	//----------------------------------------------------------------

	private void helperLoadCombobox ()
	{
		String[] listDirectoryNames = linkDatabase.getListDirectoryNames();

		linkFormMain.getComponentPanelLeft().getComponentComboboxDirectory().removeAllItems();

		for (int k = 0; k < listDirectoryNames.length; k++)
		{
			linkFormMain.getComponentPanelLeft().getComponentComboboxDirectory().addItem(listDirectoryNames[k]);
		}

	}

	private void helperComboboxChangedDirectory ()
	{
		//--- Put file names in the directory here
		int indexDirectorySelected = linkFormMain.getComponentPanelLeft().getComponentComboboxDirectory().getSelectedIndex();

		if (indexDirectorySelected < 0) { return; }

		String[] listFileNames = linkDatabase.getListFilesInDirectory(indexDirectorySelected);

		linkFormMain.getComponentPanelLeft().getComponentComboboxFileInDirectory().removeAllItems();

		for (int k = 0; k < listFileNames.length; k++)
		{
			linkFormMain.getComponentPanelLeft().getComponentComboboxFileInDirectory().addItem(listFileNames[k]);
		}

	}

	private void helperComboboxChangedFileInDirectory ()
	{
		linkFormMain.getComponentPanelCenter().getComponentPanelImageView().maskRemove();
		actionLoadImage ();
	}

	private void helperLoadTagData (int indexDirectory, int indexFile)
	{
		/*!!*/Debugger.log("Info [GUI]: load tag data for selected image [" + indexDirectory + "," + indexFile + "]");

		transformTagsDefault[0] = linkDatabase.getFile(indexDirectory, indexFile).getDicomWindowWidth();
		transformTagsDefault[1] = linkDatabase.getFile(indexDirectory, indexFile).getDicomWindowCenter();
		transformTagsDefault[2] = linkDatabase.getFile(indexDirectory, indexFile).getDicomSlope();
		transformTagsDefault[3] = linkDatabase.getFile(indexDirectory, indexFile).getDicomIntercept();

		if (!isApplyTransform)
		{
			linkFormMain.getComponentPanelLeft().getComponentTextfieldImageWindowWidth().setText(String.valueOf(transformTagsDefault[0]));
			linkFormMain.getComponentPanelLeft().getComponentTextfieldImageWindowCenter().setText(String.valueOf(transformTagsDefault[1]));
			linkFormMain.getComponentPanelLeft().getComponentTextfieldImageRescaleSlope().setText(String.valueOf(transformTagsDefault[2]));
			linkFormMain.getComponentPanelLeft().getComponentTextfieldImageRescaleIntercept().setText(String.valueOf(transformTagsDefault[3]));
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) 
	{
		try
		{
			Object source = e.getSource();
			if (source instanceof JSlider) 
			{
				JSlider slider = (JSlider)source;
				String name = slider.getName();

				switch (name)
				{
				case SLIDER_NAME_TRANSFORM_WINDOWWIDTH: sliderTransformChanged(); break;
				case SLIDER_NAME_TRANSFORM_WINDOWCENTER: sliderTransformChanged(); break;
				case SLIDER_NAME_TRANSFORM_RESCALEINTERCEPT: sliderTransformChanged(); break;
				case SLIDER_NAME_TRANSFORM_RESCALESLOPE: sliderTransformChanged(); break;
				}

			}

		}
		catch (Exception exc)
		{

		}
	}

	//----------------------------------------------------------------

	private void sliderTransformChanged ()
	{
		int valueWidth = linkFormMain.getComponentPanelLeft().getComponentSliderImageWindowWidth().getValue();
		int valueCenter = linkFormMain.getComponentPanelLeft().getComponentSliderImageWindowCenter().getValue();
		int valueIntercept = linkFormMain.getComponentPanelLeft().getComponentSliderImageRescaleIntercept().getValue();
		int valueSlope = linkFormMain.getComponentPanelLeft().getComponentSliderImageRescaleSlope().getValue();

		isApplyTransform = true;

		//---- Are these transforms correct? FIXME
		transformTagsCustom[0] = (int) Math.round(((double) valueWidth / 100) * transformTagsDefault[0]);
		linkFormMain.getComponentPanelLeft().getComponentTextfieldImageWindowWidth().setText(String.valueOf(transformTagsCustom[0]));

		transformTagsCustom[1] = (int) Math.round(((double) valueCenter / 100) * transformTagsDefault[1]);
		linkFormMain.getComponentPanelLeft().getComponentTextfieldImageWindowCenter().setText(String.valueOf(transformTagsCustom[1]));

		if (valueSlope == 0) { valueSlope = 100; }
		transformTagsCustom[2] = (int) Math.round(((double) valueSlope / 100));
		linkFormMain.getComponentPanelLeft().getComponentTextfieldImageRescaleSlope().setText(String.valueOf(transformTagsCustom[2]));

		transformTagsCustom[3] = (int) Math.round(((double) valueIntercept / 100) * transformTagsDefault[3]);
		linkFormMain.getComponentPanelLeft().getComponentTextfieldImageRescaleIntercept().setText(String.valueOf(transformTagsCustom[3]));


		actionDisplayImageCache ();
	}

	private void sliderTransformReset ()
	{
		linkFormMain.getComponentPanelLeft().getComponentSliderImageWindowWidth().setValue(100);
		linkFormMain.getComponentPanelLeft().getComponentSliderImageWindowCenter().setValue(100);
		linkFormMain.getComponentPanelLeft().getComponentSliderImageRescaleSlope().setValue(100);
		linkFormMain.getComponentPanelLeft().getComponentSliderImageRescaleIntercept().setValue(100);

		isApplyTransform = false;
	}

	//----------------------------------------------------------------

	public void callbackImportNewDatabaseFinished (String[] failStatus)
	{
		//---- UnBlock buttons
		linkFormMain.getComponentToolbar().switchBlockOFF();
		
		//---- Show a dialog to inform the user, that some files could not be imported
		if (failStatus != null)
		{
			if (failStatus.length == 1)
			{
				JOptionPane.showMessageDialog (null, "The following file could not be imported \n" + failStatus[0], "Import error", JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog (null, "During the import stage " + failStatus.length + " files could not be imported.\n Check the error log for more information.", "Import error", JOptionPane.WARNING_MESSAGE);
			}
				
			try
			{
				//---- Save information about the files that could not be imported into
				//---- the log file
				PrintWriter outputStream = new PrintWriter(new File ("log-error.txt"));
				
				for (int k = 0; k < failStatus.length; k++)
				{
					outputStream.println(failStatus[k]);
				}
				
				outputStream.close();
				
			}
			catch (Exception e) {}
		}
		
		helperLoadCombobox();
	}
	
	public void callbackAnonymizeFinished ()
	{
		//---- UnBlock buttons
		linkFormMain.getComponentToolbar().switchBlockOFF();
	}



}
