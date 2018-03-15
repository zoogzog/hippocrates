package edu.hpc.andrey.zmask.gui.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


/**
 * Panel for displaying image. Supports zooming and moving the image. 
 * Also provides displaying polygons on the displayed image.
 * @author Andrey 
 */
public class PanelImageView extends JPanel
{
	private static final long serialVersionUID = 1L;

	private Image imageOriginal;
	private Image imageScaled;

	private boolean isLoaded;

	private double zoom;

	private int translateX;
	private int translateY;

	private Vector <Point> mask;
	private Vector <Integer> maskPaintbrushSizeSet;
	private Color maskColor;
	
	private int maskPaintbrushSizeCurrent;
	
	private Image maskImage;
	

	private Object DEFAULT_RENDERING_HINTS = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

	//============================================================================================

	public PanelImageView ()
	{
		zoom = 1.0;

		translateX = 0;
		translateY = 0;

		isLoaded = false;

		imageOriginal = null;
		imageScaled = null;

		mask = new Vector <Point> ();
		maskPaintbrushSizeSet = new Vector <Integer>();
		maskColor = Color.red;
		maskPaintbrushSizeCurrent = 10;
		
		maskImage = null;
	}

	//============================================================================================

	public void loadImage (String inputFilePath)
	{
		try
		{
			BufferedImage inputImage = ImageIO.read(new File(inputFilePath));
			loadImage(inputImage);
		}
		catch (IOException Exception) {}
	}

	public void loadImage (Image inputImage)
	{
		zoom = 1.0;

		translateX = 0;
		translateY = 0;

		isLoaded = true;

		imageOriginal = inputImage;

		this.invalidate();
		this.repaint();
	}

	/**
	 * Resets the position of the image
	 */
	public void resetImagePosition ()
	{
		zoom = 1.0;

		translateX = 0;
		translateY = 0;
		
		invalidate();
		repaint();
		
		displayImage();
	}

	public void freeImage ()
	{
		imageOriginal = null;
		imageScaled = null;

		isLoaded = false;

		invalidate();
		repaint();
	}

	//============================================================================================

	@Override	
	public Dimension getPreferredSize() 
	{
		if (isLoaded)
		{
			return new Dimension(imageScaled.getWidth(this), imageScaled.getHeight(this));
		}
		else
		{
			return super.getPreferredSize();
		}
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);

		Image imageDisplay = null;

		if (imageScaled != null) { imageDisplay = imageScaled; }
		else if (imageOriginal != null) { imageDisplay = imageOriginal; }

