package com.company.cells;


public class NumberCell extends Cell {
    public NumberCell(String value) {
        super(value);
    }

    @Override
    public void processValue() {
        setValue(getRawValue());
        setIsProcessed(true);
    }
}
