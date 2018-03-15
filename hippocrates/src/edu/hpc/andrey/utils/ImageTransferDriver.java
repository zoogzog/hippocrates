package edu.hpc.andrey.utils;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

//import sun.misc.IOUtils;

/**
 * Class to transform images to be displayed with the help of look up tables.
 * @author Andrey
 */

public class ImageTransferDriver 
{
	public static final String[] LUT_TABLE_NAME = {"lut-fire", "lut-16colors"};
	private static final String[] LUT_TABLE_PATH = {"lut-fire2.lut", "lut-16colors.lut"};


	private Color[] table = new Color[256];

	//----------------------------------------------------------------

	public ImageTransferDriver ()
	{
		loadLUT(LUT_TABLE_PATH[0]);
	}

	public ImageTransferDriver (int lutIndex)
	{
		loadLUT(LUT_TABLE_PATH[lutIndex]);
	}

	//----------------------------------------------------------------

	private void loadLUT (String LUT_PATH)
	{
		try
		{

			//----- Read all bytes from the file, located in the same jar
			InputStream is = this.getClass().getResourceAsStream(LUT_PATH);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1) { buffer.write(data, 0, nRead); }
			buffer.flush();
			
			byte[] byteData = buffer.toByteArray();

			for (int k = 0; k < 256; k++)
			{

				int red = byteData[k] & 0xFF;
				int green = byteData[k+256] & 0xFF;
				int blue = byteData[k+2*256] & 0xFF;

				table[k] = new Color(red, green, blue);
			}
		}
		catch (Exception e) { e.printStackTrace();}
	}

	//----------------------------------------------------------------

	public Mat transferIntensity (Mat input)
	{
		Mat output = new Mat (input.size(), CvType.CV_8UC3);

		for (int row = 0; row < input.rows(); row++)
		{
			for (int col = 0; col < input.cols(); col++)
			{
				int intensity = (int) input.get(row, col)[0];

				output.put(row, col, new byte[]{(byte) table[intensity].getBlue(), (byte) table[intensity].getGreen(), (byte) table[intensity].getRed()});
			}
		}

		return output;
	}
}
