package org.football.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.football.stats.api.ApiBuilder;
import org.football.stats.api.ApiRunner;
import org.football.stats.data.DataHelper;
import org.football.stats.data.FixtureOdds;
import org.football.stats.dto.FixturesResponse;
import org.football.stats.dto.InjuriesResponse;
import org.football.stats.dto.OddsResponse;
import org.football.stats.dto.StandingsResponse;
import org.football.stats.logic.odds.FixtureOddsTable;
import org.football.stats.logic.InjuriesTable;
import org.football.stats.logic.LeagueTable;
import org.football.stats.props.PropertiesLoader;
import org.football.stats.props.PropertiesSupplier;
import org.football.stats.props.ResourceReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static String league;
    private static String leagueId;
    private static String season;
    private static String bookmaker;
    private static String bookmakerId;

    public static void main(String[] args) {
        loadSettings();

        saveInjuries();

        saveStandings();

        List<String> extraBetNamesToGenerate = Arrays.asList(
                "Handicap Result", "Home Team Score a Goal"
        );
        saveOdds(extraBetNamesToGenerate);
    }

    private static void saveOdds(List<String> extraBetNamesToGenerate) {
        List<FixturesResponse> fixtures = ApiRunner.getFixtures(leagueId, season, 15);
        List<OddsResponse> odds = ApiRunner.getOdds(leagueId, season, bookmakerId);

        List<FixtureOdds> oddsForFixtures = DataHelper.getOddsForFixtures(fixtures, odds, extraBetNamesToGenerate);
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
            Map<String, String> leagues =
                    new ObjectMapper().readValue(ResourceReader.getResourceAsString("leagues.json"), HashMap.class);
            Map<String, String> bookmakers =
                    new ObjectMapper().readValue(ResourceReader.getResourceAsString("bookmakers.json"), HashMap.class);

            league = PropertiesSupplier.getProperty("league", "");
            leagueId = PropertiesSupplier.getProperty("leagueId", "");
            bookmaker = PropertiesSupplier.getProperty("bookmaker", "");
            bookmakerId = PropertiesSupplier.getProperty("bookmakerId", "");

            if (StringUtils.isNotEmpty(leagueId)) {
                if (leagues.containsValue(leagueId)) {
                    throw new RuntimeException("Don't know league name for leagueId " + leagueId + " . Please add this info to the file leagues.json");
                }
                league = getKeyFromMap(leagues, leagueId);
            } else if (StringUtils.isNotEmpty(league)) {
                if (!leagues.containsKey(league)) {
                    throw new RuntimeException("Don't know leagueId for league " + league + " . Please add this info to the file leagues.json");
                }
                leagueId = leagues.get(league);
            } else {
                throw new RuntimeException("Either league or leagueId must be passed!");
            }

            if (StringUtils.isNotEmpty(bookmakerId)) {
                bookmaker = getKeyFromMap(bookmakers, bookmakerId);
            } else if (StringUtils.isNotEmpty(bookmaker)) {
                if (!bookmakers.containsKey(bookmaker)) {
                    throw new RuntimeException("Don't know bookmakerId for bookmaker " + bookmaker + " . Please add this info to the file bookmakers.json");
                }
                bookmakerId = bookmakers.get(bookmaker);
            } else {
                throw new RuntimeException("Either bookmaker or bookmakerId must be passed!");
            }

            season = PropertiesSupplier.getProperty("season");
            printSettings();

            ApiBuilder.build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printSettings() {
        // Print the table header
        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.printf("| %-45s | %-45s |%n", "Variable", "Value");
        System.out.println("------------------------------------------------------------------------------------------------");

        System.out.printf("| %-45s | %-45s |%n", "League", league);
        System.out.printf("| %-45s | %-45s |%n", "League ID", leagueId);
        System.out.printf("| %-45s | %-45s |%n", "Bookmaker", bookmaker);
        System.out.printf("| %-45s | %-45s |%n", "Bookmaker ID", bookmakerId);
        System.out.printf("| %-45s | %-45s |%n", "Season", season);
        System.out.printf("| %-45s | %-45s |%n", "Spreadsheet ID", PropertiesSupplier.getProperty("spreadsheet.id"));
        System.out.printf("| %-45s | %-45s |%n", "Tab that must exist in spreadsheet:", league.toUpperCase());
        System.out.printf("| %-45s | %-45s |%n", "Tab that must exist in spreadsheet:", league.toUpperCase() + "_INJURIES");
        System.out.printf("| %-45s | %-45s |%n", "Tab that must exist in spreadsheet:", league.toUpperCase() + "_BETS");

        System.out.println("------------------------------------------------------------------------------------------------");
    }

    private static String getKeyFromMap(Map<String, String> map, String targetValue) {
        String targetKey = null;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(targetValue)) {
                if (targetKey != null) {
                    throw new RuntimeException("More than 1 property has ID " + targetValue + " ! Make sure you have correct values in .json files");
                }
                targetKey = entry.getKey();
            }
        }

        if (targetKey != null) {
            return targetKey;
        } else {
            throw new RuntimeException("Value " + targetValue + " not found in the json.");
        }
    }
}