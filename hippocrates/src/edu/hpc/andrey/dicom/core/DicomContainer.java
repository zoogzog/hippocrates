package edu.hpc.andrey.dicom.core;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Container for storing data extracted from DICOM file.
 * Contains: pixel data (decoded and encoded), tag list with values
 * @author Andrey
 */

public class DicomContainer 
{
	//---- DICOM decoded image data
	private short[][] dataImageBMP = null;

	//---- DICOM raw image data
	private byte[] dataImageRAW = null;

	//---- DICOM attributes
	private Map <String, String> tagList = new HashMap <String, String>();

	//-----------------------------------------------------------------------------------------

	public DicomContainer ()
	{

	}

	//-----------------------------------------------------------------------------------------

	public String getTagValue (String tag)
	{
		if (tagList.containsKey(tag))
		{
			return tagList.get(tag);
		}

		return "";
	}

	public String[] getTagList ()
	{
		String[] output = new String[tagList.size()];

		int index = 0;

		for (String key : tagList.keySet())
		{
			output[index] = key;
			index++;
		}

		return output;
	}

	public short[][] getImageBMP ()
	{
		return dataImageBMP;
	}

	public byte[] getImageRaw ()
	{
		return dataImageRAW;
	}

	public boolean isTagSet (String tag)
	{
		if (tagList.containsKey(tag)) { return true; }
		return false;
	}

	//-----------------------------------------------------------------------------------------

	public void setTag (String tag, String value)
	{
		tagList.put(tag, value);
	}

	public void setImageBMP (short[][] data)
	{
		int dimX = data.length;
		int dimY = data[0].length;

		dataImageBMP = new short[dimX][dimY];

		for (int i = 0; i < dimX; i++)
		{
			for (int j = 0; j < dimY; j++)
			{
				dataImageBMP[i][j] = data[i][j];
			}
		}
	}

	public void setImageRAW (byte[] data)
	{
		//--- Copy data here

		dataImageRAW = new byte[data.length];

		for (int i = 0; i < data.length; i++)
		{
			dataImageRAW[i] = data[i];
		}
	}

	//-----------------------------------------------------------------------------------------

	/**
	 * Save extracted tags to the specified file. Tags and their values separated by semicolon.
	 * @param outputFilePath
	 * @return
	 */
	public boolean saveTagList (String outputFilePath)
	{
		try 
		{
			FileWriter outputFile = new FileWriter(outputFilePath, false);
			PrintWriter outputStream = new PrintWriter(outputFile);

			List <String>sortedKeys=new ArrayList<String>(tagList.keySet());
			Collections.sort(sortedKeys);

			for (String key : sortedKeys)
			{
				String tag = key;
				String value = tagList.get(key);

				outputStream.println(tag + ";" + value);
			}

			outputStream.close();
			outputFile.close();

			return true;
		}
		catch (Exception e) { return false; }
	}

	/**
	 * Save extracted tags and their descriptions to the specified file. Tags, values, descriptions separated by semicolon.
	 * @param outputFilePath
	 * @param dictionary
	 * @param tagSkip -- if this array is not empty the values of the tags in the array will be removed 
	 * @return
	 */
	public boolean saveTagList (String outputFilePath, DicomDictionary dictionary, boolean isRemoveSensitiveTag)
	{
		try 
		{
			FileWriter outputFile = new FileWriter(outputFilePath, false);
			PrintWriter outputStream = new PrintWriter(outputFile);

			List <String>sortedKeys=new ArrayList<String>(tagList.keySet());
			Collections.sort(sortedKeys);

			for (String key : sortedKeys)
			{
				String tag = key;
				String value = tagList.get(key);

				String description = dictionary.getValue(tag);

				//---- Remove tag value if the flag is raised
				if (isRemoveSensitiveTag)
				{
					if (Arrays.asList(DicomTagData.SENSITIVE_DATA_TAG_LIST).contains(tag)){value = "*****";}
				}


				outputStream.println(tag + ";" + value + ";" + description);
			}

			outputStream.close();
			outputFile.close();

			return true;
		}
		catch (Exception e) { return false; }
	}

