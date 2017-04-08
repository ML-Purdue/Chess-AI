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
    private double connectedRooks;

    private double pawnshield;
    private double promotion;

    public Evaluation(double isolatedPawns, double staleMateL, double staleMateW, double manCoeff, double centerCoeff,
                      double power, double pawnCoeff, double bishVal, double kingVal,
                      double knightVal, double pawnVal, double queenVal, double rookVal, double connectedRooks, double pawnshield,
                      double promotion) {

        values = setUpValues(bishVal, kingVal, knightVal, pawnVal, queenVal, rookVal);
        this.isolatedPawns = isolatedPawns;
        staleMateWinning = staleMateW;
        staleMateLosing = staleMateL;
        this.centerCoeff = centerCoeff;
        this.manCoeff = manCoeff;
        this.power = power;
        this.pawnCoeff = pawnCoeff;
        this.connectedRooks = connectedRooks;
        this.param = new double[]{isolatedPawns, staleMateL, staleMateW, manCoeff, centerCoeff,
        power, pawnCoeff, bishVal, kingVal,
        knightVal, pawnVal, queenVal, rookVal};

        this.pawnshield = pawnshield;
        this.promotion = promotion;
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
        double runningPoints = 0;
        // TODO: Make different eval function if myPoints and enemyPoints are both below some threshold

        Rook whiteRook = null;
        Rook blackRook = null;
        double pawnPoints = 0;
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                Piece p = board.getPiece(new Position(i, j));

                // Piece values
                if (p != null) {
                    if (p.getSide().equals(side)) {
                        myPoints += getPieceValue(p);
                    } else {
                        enemyPoints += getPieceValue(p);
                    }
                }

                // Pawn Shield

                if (p != null && p.getClass().equals(King.class)) {
                    if (p.moved()) {
                        int numPawns = 0;
                        int numPawnsTwo = 0;
                        if (p.getSide() == Piece.Side.WHITE && j == 0) {
                            for (int k = Math.max(i - 1, 0); k < Math.min(i + 2, 8); k++) {
                                Piece above = board.getPiece(new Position(k, j+1));
                                Piece twoAbove = board.getPiece(new Position(k, j+2));
                                if (above instanceof Pawn && above.getSide() == Piece.Side.WHITE) {
                                    numPawns++;
                                }
                                else if (twoAbove instanceof Pawn && twoAbove.getSide() == Piece.Side.WHITE){
                                    numPawnsTwo++;
                                }
                            }

                        }
                        else if (j == 7) {
                            for (int k = Math.max(i - 1, 0); k < Math.min(i + 2, 8); k++) {
                                Piece above = board.getPiece(new Position(k, j-1));
                                Piece twoAbove = board.getPiece(new Position(k, j-2));
                                if (above instanceof Pawn && above.getSide() == Piece.Side.BLACK) {
                                    numPawns++;
                                }
                                else if (twoAbove instanceof Pawn && twoAbove.getSide() == Piece.Side.BLACK){
                                    numPawnsTwo++;
                                }
                            }
                        }
                        if (i == 0 || i == 7) {
                            numPawns++;
                        }
                        double pawnShieldScore = (2*numPawns + numPawnsTwo)/6.0;
                        if (pawnShieldScore >= 4.9/6.0) {
                            pawnShieldScore = 2.0;
                        }

                        if (side == p.getSide()) {
                            myPoints += pawnshield * pawnShieldScore;
                        }
                        else {
                            enemyPoints += pawnshield * pawnShieldScore;
                        }
                    }
                }

                // Pawn analysis
                if (p != null && p.getClass().equals(Pawn.class)) {

                    // Pawn coverage
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

                    // Isolated Pawns
                    if (p.getSide().equals(side)) {
                        if (p.getSide() == Piece.Side.WHITE) {
                            pawnPoints += topLeft(p, board);
                            pawnPoints += topRight(p, board);
                        }
                        else {
                            pawnPoints += bottomLeft(p, board);
                            pawnPoints += bottomRight(p, board);
                        }
                    } else {
                        if (p.getSide() == Piece.Side.WHITE) {
                            pawnPoints -= topLeft(p, board);
                            pawnPoints -= topRight(p, board);
                        }
                        else {
                            pawnPoints -= bottomLeft(p, board);
                            pawnPoints -= bottomRight(p, board);
                        }
                    }

                    // Promotion
                    if (p.getSide() == Piece.Side.BLACK && j <= 3) {
                        pawnPoints += promotion * Math.pow(2, 3 - j);
                    }
                    else if (p.getSide() == Piece.Side.WHITE && j >= 4) {
                        pawnPoints += promotion * Math.pow(2, j - 4);
                    }
                }

                if (p != null && p.getClass().equals(Rook.class)) {
                    //ASSUMING (0,0) IS BOTTOM LEFT
                   if(p.getSide() == Piece.Side.WHITE) {
                       if(whiteRook == null) {
                           whiteRook = (Rook)p;
                       } else {
                           boolean isConnected = false;
                           if(p.getPosition().getX() == whiteRook.getPosition().getX()) {
                               //same column
                               isConnected = true;
                               for (int k = whiteRook.getPosition().getY() + 1; k < p.getPosition().getY(); k++) {
                                   Piece somePiece = board.getPiece(new Position(p.getPosition().getX(), k));
                                   if (somePiece != null) {
                                       isConnected = false;
                                       break;
                                   }
                               }
                               if (isConnected) {
                                   runningPoints += connectedRooks;
                               }
                           }
                           else if(p.getPosition().getY() == whiteRook.getPosition().getY()) {
                                //same row
                               isConnected = true;
                               for (int k = whiteRook.getPosition().getX() + 1; k < p.getPosition().getX(); k++) {
                                   Piece somePiece = board.getPiece(new Position(k, whiteRook.getPosition().getY()));
                                   if (somePiece != null) {
                                       isConnected = false;
                                       break;
                                   }
                               }
                               if (isConnected) {
                                   //ADD SOME VALUE TO WHITE
                                   runningPoints += connectedRooks;
                               }
                           }
                       }
                   }
                    else {
                       if(blackRook == null) {
                           blackRook = (Rook)p;
                       }
                       else {
                           boolean isConnected = false;
                           if(p.getPosition().getX() == blackRook.getPosition().getX()) {
                               //same column
                               isConnected = true;
                               for (int k = blackRook.getPosition().getY() + 1; k < p.getPosition().getY(); k++) {
                                   Piece somePiece = board.getPiece(new Position(p.getPosition().getX(), k));
                                   if (somePiece != null) {
                                       isConnected = false;
                                       break;
                                   }
                               }
                               if (isConnected) {
                                   //ADD SOME VALUE TO BLACK
                                   runningPoints -= connectedRooks;
                               }
                           }
                           else if(p.getPosition().getY() == blackRook.getPosition().getY()) {
                               //same row
                               isConnected = true;
                               for (int k = blackRook.getPosition().getX() + 1; k < p.getPosition().getX(); k++) {
                                   Piece somePiece = board.getPiece(new Position(k, blackRook.getPosition().getY()));
                                   if (somePiece != null) {
                                       isConnected = false;
                                       break;
                                   }
                               }
                               if (isConnected) {
                                   //ADD SOME VALUE TO BLACK
                                   runningPoints -= connectedRooks;
                               }
                           }

                       }
                    }
                }
            }
        }
        runningPoints += myPoints - enemyPoints;
        pawnPoints *= pawnCoeff;
        runningPoints += pawnPoints;

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

        // Center control
        // Note: Value more disposable pieces in the center, as apposed to more valuable ones like a Queen
        for(int i = 3; i <= 4; i++){
            for(int j = 3; j <= 4; j++){
                Piece p = board.getPiece(new Position(i, j));
                if (p != null && p.getSide().equals(side)) {
                    runningPoints += centerCoeff/ Math.pow(getPieceValue(p), power);
                } else if(p!=null && !p.getSide().equals(side)) {
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
