package edu.hpc.andrey.dicom.anon.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

import edu.hpc.andrey.dicom.core.DicomContainer;
import edu.hpc.andrey.dicom.core.DicomDictionary;
import edu.hpc.andrey.dicom.core.DicomFile;
import edu.hpc.andrey.dicom.core.DicomTagData;

/**
 * This class manages how the data will be saved. Several output formats are supported.
 * @author Andrey
 */

public class ControllerOutput 
{
	private static final String EXTENSION_TAG = ".tg";		//---- Save tags extracted from the DICOM file
	private static final String EXTENSION_BIN = ".bn";  	//---- Save binary data with JPEG conversion applied
	private static final String EXTENSION_RAW = ".rw";  	//---- Save raw binary data without any conversion applied
	private static final String EXTENSION_BMP = ".bmp"; 	//---- Save the image data as bmp file
	private static final String EXTENSION_INF = ".inf"; 	//---- Not used
	private static final String EXTENSION_DICOM = ".dcm";  	//---- Save DICOM file with all sensitive data removed 

	//-----------------------------------------------------------------------------------------

	/**
	 * Export extracted data into files. The files, which are extracted, are specified by flags.
	 * Flag array contains the following flags: [TAG, BIN, BMP, INF, RAW] 
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @param flags
	 * @return
	 */
	public static boolean exportData (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput, boolean[] flags)
	{
		boolean isSuccessTAG = true;
		boolean isSuccessBIN = true;
		boolean isSuccessBMP = true;
		boolean isSuccessINF = true;
		boolean isSuccessRAW = true;
		boolean isSuccessDICOM = true;

		if (flags.length < 6) { return false; }
		
		if (flags[0]) { isSuccessTAG = exportDataTAG (data, dicomFile, dictionary, pathOutput); }
		if (flags[1]) { isSuccessBIN = exportDataBIN (data, dicomFile, dictionary, pathOutput); }
		if (flags[2]) { isSuccessBMP = exportDataBMP (data, dicomFile, dictionary, pathOutput); }
		if (flags[3]) { isSuccessINF = exportDataINF (data, dicomFile, dictionary, pathOutput); }
		if (flags[4]) { isSuccessRAW = exportDataRAW (data, dicomFile, dictionary, pathOutput); }
		if (flags[5]) { isSuccessDICOM = exportDataDICOM(data, dicomFile, dictionary, pathOutput); }
		
		if (!isSuccessBIN || !isSuccessBMP || !isSuccessINF || !isSuccessTAG || !isSuccessRAW || !isSuccessDICOM) { return false; }
		else { return true; }
	}

	//-----------------------------------------------------------------------------------------
	
	/**
	 * Export all dicom tags into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataTAG (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_TAG;

		try
		{
			data.saveTagList(realPathOutput, dictionary, true);

			return true;
		}
		catch (Exception e) 
		{ 
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Export a decoded image data as binary bytes into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataBIN (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_BIN;

		short[][] imageData = data.getImageBMP();

		try
		{
			if (imageData != null)
			{
				FileOutputStream ostream  = new FileOutputStream(new File(realPathOutput));

				for (int i = 0; i < imageData.length; i++)
				{
					for (int j = 0; j < imageData[0].length; j++)
					{
						short value = imageData[i][j];

						byte left = (byte)((value & 0xFF00) >> 8);
						byte right = (byte) (value & 0x00FF);

						ostream.write(new byte[] {left, right});
					}
				}

				ostream.close();

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Export compressed image data (bytes) into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataRAW (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_RAW;
		
		byte[] dataRAW = data.getImageRaw();
		try
		{
			FileOutputStream ostream  = new FileOutputStream(new File(realPathOutput));
			
			ostream.write(dataRAW);
			ostream.close();
			
			return true;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Export a decoded image data as a bmp image into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataBMP (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_BMP;

		try
		{
			data.saveImage(realPathOutput);	

			return true;
		}
		catch (Exception e) 
		{ 
			//e.printStackTrace(); 
			return false;
		}
	}

	/**
	 * Export patient's private information into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataINF (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_INF;

		try
		{
			FileWriter outputFile = new FileWriter(realPathOutput, false);
			PrintWriter outputStream = new PrintWriter(outputFile);

			for  (int k = 0; k < DicomTagData.SENSITIVE_DATA_TAG_LIST.length; k++)
			{
				String tagCode = DicomTagData.SENSITIVE_DATA_TAG_LIST[k];

				String dicomDescription = dictionary.getValue(tagCode);

				if (!dicomDescription.equals(""))
				{
					outputStream.println(dicomDescription + ";" + data.getTagValue(tagCode));
				}
			}

			outputStream.close();
			outputFile.close();

			return true;
		}
		catch (Exception e) 
		{ 
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Export dicom file with masked fields of sensitive data
	 */
	private static boolean exportDataDICOM (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_DICOM;

		try
		{
			FileOutputStream ostream  = new FileOutputStream(new File(realPathOutput));
			
			ostream.write(dicomFile.getDataMasked());
			ostream.close();
			
			return true;
		}
		catch (Exception e) 
		{ 
			//e.printStackTrace();
			return false;
		}
	}
}
