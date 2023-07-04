package org.football.stats.logic;

import org.football.stats.dto.InjuriesResponse;
import org.football.stats.google.Sheets;

import java.util.*;
import java.util.stream.Collectors;

public class InjuriesTable {

    public static void write(List<InjuriesResponse> injuriesResponses, String sheet) {
        List<List<Object>> res = new ArrayList<>();
        List<Object> header = Arrays.asList("Team", "Player", "Type", "Reason");
        res.add(header);

        if (injuriesResponses.isEmpty() || injuriesResponses.get(0).getResponse().isEmpty()) {
            throw new RuntimeException("Standings response is empty!");
        }
        List<InjuriesResponse.ResponseItem> injuries = injuriesResponses.stream().flatMap(injuriesResponse -> injuriesResponse.getResponse().stream()).collect(Collectors.toList());
        Map<String, List<InjuriesResponse.ResponseItem>> groupedMap = injuries.stream()
                .collect(Collectors.groupingBy(item -> item.getTeam().getName()));

        for (Map.Entry<String, List<InjuriesResponse.ResponseItem>> entry : groupedMap.entrySet()) {
            Set<String> alreadyProcessedPlayers = new HashSet<>(); //to do not show same players twice
            for (InjuriesResponse.ResponseItem injury : entry.getValue()) {
                if (!alreadyProcessedPlayers.contains(injury.getPlayer().getName())) {
                    List<Object> row = new ArrayList<>();
                    row.add(injury.getTeam().getName());
                    row.add(injury.getPlayer().getName());
                    row.add(injury.getPlayer().getType());
                    row.add(injury.getPlayer().getReason());
                    res.add(row);
                    alreadyProcessedPlayers.add(injury.getPlayer().getName());
                }
            }
        }

        Sheets.write(res, sheet);
    }
}
