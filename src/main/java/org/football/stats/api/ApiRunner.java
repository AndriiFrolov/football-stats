package org.football.stats.api;

import io.restassured.RestAssured;
import org.football.stats.dto.*;
import org.football.stats.dto.teams.Teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiRunner {

    public static Teams getTeams(String league, String season) {
        return RestAssured.given()
                .queryParam("league", league)
                .queryParam("season", season)
                .get("/teams").as(Teams.class);
    }

    public static List<StandingsResponse> getStandings(String league, String season) {
        Map<String, Object> params = defaultParams(league, season);
        return request("/standings", params, StandingsResponse.class);
    }

    public static List<InjuriesResponse> getInjuries(String league, String season) {
        Map<String, Object> params = defaultParams(league, season);
        return request("/injuries", params, InjuriesResponse.class);
    }


    public static List<OddsResponse> getOdds(String league, String season, String bookmaker) {
        Map<String, Object> params = defaultParams(league, season);
        params.put("bookmaker", bookmaker);
        return request("/odds", params, OddsResponse.class);
    }

    public static List<FixturesResponse> getFixtures(String league, String season, Integer nextGames) {
        Map<String, Object> params = defaultParams(league, season);
        params.put("next", nextGames);
        return request("/fixtures", params, FixturesResponse.class);
    }

    private static <T extends IResponse> List<T> request(String url, Map<String, Object> queryParams, Class<T> responseType) {
        List<T> result = new ArrayList<>();
        T response = request(url, queryParams, null, responseType);
        result.add(response);
        for (int i = 2; i <= response.getPaging().getTotal(); i++) {
            result.add(request(url, queryParams, i, responseType));
        }
        return result;
    }

    private static <T extends IResponse> T request(String url, Map<String, Object> queryParams, Integer page, Class<T> responseType) {
        if (page != null) {
            queryParams.put("page", page);
        }
        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.printf("Sending request to %s with params: \n", url);
        printMap(queryParams);
        T response = RestAssured.given()
                .queryParams(queryParams)
                .get(url)
                .as(responseType);
        System.out.printf("%s returned %d pages%n", url, response.getPaging().getTotal());
        System.out.println("------------------------------------------------------------------------------------------------");
        return response;
    }

    private static Map<String, Object> defaultParams(String league, String season) {
        Map<String, Object> params = new HashMap<>();
        params.put("league", league);
        params.put("season", season);
        return params;
    }

    private static void printMap(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            System.out.println(key + ": " + value);
        }
    }


}
