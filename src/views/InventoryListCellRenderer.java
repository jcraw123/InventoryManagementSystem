package views;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import models.Inventory;


public class InventoryListCellRenderer implements ListCellRenderer<Inventory>  {
	
private final DefaultListCellRenderer DEFAULT_RENDERER = new DefaultListCellRenderer();
    
    
    public Component getListCellRendererComponent(JList<? extends Inventory> list, Inventory value, int index,
            boolean isSelected, boolean cellHasFocus) {
        JLabel renderer = (JLabel) DEFAULT_RENDERER.getListCellRendererComponent(list, value.getId(), index, isSelected, cellHasFocus);
        return renderer;
    }


}
