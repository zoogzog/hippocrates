package edu.hpc.andrey.zmask.controller;

import javax.swing.SwingWorker;

import edu.hpc.andrey.zmask.data.DatabaseController;
import edu.hpc.andrey.zmask.data.DatabaseTable;
import edu.hpc.andrey.zmask.debugger.Debugger;
import edu.hpc.andrey.zmask.gui.form.FormMain;
import edu.hpc.andrey.zmask.gui.form.FormMainHandler;
import edu.hpc.andrey.zmask.gui.panel.TaskProgressController;


public class BackgroundController extends SwingWorker<Void, Void> 
{
	public static enum BackgroundTaskType {
		TASK_IMPORT_FILE,
		TASK_IMPORT_DIRECTORY,
		TASK_IMPORT_DATASET,
		TASK_IMPORT_DATABASEFILE,
		TASK_EXPORT_DATABASEFILE,
		TASK_ANONYMIZE_DATASET,
		NULL};

		//---- Links
		private FormMain linkFormMain = null;
		private FormMainHandler linkFormMainHandler = null;
		private static DatabaseTable linkDatabase = null;

		private BackgroundTaskType type = BackgroundTaskType.NULL;

		private String[] arguments = null;
		
		//---- Array to store 
		private String[] importFail = null;

		//----------------------------------------------------------------

		public BackgroundController ()
		{

		}

		//----------------------------------------------------------------

		public void setupEnvironment (FormMain formMain, FormMainHandler handler, DatabaseTable database)
		{
			linkFormMain = formMain;
			linkDatabase = database;
			linkFormMainHandler = handler;
		}


		public void setupTask (BackgroundTaskType taskType, String[] args)
		{
			type = taskType;
			arguments = args;
		}


		//----------------------------------------------------------------

		@Override
		protected Void doInBackground() throws Exception 
		{	
			importFail = null;
			
			TaskProgressController.statusLaunch();
			try
			{
			if (linkFormMain != null && linkDatabase != null && type != BackgroundTaskType.NULL)
			{
				switch (type)
				{
				case TASK_IMPORT_FILE: runImportFile(); break;
				case TASK_IMPORT_DIRECTORY: runImportDirectory(); break;
				case TASK_IMPORT_DATASET: runImportDataset(); break;
				case TASK_IMPORT_DATABASEFILE: runImportDatabaseFile(); break;
				case TASK_ANONYMIZE_DATASET: runAnonymizeDataset(); break;
				case TASK_EXPORT_DATABASEFILE: runExportDatabaseFile(); break;
				default: break;
				}
			}
			}
			catch (Exception e) { e.printStackTrace(); }

			return null;
		}

		@Override
		public void done()
		{		
			TaskProgressController.statusTerminate();
			TaskProgressController.statusReset();
			
			switch (type)
			{
			case TASK_IMPORT_FILE: linkFormMainHandler.callbackImportNewDatabaseFinished(importFail); break;
			case TASK_IMPORT_DIRECTORY: linkFormMainHandler.callbackImportNewDatabaseFinished(importFail); break;
			case TASK_IMPORT_DATASET: linkFormMainHandler.callbackImportNewDatabaseFinished(importFail); break;
			case TASK_IMPORT_DATABASEFILE: linkFormMainHandler.callbackImportNewDatabaseFinished(importFail); break;
			case TASK_ANONYMIZE_DATASET: linkFormMainHandler.callbackAnonymizeFinished(); break;
			case TASK_EXPORT_DATABASEFILE: linkFormMainHandler.callbackAnonymizeFinished(); break;
			default: break;
			}

			arguments = null;

		}

		//----------------------------------------------------------------

		private void runImportFile ()
		{
			if (arguments == null) 
			{ 
				/*!!*/Debugger.log("Info [BCK]: arguments for importing file are null "); 
				return;
			}

			/*!!*/Debugger.log("Info [BCK]: importing file " + arguments[0]);

			//---- Add this file to the table
			boolean isOK = DatabaseController.executeImportFile(linkDatabase, arguments[0]);
			
			if (!isOK)
			{
				/*!!*/Debugger.log("FAIL [BCK]: could not import file " + arguments[0]);
				
				importFail = new String[] {arguments[0]};
			}

			/*!!*/Debugger.log("Info [BCK]: database size: " + linkDatabase.getSize());

		}

		private void runImportDirectory ()
		{
			if (arguments == null) 
			{ 
				/*!!*/Debugger.log("Info [BCK]: arguments for importing files from directory are null "); 
				return;
			}

			/*!!*/Debugger.log("Info [BCK]: importing files from directory " + arguments[0]);

			String[] failList = DatabaseController.executeImportDirectory(linkDatabase, arguments[0]);
			
			if (failList != null)
			{
				for (int k = 0; k < failList.length; k++)
				{
					/*!!*/Debugger.log("FAIL [BCK]: could not import file " + failList[k]);
				}
				
				importFail = failList;
			}
			

			/*!!*/Debugger.log("Info [BCK]: database size: " + linkDatabase.getSize());
		}

		private void runImportDataset ()
		{
			try
			{

				if (arguments == null) 
				{ 
					/*!!*/Debugger.log("Info [BCK]: arguments for importing files from dataset are null "); 
					return;
				}

				/*!!*/Debugger.log("Info [BCK]: importing files from dataset " + arguments[0]);

				String[] failList = DatabaseController.executeImportDataset(linkDatabase, arguments[0]);
				
				if (failList != null)
				{
					for (int k = 0; k < failList.length; k++)
					{
						/*!!*/Debugger.log("FAIL [BCK]: could not import file " + failList[k]);
					}
					
					importFail = failList;
				}

				/*!!*/Debugger.log("Info [BCK]: database size: " + linkDatabase.getSize());
			}
			catch(Exception e) { e.printStackTrace(); }

		}

		private void runImportDatabaseFile ()
		{
			if (arguments == null)
			{
				/*!!*/Debugger.log("Info [BCK]: arguments for importing database file are null"); 
				return;
			}
			
			/*!!*/Debugger.log("Info [BCK]: importing from database file " + arguments[0]);
			
			DatabaseController.executeImportDatabaseFile(linkDatabase, arguments[0]);
		}

		private void runAnonymizeDataset ()
		{

			if (arguments == null)
			{
				/*!!*/Debugger.log("Info [BCK]: arguments for anonymizing dataset are null"); 
				return;
			}

			/*!!*/Debugger.log("Info [BCK]: anonymizing dataset, output location " + arguments[0]);

			//----- The flag order is the followin: isTAG, isBIN, isBMP, isINF, isRAW, isDICOM
			boolean[] flags = new boolean[]{false, false, true, false, false, true};

			//----- Run anonymizer wrapper
			Anonymizer.run(linkDatabase, arguments[0], flags);

		}

		//----------------------------------------------------------------
		
		private void runExportDatabaseFile ()
		{
			if (arguments == null)
			{
				/*!!*/Debugger.log("Info [BCK]: arguments for exporting database file are null"); 
				return;
			}
			
			/*!!*/Debugger.log("Info [BCK]: exporting database file to " + arguments[0]);
			
			DatabaseController.executeExportDatabaseFile(linkDatabase, arguments[0]);
		}
}
