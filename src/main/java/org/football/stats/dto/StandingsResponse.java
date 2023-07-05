package org.football.stats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingsResponse implements IResponse {
    private String get;
    private StandingsParameters parameters;
    private List<String> errors;
    private int results;
    private Paging paging;
    private List<StandingsData> response;

    @Override
    public Paging getPaging() {
        return paging;
    }

    @Data
    public static class StandingsParameters {
        private String league;
        private String season;
    }

    @Data
    public static class StandingsData {
        private League league;

        @Data
        public static class League {
            private int id;
            private String name;
            private String country;
            private String logo;
            private String flag;
            private int season;
            private List<List<StandingsEntry>> standings;
        }
    }

    @Data
    public static class StandingsEntry {
        private int rank;
        private Team team;
        private int points;
        private int goalsDiff;
        private String group;
        private String form;
        private String status;
        private String description;
        private Statistics all;
        private Statistics home;
        private Statistics away;
        private String update;
    }

    @Data
    public static class Team {
        private int id;
        private String name;
        private String logo;
    }

    @Data
    public static class Statistics {
        private int played;
        private int win;
        private int draw;
        private int lose;
        private Goals goals;
    }

    @Data
    public static class Goals {
        @JsonProperty("for")
        private int forGoals;
        private int against;
    }
}


