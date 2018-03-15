package edu.hpc.andrey.dicom.anon.form;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * Swing form main panel
 * @author Andrey
 */

public class FormMainPanel
{
	private JPanel panel;

	//---- Textfield to describe path to the import data
	private JTextField tfImportDataPath;

	//---- Textfield to describe path to the export data
	private JTextField tfExportDataPath;

	//---- Export settings check box: export dicom attributes file
	private JCheckBox cbDicomAttributes;

	//---- Export settings check box: export binary file (decompressed, raw), representing image
	private JCheckBox cbBinaryImage;

	//---- Export settings check box: export bmp image
	private JCheckBox cbBitmapImage;

	//---- Export settings check box: export masked dicom file
	private JCheckBox cbDicomFile;

	//---- Export settings check box: export raw data
	private JCheckBox cbDataRaw;
		
	//----------------------------------------------------------------

	public FormMainPanel (FormMainHandler handler)
	{
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		generatePanel(handler);
	}

	public JPanel getPanel ()
	{
		return panel;
	}

	//----------------------------------------------------------------

	public JTextField getComponentTFImportData ()
	{
		return tfImportDataPath;
	}

	public JTextField getComponentTFExportData ()
	{
		return tfExportDataPath;
	}

	public JCheckBox getComponentCBDicomAttributes ()
	{
		return cbDicomAttributes;
	}

	public JCheckBox getComponentCBBinaryImage ()
	{
		return cbBinaryImage;
	}

	public JCheckBox getComponentCBBitmapImage ()
	{
		return cbBitmapImage;
	}
	
	public JCheckBox getComponeCBDicomFile ()
	{
		return cbDicomFile;
	}
	
	public JCheckBox getComponentCBDataRaw ()
	{
		return cbDataRaw;
	}
		
	//----------------------------------------------------------------

	private void generatePanel (FormMainHandler handler)
	{
		generateFilePanel (panel, handler);
		generateExportSettingsPanel(panel, handler);
	}

	private void generateFilePanel (JPanel panelParent, FormMainHandler handler)
	{
		JPanel displayPanel = new JPanel();

		//displayPanel.setBackground(FormStyle.COLOR_MENU);
		displayPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		displayPanel.setLayout(new GridBagLayout());
		displayPanel.setSize(new Dimension (600, 90));
		displayPanel.setMinimumSize(displayPanel.getSize());
		displayPanel.setMaximumSize(displayPanel.getSize());
		displayPanel.setPreferredSize(displayPanel.getSize());
		GridBagConstraints layoutConstraits = new GridBagConstraints();

		//---- Label import data path
		JLabel labelImportDataPath = new JLabel("Import data path:");
		labelImportDataPath.setBorder(new EmptyBorder(0, 5, 0, 5));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 0;
		layoutConstraits.gridwidth = 1;
		layoutConstraits.weightx = 0.2;
		layoutConstraits.anchor = GridBagConstraints.LINE_START;
		displayPanel.add(labelImportDataPath, layoutConstraits);

		tfImportDataPath = new JTextField();
		tfImportDataPath.setSize(new Dimension(400, 25));
		tfImportDataPath.setPreferredSize(tfImportDataPath.getSize());
		tfImportDataPath.setEditable(false);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 0;
		layoutConstraits.weighty = 0.2;
		displayPanel.add(tfImportDataPath, layoutConstraits);

		//---- Label export data path
		JLabel labelExportDataPath = new JLabel ("Export data path:");
		labelExportDataPath.setBorder(new EmptyBorder(0, 5, 0, 5));
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 1;
		layoutConstraits.gridwidth = 1;
		displayPanel.add(labelExportDataPath, layoutConstraits);

		tfExportDataPath = new JTextField();
		tfExportDataPath.setSize(new Dimension(400, 25));
		tfExportDataPath.setPreferredSize(tfExportDataPath.getSize());
		tfExportDataPath.setEditable(false);
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 1;
		layoutConstraits.gridwidth = 1;
		displayPanel.add(tfExportDataPath, layoutConstraits);

		panelParent.add(displayPanel);
	}

	private void generateExportSettingsPanel (JPanel panelParent, FormMainHandler handler)
	{
		JPanel displayPanel = new JPanel();

		//displayPanel.setBackground(FormStyle.COLOR_MENU);
		displayPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		displayPanel.setLayout(new GridBagLayout());
		displayPanel.setSize(new Dimension (600, 40));
		displayPanel.setMinimumSize(displayPanel.getSize());
		displayPanel.setMaximumSize(displayPanel.getSize());
		displayPanel.setPreferredSize(displayPanel.getSize());
		GridBagConstraints layoutConstraits = new GridBagConstraints();

		cbDicomAttributes = new JCheckBox("DICOM Tags");
		layoutConstraits.gridx = 0;
		layoutConstraits.gridy = 0;
		layoutConstraits.gridwidth = 1;
		layoutConstraits.weightx = 0.2;
		layoutConstraits.weighty = 0.2;
		displayPanel.add(cbDicomAttributes, layoutConstraits);

		cbDicomFile = new JCheckBox("DICOM Masked");
		layoutConstraits.gridx = 1;
		layoutConstraits.gridy = 0;
		displayPanel.add(cbDicomFile, layoutConstraits);
		
		cbBinaryImage = new JCheckBox("Binary decoded");
		layoutConstraits.gridx = 2;
		layoutConstraits.gridy = 0;
		displayPanel.add(cbBinaryImage, layoutConstraits);

		cbDataRaw = new JCheckBox("Binary encoded");
		layoutConstraits.gridx = 3;
		layoutConstraits.gridy = 0;
		displayPanel.add(cbDataRaw, layoutConstraits);
		
		cbBitmapImage = new JCheckBox("Bitmap Image");
		cbBitmapImage.setSelected(true);
		layoutConstraits.gridx = 4;
		layoutConstraits.gridy = 0;
		displayPanel.add(cbBitmapImage, layoutConstraits);

		panelParent.add(displayPanel);
	}

}
