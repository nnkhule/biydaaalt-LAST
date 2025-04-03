package com.mycompany.app;

import java.util.Collections;
import java.util.List;

public class RandomCardOrganizer implements CardOrganizer {
    @Override
    public void organize(List<Flashcard> cards) {
        Collections.shuffle(cards);
    }
}
