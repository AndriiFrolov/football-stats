package org.football.stats.logic;

import org.apache.commons.lang3.tuple.Pair;
import org.football.stats.data.FixtureOdds;
import org.football.stats.dto.OddsResponse;

import java.util.*;

public class Header {
    private List<String> initial;

    Map<String, List<String>> allPossibleBetValues = new HashMap<>();
    Map<String, Integer> firstLineHeader = new HashMap<>();
    Map<String, Integer> firstLineHeaderEnd = new HashMap<>();
    Map<Pair<String, String>, Integer> secondLineHeader = new HashMap<>();

    public List<Object> getH1() {
        List<Object> rowTemplate = getRowTemplate();
        for (Map.Entry<String, Integer> entry : firstLineHeader.entrySet()) {
            rowTemplate.set(entry.getValue(), entry.getKey());
        }
        return rowTemplate;
    }

    public List<Object> getH2() {
        List<Object> rowTemplate = getRowTemplate();
        for (Map.Entry<Pair<String, String>, Integer> entry : secondLineHeader.entrySet()) {
            rowTemplate.set(entry.getValue(), entry.getKey().getValue());
        }

        return rowTemplate;
    }

    public Header(List<String> initial) {
        this.initial = initial;
        for (int i = 0; i < initial.size(); i++) {
            firstLineHeader.put(initial.get(i), i);
        }
    }

    public void add(List<FixtureOdds> oddsForFixtures) {
        for (FixtureOdds oddsForFixture : oddsForFixtures) {
            for (OddsResponse.Bet bet : oddsForFixture.getBets()) {

                String betName = bet.getName();
                List<String> betValues = allPossibleBetValues.computeIfAbsent(betName, k -> new ArrayList<>());

                for (OddsResponse.Value value : bet.getValues()) {
                    String betValue = value.getValue();
                    if (!betValues.contains(betValue)) {
                        betValues.add(betValue);
                    }
                }
            }
        }

        int startIndexH2 = this.initial.size();
        for (Map.Entry<String, List<String>> entry : allPossibleBetValues.entrySet()) {
            firstLineHeader.put(entry.getKey(), startIndexH2);
            for (String value : entry.getValue()) {
                secondLineHeader.put(Pair.of(entry.getKey(), value), startIndexH2++);
            }
            firstLineHeaderEnd.put(entry.getKey(), startIndexH2);
        }
    }

    public int getIndexH1(String name) {
        return firstLineHeader.get(name);
    }

    public int getIndexH2(String betName, String betValue) {
        return secondLineHeader.get(Pair.of(betName, betValue));
    }

    public List<Object> getRowTemplate() {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < getEndOfHeader() + 1; i++) {
            result.add("");
        }
        return result;
    }

    private int getEndOfHeader() {
        int maxInteger = Integer.MIN_VALUE;

        for (int value : secondLineHeader.values()) {
            if (value > maxInteger) {
                maxInteger = value;
            }
        }
        return maxInteger;
    }
}
