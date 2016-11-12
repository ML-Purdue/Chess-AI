package com.nullprogram.chess.purdue_legacy_ai;

import com.nullprogram.chess.*;

import static com.nullprogram.chess.Piece.Side.BLACK;
import static com.nullprogram.chess.Piece.Side.WHITE;

public class AlphaBetaPruningAI implements Player {
    private Game game;

    public AlphaBetaPruningAI(Game game) {
        this.game = game;
    }

    @Override
    public Move takeTurn(Board board, Piece.Side side) {
        long timeStart = System.currentTimeMillis();
        Move m = predictBestMove(0, 5, board, side, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).getMove();
        System.out.println(System.currentTimeMillis() - timeStart);
        if (m == null)
            throw new NullPointerException("null move returned");
        else
            return m;
    }

    public MoveScore predictBestMove(int ply, int finalPly, Board board, Piece.Side side, double beta, double alpha) {
        if (ply == finalPly) {
            return new MoveScore(Evaluation.evaluateBoard(board, side), null).getReversedMoveScore();
        } else {
            MoveScore bestMove = new MoveScore(Double.NEGATIVE_INFINITY, null);
            for (Move move : board.allMoves(side, false)) {
                board.move(move);
                Piece.Side opponent = (side == BLACK ? WHITE : BLACK);
                MoveScore newMoveScore = predictBestMove(ply + 1, finalPly, board, opponent, -alpha, -beta);
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
