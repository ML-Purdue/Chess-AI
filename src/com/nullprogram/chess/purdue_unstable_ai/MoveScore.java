package com.nullprogram.chess.purdue_unstable_ai;

import com.nullprogram.chess.Move;


public class MoveScore {
    private double score;
    private Move move;
    private boolean fromTranspositionTable;

    public MoveScore(double score, Move move) {
        this.score = score;
        this.move = move;
        this.fromTranspositionTable = false;
    }

    public void setFromTranspositionTable(boolean fromTranspositionTable) {
        this.fromTranspositionTable = fromTranspositionTable;
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

    public boolean isFromTranspositionTable() {
        return fromTranspositionTable;
    }
}
