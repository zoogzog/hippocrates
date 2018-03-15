package edu.hpc.andrey.dicomapi;

import java.awt.image.BufferedImage;

public class DicomDataExtractor
{
	private static boolean DEBUG_MODE = false;

	/**
	 * Obtain decoded pixel values from the DICOM file specified by its path.
	 * Transformation of the raw pixels is done with the parameters, specified in the DICOM file.
	 * @param filePath path to the input DICOM file
	 * @return pixel values in a 2D array or null, if the path is not correct or specified file is not a DICOM file
	 */
	public static byte[][] getPixelMatrix(String filePath)
	{
		try
		{
			DicomDictionary dictionary = new DicomDictionary("dicom-dict.txt");
			DicomContainer dicomData = new DicomContainer();
			DicomFile dicomFile = new DicomFile();

			boolean isSuccsess = DicomDecoder.convertDICOM(filePath, dicomData, dicomFile, dictionary, false);

			if (isSuccsess)
			{		
				return dicomData.getImage();
			}

			return null;
		}
		catch (Exception e)
		{
			if (DEBUG_MODE) { e.printStackTrace(); }
			return null;
		}
	}

	/**
	 * Obtain decoded pixel values from the DICOM file specified by its path.
	 * @param filePath path to the input DICOM file
	 * @return pixel values in a 1-D array or null, if the path is not correct or specified file is not a DICOM file
	 */
	public static byte[] getPixelArray (String filePath)
	{
		try
		{
			DicomDictionary dictionary = new DicomDictionary("dicom-dict.txt");
			DicomContainer dicomData = new DicomContainer();
			DicomFile dicomFile = new DicomFile();

			boolean isSuccsess = DicomDecoder.convertDICOM(filePath, dicomData, dicomFile, dictionary, false);

			if (isSuccsess)
			{
				byte[][] pixelMatrix = dicomData.getImage();

				int width = pixelMatrix.length;
				int height = pixelMatrix[0].length;

				byte[] output = new byte[width * height];

				int index = 0;

				for (int w = 0; w < width; w++)
				{
					for (int h = 0; h < height; h++)
					{
						output[index] = pixelMatrix[w][h];
					}
				}

				return output;
			}

			return null;

		}
		catch (Exception e)
		{
			if (DEBUG_MODE) { e.printStackTrace(); }
			return null;
		}

	}

	/**
	 * Obtain encoded pixels, stored in the DICOM file, no conversion or transformation is performed
	 * @param filePath path to the input DICOM file
	 * @return array of pixels
	 */
	public static byte[] getPixelArrayRaw (String filePath)
	{
		try
		{
			DicomDictionary dictionary = new DicomDictionary("dicom-dict.txt");
			DicomContainer dicomData = new DicomContainer();
			DicomFile dicomFile = new DicomFile();

			boolean isSuccsess = DicomDecoder.convertDICOM(filePath, dicomData, dicomFile, dictionary, false);

			if (isSuccsess)
			{
				return dicomData.getImageRaw();
			}
			
			return null;
		}
		catch (Exception e)
		{
			if (DEBUG_MODE) { e.printStackTrace(); }
			return null;
		}
	}
	
	/**
	 * Obtain decoded pixels without applied transformation in a 2D array.
	 * @param filePath path to the input DICOM file
	 * @return 2D array of pixels
	 */
	public static short[][] getPixelMatrixRaw (String filePath)
	{
		try
		{
			DicomDictionary dictionary = new DicomDictionary("dicom-dict.txt");
			DicomContainer dicomData = new DicomContainer();
			DicomFile dicomFile = new DicomFile();

			boolean isSuccsess = DicomDecoder.convertDICOM(filePath, dicomData, dicomFile, dictionary, false);

			if (isSuccsess)
			{
				return dicomData.getImageBMP();
			}
			
			return null;
		}
		catch (Exception e)
		{
			if (DEBUG_MODE) { e.printStackTrace(); }
			return null;
		}
	}
	
	
	/**
	 * Obtain list of tags, extracted from a DICOM file specified by its path
	 * @param filePath path to the input DICOM file
	 * @return list of tags in array, where each element is a concatenation of a tag and its value, separated with a semicolon [tag];[value]
	 */
	public static String[] getTagArray (String filePath)
	{
		try
		{
			DicomDictionary dictionary = new DicomDictionary("dicom-dict.txt");
			DicomContainer dicomData = new DicomContainer();
			DicomFile dicomFile = new DicomFile();

			boolean isSuccsess = DicomDecoder.convertDICOM(filePath, dicomData, dicomFile, dictionary, false);

			if (isSuccsess)
			{
				String[] tagList = dicomData.getTagList();
				String[] output = new String[tagList.length];

				for (int i = 0; i < output.length; i++)
				{
					String tag = tagList[i];
					String value = dicomData.getTagValue(tag);

					output[i] = tag + ";" + value;

				}

				return output;
			}

			return null;
		}
		catch (Exception e)
		{
			if (DEBUG_MODE) { e.printStackTrace(); }
			return null;
		}
	}

	/**
	 * Extract image from a DICOMfile specified by its path.
	 * Transformation of the raw pixels is done with the parameters, specified in the DICOM file.
	 * @param filePath path to the input DICOM file
	 * @return buffered
	 */
	public static BufferedImage getBufferedImage (String filePath)
	{
		try
		{
			byte[][] data = getPixelMatrix(filePath);

			if (data != null)
			{
				int width = data.length;
				int height = data[0].length;

				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
				
				for (int i = 0; i < width; i++)
				{
					for (int j = 0; j < height; j++)
					{			
						int r = data[i][j] & 0xFF;
						int g = data[i][j] & 0xFF;
						int b = data[i][j] & 0xFF;
						int col = (r << 16) | (g << 8) | b;
						
						image.setRGB(i, j, col);
					}
				}
				
				return image;
			}

			return null;
		}
		catch (Exception e)
		{
			if(DEBUG_MODE) { e.printStackTrace(); }
			return null;
		}
	}

}
