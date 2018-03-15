package edu.hpc.andrey.dicomapi;

public class DicomTagData 
{
	public static final String[] SENSITIVE_DATA_TAG_LIST =
		{
				"00080080", //---- Institution name
				"00081050", //---- Performing Physician's Name
				"00081060", //---- Name of Physicians Reading Study
				"00081070", //---- Operators' Name
				"00100010", //---- Patient's Name
				"00100020", //---- Patient's ID
				"00100021", //---- Issuer of Patient ID
				"00101000", //---- Other Patient IDs
				"00101040", //---- Patient's Address
		};

	//---- DICOM transformation tags for correctly displaying the image
	public static final String TAG_WINDOW_CENTER = "00281050";
	public static final String TAG_WINDOW_WIDTH = "00281051";
	public static final String TAG_RESCALE_INTERCEPT = "00281052";
	public static final String TAG_RESCALE_SLOPE = "00281053";
	
	public static final String TAG_IMAGE_TYPE = "00080008";
	public static final String TAG_IMAGE_DESCRIPTOR =   "00400254";	
	public static final String TAG_IMAGE_PROTOCOLNAME = "00181030";
			
}
