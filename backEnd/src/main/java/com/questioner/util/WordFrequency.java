package com.questioner.util;

public class WordFrequency {
    private int frequency;
    private String word;
    public WordFrequency(String word, int frequency) {
        this.frequency = frequency;
        this.word = word;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getWord() {
        return word;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
