package edu.hpc.andrey.zmask.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;

import edu.hpc.andrey.dicom.core.DicomContainer;
import edu.hpc.andrey.dicom.core.DicomDecoder;
import edu.hpc.andrey.dicom.core.DicomDictionary;
import edu.hpc.andrey.dicom.core.DicomFile;
import edu.hpc.andrey.dicom.core.DicomTagData;
import edu.hpc.andrey.utils.FileSystemManager;
import edu.hpc.andrey.zmask.debugger.Debugger;
import edu.hpc.andrey.zmask.gui.panel.TaskProgressController;

public class DatabaseController 
{
	private static final String[] DICOM_EXTENSION = {"DCM", "dcm", ""};

	//---------------------------------------------------------------
	
	public static void executeDropTables (DatabaseTable tbl)
	{
		/*!!*/Debugger.log("Info [DAT]: drop database tables");
		
		tbl.reset();
	}

	/**
	 * Import image file specified by the image path. If root directory of the file has to be 
	 * saved in the table, pass this parameter in pathRootDir, if the directory name is 
	 * not required for further processing pass an empty string.
	 * @param tbl
	 * @param pathFile
	 * @param dirName
	 */
	public static boolean executeImportFile (DatabaseTable tbl, String pathFile)
	{	
		/*!!*/Debugger.log("Info [DAT]: import dicom file " + pathFile);
		
		File fileDescriptor = new File (pathFile);

		//---- Just in case check if the file exists
		if (fileDescriptor.exists())
		{
			String fileName = fileDescriptor.getName();
			String filePath = fileDescriptor.getPath();

			int[] tagData = executeCollectTagData(filePath);

			//---- If DICOM decoder could not correctly extract TAG data then don't add this file
			//---- to the database 
			if (tagData == null) { return false; }
			
			DatabaseFile fileNew = new DatabaseFile();
			
			//---- Set parameters of the file
			fileNew.setFileName(fileName);
			fileNew.setFilePath(filePath);
		
			fileNew.setDicomWindowCenter(tagData[0]);
			fileNew.setDicomWindowWidth(tagData[1]);
			fileNew.setDicomIntercept(tagData[2]);
			fileNew.setDicomSlope(tagData[3]);
			fileNew.setDicomMonchromeMode(tagData[4]);
		
			tbl.addFile(fileNew);
		}

		return true;
	}

	public static boolean executeImportFile (DatabaseTable tbl, String pathFile, int directoryIndex)
	{	
		/*!!*/Debugger.log("Info [DAT]: import dicom file " + pathFile + " -> " + directoryIndex);
		
		File fileDescriptor = new File (pathFile);

		//---- Just in case check if the file exists
		if (fileDescriptor.exists())
		{
			String fileName = fileDescriptor.getName();
			String filePath = fileDescriptor.getPath();

			int[] tagData = executeCollectTagData(filePath);

			//---- If DICOM decoder could not correctly extract TAG data then don't add this file
			//---- to the database 
			if (tagData == null) { return false; }
			
			DatabaseFile fileNew = new DatabaseFile();
			
			//---- Set parameters of the file
			fileNew.setFileName(fileName);
			fileNew.setFilePath(filePath);
		
			fileNew.setDicomWindowCenter(tagData[0]);
			fileNew.setDicomWindowWidth(tagData[1]);
			fileNew.setDicomIntercept(tagData[2]);
			fileNew.setDicomSlope(tagData[3]);
			fileNew.setDicomMonchromeMode(tagData[4]);
		
			tbl.addFile(fileNew, directoryIndex);
		}

		return true;
	}
		
