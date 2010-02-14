package com.google.code.jtableannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JTableConfiguration {
    int rowHeight() default -1;
    SelectionMode selectionMode() default SelectionMode.SINGLE_SELECTION;
    boolean autoSorter() default true;
}
