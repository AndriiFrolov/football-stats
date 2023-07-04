package org.football.stats.logic;

import org.football.stats.data.FixtureOdds;
import org.football.stats.dto.OddsResponse;
import org.football.stats.dto.StandingsResponse;
import org.football.stats.google.Sheets;

import java.util.*;

public class FixtureOddsTable {

    public static void write(List<FixtureOdds> oddsForFixtures, String sheet) {
        List<List<Object>> res = new ArrayList<>();
        if (oddsForFixtures.isEmpty()) {
            throw new RuntimeException("Odds & Fixtures response is empty!");
        }

        Header header = new Header(Arrays.asList("Date", "Home Team", "Away Team"));
        header.add(oddsForFixtures);
        res.add(header.getH1());
        res.add(header.getH2());
        System.out.println("--------------------------------------");
        for (FixtureOdds oddsForFixture : oddsForFixtures) {
            List<Object> row = header.getRowTemplate();
            row.set(0, oddsForFixture.getDate());
            row.set(1, oddsForFixture.getTeam1());
            row.set(2, oddsForFixture.getTeam2());

            System.out.println("Parsing odds for fixture " + oddsForFixture.getTeam1() + " - " + oddsForFixture.getTeam2());
            for (OddsResponse.Bet bet : oddsForFixture.getBets()) {
                String betName = bet.getName();

                for (OddsResponse.Value value : bet.getValues()) {
                    int index = header.getIndexH2(betName, value.getValue());
                    row.set(index, value.getOdd().replaceAll("'", ""));
                }
            }

            res.add(row);
        }

        Sheets.write(res, sheet);
        System.out.println("--------------------------------------");
    }
}
