package org.football.stats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InjuriesResponse implements IResponse{
    private String get;
    private Parameters parameters;
    private List<Object> errors;
    private int results;
    private Paging paging;
    private List<ResponseItem> response;

    @Data
    public static class Parameters {
        private String league;
        private String season;
    }

    @Data
    public static class ResponseItem {
        private Player player;
        private Team team;
        private Fixture fixture;
        private League league;
    }

    @Data
    public static class Player {
        private int id;
        private String name;
        private String photo;
        private String type;
        private String reason;
    }

    @Data
    public static class Team {
        private int id;
        private String name;
        private String logo;
    }

    @Data
    public static class Fixture {
        private int id;
        private String timezone;
        private String date;
        private int timestamp;
    }

    @Data
    public static class League {
        private int id;
        private int season;
        private String name;
        private String country;
        private String logo;
        private String flag;
    }

}

