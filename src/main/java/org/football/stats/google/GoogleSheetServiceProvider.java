package org.football.stats.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SheetsQuickStart {
    private static final String APPLICATION_NAME = "FootballStats";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE, SheetsScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            {
        // Load client secrets.
        InputStream in = SheetsQuickStart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new RuntimeException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
                GoogleClientSecrets clientSecrets =
                        null;
                try {
                    clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Build flow and trigger user authorization request.
                GoogleAuthorizationCodeFlow flow = null;
                try {
                    flow = new GoogleAuthorizationCodeFlow.Builder(
                            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                            .setAccessType("offline")
                            .build();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
                try {
                    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

    public  static Sheets getSheets() {
        final NetHttpTransport HTTP_TRANSPORT;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        return
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        Sheets service = getSheets();
        final String spreadsheetId = "1BcAwTMvaYpS2IAl3ClbGblm0YvBwHJL_d1qMr-UalP4";
        final String range = "Class Data!A2:E";
//        ValueRange response = service.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//        List<List<Object>> values = response.getValues();
//        if (values == null || values.isEmpty()) {
//            System.out.println("No data found.");
//        } else {
//            System.out.println("Name, Major");
//            for (List row : values) {
//                // Print columns A and E, which correspond to indices 0 and 4.
//                System.out.printf("%s, %s\n", row.get(0), row.get(4));
//            }
//        }

        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetResponse response = null;
        try {
            // Change the spreadsheet's title.
//            requests.add(new Request()
//                    .setUpdateSpreadsheetProperties(new UpdateSpreadsheetPropertiesRequest()
//                            .setProperties(new SpreadsheetProperties()
//                                    .setTitle("Football stats"))
//                            .setFields("title")));
            // Find and replace text.
            requests.add(new Request()
                    .setUpdateCells(new UpdateCellsRequest()
                            .set("test", "tes")));

            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);
            response = service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
            FindReplaceResponse findReplaceResponse = response.getReplies().get(1).getFindReplace();

            System.out.printf("%d replacements made.", findReplaceResponse.getOccurrencesChanged());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Spreadsheet not found with id '%s'.\n", spreadsheetId);
            } else {
                throw e;
            }
        }
        BatchUpdateSpreadsheetResponse response1 = response;
        int c = 0;
    }

}