package com.nullprogram.chess.networking;

import com.nullprogram.chess.Game;
import com.nullprogram.chess.Player;
import com.nullprogram.chess.boards.BoardFactory;
import com.nullprogram.chess.boards.StandardBoard;
import com.nullprogram.chess.purdue_unstable_ai.Evaluation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    private ServerSocket serverSocket;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);


            Socket socket = serverSocket.accept();

            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));


            String line;
            if ((line = in.readLine()) != null) {
                String side = line.split(",")[1];

                Player white = null;
                Player black = null;
                float pieceCoef = 3;
                if (side.equals("white")) {
                    white = new com.nullprogram.chess.purdue_unstable_ai.AlphaBetaPruningAI(new Evaluation(
                            0.3, // pawn connectivity
                            2, // stalemate losing advantage
                            1,  // stalemate winning penalty
                            0.1, // maneuverability
                            0.25, // Center control
                            0.5, // Center control power
                            0.05, // Isolated pawns + promotion
                            3 * pieceCoef, // bishop
                            1000 * pieceCoef, // king
                            3 * pieceCoef, // knight
                            1 * pieceCoef, // pawn
                            9 * pieceCoef, // queen
                            5 * pieceCoef,  // rook
                            0.25, // connected rook
                            0.3, // pawn shield
                            1 // pawn promotion
                    ));

                    black = new com.nullprogram.chess.purdue_legacy_ai.AlphaBetaPruningAI(null);
                } else if (side.equals("black")) {
                    black = new com.nullprogram.chess.purdue_unstable_ai.AlphaBetaPruningAI(new Evaluation(
                            0.3, // pawn connectivity
                            2, // stalemate losing advantage
                            1,  // stalemate winning penalty
                            0.1, // maneuverability
                            0.25, // Center control
                            0.5, // Center control power
                            0.05, // Isolated pawns + promotion
                            3 * pieceCoef, // bishop
                            1000 * pieceCoef, // king
                            3 * pieceCoef, // knight
                            1 * pieceCoef, // pawn
                            9 * pieceCoef, // queen
                            5 * pieceCoef,  // rook
                            0.25, // connected rook
                            0.3, // pawn shield
                            1 // pawn promotion
                    ));
                    white = new com.nullprogram.chess.purdue_legacy_ai.AlphaBetaPruningAI(null);
                }

                Game newgame = new Game(BoardFactory.create(StandardBoard.class));
                newgame.seat(white, black);
                newgame.begin(out);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

//        Player white = new AlphaBetaPruningAI();
//        Player black = new AlphaBetaPruningAI();
//        Game newgame = new Game(BoardFactory.create(StandardBoard.class));
//        newgame.seat(white, black);
//        Thread currThread = newgame.begin();


    }
}