	/**
	 * Save extracted image to the specified file. Conversion algorithm is specified by the flag
	 * @param outputFilePath
	 */
	public void saveImage (String outputFilePath)
	{
		if (dataImageBMP != null)
		{
			try
			{
				String strRescaleIntercept = tagList.get(DicomTagData.TAG_RESCALE_INTERCEPT);
				String strRescaleSlope = tagList.get(DicomTagData.TAG_RESCALE_SLOPE);
				String strWindowWidth = tagList.get(DicomTagData.TAG_WINDOW_WIDTH);
				String strWindowCenter = tagList.get(DicomTagData.TAG_WINDOW_CENTER);
				
				int RESCALE_INTERCEPT = 0;
				int RESCALE_SLOPE = 1;
				int WINDOW_WIDTH = 255;
				int WINDOW_CENTER = 128;

				if (strRescaleIntercept != null) 
				{ 	
					if (strRescaleIntercept.contains(".")) { strRescaleIntercept = strRescaleIntercept.substring(0, strRescaleIntercept.indexOf(".")); }
					if (strRescaleIntercept.contains("\\")) { strRescaleIntercept = strRescaleIntercept.substring(0, strRescaleIntercept.indexOf("\\")); }
					RESCALE_INTERCEPT = Integer.parseInt(strRescaleIntercept.trim()); 
				}
				
				if (strRescaleSlope != null) 
				{ 
					if (strRescaleSlope.contains(".")) {strRescaleSlope = strRescaleSlope.substring(0, strRescaleSlope.indexOf(".")); }
					if (strRescaleSlope.contains("\\")) {strRescaleSlope = strRescaleSlope.substring(0, strRescaleSlope.indexOf("\\")); }
					RESCALE_SLOPE = Integer.parseInt(strRescaleSlope.trim()); 
				}
				
				if (strWindowWidth != null) 
				{ 
					if (strWindowWidth.contains(".")) { strWindowWidth = strWindowWidth.substring(0, strWindowWidth.indexOf(".")); }
					if (strWindowWidth.contains("\\")) { strWindowWidth = strWindowWidth.substring(0, strWindowWidth.indexOf("\\")); }
					WINDOW_WIDTH = Integer.parseInt(strWindowWidth.trim());
				}
				
				if (strWindowCenter != null) 
				{ 
					if (strWindowCenter.contains(".")) { strWindowCenter = strWindowCenter.substring(0, strWindowCenter.indexOf(".")); }
					if (strWindowCenter.contains("\\")) { strWindowCenter = strWindowCenter.substring(0, strWindowCenter.indexOf("\\")); }
					
					WINDOW_CENTER = Integer.parseInt(strWindowCenter.trim()); 
				}

				//There could be two modes of encoding image MONOCHROME1 and MONOCHROME2
				//MONOCHROME1 encodes images as intensities from bright to dark in ascending order 
				//MONOCHROME2 encodes images as intensities from dark to bright in ascending order
				
				boolean isInvert = false;
			
				if (getTagValue("00280004").equals("MONOCHROME1 ") || getTagValue("s00280004").equals("MONOCHROME1 "))
				{
					isInvert = true;
				}
				
				Mat imageMatrix = DicomImageDecoder.convertToImage(dataImageBMP, WINDOW_CENTER, WINDOW_WIDTH, RESCALE_INTERCEPT, RESCALE_SLOPE, isInvert);

				Imgcodecs.imwrite(outputFilePath, imageMatrix);
			}
			catch (Exception e) { e.printStackTrace(); }
		}

	}

	//-----------------------------------------------------------------------------------------
	
