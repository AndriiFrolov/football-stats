package org.football.stats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OddsResponse implements IResponse {
    private String get;
    private Parameters parameters;
    private List<Object> errors;
    private Integer results;
    private Paging paging;
    private List<Response> response;

    @Data
    public static class Parameters {
        private String league;
        private String season;
        private String page;
        private String fixture;
        private String bookmaker;
    }

    @Data
    public static class Response {
        private League league;
        private Fixture fixture;
        private String update;
        private List<Bookmaker> bookmakers;
    }

    @Data
    public static class League {
        private Integer id;
        private String name;
        private String country;
        private String logo;
        private Object flag;
        private Integer season;
    }

    @Data
    public static class Fixture {
        private Integer id;
        private String timezone;
        private String date;
        private Integer timestamp;
    }

    @Data
    public static class Bookmaker {
        private Integer id;
        private String name;
        private List<Bet> bets;
    }

    @Data
    public static class Bet {
        private Integer id;
        private String name;
        private List<Value> values;
        private Map<String, Object> additionalProperties;
    }

    @Data
    public static class Value {
        private String value;
        private String odd;
        private Map<String, Object> additionalProperties;
    }

}

