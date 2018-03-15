package edu.hpc.andrey.dicom.anon.data;

import java.io.File;

import edu.hpc.andrey.utils.FileSystemManager;

/**
 * Class for loading a single image or a set of images from a directory into the task queue. 
 * The directory structure from which the images are loading should be the following:
 * rootDIR -> subDIR -> DICOM-image01, DICOM-image02 ...
 * @author Andrey
 */

public class ControllerInput 
{
	/**
	 * Add A single file into the task queue.
	 * @param pathInput - the path to the input file
	 * @param pathOutput - the path to the output directory, or name of the output file
	 * @param table - table where the file paths are stored
	 */

	public static void scanFile (String pathInput, String pathOutput, FileTable table)
	{
		File fileInput = new File(pathInput);
		File fileOutput = new File(pathOutput);

		String fileExtension = FileSystemManager.getFileExtension(fileInput.getName());
		String fileNameNew = "";

		if (fileExtension.equals("dcm") || fileExtension.equals("DCM") || fileExtension.equals(""))
		{
			fileNameNew = "$tagid";

			table.setPathRootDirImport(pathInput);
			table.setPathRootDirExport(pathOutput);

			if (fileOutput.isDirectory()) { table.addFilePath(pathInput, pathOutput + "/" + fileNameNew); }
		}
	}

	/**
	 * Add all dicom (*.dcm, *.DCM, no extension) files located in sub-directories of a directory,
	 * specified by the path. Copy the folder structure with changing directory names 
	 * @param pathInput - the path to the root directory
	 * @param pathOutput - the path to the output root directory, the directory structure will be copied there.
	 * @param table - table, where to store paths to all files
	 */

	public static void scanDirectory (String pathInput, String pathOutput, FileTable table)
	{

		table.setPathRootDirImport(pathInput);
		table.setPathRootDirExport(pathOutput);


		//---- Get all files in each directory
		File[] fileList = new File(pathInput).listFiles(File::isFile);

		for (int j = 0; j < fileList.length; j++)
		{
			//---- Check a type of file (.dcm extension or no extension)
			String filePathOld = fileList[j].getPath();
			String fileNameOld = fileList[j].getName();
			String fileExtension = FileSystemManager.getFileExtension(fileNameOld);

			//---- Here we specify a new file name with a template, which later has to be
			//---- changed to the new patient id
			String fileNameNew = "$tagid";
			String filePathNew = pathOutput + File.separator + fileNameNew;

			if (fileExtension.equals("dcm") || fileExtension.equals("DCM") || fileExtension.equals(""))
			{
				//---- Push the input file into the file list
				table.addFilePath(filePathOld, filePathNew);
			}

		}

	}

	//-----------------------------------------------------------------------------------------


}
