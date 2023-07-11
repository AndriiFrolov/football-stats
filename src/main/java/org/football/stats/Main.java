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
import org.football.stats.logic.InjuriesTable;
import org.football.stats.logic.LeagueTable;
import org.football.stats.logic.odds.FixtureOddsTable;
import org.football.stats.props.PropertiesLoader;
import org.football.stats.props.PropertiesSupplier;
import org.football.stats.props.ResourceReader;

import java.util.*;

public class Main {
    private static List<String> leagues = new ArrayList<>();
    private static List<String> leagueIds = new ArrayList<>();
    private static Map<String, String> leaguesJson = new HashMap<>();
    private static String league;
    private static String leagueId;
    private static String season;
    private static String bookmaker;
    private static String bookmakerId;

    public static void main(String[] args) {
        loadSettings();

        for (String currentLeague : leagues) {
            league = currentLeague;
            leagueId = leaguesJson.get(league);

            saveInjuries();

            saveStandings();

            List<String> extraBetNamesToGenerate = Arrays.asList(
                    "Handicap Result", "Home Team Score a Goal"
            );
            saveOdds(extraBetNamesToGenerate);
        }
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
            Map<String, Object> credentials = new ObjectMapper().readValue(ResourceReader.getResourceAsString("credentials.json"), HashMap.class);
            if (!credentials.containsKey("installed")) {
                throw new RuntimeException("Make sure you have filled your credentials.json file. Delete tokens/StoredCredentials file if you want to use new credentials");
            }
            leaguesJson =
                    new ObjectMapper().readValue(ResourceReader.getResourceAsString("leagues.json"), HashMap.class);
            Map<String, String> bookmakersJson =
                    new ObjectMapper().readValue(ResourceReader.getResourceAsString("bookmakers.json"), HashMap.class);

            league = PropertiesSupplier.getProperty("league", "");
            //leagueId = PropertiesSupplier.getProperty("leagueId", "");
            bookmaker = PropertiesSupplier.getProperty("bookmaker", "");
            //bookmakerId = PropertiesSupplier.getProperty("bookmakerId", "");

            if (StringUtils.isNotEmpty(league)) {
                leagues = Arrays.asList(league.split(","));
                leagues.replaceAll(String::trim);
                leagues.forEach(parsedLeague -> {
                    if (!leaguesJson.containsKey(parsedLeague)) {
                        throw new RuntimeException("Don't know leagueId for league " + parsedLeague + " . Please add this info to the file leagues.json");
                    }
                    leagueIds.add(leaguesJson.get(parsedLeague));
                });
            } else {
                throw new RuntimeException("League or leagues must be passed!");
            }

            if (StringUtils.isNotEmpty(bookmaker)) {
                if (!bookmakersJson.containsKey(bookmaker)) {
                    throw new RuntimeException("Don't know bookmakerId for bookmaker " + bookmaker + " . Please add this info to the file bookmakers.json");
                }
                bookmakerId = bookmakersJson.get(bookmaker);
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

        System.out.printf("| %-45s | %-45s |%n", "Leagues", leagues);
        System.out.printf("| %-45s | %-45s |%n", "League IDs", leagueIds);
        System.out.printf("| %-45s | %-45s |%n", "Bookmaker", bookmaker);
        System.out.printf("| %-45s | %-45s |%n", "Bookmaker ID", bookmakerId);
        System.out.printf("| %-45s | %-45s |%n", "Season", season);
        System.out.printf("| %-45s | %-45s |%n", "Spreadsheet ID", PropertiesSupplier.getProperty("spreadsheet.id"));
        for (String parsedLeague : leagues) {
            System.out.printf("| %-45s | %-45s |%n", "Tab that must exist in spreadsheet:", parsedLeague.toUpperCase());
            System.out.printf("| %-45s | %-45s |%n", "Tab that must exist in spreadsheet:", parsedLeague.toUpperCase() + "_INJURIES");
            System.out.printf("| %-45s | %-45s |%n", "Tab that must exist in spreadsheet:", parsedLeague.toUpperCase() + "_BETS");
        }

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