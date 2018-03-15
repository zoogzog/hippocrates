package edu.hpc.andrey.zmask.gui.form;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import edu.hpc.andrey.zmask.gui.panel.PanelImageView;

public class FormMainPanelCenter 
{
	private JPanel panel;

	private PanelImageView panelImageView;

	//----------------------------------------------------------------

	public FormMainPanelCenter (FormMainMouse controllerMouse)
	{
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


		createPanel(controllerMouse);
	}

	public JPanel get ()
	{
		return panel;
	}

	//----------------------------------------------------------------

	private void createPanel (FormMainMouse controllerMouse)
	{
		JPanel ImageViewPanel = new JPanel();
		ImageViewPanel.setLayout(new BorderLayout());
		ImageViewPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		JPanel panelMainImageViewer= new JPanel();
		panelMainImageViewer.setLayout(new BorderLayout());
		ImageViewPanel.add(panelMainImageViewer, BorderLayout.CENTER);

		panelImageView = new PanelImageView();
		panelImageView.addMouseMotionListener(controllerMouse);
		panelImageView.addMouseWheelListener(controllerMouse);
		panelImageView.addMouseListener(controllerMouse);
		ImageViewPanel.add(panelImageView);

		panel.add(ImageViewPanel, BorderLayout.CENTER);
	}

	//----------------------------------------------------------------

	public PanelImageView getComponentPanelImageView ()
	{
		return panelImageView;
	}

	/**
	 * Resets this panel to default.
	 */
	public void resetState ()
	{
		panelImageView.resetImagePosition();
		panelImageView.freeImage();
	}
}
