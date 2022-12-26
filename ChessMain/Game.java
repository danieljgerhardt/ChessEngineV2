package ChessMain;

import java.util.ArrayList;

public class Game {

     private static ArrayList<Move> activeMoveList = new ArrayList<>();

     private ArrayList<Board> boardList = new ArrayList<>();

     private int repetitionCount = 0;

     private boolean whiteToMove;

     private final int WHITE_EN_PASSANT_INT = 3;

     private final int BLACK_EN_PASSANT_INT = 4;

     private int enPassantInt;

     private Board gameBoard;

     private Engine engine;

     public Game(ArrayList<Move> ml, Board b) {
          this.activeMoveList = ml;
          this.gameBoard = b;
          this.whiteToMove = true;
          this.engine = new Engine(this.gameBoard);
          //this.boardList.add(this.gameBoard);
     }

     public Game(Board b) {
          this.gameBoard = b;
          this.whiteToMove = false;
          //May need to change above if engine ever plays white
          this.engine = new Engine(this.gameBoard);
          //this.boardList.add(this.gameBoard);
     }

     public Game(Board b, boolean w) {
          this.gameBoard = b;
          this.whiteToMove = w;
          //May need to change above if engine ever plays white
          this.engine = new Engine(this.gameBoard);
          //this.boardList.add(this.gameBoard);
     }

     public Game(ArrayList<Move> ml, Board b, boolean w) {
          this.activeMoveList = ml;
          this.gameBoard = b;
          this.whiteToMove = w;
          this.engine = new Engine(this.gameBoard);
          //this.boardList.add(this.gameBoard);
     }

     public static String getOppositeColor(String currColor) {
          if (currColor.equals("w")) {
               return "b";
          } else {
               return "w";
          }
     }

     //piece one and piece two are two most recent pieces in the DisplayGUI's ArrayList
     public boolean executePlayerMove(Piece pieceOne, Piece pieceTwo) {
          String currentColor = "";
          if (whiteToMove) {
               this.enPassantInt = WHITE_EN_PASSANT_INT;
               currentColor = "w";
          } else {
               this.enPassantInt = BLACK_EN_PASSANT_INT;
               currentColor = "b";
          }
          if (pieceOne.getColor().equals(currentColor)) {
               Move previousMove = null;
               Move move;
               boolean castling = false;
               boolean enPassant = false;
               if (this.activeMoveList.size() > 1) {
                    previousMove = this.activeMoveList.get(this.activeMoveList.size() - 1);
               }
               if ((whiteToMove && pieceOne.getColor().equals("w")) || (!whiteToMove
                       && pieceOne.getColor().equals("b"))) {
                    if (previousMove != null) {
                         //Next line checks if en passant is legal
                         //Final number on next line is 3 for white and 4 for black
                         if (previousMove.getStartingPiece().isPawn() && previousMove.getEndingTile().getRow()
                                 == this.enPassantInt) {
                              //moved pawn 2 squares on previous move -- en passant is available
                              enPassant = true;
                         }
                    }
                    if (pieceOne.isKing() && Math.abs((pieceOne.getColumn() - pieceTwo.getColumn())) == 2) {
                         //castling
                         castling = true;
                    }
                    if (previousMove == null) {
                         move = new Move(pieceOne, pieceTwo, this.gameBoard, castling);
                    } else {
                         move = new Move(pieceOne, pieceTwo, this.gameBoard, enPassant, previousMove.getEndingTile(),
                                 castling);
                    }
                    if (move.makeMove()) {
                         this.activeMoveList.add(move);
                         this.whiteToMove = !whiteToMove;
                         int eqLimit = 0;
                         //Maybe should add if white to move
                         //Check if the board has already been reached to check for draws
                         for (Board testRep : this.boardList) {
                              if (testRep.toString().equals(this.gameBoard.toString()) && eqLimit == 0) {
                                   eqLimit++;
                                   this.repetitionCount++;
                                   testRep.incrRepetitionCount();
                                   System.out.println("rep found");
                              }
                         }
                         this.boardList.add(this.gameBoard.getBoardCopy());
                         return true;
                    }
               }
          }
          return false;
     }

