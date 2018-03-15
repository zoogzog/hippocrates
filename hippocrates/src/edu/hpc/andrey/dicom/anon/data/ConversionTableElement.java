package edu.hpc.andrey.dicom.anon.data;

/**
 * This class is a container for sensitive data. Element in the table that stores sensitive data
 * extracted from all files.
 * Not used in version 1.1.0
 * @author Andrey
 */

public class ConversionTableElement 
{
	public String filePathOriginal;
	public String filePathNew;
	
	public String patientIdOriginal;
	public String patientIdNew;
	
	//---- Patient info about study (only 1st study used as info source)
	public String tagInstitutionName;		//TAG: "00080080"
	public String tagPhysicianName;			//TAG: "00081050"
	public String tagPhysicianStudyName;	//TAG: "00081060"
	public String tagOperatorName;			//TAG: "00081070"
	public String tagPatientName; 			//TAG: "00100010"
	public String tagPatientID;				//TAG: "00100020"
	public String tagIssuerPatientID;		//TAG: "00100021"
	public String tagPatienSuplementaryID;		//TAG: "00101000"
	public String tagPatientAddress;		//TAG: "00101040"
	
}
