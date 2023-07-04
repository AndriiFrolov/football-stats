package org.football.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.football.stats.api.ApiBuilder;
import org.football.stats.api.ApiRunner;
import org.football.stats.data.DataHelper;
import org.football.stats.data.FixtureOdds;
import org.football.stats.dto.FixturesResponse;
import org.football.stats.dto.InjuriesResponse;
import org.football.stats.dto.OddsResponse;
import org.football.stats.dto.StandingsResponse;
import org.football.stats.logic.FixtureOddsTable;
import org.football.stats.logic.InjuriesTable;
import org.football.stats.logic.LeagueTable;
import org.football.stats.props.PropertiesLoader;
import org.football.stats.props.PropertiesSupplier;
import org.football.stats.props.ResourceReader;

import java.util.*;

public class Main {
    private static String league;
    private static String leagueId;
    private static String season;
    private static String bookmakerId;

    public static void main(String[] args) {
        loadSettings();

        saveInjuries();

        saveStandings();

        saveOdds();
    }

    private static void saveOdds() {
        List<FixturesResponse> fixtures = ApiRunner.getFixtures(leagueId, season, 15);
        List<OddsResponse> odds = ApiRunner.getOdds(leagueId, season, bookmakerId);
        List<FixtureOdds> oddsForFixtures = DataHelper.getOddsForFixtures(fixtures, odds, Arrays.asList(
                "Match Winner",
                "Exact Score",
                "Goals Over/Under",
                "Team To Score First",
                "Double Chance",
                "Both Teams Score",
                "Winning Margin",
                "First Half Winner",
                "Corners 1x2",
                "Total Goals (3 way)"
        ));
        FixtureOddsTable.write(oddsForFixtures, league.toUpperCase() + "_BETS");
    }

    private static void saveStandings() {
        List<StandingsResponse> standings = ApiRunner.getStandings(leagueId, season);
        LeagueTable.write(standings, league.toUpperCase());
    }

    private static void saveInjuries() {
        List<InjuriesResponse> injuries = ApiRunner.getInjuries(leagueId, season);
        InjuriesTable.write(injuries, league.toUpperCase() + "_INJURIES");
    }


    private static void loadSettings() {
        try {
            PropertiesLoader.loadGlobalProperties(Main.class, "settings.properties");
            Map<String,String> leagues =
                    new ObjectMapper().readValue(ResourceReader.getResourceAsString("leagues.json"), HashMap.class);
            Map<String,String> bookmakers =
                    new ObjectMapper().readValue(ResourceReader.getResourceAsString("bookmakers.json"), HashMap.class);
            league = PropertiesSupplier.getProperty("league");
            leagueId = leagues.get(league);
            bookmakerId = bookmakers.get(PropertiesSupplier.getProperty("bookmaker"));
            season = PropertiesSupplier.getProperty("season");
            ApiBuilder.build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}