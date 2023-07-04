package org.football.stats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FixturesResponse implements IResponse{
    private String get;
    private FixturesParameters parameters;
    private List<String> errors;
    private int results;
    private Paging paging;
    private List<FixtureData> response;

    @Data
    public static class FixturesParameters {
        private String league;
        private String season;
        private String date;
    }

    @Data
    public static class FixtureData {
        private Fixture fixture;
        private League league;
        private Teams teams;
        private Goals goals;
        private Score score;
    }

    @Data
    public static class Fixture {
        private int id;
        private String referee;
        private String timezone;
        private String date;
        private long timestamp;
        private Periods periods;
        private Venue venue;
        private Status status;
    }

    @Data
    public static class Periods {
        private String first;
        private String second;
    }

    @Data
    public static class Venue {
        private int id;
        private String name;
        private String city;
    }

    @Data
    public static class Status {
        private String longStatus;
        private String shortStatus;
        private Integer elapsed;
    }

    @Data
    public static class League {
        private int id;
        private String name;
        private String country;
        private String logo;
        private String flag;
        private int season;
        private String round;
    }

    @Data
    public static class Teams {
        private Team home;
        private Team away;
    }

    @Data
    public static class Team {
        private int id;
        private String name;
        private String logo;
        private String winner;
    }

    @Data
    public static class Goals {
        private Integer home;
        private Integer away;
    }

    @Data
    public static class Score {
        private HalfTime halftime;
        private FullTime fulltime;
        private ExtraTime extratime;
        private Penalty penalty;
    }

    @Data
    public static class HalfTime {
        private Integer home;
        private Integer away;
    }

    @Data
    public static class FullTime {
        private Integer home;
        private Integer away;
    }

    @Data
    public static class ExtraTime {
        private Integer home;
        private Integer away;
    }

    @Data
    public static class Penalty {
        private Integer home;
        private Integer away;
    }
}

