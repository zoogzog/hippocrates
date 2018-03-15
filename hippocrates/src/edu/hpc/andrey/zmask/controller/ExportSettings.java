package edu.hpc.andrey.zmask.controller;

public class ExportSettings 
{
	public String exportPatient;
	public String exportStudy;
	public String exportFile;

	public String outputPath;
	
	public boolean exportTAGS = false;
	public boolean exportDICOM = false;
	public boolean exportBINDECODE = false;
	public boolean exportBINENCODE = false;
	public boolean exportBMP = false;
	
	public static final int DBHASH_NONE = 0;
	public static final int DBHASH_INDEX = 0;
	public static final int DBHASH_UTU16 = 0;
	public static final int DBHASH_SHA256 = 0;
	
	public int exportDatabaseHashing = 0;
	
	public int imageTransformWindowWidth = 0;
	public int imageTransformWindowCenter = 0;
	public int imageTransformRescaleIntercept = 0;
	public int imageTransformRescaleSlope = 0;
}
