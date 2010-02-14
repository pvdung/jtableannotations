package com.google.code.jtableannotation;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class IconCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(final JTable table,final Object value,final boolean isSelected,final boolean hasFocus,final int row, int column) {
        final Icon imagem = (Icon) (new javax.swing.ImageIcon(value.toString()));
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        setIcon(imagem);
        return this;
    }
}
