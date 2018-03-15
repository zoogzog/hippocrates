package edu.hpc.andrey.dicomapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for storing extracted BMP file and patients data.
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

		List <String>sortedKeys=new ArrayList<String>(tagList.keySet());
		Collections.sort(sortedKeys);

		for (String key : sortedKeys)
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

	
	public byte[][] getImage ()
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

			if (strRescaleIntercept != null) { RESCALE_INTERCEPT = Integer.parseInt(strRescaleIntercept.trim()); }
			if (strRescaleSlope != null) { RESCALE_SLOPE = Integer.parseInt(strRescaleSlope.trim()); }
			if (strWindowWidth != null) { WINDOW_WIDTH = Integer.parseInt(strWindowWidth.trim()); }
			if (strWindowCenter != null) { WINDOW_CENTER = Integer.parseInt(strWindowCenter.trim()); }


			return DicomImageDecoder.convertToImage(dataImageBMP, WINDOW_CENTER, WINDOW_WIDTH, RESCALE_INTERCEPT, RESCALE_SLOPE);

		}
		catch (Exception e) { e.printStackTrace(); }

		return null;
	}

}
