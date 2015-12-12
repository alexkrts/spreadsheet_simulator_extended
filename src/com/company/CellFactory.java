package com.company;

import com.company.cells.*;

import java.util.Map;

public class CellFactory {

    public static final String CELL_TEXT_PATTERN = "^\\'.+";
    public static final String CELL_NUMBER_PATTERN = "^[+-]?(?:\\d+\\,?\\d*|\\d*\\.?\\d+)[\\r\\n]*$";
    public static final String CELL_CONTAINS_ONLY_ADDRESS_PATTERN = "^([A-Z]+[0-9]+)$";
    public static final String CELL_CONTAINS_NOT_ONLY_ADDRESS_PATTERN = ".+[+|\\-|*|/|^].+";
    public static final String CELL_ERROR_VALUE = "#";

    public Cell createCell(String value) {


        if (value.matches(CELL_TEXT_PATTERN)) {
            TextCell textCell = new TextCell(value);

            return textCell;

        } else if (value.matches(CELL_NUMBER_PATTERN)) {
            value = value.replaceAll(",", ".");
            NumberCell numberCell = new NumberCell(value);

            return numberCell;


        } else if (value.equals("")) {
            EmptyCell emptyCell = new EmptyCell();
            return emptyCell;

        } else {
            ErrorCell errorCell = new ErrorCell("Unknown cell format");
            return errorCell;

        }

    }


}





