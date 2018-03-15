package edu.hpc.andrey.dicom.anon.task;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;

import edu.hpc.andrey.dicom.anon.data.ControllerInput;
import edu.hpc.andrey.dicom.anon.data.ControllerOutput;
import edu.hpc.andrey.dicom.anon.data.FileTable;
import edu.hpc.andrey.dicom.core.DicomContainer;
import edu.hpc.andrey.dicom.core.DicomDecoder;
import edu.hpc.andrey.dicom.core.DicomDictionary;
import edu.hpc.andrey.dicom.core.DicomFile;
import edu.hpc.andrey.dicom.core.DicomTagData;
import edu.hpc.andrey.dicom.core.Utils;
import edu.hpc.andrey.zmask.debugger.Debugger;
import edu.hpc.andrey.zmask.gui.panel.TaskProgressController;

/**
 * Swing background process to perform removing of sensitive information from the DICOM files.
 * @author Andrey
 */

public class TaskBackground extends SwingWorker<Void, Void> 
{
	private boolean DEBUG_MODE = false;

	private FileTable linkData;

	private boolean isTAG = false;
	private boolean isBIN = false;
	private boolean isBMP = false;
	private boolean isINF = false;
	private boolean isRAW = false;
	private boolean isDICOM = false;

	private String pathImport = "";
	private String pathExport = "";

	//----------------------------------------------------------------

	public void setLinkData (FileTable data)
	{
		linkData = data;
	}

	public void setFlagExportDicomTags (boolean value)
	{
		isTAG = value;
	}

	public void setFlagExportImageBinary (boolean value)
	{
		isBIN = value;
	}

	public void setFlagExportImageBitmap (boolean value)
	{
		isBMP = value;
	}

	public void setFlagExportPatientData (boolean value)
	{
		isINF = value;
	}

	public void setFlagExportRawData (boolean value)
	{
		isRAW = value;
	}

	public void setFlagExportDicom (boolean value)
	{
		isDICOM = value;
	}

	public void setPathImport (String value)
	{
		pathImport = value;
	}

	public void setPathExport (String value)
	{
		pathExport = value;
	}

	//----------------------------------------------------------------

	@Override
	protected Void doInBackground() throws Exception 
	{		
		File pathFile = new File(pathImport);

		if (pathFile.isDirectory()) 
		{ 
			ControllerInput.scanDirectory(pathImport, pathExport, linkData);
			
			//---- Set up status bar in GUI
			TaskProgressController.statusReset();
			TaskProgressController.statusSetTaskCount(linkData.getCountFile());
			TaskProgressController.statusLaunch();
			
			runProcessDirectory();
		}
		if (pathFile.isFile()) 
		{ 
			ControllerInput.scanFile(pathImport, pathExport, linkData); 
			
			//---- Set up status bar in GUI
			TaskProgressController.statusReset();
			TaskProgressController.statusSetTaskCount(1);
			TaskProgressController.statusLaunch();
			
			runProcessFile();
		}
		
		return null;
	}


	@Override
	public void done()
	{
		TaskProgressController.statusTerminate();
		TaskProgressController.statusReset();
	}

	//----------------------------------------------------------------


