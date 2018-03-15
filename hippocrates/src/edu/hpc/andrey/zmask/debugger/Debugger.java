package edu.hpc.andrey.zmask.debugger;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Debugger 
{
	private static boolean DEBUG_MODE = false;

	private static int LOG_MODE = 0;

	public static final int LOG_MODE_CONSOLE = 0;
	public static final int LOG_MODE_FILE = 1;

	private static PrintWriter outputStream = null;

	public static void switchDebugModeON ()
	{
		DEBUG_MODE = true;
	}

	public static void switchDebugModeOFF ()
	{
		DEBUG_MODE = false;
	}

	public static void switchLogMode (int mode)
	{
		LOG_MODE = mode;

		if (LOG_MODE == LOG_MODE_FILE)
		{
			String path = "log.txt";

			//---- Create new file stream for the log file
			if (outputStream == null)
			{
				try{outputStream = new PrintWriter(new FileWriter(path, false));} catch (Exception e) {}	
			}
		}
	}

	public static void log (String message)
	{
		if (!DEBUG_MODE) { return; }

		switch (LOG_MODE)
		{
		case LOG_MODE_CONSOLE: 
		{
			System.out.println(message);
			break;
		}

		case LOG_MODE_FILE:
		{
			if (outputStream != null) { outputStream.println(message); }
			break;
		}
		}
	}

	public static void log (Exception e)
	{
		String message = e.getMessage();

		log(message);
	}
}
