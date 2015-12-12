package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by alex on 27.10.2015.
 */
public class FilesSingleton {
    private RandomAccessFile pointersFile;
    private RandomAccessFile spreadsheetFile;
    private static FilesSingleton instance = new FilesSingleton();

    private FilesSingleton() {
        try {
            pointersFile = new RandomAccessFile(SpreadSheet.TMP_POINTERS_FILE, "r");
            spreadsheetFile = new RandomAccessFile(SpreadSheet.TMP_SPREADSHEET_FILE, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static FilesSingleton getInstance() {
        return instance;
    }


    public RandomAccessFile getPointersFile() throws IOException {
        return pointersFile;
    }


    public RandomAccessFile getSpreadsheetFile() throws IOException {
        return spreadsheetFile;
    }

}
