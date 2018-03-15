package edu.hpc.andrey.dicom.anon.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * Class serves as a container to store paths to import and export files.
 * Also has a directory conversion table.
 * @author Andrey
 */

public class FileTable 
{
	//---- Path to the root directory or input dicom file
	private String pathRootDirImport;
	
	//---- Path to the export directory
	private String pathRootDirExport;
	
	//---- Table to convert directory names
	private Map <String, String> directoryLookupTable;
	
	//---- Paths to all input files: $rootdir/$dirname/$filename
	private Vector <String> filePathListImport;
	
	//---- Paths to all export files, file name is not specified: rootdir/$newdirname/
	private Vector <String> filePathListExport;
	
	//----------------------------------------------------------------------
	
	public FileTable ()
	{
		pathRootDirImport = "";
		pathRootDirExport = "";
		
		directoryLookupTable = new HashMap<String, String> ();
		
		filePathListImport = new Vector<String>();
		filePathListExport = new Vector<String>();
	}
	
	//----------------------------------------------------------------------
	
	public void reset ()
	{
		pathRootDirImport = "";
		pathRootDirExport = "";
		
		directoryLookupTable.clear();
		
		filePathListImport.removeAllElements();
		filePathListExport.removeAllElements();
	}
	
	//----------------------------------------------------------------------
	
	public int getCountFile ()
	{
		if (filePathListImport.size() != filePathListExport.size()) { return -1; }
		
		return filePathListImport.size();
	}
	
	public int getCountDirectory ()
	{
		return directoryLookupTable.size();
	}
	
	public String getPathRootDirImport ()
	{
		return pathRootDirImport;
	}
	
	public String getPathRootDirExport ()
	{
		return pathRootDirExport;
	}
	
	public String getFilePathImport (int index)
	{
		if (index >= 0 && index < filePathListImport.size())
		{
			return filePathListImport.get(index);
		}
		
		return "";
	}
	
	public String getFilePathExport (int index)
	{
		if (index >= 0 && index < filePathListExport.size())
		{
			return filePathListExport.get(index);
		}
		
		return "";
	}
	
	public String getPathDirNew (String pathDirOld)
	{
		if (directoryLookupTable.containsKey(pathDirOld))
		{
			return directoryLookupTable.get(pathDirOld);
		}
		
		return "";
	}
	
	public String[] getDirectoryListOld ()
	{
		String[] output = new String [directoryLookupTable.size()];
		
		int i = 0;
		for (String key:directoryLookupTable.keySet())
		{
			output[i] = key;
			i++;
		}
		
		return output;
	}
	
	public String[] getDirectoryListNew ()
	{
		String[] output = new String [directoryLookupTable.size()];
		
		int i = 0;
		for (String key:directoryLookupTable.keySet())
		{
			output[i] = directoryLookupTable.get(key);
			i++;
		}
		
		return output;
	}
	
	//----------------------------------------------------------------------
	
	public void setPathRootDirImport (String value)
	{
		pathRootDirImport = value;
	}
	
	public void setPathRootDirExport (String value)
	{
		pathRootDirExport = value;
	}
	
	//----------------------------------------------------------------------
	
	public void addFilePath (String pathImport, String pathExport)
	{
		filePathListImport.addElement(pathImport);
		filePathListExport.addElement(pathExport);
	}
	
	public void addPathDirNew (String pathDirOld, String pathDirNew)
	{
		directoryLookupTable.put(pathDirOld, pathDirNew);
	}
}
