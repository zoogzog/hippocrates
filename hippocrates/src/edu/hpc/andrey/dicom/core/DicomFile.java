package edu.hpc.andrey.dicom.core;

import java.util.Vector;

/**
 * This class is mainly used to preserve original data, then remove sensitive data from it
 * and later save as DICOM file.
 * @author Andrey
 */

public class DicomFile 
{
	private byte[] dataOriginal = null;
	
	private Vector <Integer> indexRemove;
	
	//----------------------------------------------------------------
	
	public DicomFile ()
	{
		indexRemove = new Vector <Integer> ();
	}
	
	//----------------------------------------------------------------
	
	/**
	 * Set up the raw data, grabbed form file
	 * @param data
	 */
	public void setDataRaw (byte[] data)
	{
		dataOriginal = new byte[data.length];
		
		for (int i = 0; i < data.length; i++)
		{
			dataOriginal[i] = data[i];
		}
	}
	
	/**
	 * Set the position of the mask to hide sensitive data
	 * @param position
	 * @param length
	 */
	public void setMask (int position, int length)
	{
		for (int i = 0; i < length; i++)
		{
			indexRemove.addElement(position + i);
		}
	}
	
	//----------------------------------------------------------------
	
	/**
	 * Get the original data grabbed from file
	 * @return
	 */
	public byte[] getDataOriginal ()
	{
		return dataOriginal;
	}
	
	/**
	 * Get data with masked sensitive areas (masked with '*')
	 * @return
	 */
	public byte[] getDataMasked ()
	{
		if (dataOriginal == null) { return null; }
		
		byte[] output = new byte[dataOriginal.length];
		
		for (int i = 0; i < dataOriginal.length; i++)
		{
			output[i] = dataOriginal[i];
		}
		
		for (int i = 0; i < indexRemove.size(); i++)
		{
			output[indexRemove.get(i)] = '*';
		}
		
		return output;
	}
}