	/**
	 * Import all dicom image files from a directory specified by its path. If some DICOM files are corrupted
	 * or could not be parsed this method returns all the names of such files. If there are no such files, 
	 * then returns null.
	 * @param tbl
	 * @param pathDirectory
	 */
	public static String[] executeImportDirectory (DatabaseTable tbl, String pathDirectory)
	{
	
		Vector <String> importFailList = new Vector <String> ();
		
		/*!!*/Debugger.log("Info [DAT]: import dicom files from directory " + pathDirectory);
		
		File directoryDescriptor = new File (pathDirectory);
		
		//---- Check if the specified path is a directory, and just in case that it exists
		if (directoryDescriptor.isDirectory() && directoryDescriptor.exists())
		{
			File[] fileList = directoryDescriptor.listFiles(File::isFile);

			/*-->*/TaskProgressController.statusSetTaskCount(fileList.length);
			
			tbl.addDirectory(directoryDescriptor.getName(), directoryDescriptor.getAbsolutePath());
			int directoryNewID = tbl.getSize() - 1;
			
			for (int k = 0; k < fileList.length; k++)
			{
				//---- Check file extension is allowed
				String fileExtension = FileSystemManager.getFileExtension(fileList[k].getName());

				/*-->*/TaskProgressController.statusUpdate(k + " | " + fileList.length + " files imported");
				
				if (Arrays.asList(DICOM_EXTENSION).contains(fileExtension))
				{
					//---- Import file, push the data into table, if the import was successful 
					boolean isImportOK = executeImportFile(tbl, fileList[k].getAbsolutePath(), directoryNewID);
					
					//---- Add
					if (!isImportOK) { importFailList.addElement(fileList[k].getAbsolutePath()); }
				
				}
			}
		}
		
		if (importFailList.size() == 0) { return null; }
		else { return importFailList.toArray(new String [importFailList.size()]); }
	}

	/**
	 * Import all dicom files from sub-directories located in the root directory, specified by its path.
	 * Returns all files, which could not be imported, if all files were successfully imported, then 
	 * return null;
	 * @param tbl
	 * @param pathDataset
	 */
	public static String[] executeImportDataset (DatabaseTable tbl, String pathDataset)
	{
		Vector <String> importFailList = new Vector <String> ();
		
		/*!!*/Debugger.log("Info [DAT]: import dicom files from dataset " + pathDataset);
		
		File directoryDescriptor = new File(pathDataset);
		
		if (directoryDescriptor.isDirectory() && directoryDescriptor.exists())
		{
			File[] subdirectoryList = directoryDescriptor.listFiles(File::isDirectory);

			/*-->*/TaskProgressController.statusSetTaskCount(subdirectoryList.length);
			
			for (int k = 0; k < subdirectoryList.length; k++)
			{
				/*-->*/TaskProgressController.statusUpdate(k + " | " + subdirectoryList.length + " directories imported");
				/*-->*/TaskProgressController.statusSwitchBlockON(); // Prevent modification of the status by the subroutine
				String[] listFileFail = executeImportDirectory(tbl, subdirectoryList[k].getPath());
				
				if (listFileFail != null) { for (int p = 0; p < listFileFail.length; p++) { importFailList.addElement(listFileFail[p]); } }
				
				/*-->*/TaskProgressController.statusSwitchBlockOFF();
			}
		}
		
		if (importFailList.size() == 0) { return null; }
		else { return importFailList.toArray(new String [importFailList.size()]); }
	}

