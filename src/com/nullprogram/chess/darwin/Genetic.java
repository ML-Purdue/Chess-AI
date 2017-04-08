package com.nullprogram.chess.darwin;

import com.nullprogram.chess.*;
import com.nullprogram.chess.boards.BoardFactory;
import com.nullprogram.chess.boards.StandardBoard;
import com.nullprogram.chess.pieces.*;
import com.nullprogram.chess.purdue_unstable_ai.AlphaBetaPruningAI;
import com.nullprogram.chess.purdue_unstable_ai.Evaluation;
import java.util.Arrays;

public class Genetic {

    private static final int SIZE = 20;
    private static final int SURVIVORS = 3;

    public static void main(String[] args) throws InterruptedException {
        Evaluation[] population = new Evaluation[SIZE];
        for (int i = 0; i < SIZE; i++) {
            population[i] = random_eval();
        }

        int generation = 0;

        while (true) {
            generation++;
            double[] scoreBoard = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (i != j) {
                        Player white = new AlphaBetaPruningAI(population[i]);
                        Player black = new AlphaBetaPruningAI(population[j]);
                        Game newgame = new Game(BoardFactory.create(StandardBoard.class));
                        newgame.seat(white, black);
                        Thread currThread = newgame.begin();

                        currThread.join(15*60*1000);
                        if (newgame.getWinner() == null) {
                            Board board = newgame.getBoard();
                            double rpoint = 0;
                            for (int k = 0; k < 8; k++) {
                                for (int w = 0; w < 8; w++) {
                                    Piece currPiece = board.getPiece(new Position(k, w));
                                    if (currPiece.getSide() == Piece.Side.WHITE) {
                                        rpoint += getScore(currPiece);
                                    } else {
                                        rpoint -= getScore(currPiece);
                                    }
                                }
                            }
                            if (rpoint > 0.001) {
                                scoreBoard[i] += 0.75;
                                scoreBoard[j] += 0.25;
                            } else if (rpoint < -0.001) {
                                scoreBoard[j] += 0.75;
                                scoreBoard[i] += 0.25;
                            } else {
                                scoreBoard[i] += 0.5;
                                scoreBoard[j] += 0.5;
                            }
                        }
                        else {
                            if (newgame.getWinner() == Piece.Side.WHITE) {
                                scoreBoard[i]++;
                            } else {
                                scoreBoard[j]++;
                            }
                        }
                    }
                }
            }
            my_sort(population, scoreBoard);
            Evaluation[] survivors = new Evaluation[SURVIVORS];
            for (int i = 0; i < SURVIVORS; i++) {
                survivors[i] = population[i];
            }
            double[][] config = new double[13][2];
            for (int i = 0; i < 13; i++) {
                config[i][0] = survivors[0].getParam()[i];
                for (int j = 1; j < SURVIVORS; j++) {
                    if (survivors[j].getParam()[i] > config[i][0]) {
                        config[i][0] = survivors[j].getParam()[i];
                    }
                }

                config[i][1] = survivors[0].getParam()[i];
                for (int j = 1; j < SURVIVORS; j++) {
                    if (survivors[j].getParam()[i] < config[i][1]) {
                        config[i][1] = survivors[j].getParam()[i];
                    }
                }
            }

            System.out.println("Generation: "+generation);
            System.out.println("1st place: score: " + scoreBoard[0] + "\nconfig" +
                    Arrays.toString(survivors[0].getParam()) +
                    "\n\n2nd place: score: " + scoreBoard[1] + "\nconfig" +
                    Arrays.toString(survivors[1].getParam()) +
                    "\n\n3rd place: score: " + scoreBoard[2] + "\nconfig" +
                    Arrays.toString(survivors[2].getParam()) + "\n\n\n\n");

            population[SURVIVORS] = random_eval();
            for (int i = SURVIVORS + 1; i < SIZE; i++) {
                population[i] = new Evaluation(
                        my_random(config[0][1], config[0][0]),
                        my_random(config[1][1], config[1][0]),
                        my_random(config[2][1], config[2][0]),
                        my_random(config[3][1], config[3][0]),
                        my_random(config[4][1], config[4][0]),
                        my_random(config[5][1], config[5][0]),
                        my_random(config[6][1], config[6][0]),
                        my_random(config[7][1], config[7][0]),
                        my_random(config[8][1], config[8][0]),
                        my_random(config[9][1], config[9][0]),
                        my_random(config[10][1], config[10][0]),
                        my_random(config[11][1], config[11][0]),
                        my_random(config[12][1], config[12][0]),  5, 0.5, 1

                );

            }
        }
    }

    private static void my_sort(Evaluation[] population, double[] scoreBoard) {

        for (int i = 0; i < SIZE; i++) {
            boolean changed = false;
            for (int j = 0; j < SIZE - i - 1; j++) {
                if (scoreBoard[j] < scoreBoard[j + 1]) {
                    Evaluation temp = population[j];
                    population[j] = population[j + 1];
                    population[j + 1] = temp;

                    double temp_score = scoreBoard[j];
                    scoreBoard[j] = scoreBoard[j + 1];
                    scoreBoard[j + 1] = temp_score;
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }
    }

    private static double getScore(Piece currPiece) {
        if (currPiece instanceof Pawn) return 1;
        if (currPiece instanceof Rook) return 5;
        if (currPiece instanceof Knight) return 3;
        if (currPiece instanceof Bishop) return 3;
        if (currPiece instanceof King) return 1000000;
        if (currPiece instanceof Queen) return 9;
        return 0;
    }

    private static double my_random(double min, double max) {
        return (max-min)*Math.random() + min;
    }

    private static Evaluation random_eval() {
        return new Evaluation(Math.random(),
                Math.random()*10,
                Math.random()*10,
                Math.random(),
                Math.random()*2,
                2 - 4*Math.random(),
                Math.random(),
                Math.random()*20,
                Math.random()*1000000,
                Math.random()*20,
                1,
                Math.random()*50,
                Math.random()*30, 5, 0.5, 1);
    }
}
