package edu.hpc.andrey.dicomapi;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * This class reads an input DICOM file and extracts image and text.
 * Some methods of this class are copied from this http://www.codeproject.com/script/Articles/ViewDownloads.aspx?aid=36014
 * @author Andrey
 */
public class DicomDecoder 
{	
	private static boolean DEBUG_MODE = false;

	private static final String TAG_PIXEL_DATA = "7fe00010";
	private static final String TAG_ENDFILE = "fffee0dd";
	private static final String TAG_IMAGE_ROWS = "00280010";
	private static final String TAG_IMAGE_COLS = "00280011";
	private static final String TAG_TRANSFER_SYNTAX = "00020010";
	
	private static final String TRANSFER_SYNTAX_JPEGLOSSLESS = "1.2.840.10008.1.2.4.70";

	private static String tagCurrent = "";

	//---- Header preamble size (offset) and prefix length
	private static final int HEADER_PREAMBLE_OFFSET = 128;
	private static final int HEADER_PREFIX_LENGTH = 4;

	private static int parserPointer = 1;

	private static boolean isLogON = false;
	private static PrintWriter outputStream;
	
	private static boolean isSequence = false;
	

	static boolean isMaskSensitiveData = false;
	
	//---- Supported DICOM value representations
	//---- Refer to the following mink ftp://dicom.nema.org/medical/Dicom/2014a/output/chtml/part05/sect_6.2.html

	private static final String[] DICOMVR = 
		{
				"AE",	//---- Application Entity, a string of chars, 16 bytes max
				"AS", 	//---- Age String, a string of chars, 4 bytes fixed
				"AT",	//---- Attribute Tag, ordered pair of 16-bit uint, 4 bytes fixed
				"CS", 	//---- Code string, a string of chars, 16 bytes max
				"DA", 	//---- Date, a string of chars: YYYYMMDD, 8 bytes fixed
				"DS",	//---- Decimal String, a string of char, 16 bytes max 
				"DT", 	//---- Date Time, a string of char: YYYYMMDDDHHMMSS.FFFFFF&ZZXX, 26 bytes max
				"FL", 	//---- Floating Point Single, float, 4 bytes fixed
				"FD",	//---- Floating Point Double, double, 8 bytes fixed
				"IS",	//---- Integer String, a string of char, 12 bytes max
				"LO",	//---- Long String, a string of char, 64 bytes max
				"LT",	//---- Long Text, a string of char, 10240 bytes max
				"OB",	//---- Other Byte String, a string of bytes
				"OD",	//---- Other Double String, a string of 64-bit floating points
				"OF", 	//---- Other Float String, a string of 32-bit floating points
				"OW",	//---- Other Word String, a string of 16-bit words
				"PN",	//---- Person Name, a string of char, 64 bytes max
				"SH",	//---- Short String, a char string, 16 bytes max
				"SL",	//---- Signed Long, 4 bytes fixed
				"SQ",	//---- Sequence of items, 
				"SS",	//---- Signed Short, signed binary int, 2 bytes
				"ST",	//---- Short Text, a string of char, 1024 char max
				"TM",	//---- Time, a string of char HHMMSS.FFFFFF, 16 bytes max
				"UI",	//---- Unique ID, a string of char, 64 bytes max
				"UL",	//---- Unsigned Long, 32-bit int, 4 bytes 
				"UN",	//---- Unknown
				"US",	//---- Unsigned Short, 16-bit int, 2 bytes
				"UT",	//---- Unlimited text

		};

	//---- These VR, which require encoding with additional reserved bytes
	private static final String[] DICOMVR_EXTENDED =
		{
				"OB", "OW", "SQ", "UN"
		};
	
	public static void setDebugMode (boolean value)
	{
		DEBUG_MODE = value;
	}
	
	//-----------------------------------------------------------------------------------------

