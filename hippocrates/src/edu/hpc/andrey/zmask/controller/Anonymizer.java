package edu.hpc.andrey.zmask.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.hpc.andrey.dicom.anon.data.ControllerOutput;
import edu.hpc.andrey.dicom.core.DicomContainer;
import edu.hpc.andrey.dicom.core.DicomDecoder;
import edu.hpc.andrey.dicom.core.DicomDictionary;
import edu.hpc.andrey.dicom.core.DicomFile;
import edu.hpc.andrey.dicom.core.DicomTagData;
import edu.hpc.andrey.dicom.core.Utils;
import edu.hpc.andrey.zmask.data.DatabaseTable;
import edu.hpc.andrey.zmask.debugger.Debugger;
import edu.hpc.andrey.zmask.gui.panel.TaskProgressController;

public class Anonymizer 
{
	public static void run (DatabaseTable tbl, String outputDirectory, boolean[] flags)
	{
		Map <String, String> tblDirectoryNameLookup = new HashMap <String, String> ();

		runInitLookupTable(tbl, tblDirectoryNameLookup);
		runInitDirectoryTree(outputDirectory, tblDirectoryNameLookup);
		runAnonymizer(tbl, tblDirectoryNameLookup, outputDirectory, flags);
	}

	private static void runInitLookupTable (DatabaseTable tbl, Map <String, String> tableDirectoryNameLookup)
	{
		/*!!*/Debugger.log("Info [RUN]: generating directory lookup table");

		//---- Fill the lookup table 
		int index = 0;



		for (int i = 0; i < tbl.getSize(); i++)
		{
			String directoryName = tbl.getDirectory(i).getDirectoryName();

			if (!tableDirectoryNameLookup.containsKey(directoryName))
			{
				//---- Define new directory name
				String directoryNameNew = String.format("%09d", index + 1);

				if (directoryName.equals("UNKNOWN")) { directoryNameNew = "UNKNOWN"; }
				else { index++; }

				tableDirectoryNameLookup.put(directoryName, directoryNameNew);

				/*!!*/Debugger.log("Info [RUN]: directory name conversion: " + directoryName + " -> " + directoryNameNew);
			}
		}
	}

	private static void runInitDirectoryTree (String outputDirectory, Map <String, String> tblDirectoryNameLookup)
	{
		/*!!*/Debugger.log("Info [RUN]: generating directory tree");

		try
		{
			String fileLogPath = outputDirectory + File.separator + "log-dir.txt";

			PrintWriter outputStream  = new PrintWriter(new FileWriter((fileLogPath)));


			for (String key:tblDirectoryNameLookup.keySet())
			{
				String path = outputDirectory + File.separator + tblDirectoryNameLookup.get(key);

				//---- Do not attempt to create a new directory if already exists
				File directoryNew = new File (path);

				if (!directoryNew.exists() || (directoryNew.exists() && directoryNew.isFile()))
				{
					boolean isOK = new File (path).mkdir();

					if (!isOK)
					{
						/*!!*/Debugger.log("Error [RUN]: Can not create directory " + path);
					}

					outputStream.println(key + ";" + tblDirectoryNameLookup.get(key));
				}
			}

			outputStream.close();

		}
		catch (Exception e)
		{

		}
	}

