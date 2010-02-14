package com.google.code.jtableannotation;

import javax.swing.SwingConstants;

public enum Align {
    LEFT(SwingConstants.LEFT),RIGHT(SwingConstants.RIGHT),CENTER(SwingConstants.CENTER);
    private final int align;
    private Align(int value){
        align = value;
    }
    public int getAlign(){
        return align;
    }
}