	/**
	 * This method performs conversion of the DICOM file into BMP image and additional data
	 * @param filePath
	 */
	public static boolean convertDICOM (String filePath, DicomContainer output, DicomFile dicomFile, DicomDictionary dictionary, boolean isSaveLog)
	{				
		//---- Check if DICOM file exists
		File inputFile = new File(filePath);

		if (!inputFile.exists()) {return false; }

		try
		{
			if (isSaveLog)
			{
				isLogON = true;	
				outputStream = new PrintWriter(new FileWriter("log.csv", false));
			}
			
			boolean isSuccess = false;

			//---- Read binary file and then parse it
			byte[] byteData = Files.readAllBytes(Paths.get(filePath));
			
			//---- Save the raw data to the file container
			dicomFile.setDataRaw(byteData);

			//---- Parse header, skip preamble, check prefix
			isSuccess = parseHeaderPreamble(byteData);
			if (!isSuccess) { throw new Exception(); }

			isSuccess = parseHeaderPrefix(byteData);
			if (!isSuccess) { throw new Exception(); }

			//---- Parse data set
			isSuccess = parseDataSet(byteData, dictionary, output, dicomFile);
			if (!isSuccess) { throw new Exception();}
			
			if (isLogON) { outputStream.close(); }
			
		}
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
			return false; 
		}