	/**
	 * Import database data from file
	 * @param tbl
	 * @param pathFileDB
	 */
	public static void executeImportDatabaseFile (DatabaseTable tbl, String pathFileDB)
	{
		/*!!*/Debugger.log("Info [DAT]: import database file");
		
		try
		{
			FileReader inputFile = new FileReader(pathFileDB);
			BufferedReader inputStream = new BufferedReader(inputFile);

			String line = "";

			//---- Check the first line, get the number of directories
			line = inputStream.readLine();
			
			String[] data = line.split(";");
			
			//---- Check the prefix
			if (!data[0].equals("DRS-DB")) { inputStream.close(); return; }
			
			int directoryCount = Integer.parseInt(data[1]);
			
			for (int k = 0; k < directoryCount; k++)
			{
				tbl.addDirectory("", "");
			}
			
			
			while ((line = inputStream.readLine()) != null)
			{
				data = line.split(";");

				int directoryID = Integer.parseInt(data[0]);
				String directoryName = data[1];
				String directoryPath = data[2];
				
				String fileName = data[3];
				String filePath = data[4];
			
				int dicomWindowWidth = Integer.parseInt(data[5]);
				int dicomWindowCenter = Integer.parseInt(data[6]);
				int dicomIntercept = Integer.parseInt(data[7]);
				int dicomSlope = Integer.parseInt(data[8]);
				int dicomMonochromeMode = Integer.parseInt(data[9]);
				int maskSaveState = Integer.parseInt(data[10]);
				
				boolean isMaskSaved = (maskSaveState == 1) ? true : false;
				
				DatabaseFile fileNew = new DatabaseFile();
				fileNew.setFileName(fileName);
				fileNew.setFilePath(filePath);
				fileNew.setDicomWindowWidth(dicomWindowWidth);
				fileNew.setDicomWindowCenter(dicomWindowCenter);
				fileNew.setDicomIntercept(dicomIntercept);
				fileNew.setDicomSlope(dicomSlope);
				fileNew.setDicomMonchromeMode(dicomMonochromeMode);
				fileNew.setIsMaskSaved(isMaskSaved);
				
				tbl.getDirectory(directoryID).setDirectoryID(directoryID);
				tbl.getDirectory(directoryID).setDirectoryName(directoryName);
				tbl.getDirectory(directoryID).setDirectoryPath(directoryPath);
				
				tbl.getDirectory(directoryID).addFile(fileNew);
				
			}

			inputStream.close();
			inputFile.close();
		}
		catch (Exception e)
		{
			Debugger.log(e);
		}
	}

