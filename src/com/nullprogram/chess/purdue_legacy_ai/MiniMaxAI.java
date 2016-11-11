package com.nullprogram.chess.purdue_legacy_ai;

import com.nullprogram.chess.*;

import static com.nullprogram.chess.Piece.Side.BLACK;
import static com.nullprogram.chess.Piece.Side.WHITE;

public class MiniMaxAI implements Player {
    private Game game;

    public MiniMaxAI(Game game) {
        this.game = game;
    }

    @Override
    public Move takeTurn(Board board, Piece.Side side) {
        return predictBestMove(0, 3, board, side).getMove();
    }

    public MoveScore predictBestMove(int ply, int finalPly, Board board, Piece.Side side) {
        if (ply == finalPly) {
            return new MoveScore(Evaluation.evaluateBoard(board, side), null).getReversedMoveScore();
        } else {
            MoveScore bestMove = new MoveScore(Double.NEGATIVE_INFINITY, null);
            for (Move move : board.allMoves(side, true)) {
                board.move(move);
                Piece.Side opponent = (side == BLACK ? WHITE : BLACK);
                MoveScore newMoveScore = predictBestMove(ply + 1, finalPly, board, opponent);
                if (newMoveScore.getScore() > bestMove.getScore()) {
                    bestMove = new MoveScore(newMoveScore.getScore(), move);
                }
                board.undo();
            }
            if (bestMove.getMove() == null) // stalemate
                return new MoveScore(0, null);
            else
                return bestMove.getReversedMoveScore();
        }
    }
}
