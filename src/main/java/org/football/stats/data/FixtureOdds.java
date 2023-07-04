package org.football.stats.data;

import lombok.Data;
import org.football.stats.dto.OddsResponse;
import java.util.List;

@Data
public class FixtureOdds {
    private String date;
    private String team1;
    private String team2;

    private List<OddsResponse.Bet> bets;
}
