package com.mycompany.app;

import org.junit.jupiter.api.Test;
import main.java.com.mycompany.app;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FlashcardAppTest {

    @Test
    public void testLoadCards() throws IOException {
        // Create a temporary file with mock flashcard data
        File tempFile = File.createTempFile("flashcards", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("Question1;Answer1\n");
            writer.write("Question2;Answer2\n");
            writer.write("# This is a comment\n");
            writer.write("Question3|Answer3\n");
        }

        // Load cards using the method
        List<Flashcard> cards = FlashcardApp.loadCards(tempFile.getAbsolutePath());

        // Verify the loaded cards
        assertEquals(3, cards.size());
        assertEquals("Question1", cards.get(0).getQuestion());
        assertEquals("Answer1", cards.get(0).getAnswer());
        assertEquals("Question3", cards.get(2).getQuestion());
        assertEquals("Answer3", cards.get(2).getAnswer());

        // Clean up the temporary file
        tempFile.delete();
    }

    @Test
    public void testGetCardOrganizer() {
        // Test random organizer
        CardOrganizer randomOrganizer = FlashcardApp.getCardOrganizer("random");
        assertTrue(randomOrganizer instanceof RandomCardOrganizer);

        // Test worst-first organizer
        CardOrganizer worstFirstOrganizer = FlashcardApp.getCardOrganizer("worst-first");
        assertTrue(worstFirstOrganizer instanceof WorstFirstSorter);

        // Test recent-mistakes-first organizer
        CardOrganizer recentMistakesOrganizer = FlashcardApp.getCardOrganizer("recent-mistakes-first");
        assertTrue(recentMistakesOrganizer instanceof RecentMistakesFirstSorter);

        // Test default case
        CardOrganizer defaultOrganizer = FlashcardApp.getCardOrganizer("unknown");
        assertTrue(defaultOrganizer instanceof RandomCardOrganizer);
    }

    @Test
    public void testAskQuestions() {
        // Mock flashcards
        List<Flashcard> cards = new ArrayList<>();
        cards.add(new Flashcard("Question1", "Answer1"));
        cards.add(new Flashcard("Question2", "Answer2"));

        // Mock user input
        InputStream in = new ByteArrayInputStream("Answer1\nWrongAnswer\nAnswer2\n".getBytes());
        System.setIn(in);

        // Capture output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Call the method
        FlashcardApp.askQuestions(cards, 1, false, "random");

        // Verify the output
        String output = out.toString();
        assertTrue(output.contains("Question: Question1"));
        assertTrue(output.contains("Correct!"));
        assertTrue(output.contains("Incorrect. Try again."));
        assertTrue(output.contains("Question: Question2"));
    }
}