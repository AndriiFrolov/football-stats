package org.football.stats.logic;

import org.football.stats.dto.StandingsResponse;
import org.football.stats.google.Sheets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeagueTable {

    public static void write(List<StandingsResponse> standings, String sheet) {
        List<List<Object>> res = new ArrayList<>();
        List<Object> header = Arrays.asList("Place", "Team", "Points", "Goals", "Conceeded Goals", "Last 5 games");
        res.add(header);

        if (standings.isEmpty() || standings.get(0).getResponse().isEmpty()) {
            throw new RuntimeException("Standings response is empty!");
        }
        for (List<StandingsResponse.StandingsEntry> standing : standings.get(0).getResponse().get(0).getLeague().getStandings()) {
            //Typically it should be just one standing, but for some leagues like MLS - it is 2
            for (StandingsResponse.StandingsEntry standingsEntry : standing) {
                List<Object> teamRow = new ArrayList<>();
                teamRow.add(String.valueOf(standingsEntry.getRank()));
                teamRow.add(standingsEntry.getTeam().getName());
                teamRow.add(String.valueOf(standingsEntry.getPoints()));
                teamRow.add(String.valueOf(standingsEntry.getAll().getGoals().getForGoals()));
                teamRow.add(String.valueOf(standingsEntry.getAll().getGoals().getAgainst()));
                teamRow.add(standingsEntry.getForm());
                res.add(teamRow);
            }
            res.add(Collections.singletonList("----"));
        }
        Sheets.write(res, sheet);
    }
}
