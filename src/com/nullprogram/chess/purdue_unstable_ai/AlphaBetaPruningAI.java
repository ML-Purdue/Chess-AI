package com.nullprogram.chess.purdue_unstable_ai;

import com.nullprogram.chess.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import static com.nullprogram.chess.Piece.Side.BLACK;
import static com.nullprogram.chess.Piece.Side.WHITE;

public class AlphaBetaPruningAI implements Player {
    private Hashtable<Transposition, Transposition> transpositionTable;
    List<Move> moves;
    List<MoveScore> moveScores;

    private static final int THRESHOLD = (int) Math.pow(35, 3.5);
    private static final int PLY_THRESHOLD = 4;
    private static final int NUM_THREADS = 8;
    private static final int NUM_BUCKETS = Integer.MAX_VALUE / 100;

    public AlphaBetaPruningAI() {
        transpositionTable = new Hashtable<>(Integer.MAX_VALUE / 100);
    }

    private synchronized Move getNextMoveToConsider() {
        if (moves.isEmpty()) {
            return null;
        } else {
            return moves.remove(0);
        }
    }

    private synchronized void addNewMovescore(MoveScore score) {
        moveScores.add(score);
    }

    @Override
    public Move takeTurn(Board board, Piece.Side side) {
        long timeStart = System.currentTimeMillis();
        transpositionTable = new Hashtable<>(NUM_BUCKETS);

        // Create threads
        Thread[] threads = new Thread[NUM_THREADS - 1];
        moveScores = new LinkedList<>();
        moves = board.allMoves(side, true).toList();
        int numMoves = moves.size();
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {

                // Loop through each move to consider
                Move move = getNextMoveToConsider();
                while (move != null) {
                    Board boardCopy = board.copy();
                    boardCopy.move(move);
                    MoveScore moveScore = transpositionTable(1, PLY_THRESHOLD, numMoves, boardCopy,
                            side == WHITE ? BLACK : WHITE,
                            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
                    moveScore = new MoveScore(moveScore.getScore(), move);
                    addNewMovescore(moveScore);
                    move = getNextMoveToConsider();
                    System.out.println("Evaluated " + (numMoves - moves.size()) + " of " + numMoves + " moves");
                }
            });
            threads[i].start();
        }

        // Join threads
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Determine best move
        MoveScore bestMoveScore = new MoveScore(Double.NEGATIVE_INFINITY, null);
        System.out.println("Num MoveScores: " + moveScores.size());
        for (MoveScore moveScore : moveScores) {
            if (moveScore.getScore() > bestMoveScore.getScore()) {
                bestMoveScore = moveScore;
            }
        }

        // Return our move
        System.out.println("Time: " + (System.currentTimeMillis() - timeStart));
        System.out.println("Score: " + bestMoveScore.getScore() * -1);
        System.out.println("From Transposition Table: " + bestMoveScore.isFromTranspositionTable());
        return bestMoveScore.getMove();
    }

    public MoveScore transpositionTable(int ply, int finalPly, int numMoves, Board board, Piece.Side side, double beta,
                                        double alpha) {
        Transposition lookup = new Transposition(board, numMoves, new MoveScore(0, null));
        if (transpositionTable.containsKey(lookup)) {
            MoveScore moveScore = transpositionTable.get(lookup).getMoveScore();
            moveScore.setFromTranspositionTable(true);
            if (side == Piece.Side.BLACK) {
                moveScore = moveScore.getReversedMoveScore();
            }
            return moveScore.getReversedMoveScore();
        }
        return alphaBetaPruning(ply, finalPly, numMoves, board, side, alpha, beta);
    }

    private MoveScore alphaBetaPruning(int ply, int finalPly, int numMoves, Board board, Piece.Side side, double alpha,
                                       double beta) {
        // Evaluate if we are deep enough
        if (ply >= finalPly && numMoves >= THRESHOLD) {
            MoveScore r = new MoveScore(Evaluation.evaluateBoard(board, side), null);
            addTranspositionToTable(side, board, numMoves, r);
            return r.getReversedMoveScore();
        } else {
            // Loop through every possible move
            MoveScore bestMove = new MoveScore(Double.NEGATIVE_INFINITY, null);
            MoveList moves = board.allMoves(side, true);
            int size = moves.size() * numMoves;
            for (Move move : moves) {
                // Make the move
                board.move(move);
                Piece.Side opponent = (side == BLACK ? WHITE : BLACK);

                // Evaluate that move
                MoveScore newMoveScore = transpositionTable(ply + 1, finalPly, size, board, opponent, -alpha, -beta);
                addTranspositionToTable(side, board, numMoves, newMoveScore);
                board.undo();

                // Alpha-beta pruning
                alpha = Math.max(alpha, newMoveScore.getScore());
                if (beta <= alpha) {
                    return new MoveScore(-beta, null);
                }

                // Find best move
                if (newMoveScore.getScore() > bestMove.getScore()) {
                    bestMove = new MoveScore(newMoveScore.getScore(), move);
                }
            }

            // Return best move
            if (bestMove.getMove() == null) { // stalemate
                return new MoveScore(0, null);
            } else {
                return bestMove.getReversedMoveScore();
            }
        }
    }

    private void addTranspositionToTable(Piece.Side side, Board board, int numMoves, MoveScore moveScore) {

        if (side == Piece.Side.WHITE) {
            Transposition transposition = new Transposition(board.copy(), numMoves, moveScore);
            transpositionTable.put(transposition, transposition);
        } else {
            Transposition transposition = new Transposition(board.copy(), numMoves, moveScore.getReversedMoveScore());
            transpositionTable.put(transposition, transposition);
        }
    }
}
