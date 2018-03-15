package edu.hpc.andrey.zmask.data;

public class DatabaseFile 
{
	public static final int MONOCHROME1 = 1;
	public static final int MONOCHROME2 = 2;

	
	//---- Absolute path to the DICOM file
	private String filePath;
	
	//---- Name of the DICOM file
	private String fileName;
	
	//---- DICOM parameters
	private int dicomWindowWidth;
	private int dicomWidnowCenter;
	private int dicomIntercept;
	private int dicomSlope;
	private int dicomMonochromeMode;
	
	private boolean isMaskSaved;
	
	//-----------------------------------------------------
	
	public DatabaseFile ()
	{
		filePath = "";
		fileName = "";
		
		dicomWindowWidth = 0;
		dicomWidnowCenter = 0;
		dicomIntercept = 0;
		dicomSlope = 0;
		dicomMonochromeMode = 0;
		
		isMaskSaved = false;
	}
	
	//-----------------------------------------------------
	
	public String getFileName ()
	{
		return fileName;
	}
	
	public String getFilePath ()
	{
		return filePath;
	}
	
	public int getDicomWindowWidth ()
	{
		return dicomWindowWidth;
	}
	
	public int getDicomWindowCenter ()
	{
		return dicomWidnowCenter;
	}
	
	public int getDicomIntercept ()
	{
		return dicomIntercept;
	}
	
	public int getDicomSlope ()
	{
		return dicomSlope;
	}
	
	public int getDicomMonochromeMode ()
	{
		return dicomMonochromeMode;
	}
	
	public boolean getIsMaskSaved ()
	{
		return isMaskSaved;
	}
	
	//-----------------------------------------------------
	
	public void setFileName (String value)
	{
		fileName = value;
	}
	
	public void setFilePath (String value)
	{
		filePath = value;
	}
	
	public void setDicomWindowWidth (int value)
	{
		dicomWindowWidth = value;
	}
	
	public void setDicomWindowCenter (int value)
	{
		dicomWidnowCenter = value;
	}
	
	public void setDicomIntercept (int value)
	{
		dicomIntercept = value;
	}
	
	public void setDicomSlope (int value)
	{
		dicomSlope = value;
	}
	
	public void setDicomMonchromeMode (int value)
	{
		dicomMonochromeMode = value;
	}
	
	public void setIsMaskSaved (boolean value)
	{
		isMaskSaved = value;
	}
	
	//-----------------------------------------------------
	
	public void deubgPrint ()
	{
		System.out.println("Element->file path: " + filePath);
		System.out.println("Element->file name: " + fileName);
		System.out.println("Element->tag->window center: " + dicomWidnowCenter);
		System.out.println("Element->tag->window width: " + dicomWindowWidth);
		System.out.println("Element->tag->intercept: " + dicomIntercept);
		System.out.println("Element->tag->slope: " + dicomSlope);
		System.out.println("Element->is mask saved: " + isMaskSaved);
	}
	
	//-----------------------------------------------------
}
