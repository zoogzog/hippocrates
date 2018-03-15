package edu.hpc.andrey.zmask.controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import edu.hpc.andrey.dicom.anon.data.ControllerOutput;
import edu.hpc.andrey.dicom.core.DicomContainer;
import edu.hpc.andrey.dicom.core.DicomDecoder;
import edu.hpc.andrey.dicom.core.DicomDictionary;
import edu.hpc.andrey.dicom.core.DicomFile;
import edu.hpc.andrey.dicom.core.DicomTagData;
import edu.hpc.andrey.utils.ImageTransferDriver;
import edu.hpc.andrey.zmask.cache.ImageCache;
import edu.hpc.andrey.zmask.data.DatabaseTable;

public class ExportController 
{
	private static final String EXTENSION_TAG = ".tg";
	private static final String EXTENSION_BIN = ".bn";
	private static final String EXTENSION_RAW = ".rw";
	private static final String EXTENSION_BMP = ".bmp";
	private static final String EXTENSION_INF = ".inf";
	private static final String EXTENSION_DICOM = ".dcm";

	public static void exportDataFile (DatabaseTable db, ExportSettings settings)
	{
		
//FIXME
		String fullFilePath = "";

		DicomDictionary dictionary = DicomDictionary.getInstance();
		DicomContainer dicomData = new DicomContainer();
		DicomFile dicomFile = new DicomFile();

		System.out.println(fullFilePath);

		String fullFilePathOutput = settings.outputPath  +"\\" + settings.exportFile;

		boolean isSuccsess = DicomDecoder.convertDICOM(fullFilePath, dicomData, dicomFile, dictionary, false);

		boolean isTAG = settings.exportTAGS;
		boolean isDICOM = settings.exportDICOM;
		boolean isBIN = settings.exportBINDECODE;
		boolean isRAW = settings.exportBINENCODE;
		boolean isBMP = settings.exportBMP;
		boolean isINF = false;

		//---- Save data with specified formats
		if (isSuccsess)
		{
			//---- SUCCESS
			ControllerOutput.exportData(dicomData, dicomFile, dictionary, fullFilePathOutput, new boolean[]{isTAG, isBIN, isBMP, isINF, isRAW, isDICOM});
		}
		else{}

	}


	public static void exportDataDatabase ()
	{

	}

	//-----------------------------------------------------------------------------------------

	/**
	 * Export extracted data into files. The files, which are extracted, are specified by flags.
	 * Flag array contains the following flags: [TAG, BIN, BMP, INF, RAW] 
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @param flags
	 * @return
	 */
	public static boolean exportData (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput, boolean[] flags)
	{
		boolean isSuccessTAG = true;
		boolean isSuccessBIN = true;
		boolean isSuccessBMP = true;
		boolean isSuccessINF = true;
		boolean isSuccessRAW = true;
		boolean isSuccessDICOM = true;

		if (flags.length < 6) { return false; }

		if (flags[0]) { isSuccessTAG = exportDataTAG (data, dicomFile, dictionary, pathOutput); }
		if (flags[1]) { isSuccessBIN = exportDataBIN (data, dicomFile, dictionary, pathOutput); }
		if (flags[2]) { isSuccessBMP = exportDataBMP (data, dicomFile, dictionary, pathOutput); }
		if (flags[3]) { isSuccessINF = exportDataINF (data, dicomFile, dictionary, pathOutput); }
		if (flags[4]) { isSuccessRAW = exportDataRAW (data, dicomFile, dictionary, pathOutput); }
		if (flags[5]) { isSuccessDICOM = exportDataDICOM(data, dicomFile, dictionary, pathOutput); }

		if (!isSuccessBIN || !isSuccessBMP || !isSuccessINF || !isSuccessTAG || !isSuccessRAW || !isSuccessDICOM) { return false; }
		else { return true; }
	}

	//-----------------------------------------------------------------------------------------

