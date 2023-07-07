package org.football.stats.logic.odds;

import org.apache.commons.lang3.tuple.Pair;
import org.football.stats.data.FixtureOdds;
import org.football.stats.dto.OddsResponse;

import java.util.*;

public class Header {
    Map<String, List<String>> allPossibleBetValues = new HashMap<>();
    Map<String, Integer> firstLineHeader = new HashMap<>();
    Map<String, Integer> firstLineHeaderEnd = new HashMap<>();
    Map<Pair<String, String>, Integer> secondLineHeader = new HashMap<>();

    public static final List<OddColumn> columns = Arrays.asList(
            new OddColumn("Date", "A", 1, false),
            new OddColumn("Home Team", "B", 1, false),
            new OddColumn("Away Team", "C", 1, false),
            new OddColumn("Match Winner", "D", 3, true),
            new OddColumn("First Half Winner", "H", 3, true),
            new OddColumn("Double Chance", "L", 3, true),
            new OddColumn("Both Teams Score", "P", 2, true),
            new OddColumn("Goals Over/Under", "S", 15, true),
            new OddColumn("Exact Score", "AI", 26, true)
    );

    public Header() {
        for (OddColumn column : columns) {
            firstLineHeader.put(column.getName(), column.getColumnIdx());
        }
    }

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

    public void add(List<FixtureOdds> oddsForFixtures) {
        //Сollect all possible bet values, e.g.
        //Match Winner: Home	Draw	Away
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

        OddColumn lastKnownBetColumn = columns.stream()
                .max(Comparator.comparingInt(OddColumn::getColumnIdx)).get();

        //Firstly start with fixed columns (Match winner etc)
        columns.stream().filter(OddColumn::isOdd).forEach(oddCol -> {
            List<String> betValues = allPossibleBetValues.get(oddCol.getName());
            int colIndex = oddCol.getColumnIdx();
            for (String betValue : betValues) {
                secondLineHeader.put(Pair.of(oddCol.getName(), betValue), colIndex++);
            }
            if (colIndex > oddCol.getColumnIdx() + oddCol.getSize()) {
                System.out.println("!!!! ERROR: Reserved " + oddCol.getSize() + " columns for '" + oddCol.getName() + "', but got more values: " + betValues);
            }
        });

        //Now process other bets that are not fixed to columns
        int startIndexH2 = lastKnownBetColumn.getColumnIdx() + lastKnownBetColumn.getSize();
        for (Map.Entry<String, List<String>> entry : allPossibleBetValues.entrySet()) {
            if (columns.stream().noneMatch(column -> column.getName().equals(entry.getKey()))) {
                firstLineHeader.put(entry.getKey(), startIndexH2);
                //if column was not already processed
                for (String value : entry.getValue()) {
                    secondLineHeader.put(Pair.of(entry.getKey(), value), startIndexH2++);
                }
                firstLineHeaderEnd.put(entry.getKey(), startIndexH2);
            }
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
