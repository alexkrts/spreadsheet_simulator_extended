package com.company.cells;

import com.company.CellFactory;
import com.company.FilesSingleton;
import com.company.KeyProcessor;
import com.company.error.ExpressionError;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExpressionCell extends Cell {
    private static final String OPERATORS = "[-,+,*,/,^]{1}";
    //    private RandomAccessFile pointersFile;
//    private RandomAccessFile spreadsheetFile;
    private List<Integer> cellsCountList;

    public ExpressionCell(String value, List<Integer> cellsCountList) {
        super(value);
        this.cellsCountList = cellsCountList;
    }


/*
    public ExpressionCell(String value, List<Integer> cellsCountList, RandomAccessFile pointersFile, RandomAccessFile spreadsheetFile) {

        super(value);
        this.cellsCountList = cellsCountList;
        this.spreadsheetFile = spreadsheetFile;
        this.pointersFile = pointersFile;
    }
*/


    @Override
    public void processValue() throws IOException {

        String rawValue = getRawValue().substring(1);

        while (rawValue.contains("(")) {  //brackets processing
            rawValue = processValueInBrackets(rawValue);
        }
        if (rawValue.startsWith("-")) {
            rawValue = "0" + rawValue;
        }

        try {
            if (rawValue.matches(CellFactory.CELL_CONTAINS_ONLY_ADDRESS_PATTERN)) {
                String cellValue = getCell(rawValue);
                if (cellValue != null) {
                    setValue(cellValue);
                } else {
                    throw new ExpressionError(CellFactory.CELL_ERROR_VALUE + "Cell link not found");
                }
            } else if (rawValue.matches(CellFactory.CELL_CONTAINS_NOT_ONLY_ADDRESS_PATTERN)) {
                rawValue = rawValue.replaceAll(",", ".");
                List<String> operandsList = new ArrayList<String>(Arrays.asList(rawValue.replaceAll(OPERATORS, " ").split(" ")));
                List<String> operatorsList = new ArrayList<String>(Arrays.asList(rawValue.replaceAll("[A-Za-z0-9.]+", " ").trim().split(" ")));

                if (operandsList.contains("") || operatorsList.contains("")) {
                    operandsList.remove("");
                    operatorsList.remove("");
                }
                if (operandsList.size() == operatorsList.size()) {
                    throw new ExpressionError(CellFactory.CELL_ERROR_VALUE + "Wrong expression");
                }


                if (rawValue.contains("^")) {
                    processPow(operandsList, operatorsList);
                }

                for (int i = 0; i < operandsList.size(); i++) {
                    if (operandsList.get(i).matches(CellFactory.CELL_CONTAINS_ONLY_ADDRESS_PATTERN)) {
                        if (getCell(operandsList.get(i)) != null) {

                            operandsList.add(i, getCell(operandsList.get(i)));
                            operandsList.remove(i + 1);
                        } else {
                            throw new ExpressionError(CellFactory.CELL_ERROR_VALUE + "Cell link not found");
                        }
                    }

                }

                String expValue = processExpression(operandsList, operatorsList);

                expValue = removeDecimalZero(expValue);

                setValue(expValue);
            } else {
                throw new ExpressionError(CellFactory.CELL_ERROR_VALUE + "Incorrect cell value");
            }
        } catch (ExpressionError e) {
            setValue(e.getMessage());
            setIsProcessed(true);
        }
        setIsProcessed(true);
    }


    private String processExpression(List<String> operandsList, List<String> operatorsList) {

        double value;


        if (!operandsList.get(0).matches(CellFactory.CELL_NUMBER_PATTERN)) {
            return (CellFactory.CELL_ERROR_VALUE + "No such operand");
        }
        value = Double.parseDouble(operandsList.get(0));
        for (int i = 0; i < operatorsList.size(); i++) {

            String operator = operatorsList.get(i);
            int operandsIndex = i + 1;
            if (!operandsList.get(operandsIndex).matches(CellFactory.CELL_NUMBER_PATTERN)) {
                return (CellFactory.CELL_ERROR_VALUE + "Wrong operand");
            }

            try {
                boolean lengthSize = operator.length() == 2;
                boolean correctFirstOperator = operator.substring(0, 1).matches(OPERATORS);
                boolean correctSecondOperator = operator.substring(1).equals("-");


                if (lengthSize && (correctFirstOperator) && (correctSecondOperator)) {
                    value = processOperandWithNegativeValue(operandsList.get(operandsIndex), operator, value);
                } else {
                    value = processOperand(operandsList.get(operandsIndex), operator, value);
                }
            } catch (ExpressionError e) {
                return e.getMessage();
            }

        }

        return Double.toString(value);
    }

    private double processOperand(String operand, String operator, double value) throws ExpressionError {

        switch (operator) {

            case ("+"): {
                value = value + Double.parseDouble(operand);

                break;
            }
            case ("-"): {
                value = value - Double.parseDouble(operand);

                break;
            }
            case ("*"): {
                value = value * Double.parseDouble(operand);

                break;
            }
            case ("/"): {
                double doubleValue = Double.parseDouble(operand);
                if (doubleValue == 0) {
                    throw new ExpressionError(CellFactory.CELL_ERROR_VALUE + "Division by zero");
                }
                value = value / Double.parseDouble(operand);

                break;
            }
            case ("^"): {

                value = Math.pow(value, (-Double.parseDouble(operand)));

                break;
            }

            default: {
                throw new ExpressionError(CellFactory.CELL_ERROR_VALUE + "Wrong operand");

            }


        }

        return value;
    }


    private double processOperandWithNegativeValue(String operand, String operator, double value) throws ExpressionError {
        String mainOperator = operator.substring(0, 1);
        switch (mainOperator) {
            case ("+"): {
                value = value + (-Double.parseDouble(operand));

                break;
            }
            case ("-"): {
                value = value - (-Double.parseDouble(operand));

                break;
            }
            case ("*"): {
                value = value * (-Double.parseDouble(operand));

                break;
            }
            case ("/"): {
                double doubleValue = Double.parseDouble(operand);
                if (doubleValue == 0) {
                    throw new ExpressionError(CellFactory.CELL_ERROR_VALUE + "Division by zero");
                }
                value = value / (-Double.parseDouble(operand));

                break;
            }
            case ("^"): {

                value = Math.pow(value, (-Double.parseDouble(operand)));

                break;
            }

            default: {
                throw new ExpressionError(CellFactory.CELL_ERROR_VALUE + "Wrong operand");

            }


        }

        return value;
    }


    private String removeDecimalZero(String value) {

        if (!value.matches(CellFactory.CELL_NUMBER_PATTERN)) {
            return value;
        }
        double d = Double.parseDouble(value);
        if ((d - (int) d) == 0) {

            return Integer.toString((int) d);
        } else {
            return value;
        }


    }

    private String processValueInBrackets(String valueWithBrackets) throws IOException {
        boolean breaksExistFlag = false;
        int breaksBalancer = 0;
        int bracketsOpenPos = 0;
        int bracketsClosePos = 0;
        StringBuilder newValue = new StringBuilder();


        char[] valArray = valueWithBrackets.toCharArray();
        for (int i = 0; i < valArray.length; i++) {
            if (valArray[i] == '(') {
                bracketsOpenPos = i + 1;
                breaksBalancer = 1;
                breaksExistFlag = true;
            } else if (valArray[i] == ')') {
                breaksBalancer--;
                bracketsClosePos = i;
            }
            if (breaksBalancer == 0 && breaksExistFlag == true) {
                String expressValue = valueWithBrackets.substring(bracketsOpenPos, bracketsClosePos);
                if (expressValue.matches(CellFactory.CELL_NUMBER_PATTERN)) {

                    String startExp = valueWithBrackets.substring(0, (bracketsOpenPos - 1));
                    String endExp = valueWithBrackets.substring(bracketsClosePos + 1) + "+0";
                    newValue = new StringBuilder(startExp + expressValue + endExp);
                    return newValue.toString();
                }
                expressValue = "=" + expressValue;
                Cell bracketCell = new ExpressionCell(expressValue, cellsCountList);
                newValue = new StringBuilder(bracketCell.getValue());
                break;
            }

        }
        String startExp = valueWithBrackets.substring(0, (bracketsOpenPos - 1));
        String endExp = valueWithBrackets.substring(bracketsClosePos + 1) + "+0";
        newValue = new StringBuilder(startExp + newValue + endExp);
        return newValue.toString();
    }


    private void processPow(List<String> operandsList, List<String> operatorsList) {

        while (operatorsList.contains("^")) {
            double operand1;
            double operand2;
            double powValue;
            int powIndex = operatorsList.indexOf("^");
            operand1 = Double.parseDouble(operandsList.get(powIndex));
            operand2 = Double.parseDouble(operandsList.get(powIndex + 1));
            powValue = Math.pow(operand1, operand2);
            operatorsList.remove(powIndex);
            operandsList.remove(powIndex);
            operandsList.remove(powIndex);

            operandsList.add(powIndex, removeDecimalZero(Double.toString(powValue)));

        }


    }


    public String getCell(String key) throws IOException {
        KeyProcessor keyProcessor = new KeyProcessor();

        long pointerPosition = keyProcessor.convertCellKeyToPointer(key, cellsCountList);

        RandomAccessFile pointerFile = FilesSingleton.getInstance().getPointersFile();
        RandomAccessFile spreadsheetFile = FilesSingleton.getInstance().getSpreadsheetFile();

        pointerFile.seek(pointerPosition);
        long cellPosition = pointerFile.readLong();
        spreadsheetFile.seek(cellPosition);
        String currentSymbol = spreadsheetFile.readUTF();
        String cellValue;
        //   spreadsheetFile.close();
//        pointerFile.close();
        cellValue = currentSymbol;
        if (cellValue.startsWith("=")) {
            ExpressionCell cell = new ExpressionCell(cellValue, cellsCountList);
            cellValue = cell.getValue();
        }

        return cellValue;

    }


}
