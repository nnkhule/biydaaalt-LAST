package com.mycompany.app;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;


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
                double aScore = getErrorRate(a);
                double bScore = getErrorRate(b);
                
                if (aScore > bScore) {
                    return -1;
                } else if (aScore < bScore) {
                    return 1;
                } else {
                    return 0;
                }
            }
            
            private double getErrorRate(Flashcard card) {
                String question = card.getQuestion();
                int correct = correctCounts.getOrDefault(question, 0);
                int incorrect = incorrectCounts.getOrDefault(question, 0);
                int total = correct + incorrect;
                
                if (total == 0) return 0;
                return (double) incorrect / total;
            }
        });
    }
}