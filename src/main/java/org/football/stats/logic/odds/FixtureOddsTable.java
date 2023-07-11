package org.football.stats.logic.odds;

import org.football.stats.data.FixtureOdds;
import org.football.stats.dto.OddsResponse;
import org.football.stats.google.Sheets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FixtureOddsTable {

    public static void write(List<FixtureOdds> oddsForFixtures, String sheet) {
        List<List<Object>> res = new ArrayList<>();
        if (oddsForFixtures.isEmpty()) {
            System.out.println("!!!! API Odds & Fixtures response is empty!");
            res.add(Collections.singletonList("API Odds & Fixtures  response was empty. Perhaps not exist?"));
        } else {
            Header header = new Header();
            header.add(oddsForFixtures);
            res.add(header.getH1());
            res.add(header.getH2());
            System.out.println("--------------------------------------");
            for (FixtureOdds oddsForFixture : oddsForFixtures) {
                List<Object> row = header.getRowTemplate();
                row.set(0, readableDate(oddsForFixture.getDate()));
                row.set(1, oddsForFixture.getTeam1());
                row.set(2, oddsForFixture.getTeam2());

                System.out.println("Parsing odds for fixture " + oddsForFixture.getTeam1() + " - " + oddsForFixture.getTeam2());
                for (OddsResponse.Bet bet : oddsForFixture.getBets()) {
                    String betName = bet.getName();

                    for (OddsResponse.Value value : bet.getValues()) {
                        int index = header.getIndexH2(betName, value.getValue());
                        row.set(index, value.getOdd());
                    }
                }

                res.add(row);
            }
        }
        Sheets.write(res, sheet);
        System.out.println("--------------------------------------");
    }

    private static Object readableDate(String date) {

        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Parse the date-time string
        LocalDateTime localDateTime = LocalDateTime.parse(date, inputFormatter);

        // Format the date-time in a readable format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        return localDateTime.format(outputFormatter);
    }
}
