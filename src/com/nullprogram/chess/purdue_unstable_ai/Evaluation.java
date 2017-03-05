package com.nullprogram.chess.purdue_unstable_ai;

import com.nullprogram.chess.Board;
import com.nullprogram.chess.Piece;
import com.nullprogram.chess.Position;
import com.nullprogram.chess.pieces.*;

import java.util.HashMap;

public class Evaluation {
    private HashMap<Class, Double> values;

    private double isolatedPawns;
    private final int CHECKMATEVAL = 1000000 ;
    private double staleMateWinning;
    private double staleMateLosing;
    private double manCoeff;
    private double centerCoeff;
    private double pawnCoeff;
    private double power;
    private double[] param;

    public Evaluation(double isolatedPawns, double staleMateL, double staleMateW, double manCoeff, double centerCoeff,
                      double power, double pawnCoeff, double bishVal, double kingVal,
                      double knightVal, double pawnVal, double queenVal, double rookVal) {

        values = setUpValues(bishVal, kingVal, knightVal, pawnVal, queenVal, rookVal);
        this.isolatedPawns = isolatedPawns;
        staleMateWinning = staleMateW;
        staleMateLosing = staleMateL;
        this.centerCoeff = centerCoeff;
        this.manCoeff = manCoeff;
        this.power = power;
        this.pawnCoeff = pawnCoeff;
        this.param = new double[]{isolatedPawns, staleMateL, staleMateW, manCoeff, centerCoeff,
        power, pawnCoeff, bishVal, kingVal,
        knightVal, pawnVal, queenVal, rookVal};
    }

    public double[] getParam() {
        return param;
    }

    /**
     * Given a state of the board, evaluate the board with respect to the given side.
     */
    double evaluateBoard(Board board, Piece.Side side) {
        double myPoints = 0;
        double enemyPoints = 0;

        // TODO: Make different eval function if myPoints and enemyPoints are both below some threshold
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
        double runningPoints = (myPoints - enemyPoints) * 1;



        // End of game
        if (runningPoints < 0 && (board.stalemate() || board.threeFold())) {
            runningPoints += staleMateWinning;
        } else if (runningPoints > 0 && (board.stalemate() || board.threeFold())) {
            runningPoints -= staleMateLosing;
        }
        if (board.checkmate(side)) {
            runningPoints -= CHECKMATEVAL;
        } else if (board.checkmate()) {
            runningPoints += CHECKMATEVAL;
        }

        // Maneuverability
        int man = board.allMoves(side, true).size();
        Piece.Side opp = (side == Piece.Side.BLACK)? Piece.Side.WHITE:Piece.Side.BLACK;
        int oppMan = board.allMoves(opp, true).size();
        runningPoints += ((man - oppMan)*manCoeff);

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
        pawnPoints *= pawnCoeff;
        runningPoints += pawnPoints;

        // Isolated pawns
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                Piece p = board.getPiece(new Position(i, j));
                if (p != null && p.getClass().equals(Pawn.class)) {
                    if (p.getSide() == side) {
                        if (bottomRight(p, board) == 0 && bottomLeft(p, board) == 0 && topLeft(p, board) == 0 &&
                                topRight(p, board) == 0) {
                            runningPoints -= isolatedPawns;
                        }
                    }
                    else {
                        if (bottomRight(p, board) == 0 && bottomLeft(p, board) == 0 && topLeft(p, board) == 0 &&
                                topRight(p, board) == 0) {
                            runningPoints += isolatedPawns;
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
                    runningPoints += centerCoeff/ Math.pow(getPieceValue(p), power);
                }else if(p!=null && !p.getSide().equals(side)) {
                    runningPoints -= centerCoeff / Math.pow(getPieceValue(p), power);
                }
            }
        }

        return runningPoints;
    }


    private double bottomRight(Piece p, Board board) {
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

    private double bottomLeft(Piece p, Board board) {
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

    private double topRight(Piece p, Board board){
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


    private double topLeft(Piece p, Board board){
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

    private double getPieceValue(Piece p) {
        return values.get(p.getClass());
    }

    private  HashMap<Class, Double> setUpValues(double bishVal, double kingVal, double knightVal, double pawnVal,
                                                 double queenVal, double rookVal) {
        HashMap<Class, Double> values = new HashMap<>();
        values.put(Bishop.class, bishVal);
        values.put(King.class, kingVal);
        values.put(Knight.class, knightVal);
        values.put(Pawn.class, pawnVal);
        values.put(Queen.class, queenVal);
        values.put(Rook.class, rookVal);
        return values;
    }
}
