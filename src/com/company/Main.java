package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        SpreadSheet spreadSheet = new SpreadSheet();
        spreadSheet.writeInputStreamToFile();
        spreadSheet.createPointersFile();
        spreadSheet.printCells();
    }
}
