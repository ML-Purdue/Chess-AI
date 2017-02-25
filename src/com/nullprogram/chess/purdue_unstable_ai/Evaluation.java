package com.nullprogram.chess.purdue_unstable_ai;

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
        double runningPoints = myPoints - enemyPoints;



        // End of game
        if (runningPoints < 0 && (board.stalemate() || board.threeFold())) {
            runningPoints += 1;
        } else if (runningPoints > 0 && (board.stalemate() || board.threeFold())) {
            runningPoints -= 2;
        }
        if (board.checkmate(side)) {
            runningPoints -= 1000000;
        } else if (board.checkmate()) {
            runningPoints += 1000000;
        }

        // Maneuverability
        int man = board.allMoves(side, true).size();
        Piece.Side opp = (side == Piece.Side.BLACK)? Piece.Side.WHITE:Piece.Side.BLACK;
        int oppMan = board.allMoves(opp, true).size();
        runningPoints += ((man - oppMan)*0.05);

        // Pawn coverage
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
        pawnPoints *= .05;
        runningPoints += pawnPoints;

        // Isolated pawns
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                Piece p = board.getPiece(new Position(i, j));
                if (p != null && p.getClass().equals(Pawn.class)) {
                    if (p.getSide() == side) {
                        if (bottomRight(p, board) == 0 && bottomLeft(p, board) == 0 && topLeft(p, board) == 0 &&
                                topRight(p, board) == 0) {
                            runningPoints -= 0.5;
                        }
                    }
                    else {
                        if (bottomRight(p, board) == 0 && bottomLeft(p, board) == 0 && topLeft(p, board) == 0 &&
                                topRight(p, board) == 0) {
                            runningPoints += 0.5;
                        }
                    }
                }
            }
        }

        // Center control
        // Note: Value more disposable pieces in the center, as apposed to more valuable ones like a Queen
        for(int i = 3; i <= 4; i++){
            for(int j = 3; j <= 4; j++){
                Piece p = board.getPiece(new Position(i, j));
                if (p != null && p.getSide().equals(side)) {
                    runningPoints += .25 / Math.sqrt(getPieceValue(p));
                }else if(p!=null && !p.getSide().equals(side)) {
                    runningPoints -= .25 / getPieceValue(p);
                }
            }
        }

        return runningPoints;
    }


    private static int bottomRight(Piece p, Board board) {
        int x = p.getPosition().getX();
        int y = p.getPosition().getY();
        if (x + 1 < board.getWidth() && y - 1 >= 0) {
            Piece next = board.getPiece(new Position(x + 1, y - 1));
            if (next != null && next.getSide().equals(p.getSide()) && !next.getClass().equals(King.class)) {
                return getPieceValue(next);
            }
        }
        return 0;
    }

    private static int bottomLeft(Piece p, Board board) {
        int x = p.getPosition().getX();
        int y = p.getPosition().getY();
        if (x - 1 >= 0 && y - 1 >= 0) {
            Piece next = board.getPiece(new Position(x - 1, y - 1));
            if (next != null && next.getSide().equals(p.getSide()) && !next.getClass().equals(King.class)) {
                return getPieceValue(next);
            }
        }
        return 0;
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
