package edu.hpc.andrey.zmask.gui.form;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import edu.hpc.andrey.dicom.anon.form.FormSettings;
import edu.hpc.andrey.zmask.gui.panel.PanelStatusBar;

public class FormMainPanelDown 
{
	private JPanel panel;
	
	private PanelStatusBar progressBar;
	
	public FormMainPanelDown ()
	{
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		createPanel();
	}
	
	public JPanel get ()
	{
		return panel;
	}
	
	private void createPanel ()
	{
		//---- Set current panel properties
		JPanel panelStatusBar = new JPanel();
		panelStatusBar.setLayout(new BoxLayout(panelStatusBar, BoxLayout.X_AXIS));
		panelStatusBar.setSize(new Dimension(50, 50));
		panel.add(panelStatusBar, BorderLayout.PAGE_END);

		panelStatusBar.add(Box.createRigidArea(new Dimension(5,0)));

		//---- Text labels & progress bar for processing
		JPanel panelProgressBarLocation = new JPanel();
		progressBar = new PanelStatusBar(panelProgressBarLocation, FormSettings.RESOURCE_ICO_PATH_IDLE, FormSettings.RESOURCE_ICO_PATH_PROCESSING);
		panelStatusBar.add(panelProgressBarLocation);
		//	SystemStatus.setGuiLink(panelProgressBar);

		panelStatusBar.add(Box.createRigidArea(new Dimension(5,0)));
		panelStatusBar.add(Box.createHorizontalGlue());
	}
	
	public PanelStatusBar getComponentStatusBar ()
	{
		return progressBar;
	}
}
