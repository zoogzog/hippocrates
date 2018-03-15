package edu.hpc.andrey.zmask.gui.form;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import edu.hpc.andrey.utils.ImageTransferDriver;
import edu.hpc.andrey.zmask.gui.panel.CustomSliderUI;

public class FormMainPanelLeft 
{
	private JPanel panel;

	private JComboBox <String> comboboxDirectoryName;
	private JComboBox <String> comboboxFileInDirectory;
	private JComboBox <String> comboboxTableLUT;
	
	private JTextField textfieldImageWindowCenter;
	private JTextField textfieldImageWindowWidth;
	private JTextField textfieldImageRescaleIntercept;
	private JTextField textfieldImageRescaleSlope;
	
	private JSlider sliderImageWindowCenter;
	private JSlider sliderImageWindowWidth;
	private JSlider sliderImageRescaleIntercept;
	private JSlider sliderImageRescaleSlope;
	
	//----------------------------------------------------------------

	public FormMainPanelLeft (FormMainHandler controllerButton)
	{
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		createPanel(controllerButton);
	}

	public JPanel get ()
	{
		return panel;
	}

	//----------------------------------------------------------------

	private void createPanel (FormMainHandler controller)
	{
		componentFileManager(controller);
		componentTableLutChooser(controller);
		componentImageTransformManager(controller);
	}

	//----------------------------------------------------------------
	
	public JComboBox <String> getComponentComboboxDirectory ()
	{
		return comboboxDirectoryName;
	}
	
	public JComboBox <String> getComponentComboboxFileInDirectory ()
	{
		return comboboxFileInDirectory;
	}
	
	public JComboBox <String> getComponentComboboxTableLUT ()
	{
		return comboboxTableLUT;
	}
	
	public JTextField getComponentTextfieldImageWindowWidth ()
	{
		return textfieldImageWindowWidth;
	}
	
	public JTextField getComponentTextfieldImageWindowCenter ()
	{
		return textfieldImageWindowCenter;
	}
	
	public JTextField getComponentTextfieldImageRescaleIntercept ()
	{
		return textfieldImageRescaleIntercept;
	}
	
	public JTextField getComponentTextfieldImageRescaleSlope ()
	{
		return textfieldImageRescaleSlope;
	}
	
	public JSlider getComponentSliderImageWindowWidth ()
	{
		return sliderImageWindowWidth;
	}
	
	public JSlider getComponentSliderImageWindowCenter ()
	{
		return sliderImageWindowCenter;
	}
	
	public JSlider getComponentSliderImageRescaleIntercept ()
	{
		return sliderImageRescaleIntercept;
	}
	
	public JSlider getComponentSliderImageRescaleSlope ()
	{
		return sliderImageRescaleSlope;
	}
	
	//----------------------------------------------------------------
	
	private void componentFileManager (FormMainHandler controller)
	{
		JPanel displayPanel = new JPanel();

		displayPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		displayPanel.setLayout(new GridBagLayout());
		displayPanel.setSize(new Dimension (240, 70));
		displayPanel.setMinimumSize(displayPanel.getSize());
		displayPanel.setMaximumSize(displayPanel.getSize());
		displayPanel.setPreferredSize(displayPanel.getSize());
		GridBagConstraints layoutConstraits = new GridBagConstraints();


		
		JLabel labelTest = new JLabel ("Driectory: ");
		labelTest.setBorder(new EmptyBorder(0, 5, 0, 5));
		layoutConstraits.anchor = GridBagConstraints.LINE_START;
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 0;
		layoutConstraits.weightx = 0.1;
		layoutConstraits.weighty = 0.1;
		displayPanel.add(labelTest, layoutConstraits);
		
		comboboxDirectoryName = new JComboBox<String>();
		comboboxDirectoryName.setPreferredSize(new Dimension(130, 20));
		comboboxDirectoryName.setSize(new Dimension(130, 20));
		comboboxDirectoryName.addActionListener(controller);
		comboboxDirectoryName.setActionCommand(FormMainHandler.COMMAND_CHANGED_COMBOBOX_DIRECTORY);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 0;
		displayPanel.add(comboboxDirectoryName, layoutConstraits);
		
		JLabel labelFile = new JLabel("File name:");
		labelFile.setBorder(new EmptyBorder(0, 5, 0, 5));
		layoutConstraits.anchor = GridBagConstraints.LINE_START;
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 1;
		displayPanel.add(labelFile, layoutConstraits);
		
		comboboxFileInDirectory = new JComboBox<String>();
		comboboxFileInDirectory.setPreferredSize(new Dimension(130, 20));
		comboboxFileInDirectory.setSize(new Dimension(130, 20));
		comboboxFileInDirectory.addActionListener(controller);
		comboboxFileInDirectory.setActionCommand(FormMainHandler.COMMAND_CHANGED_COMBOBOX_FILEINDIR);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 1;
		displayPanel.add(comboboxFileInDirectory, layoutConstraits);
		
		panel.add(displayPanel);
	}

