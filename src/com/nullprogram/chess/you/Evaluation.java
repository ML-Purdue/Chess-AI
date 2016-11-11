package com.nullprogram.chess.you;

import com.nullprogram.chess.Board;
import com.nullprogram.chess.Piece;
import com.nullprogram.chess.Position;
import com.nullprogram.chess.pieces.*;

import java.util.HashMap;

public class Evaluation {
    private static HashMap<Class, Integer> values;

    /**
     * Given a state of the board, evaluate the board with respect to the given side.
     */
    public static double evaluateBoard(Board board, Piece.Side side) {
        if (values == null)
            values = setUpValues();

        int myPoints = 0;
        int enemyPoints = 0;

        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                Piece p = board.getPiece(new Position(i, j));
                if (p != null) {
                    if (p.getSide().equals(side)) {
                        myPoints += getPieceValue(p);
                    } else {
                        enemyPoints += getPieceValue(p);
                    }
                }
            }
        }
        int runningPoints = myPoints - enemyPoints;

        if (runningPoints < 0 && (board.stalemate() || board.threeFold())) {
            runningPoints += 1;
        } else if (runningPoints > 0 && (board.stalemate() || board.threeFold())) {
            runningPoints -= 2;
        }

        if (board.checkmate(side)) {
            runningPoints -= 1000;
        }
        return runningPoints;
    }

    private static int getPieceValue(Piece p) {
        return values.get(p.getClass());
    }

    private static HashMap<Class, Integer> setUpValues() {
        HashMap<Class, Integer> values = new HashMap<Class, Integer>();
        values.put(Archbishop.class, 4);
        values.put(Bishop.class, 3);
        values.put(Chancellor.class, 4);
        values.put(King.class, 1000);
        values.put(Knight.class, 3);
        values.put(Pawn.class, 1);
        values.put(Queen.class, 9);
        values.put(Rook.class, 5);
        return values;
    }
}
