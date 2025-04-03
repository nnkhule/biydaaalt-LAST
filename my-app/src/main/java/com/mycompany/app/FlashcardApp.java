package com.mycompany.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FlashcardApp {
    public static void main(String[] args) {
        if (args.length == 0 || Arrays.asList(args).contains("--help")) {
            printHelp();
            return;
        }

        String cardFile = args[0];
        String order = "random";
        int repetitionCount = 1;
        boolean invertCards = false;

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--order":
                    if (i + 1 < args.length) {
                        order = args[++i];
                    }
                    break;
                case "--repetitions":
                    if (i + 1 < args.length) {
                        repetitionCount = Integer.parseInt(args[++i]);
                    }
                    break;
                case "--invertCards":
                    invertCards = true;
                    break;
                default:
                    System.out.println("Unrecognized option: " + args[i]);
                    printHelp();
                    return;
            }
        }

        List<Flashcard> cards = loadCards(cardFile);
        CardOrganizer organizer = getCardOrganizer(order);
        organizer.organize(cards);

        askQuestions(cards, repetitionCount, invertCards, order);
    }

    private static void printHelp() {
        System.out.println("Usage: flashcard <card-file> [options]");
        System.out.println("Options:");
        System.out.println("  --help           Show help information");
        System.out.println("  --order <order>  Card organization type (random, worst-first, recent-mistakes-first)");
        System.out.println("  --repetitions <num>  Number of times to repeat each card");
        System.out.println("  --invertCards    Swap questions and answers");
    }

    private static List<Flashcard> loadCards(String fileName) {
        List<Flashcard> cards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines and comments (lines starting with #)
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }
                
                // Split the line by semicolon (;) or pipe (|) delimiter
                String[] parts = line.split(";|\\|");
                if (parts.length == 2) {
                    cards.add(new Flashcard(parts[0].trim(), parts[1].trim()));
                } else if (parts.length > 2) {
                    // Handle cases where answer contains the delimiter
                    String answer = line.substring(line.indexOf(parts[0]) + parts[0].length() + 1);
                    cards.add(new Flashcard(parts[0].trim(), answer.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
        
        if (cards.isEmpty()) {
            System.err.println("No valid flashcards found in the file.");
            System.exit(1);
        }
        
        return cards;
    }

    private static CardOrganizer getCardOrganizer(String order) {
        switch (order) {
            case "recent-mistakes-first":
                return new RecentMistakesFirstSorter();
            case "worst-first":
                return new WorstFirstSorter();
            default:
                return new RandomCardOrganizer();
        }
    }

    private static void askQuestions(List<Flashcard> cards, int repetitionCount, boolean invertCards, String order) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Integer> correctAnswers = new HashMap<>();
        Map<String, Integer> incorrectAnswers = new HashMap<>();
        List<Flashcard> incorrectCards = new ArrayList<>();

        for (int r = 0; r < repetitionCount; r++) {
            // Combine incorrect cards from the previous round with the rest of the cards
            List<Flashcard> roundCards = new ArrayList<>(incorrectCards);
            for (Flashcard card : cards) {
                if (!incorrectCards.contains(card)) {
                    roundCards.add(card);
                }
            }
            incorrectCards.clear(); // Reset for the next round

            for (Flashcard card : roundCards) {
                String question = invertCards ? card.getAnswer() : card.getQuestion();
                String answer = invertCards ? card.getQuestion() : card.getAnswer();

                System.out.println("Question: " + question);
                String userAnswer = scanner.nextLine();
                if (userAnswer.equalsIgnoreCase(answer)) {
                    System.out.println("Correct!");
                    correctAnswers.put(question, correctAnswers.getOrDefault(question, 0) + 1);
                    if (order.equals("worst-first")) {
                        WorstFirstSorter.recordCorrect(card);
                    }
                } else {
                    System.out.println("Incorrect. Correct answer: " + answer);
                    incorrectAnswers.put(question, incorrectAnswers.getOrDefault(question, 0) + 1);
                    incorrectCards.add(card); // Add to incorrect cards for the next round
                    if (order.equals("recent-mistakes-first")) {
                        RecentMistakesFirstSorter.recordIncorrect(card);
                    }
                    if (order.equals("worst-first")) {
                        WorstFirstSorter.recordIncorrect(card);
                    }
                }
            }
        }
        checkAchievements(correctAnswers, incorrectAnswers);
    }

    private static void checkAchievements(Map<String, Integer> correct, Map<String, Integer> incorrect) {
        if (incorrect.isEmpty()) {
            System.out.println("Achievement: CORRECT - All cards answered correctly!");
        }
        if (correct.values().stream().anyMatch(v -> v >= 3)) {
            System.out.println("Achievement: CONFIDENT - Answered a card correctly at least 3 times!");
        }
        if (incorrect.values().stream().anyMatch(v -> v >= 5)) {
            System.out.println("Achievement: REPEAT - Answered a card incorrectly 5 or more times!");
        }
    }
}
