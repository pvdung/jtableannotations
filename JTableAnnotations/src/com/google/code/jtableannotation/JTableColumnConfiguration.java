package com.google.code.jtableannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JTableColumnConfiguration {
    int order();
    String name() default "JTableColumnConfiguration_NM";
    Align align() default Align.LEFT;
    String decimalFormat() default "JTableColumnConfiguration_DF";
    String dateFormat() default "JTableColumnConfiguration_DF";
    boolean editable() default false;
    boolean resizable() default false;
    int width() default 100;
    Class<? extends TableCellRenderer> cellRender() default DefaultTableCellRenderer.class;
    Class<? extends TableCellEditor> cellEditor() default DefaultCellEditor.class;
}