		if (imageDisplay != null) 
		{
			int x = (getWidth() - imageDisplay.getWidth(this)) / 2;
			int y = (getHeight() - imageDisplay.getHeight(this)) / 2;
			g.drawImage(imageDisplay, x, y, this);
		}
	}

	@Override
	public void invalidate() 
	{
		super.invalidate();

		if (isLoaded)
		{
			imageScaled = null;

			Dimension source = new Dimension(imageOriginal.getWidth(this), imageOriginal.getHeight(this));
			Dimension target = getSize();

			imageScaled = generateScaledImageInstance(convertImageToBufferedImage(imageOriginal), source, target);

			zoom = 1.0 / ((double)imageOriginal.getWidth(this) / imageScaled.getWidth(this));
		}
		else {zoom = 1; }
	}

	//============================================================================================

	private Image generateScaledImageInstance(BufferedImage image, Dimension source, Dimension target)
	{
		if (isLoaded)
		{
			if (source != null && target != null)
			{
				double scaleFactorWidth = (double) target.width / (double) source.width;
				double scaleFactorHeight = (double) target.height / (double) source.height;

				double scaleFactor = Math.min(scaleFactorWidth, scaleFactorHeight);

				BufferedImage outputImage = image;

				int scaledWidth = (int) Math.round(image.getWidth() * scaleFactor);
				int scaledHeight = (int) Math.round(image.getHeight() * scaleFactor);

				int type;
				if (scaleFactor <= 1.0)
				{
					type = (outputImage.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB: BufferedImage.TYPE_INT_ARGB;
				}
				else
				{
					type = BufferedImage.TYPE_INT_ARGB;
				}

				BufferedImage tmp = new BufferedImage(scaledWidth, scaledHeight, type);
				Graphics2D g2 = tmp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, DEFAULT_RENDERING_HINTS);
				g2.drawImage(outputImage, 0, 0, scaledWidth, scaledHeight, null);
				g2.dispose();

				return tmp;

			}
		}

		return null;
	}

	//============================================================================================

	public double getZoom ()
	{
		return zoom;
	}

	public Point getRealPoint (Point globalPoint)
	{
		int TrX = 0;
		int TrY = 0;

		if (isLoaded)
		{
			int NewWidth = (int) Math.round ((double)imageOriginal.getWidth(this) * zoom);
			int NewHeight = (int) Math.round ((double)imageOriginal.getHeight(this) * zoom);

			int CurrentWidth = this.getSize().width;
			int CurrentHeight = this.getSize().height;

			int NewX = (CurrentWidth - NewWidth) / 2;
			int NewY = (CurrentHeight - NewHeight) / 2;

			TrX = (int) Math.round((globalPoint.x - NewX) / zoom); TrY = (int) Math.round((globalPoint.y - NewY)  / zoom);

			if (NewWidth < CurrentWidth && NewHeight < CurrentHeight)
			{

			}
			else
			{
				TrX -= translateX / zoom;
				TrY  -= translateY / zoom;
			}

			if (TrX > imageOriginal.getWidth(this)) { TrX = imageOriginal.getWidth(this); }
			if (TrY > imageOriginal.getHeight(this)) { TrY = imageOriginal.getHeight(this); }
			if (TrX < 0) { TrX = 0; } if (TrY < 0) { TrY = 0; }

		}

		return new Point(TrX, TrY);
	}

	//============================================================================================

	public double transformZoom (double factorValue)
	{
		if (isLoaded)
		{
			zoom += factorValue;

			if (zoom < 1) { translateX = 0; translateY = 0; }

			displayImage();

			return zoom;
		}

		return 1.0;
	}

	public void transformMove (int x, int y)
	{
		if (isLoaded)
		{
			translateX += x;
			translateY += y;

			displayImage();
		}
	}

	//============================================================================================

	protected BufferedImage convertImageToBufferedImage(Image inputImage) 
	{
		Dimension inputImageSize = new Dimension(inputImage.getWidth(this), inputImage.getHeight(this));

		BufferedImage image = createCompatibleImage((int)inputImageSize.getWidth(), (int)inputImageSize.getHeight());
		Graphics2D g2d = image.createGraphics();
		g2d.drawImage(inputImage, 0, 0, this);
		g2d.dispose();
		return image;
	}

	public BufferedImage createCompatibleImage(int width, int height) 
	{
		GraphicsConfiguration gc = getGraphicsConfiguration();

		if (gc == null) 
		{
			gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		}

		BufferedImage image = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		image.coerceData(true);
		return image;
	}
	//============================================================================================

	public void displayImage ()
	{
		if (isLoaded)
		{
			int transformWidth = (int) Math.round((double)imageOriginal.getWidth(this) * zoom);
			int transformHeight = (int) Math.round((double)imageOriginal.getHeight(this) * zoom);

			int currentWidth = this.getSize().width;
			int currentHeight = this.getSize().height;

			BufferedImage tempImage = convertImageToBufferedImage(imageOriginal);
			Graphics2D GraphDriver = tempImage.createGraphics();
			GraphDriver.setColor(java.awt.Color.black);

			//---- Draw on the canvas here
			if (mask != null)
			{
				if (mask.size() != 0)
				{
					for (int i = 0; i < mask.size(); i+=2)
					{
						GraphDriver.setColor(maskColor);
		
						Point lineStart = mask.get(i);
						Point lineEnd = mask.get(i+1);
						
						int psize = maskPaintbrushSizeSet.get(i);
					
						
						if (lineStart.x == lineEnd.x && lineStart.y == lineEnd.y)
						{
							Ellipse2D circle = new Ellipse2D.Float(lineStart.x - psize / 2, lineStart.y - psize / 2, psize, psize);
							GraphDriver.fill(circle);
						}
						else
						{
				
							GraphDriver.setStroke(new BasicStroke(psize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
						GraphDriver.drawLine(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);
						
						}
					}
				}
			}
			

			//---- Draw here overlayed mask
			if (maskImage != null)
			{
				GraphDriver.drawImage(maskImage, 0, 0, null);
			}


			//---- Perform transformation
			if (transformWidth < currentWidth && transformHeight < currentHeight)
			{	
				Dimension source =  new Dimension(imageOriginal.getWidth(this), imageOriginal.getHeight(this));
				Dimension target = new Dimension(transformWidth, transformHeight);

				imageScaled = generateScaledImageInstance(tempImage, source, target);
			}
			else
			{
				int newX = (transformWidth - currentWidth) / 2;
				int newY = (transformHeight - currentHeight) / 2;

				int scaledX = (int) Math.round((double) newX / zoom);
				int scaledY = (int) Math.round((double) newY / zoom);

				int scaledWidth =(int) Math.round((double) currentWidth / zoom);
				int scaledHeight = (int) Math.round((double) currentHeight / zoom);

				int currentTranslatedX = - (int) Math.round((double)  translateX / zoom);
				int currentTranslatedY = - (int) Math.round((double)  translateY / zoom);

				/** Here cropping is done before scaling in order to increase processing speed */
				BufferedImage croppedImage = new BufferedImage(scaledWidth,	scaledHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D GraphicsDriver = croppedImage.createGraphics();
				GraphicsDriver.drawImage(tempImage, 0, 0, scaledWidth,  scaledHeight, scaledX + currentTranslatedX, scaledY + currentTranslatedY, scaledX + currentTranslatedX + scaledWidth, scaledY + currentTranslatedY + scaledHeight, null);
				GraphicsDriver.dispose();

				Dimension source = new Dimension(croppedImage.getWidth(), croppedImage.getHeight());
				Dimension target = new Dimension(currentWidth, currentHeight);

				imageScaled = generateScaledImageInstance(croppedImage, source, target);
			}
		}
	}

	//============================================================================================

	public void maskAddPoint (int x, int y)
	{
		mask.addElement(new Point(x, y));
		maskPaintbrushSizeSet.addElement(maskPaintbrushSizeCurrent);
	}
	
	public void maskAddPoint (Point p)
	{
		mask.addElement(p);
		maskPaintbrushSizeSet.addElement(maskPaintbrushSizeCurrent);
	}
	
	public void maskSetColor (Color c)
	{
		maskColor = c;
	}
	
	public void maskSetPaintbrushSize (int size)
	{
		maskPaintbrushSizeCurrent = size;
	}

	public int[][] maskGetData ()
	{
		int[][] output = new int[mask.size()][3];
		
		for (int i = 0; i < mask.size(); i++)
		{
			output[i][0] = mask.get(i).x;
			output[i][1] = mask.get(i).y;
			output[i][2] = maskPaintbrushSizeSet.get(i);
		}
		
		return output;
	}

	public void maskRemove ()
	{
		mask.removeAllElements();
		maskPaintbrushSizeSet.removeAllElements();
	}
	
	public void maskRemoveLastPoint ()
	{
		mask.removeElementAt(mask.size() - 1);
		maskPaintbrushSizeSet.removeElementAt(maskPaintbrushSizeSet.size() - 1);
		
		mask.removeElementAt(mask.size() - 1);
		maskPaintbrushSizeSet.removeElementAt(maskPaintbrushSizeSet.size() - 1);
	}
	
	public void maskSetImage (Image img)
	{
		maskImage = img;
	}
	
	public Image maskGetImage ()
	{
		return maskImage;
	}
	
	
}
