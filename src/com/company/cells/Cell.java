package com.company.cells;

import java.io.IOException;


public abstract class Cell {
    private String value;
    private String rawValue;
    private boolean isProcessed = false;


    public Cell(String rawValue) {
        this.rawValue = rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }


    public abstract void processValue() throws IOException;

    public synchronized String getValue() throws IOException {
        if (!isProcessed) {
            processValue();
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public boolean isProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(boolean isProcessed) {
        this.isProcessed = isProcessed;
    }


}