	public Mat getImage ()
	{
		try
		{
			String strRescaleIntercept = tagList.get(DicomTagData.TAG_RESCALE_INTERCEPT);
			String strRescaleSlope = tagList.get(DicomTagData.TAG_RESCALE_SLOPE);
			String strWindowWidth = tagList.get(DicomTagData.TAG_WINDOW_WIDTH);
			String strWindowCenter = tagList.get(DicomTagData.TAG_WINDOW_CENTER);

			int RESCALE_INTERCEPT = 0;
			int RESCALE_SLOPE = 1;
			int WINDOW_WIDTH = 255;
			int WINDOW_CENTER = 128;
			
			if (strRescaleIntercept != null) 
			{ 	
				if (strRescaleIntercept.contains(".")) { strRescaleIntercept = strRescaleIntercept.substring(0, strRescaleIntercept.indexOf(".")); }
				if (strRescaleIntercept.contains("\\")) { strRescaleIntercept = strRescaleIntercept.substring(0, strRescaleIntercept.indexOf("\\")); }
				RESCALE_INTERCEPT = Integer.parseInt(strRescaleIntercept.trim()); 
			}
			
			if (strRescaleSlope != null) 
			{ 
				if (strRescaleSlope.contains(".")) {strRescaleSlope = strRescaleSlope.substring(0, strRescaleSlope.indexOf(".")); }
				if (strRescaleSlope.contains("\\")) {strRescaleSlope = strRescaleSlope.substring(0, strRescaleSlope.indexOf("\\")); }
				RESCALE_SLOPE = Integer.parseInt(strRescaleSlope.trim()); 
			}
			
			if (strWindowWidth != null) 
			{ 
				if (strWindowWidth.contains(".")) { strWindowWidth = strWindowWidth.substring(0, strWindowWidth.indexOf(".")); }
				if (strWindowWidth.contains("\\")) { strWindowWidth = strWindowWidth.substring(0, strWindowWidth.indexOf("\\")); }
				WINDOW_WIDTH = Integer.parseInt(strWindowWidth.trim());
			}
			
			if (strWindowCenter != null) 
			{ 
				if (strWindowCenter.contains(".")) { strWindowCenter = strWindowCenter.substring(0, strWindowCenter.indexOf(".")); }
				if (strWindowCenter.contains("\\")) { strWindowCenter = strWindowCenter.substring(0, strWindowCenter.indexOf("\\")); }
				
				WINDOW_CENTER = Integer.parseInt(strWindowCenter.trim()); 
			}

			//There could be two modes of encoding image MONOCHROME1 and MONOCHROME2
			//MONOCHROME1 encodes images as intensities from bright to dark 
			//MONOCHROME2 encodes images as intensities from dark to bright
			
			boolean isInvert = false;
		
			if (getTagValue("00280004").equals("MONOCHROME1 ") || getTagValue("s00280004").equals("MONOCHROME1 "))
			{
				isInvert = true;
			}
			
			Mat imageMatrix = DicomImageDecoder.convertToImage(dataImageBMP, WINDOW_CENTER, WINDOW_WIDTH, RESCALE_INTERCEPT, RESCALE_SLOPE, isInvert);

			return imageMatrix;
		}
		catch (Exception e) { e.printStackTrace(); }

		return null;
	}

	public Mat getImage (int WINDOW_WIDTH, int WINDOW_CENTER, int RESCALE_SLOPE, int RESCALE_INTERCEPT)
	{
		try
		{
			//There could be two modes of encoding image MONOCHROME1 and MONOCHROME2
			//MONOCHROME1 encodes images as intensities from bright to dark in ascending order 
			//MONOCHROME2 encodes images as intensities from dark to bright in ascending order
			
			boolean isInvert = false;
		
			if (getTagValue("00280004").equals("MONOCHROME1 ") || getTagValue("s00280004").equals("MONOCHROME1 "))
			{
				isInvert = true;
			}
			
			Mat imageMatrix = DicomImageDecoder.convertToImage(dataImageBMP, WINDOW_CENTER, WINDOW_WIDTH, RESCALE_INTERCEPT, RESCALE_SLOPE, isInvert);
			

			return imageMatrix;
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

	public int getImageWidth ()
	{
		return dataImageBMP[0].length;
	}
	
	public int getImageHeight ()
	{
		return dataImageBMP.length;
	}
	
	public int getMonochromeMode ()
	{
		if (getTagValue("00280004").equals("MONOCHROME1 ") || getTagValue("s00280004").equals("MONOCHROME1 "))
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}
}