	private static void runAnonymizer (DatabaseTable tbl, Map<String, String> tblDirectoryNameLookup, String outputDirectory, boolean[] flags)
	{

		//---- File output stream for saving the structure of the database
		String pathDatabaseFile = outputDirectory + File.separator + "database.txt";
		PrintWriter outputStreamDatabase = null;
	
		try { outputStreamDatabase = new PrintWriter(new FileWriter((pathDatabaseFile))); } catch (Exception e) { outputStreamDatabase = null; }
		
		//---- File output stream for saving the transformed patient id-s
		String pathTablePatientID = outputDirectory + File.separator + "log-pid.txt";
		PrintWriter outputStreamTable = null;
	
		try { outputStreamTable = new PrintWriter(new FileWriter((pathTablePatientID))); } catch (Exception e) { outputStreamTable = null; }

		Vector <String> tablePatientID = new Vector <String> ();
		
		/*!!*/Debugger.log("Info [RUN]: anonymization process has been initialized");

		DicomDictionary dictionary = DicomDictionary.getInstance();

		int totalFileCount = tbl.getFileCountTotal();
		int fileIndex = 0;
		/*-->*/ TaskProgressController.statusSetTaskCount(totalFileCount);

		outputStreamDatabase.println("DRS-DB;" + tbl.getSize() + ";");
		
		for (int k = 0; k < tbl.getSize(); k++)
		{
			String directoryID = String.valueOf(k);
			String directoryName = tbl.getDirectory(k).getDirectoryName();
			String directoryNameNew = tblDirectoryNameLookup.get(directoryName);
			String directoryPathNew = outputDirectory + File.separator + directoryNameNew;

			Map <String, Integer> listFileNameCollision = new HashMap <String, Integer> ();

			//---- Scan files
			for (int i = 0; i < tbl.getDirectory(k).getSize(); i++)
			{
				//----- Get info about the file to store int the database file
				String filePath = tbl.getDirectory(k).getFile(i).getFilePath();
				String dicomWindowWidth = String.valueOf(tbl.getDirectory(k).getFile(i).getDicomWindowWidth());
				String dicomWindowCenter = String.valueOf(tbl.getDirectory(k).getFile(i).getDicomWindowCenter());
				String dicomIntercept = String.valueOf(tbl.getDirectory(k).getFile(i).getDicomIntercept());
				String dicomSlope = String.valueOf(tbl.getDirectory(k).getFile(i).getDicomSlope());
				String dicomMonochromeMode = String.valueOf(tbl.getDirectory(k).getFile(i).getDicomMonochromeMode());
				String isMaskSaved = (tbl.getDirectory(k).getFile(i).getIsMaskSaved() == true) ? "1" : "0";
				
				/*!!*/Debugger.log("Info [RUN]: anonnymizing file " + filePath);

				//---- Extract patient ID
				DicomContainer dicomData = new DicomContainer();
				DicomFile dicomFile = new DicomFile();

				boolean isOK = DicomDecoder.convertDICOM(filePath, dicomData, dicomFile, dictionary, false);

				fileIndex++;
				
				//---- If dicom tags were extracted successfully 
				if (isOK)
				{
					/*-->*/ TaskProgressController.statusUpdate(fileIndex + " | " + totalFileCount + " processed");

					String pidOLD = dicomData.getTagValue(DicomTagData.TAG_PATIENTID);
					String pidNEW = Utils.anonymizeString(pidOLD, Utils.HASH_ALGORITHM.JAVA_STRHASHCODE);

					//---- Form file name as newpid-accdate-[index]

					String dateValue = "FFFFFFFF";

					String tagDateStudy = dicomData.getTagValue(DicomTagData.TAG_STUDY_DATE);
					String tagDateSeries = dicomData.getTagValue(DicomTagData.TAG_SERIES_DATE);
					String tagDateAcquisition = dicomData.getTagValue(DicomTagData.TAG_AQUISITION_DATE);
					String tagDateContent = dicomData.getTagValue(DicomTagData.TAG_CONTENT_DATE);

					if (tagDateStudy.length() != 0) { dateValue = tagDateStudy; }
					else if (tagDateSeries.length() != 0) { dateValue = tagDateSeries; }
					else if (tagDateAcquisition.length() != 0) { dateValue = tagDateAcquisition; }
					else if (tagDateContent.length() != 0) { dateValue = tagDateContent; }

					String fileNameNew = pidNEW + "-" + dateValue;

					//---- Check name collision here
					if (!listFileNameCollision.containsKey(fileNameNew))
					{ 
						/*!!*/Debugger.log("Info [RUN]: file name " + fileNameNew + " is unique");
						listFileNameCollision.put(fileNameNew, 1); 
						fileNameNew = fileNameNew + "-" + String.format("%03d", 1); 
					}
					else 
					{ 
						int index = listFileNameCollision.get(fileNameNew);

						/*!!*/Debugger.log("Info [RUN]: file name " + fileNameNew + " is not unique, setting index " + index);
						listFileNameCollision.put(fileNameNew, index + 1);
						fileNameNew = fileNameNew + "-" + String.format("%03d", index + 1); 		
					}


					String filePathNew = outputDirectory + File.separator + directoryNameNew + File.separator + fileNameNew;

					/*!!*/Debugger.log("Info [RUN]: assigned path " + fileNameNew + " -> " + filePathNew);

					//----- Save here the new patient ID to the file, if it has not been saved/stored already
					if (outputStreamTable != null && !tablePatientID.contains(pidOLD))
					{
						tablePatientID.addElement(pidOLD);
						outputStreamTable.println(pidOLD + ";" + pidNEW);
					}
					
					//----- Save here the information about the new file in the database file
					if (outputStreamDatabase != null)
					{
						String outputLine = directoryID + ";" +	directoryNameNew + ";" + directoryPathNew  + ";" + fileNameNew + ".dcm"  + ";" +
											filePathNew + ".dcm"  + ";" + dicomWindowWidth  + ";" + dicomWindowCenter  + ";" + dicomIntercept  + ";" +
											dicomSlope  + ";" + dicomMonochromeMode  + ";" + isMaskSaved;
						outputStreamDatabase.println(outputLine);
					}
					
					ControllerOutput.exportData(dicomData, dicomFile, dictionary, filePathNew, flags);

				}
				else { /*!!*/Debugger.log("Error [RUN]: can't parse dicom file " + filePath); }

			}
		}

		//---- Close streams if they have been openned
		if (outputStreamDatabase != null) { outputStreamDatabase.close(); }
		if (outputStreamTable != null) { outputStreamTable.close(); }
	}
}