	private void componentTableLutChooser (FormMainHandler controller)
	{
		JPanel displayPanel = new JPanel();

		displayPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		displayPanel.setLayout(new GridBagLayout());
		displayPanel.setSize(new Dimension (240, 30));
		displayPanel.setMinimumSize(displayPanel.getSize());
		displayPanel.setMaximumSize(displayPanel.getSize());
		displayPanel.setPreferredSize(displayPanel.getSize());
		GridBagConstraints layoutConstraits = new GridBagConstraints();
		
		JLabel labelTableLUT = new JLabel("Color LUT:");
		labelTableLUT.setBorder(new EmptyBorder(0, 5, 0, 33));
		layoutConstraits.anchor = GridBagConstraints.LINE_START;
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 1;
		layoutConstraits.weightx = 0.1;
		layoutConstraits.weighty = 0.1;
		displayPanel.add(labelTableLUT, layoutConstraits);
		
		comboboxTableLUT = new JComboBox<String>();
		comboboxTableLUT.setPreferredSize(new Dimension(130, 20));
		comboboxTableLUT.setSize(new Dimension(130, 20));
		comboboxTableLUT.addActionListener(controller);
		comboboxTableLUT.setActionCommand(FormMainHandler.COMMAND_COMBOBOX_LUT);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 1;
		displayPanel.add(comboboxTableLUT, layoutConstraits);
		
		comboboxTableLUT.addItem("grayscale");
		
		for (int i = 0; i < ImageTransferDriver.LUT_TABLE_NAME.length; i++)
		{
			comboboxTableLUT.addItem(ImageTransferDriver.LUT_TABLE_NAME[i]);
		}
		
		panel.add(displayPanel);
	}

