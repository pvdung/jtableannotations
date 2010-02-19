package com.google.code.jtableannotation;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public final class Configurator {

    public final void configureAndPopulateJTable(final JTable jtable, List<?> list) throws IllegalAccessException, InstantiationException {
        if (jtable == null) {
            throw new IllegalArgumentException("JTable must be not null!");
        }
        if (list == null) {
            throw new IllegalArgumentException("Your list must be not null!");
        }
        if (list.size() == 0) {
            return;
        }
        Field[] fields = configureJTable(list, jtable);
        populateJTable(list, fields, jtable);
    }

    private Field[] configureJTable(List<?> list, final JTable jtable) throws SecurityException, InstantiationException, IllegalAccessException {
        final Class<?> typeClass = list.get(0).getClass();
        final int numberOfRows = list.size();
        final int numberOfColumns = countColumnsFrom(typeClass);
        final TableColumnModel tcm = setupTableColumnModelWith(numberOfColumns);
        final String[] columnNames = getColumnNames(numberOfColumns, typeClass);
        final int rowHeight = getRowHeight(typeClass);
        final boolean autoSorter = getAutoSorter(typeClass);
        final int selectionMode = getSelectionMode(typeClass);
        finalSetup(columnNames, numberOfRows, jtable, tcm, rowHeight, autoSorter, selectionMode, typeClass, list);
        Field[] fields = typeClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(JTableColumnConfiguration.class) != null) {
                JTableColumnConfiguration columnAnnotation = field.getAnnotation(JTableColumnConfiguration.class);
                final TableColumn col = tcm.getColumn(columnAnnotation.order());
                final int width = columnAnnotation.width();
                final boolean resizable = columnAnnotation.resizable();

                String canonicalName = columnAnnotation.cellRender().getCanonicalName();
                TableCellRenderer tcr = columnAnnotation.cellRender().newInstance();
                try {
                    ((DefaultTableCellRenderer) tcr).setHorizontalAlignment(columnAnnotation.align().getAlign());
                } catch (Exception ex) {
                    System.err.println("It wasn't possible to apply horizontal alignment on this field : "+field.getName()+" ex:"+ex);
                    System.err.println("To solve this problem make sure that your cellrender is a subclass of DefaultTableCellRenderer!");
                }

                canonicalName = columnAnnotation.cellEditor().getCanonicalName();
                TableCellEditor tce = null;
                if (!"javax.swing.DefaultCellEditor".equals(canonicalName)) {
                    tce = columnAnnotation.cellEditor().newInstance();
                }

                if (tcr == null & tce == null) {
                    setupTableColumn(col, width, resizable);
                } else {
                    if (tcr != null & tce != null) {
                        setupTableColumn(col, width, tcr, tce, resizable);
                    } else if (tcr == null) {
                        setupTableColumn(col, width, tce, resizable);
                    } else {
                        setupTableColumn(col, width, tcr, resizable);
                    }
                }
            }
        }
        return fields;
    }

    private final int countColumnsFrom(Class<?> typeClass) {
        final Field[] fields = typeClass.getDeclaredFields();
        int count = 0;
        for (Field field : fields) {
            if (field.getAnnotation(JTableColumnConfiguration.class) != null) {
                count++;
            }
        }
        return count;
    }

    private void populateJTable(List<?> list, Field[] fields, final JTable jtable) throws IllegalAccessException, IllegalArgumentException {
        int row = 0;
        Iterator<?> it = list.iterator();
        while (it.hasNext()) {
            Object object = it.next();
            for (Field field : fields) {
                field.setAccessible(true);
                JTableColumnConfiguration columnAnnotation = field.getAnnotation(JTableColumnConfiguration.class);
                if (columnAnnotation != null) {
                    Object value = field.get(object);
                    if ("JTableColumnConfiguration_DF".equals(columnAnnotation.decimalFormat())) {
                        if ("JTableColumnConfiguration_DF".equals(columnAnnotation.dateFormat())) {
                            jtable.setValueAt(value, row, columnAnnotation.order());
                        } else {
                            DateFormat df = new SimpleDateFormat(columnAnnotation.dateFormat());
                            jtable.setValueAt(df.format(value), row, columnAnnotation.order());
                        }
                    } else {
                        DecimalFormat df = new DecimalFormat(columnAnnotation.decimalFormat());
                        jtable.setValueAt(df.format(value), row, columnAnnotation.order());
                    }
                }
            }
            row++;
        }
    }

    private TableColumnModel setupTableColumnModelWith(final int howMuchColumns) {
        final TableColumnModel tcm = new DefaultTableColumnModel();
        for (int i = 0; i < howMuchColumns; i++) {
            tcm.addColumn(new TableColumn());
        }
        return tcm;
    }

    private final String[] getColumnNames(final int numberOfRows, Class<?> typeClass) {
        final Field[] fields = typeClass.getDeclaredFields();
        String[] columnNames = new String[numberOfRows];
        for (Field field : fields) {
            if (field.getAnnotation(JTableColumnConfiguration.class) != null) {
                JTableColumnConfiguration columnAnnotation = field.getAnnotation(JTableColumnConfiguration.class);
                if ("JTableColumnConfiguration_NM".equals(columnAnnotation.name())) {
                    columnNames[columnAnnotation.order()] = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1, field.getName().length());
                } else {
                    columnNames[columnAnnotation.order()] = columnAnnotation.name();
                }
            }
        }
        return columnNames;
    }

    private final int getRowHeight(Class<?> typeClass) {
        int rowHeight = -1;
        JTableConfiguration config = typeClass.getAnnotation(JTableConfiguration.class);
        if (config != null) {
            rowHeight = config.rowHeight();
        }
        return rowHeight;
    }

    private final boolean getAutoSorter(Class<?> typeClass) {
        boolean autoSorter = true;
        JTableConfiguration config = typeClass.getAnnotation(JTableConfiguration.class);
        if (config != null) {
            autoSorter = config.autoSorter();
        }
        return autoSorter;
    }

    private final int getSelectionMode(Class<?> typeClass) {
        int selectionMode = 0;
        JTableConfiguration config = typeClass.getAnnotation(JTableConfiguration.class);
        if (config != null) {
            selectionMode = config.selectionMode().ordinal();
        }
        return selectionMode;
    }

    private void finalSetup(final String[] columnNames, final int rowCount, final JTable tab, final TableColumnModel tcm, final int rowHeight, final boolean autoSorter, final int selectionMode, final Class<?> typeClass, final List<?> list) {
        final boolean[] editable = getEditableCells(typeClass, tcm.getColumnCount());
        final boolean bindable = typeClass.getAnnotation(JTableConfiguration.class).bindable();
        TableModel tbm = createTableModel(columnNames, rowCount, editable, typeClass, list, bindable);
        tab.setColumnModel(tcm);
        tab.setModel(tbm);
        tab.setSelectionMode(selectionMode);
        tab.setAutoCreateRowSorter(autoSorter);
        if (rowHeight != -1) {
            tab.setRowHeight(rowHeight);
        }
    }

    private TableModel createTableModel(final String[] columnNames, final int rowCount, final boolean[] editable, final Class<?> typeClass, final List<?> list, final boolean bindable) {
        if (bindable) {
            return new DefaultTableModel(columnNames, rowCount) {

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return editable[columnIndex];
                }

                @Override
                public void setValueAt(Object aValue, int row, int column) {
                    Field exception = null;
                    try {
                        Object obj = list.get(row);
                        final Field[] fields = typeClass.getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            exception = field;
                            if (field.getAnnotation(JTableColumnConfiguration.class) != null) {
                                JTableColumnConfiguration columnAnnotation = field.getAnnotation(JTableColumnConfiguration.class);
                                if (columnAnnotation.order() == column) {
                                    if ("JTableColumnConfiguration_DF".equals(columnAnnotation.decimalFormat())) {
                                        if ("JTableColumnConfiguration_DF".equals(columnAnnotation.dateFormat())) {
                                            field.set(obj, aValue);
                                        } else {
                                            DateFormat df = new SimpleDateFormat(columnAnnotation.dateFormat());
                                            field.set(obj, df.parse(aValue.toString()));
                                        }
                                    } else {
                                        DecimalFormat df = new DecimalFormat(columnAnnotation.decimalFormat());
                                        field.set(obj, df.parse(aValue.toString()));
                                    }

                                }
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("The field " + exception.getName() + " could not asign the value = " + aValue + " ex=" + ex);
                    }
                    super.setValueAt(aValue, row, column);
                }
            };
        } else {
            return new DefaultTableModel(columnNames, rowCount) {

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return editable[columnIndex];
                }
            };
        }
    }

    private void setupTableColumn(final TableColumn column, final int width,
            final TableCellRenderer cellRender, final TableCellEditor cellEditor, final boolean resizable) {
        setupTableColumn(column, width, resizable);
        column.setCellRenderer(cellRender);
        column.setCellEditor(cellEditor);
    }

    private void setupTableColumn(final TableColumn column, final int width,
            final TableCellEditor cellEditor, final boolean resizable) {
        setupTableColumn(column, width, resizable);
        column.setCellEditor(cellEditor);

    }

    private void setupTableColumn(final TableColumn column, final int width,
            final TableCellRenderer cellRender, final boolean resizable) {
        setupTableColumn(column, width, resizable);
        column.setCellRenderer(cellRender);

    }

    private void setupTableColumn(final TableColumn column, final int width, final boolean resizable) {
        if (width != -1) {
            column.setWidth(width);
        }
        column.setResizable(resizable);
    }

    private final boolean[] getEditableCells(Class<?> typeClass, final int count) {
        final boolean[] editable = new boolean[count];
        Field[] fields = typeClass.getFields();
        for (Field field : fields) {
            if (field.getAnnotation(JTableColumnConfiguration.class) != null) {
                JTableColumnConfiguration columnAnnotation = field.getAnnotation(JTableColumnConfiguration.class);
                editable[columnAnnotation.order()] = columnAnnotation.editable();
            }
        }
        return editable;
    }
}
