package edu.hpc.andrey.zmask.data;

import java.util.Vector;

public class DatabaseDirectory 
{
	private int directoryID;
	private String directoryName;
	private String directoryPath;
	
	private Vector <DatabaseFile> listFiles;
	
	public DatabaseDirectory ()
	{
		listFiles = new Vector <DatabaseFile> ();
	}
	
	public int getSize ()
	{
		return listFiles.size();
	}
	
	public String getDirectoryName ()
	{
		return directoryName;
	}
	
	public String getDirectoryPath ()
	{
		return directoryPath;
	}
	
	public DatabaseFile getFile (int index)
	{
		if (index >= 0 && index < listFiles.size())
		{
			return listFiles.get(index);
		}
		
		return null;
	}
	
	public int getDirectoryID ()
	{
		return directoryID;
	}
	
	
	public void setDirectoryName (String name)
	{
		directoryName = name;
	}
	
	public void setDirectoryPath (String path)
	{
		directoryPath = path;
	}
	
	public void setDirectoryID (int id)
	{
		directoryID = id;
	}
	
	public void addFile (DatabaseFile file)
	{
		listFiles.addElement(file);
		
	}
}