	/**
	 * Export database data into file
	 * @param tbl
	 * @param pathFileDB
	 */
	public static void executeExportDatabaseFile (DatabaseTable tbl, String pathFileDB)
	{
		/*!!*/Debugger.log("Info [DAT]: export database file " + pathFileDB);
		
		try
		{
			FileWriter outputFile = new FileWriter(pathFileDB, false);
			PrintWriter outputStream = new PrintWriter(outputFile);

			outputStream.println("DRS-DB;" + tbl.getSize() + ";");
			
			for (int i = 0; i < tbl.getSize(); i++)
			{
			
				String directoryID = String.valueOf(i);
				String directoryName = tbl.getDirectory(i).getDirectoryName();
				String directoryPath = tbl.getDirectory(i).getDirectoryPath();
				
				
				for (int k = 0; k < tbl.getDirectory(i).getSize(); k++)
				{
					String fileName = tbl.getDirectory(i).getFile(k).getFileName();
					String filePath = tbl.getDirectory(i).getFile(k).getFilePath();
					String dicomWindowWidth = String.valueOf(tbl.getDirectory(i).getFile(k).getDicomWindowWidth());
					String dicomWindowCenter = String.valueOf(tbl.getDirectory(i).getFile(k).getDicomWindowCenter());
					String dicomIntercept = String.valueOf(tbl.getDirectory(i).getFile(k).getDicomIntercept());
					String dicomSlope = String.valueOf(tbl.getDirectory(i).getFile(k).getDicomSlope());
					String dicomMonochromeMode = String.valueOf(tbl.getDirectory(i).getFile(k).getDicomMonochromeMode());
					String isMaskSaved = (tbl.getDirectory(i).getFile(k).getIsMaskSaved() == true) ? "1" : "0";
				
					String outputLine = 
							directoryID + ";" +
							directoryName + ";" +
							directoryPath  + ";" +
							fileName  + ";" +
							filePath  + ";" +
							dicomWindowWidth  + ";" +
							dicomWindowCenter  + ";" +
							dicomIntercept  + ";" +
							dicomSlope  + ";" +
							dicomMonochromeMode  + ";" +
							isMaskSaved + ";";
					
					outputStream.println(outputLine);
				}
			}

			outputStream.close();
			outputFile.close();

		}
		catch (Exception e)
		{
			Debugger.log(e);
		}
	}

	
	/**
	 * Extract DICOM tag data from a file, where its path is taken from the database 
	 * using the index. Be careful, index here is not fileID, but just the position in the table.
	 * @param tbl
	 * @param index
	 */
	private static int[] executeCollectTagData (String path)
	{

		DicomDictionary dictionary = DicomDictionary.getInstance();
		DicomContainer dicomData = new DicomContainer();
		DicomFile dicomFile = new DicomFile();
		boolean isOK = DicomDecoder.convertDICOM(path, dicomData, dicomFile, dictionary, false);
		
		//---- DICOM decoder could not correctly process the file
		if (!isOK) { return null; }

		String tagImageDescriptor = dicomData.getTagValue(DicomTagData.TAG_IMAGE_DESCRIPTOR).replace(" ", "");
		String tagImageType = dicomData.getTagValue(DicomTagData.TAG_IMAGE_TYPE).replace(" ", "");

		if (tagImageDescriptor.length() == 0) { tagImageDescriptor = "none"; }
		if (tagImageType.length() == 0) { tagImageType = "none"; }

		int tagWindowCenter = 0;
		String strTagWindowCenter = dicomData.getTagValue(DicomTagData.TAG_WINDOW_CENTER).replace(" ", "");
		if (strTagWindowCenter.length() >= 1) 
		{ 
			if (strTagWindowCenter.contains(".")) { strTagWindowCenter = strTagWindowCenter.substring(0, strTagWindowCenter.indexOf(".")); }
			if (strTagWindowCenter.contains("\\")) { strTagWindowCenter = strTagWindowCenter.substring(0, strTagWindowCenter.indexOf("\\")); }
			tagWindowCenter = Integer.parseInt(strTagWindowCenter);
		}

		int tagWindowWidth = 0;
		String strTagWindowWidth = dicomData.getTagValue(DicomTagData.TAG_WINDOW_WIDTH).replace(" ", "");
		if (strTagWindowWidth.length() >= 1) 
		{ 
			if (strTagWindowWidth.contains(".")) { strTagWindowWidth = strTagWindowWidth.substring(0, strTagWindowWidth.indexOf(".")); }
			if (strTagWindowWidth.contains("\\")) { strTagWindowWidth = strTagWindowWidth.substring(0, strTagWindowWidth.indexOf("\\")); }
			tagWindowWidth = Integer.parseInt(strTagWindowWidth);
		}

		int tagRescaleIntercept = 0;
		String strTagRescaleIntercept = dicomData.getTagValue(DicomTagData.TAG_RESCALE_INTERCEPT).replace(" ", "");
		if (strTagRescaleIntercept.length() >= 1) 
		{ 
			if (strTagRescaleIntercept .contains(".")) { strTagRescaleIntercept  = strTagRescaleIntercept.substring(0, strTagRescaleIntercept .indexOf(".")); }
			if (strTagRescaleIntercept .contains("\\")) { strTagRescaleIntercept  = strTagRescaleIntercept.substring(0, strTagRescaleIntercept .indexOf("\\")); }
			tagRescaleIntercept = Integer.parseInt(strTagRescaleIntercept); 
		}

		int tagRescaleSlope = 1;
		String strTagRescaleSlope = dicomData.getTagValue(DicomTagData.TAG_RESCALE_SLOPE).replace(" ", "");
		if (strTagRescaleSlope.length() >= 1) 
		{ 
			if (strTagRescaleSlope.contains(".")) { strTagRescaleSlope  = strTagRescaleSlope.substring(0, strTagRescaleSlope.indexOf(".")); }
			if (strTagRescaleSlope.contains("\\")) { strTagRescaleSlope  = strTagRescaleSlope.substring(0, strTagRescaleSlope.indexOf("\\")); }
			tagRescaleSlope = Integer.parseInt(strTagRescaleSlope);
		}

		int tagMonochromeMode = dicomData.getMonochromeMode();

		return new int[] {tagWindowCenter, tagWindowWidth, tagRescaleIntercept, tagRescaleSlope, tagMonochromeMode};
	}


	//---------------------------------------------------------------
	
	
	public static String[] getListDirectory (DatabaseTable tbl)
	{
		if (tbl == null) { return null; }
		if (tbl.getSize() == 0) { return null; }
		
		return tbl.getListDirectoryNames();
	}
	
	public static String[] getListFilesInDir (DatabaseTable tbl, int directoryIndex)
	{
		return tbl.getListFilesInDirectory(directoryIndex);

		
	}

}
