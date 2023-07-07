package org.football.stats.logic.odds;

import lombok.Data;

@Data
public class OddColumn {
    private String name;
    private int columnIdx;
    private String column;
    private int size;
    private boolean isOdd;

    public OddColumn(String name, String column, int size, boolean isOdd) {
        this.name = name;
        this.column = column;
        this.size = size;
        this.columnIdx = getColumnIndex(column);
        this.isOdd = isOdd;
    }

    private int getColumnIndex(String columnName) {
        int columnIndex = 0;
        int power = 0;

        for (int i = columnName.length() - 1; i >= 0; i--) {
            char c = columnName.charAt(i);
            int value = c - 'A' + 1;
            columnIndex += value * Math.pow(26, power);
            power++;
        }

        return columnIndex - 1;
    }
}
