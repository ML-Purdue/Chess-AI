package com.nullprogram.chess.purdue_unstable_ai;

/**
 * Created by matthewpage on 11/12/16.
 */
import com.nullprogram.chess.*;

public class Transposition {
    Board board;
    int plysIntoFutueEvaluated;
    MoveScore moveScore;

    public Transposition(Board board, int plysIntoFutueEvaluated, MoveScore moveScore) {
        this.board = board;
        this.plysIntoFutueEvaluated = plysIntoFutueEvaluated;
        this.moveScore = moveScore;
    }

    public MoveScore getMoveScore() {
        return moveScore;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Move move : board.getMoves().getLast5Moves()) {
            hash += move.getOrigin().getX()*8 + move.getOrigin().getY();
            hash *= 67;
            hash += move.getDest().getX()*8 + move.getDest().getY();
            hash *= 67;
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        Transposition transposition = (Transposition) o;
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                Piece p0 = board.getPiece(new Position(i,j));
                Piece p1 = transposition.board.getPiece(new Position(i,j));
                if (p0 == null || p1 == null)
                    if (p0 != null || p1 != null)
                        return false;
                    else
                        continue;
                if (!p0.equals(p1))
                    return false;
            }
        }
        if (this.plysIntoFutueEvaluated < transposition.plysIntoFutueEvaluated) {
            return false;
        }
        if (this.board.getMoves().size() % 2 != transposition.board.getMoves().size() % 2)
            return false;

        return true;
    }

    @Override
    public Object clone() {
        return new Transposition(board.copy(), plysIntoFutueEvaluated, moveScore);
    }
}
