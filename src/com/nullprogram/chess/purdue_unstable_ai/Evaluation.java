package com.nullprogram.chess.purdue_unstable_ai;

import com.nullprogram.chess.Board;
import com.nullprogram.chess.Move;
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
        double runningPoints = myPoints - enemyPoints;
        runningPoints *= 10;

        runningPoints += 0.1 * board.allMoves(side, true).size();

        /*
        for (Move move : board.allMoves(side, true)) {
            int x = move.getDest().getX();
            int y = move.getDest().getY();
            if ((x == 3 || x == 4) && (y == 3 || y == 4))
                runningPoints += 0.25;
        }

        if (runningPoints < 0 && (board.stalemate() || board.threeFold())) {
            runningPoints += 1;
        } else if (runningPoints > 0 && (board.stalemate() || board.threeFold())) {
            runningPoints -= 2;
        }

        if (board.checkmate(side)) {
            runningPoints -= 1000;
        }
        if (board.checkmate(Piece.opposite(side))) {
            runningPoints += 500;
        }

        //if (board.check(side)) {
        //    runningPoints -= .1;
        //}
        /*
        if (board.check(Piece.opposite(side))) {
            runningPoints += 5;
        }
        */

        // Ideas
        // Distance of opponents pieces to king
        //  Distance of your pieces to their king
        // Center control
        // How far away king is from higher value pieces
        // Stacked rooks
        //    That other version


        /*
        // Staggered pawns
        double pawnPoints = 0;
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                Piece p = board.getPiece(new Position(i, j));
                if (p != null && p.getClass().equals(Pawn.class)) {
                    if (p.getSide().equals(side)) {
                        pawnPoints += topLeft(p, board);
                        pawnPoints += topRight(p, board);
                    } else {
                        pawnPoints -= topLeft(p, board);
                        pawnPoints -= topRight(p, board);
                    }
                }
            }
        }


        pawnPoints *= .005;
        runningPoints += pawnPoints;
        */


        return runningPoints;
    }

    private static int topRight(Piece p, Board board){
        int x = p.getPosition().getX();
        int y = p.getPosition().getY();
        if(x+1 < board.getWidth() && y+1 < board.getHeight()){
            Piece next = board.getPiece(new Position(x+1,y+1));
            if (next!=null && next.getSide().equals(p.getSide()) && !next.getClass().equals(King.class)){
                return getPieceValue(next);
            }
        }
        return 0;
    }


    private static int topLeft(Piece p, Board board){
        int x = p.getPosition().getX();
        int y = p.getPosition().getY();
        if(x-1 >= 0 && y+1 < board.getHeight()){
            Piece next = board.getPiece(new Position(x-1,y+1));
            if (next!=null && next.getSide().equals(p.getSide())&& !next.getClass().equals(King.class)){
                return getPieceValue(next);
            }
        }
        return 0;
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
