package views;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import models.Part;


public class PartListCellRenderer implements ListCellRenderer<Part> {
	
private final DefaultListCellRenderer DEFAULT_RENDERER = new DefaultListCellRenderer();
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Part> list, Part value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) DEFAULT_RENDERER.getListCellRendererComponent(list, value.getPartName(), index, isSelected, cellHasFocus);
		return renderer;
	}
	

}