	/**
	 * Export all dicom tags into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataTAG (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_TAG;

		try
		{
			data.saveTagList(realPathOutput, dictionary, true);

			return true;
		}
		catch (Exception e) 
		{ 
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Export a decoded image data as binary bytes into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataBIN (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_BIN;

		short[][] imageData = data.getImageBMP();

		try
		{
			if (imageData != null)
			{
				FileOutputStream ostream  = new FileOutputStream(new File(realPathOutput));

				for (int i = 0; i < imageData.length; i++)
				{
					for (int j = 0; j < imageData[0].length; j++)
					{
						short value = imageData[i][j];

						byte left = (byte)((value & 0xFF00) >> 8);
						byte right = (byte) (value & 0x00FF);

						ostream.write(new byte[] {left, right});
					}
				}

				ostream.close();

				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Export compressed image data (bytes) into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataRAW (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_RAW;

		byte[] dataRAW = data.getImageRaw();
		try
		{
			FileOutputStream ostream  = new FileOutputStream(new File(realPathOutput));

			ostream.write(dataRAW);
			ostream.close();

			return true;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Export a decoded image data as a bmp image into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataBMP (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_BMP;

		try
		{
			data.saveImage(realPathOutput);	

			return true;
		}
		catch (Exception e) 
		{ 
			//e.printStackTrace(); 
			return false;
		}
	}

	/**
	 * Export patient's private information into a file, specified by path
	 * @param data
	 * @param dictionary
	 * @param pathOutput
	 * @return
	 */
	private static boolean exportDataINF (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_INF;

		try
		{
			FileWriter outputFile = new FileWriter(realPathOutput, false);
			PrintWriter outputStream = new PrintWriter(outputFile);

			for  (int k = 0; k < DicomTagData.SENSITIVE_DATA_TAG_LIST.length; k++)
			{
				String tagCode = DicomTagData.SENSITIVE_DATA_TAG_LIST[k];

				String dicomDescription = dictionary.getValue(tagCode);

				if (!dicomDescription.equals(""))
				{
					outputStream.println(dicomDescription + ";" + data.getTagValue(tagCode));
				}
			}

			outputStream.close();
			outputFile.close();

			return true;
		}
		catch (Exception e) 
		{ 
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Export dicom file with masked fields of sensitive data
	 */
	private static boolean exportDataDICOM (DicomContainer data, DicomFile dicomFile, DicomDictionary dictionary, String pathOutput)
	{
		String realPathOutput = pathOutput + EXTENSION_DICOM;

		try
		{
			FileOutputStream ostream  = new FileOutputStream(new File(realPathOutput));

			ostream.write(dicomFile.getDataMasked());
			ostream.close();

			return true;
		}
		catch (Exception e) 
		{ 
			//e.printStackTrace();
			return false;
		}
	}


	//-----------------------------------------------------------------------------------------

	public static void exportImageOriginal (DicomContainer data, String pathOutput)
	{
		Mat img = data.getImage();
		
		Imgcodecs.imwrite(pathOutput + ".bmp", img);
	}
	
	public static void exportImageDisplayed (DicomContainer data, int[] transformation, int lutIndex, String pathOutput)
	{
		Mat img = data.getImage(transformation[0], transformation[1], transformation[2], transformation[3]);

		if (lutIndex > 0)
		{
			ImageTransferDriver driver = new ImageTransferDriver(lutIndex - 1);
			img = driver.transferIntensity(img);
		}

		Imgcodecs.imwrite(pathOutput + ".bmp", img);
	}

	public static void exportImageMask (DicomContainer data, ImageCache cache, int[][] mask, Image maskImage, String pathOutput)
	{
		try
		{
			int width = data.getImageWidth();
			int height = data.getImageHeight();

			double scalefactor = cache.getImageScaleFactor();

			BufferedImage output = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = output.createGraphics();

			g2.setColor(new Color(0, 0, 0, 10));
			g2.drawRect(0, 0, width, height);
			g2.setColor(Color.red);
			
			for (int i = 0; i < mask.length; i+=2)
			{
				//---- Scaled coordinates
				int xsc1 = mask[i][0];
				int ysc1 = mask[i][1];

				int xsc2 = mask[i+1][0];
				int ysc2 = mask[i+1][1];

				//---- Mask brush size 
				int size = (int) Math.floor(mask[i][2] * scalefactor);

				//---- Initial coordinates
				int xin1 = (int) Math.floor(xsc1 * scalefactor);
				int yin1 = (int) Math.floor(ysc1 * scalefactor);

				int xin2 = (int) Math.floor(xsc2 * scalefactor);
				int yin2 = (int) Math.floor(ysc2 * scalefactor);

				//---- Draw stuff here
				if (xsc1 == xsc2 && ysc1 == ysc2)
				{
					Ellipse2D circle = new Ellipse2D.Float(xin1 - size / 2, yin1 - size / 2, size, size);
					g2.fill(circle);
				}
				else
				{

					g2.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					g2.drawLine(xin1, yin1, xin2, yin2);

				}

			}
			
			if (maskImage != null)
			{
				g2.drawImage(maskImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
			}
			
			File outputfile = new File(pathOutput + ".png");
			ImageIO.write(output, "png", outputfile);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


	}
}
