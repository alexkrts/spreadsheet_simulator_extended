package com.company;

import com.company.cells.Cell;
import com.company.cells.ExpressionCell;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 24.10.2015.
 */
public class SpreadSheet {
    public static final String TMP_SPREADSHEET_FILE = "spreadsheetTemp";
    public static final String TMP_POINTERS_FILE = "pointers.tmp";
    public static final int POINTER_LENGTH = 8;
    // TODO: change input/output files to streams
    public static final String INPUT_FILE = "spreadsheet.in";

    private List<Integer> cellsCountList = new ArrayList<>();
    private CellFactory cellFactory = new CellFactory();
    private long longestRowCellsCounter = 0;
    private int totalCellsCount = 0;

    public void writeInputStreamToFile() throws IOException {
        File pointersTempFile = new File(TMP_POINTERS_FILE);
        File spreadsheetTempFile = new File(TMP_SPREADSHEET_FILE);
        pointersTempFile.delete();
        spreadsheetTempFile.delete();

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
//        BufferedReader inputStream = new BufferedReader(new FileReader(INPUT_FILE));
        RandomAccessFile spreadsheetFile = new RandomAccessFile(TMP_SPREADSHEET_FILE, "rw");
        String inputLine;

        cellsCountList.add(0);
        while ((inputLine = inputStream.readLine()) != null) {
            int count = inputLine.length() - inputLine.replace("\t", "").length() + 1;
            String[] cells = splitCells(inputLine, count);


            if (longestRowCellsCounter < count) {
                longestRowCellsCounter = count;
            }
            totalCellsCount += cells.length;
            cellsCountList.add(totalCellsCount);

            for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
                if (cells[cellIndex].startsWith("=")) {
                    spreadsheetFile.writeUTF(cells[cellIndex]);
                } else {
                    Cell inputCell = cellFactory.createCell(cells[cellIndex]);
                    spreadsheetFile.writeUTF(inputCell.getValue());
                }
            }

        }
        inputStream.close();
        spreadsheetFile.close();

    }

    public void createPointersFile() throws IOException {
        RandomAccessFile spreadsheetFile = new RandomAccessFile(TMP_SPREADSHEET_FILE, "r");
        RandomAccessFile pointersFile = new RandomAccessFile(TMP_POINTERS_FILE, "rw");
        long spreadsheetLength = spreadsheetFile.length();

        spreadsheetFile.readUTF();
        pointersFile.writeLong(0);
        while (spreadsheetFile.getFilePointer() != spreadsheetLength) {
            pointersFile.writeLong(spreadsheetFile.getFilePointer());
            spreadsheetFile.readUTF();
        }
        spreadsheetFile.close();
        pointersFile.close();
    }


    public void printCells() throws IOException {
        RandomAccessFile spreadsheetFile = FilesSingleton.getInstance().getSpreadsheetFile();
        spreadsheetFile.seek(0);
        int currentRow = 1;
        int currentCell = 1;
        int cellsCountListSize = cellsCountList.size();

        long spreadsheetFileLength = spreadsheetFile.length();
        String currentSpreadsheetValue = spreadsheetFile.readUTF();


        while (spreadsheetFile.getFilePointer() < spreadsheetFileLength) {

            String cell;
            cell = currentSpreadsheetValue;
            int currentRowSize = cellsCountList.get(currentRow);

            if (cell.startsWith("=")) {
                long position = spreadsheetFile.getFilePointer();
                Cell trueCell = new ExpressionCell(cell, cellsCountList);
                cell = trueCell.getValue();
                spreadsheetFile.seek(position);
            }
            System.out.print(cell);
            if (currentCell != currentRowSize && currentRow < cellsCountListSize) {
                System.out.print("\t");
            }
            if (spreadsheetFile.getFilePointer() >= spreadsheetFileLength) {
                break;
            }

            if (currentCell == currentRowSize) {
                if (currentRow != cellsCountListSize - 1) {
                    System.out.print("\r\n");
                }
                currentRow++;
            }
            currentCell++;
            currentSpreadsheetValue = spreadsheetFile.readUTF();
        }
        FilesSingleton.getInstance().getSpreadsheetFile().close();
        FilesSingleton.getInstance().getPointersFile().close();

    }


    public String[] splitCells(String line, int size) {
        String[] cells = new String[size];
        int beginIndex = 0;
        int arrayIndex = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\t') {
                String subs = line.substring(beginIndex, i);
                cells[arrayIndex] = subs;
                arrayIndex++;
                beginIndex = i + 1;

            }

        }
        cells[size - 1] = line.substring(beginIndex, line.length());

        return cells;

    }


}
