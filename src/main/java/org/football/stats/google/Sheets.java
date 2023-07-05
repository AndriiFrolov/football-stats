package org.football.stats.google;

import com.google.api.services.sheets.v4.model.*;
import org.checkerframework.checker.units.qual.A;
import org.football.stats.props.PropertiesSupplier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sheets {

    public static void writeStrings(List<List<String>> values, String sheet) {
        List<List<Object>> objectValues = new ArrayList<>();
        // Perform type conversion
        for (List<String> row : values) {
            List<Object> objectRow = new ArrayList<>(row);
            objectValues.add(objectRow);
        }
        write(objectValues, sheet);
    }

    public static void write(List<List<Object>> values, String sheet) {
        clear(sheet);
        try {

            com.google.api.services.sheets.v4.Sheets sheetsService = GoogleSheetServiceProvider.getSheetsService();

            List<ValueRange> data = new ArrayList<>();
            ValueRange valueRange = new ValueRange();
            valueRange.setRange(sheet + "!A1:ZZ" + values.size());

            // Set the values to be written to the sheet
            valueRange.setValues(values);
            data.add(valueRange);

            // Write the data to the sheet
            BatchUpdateValuesRequest batchUpdateRequest = new BatchUpdateValuesRequest()
                    .setValueInputOption("RAW")
                    .setData(data);

            String spreadsheetId = PropertiesSupplier.getProperty("spreadsheet.id");
            sheetsService.spreadsheets().values()
                    .batchUpdate(spreadsheetId, batchUpdateRequest)
                    .execute();

            System.out.println("Data written to Google Sheets successfully!");
            System.out.println("Spreadsheet URL: https://docs.google.com/spreadsheets/d/" + spreadsheetId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void empty(String sheet) {
        int rows = 400;
        int columns = 400;
        List<List<Object>> values = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                row.add("");
            }
            values.add(row);
        }
        write(values, sheet);
    }

    public static void clear(String sheet) {
        // Create the clear values request
        ClearValuesRequest request = new ClearValuesRequest();
        String spreadsheetId = PropertiesSupplier.getProperty("spreadsheet.id");
        // Call the Sheets API to clear all values in the sheet
        com.google.api.services.sheets.v4.Sheets sheetsService = GoogleSheetServiceProvider.getSheetsService();
        try {
            com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values.Clear clear = sheetsService.spreadsheets().values().clear(spreadsheetId, sheet, request);
            ClearValuesResponse execute = clear.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Data cleared successfully from sheet " + sheet);
    }
    public static void mergeCells() {
        // Initialize Sheets client
        com.google.api.services.sheets.v4.Sheets sheetsService = GoogleSheetServiceProvider.getSheetsService();

        // Define the spreadsheet ID and range
        String spreadsheetId = PropertiesSupplier.getProperty("spreadsheet.id");

         // Create the merge cells request
        MergeCellsRequest mergeRequest = new MergeCellsRequest()
                .setRange(new GridRange().setSheetId(0).setStartRowIndex(0).setEndRowIndex(2)
                        .setStartColumnIndex(0).setEndColumnIndex(2))
                .setMergeType("MERGE_ALL");

        // Create the batch update request
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setMergeCells(mergeRequest));

        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
        batchUpdateSpreadsheetRequest.setRequests(requests);

        try {
            com.google.api.services.sheets.v4.Sheets.Spreadsheets.BatchUpdate batchUpdate = sheetsService.spreadsheets().batchUpdate(spreadsheetId,
                    new BatchUpdateSpreadsheetRequest().setRequests(requests));
            batchUpdate.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Cells merged successfully!");
    }
}
