package com.nullprogram.chess.you;

import com.nullprogram.chess.*;

public class AlphaBetaPruningAI implements Player {
    private Game game;

    public AlphaBetaPruningAI(Game game) {
        this.game = game;
    }

    @Override
    public Move takeTurn(Board board, Piece.Side side) {
        return predictBestMove(0, 5, board, side).getMove();
    }

    public MoveScore predictBestMove(int ply, int finalPly, Board board, Piece.Side side) {
        // Read the README to get a hint on what to do.

        return null;
    }
}
