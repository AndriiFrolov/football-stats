package org.football.stats.data;

import org.football.stats.dto.FixturesResponse;
import org.football.stats.dto.OddsResponse;
import org.football.stats.logic.odds.Header;
import org.football.stats.logic.odds.OddColumn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataHelper {

    public static List<FixtureOdds> getOddsForFixtures(List<FixturesResponse> fixturesResponseList, List<OddsResponse> oddsResponseList, List<String> extraBetNames) {
        Set<String> betNames = Header.columns.stream().filter(OddColumn::isOdd).map(OddColumn::getName).collect(Collectors.toSet());
        betNames.addAll(extraBetNames);
        Set<FixturesResponse.FixtureData> fixtures = fixturesResponseList.stream()
                .flatMap(fixturesResponse -> fixturesResponse.getResponse().stream())
                .collect(Collectors.toSet());
        Set<Integer> fixtureIds = fixtures.stream().map(fixtureData -> fixtureData.getFixture().getId())
                .collect(Collectors.toSet());

        Set<OddsResponse.Response> odds = oddsResponseList.stream()
                .flatMap(oddsResponse -> oddsResponse.getResponse().stream())
                .filter(oddsResponse -> fixtureIds.contains(oddsResponse.getFixture().getId()))
                .collect(Collectors.toSet());

        List<FixtureOdds> result = new ArrayList<>();

        for (OddsResponse.Response odd : odds) {
            FixtureOdds fixtureOdds = new FixtureOdds();
            FixturesResponse.FixtureData fixture = getFixtureById(fixtures, odd.getFixture().getId());
            fixtureOdds.setDate(fixture.getFixture().getDate());
            fixtureOdds.setTeam1(fixture.getTeams().getHome().getName());
            fixtureOdds.setTeam2(fixture.getTeams().getAway().getName());

            List<OddsResponse.Bet> requiredBets = odd.getBookmakers().get(0).getBets()
                    .stream()
                    .filter(bet -> betNames.contains(bet.getName()))
                    .collect(Collectors.toList());

            fixtureOdds.setBets(requiredBets);
            result.add(fixtureOdds);
        }
        return result;
    }

    private static FixturesResponse.FixtureData getFixtureById(Set<FixturesResponse.FixtureData> fixtures, Integer id) {
        List<FixturesResponse.FixtureData> res = fixtures.stream().filter(fixture -> fixture.getFixture().getId() == id).collect(Collectors.toList());
        if (res.size() != 1) {
            throw new RuntimeException(("For some reason fixtures has not 1 fixture with id " + id));
        }
        return res.get(0);
    }
}
