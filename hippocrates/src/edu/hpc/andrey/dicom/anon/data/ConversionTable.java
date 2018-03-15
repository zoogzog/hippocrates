package edu.hpc.andrey.dicom.anon.data;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * This class was made to store sensitive data to put later in some file.
 * Not used in version 1.1.0
 * @author Andrey
 */

public class ConversionTable 
{
	private Vector <ConversionTableElement> table = null;

	//--------------------------------------------------------

	public ConversionTable ()
	{
		table = new Vector <ConversionTableElement> ();
	}

	//--------------------------------------------------------

	public int getSize ()
	{
		return table.size();
	}

	public ConversionTableElement getElement (int index)
	{
		if (index < 0 || index > table.size()) { return null; }

		return table.get(index);
	}

	//--------------------------------------------------------

	public void addElement (ConversionTableElement element)
	{
		table.addElement(element);
	}

	public void addElement ()
	{
		table.addElement(new ConversionTableElement());
	}

	//--------------------------------------------------------

	public void saveTable (String filePath)
	{
		try
		{
			FileWriter outputFile = new FileWriter(filePath + ".csv", false);
			PrintWriter outputStream = new PrintWriter(outputFile);

			for (int i = 0;i < table.size(); i++)
			{
				outputStream.println("Original patient ID: " + table.get(i).filePathOriginal);
				outputStream.println("New assigned ID: " + table.get(i).patientIdNew);
				outputStream.println("Institution name: " + table.get(i).tagInstitutionName);
				outputStream.println("Performing physician's name: " + table.get(i).tagInstitutionName);
				outputStream.println("Analyzing physician's name: " + table.get(i).tagPhysicianStudyName);
				outputStream.println("Operator's name: " + table.get(i).tagOperatorName);
				outputStream.println("Patient's name: " + table.get(i).tagPatientName);
				outputStream.println("Issuer of patient's ID: " + table.get(i).tagIssuerPatientID);
				outputStream.println("Supplementary patient's ID: " + table.get(i).tagPatienSuplementaryID);
				outputStream.println("Patient's address: " + table.get(i).tagPatientAddress);
				outputStream.print("------------------------------------------------");
			}

		    outputStream.close();
		    outputFile.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


}
