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
        Move move = predictBestMove(0, 5, board, side, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).getMove();
        System.out.println("size: " + transpositionTable.size());
        int max = 0;
        for (int i : counts)
            max = Math.max(i, max);
        System.out.println("max: " + max);
        System.out.println(System.currentTimeMillis() - timeStart);
        return move;
    }

    public MoveScore predictBestMove(int ply, int finalPly, Board board, Piece.Side side, double beta, double alpha) {
        Transposition lookup = new Transposition(board, finalPly - ply, new MoveScore(0, null));
        if (transpositionTable.containsKey(lookup)) {
            MoveScore moveScore = transpositionTable.get(lookup).getMoveScore();
            if (board.allMoves(side, false).getMoves().contains((moveScore.getMove()))) {
                return moveScore;
            } else {
                System.err.println("Hey, we are returning an invalid move!!!");
            }
        }
        if (ply == finalPly) {
            MoveScore r = new MoveScore(Evaluation.evaluateBoard(board, side), null).getReversedMoveScore();
            Transposition transposition = new Transposition(board.copy(), 0, r);
            transpositionTable.put(transposition, transposition);
            int hash = transposition.hashCode() % (Integer.MAX_VALUE / 100);
            if (hash < 0)
                hash *= -1;
            counts[hash]++;
            return r;
        } else {
            MoveScore bestMove = new MoveScore(Double.NEGATIVE_INFINITY, null);
            for (Move move : board.allMoves(side, false)) {
                board.move(move);
                Piece.Side opponent = (side == BLACK ? WHITE : BLACK);
                MoveScore newMoveScore = predictBestMove(ply + 1, finalPly, board, opponent, -alpha, -beta);

                Transposition transposition = new Transposition(board.copy(), finalPly - ply, newMoveScore);
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
