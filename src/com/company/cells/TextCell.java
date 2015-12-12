package com.company.cells;


public class TextCell extends Cell {
    public TextCell(String value) {
        super(value);

    }

    @Override
    public void processValue() {
        setValue(getRawValue().substring(1));
        setIsProcessed(true);

    }
}
