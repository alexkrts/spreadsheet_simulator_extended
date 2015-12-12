package com.company;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by alex on 25.10.2015.
 */
public class KeyProcessor {
    private List<Integer> keyList;
    private StringBuilder stringKey;
    private static final int ALPHABET_START_ASCII = 65;
    private static final int ALPHABET_SIZE = 26;


    public long convertCellKeyToPointer(String key, List<Integer> cellsCountList) {
        String columnPart = key.replaceAll("[0-9]", "").trim();
        int rowIndex = Integer.parseInt(key.replaceAll("[A-Z]", ""));
        int columnIndex = convertKeyToInt(columnPart);
        long pointerPosition = (cellsCountList.get(rowIndex-1) + columnIndex) * SpreadSheet.POINTER_LENGTH;
        return pointerPosition;
    }

    public long convertCellKeyToNextPointer(String key,List<Integer> cellsCountList) {
        String columnPart = key.replaceAll("[0-9]", "").trim();
        int rowIndex = Integer.parseInt(key.replaceAll("[A-Z]", ""));
        int columnIndex = convertKeyToInt(columnPart);
        long pointerPosition = (cellsCountList.get(rowIndex-1) + columnIndex+1) * SpreadSheet.POINTER_LENGTH;
        return pointerPosition;
    }


    public int convertKeyToInt(String stringKey) {
        int intKey = 0;
        char[] keyArray = stringKey.toCharArray();
        int letter;
        for (int letterPosition = 1; letterPosition <= keyArray.length; letterPosition++) {
            letter = keyArray[letterPosition - 1] - ALPHABET_START_ASCII;
            intKey = (int) (intKey + Math.pow(ALPHABET_SIZE, keyArray.length - letterPosition) * letter);


        }
        return intKey;
    }


    public synchronized String convertKeyToString(int intKey) {
        keyList = new LinkedList<>();
        stringKey = new StringBuilder();
        calculateColumnKey(intKey);
        generateLetterKey();
        return stringKey.toString();

    }


    private void calculateColumnKey(int key) {
        int result;
        result = key % ALPHABET_SIZE;
        keyList.add(0, result);

        if (key > 25) {
            key = key / ALPHABET_SIZE;
            calculateColumnKey(key);

        }

    }

    private void generateLetterKey() {
        int intKey;
        for (Integer i : keyList) {
            intKey = i + ALPHABET_START_ASCII;
            char letter = (char) intKey;
            stringKey.append(letter);

        }

    }

}
