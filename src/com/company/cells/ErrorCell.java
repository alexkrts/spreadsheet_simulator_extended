package com.company.cells;


public class ErrorCell extends Cell {

    public ErrorCell(String description) {
        super("# " + description);
    }


    @Override
    public void processValue() {

        setValue(getRawValue());
        setIsProcessed(true);
    }
}
