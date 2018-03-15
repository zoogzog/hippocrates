package edu.hpc.andrey.dicomapi;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class DicomImageDecoder 
{	
	/**
	 * Converts byte data into image representation. Uses JPEG lossless decompression for
	 * the transfer syntax 1.2.840.10008.1.2.4.70.
	 * @param data
	 * @param output
	 */
	public static int[] decodeJPEG (byte[] data)
	{
		try
		{
				if (data.length != 0)
				{
					//---- Decompress data and store in dicom output container.
					JPEGLosslessDecoder decoder = new JPEGLosslessDecoder(data);
								
					//---- After decompression 0xF830 values represent artificial border
					//---- These value should be removed or set to zero
					int VALUE_BORDER = 0xF830;
					int[][] dataDecoded = decoder.decode();

					int[] output = new int[dataDecoded[0].length];
					
					for (int i = 0; i < dataDecoded[0].length; i++)
					{
						if (dataDecoded[0][i] != VALUE_BORDER)
						{
							output[i] = dataDecoded[0][i];
						}
						else
						{
							output[i] = 0;
						}
					}
					
					return output;
			}
				
		}
		catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}


	/**
	 * Converts decoded data stored in a 2 dimensional array into visible representation using DICOM
	 * rescale procedure. Rescale intercept, slope, window center, width have to be specified.
	 * The DICOM conversion algorithm uses the following concepts: rescale intercept, rescale slope, window width, window center.
	 * The values of these parameters are stored in tags. To perform transformation first rescale the original data:
	 * FinalValue = OriginalPixelValue * RESCALE_SLOPE + RESCALE_INTERCEPT
	 * LOWEST_VISIBLE = WINDOW_CENTER - WINDOW_WIDTH / 2;
	 * HIGHEST_VISIBLE = WINDOW_CENTER + WINDOW_WIDTH / 2;
	 * All pixels outside of the window lowest visible are displayed as black, all pixels outside of the highest visible
	 * are displayed as white.
	 * For more information check this stackoverflow thread:
	 * [http://stackoverflow.com/questions/8756096/window-width-and-center-calculation-of-dicom-image]
	 */
	public static byte[][] convertToImage (short[][] data, int windowCenter, int windowWidth, int rescaleIntercept, int rescaleSlope)
	{
		int rows = data.length;
		int cols = data[0].length;
		
		byte[][] imageMatrix = new byte[rows][cols];
		
		int VALUE_MIN = windowCenter - windowWidth / 2;
		int VALUE_MAX = windowCenter + windowWidth / 2;
		
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < cols; col++)
			{
				
				short valueOriginal = data[row][col];
				
				if (valueOriginal != 0)
				{
				
				int valueFinal = valueOriginal * rescaleSlope + rescaleIntercept;
				
				byte valueDisplay = 0;
				
				if (valueFinal <= VALUE_MIN) { valueDisplay = 0; }
				else if (valueFinal >= VALUE_MAX){ valueDisplay = (byte) 255; }
				else { valueDisplay = (byte)((double) (valueFinal - VALUE_MIN) / (VALUE_MAX - VALUE_MIN) * 255); }
				
				imageMatrix[col][row] = (byte) valueDisplay;
				
				
				}
			}
		}
		
		return imageMatrix;
	}
	
	public static Mat convertToImageM (short[][] data, int windowCenter, int windowWidth, int rescaleIntercept, int rescaleSlope)
	{
		Mat imageMatrix = Mat.zeros(data.length, data[0].length, CvType.CV_8UC1);
		
		int VALUE_MIN = windowCenter - windowWidth / 2;
		int VALUE_MAX = windowCenter + windowWidth / 2;
		
		for (int row = 0; row < imageMatrix.rows(); row++)
		{
			for (int col = 0; col < imageMatrix.cols(); col++)
			{
				
				short valueOriginal = data[row][col];
				
				if (valueOriginal != 0)
				{
				
				int valueFinal = valueOriginal * rescaleSlope + rescaleIntercept;
				
				byte valueDisplay = 0;
				
				if (valueFinal <= VALUE_MIN) { valueDisplay = 0; }
				else if (valueFinal >= VALUE_MAX){ valueDisplay = (byte) 255; }
				else { valueDisplay = (byte)((double) (valueFinal - VALUE_MIN) / (VALUE_MAX - VALUE_MIN) * 255); }
				
				imageMatrix.put(row, col, new byte[] {(byte)valueDisplay});
				}
			}
		}
		
		return imageMatrix;
	}
}