     public boolean executeComputerMove(String color) {
          Move move = null;
          if (color.equals("b")) {
               move = engine.generateMoveBlack(this.activeMoveList.get(this.activeMoveList.size() - 1));
          } else if (color.equals("w")) {
               if (activeMoveList.size() < 1) {
                    move = engine.generateFirstMoveWhite();
               } else {
                    move = engine.generateMoveWhite(this.activeMoveList.get(this.activeMoveList.size() - 1));
               }
          }
          if (move.makeMove()) {
               this.activeMoveList.add(move);
               System.out.println(move.toString());
               this.whiteToMove = !whiteToMove;
               for (Board testRep : this.boardList) {
                    if (testRep.equals(this.gameBoard)) {
                         this.repetitionCount++;
                    }
               }
               this.boardList.add(this.gameBoard);
               return true;
           }
           System.out.println(move.getStartingPiece().getRow() + ", " + move.getStartingPiece().getColumn());
           System.out.println("Engine attempted illegal move: " + move);
           return false;
     }

     public void getGameOverStatus() {
          //checks for and draw by stalemate
          if (activeMoveList.size() < 1) {
               this.engine.generateFirstMoveWhite();
          } else {
               this.engine.checkMoveWhite(this.activeMoveList.get(this.activeMoveList.size() - 1));
               //System.out.println("checking if black can move");
               this.engine.checkMoveBlack(this.activeMoveList.get(this.activeMoveList.size() - 1));
          }
          //checks for draw by repetition
          for (Board testRep : this.boardList) {
               if (testRep.getRepetitionCount() >= 3) {
                    throw new GameOverException("draw");
               }
          }
          int pieceCount = 0;
          int bishopCount = 0;
          int knightCount = 0;
          for(String[] sArray : this.gameBoard.getPieces()) {
               for(String s : sArray) {
                    if (!s.equals("ee")) {
                         pieceCount++;
                    }
                    if (s.equals("bN") || s.equals("wN")) {
                         knightCount++;
                    } else if (s.equals("bB") || s.equals("wB")) {
                         bishopCount++;
                    }
               }
          }
          //draw -- insufficient material
          //(king v king, king v king and bishop, king vs king and knight
          // king and knight/bishop vs king and knight/bishop)
          if (pieceCount == 2) {
               System.out.println("kings only");
               throw new GameOverException("draw");
          } else if (pieceCount == 3) {
               if (bishopCount > 0 || knightCount > 3) {
                    System.out.println("king and bishop/knight");
                    throw new GameOverException("draw");
               }
          } else if (pieceCount == 4) {
               if ((bishopCount > 0 && knightCount > 0) || bishopCount > 1 || knightCount > 1) {
                    System.out.println("king and bishop/knight vs king and bishop/knight");
                    throw new GameOverException("draw");
               }
          }
     }

     public void undoMove() {
          //not currently used :(
          Move toUndo = this.activeMoveList.get(this.activeMoveList.size() - 1);
          toUndo.undoMove();
     }

     public static ArrayList<Move> getActiveMoveList() {
          return activeMoveList;
     }

     public static Move getMostRecentMove() {
          if (activeMoveList.size() < 1) {
               return null;
          }
          return activeMoveList.get(activeMoveList.size() - 1);
     }

     public boolean getWhiteToMove() {
          return this.whiteToMove;
     }

     public Board getBoard() {
          return this.gameBoard;
     }

     public boolean getWhiteCanCastleKingside() {
          //white has moved king
          //white has moved rook
          return !this.gameBoard.getKing("w").getHasMoved()
                  && !this.gameBoard.getSpecificHasMoved("w", "R", 7, 7);
     }

     public boolean getWhiteCanCastleQueenside() {
          //white has moved king
          //white has moved rook
          return !this.gameBoard.getKing("w").getHasMoved()
                  && !this.gameBoard.getSpecificHasMoved("w", "R", 7, 0);
     }

     public boolean getBlackCanCastleKingside() {
          return !this.gameBoard.getKing("b").getHasMoved()
                  && !this.gameBoard.getSpecificHasMoved("b", "R", 0, 7);
     }

     public boolean getBlackCanCastleQueenside() {
          return !this.gameBoard.getKing("b").getHasMoved()
                  && !this.gameBoard.getSpecificHasMoved("b", "R", 0, 0);
     }

}
