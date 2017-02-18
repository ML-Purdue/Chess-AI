package com.nullprogram.chess.purdue_unstable_ai;

import com.nullprogram.chess.*;

import java.util.ArrayList;
import java.util.HashMap;

import static com.nullprogram.chess.Piece.Side.BLACK;
import static com.nullprogram.chess.Piece.Side.WHITE;

public class AlphaBetaPruningAI implements Player {
    private Game game;
    private HashMap<Transposition, Transposition> transpositionTable;
    private int[] counts;

    private static final int threshold = 1500000;

    public AlphaBetaPruningAI(Game game) {
        this.game = game;
        transpositionTable = new HashMap<>(Integer.MAX_VALUE / 100);
        counts = new int[Integer.MAX_VALUE / 100];
    }

    @Override
    public Move takeTurn(Board board, Piece.Side side) {
        long timeStart = System.currentTimeMillis();
        transpositionTable = new HashMap<>(Integer.MAX_VALUE / 100);
        counts = new int[Integer.MAX_VALUE / 100];
        MoveScore move = predictBestMove(0, 4, 1, board, side, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        System.out.println("size: " + transpositionTable.size());
        int max = 0;
        for (int i : counts)
            max = Math.max(i, max);
        System.out.println("max: " + max);
        System.out.println(System.currentTimeMillis() - timeStart);
        System.out.println("Score: " + move.getScore()*-1);
        return move.getMove();
    }

    public MoveScore predictBestMove(int ply, int finalPly, int numMoves, Board board, Piece.Side side, double beta, double alpha) {
        Transposition lookup = new Transposition(board, numMoves, new MoveScore(0, null));
        if (transpositionTable.containsKey(lookup)) {
//            System.out.println("Contains key");
            MoveScore moveScore = transpositionTable.get(lookup).getMoveScore();
            if (side == Piece.Side.BLACK) {
                moveScore = moveScore.getReversedMoveScore();
            }
//            if (board.allMoves(side, false).getMoves().contains((moveScore.getMove()))) {
//                MoveScore actual_score = bestMove(ply, finalPly, board, side, alpha, beta);
//                if (actual_score.getMove().equals(moveScore.getMove())) {
//                    System.out.println("Transposition table differed");
//                } else {
//                    System.out.println("Transposition tablle was correct");
//                }
//                return moveScore;
//            } else {
//                System.err.println("Hey, we are returning an invalid move!!!");
//            }
            return moveScore.getReversedMoveScore();
        }
        return bestMove(ply, finalPly, numMoves, board, side, alpha, beta);
    }

    private MoveScore bestMove(int ply, int finalPly, int numMoves, Board board, Piece.Side side, double alpha, double beta) {
        if (ply >= finalPly && numMoves >= threshold) {
            MoveScore r = new MoveScore(Evaluation.evaluateBoard(board, side), null);
            if (side == Piece.Side.WHITE) {
                Transposition transposition = new Transposition(board.copy(), numMoves, r);
                transpositionTable.put(transposition, transposition);
            }
            else {
                Transposition transposition = new Transposition(board.copy(), numMoves, r.getReversedMoveScore());
                transpositionTable.put(transposition, transposition);
            }
            //int hash = transposition.hashCode() % (Integer.MAX_VALUE / 100);
//            if (hash < 0)
//                hash *= -1;
//            counts[hash]++;
            return r.getReversedMoveScore();
        } else {
            if (ply > finalPly && numMoves < threshold)
                System.out.println(ply+" "+numMoves);
            MoveScore bestMove = new MoveScore(Double.NEGATIVE_INFINITY, null);
            MoveList moves = board.allMoves(side, true);
            int size = moves.size() * numMoves;
            for (Move move : moves) {
                board.move(move);
                Piece.Side opponent = (side == BLACK ? WHITE : BLACK);
                MoveScore newMoveScore = predictBestMove(ply + 1, finalPly, size, board, opponent, -alpha, -beta);

                if (side == Piece.Side.WHITE) {
                    Transposition transposition = new Transposition(board.copy(), numMoves, newMoveScore);
                    transpositionTable.put(transposition, transposition);
                }
                else {
                    Transposition transposition = new Transposition(board.copy(), numMoves, newMoveScore.getReversedMoveScore());
                    transpositionTable.put(transposition, transposition);
                }

                board.undo();
                alpha = Math.max(alpha, newMoveScore.getScore());
                if (beta <= alpha) {
                    return new MoveScore(-beta, null);
                }
                if (newMoveScore.getScore() > bestMove.getScore()) {
                    bestMove = new MoveScore(newMoveScore.getScore(), move);
                }
            }
            if (bestMove.getMove() == null) // stalemate
                return new MoveScore(0, null);
            else
                return bestMove.getReversedMoveScore();
        }
    }
}
