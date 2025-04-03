package com.mycompany.app;

import java.util.ArrayList;
import java.util.List;

public class RecentMistakesFirstSorter implements CardOrganizer {
    private static final List<Flashcard> recentMistakes = new ArrayList<>();

    public static void recordIncorrect(Flashcard card) {
        if (!recentMistakes.contains(card)) {
            recentMistakes.add(card);
        }
    }

    public static void clearMistakes() {
        recentMistakes.clear();
    }

    @Override
    public void organize(List<Flashcard> cards) {
        List<Flashcard> incorrectFirst = new ArrayList<>(recentMistakes);
        for (Flashcard card : cards) {
            if (!recentMistakes.contains(card)) {
                incorrectFirst.add(card);
            }
        }
        cards.clear();
        cards.addAll(incorrectFirst);
    }
}