package com.nullprogram.chess.purdue_unstable_ai;

/**
 * Created by matthewpage on 11/12/16.
 */
import com.nullprogram.chess.*;
import com.nullprogram.chess.pieces.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Transposition {
    Board board;
    int plysIntoFutueEvaluated;
    MoveScore moveScore;
    List<PiecePosition> pieces;

    public Transposition(Board board, int plysIntoFutueEvaluated, MoveScore moveScore) {
        this.board = board;
        this.plysIntoFutueEvaluated = plysIntoFutueEvaluated;
        this.moveScore = moveScore;
        this.pieces = new LinkedList<>();

        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                Position pos = new Position(i, j);
                Piece p0 = board.getPiece(pos);
                if (p0 != null) {
                    PiecePosition piecePos = new PiecePosition(p0, pos);
                    pieces.add(piecePos);
                }
            }
        }
    }

    public MoveScore getMoveScore() {
        return moveScore;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                Piece p = board.getPiece(new Position(i, j));
                if (p instanceof Bishop)
                    hash += 1;
                else if (p instanceof Rook)
                    hash += 2;
                else if (p instanceof Knight)
                    hash += 3;
                else if (p instanceof Pawn)
                    hash += 4;
                else if (p instanceof King)
                    hash += 5;
                else if (p instanceof Queen)
                    hash += 6;
                hash *= 7;
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transposition transposition = (Transposition) o;

        if (!pieces.equals(transposition.pieces)) {
            return false;
        }

        if (this.plysIntoFutueEvaluated > transposition.plysIntoFutueEvaluated) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Object clone() {
        return new Transposition(board.copy(), plysIntoFutueEvaluated, moveScore);
    }


    class PiecePosition {
        private Position position;
        private Class pieceType;
        private Piece.Side pieceSide;

        public PiecePosition(Piece piece,Position position) {
            this.position = position;
            pieceType = piece.getClass();
            pieceSide = piece.getSide();
        }

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public Class getPieceType() {
            return pieceType;
        }

        public void setPieceType(Class pieceType) {
            this.pieceType = pieceType;
        }

        public Piece.Side getPieceSide() {
            return pieceSide;
        }

        public void setPieceSide(Piece.Side pieceSide) {
            this.pieceSide = pieceSide;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PiecePosition that = (PiecePosition) o;

            if (!position.equals(that.position)) return false;
            if (!pieceType.equals(that.pieceType)) return false;
            return pieceSide == that.pieceSide;

        }

        @Override
        public int hashCode() {
            int result = position.hashCode();
            result = 31 * result + pieceType.hashCode();
            result = 31 * result + pieceSide.hashCode();
            return result;
        }
    }
}
