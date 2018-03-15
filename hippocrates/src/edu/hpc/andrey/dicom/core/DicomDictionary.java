package edu.hpc.andrey.dicom.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a singleton class, only one dictionary should exist during execution. 
 * @author Andrey
 *
 */
public class DicomDictionary 
{
	private static DicomDictionary instance = null;
	
	private Map <String, String> dictionary = new HashMap <String, String>();

	private DicomDictionary ()
	{
		try
		{
			BufferedReader inputStream =new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("dicom-dict.txt")));

			String line = "";

			while ((line = inputStream.readLine()) != null) 
			{
				String[] data = line.split(";");
				
				if (data.length >= 2)
				{
					dictionary.put(data[0], data[1]);
				}
			}

		}
		catch (Exception e) {}
	}
	
	public static DicomDictionary getInstance ()
	{
		if(instance == null) { instance = new DicomDictionary(); }
		
		return instance;
	}
	
	

	public String getValue (String key)
	{
		if (dictionary.containsKey(key)) { return dictionary.get(key); }
		
		return "";
	}
}


