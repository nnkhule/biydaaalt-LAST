package com.mycompany.app;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WorstFirstSorter implements CardOrganizer {
    private static Map<String, Integer> correctCounts = new HashMap<>();
    private static Map<String, Integer> incorrectCounts = new HashMap<>();

    public static void recordCorrect(Flashcard card) {
        String question = card.getQuestion();
        correctCounts.put(question, correctCounts.getOrDefault(question, 0) + 1);
    }

    public static void recordIncorrect(Flashcard card) {
        String question = card.getQuestion();
        incorrectCounts.put(question, incorrectCounts.getOrDefault(question, 0) + 1);
    }

    @Override
    public void organize(List<Flashcard> cards) {
        cards.sort(new Comparator<Flashcard>() {
            @Override
            public int compare(Flashcard a, Flashcard b) {
                // Жишээний дагуу буруу хариултын хувь хэмжээгээр эрэмбэлнэ
                return Double.compare(getErrorRate(b), getErrorRate(a));
            }

            private double getErrorRate(Flashcard card) {
                // Жишээний дагуу буруу хариултын хувь хэмжээг тооцоолно
                String question = card.getQuestion();
                int correct = correctCounts.getOrDefault(question, 0);
                int incorrect = incorrectCounts.getOrDefault(question, 0);
                int total = correct + incorrect;
                
                if (total == 0) return 0;
                return (double) incorrect / total;
            }
        });
    }

private static final String STATS_FILE = "flashcard-stats.txt";

public static void loadStats() {
    try (BufferedReader reader = new BufferedReader(new FileReader(STATS_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length == 3) {
                String question = parts[0];
                int correct = Integer.parseInt(parts[1]);
                int incorrect = Integer.parseInt(parts[2]);
                correctCounts.put(question, correct);
                incorrectCounts.put(question, incorrect);
            }
        }
    } catch (IOException e) {
        // анх удаа ажиллуулж байгаа байж болно
    }
}

public static void saveStats() {
    try (PrintWriter writer = new PrintWriter(new FileWriter(STATS_FILE))) {
        for (String question : correctCounts.keySet()) {
            int correct = correctCounts.getOrDefault(question, 0);
            int incorrect = incorrectCounts.getOrDefault(question, 0);
            writer.println(question + ";" + correct + ";" + incorrect);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}

