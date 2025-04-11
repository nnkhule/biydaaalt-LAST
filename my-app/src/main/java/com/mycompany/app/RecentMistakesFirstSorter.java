package com.mycompany.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class RecentMistakesFirstSorter implements CardOrganizer {
    private static final String STATS_FILE = "recent.txt";
    private static final Deque<Flashcard> recentMistakes = new ArrayDeque<>();

    public static void recordIncorrect(Flashcard card) {
        if (recentMistakes.contains(card)) {
            recentMistakes.remove(card);
        }
        recentMistakes.addFirst(card);
        saveRecentMistakes();
    }

    public static void loadRecentMistakes(List<Flashcard> allCards) {
        recentMistakes.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(STATS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Flashcard card : allCards) {
                    if (card.getQuestion().equals(line.trim())) {
                        recentMistakes.add(card);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // эхний удаад файл байхгүй байж болно — алгасана
        }
    }

    private static void saveRecentMistakes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATS_FILE))) {
            for (Flashcard card : recentMistakes) {
                writer.write(card.getQuestion());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save recent mistakes: " + e.getMessage());
        }
    }

    @Override
    public void organize(List<Flashcard> cards) {
        List<Flashcard> orderedCards = new ArrayList<>(recentMistakes);
        for (Flashcard card : cards) {
            if (!recentMistakes.contains(card)) {
                orderedCards.add(card);
            }
        }
        cards.clear();
        cards.addAll(orderedCards);
    }
}
