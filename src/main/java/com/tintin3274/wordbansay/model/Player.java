package com.tintin3274.wordbansay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private String uuid;
    private int id;
    private String name;
    private int score;
    private String word;
    private int killScore;
    private int guessScore;
    private int deadCount;

    public int getScore() {
        return killScore+guessScore;
    }

    public String getScoreDetail() {
        return "ID: " + id + " | Name: " + name + " | KillScore: " + killScore + " | GuessScore: " + guessScore + " | DeadCount: " + deadCount + " | SumScore: " + getScore();
    }
}
