package com.company.cells;


public class EmptyCell extends Cell {
    public EmptyCell() {
        super("");
    }

    @Override
    public void processValue() {

        setValue(getRawValue());
        setIsProcessed(true);
    }
}