	private void componentImageTransformManager (FormMainHandler controller)
	{
		JPanel displayPanel = new JPanel();

		displayPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		displayPanel.setLayout(new GridBagLayout());
		displayPanel.setSize(new Dimension (240, 100));
		displayPanel.setMinimumSize(displayPanel.getSize());
		displayPanel.setMaximumSize(displayPanel.getSize());
		displayPanel.setPreferredSize(displayPanel.getSize());
		GridBagConstraints layoutConstraits = new GridBagConstraints();
		
		JLabel labelDicomTagWindowWidth = new JLabel("Window width:");
		labelDicomTagWindowWidth.setBorder(new EmptyBorder(0, 5, 0, 5));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 0;
		layoutConstraits.anchor = GridBagConstraints.LINE_START;
		layoutConstraits.weightx = 0.1;
		layoutConstraits.weighty = 0.1;
		displayPanel.add(labelDicomTagWindowWidth, layoutConstraits);
		
		textfieldImageWindowWidth = new JTextField();
		textfieldImageWindowWidth.setEditable(false);
		textfieldImageWindowWidth.setPreferredSize(new Dimension(55, 20));
		textfieldImageWindowWidth.setSize(new Dimension(55, 20));
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 0;
		displayPanel.add(textfieldImageWindowWidth, layoutConstraits);
		
		sliderImageWindowWidth = new JSlider();
		sliderImageWindowWidth.setPreferredSize(new Dimension(80,  20));
		sliderImageWindowWidth.setSize(new Dimension(80, 20));
		sliderImageWindowWidth.setMaximum(200);
		sliderImageWindowWidth.setValue(100);
		sliderImageWindowWidth.setUI(new CustomSliderUI(sliderImageWindowWidth));
		sliderImageWindowWidth.setName(FormMainHandler.SLIDER_NAME_TRANSFORM_WINDOWWIDTH);
		sliderImageWindowWidth.addChangeListener(controller);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = 0;
		displayPanel.add(sliderImageWindowWidth, layoutConstraits);
		
		//--------------------------------------------------------------------------------
		
		JLabel labelDicomTagWindowCenter = new JLabel("Window center:");
		labelDicomTagWindowCenter.setBorder(new EmptyBorder(0, 5, 0, 5));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 1;
		displayPanel.add(labelDicomTagWindowCenter, layoutConstraits);
		
		textfieldImageWindowCenter = new JTextField();
		textfieldImageWindowCenter.setEditable(false);
		textfieldImageWindowCenter.setPreferredSize(new Dimension(55, 20));
		textfieldImageWindowCenter.setSize(new Dimension(55, 20));
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 1;
		displayPanel.add(textfieldImageWindowCenter, layoutConstraits);
		
		sliderImageWindowCenter = new JSlider();
		sliderImageWindowCenter .setPreferredSize(new Dimension(80,  20));
		sliderImageWindowCenter .setSize(new Dimension(80, 20));
		sliderImageWindowCenter .setMaximum(200);
		sliderImageWindowCenter .setValue(100);
		sliderImageWindowCenter .setUI(new CustomSliderUI(sliderImageWindowCenter));
		sliderImageWindowCenter.setName(FormMainHandler.SLIDER_NAME_TRANSFORM_WINDOWCENTER);
		sliderImageWindowCenter.addChangeListener(controller);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = 1;
		displayPanel.add(sliderImageWindowCenter, layoutConstraits);
		
		//--------------------------------------------------------------------------------
		
		JLabel labelDicomTagRescaleIntercept = new JLabel("Intercept:");
		labelDicomTagRescaleIntercept .setBorder(new EmptyBorder(0, 5, 0, 5));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 2;
		displayPanel.add(labelDicomTagRescaleIntercept , layoutConstraits);
		
		textfieldImageRescaleIntercept = new JTextField();
		textfieldImageRescaleIntercept.setEditable(false);
		textfieldImageRescaleIntercept.setPreferredSize(new Dimension(55, 20));
		textfieldImageRescaleIntercept.setSize(new Dimension(55, 20));
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 2;
		displayPanel.add(textfieldImageRescaleIntercept, layoutConstraits);
		
		sliderImageRescaleIntercept = new JSlider();
		sliderImageRescaleIntercept .setPreferredSize(new Dimension(80,  20));
		sliderImageRescaleIntercept .setSize(new Dimension(80, 20));
		sliderImageRescaleIntercept .setMaximum(200);
		sliderImageRescaleIntercept .setValue(100);
		sliderImageRescaleIntercept .setUI(new CustomSliderUI(sliderImageRescaleIntercept));
		sliderImageRescaleIntercept.setName(FormMainHandler.SLIDER_NAME_TRANSFORM_RESCALEINTERCEPT);
		sliderImageRescaleIntercept.addChangeListener(controller);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = 2;
		displayPanel.add(sliderImageRescaleIntercept, layoutConstraits);
		
		//--------------------------------------------------------------------------------
		
		JLabel labelDicomTagRescaleSlope = new JLabel("Slope:");
		labelDicomTagRescaleSlope .setBorder(new EmptyBorder(0, 5, 0, 5));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 3;
		displayPanel.add(labelDicomTagRescaleSlope , layoutConstraits);
		
		textfieldImageRescaleSlope = new JTextField();
		textfieldImageRescaleSlope.setEditable(false);
		textfieldImageRescaleSlope.setPreferredSize(new Dimension(55, 20));
		textfieldImageRescaleSlope.setSize(new Dimension(55, 20));
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 3;
		displayPanel.add(textfieldImageRescaleSlope, layoutConstraits);
		
		sliderImageRescaleSlope = new JSlider();
		sliderImageRescaleSlope .setPreferredSize(new Dimension(80,  20));
		sliderImageRescaleSlope .setSize(new Dimension(80, 20));
		sliderImageRescaleSlope .setMaximum(200);
		sliderImageRescaleSlope .setValue(100);
		sliderImageRescaleSlope .setUI(new CustomSliderUI(sliderImageRescaleSlope));
		sliderImageRescaleSlope.setName(FormMainHandler.SLIDER_NAME_TRANSFORM_RESCALESLOPE);
		sliderImageRescaleSlope.addChangeListener(controller);
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = 3;
		displayPanel.add(sliderImageRescaleSlope, layoutConstraits);
		
		panel.add(displayPanel);
	}

	//----------------------------------------------------------------
	
	/**
	 * Parses image transformation data displayed in the textfields. Returns null
	 * if some error occurred or empty data. Otherwise returns [width, center, intercept, slope]
	 * @return
	 */
	public int[] getTransformData ()
	{		
		try
		{
			int intWindowWidth = Integer.parseInt(textfieldImageWindowWidth.getText());
			int intWindowCenter = Integer.parseInt(textfieldImageWindowCenter.getText());
			int intRescaleIntercept = Integer.parseInt(textfieldImageRescaleIntercept.getText());
			int intRescaleSlope = Integer.parseInt(textfieldImageRescaleSlope.getText());

			return new int[] {intWindowWidth, intWindowCenter, intRescaleIntercept, intRescaleSlope};
		}
		catch (Exception e) {}
		
		return null;
	}


	//----------------------------------------------------------------
	
	public void resetState ()
	{		
		comboboxDirectoryName.removeAllItems();
		comboboxFileInDirectory.removeAllItems();

		comboboxTableLUT.setSelectedIndex(0);
		
		sliderImageWindowWidth.setMaximum(200);
		sliderImageWindowWidth.setValue(100);
		
		sliderImageWindowCenter.setMaximum(200);
		sliderImageWindowCenter.setValue(100);
		
		sliderImageRescaleIntercept.setMaximum(200);
		sliderImageRescaleIntercept.setValue(100);
		
		sliderImageRescaleSlope.setMaximum(200);
		sliderImageRescaleSlope.setValue(100);
		
		textfieldImageWindowWidth.setText("");
		textfieldImageWindowCenter.setText("");
		textfieldImageRescaleIntercept.setText("");
		textfieldImageRescaleSlope.setText("");
	}
	
}


