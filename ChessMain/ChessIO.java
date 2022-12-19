package ChessMain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ChessIO {

    public static Game readFile(String filePath) {
        Game ret;
        Board retBoard;
        boolean retWhiteToMove = true;
        boolean retWhiteCastleKingside = true;
        boolean retWhiteCastleQueenside = true;
        boolean retBlackCastleKingside = true;
        boolean retBlackCastleQueenside = true;
        String readingEndedGame = "";
        String[][] pieces = new String[8][8];
        try {
            Scanner s = new Scanner(new File(filePath));
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (s.hasNext()) pieces[i][j] = s.next();
                }
            }
            retBoard = new Board(pieces);
            if (s.hasNext()) {
                retWhiteToMove = Boolean.parseBoolean(s.next());
            }
            if (s.hasNext()) {
                retWhiteCastleKingside = Boolean.parseBoolean(s.next());
            }
            if (s.hasNext()) {
                retWhiteCastleQueenside = Boolean.parseBoolean(s.next());
            }
            if (s.hasNext()) {
                retBlackCastleKingside = Boolean.parseBoolean(s.next());
            }
            if (s.hasNext()) {
                retBlackCastleQueenside = Boolean.parseBoolean(s.next());
            }
            if (s.hasNext()) {
                readingEndedGame = s.next();
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found :(");
            throw new RuntimeException(e);
        }
        if (!retWhiteCastleKingside) {
            Move.writtenCantCastleWhiteKingside();
            System.out.println("FILE SAYS WHITE CANT CASTLE KINGSIDE");
        }
        if (!retWhiteCastleQueenside) {
            Move.writtenCantCastleWhiteQueenside();
            System.out.println("FILE SAYS WHITE CANT CASTLE QUEENSIDE");
        }
        if (!retBlackCastleKingside) {
            Move.writtenCantCastleBlackKingside();
            System.out.println("FILE SAYS BLACK CANT CASTLE KINGSIDE");
        }
        if (!retBlackCastleQueenside) {
            Move.writtenCantCastleBlackQueenside();
            System.out.println("FILE SAYS BLACK CANT CASTLE QUEENSIDE");
        }
        if (readingEndedGame.equals("ENDED")) {
            throw new IllegalArgumentException("THAT GAME HAS ENDED");
        }
        ret = new Game(retBoard, retWhiteToMove);
        return ret;
    }

    public static void printFile(Board board, boolean whiteToMove,
                                 boolean canCastleWhiteKingside, boolean canCastleWhiteQueenside,
                                 boolean canCastleBlackKingside, boolean canCastleBlackQueenside,
                                 boolean gameEnded, String filePath) {
        FileWriter writer;
        String[][] pieces = board.getPieces();
        try {
            writer = new FileWriter(filePath);
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    writer.write(pieces[i][j] + " ");
                }
                writer.write("\n");
            }
            writer.write(String.valueOf(whiteToMove));
            writer.write(" ");
            writer.write(String.valueOf(canCastleWhiteKingside));
            writer.write(" ");
            writer.write(String.valueOf(canCastleWhiteQueenside));
            writer.write(" ");
            writer.write(String.valueOf(canCastleBlackKingside));
            writer.write(" ");
            writer.write(String.valueOf(canCastleBlackQueenside));
            if (gameEnded) {
                writer.write(" ");
                writer.write("ENDED");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