	private void runProcessDirectory () throws Exception
	{
		//---- Load dicom dictionary file 
		DicomDictionary dictionary = DicomDictionary.getInstance();
		
		//---- Create a table for change patient ID's
		Map <String, String> tablePID = new HashMap <String, String> ();
		Map <String, Integer> listFileNameCollision = new HashMap <String, Integer> ();

		//---- Log file where PID conversion table will be stored
		String fileLogPath = linkData.getPathRootDirExport() + File.separator + "log-pid.txt";
		
		//---- Log file where names of files that could not be processed will be stored
		String errorLogPath = linkData.getPathRootDirExport() + File.separator + "log-error.txt";
		
		PrintWriter outputStream = new PrintWriter(new FileWriter((fileLogPath)));
		PrintWriter outputStreamError = new PrintWriter (new FileWriter((errorLogPath)));
		

		//---- Perform conversion here
		for (int i = 0; i < linkData.getCountFile(); i++)
		{
			//---- Update progress
			TaskProgressController.statusUpdate((i+1) + "/" + linkData.getCountFile());

			String fileInputPath = linkData.getFilePathImport(i);

			//---- Process file
			if (!fileInputPath.equals(""))
			{
				DicomContainer dicomData = new DicomContainer();
				DicomFile dicomFile = new DicomFile();

				boolean isOK = DicomDecoder.convertDICOM(fileInputPath, dicomData, dicomFile, dictionary, false);

				//---- Save data with specified formats
				if (isOK)
				{
					//---- SUCCESS
					String fileOutputPath = linkData.getFilePathExport(i);

					//---- Change file name if a directory is being processed

					String pidOLD = dicomData.getTagValue(DicomTagData.TAG_PATIENTID);
					String pidNEW = Utils.anonymizeString(pidOLD, Utils.HASH_ALGORITHM.JAVA_STRHASHCODE);

					
					String dateValue = "FFFFFFFF";
							
					String tagDateStudy = dicomData.getTagValue(DicomTagData.TAG_STUDY_DATE);
					String tagDateSeries = dicomData.getTagValue(DicomTagData.TAG_SERIES_DATE);
					String tagDateAcquisition = dicomData.getTagValue(DicomTagData.TAG_AQUISITION_DATE);
					String tagDateContent = dicomData.getTagValue(DicomTagData.TAG_CONTENT_DATE);
					
					if (tagDateStudy.length() != 0) { dateValue = tagDateStudy; }
					else if (tagDateSeries.length() != 0) { dateValue = tagDateSeries; }
					else if (tagDateAcquisition.length() != 0) { dateValue = tagDateAcquisition; }
					else if (tagDateContent.length() != 0) { dateValue = tagDateContent; }
 					
					//---- Check if no key collision occurs
					if (tablePID.containsKey(pidOLD))  
					{						
						if (!tablePID.get(pidOLD).equals(pidNEW))  { if (DEBUG_MODE) { Debugger.log("Error [BCk]: collision in pid table is detected"); } }
					}
					else { tablePID.put(pidOLD, pidNEW); }

					String fileNameNew = pidNEW + "-" + dateValue;
					
					if (!listFileNameCollision.containsKey(fileNameNew))
					{ 
						listFileNameCollision.put(fileNameNew, 1); 
						fileNameNew = fileNameNew + "-" + String.format("%03d", 1); 
					}
					else 
					{ 

						int index = listFileNameCollision.get(fileNameNew);
						listFileNameCollision.put(fileNameNew, index + 1);
						fileNameNew = fileNameNew + "-" + String.format("%03d", index + 1); 		
					}
					
					fileOutputPath = fileOutputPath.replace("$tagid", fileNameNew);

					ControllerOutput.exportData(dicomData, dicomFile, dictionary, fileOutputPath, new boolean[]{isTAG, isBIN, isBMP, isINF, isRAW, isDICOM});
				}
				else 
				{
					/*!!*/Debugger.log("Error [RUN]: can't parse dicom file " + fileInputPath); 
					outputStreamError.println(fileInputPath);
				}
			}
		}

		//----- Write the patient ID conversion table to the log file
		for (String key : tablePID.keySet()) { outputStream.println(key + ";" + tablePID.get(key));	}

		outputStreamError.flush();
		outputStreamError.close();
		
		outputStream.flush();
		outputStream.close();	
	}

	private void runProcessFile ()
	{
		//---- Load dicom dictionary file 
		DicomDictionary dictionary = DicomDictionary.getInstance();
		
		//---- Set status in GUI
		TaskProgressController.statusUpdate(1 + "/" + linkData.getCountFile());

		String fileInputPath = linkData.getFilePathImport(0);
		String fileOutputPath = linkData.getFilePathExport(0);
		
		if (!fileInputPath.equals(""))
		{
			DicomContainer dicomData = new DicomContainer();
			DicomFile dicomFile = new DicomFile();
			
			boolean isOK = DicomDecoder.convertDICOM(fileInputPath, dicomData, dicomFile, dictionary, false);
			
			//---- If DICOM conversion was successful 
			if (isOK)
			{
				String pidOLD = dicomData.getTagValue(DicomTagData.TAG_PATIENTID);
				String pidNEW = Utils.anonymizeString(pidOLD, Utils.HASH_ALGORITHM.JAVA_STRHASHCODE);
				
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
				
				fileOutputPath = fileOutputPath.replace("$tagid", fileNameNew);
				
				ControllerOutput.exportData(dicomData, dicomFile, dictionary, fileOutputPath, new boolean[]{isTAG, isBIN, isBMP, isINF, isRAW, isDICOM});
			}
			else 
			{ 
				/*!!*/Debugger.log("Error [RUN]: can't parse dicom file " + fileInputPath); 	
			}
		}
	}

}
