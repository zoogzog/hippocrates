package edu.hpc.andrey.dicom.core;

import java.security.MessageDigest;

/**
 * Some helper functions used during DICOM decoding stage, plus an anonymization hash function
 * @author Andrey
 */

public class Utils 
{
	public static enum HASH_ALGORITHM {ALG_SHA256, ALG_INDEX, ALG_UTU16, SIMPLE_HASH, JAVA_STRHASHCODE, NONE};
	public static int DIRECTORY_INDEX_START = 1;
	public static int DIRECTORY_INDEX_OFFSET = 1;
	private static int CURRENT_INDEX = DIRECTORY_INDEX_START;
	
	public static int indexPattern( byte[] data, int start, int stop, byte[] pattern) 
	{
		if( data == null || pattern == null) { return -1; }

		int[] failure = computeFailure(pattern);

		int j = 0;

		for( int i = start; i < stop; i++) 
		{
			while (j > 0 && ( pattern[j] != '*' && pattern[j] != data[i])) 
			{
				j = failure[j - 1];
			}
			if (pattern[j] == '*' || pattern[j] == data[i]) 
			{
				j++;
			}
			if (j == pattern.length) 
			{
				return i - pattern.length + 1;
			}
		}
		return -1;
	}

	private static int[] computeFailure(byte[] pattern) 
	{
		int[] failure = new int[pattern.length];

		int j = 0;
		for (int i = 1; i < pattern.length; i++) 
		{
			while (j>0 && pattern[j] != pattern[i]) 
			{
				j = failure[j - 1];
			}
			if (pattern[j] == pattern[i]) 
			{
				j++;
			}
			failure[i] = j;
		}

		return failure;
	}

	//-----------------------------------------------------------------------------------------
	
	public static short[][] convertArrayToMatrix (int[] input, int rows, int cols)
	{
		if (input.length >= rows * cols)
		{
			short[][] output = new short[rows][cols];
			
			int index = 0;
			
			for (int row = 0; row < rows; row++)
			{
				for (int col = 0; col < cols; col++)
				{
					output[row][col] = (short) input[index];
					index++;
				}
			}
			
			return output;
		}
		
		return null;
	}

	//-----------------------------------------------------------------------------------------
	
	public static String anonymizeString (String input, HASH_ALGORITHM method)
	{
		if (method == HASH_ALGORITHM.NONE) { return input; }
		
		String output = "";
		
		try
		{
			switch (method)
			{
			//----------------------------------------------
			case ALG_SHA256: 
			{
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.update(input.getBytes());
				byte[] hexCode = messageDigest.digest();
				
				for (int k = 0; k < hexCode.length; k++)
				{
					output += String.format("%02x", hexCode[k]);
				}
				
				break;
			}
			//----------------------------------------------
			case ALG_INDEX:
			{	
			
				output = String.format("%010d", CURRENT_INDEX);
				CURRENT_INDEX += DIRECTORY_INDEX_OFFSET;
				
				break;
			}
			//----------------------------------------------
			case ALG_UTU16:
			{	
				int length = 16;
				
				//---- Hash transformation unique string (length < 16) to unique (hex representation) 
				byte[] hexCode = new byte[length];
	
				for (int k = length - 1, m = input.length() - 1; k >= 0; k--, m--)
				{
					if (m >= 0) { hexCode[k] = (byte) input.charAt(m); }
					else { hexCode[k] = -1; }
				}	
				
				for (int k = 0; k < hexCode.length; k++)
				{
					output += String.format("%02X", hexCode[k]);
				}
				
				break;
				
			}	
			//----------------------------------------------
			
			case SIMPLE_HASH:
			{
				  long h = 1125899906842597L; 
				  int len = input.length();

				  for (int i = 0; i < len; i++) 
				  {
				    h = 31*h + input.charAt(i);
				  }
				  
				  output = String.valueOf(Math.abs(h));
				
				break;
			
			}
			//----------------------------------------------
			
			case JAVA_STRHASHCODE:
			{
				
				int hash = Math.abs(input.hashCode());
				
				output = String.format("%010d", hash);
				
				break;
			}
			//----------------------------------------------
			default:
				break;
			
			
			}
		}
		catch (Exception e) {}
		
		return output;
	}

}
