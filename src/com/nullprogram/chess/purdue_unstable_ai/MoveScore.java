package com.nullprogram.chess.purdue_unstable_ai;

import com.nullprogram.chess.Move;


public class MoveScore {
    private double score;
    private Move move;

    public MoveScore(double score, Move move) {
        this.score = score;
        this.move = move;
    }

    public double getScore() {
        return score;
    }

    public Move getMove() {
        return move;
    }

    public MoveScore getReversedMoveScore() {
        return new MoveScore(-score, move);
    }
}
