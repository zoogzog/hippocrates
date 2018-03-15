package edu.hpc.andrey.zmask.data;

import java.util.Vector;

public class DatabaseTable 
{
	private Vector <DatabaseDirectory> db;
	
	private boolean isDefaultDirectoryOpen;
		
	//-----------------------------------------------------
	
 	public DatabaseTable ()
	{
		db = new Vector <DatabaseDirectory> ();
		
		isDefaultDirectoryOpen = false;
	}
	
	//-----------------------------------------------------
 	
 	public void reset () 
 	{
 		db.removeAllElements();
 		
 		isDefaultDirectoryOpen = false;
 		
 		System.gc();
 	}
 	
 	/**
 	 * Add a file into the default directory structure
 	 * @param file
 	 */
 	public void addFile (DatabaseFile file)
 	{
 		//---- First, check if the element, in the table for
 		//---- representing files without directory 
 		if (isDefaultDirectoryOpen)
 		{
 			db.get(0).addFile(file);
 		}
 		else
 		{
 			DatabaseDirectory directoryDefault = new DatabaseDirectory();
 			directoryDefault.setDirectoryID(-1);
 			directoryDefault.setDirectoryName("UNKNOWN");
 			directoryDefault.setDirectoryPath("UNKNOWN");
 			
 			db.add(0, directoryDefault);
 			
 			db.get(0).addFile(file);
 			
 			isDefaultDirectoryOpen = true;
 		}
 	}
 	
 	/**
 	 * Add a file into a directory specified by its index
 	 * @param file
 	 * @param index
 	 */
 	public void addFile (DatabaseFile file, int index)
 	{
 		if (index >= 0 && index < db.size())
 		{
 			db.get(index).addFile(file);
 		}
 	}
 	
 	public void addDirectory (String directoryName, String directoryPath)
 	{
 		int index = db.size();
 		
 		db.addElement(new DatabaseDirectory());
 		db.lastElement().setDirectoryName(directoryName);
 		db.lastElement().setDirectoryPath(directoryPath);
 		db.lastElement().setDirectoryID(index);
 	}
	
	//-----------------------------------------------------
 	
 	public DatabaseDirectory getDirectory (int index)
 	{
 		if (index >= 0 && index < db.size())
 		{
 			return db.get(index);
 		}
 		
 		return null;
 	}
 	
 	public DatabaseFile getFile (int indexDirectory, int indexFile)
 	{
 		if (indexDirectory >= 0 && indexDirectory < db.size())
 		{
 			int fileCount = db.get(indexDirectory).getSize();
 			
 			if (indexFile >= 0 && indexFile < fileCount)
 			{
 				return db.get(indexDirectory).getFile(indexFile);
 			}
 		}
 		
 		return null;
 	}

 	public String[] getListDirectoryNames ()
 	{
 		String[] output = new String[db.size()];
 		
 		for (int k = 0; k < output.length; k++)
 		{
 			output[k] = db.get(k).getDirectoryName();
 		}
 		
 		return output;
 	}
 	
 	public String[] getListFilesInDirectory (int index)
 	{
 		if (index < 0 || index >= db.size()) { return null; }
 		
 		int fileCount = db.get(index).getSize();
 		
 		String[] output = new String[fileCount];
 		
 		for (int k = 0; k < fileCount; k++)
 		{
 			output[k] = db.get(index).getFile(k).getFileName();
 		} 
 		
 		return output;
 	}
 	
 	public boolean getIsLoaded ()
 	{
 		if (db.size() == 0) { return false; }
 		else { return true; }
 	}
 	
 	public int getSize ()
 	{
 		return db.size();
 	}
 	
 	public int getFileCountTotal ()
 	{
 		int sum = 0;
 		
 		for (int k = 0; k < db.size(); k++)
 		{
 			sum += db.get(k).getSize();
 		}
 		
 		return sum;
 	}
 	
 	/**
 	 * Returns an index pair [folder index, file index] of a random file for which the mask has not 
 	 * been set up yet. If all files have mask, then returns [-1, -1]
 	 * @return
 	 */
 	public int[] getFileNoMask ()
 	{
 		//---- This is a simple but slow way to do it. Maybe hash map, dedicated
 		//---- table should be used to speed up the search process. Try to run 
 		//---- on a high scale test.
 		for (int i = 0; i < db.size(); i++)
 		{
 			for (int j = 0; j < db.get(i).getSize(); j++)
 			{
 				if (!db.get(i).getFile(j).getIsMaskSaved())
 				{
 					return new int[] {i, j};
 				}
 			}
 		}
 		
 		return new int[] {-1, -1};
 	}
 	
	//-----------------------------------------------------
 	
 	public void debugPrint ()
 	{
 		System.out.println("Database contains " + db.size() + " directories");
 		
 		try
 		{
 		for (int k = 0; k < db.size(); k++)
 		{
 			System.out.println("----------------------------");
 			System.out.println("Directory [" + k + "]: " + db.get(k).getDirectoryName());
 			System.out.println("Directory contains " + db.get(k).getSize() + " files");
 			for (int j = 0; j < db.get(k).getSize(); j++)
 			{
 				System.out.println("--[" + j + "]-- " + db.get(k).getFile(j).getFileName());
 			}
 			
 			
 		}
 		}
 		catch(Exception e) { e.printStackTrace(); }
 		System.out.println("----------------------------");
 	}
 	
}
