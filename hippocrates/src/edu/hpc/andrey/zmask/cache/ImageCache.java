package edu.hpc.andrey.zmask.cache;

import org.opencv.core.Mat;

import edu.hpc.andrey.dicom.core.DicomImageDecoder;

/**
 * This class is designed for faster processing of currently displayed DICOM image file.
 * @author Andrey
 *
 */
public class ImageCache 
{
	private short[][] cache = null;
	
	private int cacheWidth = 0;
	private int cacheHeight = 0;
	
	//---- If an image is bigger than 500x500 scale it
	private double scaleFactor = 1;
	
	public static final int MAX_DIMENSIION_SIZE = 500;
	
	public ImageCache ()
	{
		
	}
	
	public void upload (short[][] data)
	{
		if (data == null) { return; }
		if (data[0] == null) { return; }
		
		int width = data.length;
		int height = data[0].length;
		
		scaleFactor = (double) Math.max(width, height) / MAX_DIMENSIION_SIZE;
		
		cacheWidth = (int) Math.floor((double) width / scaleFactor);
		cacheHeight = (int) Math.floor((double) height / scaleFactor);
		
		cache = new short[cacheWidth][cacheHeight];
		
		//---- Apply nearest neighbour scaling 
		for (int i = 0; i < cacheWidth; i++)
		{
			for (int j = 0; j < cacheHeight; j++)
			{
				int ni = (int) Math.floor(i * scaleFactor);
				int nj = (int) Math.floor(j * scaleFactor);
				
				cache[i][j] = data[ni][nj];
			}
		}
		
	}
	
	public short[][] download ()
	{
		return cache;
	}
	
	
	public Mat getImage (int WINDOW_WIDTH, int WINDOW_CENTER, int RESCALE_SLOPE, int RESCALE_INTERCEPT, boolean isInvert)
	{
		return  DicomImageDecoder.convertToImage(cache, WINDOW_CENTER, WINDOW_WIDTH, RESCALE_INTERCEPT, RESCALE_SLOPE, isInvert);
	}

	public double getImageScaleFactor ()
	{
		return scaleFactor;
	}
	
	public int getWidth ()
	{
		return cacheWidth;
	}
	
	public int getHeight ()
	{
		return cacheHeight;
	}
	
	
}