		return true;
	}

	//-----------------------------------------------------------------------------------------

	private static boolean parseHeaderPreamble (byte[] data)
	{
		if (data.length < HEADER_PREAMBLE_OFFSET + HEADER_PREFIX_LENGTH + 1) { return false; }

		parserPointer = HEADER_PREAMBLE_OFFSET;

		return true;
	}

	private static boolean parseHeaderPrefix (byte[] data)
	{
		String prefix = "";

		for (int i = 0; i < HEADER_PREFIX_LENGTH; i++)
		{
			prefix += (char) data[parserPointer];
			parserPointer++;
		}

		if (prefix.equals("DICM")) { return true; }
		else { return false; }
	}

	//---- Parse all data elements in this file
	private static boolean parseDataSet (byte[] data, DicomDictionary dictionary, DicomContainer output, DicomFile dicomFile) throws Exception
	{

		while (parserPointer < data.length)
		{
			parseDataElement(data, dictionary, output, dicomFile);
		}
		return true;
	}

	private static void parseDataElement (byte[] data, DicomDictionary dictionary, DicomContainer output, DicomFile dicomFile) throws Exception
	{
		String elementTAG = parseTag(data);
		if (DEBUG_MODE) { System.out.print(elementTAG + " "); }
		
		tagCurrent = elementTAG;
		
		//---- Check EOF, if EOF force exit
		if (tagCurrent.equals(TAG_ENDFILE)) { parserPointer = data.length + 1; return; } 
		
		String elementVR = parseVR (data);
		if (DEBUG_MODE) { System.out.print(elementVR + " "); }
		
		int elementVL = parseVL(data, dicomFile, elementVR);
		if (DEBUG_MODE) { System.out.print(elementVL + " "); }
		
		String elementVF = parseVF(data, elementVR, elementVL, dictionary, output, dicomFile);
		if (DEBUG_MODE) { System.out.print(elementVF + " "); }
		
		String elementDescriptor = dictionary.getValue(elementTAG);
		if (DEBUG_MODE) { System.out.println(" >> " + elementDescriptor); }	
		
		//---- Store tags, if parsing the main sequence
		if (!isSequence) 
		{
			output.setTag(elementTAG, elementVF);
		}
		else
		{
			output.setTag("s" + elementTAG, elementVF);
		}
		
		if (isLogON)
		{
			outputStream.println(elementTAG + ";" + elementVR + ";b=" + elementVL + ";" + elementVF + ";" + elementDescriptor); 
		}
	}
	
	
	//---- Parse tag: the description of the data element
	private static String parseTag (byte[] data)
	{
		//---- Tag is encoded in little endian byte ordering
		//---- Tag has only 4 bytes, 2 bytes for group number, followed
		//---- by 2 bytes representing element number
		int tagLength = 4;

		byte[] tagByte = new byte[tagLength];

		//---- Grab group number
		tagByte[1] = data[parserPointer];
		parserPointer++;

		tagByte[0] = data[parserPointer];
		parserPointer++;

		//---- Grab element number
		tagByte[3] = data[parserPointer];
		parserPointer++;

		tagByte[2] = data[parserPointer];
		parserPointer++;

		String outputTag = "";

		for (int i = 0; i < tagByte.length; i++)
		{
			outputTag += String.format("%02x", tagByte[i]);
		}

		//---- Turn masking ON if this TAG belongs to sensitive data tag list
		if (Arrays.asList(DicomTagData.SENSITIVE_DATA_TAG_LIST).contains(outputTag)) 
		{ 
			isMaskSensitiveData = true;
		};
		
		return outputTag;
	}

	//---- Parse value representation: type of data used for encoding this data element
	private static String parseVR (byte[] data) throws Exception
	{
		//---- VR is stored in normal order (big endian)
		//---- Represented as char
		int vrLength = 2;

		String outputVR = "";

		for (int i = 0; i < vrLength; i++)
		{
			outputVR += (char)data[parserPointer];
			parserPointer++;
		}

		//---- Check if this VR exists
		boolean isDICOMVR = Arrays.asList(DICOMVR).contains(outputVR);

		
		if (!isDICOMVR) 
		{ 
			//---- Here we attempt to circumvent the error, spotted in one of the files
			//---- ERROR: Overflow of LO container with 8 additional bytes: fe ff 00 e0 d6 00 00 00
			//---- In this case trying to spot the FEFF00E0 part, adding +4 bytes
			
			if (tagCurrent.equals("fffee000")) 
			{  }
			else
			{
			throw new Exception(); 
			}
			
			
		}

		return outputVR;
	}

	//---- Parse value length: the number of bytes used for this data element
	private static int parseVL (byte[] data, DicomFile dicomFile, String VR)
	{
		int elementLength = 0;

		//---- Check if this VR requires extended parsing
		boolean isEXTDICOMVR = Arrays.asList(DICOMVR_EXTENDED).contains(VR);

		//---- If this VR requires extended parsing, first skip two bytes
		if (isEXTDICOMVR) { parserPointer++; parserPointer++; }

		//---- Next, parse value length as 4 bytes if extended, 2 bytes if normal
		if (isEXTDICOMVR) 
		{
			int byte1 = data[parserPointer]; parserPointer++;
			int byte2 = data[parserPointer]; parserPointer++;
			int byte3 = data[parserPointer]; parserPointer++;
			int byte4 = data[parserPointer]; parserPointer++;

			elementLength = (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
		}
		else
		{
			
			byte byte1 = data[parserPointer]; parserPointer++;
			byte byte2 = data[parserPointer]; parserPointer++;
			
			//elementLength = (byte2 << 8) + byte1;
			elementLength = ByteBuffer.wrap(new byte[] {byte1, byte2, 0, 0}).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFF;
		}

		//---- Masking sensitive data in the original file here
		if (isMaskSensitiveData)
		{	
			dicomFile.setMask(parserPointer, elementLength);		
			isMaskSensitiveData = false;
		}

		return elementLength;
	}

	//---- Parse value field: bytes corresponding to the value of this data element
	//---- Returns element as string (double, integer values are also converted presented as string)
	private static String parseVF (byte[] data, String VR, int VL, DicomDictionary dictionary, DicomContainer output, DicomFile dicomFile) throws Exception
	{
		String outputVF = "";

		switch (VR)
		{
			//-------------------------------------
			//---- Application Entity, a string of chars, 16 bytes max
			case "AE": 
				outputVF = extractString(data, VL);
				break;
			//-------------------------------------
			//---- Age String, a string of chars, 4 bytes fixed
			case "AS": 
				outputVF = extractString(data, VL);
				break; 	
			//-------------------------------------
			//---- Attribute Tag, ordered pair of 16-bit uint, 4 bytes fixed
			case "AT": 
				outputVF = extractByteString(data, VL);
				break;	
			//-------------------------------------
			//---- Code string, a string of chars, 16 bytes max
			case "CS": 
				outputVF = extractString(data, VL);
				break; 
			//-------------------------------------
			//---- Date, a string of chars: YYYYMMDD, 8 bytes fixed
			case "DA": 
				outputVF = extractString(data, VL);
				break; 	
			//-------------------------------------
			//---- Decimal String, a string of char, 16 bytes max 
			case "DS": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Date Time, a string of char: YYYYMMDDDHHMMSS.FFFFFF&ZZXX, 26 bytes max
			case "DT": 
				outputVF = extractString(data, VL);
				break; 
			//-------------------------------------
			//---- Floating Point Single, float, 4 bytes fixed
			case "FL": 
				outputVF = extractF32(data, VL);
				break; 	
			//-------------------------------------
			//---- Floating Point Double, double, 8 bytes fixed
			case "FD": 
				outputVF = extractD64(data, VL);
				break;	
			//-------------------------------------
			//---- Integer String, a string of char, 12 bytes max
			case "IS": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Long String, a string of char, 64 bytes max
			case "LO": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Long Text, a string of char, 10240 bytes max
			case "LT": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Other Byte String, a string of bytes
			case "OB": 
				if (tagCurrent.equals(TAG_PIXEL_DATA)) { extractPixelData(data, VL, output); }
				else { outputVF = extractByteString(data, VL); }
				break;	
			//-------------------------------------
			//---- Other Double String, a string of 64-bit floating points
			case "OD": 
				outputVF = extractD64(data, VL);
				break;	
			//-------------------------------------
			//---- Other Float String, a string of 32-bit floating points
			case "OF": 
				outputVF = extractF32(data, VL);
				break; 	
			//-------------------------------------
			//---- Other Word String, a string of 16-bit words
			case "OW":
				if (isSequence) {extractJFIFDATA(data, VL);}
				else {extractPixelData(data, VL, output); }
				break;	
			//-------------------------------------
			//---- Person Name, a string of char, 64 bytes max
			case "PN": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Short String, a char string, 16 bytes max
			case "SH": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Signed Long, 4 bytes fixed
			case "SL": 
				outputVF = extractS32(data, VL);
				break;	
			//-------------------------------------
			//---- Sequence of items (nested)
			case "SQ":
				isSequence = true;
				
				//---- Nested data elements: ftp://dicom.nema.org/medical/Dicom/2014a/output/chtml/part05/sect_7.5.html
				if (DEBUG_MODE) { System.out.println(); }
				if (DEBUG_MODE) { System.out.println("--------------->>"); }
				
				//---- Skip 2 bytes
				parserPointer+= 8;
				parseSQ(data, VL - 8, dictionary, output, dicomFile);
				if (DEBUG_MODE) { System.out.println("<<---------------"); }
				
				isSequence = false;
				break;	
			
			//-------------------------------------
			//---- Signed Short, signed binary int, 2 bytes
			case "SS": 
				outputVF = extractS16(data, VL);
				break;	
			//-------------------------------------
			//---- Short Text, a string of char, 1024 char max
			case "ST": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Time, a string of char HHMMSS.FFFFFF, 16 bytes max
			case "TM": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Unique ID, a string of char, 64 bytes max
			case "UI": 
				//---- Here, drop the last byte, cause it is NULL filling
				outputVF = extractString(data, VL);
				if (data[parserPointer - 1] == 0 && outputVF.length() > 2) { outputVF = outputVF.substring(0, outputVF.length() - 1); }
				break;	
			//-------------------------------------
			//---- Unsigned Long, 32-bit int, 4 bytes 
			case "UL": 
				outputVF = extractU32(data, VL);
				break;	
			//-------------------------------------
			//---- Unknown
			case "UN": 
				outputVF = extractString(data, VL);
				break;	
			//-------------------------------------
			//---- Unsigned Short, 16-bit int, 2 bytes
			case "US": 
				outputVF = extractU16(data, VL);
				break;
			//-------------------------------------
			//---- Unlimited text
			case "UT": 
				outputVF = extractString(data, VL);
				break;	
		}
		
		return outputVF;
	}
	
	private static void parseSQ (byte[] data, int VL, DicomDictionary dictionary, DicomContainer output, DicomFile dicomFile) throws Exception
	{
		int parserPointerStart = parserPointer;
		
		do
		{
			parseDataElement(data, dictionary, output, dicomFile);
		}
		while (parserPointer - parserPointerStart < VL);
	}
	
	//-----------------------------------------------------------------------------------------
	
	private static String extractString (byte[] data, int length)
	{
		
		String output = "";
		
		for (int i = 0; i < length; i++)
		{
			output += (char)data[parserPointer];
			parserPointer++;
		}
		
		return output;
	}
	
	private static String extractByteString (byte[] data, int length)
	{
		String output = "";
		
		for (int i = 0; i < length; i++)
		{
			output += String.format("%02x", data[parserPointer]);
			parserPointer++;
		}
		
		return output;
	}
	
	/**
	 * Extract a 4-byte unsigned integer
	 * @param data
	 * @param length
	 * @return
	 */
	private static String extractU32 (byte[] data, int length)
	{
		//---- I don't understand why sometimes it is zero, while in the specification
		//---- it is clearly said it should be always 4 bytes
		//---- For example in case of UL in one of the files it was zero and caused errors.
		if (length == 0) { return ""; }
		
		int value = 0;
		
		//---- Little endian conversion
		byte[] hexCODE = new byte[4];
		
		for (int i = 0; i < 4; i++)
		{
			hexCODE[i] = data[parserPointer];
			parserPointer++;
		}
		
		//---- Convert byte array into an unsigned int with little endian order
		value = ByteBuffer.wrap(hexCODE).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFF;
		
		return Integer.toUnsignedString((int)value);
	}

	/**
	 * Extract a 2-byte unsigned integer
	 * @param data
	 * @param length
	 * @return
	 */
	private static String extractU16 (byte[] data, int length)
	{
		int value = 0;
		
		//---- Copy data
		byte[] hexCODE = new byte[2];
		
		for (int i = 0; i < 2; i++)
		{
			hexCODE[i] = data[parserPointer];
			parserPointer++;
		}
		
		//---- Skip the rest
		if(length > 2) { parserPointer += length - 2; }
		
		//---- Convert byte array of hex codes into unsigned short with little endian order
		value = ByteBuffer.wrap(hexCODE).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
		
		return Integer.toUnsignedString((int)value);
	}
	
	/**
	 * Extract a 4-byte signed integer
	 * @param data
	 * @param length
	 * @return
	 */
	private static String extractS32 (byte[] data, int length)
	{
		int value = 0;
		
		byte[] hexCODE = new byte[4];
		
		for (int i = 0; i < 4; i++)
		{
			hexCODE[i] = data[parserPointer];
			parserPointer++;
		}
			
		//---- Convert byte array of hex codes into signed int with little endian order
		value = ByteBuffer.wrap(hexCODE).order(ByteOrder.LITTLE_ENDIAN).getInt();
		
		return Integer.toString(value);
	}
	
	/**
	 * Extract a 2-byte signed integer
	 * @param data
	 * @param length
	 * @return
	 */
	private static String extractS16 (byte[] data, int length)
	{
		int value = 0;
		
		//---- Copy data
		byte[] hexCODE = new byte[2];
		
		for (int i = 0; i < 2; i++)
		{
			hexCODE[i] = data[parserPointer];
			parserPointer++;
		}
		
		if(length > 2) { parserPointer += length - 2; }

		//----- Convert byte array of hex codes into signed short with little endian order
		value = ByteBuffer.wrap(hexCODE).order(ByteOrder.LITTLE_ENDIAN).getShort();
		
		return Integer.toString((int)value);
	}

	/**
	 * Extract a 4-byte float
	 * @param data
	 * @param length
	 * @return
	 */
	private static String extractF32 (byte[] data, int length)
	{
		byte[] valueByte = new byte[4];
		
		for (int i = 0; i < 4; i++)
		{
			valueByte[i] = data[parserPointer];
			parserPointer++;
		}
		
		//---- Convert array of hex codes into float x32-bit with little endian order
		float valueFloat = ByteBuffer.wrap(valueByte).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		
		return String.valueOf(valueFloat);
	}
	
	/**
	 * Extract a 8-byte double
	 * @param data
	 * @param length
	 * @return
	 */
	private static String extractD64 (byte[] data, int length)
	{
		byte[] valueByte = new byte[8];
		
		for (int i = 0; i < 8; i++)
		{
			valueByte[i] = data[parserPointer];
			parserPointer++;
		}
		
		//---- Skip the rest
		if(length > 8) { parserPointer += length - 8; }
		
		
		//---- Convert array of hex codes into double x64-bit with little endian order
		double valueDouble = ByteBuffer.wrap(valueByte).order(ByteOrder.LITTLE_ENDIAN).getDouble();
		
		return String.valueOf(valueDouble);
	}
	
	private static void extractJFIFDATA (byte[] data, int length)
	{	
		//---- Scan here and locate JFIF file
		
		//---- The JFIF file starts with FF D8 FF E0 ?? ?? 4A 46 49 46 00
		//byte[] maskSTART = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
			
		//---- The JFIF file ends with FF D9
		byte[] maskEND = {(byte) 0xFF, (byte) 0xD9};
		
		//int pointerSTART = indexPattern(data, parserPointer, data.length, maskSTART);
		int pointerEND = indexPattern(data, parserPointer, data.length, maskEND);
	
		parserPointer = pointerEND + 2;
	
		//---- Should be even length, append if not even
		if (parserPointer %2 != 0) { parserPointer++; }
	}
	
	private static void extractPixelData (byte[] data, int length, DicomContainer output)
	{
		//---- Check transfer syntax. 
		//---------------------------------------------------
		//---- 1.2.840.10008.1.2.4.70 JPEG Lossless, Nonhierarchical (Processes 14 [Selection 1])
		//---- Use JPEGLosslessDecoder for decoding data with this format
		//---- https://github.com/rii-mango/JPEGLosslessDecoder
		//---- Check the decoder.jar
		
		//---- Current transfer syntax
		String transferSyntax = output.getTagValue(TAG_TRANSFER_SYNTAX);
		
		if (transferSyntax.equals(TRANSFER_SYNTAX_JPEGLOSSLESS))
		{
			//---- JPEG SOI is 0xFFD8, locate it in our bytes
			byte[] maskSTART = {(byte) 0xFF, (byte) 0xD8};
			int pointerSTART = indexPattern(data, parserPointer, data.length, maskSTART);
		
			parserPointer = pointerSTART;
			
			byte[] dataRAW = new byte[data.length - parserPointer];
			
			for (int i = parserPointer, j = 0; i < data.length; i++, j++)
			{
				dataRAW[j] = data[i];
			}
			
			//---- Send the byte array for decoding and receive decoded image data
			int[] dataIMG = DicomImageDecoder.decodeJPEG(dataRAW);
			
			//---- Try to locate rows out of sequence, if not, find in sequence (for those weird cases)
			String strRows = output.getTagValue(TAG_IMAGE_ROWS);
			if (strRows.equals("")) { strRows = output.getTagValue("s" + TAG_IMAGE_ROWS); }
			
			String strCols = output.getTagValue(TAG_IMAGE_COLS);
			if (strCols.equals("")) { strCols = output.getTagValue("s" + TAG_IMAGE_COLS); }
			
			int rows = Integer.parseInt(strRows);
			int cols = Integer.parseInt(strCols);
			
			//---- Fix this later
			if (dataIMG == null) {  }
			
			output.setImageRAW(dataRAW);
			output.setImageBMP(convertArrayToMatrix(dataIMG, rows, cols));
		}
		
		parserPointer = data.length;
		
		
	}
	
	
	//-----------------------------------------------------------------------------------------
	
	private static int indexPattern( byte[] data, int start, int stop, byte[] pattern) 
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

	private static short[][] convertArrayToMatrix (int[] input, int rows, int cols)
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
	
	
}
