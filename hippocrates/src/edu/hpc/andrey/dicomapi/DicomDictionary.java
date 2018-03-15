package edu.hpc.andrey.dicomapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DicomDictionary 
{
	private Map <String, String> dictionary = new HashMap <String, String>();

	public DicomDictionary (String filePath)
	{
		try
		{
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(
			         this.getClass().getResourceAsStream("dicom-dict.txt")));

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
		catch (Exception e) { e.printStackTrace();}
	}

	public String getValue (String key)
	{
		if (dictionary.containsKey(key)) { return dictionary.get(key); }
		
		return "";
	}
}


