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
        System.out.println("Current moves:\n");
        System.out.println(board);
        Move move = predictBestMove(0, 4, board, side, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).getMove();
        System.out.println("size: " + transpositionTable.size());
        int max = 0;
        for (int i : counts)
            max = Math.max(i, max);
        System.out.println("max: " + max);
        System.out.println("time: " + (System.currentTimeMillis() - timeStart));
        return move;
    }

    public MoveScore predictBestMove(int ply, int finalPly, Board board, Piece.Side side, double beta, double alpha) {
        if (board.fiftyMoveRule() || board.stalemate() || board.threeFold()) {
            return new MoveScore(0, null);
        } else if (board.checkmate(side)) {
            return new MoveScore(Double.NEGATIVE_INFINITY, null);
        } else if (board.checkmate()) {
            return new MoveScore(Double.POSITIVE_INFINITY, null);
        }
        Transposition lookup = new Transposition(board, finalPly - ply, new MoveScore(0, null));
        if (transpositionTable.containsKey(lookup)) {
            MoveScore moveScore = transpositionTable.get(lookup).getMoveScore();
            if (side == Piece.Side.BLACK)
                moveScore = moveScore.getReversedMoveScore();
            if (board.allMoves(side, false).getMoves().contains((moveScore.getMove()))) {
                return moveScore;
            } else {
                System.err.println("Hey, we are returning an invalid move!!!");
            }
        }
        if (ply == finalPly) {
            double value = Evaluation.evaluateBoard(board, side);
            MoveScore rToReturn = new MoveScore(value, null).getReversedMoveScore();
            if (side == Piece.Side.BLACK)
                value *= -1;
            MoveScore r = new MoveScore(value, null).getReversedMoveScore();
            Transposition transposition = new Transposition(board.copy(), 0, r);
            transpositionTable.put(transposition, transposition);
            int hash = transposition.hashCode() % (Integer.MAX_VALUE / 100);
            if (hash < 0)
                hash *= -1;
            counts[hash]++;
            return rToReturn;
        } else {
            MoveScore bestMove = new MoveScore(Double.NEGATIVE_INFINITY, null);
            for (Move move : board.allMoves(side, true)) {
                board.move(move);
                Piece.Side opponent = (side == BLACK ? WHITE : BLACK);
                double score = predictBestMove(ply + 1, finalPly, board, opponent, -alpha, -beta).getScore();
                MoveScore newMoveScore = new MoveScore(score, move);
                if (side == Piece.Side.BLACK)
                    score *= -1;

                MoveScore newMoveScoreTrasposition = new MoveScore(score, move);
                Transposition transposition = new Transposition(board.copy(), finalPly - ply, newMoveScoreTrasposition);
                transpositionTable.put(transposition, transposition);
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
