# hippocrates

HIPPOCRATES is an application for anonymizing and visualizing DICOM files that also supports generating user specified image masks. 
DICOM is a standard for storing and transmitting medical images. Besides image related data DICOM files also contain a set of tags that represent additional information, such as date of the conducted study, device specification, facts about patient, and other. Since DICOM files contain private information about the patient in order to appropriately handle the data it is mandatory to remove all sensitive data from the file. HIPPOCRATES is primarily designed to perform this operation. 
During the course of studying visual data often it is necessary to highlight a part of image where a region of interest is located. HIPPOCRATES allows user to investigate visual data stored in DICOM files, perform simple transformations, create masks, and save desired images.


*Requirements*
*	OS: Windows 7,8 or 10
*	Java virtual machine: 1.8.0



*Quick start*
*1.	Launch*

Launch the Hippocrates.exe from the application, folder

*2.	Import files*

Import some images stored in a directory by specifying its path by either selecting From directory menu (File Import -> From directory), or by clicking the Import directory button on the toolbar. Importing procedure will take some time, since the application checks if it is possible to parse selected DICOM files and extracts all 
DICOM tags required for further processing.

*3.	Anonymize files*

Perform anonymization of the imported files by selecting Anonymize data menu 
(Run -> Anonymize data) or clicking the Anonymize button located on the toolbar.
You will need to select the folder where the anonymized data will be stored. During the anonymization process all sensitive data in DICOM files will be swapped with asterisks, and then the file is saved as a DICOM file and a BMP file, that represents visual information. The name of the anonymized file is changed and becomes [Unique Patient ID]-[Study date]-[File index], the name of subdirectories also is changed to their index in the internal directory structures. The conversion tables for both patient IDs and directory names are stored alongside in files log-dir.txt and log-pid.txt. The information about location of all anonymized files is stored in the database file database.txt.

*4.	Import anonymized dataset*

It is suggested to perform mask selection on the anonymized dataset, thus to do so first, remove all previously imported files by selecting the menu New (File -> New).
Then import the anonymized files by loading by selecting the saved database file database.txt. through the File -> Import -> From database file menu option. This operation will take significantly less time than importing anonymized files via previously used import procedure.

*5.	Image masking*

To mask a desired area of the image select the paintbrush tool and paint over the displayed image. The quickest and easiest method to save the created mask is to click the Save mask button, located in the toolbar. This saves the mask image as a PNG file to the same location as the currently displayed file, with the same name as the DICOM file concatenated with the -mask suffix. The other way to save the mask is to select the Save image -> Image mask. With this method it is possible to manually specify the name of the output file.

*6.	Saving progress*

To save the information about imported files and masks before closing the application it is necessary to export the database file. This could be done by selecting the Export -> Database file from the menu and specifying the name of the output file.

 <img src="https://github.com/zoogzog/hippocrates/blob/master/hippocrates/doc/gui.jpg" width="374" height="301" /> 
